package fr.gouv.monprojetsup.commun.erreur.domain

abstract class MonProjetSupExceptions(
    open val code: String,
    override val message: String,
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

data class MonProjetSupBadRequestException(
    override val code: String,
    val msg: String,
    val origine: Throwable? = null,
) : MonProjetSup4xxExceptions(code, msg, origine)

data class MonProjetSupNotFoundException(
    override val code: String,
    val msg: String,
    val origine: Throwable? = null,
) : MonProjetSup4xxExceptions(code, msg, origine)

fun eleveSansCompteException() =
    MonProjetSupNotFoundException(
        code = "ELEVE_SANS_COMPTE",
        msg = "L'élève connecté n'a pas encore crée son compte",
    )

open class MonProjetSupForbiddenException(
    override val code: String,
    val msg: String,
) : MonProjetSup4xxExceptions(code, msg, null)

data class MonProjetSupInternalErrorException(
    override val code: String,
    val msg: String,
    val origine: Throwable? = null,
) : MonProjetSup5xxExceptions(code, msg, origine)

data class MonProjetSupIllegalStateErrorException(
    override val code: String,
    val msg: String,
    val origine: Throwable? = null,
) : MonProjetSup5xxExceptions(code, msg, origine)

data class MonProjetSupServiceUnavailableException(
    override val code: String,
    val msg: String,
    val origine: Throwable? = null,
) : MonProjetSup5xxExceptions(code, msg, origine)
