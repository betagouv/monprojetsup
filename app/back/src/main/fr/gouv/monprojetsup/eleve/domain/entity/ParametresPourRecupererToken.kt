package fr.gouv.monprojetsup.eleve.domain.entity

data class ParametresPourRecupererToken(
    val codeVerifier: String,
    val code: String,
    val redirectUri: String,
)
