package fr.gouv.monprojetsup.eleve.application.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

data class AjoutCompteParcoursupDTO(
    @Schema(
        description = "Jwt parcoursup valide",
        example = "eykfjhzek",
        required = true,
    )
    @JsonProperty("jwtParcoursup")
    val jwtParcoursup: String,
)
