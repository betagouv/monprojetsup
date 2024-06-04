package fr.gouv.monprojetsup.recherche.infrastructure.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import fr.gouv.monprojetsup.recherche.domain.entity.AffinitesPourProfil
import fr.gouv.monprojetsup.recherche.domain.entity.FormationAvecSonAffinite

@JsonIgnoreProperties(ignoreUnknown = true)
data class AffinitesProfilReponseDTO(
    @JsonProperty(value = "affinites")
    val affinites: List<AffinitesDTO>,
    @JsonProperty(value = "metiers")
    val metiersTriesParAffinites: List<String>,
) : APISuggestionReponseDTO() {
    fun toAffinitesPourProfil() =
        AffinitesPourProfil(
            formations =
                affinites.sortedByDescending { it.affinite }.map {
                    FormationAvecSonAffinite(
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
