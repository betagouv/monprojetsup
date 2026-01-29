from fastapi import FastAPI
from prometheus_client import make_asgi_app
from os import getenv
from dotenv import load_dotenv

from app.adapters.http.make_endpoint import make_endpoint
from app.adapters.naive_bayes import NaiveBayesMatrix
from app.adapters.pg_database import PostgresDatabase
from app.application.service import MultiSuggestionsService
from app.config import CONFIG, VERSION, LOGGER
from app.db_schema import DbTables


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


LOGGER.info("Creating DB connection...")
data_repo = PostgresDatabase.from_env()

LOGGER.info(
    f"Creating 'profil_expert' service based on ref table ${DB_SUGGESTIONS2_REF_EXPERT}..."
)
data_profil_eleve = data_repo.load_profiles(
    table_name=DB_SUGGESTIONS2_REF_EXPERT, config=CONFIG
)
profil_expert_service = NaiveBayesMatrix(regularization_laplace=1.0)
profil_expert_service.init_from_profiles(data_profil_eleve)

LOGGER.info(
    f"Creating 'profil_eleve' service service based on ref table ${DB_SUGGESTIONS2_REF_LYCEEN}..."
)
data_profil_lyceen = data_repo.load_profiles(
    table_name=DB_SUGGESTIONS2_REF_LYCEEN, config=CONFIG
)
profil_lyceen_service = NaiveBayesMatrix(regularization_laplace=1.0)
profil_lyceen_service.init_from_profiles(data_profil_lyceen)

LOGGER.info(
    f"Creating 'voeux_parcoursup' service based on tables "
    f"${DB_SUGGESTIONS2_PANIERS_VOEUX} and ${DB_SUGGESTIONS2_JOIN_FORMATION_VOEU}..."
)
data_voeux_parcoursup = data_repo.load_paniers_voeux_as_profiles(
    paniers_table=DB_SUGGESTIONS2_PANIERS_VOEUX,
    join_table=DB_SUGGESTIONS2_JOIN_FORMATION_VOEU,
)
voeux_parcoursup_service = NaiveBayesMatrix(regularization_laplace=1.0)
voeux_parcoursup_service.init_from_profiles(data_voeux_parcoursup)

# TODO: add more services here

LOGGER.info("Creating aggregate service...")
service = MultiSuggestionsService(
    expert=profil_expert_service,
    lyceen=profil_lyceen_service,
    parcoursup=voeux_parcoursup_service,
)

LOGGER.info("Creating endpoint...")
make_endpoint(service, app, CONFIG)
