package fr.gouv.monprojetsup.eleve.domain.entity

data class VoeuFormation(
    val idFormation: String,
    val niveauAmbition: Int,
    val voeuxChoisis: List<String>,
    val priseDeNote: String?,
)
