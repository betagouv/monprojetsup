package fr.gouv.monprojetsup.eleve.domain.entity

data class FormationFavorite(
    val idFormation: String,
    val niveauAmbition: Int,
    val priseDeNote: String?,
) {
    companion object {
        const val MAX_NIVEAU_AMBITION = 3
        const val NIVEAU_AMBITION_PLANB = 1
        const val NIVEAU_AMBITION_REALISTE = 2
        const val NIVEAU_AMBITION_AMBITIEUX = 3
    }
}
