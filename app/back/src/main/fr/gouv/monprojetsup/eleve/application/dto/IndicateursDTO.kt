package fr.gouv.monprojetsup.eleve.application.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

data class IndicateursDTO(
    @JsonProperty("formationsRealistes")
    @Schema(
        description = "Le nombre de formations estimées \"réalistes\" par le lycéen",
        example = "1",
    )
    val formationsRealistes: Int,
    @JsonProperty("formationsAmbitieuses")
    @Schema(
        description = "Le nombre de formations estimées \"ambitieuses\" par le lycéen",
        example = "2",
    )
    val formationsAmbitieuses: Int,
    @JsonProperty("formationsPlanB")
    @Schema(
        description = "Le nombre de formations estimées \"plan B\" par le lycéen",
        example = "3",
    )
    val formationsPlanB: Int,
)
