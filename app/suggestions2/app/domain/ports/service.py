from abc import ABC, abstractmethod

from app.domain.models.explanation import MultiExplanations
from app.domain.models.profile import Profile
from app.domain.models.suggestion import MultiSuggestions


class SuggestionsService(ABC):
    @abstractmethod
    def suggest(self, profile: Profile) -> MultiSuggestions:
        raise NotImplementedError("Method 'suggest' not implemented")

    @abstractmethod
    def explain(self, profile: Profile) -> MultiExplanations:
        raise NotImplementedError("Method 'explain' not implemented")
