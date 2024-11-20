package fr.gouv.monprojetsup.formation.domain.entity

import fr.gouv.monprojetsup.eleve.domain.entity.CommuneFavorite

data class CommuneAvecVoeuxAuxAlentours(
    val communeFavorite: CommuneFavorite,
    val distances: List<VoeuAvecDistance>,
) {
    data class VoeuAvecDistance(
        val voeu: Voeu,
        val km: Int,
    )
}

data class CommuneAvecIdsVoeuxAuxAlentours(
    val communeFavorite: CommuneFavorite,
    val distances: List<VoeuAvecDistance>,
) {
    data class VoeuAvecDistance(
        val idVoeu: String,
        val km: Int,
    )
}
