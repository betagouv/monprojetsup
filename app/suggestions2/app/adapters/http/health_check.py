from pydantic import BaseModel, Field


class HealthCheck(BaseModel):
    """Health check response"""

    status: str = Field(
        "OK",
        description="Statut du service, toujours égal à 'OK'.",
        examples=["OK"],
    )
