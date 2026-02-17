from fastapi import FastAPI
from prometheus_client import make_asgi_app
from pathlib import Path
from typing import Iterator

from app.adapters.http.make_endpoint import make_endpoint
from app.adapters.naive_bayes import NaiveBayesMatrix
from app.adapters.pg_database import PostgresDatabase
from app.application.service import MultiSuggestionsService
from app.config import CONFIG, VERSION, LOGGER, DEFAULT_MODELS_DIR
from app.db_schema import DbTables
from app.domain.models.profile import Profile

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
    print(f"Creating '{name}' service...")
    service = NaiveBayesMatrix(regularization_laplace=regularization_laplace)
    service.init_from_profiles_batched(profiles_iterator)
    print(f"Service '{name}' created with matrix shape {service.matrix.shape}")
    return service


def create_naive_bayes_service_from_precomputed_model(
    name: str,
    models_dir: Path | str = DEFAULT_MODELS_DIR,
) -> NaiveBayesMatrix:
    """
    Create a NaiveBayes service by loading a precomputed model.
    
    Much faster than computing the model from scratch, which is useful
    for services that take a long time to compute.
    
    Args:
        name: Name of the model (it is used to find the files)
        models_dir: Directory where the model files are stored
        
    Returns:
        A NaiveBayesMatrix instance loaded from disk
    """
    print(f"Loading '{name}' service from stored model...")
    return NaiveBayesMatrix.load_from_file(
        directory=models_dir,
        name=name,
    )


def create_services() -> MultiSuggestionsService:
    """
    Create the different services for suggestions2.
    """
    print("Creating DB connection...")
    data_repo = PostgresDatabase.from_env()

    profil_expert_service = create_naive_bayes_service(
        name="profil_expert",
        profiles_iterator=data_repo.iter_profiles_batched(
            table_name=DbTables.REF_EXPERT,
            config=CONFIG,
        ),
    )

    profil_lyceen_service = create_naive_bayes_service(
        name="profil_lyceen",
        profiles_iterator=data_repo.iter_profiles_batched(
            table_name=DbTables.REF_LYCEEN,
            config=CONFIG,
        ),
    )

    voeux_parcoursup = None
    try:
        voeux_parcoursup = create_naive_bayes_service_from_precomputed_model(
            name="voeux_parcoursup",
        )
    except FileNotFoundError as e:
        LOGGER.warning(
            f"Precomputed model not found: {e.filename}. "
            "Run 'python -m app.tools.precompute_models' to generate them."
        )
    except Exception as e:
        LOGGER.error(f"Could not load precomputed model 'voeux_parcoursup': {e}")

# TODO: add more services here
    LOGGER.info("Creating aggregate service...")
    
    services = {
        "expert": profil_expert_service,
        "lyceen": profil_lyceen_service,
    }
    if voeux_parcoursup is not None:
        services["parcoursup"] = voeux_parcoursup
    
    return MultiSuggestionsService(**services)


service = create_services()

LOGGER.info("Creating endpoint...")
make_endpoint(service, app, CONFIG)
