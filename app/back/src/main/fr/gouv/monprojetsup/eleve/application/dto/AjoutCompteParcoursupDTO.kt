package fr.gouv.monprojetsup.eleve.application.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

data class AjoutCompteParcoursupDTO(
    @JsonProperty("codeVerifier")
    val codeVerifier: String,
    @JsonProperty("code")
    val code: String,
    @JsonProperty("redirectUri")
    val redirectUri: String,
)
