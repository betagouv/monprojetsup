package fr.gouv.monprojetsup.formation.application.dto

data class FormationsCourtesReponseDTO(
    val formations: List<FormationDTO>,
) {
    data class FormationDTO(
        val id: String,
        val nom: String,
        val villes: List<String>,
        val metiers: List<String>,
        val tauxAffinite: Float,
    )
}
