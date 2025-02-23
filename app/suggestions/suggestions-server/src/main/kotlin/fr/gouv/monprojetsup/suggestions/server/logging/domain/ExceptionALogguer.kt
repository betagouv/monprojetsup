package fr.gouv.monprojetsup.suggestions.server.logging.domain

data class ExceptionALogguer(
    val statusCode: Int,
    val code: String,
    val message: String,
    val exception: Throwable? = null,
)
