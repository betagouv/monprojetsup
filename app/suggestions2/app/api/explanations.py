from prometheus_client import Histogram

from app.api.types import (
    Explanation,
    ExplanationRequestBody,
    ExplanationRequestResponse,
    NaivesBayesExplanation,
)
from app.data_types import Basket
from app.naive_bayes.matrix import NaiveBayesMatrix


EXPL_TIMER = Histogram(
    "http_explanation_request_processing_seconds",
    "Time used to answer explanation requests",
    unit="seconds",
    buckets=[0.0001, 0.001, 0.005, 0.01, 0.025, 0.05, 0.075, 0.1, 0.25, 0.5, 1.0],
)


def compute_explanations(
    request_body: ExplanationRequestBody,
    profile_matrix: NaiveBayesMatrix,
) -> ExplanationRequestResponse:
    with EXPL_TIMER.time():
        profile_explanations, popularity = profile_matrix.explain(
            Basket.from_request_profile(request_body.profile),
            request_body.keys,
        )

        explanations = [
            Explanation(
                key=f,
                popularity=popularity[f],
                profile=[
                    NaivesBayesExplanation(item_key=item, score=score, side=side)
                    for (
                        (item, side),
                        score,
                    ) in scores.items()
                ],
            )
            for (f, scores) in profile_explanations.items()
        ]
        response = ExplanationRequestResponse(liste=explanations)

        return response
