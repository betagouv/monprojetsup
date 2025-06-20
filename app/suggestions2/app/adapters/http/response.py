from enum import Enum
from typing import Dict, List, Optional

from pydantic import BaseModel, Field


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


class Score(BaseModel):
    key: str = Field(examples=["fl2014"])
    scores: Dict[str, float] = Field(description="Scores obtenus aux différents critères.")


class SuggestionRequestResponse(BaseModel):
    header: ResponseHeader = ResponseHeader()
    scores: List[Score] = Field(description="Liste des scores de chaque formation.")
