### Helper functions for parsing db rows
from typing import Annotated, Any, Dict, List, Literal

from pydantic import BaseModel, BeforeValidator


def parse_formations(val: List[Dict[str, Any]] | None) -> List[str]:
    """
    Extract the IDs of the formations
    """
    return [f["idFormation"] for f in val or []]


def parse_voeux(val: List[Dict[str, Any]] | None) -> List[str]:
    """
    Extract the IDs of the "parcoursup voeux"
    """
    return [v["idVoeu"] for v in val or []]


def parse_communes(val: List[Dict[str, Any]] | None) -> List[str]:
    """
    Extract the INSEE Code of the cities
    """
    return [c["codeInsee"] for c in val or []]


def none_to_empty_list(val: List[Any] | None) -> List[Any]:
    return val or []


class StudentDbRow(BaseModel):
    id: str
    situation: Literal["AUCUNE_IDEE", "QUELQUES_PISTES", "PROJET_PRECIS"] | None
    classe: Literal["SECONDE", "PREMIERE", "TERMINALE"] | None
    id_baccalaureat: (
        Literal[
            "Générale",
            "E",
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
    )
    duree_etudes_prevue: Literal["INDIFFERENT", "AUCUNE_IDEE", "COURTE", "LONGUE"] | None
    alternance: Literal["PAS_INTERESSE", "INTERESSE", "INDIFFERENT", "TRES_INTERESSE"] | None
    specialites: Annotated[List[str], BeforeValidator(none_to_empty_list)] = []
    domaines: Annotated[List[str], BeforeValidator(none_to_empty_list)] = []
    centres_interets: Annotated[List[str], BeforeValidator(none_to_empty_list)] = []
    metiers_favoris: Annotated[List[str], BeforeValidator(none_to_empty_list)] = []
    corbeille_formations: Annotated[List[str], BeforeValidator(none_to_empty_list)] = []
    communes_favorites: Annotated[List[str], BeforeValidator(parse_communes)] = []
    formations_favorites: Annotated[List[str], BeforeValidator(parse_formations)] = []
    voeux_favoris: Annotated[List[str], BeforeValidator(parse_voeux)] = []
