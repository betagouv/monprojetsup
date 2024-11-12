package fr.gouv.monprojetsup.referentiel.domain.entity

data class Domaine(
    val id: String,
    val nom: String,
    val description: String?,
    val emoji: String,
)

data class CategorieDomaine(
    val id: String,
    val nom: String,
    val emoji: String,
)
