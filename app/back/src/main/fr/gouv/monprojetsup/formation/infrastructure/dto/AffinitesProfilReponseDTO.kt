package fr.gouv.monprojetsup.formation.infrastructure.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil

@JsonIgnoreProperties(ignoreUnknown = true)
data class AffinitesProfilReponseDTO(
    @JsonProperty(value = "affinites")
    val affinites: List<AffinitesDTO>,
    @JsonProperty(value = "metiers")
    val metiersTriesParAffinites: List<String>,
) : APISuggestionReponseDTO() {
    fun toAffinitesPourProfil() =
        SuggestionsPourUnProfil(
            formations =
                affinites.sortedByDescending { it.affinite }.map {
                    SuggestionsPourUnProfil.FormationAvecSonAffinite(
                        idFormation = it.key,
                        tauxAffinite = it.affinite,
                    )
                },
            metiersTriesParAffinites = metiersTriesParAffinites,
        )
}

data class AffinitesDTO(
    @JsonProperty(value = "key")
    val key: String,
    @JsonProperty(value = "affinite")
    val affinite: Float,
)
