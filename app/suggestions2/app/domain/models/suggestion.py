from dataclasses import dataclass


@dataclass
class Suggestions:
    # For each target item, its score
    scores: dict[str, float]


@dataclass
class MultiSuggestions:
    # For each service, its suggestions
    scores: dict[str, Suggestions]
