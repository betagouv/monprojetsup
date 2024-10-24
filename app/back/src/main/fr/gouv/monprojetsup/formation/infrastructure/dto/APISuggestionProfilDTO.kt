package fr.gouv.monprojetsup.formation.infrastructure.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.Constantes.NOTE_NON_REPONSE
import fr.gouv.monprojetsup.commun.Constantes.TAILLE_ECHELLON_NOTES
import fr.gouv.monprojetsup.formation.infrastructure.dto.SuggestionDTO.CorbeilleSuggestionDTO
import fr.gouv.monprojetsup.formation.infrastructure.dto.SuggestionDTO.FavorisSuggestionDTO

@JsonIgnoreProperties(ignoreUnknown = true)
data class APISuggestionProfilDTO(
    @field:JsonProperty(value = "niveau")
    val classe: String?,
    @field:JsonProperty(value = "bac")
    val baccalaureat: String?,
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
    constructor(profilEleve: ProfilEleve.AvecProfilExistant) : this(
        classe = profilEleve.classe?.apiSuggestionValeur,
        baccalaureat =
            when (profilEleve.baccalaureat) {
                "NC" -> ""
                else -> profilEleve.baccalaureat
            },
        duree = profilEleve.dureeEtudesPrevue?.apiSuggestionValeur,
        alternance = profilEleve.alternance?.apiSuggestionValeur,
        preferencesGeographiques = profilEleve.communesFavorites?.map { it.codeInsee },
        specialites = profilEleve.specialites,
        interets = (profilEleve.centresInterets ?: emptyList()) + (profilEleve.domainesInterets ?: emptyList()),
        moyenneGenerale =
            profilEleve.moyenneGenerale?.let {
                if (it == NOTE_NON_REPONSE) null else (it / TAILLE_ECHELLON_NOTES).toInt().toString()
            },
        choix =
            (
                profilEleve.metiersFavoris?.map { FavorisSuggestionDTO(it) }
                    ?: emptyList()
            ) + (profilEleve.formationsFavorites?.map { FavorisSuggestionDTO(it.idFormation) } ?: emptyList()) +
                profilEleve.corbeilleFormations.map { CorbeilleSuggestionDTO(it) },
    )
}

sealed class SuggestionDTO(
    @field:JsonProperty(value = "fl")
    open val id: String,
    @field:JsonProperty(value = "status")
    val statut: Int, // "statut. \"1\": dans les favoris. \"2\": dans la corbeille."
    @field:JsonProperty(value = "date")
    val date: String? = null,
) {
    data class FavorisSuggestionDTO(
        @field:JsonProperty(value = "fl")
        override val id: String,
    ) : SuggestionDTO(id = id, statut = 1)

    data class CorbeilleSuggestionDTO(
        @field:JsonProperty(value = "fl")
        override val id: String,
    ) : SuggestionDTO(id = id, statut = 2)
}
