from fastapi import FastAPI
from prometheus_client import make_asgi_app

from app.adapters.http.make_endpoint import make_endpoint
from app.adapters.naive_bayes import NaiveBayesMatrix
from app.adapters.pg_database import PostgresDatabase
from app.application.service import MultiSuggestionsService
from app.config import CONFIG, VERSION, LOGGER


app = FastAPI(title="MonProjetSup Suggestions2 API", version=VERSION)

# Add prometheus asgi middleware to route /metrics requests
metrics_app = make_asgi_app()
app.mount("/metrics", metrics_app)


LOGGER.info("Creating DB connection...")
data_repo = PostgresDatabase.from_env()

LOGGER.info("Creating 'profil_eleve' service...")
data_profil_eleve = data_repo.load_profiles(table_name="profil_eleve", config=CONFIG)
profil_eleve_service = NaiveBayesMatrix(regularization_laplace=1.0)
profil_eleve_service.init_from_profiles(data_profil_eleve)
# TODO: add other services here.

LOGGER.info("Creating aggregate service...")
service = MultiSuggestionsService(
    profil_eleve=profil_eleve_service,
)

LOGGER.info("Creating endpoint...")
make_endpoint(service, app, CONFIG)
