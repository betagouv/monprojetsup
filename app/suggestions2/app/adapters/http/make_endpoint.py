from fastapi import FastAPI

from app.adapters.http.health_check import HealthCheck
from app.adapters.http.request import ExplanationRequest, SuggestionRequest


from prometheus_client import Histogram

from app.adapters.http.response import (
    ExplanationResponse,
    ExplanationResponseContent,
    ExplanationResponseDetails,
    ExplanationResponseScore,
    Score,
    SuggestionResponse,
)
from app.domain.models.config import ProfileConfig
from app.domain.models.explanation import Explanations, MultiExplanations
from app.domain.models.suggestion import MultiSuggestions
from app.domain.ports.service import SuggestionsService


def make_endpoint(
    service: SuggestionsService, app: FastAPI, config: ProfileConfig
) -> None:
    SUGG_TIMER = Histogram(
        "http_suggestion_request_processing_seconds",
        "Time used to answer suggestion requests",
        unit="seconds",
        buckets=[0.0001, 0.001, 0.005, 0.01, 0.025, 0.05, 0.075, 0.1, 0.25, 0.5, 1.0],
    )

    @app.post("/suggestions", response_model_exclude_none=True)
    async def get_suggestions(request: SuggestionRequest) -> SuggestionResponse:
        print(f"request: {request}")
        with SUGG_TIMER.time():
            suggestions: MultiSuggestions = service.suggest(
                request.profile.to_profile(config)
            )
            scores = get_scores(suggestions, request)
            print(f"Scores: {scores}")
            return SuggestionResponse(scores=scores)

    EXPL_TIMER = Histogram(
        "http_explanation_request_processing_seconds",
        "Time used to answer explanation requests",
        unit="seconds",
        buckets=[0.0001, 0.001, 0.005, 0.01, 0.025, 0.05, 0.075, 0.1, 0.25, 0.5, 1.0],
    )

    @app.post("/explanations", response_model_exclude_none=True)
    async def get_explanations(request: ExplanationRequest) -> ExplanationResponse:
        with EXPL_TIMER.time():

            suggestions: MultiSuggestions = service.suggest(
                request.profile.to_profile(config)
            )
            scores = get_scores(suggestions, request)

            explanations: MultiExplanations = service.explain(
                request.profile.to_profile(config), request.keys
            )

            service_expls: dict[str, Explanations] = explanations.explanations
            expl = [
                ExplanationResponseContent(
                    key=item,
                    explanations={
                        service: ExplanationResponseDetails(
                            popularity=service_expls[service].expls[item].popularity,
                            scores=[
                                ExplanationResponseScore(
                                    key=it.value, log_influence=s, categorie=it.category
                                )
                                for (it, s) in service_expls[service]
                                .expls[item]
                                .relative_frequency.items()
                            ],
                        )
                        for service in service_expls
                        if item in service_expls[service].expls
                    },
                )
                for item in request.keys
            ]
            return ExplanationResponse(
                explanations=expl,
                scores=scores,
            )

    @app.get("/health")
    def health_check() -> HealthCheck:
        return HealthCheck(status="OK")

    def get_scores(
        suggestions: MultiSuggestions, request: SuggestionRequest
    ) -> list[Score]:
        service_scores = suggestions.scores
        scores = [
            Score(
                key=item,
                scores={
                    service: (
                        service_scores[service].scores[item]
                        if item in service_scores[service].scores
                        else 0.0
                    )
                    for service in service_scores
                },
            )
            for item in request.keys
        ]
        return scores
