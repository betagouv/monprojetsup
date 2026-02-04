from abc import ABC, abstractmethod
from typing import Iterator

from app.domain.models.config import ProfileConfig
from app.domain.models.profile import Profile


class DataRepository(ABC):
    @abstractmethod
    def iter_profiles_batched(
        self, table_name: str, config: ProfileConfig, batch_size: int = 10000
    ) -> Iterator[list[Profile]]:
        raise NotImplementedError("Method 'iter_profiles_batched' not implemented")
