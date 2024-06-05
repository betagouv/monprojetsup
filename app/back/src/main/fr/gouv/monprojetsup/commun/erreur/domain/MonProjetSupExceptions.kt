package fr.gouv.monprojetsup.commun.erreur.domain

abstract class MonProjetSupExceptions(
    code: String,
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)

abstract class MonProjetSup4xxExceptions(
    code: String,
    message: String,
    cause: Throwable?,
) : MonProjetSupExceptions(code, message, cause)

abstract class MonProjetSup5xxExceptions(
    code: String,
    message: String,
    cause: Throwable?,
) : MonProjetSupExceptions(code, message, cause)

data class MonProjetSupNotFoundException(
    val code: String,
    val msg: String,
    val origine: Throwable? = null,
) : MonProjetSup4xxExceptions(code, msg, origine)

data class MonProjetSupInternalErrorException(
    val code: String,
    val msg: String,
    val origine: Throwable?,
) : MonProjetSup5xxExceptions(code, msg, origine)

data class MonProjetIllegalStateErrorException(
    val code: String,
    val msg: String,
    val origine: Throwable? = null,
) : MonProjetSup5xxExceptions(code, msg, origine)
