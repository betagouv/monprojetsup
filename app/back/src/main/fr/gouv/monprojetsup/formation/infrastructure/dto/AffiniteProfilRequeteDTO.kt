package fr.gouv.monprojetsup.formation.infrastructure.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class AffiniteProfilRequeteDTO(
    @field:JsonProperty(value = "profile")
    val profil: APISuggestionProfilDTO,
) : APISuggestionRequeteDTO()