package fr.gouv.monprojetsup.recherche.application.dto

data class RechercheFormationReponseDTO(
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
