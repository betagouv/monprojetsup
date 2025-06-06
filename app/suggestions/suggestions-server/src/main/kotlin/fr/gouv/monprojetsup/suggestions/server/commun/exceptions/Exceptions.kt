package fr.gouv.monprojetsup.suggestions.server.commun.exceptions

abstract class MPSExceptions(
    open val code: String,
    override val message: String,
    cause: Throwable? = null,
) : Exception(message, cause)

abstract class MPS4XxExceptions(
    override val code: String,
    message: String,
    cause: Throwable?,
) : MPSExceptions(code, message, cause)

abstract class MPS5XxExceptions(
    override val code: String,
    message: String,
    cause: Throwable?,
) : MPSExceptions(code, message, cause)

data class MPSBadRequestException(
    override val code: String,
    val msg: String,
    val origine: Throwable? = null,
) : MPS4XxExceptions(code, msg, origine)

data class MPSNotFoundException(
    override val code: String,
    val msg: String,
    val origine: Throwable? = null,
) : MPS4XxExceptions(code, msg, origine)

data class MPSIllegalStateErrorException(
    override val code: String,
    val msg: String,
    val origine: Throwable? = null,
) : MPS5XxExceptions(code, msg, origine)

data class MPSInternalErrorException(
    override val code: String,
    val msg: String,
    val origine: Throwable? = null,
) : MPS5XxExceptions(code, msg, origine)

