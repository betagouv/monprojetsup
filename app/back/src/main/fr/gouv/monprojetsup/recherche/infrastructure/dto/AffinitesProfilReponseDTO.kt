package fr.gouv.monprojetsup.recherche.infrastructure.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class AffinitesProfilReponseDTO(
    @JsonProperty(value = "affinites")
    val affinites: List<AffinitesDTO>,
    @JsonProperty(value = "metiers")
    val metiersTriesParAffinites: List<String>,
)

data class AffinitesDTO(
    @JsonProperty(value = "key")
    val key: String,
    @JsonProperty(value = "affinite")
    val affinite: Float,
)
