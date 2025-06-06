package fr.gouv.monprojetsup.eleve.domain.entity

data class FormationFavorite(
    val idFormation: String,
    val niveauAmbition: Int,
    val priseDeNote: String?,
)
