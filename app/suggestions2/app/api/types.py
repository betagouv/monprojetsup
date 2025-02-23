from enum import Enum
from typing import Dict, List, Literal, Optional
from pydantic import BaseModel, Field


class Choix(BaseModel):
    id: str
    status: int
    date: str | None = None


class ChoixStatus(Enum):
    Favorite = 1
    Deleted = 2


class Profile(BaseModel):
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
    ) = Field(default=None, description="type de Bac choisi ou envisagé", examples=["Générale"])
    duree: Literal["", "court", "long", "indiff"] = Field(
        default="", description="durée envisagée des études", examples=["court", "long", "indiff"]
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


class ServerStatus(Enum):
    OK = 0
    SERVER_ERROR = 1
    USER_ERROR = 2


class ResponseHeader(BaseModel):
    status: ServerStatus = ServerStatus.OK
    error: Optional[str] = Field(default=None, description="explication de l'erreur si status != 0")
    userMessage: Optional[str] = Field(
        default=None, description="message à afficher à l'utilisateur final."
    )


## Suggestions
class SuggestionRequestBody(BaseModel):
    profile: Profile = Field(description="Profil utilisé pour évaluer l'affinité.")
    inclureScores: bool = False


class Affinite(BaseModel):
    key: str = Field(examples=["fl2014"])
    affinite: float = Field(description="Score d'affinité entre 0.0 et 1.0.")
    scores: Optional[Dict[str, float]] = Field(
        default=None, description="Scores obtenus aux différents critères."
    )


class SuggestionRequestResponse(BaseModel):
    header: ResponseHeader = ResponseHeader()
    affinites: List[Affinite] = Field(
        description="Liste des formations dans l'ordre d'affichage, ainsi que le score d'affinité dans l'intervalle [0.0 , 1.0]."
    )


## Explanations
class ExplanationRequestBody(BaseModel):
    profile: Profile = Field(description="Profil utilisé pour évaluer l'affinité.")
    keys: List[str] = Field(
        description="clés des formations pour lesquelles les explications sont demandées",
        examples=[["fl210", "fr22", "fl2014"]],
    )


class NaivesBayesExplanation(BaseModel):
    item_key: str = Field(description="clé de l'item", examples=["mat5", "ci1"])
    score: float = Field(
        description="score mesurant l'impact de l'item la sélection de la formation",
        examples=[-0.12345, 1.78253],
    )
    side: Literal["positive", "negative"] = Field(
        description="si l'item était présent positivement ou négativement dans le profil."
    )


class Explanation(BaseModel):
    key: str = Field(examples=["fl2014"], description="clé de la formation")
    popularity: float = Field(
        description="score lié à la popularité de la formation", examples=[1.78253, -0.12345]
    )
    profile: List[NaivesBayesExplanation] = Field(
        description="pour chaque item du profil, un score mesurant son impact dans la sélection de la clé"
    )


class ExplanationRequestResponse(BaseModel):
    header: ResponseHeader = ResponseHeader()
    liste: List[Explanation] = Field()
