package fr.gouv.monprojetsup.formation.domain.entity

data class Voeu(
    val id: String,
    val nom: String,
    val commune: CommuneCourte,
    val latitude: Double,
    val longitude: Double,
)
