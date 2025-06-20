from dataclasses import dataclass
from pydantic import BaseModel


class Explanations(BaseModel):
    pass


@dataclass
class MultiExplanations:
    explanations: dict[str, Explanations]
