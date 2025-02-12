package fr.gouv.monprojetsup.eleve.application.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

data class ProgressionDTO(
    @JsonProperty("progression")
    @Schema(
        description = "Progression dans les six niveaux MPS",
        example = "6",
        allowableValues = ["1", "2", "3", "4", "5", "6"],
    )
    val progression: Int,
)
