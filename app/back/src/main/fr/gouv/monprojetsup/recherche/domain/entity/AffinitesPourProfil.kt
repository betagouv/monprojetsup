package fr.gouv.monprojetsup.recherche.domain.entity

data class AffinitesPourProfil(
    val metiersTriesParAffinites: List<String>,
    val formations: List<FormationAvecSonAffinite>,
) {
    data class FormationAvecSonAffinite(
        val idFormation: String,
        val tauxAffinite: Float,
    )
}
