from enum import Enum
from typing import Dict, List, Optional

from pydantic import BaseModel, Field


class ServerStatus(Enum):
    OK = 0
    SERVER_ERROR = 1
    USER_ERROR = 2


class ResponseHeader(BaseModel):
    status: ServerStatus = ServerStatus.OK
    error: Optional[str] = Field(
        default=None, description="explication de l'erreur si status != 0"
    )
    userMessage: Optional[str] = Field(
        default=None, description="message à afficher à l'utilisateur final."
    )


class Score(BaseModel):
    key: str = Field(examples=["fl2014"])
    scores: Dict[str, float] = Field(
        description="Scores obtenus aux différents critères.",
        examples=[{"expert": 0.5, "eleve": 0.3}],
    )


class SuggestionResponse(BaseModel):
    header: ResponseHeader = ResponseHeader()
    scores: List[Score] = Field(description="Liste des scores de chaque formation.")


class ExplanationResponseScore(BaseModel):
    key: str = Field(
        description="Identifiant de la feature responsable de la différence de score"
    )
    log_influence: float = Field(
        description="Influence de cette feature sur le score, en échelle logarithmique"
    )
    categorie: str = Field(
        description="Catégorie dans laquelle se trouvait la feature.",
        examples=["corbeille_formation", "voeux_favoris"],
    )


class ExplanationResponseDetails(BaseModel):
    popularity: float = Field(description="Log-popularité de l'item.")
    scores: List[ExplanationResponseScore] = Field(
        description="Influence de chaque feature du profile sur le score de l'item."
    )


class ExplanationResponseContent(BaseModel):
    key: str = Field(description="Item expliqué")
    explanations: Dict[str, ExplanationResponseDetails] = Field(
        description="Pour chaque service, les explications associées"
    )


class ExplanationResponse(BaseModel):
    header: ResponseHeader = ResponseHeader()
    explanations: List[ExplanationResponseContent] = Field(
        description="Liste des explications générées pour ce profil."
    )
