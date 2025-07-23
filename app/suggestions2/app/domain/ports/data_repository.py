from abc import ABC, abstractmethod


from app.domain.models.config import ProfileConfig
from app.domain.models.profile import Profile


class DataRepository(ABC):
    @abstractmethod
    def load_profiles(self, table_name: str, config: ProfileConfig) -> list[Profile]:
        raise NotImplementedError("Method 'load_profiles' not implemented")
