from typing import Iterator, List, Literal, Tuple
from pydantic import BaseModel

from app.api.types import ChoixStatus, Profile
from app.config import BASKET_CONFIG, Variable
from app.database.types import StudentDbRow


def encode_item(item: str, positive: bool = True) -> str:
    if positive:
        return item + "_pos"
    else:
        return item + "_neg"


ItemSide = Literal["positive", "negative"]


def decode_item(item: str) -> Tuple[str, ItemSide]:
    assert item.endswith(("_pos", "_neg")), "Invalid suffix for decoding items"
    side: ItemSide = "positive" if item[-4:] == "_pos" else "negative"
    return item[:-4], side


class Basket(BaseModel):
    targets: List[str]
    items: List[str]

    @staticmethod
    def from_pg_row(row: StudentDbRow) -> "Basket":
        """
        Extracts the relevant information from a row of the `profil_eleve` table
        """
        targets = row.formations_favorites
        items: List[str] = []

        features = BASKET_CONFIG["features"]
        if Variable.FormationsFavorites in features:
            items += [encode_item(f) for f in row.formations_favorites]
        if Variable.CorbeilleFormations in features:
            items += [encode_item(v, False) for v in row.corbeille_formations]
        if Variable.Specialites in features:
            items += [encode_item(v) for v in row.specialites]
        if Variable.DomainesEtInterets in features:
            items += [encode_item(v) for v in row.domaines]
            items += [encode_item(v) for v in row.centres_interets]
        if Variable.VoeuxParcoursup in features:
            items += [encode_item(v) for v in row.voeux_favoris]
        if Variable.Metiers in features:
            items += [encode_item(f) for f in row.metiers_favoris]

        return Basket(targets=targets, items=items)

    @staticmethod
    def from_request_profile(profile: Profile) -> "Basket":
        # Targets for a profile are empty, we only need the features to predict
        targets: List[str] = []
        items = [
            encode_item(el.id, el.status == ChoixStatus.Favorite)
            for el in profile.choix
            if el.status in [ChoixStatus.Favorite, ChoixStatus.Deleted]
        ]

        features = BASKET_CONFIG["features"]
        if Variable.Specialites in features:
            items += [encode_item(v) for v in profile.spe_classes]
        if Variable.DomainesEtInterets in features:
            items += [encode_item(v) for v in profile.interests]

        return Basket(targets=targets, items=items)

    def get_targets(self) -> List[str]:
        return self.targets

    def get_items(self) -> List[str]:
        return self.items

    def __str__(self) -> str:
        return f"Basket {{ targets: {self.targets}, items: {self.items} }}"


class Student(BaseModel):
    id: str
    basket: Basket

    def __str__(self) -> str:
        return f"Student {self.id}: {self.basket}"


class DatasetStudents:
    def __init__(self, students: List[Student]):
        self.students = {student.id: student for student in students}
        self.ids = list(self.students.keys())

    def get_nth_student(self, n: int) -> Student:
        return self.students[self.ids[n]]

    def __iter__(self) -> Iterator[Student]:
        return (self.students[id] for id in self.students)

    def __len__(self) -> int:
        return len(self.students)

    def __str__(self) -> str:
        return "\n".join(str(student) for student in self)
