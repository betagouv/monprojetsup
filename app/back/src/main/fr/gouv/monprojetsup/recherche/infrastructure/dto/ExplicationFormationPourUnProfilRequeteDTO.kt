package fr.gouv.monprojetsup.recherche.infrastructure.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class ExplicationFormationPourUnProfilRequeteDTO(
    @field:JsonProperty(value = "profile")
    val profil: APISuggestionProfilDTO,
    @field:JsonProperty(value = "keys")
    val formations: List<String>,
) : APISuggestionRequeteDTO()
