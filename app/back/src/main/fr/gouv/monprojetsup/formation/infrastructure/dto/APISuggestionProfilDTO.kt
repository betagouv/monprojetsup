package fr.gouv.monprojetsup.formation.infrastructure.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.Constantes.NOTE_NON_REPONSE
import fr.gouv.monprojetsup.commun.Constantes.TAILLE_ECHELLON_NOTES
import fr.gouv.monprojetsup.formation.infrastructure.dto.ChoixDTO.CorbeilleChoixDTO
import fr.gouv.monprojetsup.formation.infrastructure.dto.ChoixDTO.FavorisChoixDTO

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
    @field:JsonProperty(value = "choix")
    val choix: List<ChoixDTO>?,
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
                profilEleve.metiersFavoris?.map { FavorisChoixDTO(it) }
                    ?: emptyList()
            ) + (profilEleve.formationsFavorites?.map { FavorisChoixDTO(it.idFormation) } ?: emptyList()) +
                profilEleve.corbeilleFormations.map { CorbeilleChoixDTO(it) } +
                profilEleve.voeuxFavoris.map { FavorisChoixDTO(it.idVoeu) },
    )
}

sealed class ChoixDTO(
    @field:JsonProperty(value = "id")
    open val id: String,
    @field:JsonProperty(value = "status")
    val statut: Int, // "statut. \"1\": dans les favoris. \"2\": dans la corbeille."
    @field:JsonProperty(value = "date")
    val date: String? = null,
) {
    data class FavorisChoixDTO(
        @field:JsonProperty(value = "id")
        override val id: String,
    ) : ChoixDTO(id = id, statut = 1)

    data class CorbeilleChoixDTO(
        @field:JsonProperty(value = "id")
        override val id: String,
    ) : ChoixDTO(id = id, statut = 2)
}
