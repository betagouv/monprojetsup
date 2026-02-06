from fastapi import FastAPI
from prometheus_client import make_asgi_app
from os import getenv
from dotenv import load_dotenv
from typing import Iterator

from app.adapters.http.make_endpoint import make_endpoint
from app.adapters.naive_bayes import NaiveBayesMatrix
from app.adapters.pg_database import PostgresDatabase
from app.application.service import MultiSuggestionsService
from app.config import CONFIG, VERSION, LOGGER
from app.db_schema import DbTables
from app.domain.models.profile import Profile


load_dotenv()
DB_SUGGESTIONS2_REF_EXPERT: str = getenv(
    "DB_SUGGESTIONS2_REF_EXPERT", default=DbTables.REF_EXPERT
)
DB_SUGGESTIONS2_REF_LYCEEN: str = getenv(
    "DB_SUGGESTIONS2_REF_LYCEEN", default=DbTables.REF_LYCEEN
)
DB_SUGGESTIONS2_PANIERS_VOEUX: str = getenv(
    "DB_SUGGESTIONS2_PANIERS_VOEUX", default=DbTables.PANIERS_VOEUX
)
DB_SUGGESTIONS2_JOIN_FORMATION_VOEU: str = getenv(
    "DB_SUGGESTIONS2_JOIN_FORMATION_VOEU", default=DbTables.JOIN_FORMATION_VOEU
)

app = FastAPI(title="MonProjetSup Suggestions2 API", version=VERSION)

# Add prometheus asgi middleware to route /metrics requests
metrics_app = make_asgi_app()
app.mount("/metrics", metrics_app)


def create_naive_bayes_service(
    name: str,
    profiles_iterator: Iterator[list[Profile]],
    regularization_laplace: float = 1.0,
) -> NaiveBayesMatrix:
    """
    Create a NaiveBayes service from a batched profiles iterator.
    """
    LOGGER.info(f"Creating '{name}' service...")
    service = NaiveBayesMatrix(regularization_laplace=regularization_laplace)
    service.init_from_profiles_batched(profiles_iterator)
    LOGGER.info(f"Service '{name}' created with matrix shape {service.matrix.shape}")
    return service


def create_services() -> MultiSuggestionsService:
    """
    Create the different services for suggestions2.
    """
    LOGGER.info("Creating DB connection...")
    data_repo = PostgresDatabase.from_env()

    profil_expert_service = create_naive_bayes_service(
        name="profil_expert",
        profiles_iterator=data_repo.iter_profiles_batched(
            table_name=DB_SUGGESTIONS2_REF_EXPERT,
            config=CONFIG,
        ),
    )

    profil_lyceen_service = create_naive_bayes_service(
        name="profil_lyceen",
        profiles_iterator=data_repo.iter_profiles_batched(
            table_name=DB_SUGGESTIONS2_REF_LYCEEN,
            config=CONFIG,
        ),
    )

    # voeux_parcoursup_service = create_naive_bayes_service(
    #     name="voeux_parcoursup",
    #     profiles_iterator=data_repo.iter_paniers_voeux_as_profiles_batched(
    #         paniers_table=DB_SUGGESTIONS2_PANIERS_VOEUX,
    #         join_table=DB_SUGGESTIONS2_JOIN_FORMATION_VOEU,
    #     ),
    # )
# TODO: add more services here
    LOGGER.info("Creating aggregate service...")
    return MultiSuggestionsService(
        expert=profil_expert_service,
        lyceen=profil_lyceen_service,
        # parcoursup=voeux_parcoursup_service,
    )


service = create_services()

LOGGER.info("Creating endpoint...")
make_endpoint(service, app, CONFIG)
