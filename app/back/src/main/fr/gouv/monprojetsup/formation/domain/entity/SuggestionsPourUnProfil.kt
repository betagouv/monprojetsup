package fr.gouv.monprojetsup.formation.domain.entity

data class SuggestionsPourUnProfil(
    val metiersTriesParAffinites: List<String>,
    val formations: List<FormationAvecSonAffinite>,
) {
    data class FormationAvecSonAffinite(
        val idFormation: String,
        val tauxAffinite: Float,
    )
}
