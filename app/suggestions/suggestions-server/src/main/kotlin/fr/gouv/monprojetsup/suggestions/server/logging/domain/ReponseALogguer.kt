package fr.gouv.monprojetsup.suggestions.server.logging.domain

data class ReponseALogguer(
    val statusCode: Int,
    val methode: String,
    val path: String,
)
