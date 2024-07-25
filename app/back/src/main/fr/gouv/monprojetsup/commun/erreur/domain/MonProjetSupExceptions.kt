package fr.gouv.monprojetsup.commun.erreur.domain

abstract class MonProjetSupExceptions(
    open val code: String,
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)

abstract class MonProjetSup4xxExceptions(
    override val code: String,
    message: String,
    cause: Throwable?,
) : MonProjetSupExceptions(code, message, cause)

abstract class MonProjetSup5xxExceptions(
    override val code: String,
    message: String,
    cause: Throwable?,
) : MonProjetSupExceptions(code, message, cause)

data class MonProjetSupNotFoundException(
    override val code: String,
    val msg: String,
    val origine: Throwable? = null,
) : MonProjetSup4xxExceptions(code, msg, origine)

data class MonProjetSupForbiddenException(
    override val code: String,
    val msg: String,
    val origine: Throwable? = null,
) : MonProjetSup4xxExceptions(code, msg, origine)

data class MonProjetSupInternalErrorException(
    override val code: String,
    val msg: String,
    val origine: Throwable?,
) : MonProjetSup5xxExceptions(code, msg, origine)

data class MonProjetIllegalStateErrorException(
    override val code: String,
    val msg: String,
    val origine: Throwable? = null,
) : MonProjetSup5xxExceptions(code, msg, origine)
