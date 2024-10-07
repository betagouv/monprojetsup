package fr.gouv.monprojetsup.eleve.application.dto

import com.fasterxml.jackson.annotation.JsonProperty
import fr.gouv.monprojetsup.eleve.domain.entity.ParametresPourRecupererToken

data class AjoutCompteParcoursupDTO(
    @JsonProperty("codeVerifier")
    val codeVerifier: String,
    @JsonProperty("code")
    val code: String,
    @JsonProperty("redirectUri")
    val redirectUri: String,
) {
    fun toParametresPourRecupererToken() =
        ParametresPourRecupererToken(
            codeVerifier = codeVerifier,
            code = code,
            redirectUri = redirectUri,
        )
}
