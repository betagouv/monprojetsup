from typing import Iterable
from pydantic import BaseModel, BeforeValidator
from typing_extensions import Annotated

from app.domain.models.profile import Item


def iterable_to_set(value: Iterable[str]) -> set[str]:
    return set(value)


class ProfileConfig(BaseModel):
    """
    Defines the set of features to load in a profile.
    """

    use_situation: bool = False
    use_classe: bool = False
    use_id_baccalaureat: bool = False
    use_duree_etudes_prevue: bool = False
    use_alternance: bool = False
    use_specialites: bool = False
    use_interests: bool = False
    use_metiers_favoris: bool = False
    use_corbeille_formations: bool = False
    use_communes_favorites: bool = False
    use_formations_favorites: bool = False
    use_voeux_favoris: bool = False
    targets: Annotated[set[str], BeforeValidator(iterable_to_set)] = set()

    def filter_targets(self, items: list[Item]) -> list[str]:
        return [item.value for item in items if item.category in self.targets]
