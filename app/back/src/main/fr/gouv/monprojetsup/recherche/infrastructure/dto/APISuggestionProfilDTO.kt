package fr.gouv.monprojetsup.recherche.infrastructure.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import fr.gouv.monprojetsup.recherche.domain.entity.ProfilEleve

@JsonIgnoreProperties(ignoreUnknown = true)
data class APISuggestionProfilDTO(
    @field:JsonProperty(value = "niveau")
    val classe: String?,
    @field:JsonProperty(value = "bac")
    val bac: String?,
    @field:JsonProperty(value = "duree")
    val duree: String?,
    @field:JsonProperty(value = "apprentissage")
    val alternance: String?,
    @field:JsonProperty(value = "geo_pref")
    val preferencesGeographiques: List<String>?,
    @field:JsonProperty(value = "spe_classes")
    val specialites: List<String>?,
    @field:JsonProperty(value = "interests")
    val interets: List<String>?,
    @field:JsonProperty(value = "moygen")
    val moyenneGenerale: String?,
    @field:JsonProperty(value = "choices")
    val choix: List<SuggestionDTO>?,
) {
    constructor(profilEleve: ProfilEleve, specialites: List<String>?) : this(
        classe = profilEleve.classe.apiSuggestionValeur,
        bac =
            when (profilEleve.bac) {
                "NC" -> ""
                else -> profilEleve.bac
            },
        duree = profilEleve.dureeEtudesPrevue.apiSuggestionValeur,
        alternance = profilEleve.alternance.apiSuggestionValeur,
        preferencesGeographiques = profilEleve.villesPreferees,
        specialites = specialites,
        interets = (profilEleve.centresInterets ?: emptyList()) + (profilEleve.domainesInterets ?: emptyList()),
        moyenneGenerale = profilEleve.moyenneGenerale?.toString(),
        choix =
            (
                profilEleve.metiersChoisis?.map { SuggestionDTO(it) }
                    ?: emptyList()
            ) + (profilEleve.formationsChoisies?.map { SuggestionDTO(it) } ?: emptyList()),
    )
}

data class SuggestionDTO(
    val fl: String,
    val status: Int,
    val date: String? = null,
) {
    constructor(nom: String) : this(
        fl = nom,
        status = 1, // "statut. \"1\": dans les favoris. \"2\": dans la corbeille."
    )
}
