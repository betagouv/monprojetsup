package fr.gouv.monprojetsup.logging.domain

data class ReponseALogguer(
    val statusCode: Int,
    val methode: String,
    val path: String,
)
