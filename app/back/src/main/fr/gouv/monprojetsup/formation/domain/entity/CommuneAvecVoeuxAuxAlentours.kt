package fr.gouv.monprojetsup.formation.domain.entity

import fr.gouv.monprojetsup.eleve.domain.entity.Commune

data class CommuneAvecVoeuxAuxAlentours(
    val commune: Commune,
    val distances: List<VoeuAvecDistance>,
) {
    data class VoeuAvecDistance(
        val voeu: TripletAffectation,
        val km: Int,
    )
}

data class CommuneAvecIdsVoeuxAuxAlentours(
    val commune: Commune,
    val distances: List<VoeuAvecDistance>,
) {
    data class VoeuAvecDistance(
        val idVoeu: String,
        val km: Int,
    )
}
