from contextlib import asynccontextmanager
import logging
from fastapi import FastAPI
from prometheus_client import make_asgi_app

from app.api.explanations import compute_explanations
from app.api.suggestions import compute_suggestions
from app.api.types import (
    ExplanationRequestBody,
    ExplanationRequestResponse,
    SuggestionRequestBody,
    SuggestionRequestResponse,
)
from app.config import VERSION
from app.types import HealthCheck

from .naive_bayes.matrix import NaiveBayesMatrix, load_data_and_create_matrix

logger = logging.getLogger("uvicorn.error.app")
logger.setLevel("DEBUG")

student_profile_suggestions_matrix = NaiveBayesMatrix()


@asynccontextmanager
async def lifespan(app: FastAPI):
    """
    [FastAPI lifespan](https://fastapi.tiangolo.com/advanced/events/):
    can be used to execute startup (before yield) and cleanup code (after yield)
    for a FastAPI app.

    We put this code here instead of the top level of the module to avoid trying
    to connect to the DB when running tests, for example.
    """

    # Load the data and train the ML model
    global student_profile_suggestions_matrix
    student_profile_suggestions_matrix = load_data_and_create_matrix()
    yield


app = FastAPI(lifespan=lifespan, title="MonProjetSup API", version=VERSION)

# Add prometheus asgi middleware to route /metrics requests
metrics_app = make_asgi_app()
app.mount("/metrics", metrics_app)


@app.post("/suggestions", response_model_exclude_none=True)
async def get_suggestions(request_body: SuggestionRequestBody) -> SuggestionRequestResponse:
    return compute_suggestions(request_body, student_profile_suggestions_matrix)


@app.post("/explanations", response_model_exclude_none=True)
async def get_explanations(request_body: ExplanationRequestBody) -> ExplanationRequestResponse:
    return compute_explanations(request_body, student_profile_suggestions_matrix)


@app.get("/health")
def health_check() -> HealthCheck:
    return HealthCheck(status="OK")
