from app.domain.models.explanation import MultiExplanations
from app.domain.models.profile import Profile
from app.domain.models.suggestion import MultiSuggestions
from app.domain.ports.service import SuggestionsService
from app.domain.ports.suggestion import ExplainableSuggestionsEngine


class MultiSuggestionsService(SuggestionsService):
    def __init__(self, **services: ExplainableSuggestionsEngine) -> None:
        super().__init__()
        print("Found services: ", [name for name in services])
        self.services = {name: engine for name, engine in services.items()}

    def suggest(self, profile: Profile) -> MultiSuggestions:
        scores = {name: engine.suggest(profile) for name, engine in self.services.items()}
        return MultiSuggestions(scores=scores)

    def explain(self, profile: Profile, keys: list[str]) -> MultiExplanations:
        explanations = {
            name: engine.explain(profile, keys) for name, engine in self.services.items()
        }
        return MultiExplanations(explanations=explanations)
