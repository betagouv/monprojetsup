from dataclasses import dataclass


@dataclass(eq=True, frozen=True)
class Item:
    value: str
    category: str

    def __str__(self) -> str:
        return self.category + ":" + self.value


@dataclass(eq=True, frozen=True)
class Profile:
    features: list[Item]
    targets: list[str]

    def features_str(self) -> list[str]:
        return [str(feature) for feature in self.features]
