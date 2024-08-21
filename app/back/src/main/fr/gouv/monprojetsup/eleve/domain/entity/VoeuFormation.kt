package fr.gouv.monprojetsup.eleve.domain.entity

data class VoeuFormation(
    val idFormation: String,
    val niveauAmbition: Int,
    val tripletsAffectationsChoisis: List<String>,
    val priseDeNote: String?,
)
