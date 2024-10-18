package fr.gouv.monprojetsup.commun.client

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class TokenReponseDTO(
    @JsonProperty(value = "access_token")
    val token: String,
    @JsonProperty(value = "id_token")
    val idToken: String,
)
