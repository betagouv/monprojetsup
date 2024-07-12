package fr.gouv.monprojetsup.eleve.domain.entity

data class Commune(
    val codeInsee: String,
    val nom: String,
    val latitude: Float,
    val longitude: Float,
)
