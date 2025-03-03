package fr.gouv.monprojetsup.formation.application.dto

data class FormationsCourtesDTO(
    val formations: List<FormationCourteDTO>,
) {
    @Suppress("unused")
    private constructor() : this(emptyList())
}
