package fr.gouv.monprojetsup.logging.domain

data class WarningALogguer(
    val messageException: String,
    val parametres: Map<String, Any>,
    val type: String,
)
