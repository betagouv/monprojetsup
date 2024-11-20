package fr.gouv.monprojetsup.eleve.domain.entity

data class CommuneFavorite(
    val codeInsee: String,
    val nom: String,
    val latitude: Double,
    val longitude: Double,
)
