package fr.gouv.monprojetsup.data.app.domain

data class AffinitesPourProfil(
    val metiersTriesParAffinites: List<String>,
    val formations: List<fr.gouv.monprojetsup.data.app.domain.AffinitesPourProfil.FormationAvecSonAffinite>,
) {
    data class FormationAvecSonAffinite(
        val idFormation: String,
        val tauxAffinite: Float,
    )
}
