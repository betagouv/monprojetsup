from prometheus_client import Histogram

from app.api.types import Affinite, SuggestionRequestBody, SuggestionRequestResponse
from app.data_types import Basket
from app.naive_bayes.matrix import NaiveBayesMatrix


SUGG_TIMER = Histogram(
    "http_suggestion_request_processing_seconds",
    "Time used to answer suggestion requests",
    unit="seconds",
    buckets=[0.0001, 0.001, 0.005, 0.01, 0.025, 0.05, 0.075, 0.1, 0.25, 0.5, 1.0],
)


def compute_suggestions(
    request_body: SuggestionRequestBody, profile_matrix: NaiveBayesMatrix
) -> SuggestionRequestResponse:
    with SUGG_TIMER.time():
        profile_scores = profile_matrix.predict(Basket.from_request_profile(request_body.profile))

        affinites = [Affinite(key=f, affinite=profile_scores[f]) for f in profile_scores]
        response = SuggestionRequestResponse(affinites=affinites)
        return response
