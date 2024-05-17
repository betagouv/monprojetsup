package fr.gouv.monprojetsup.commun.erreur.domain

abstract class MonProjetSupExceptions(
    code: String,
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)

abstract class MonProjetSup5xxExceptions(
    code: String,
    message: String,
    cause: Throwable?,
) : MonProjetSupExceptions(code, message, cause)

data class MonProjetSupInternalErrorException(
    val code: String,
    val msg: String,
    val origine: Throwable?,
) : MonProjetSup5xxExceptions(code, msg, origine)
