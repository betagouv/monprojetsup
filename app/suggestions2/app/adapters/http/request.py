from enum import Enum
from typing import List, Literal
from pydantic import BaseModel, Field

from app.domain.models.config import ProfileConfig
from app.domain.models.profile import Item, Profile


class Choix(BaseModel):
    id: str
    status: int
    date: str | None = None


class ChoixStatus(Enum):
    Favorite = 1
    Deleted = 2


class SuggestionRequestProfile(BaseModel):
    niveau: Literal["", "sec", "prem", "term"] = Field(
        default="", description="classe actuelle", examples=["sec", "term"]
    )
    bac: (
        Literal[
            "Générale",
            "P",
            "PA",
            "S2TMD",
            "ST2S",
            "STAV",
            "STD2A",
            "STHR",
            "STI2D",
            "STL",
            "STMG",
            "NC",
        ]
        | None
    ) = Field(
        default=None,
        description="type de Bac choisi ou envisagé",
        examples=["Générale"],
    )
    duree: Literal["", "court", "long", "indiff"] = Field(
        default="",
        description="durée envisagée des études",
        examples=["court", "long", "indiff"],
    )
    apprentissage: Literal["", "A", "B", "C", "D"] = Field(
        default="",
        description="intérêt pour les formations en apprentissage",
        examples=["A", "B", "C", "D"],
    )
    geo_pref: List[str] = Field(
        [],
        description="villes préférées pour étudier (code insee)",
        examples=[["33514", "44001"]],
    )
    spe_classes: List[str] = Field(
        [],
        description="spécialités (eds ou spécialités de bac) de terminale choisis ou envisagés",
        examples=[["sp757", "mat5"]],
    )
    interests: List[str] = Field(
        [],
        description="domaines et intérêts",
        examples=[["ci1", "ci2", "ci3", "dom1", "dom2", "dom3"]],
    )
    choix: List[Choix] = Field([], description="sélection de formations, voeux et métiers")
    situation: Literal["aucune_idee", "quelques_pistes", "projet_precis"] | None = Field(
        default=None, description="statut de réflexion"
    )

    def to_profile(self, config: ProfileConfig):
        items: list[Item] = []

        def unsupported_field(cond: bool, c: str):
            if cond:
                raise NotImplementedError(
                    f"Field '{c}' is currently not supported in suggestion2 requests, "
                    "see issue https://github.com/betagouv/monprojetsup/issues/944 )."
                )

        def push_if_not_none(cond: bool, i: list[Item], v: str | None, c: str):
            if v is not None and cond:
                i.append(Item(value=v, category=c))

        unsupported_field(config.use_situation, "situation")
        unsupported_field(config.use_classe, "class")
        push_if_not_none(config.use_id_baccalaureat, items, self.bac, "id_baccalaureat")
        unsupported_field(config.use_duree_etudes_prevue, "duree_etudes_prevue")
        unsupported_field(config.use_alternance, "alternance")

        def push_list(cond: bool, i: list[Item], l: list[str], c: str):  # noqa: E741
            if cond:
                i += [Item(value=v, category=c) for v in l]

        metiers_favoris = [
            f.id for f in self.choix if f.id.startswith("met") and f.status == ChoixStatus.Favorite
        ]
        corbeille_formations = [
            f.id for f in self.choix if f.id.startswith("f") and f.status == ChoixStatus.Deleted
        ]
        formations_favorites = [
            f.id for f in self.choix if f.id.startswith("f") and f.status == ChoixStatus.Favorite
        ]
        # voeux_favoris = [
        #     f.id for f in self.choix if f.id.startswith("f") and f.status == ChoixStatus.Favorite
        # ]

        push_list(config.use_specialites, items, self.spe_classes, "specialites")
        push_list(config.use_interests, items, self.interests, "interests")
        push_list(config.use_metiers_favoris, items, metiers_favoris, "metiers_favoris")
        push_list(
            config.use_corbeille_formations, items, corbeille_formations, "corbeille_formations"
        )
        push_list(config.use_communes_favorites, items, self.geo_pref, "communes_favorites")
        push_list(
            config.use_formations_favorites,
            items,
            formations_favorites,
            "formations_favorites",
        )
        unsupported_field(
            config.use_voeux_favoris, "voeux_favoris"
        )  # TODO: how do we know if something is a voeu?

        # push_list(config.use_voeux_favoris, items, voeux_favoris, "voeux_favoris")

        targets = config.filter_targets(items)
        return Profile(features=items, targets=targets)


class SuggestionRequest(BaseModel):
    profile: SuggestionRequestProfile = Field(description="Profil utilisé pour évaluer les scores.")
    keys: list[str] = Field(
        description="Liste des clés pour lesquelles les suggestions sont demandées.",
        default_factory=list,
        examples=[["fl210", "fr22", "fl2014"]],
    )


class ExplanationRequest(BaseModel):
    profile: SuggestionRequestProfile = Field(description="Profil utilisé pour évaluer les scores.")
    keys: list[str] = Field(
        description="Liste des clés pour lesquelles les explications sont demandées.",
        default_factory=list,
        examples=[["fl210", "fr22", "fl2014"]],
    )
