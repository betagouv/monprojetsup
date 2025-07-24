from dataclasses import dataclass

from app.domain.models.profile import Item


@dataclass
class Explanation:
    key: str
    relative_frequency: dict[Item, float]
    popularity: float


@dataclass
class Explanations:
    expls: dict[str, Explanation]


@dataclass
class MultiExplanations:
    explanations: dict[str, Explanations]
