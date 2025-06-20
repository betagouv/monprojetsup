from abc import ABC, abstractmethod


from app.domain.models.explanation import Explanations
from app.domain.models.profile import Profile
from app.domain.models.suggestion import Suggestions


class ExplainableSuggestionsEngine(ABC):
    @abstractmethod
    def init_from_profiles(self, profiles: list[Profile]):
        raise NotImplementedError("Method 'init_from_profiles' not implemented")

    @abstractmethod
    def suggest(self, profile: Profile) -> Suggestions:
        raise NotImplementedError("Method 'suggest' not implemented")

    @abstractmethod
    def explain(self, profile: Profile) -> Explanations:
        raise NotImplementedError("Method 'explain' not implemented")
