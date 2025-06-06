package fr.gouv.monprojetsup.commun.erreur

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupBadRequestException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupForbiddenException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupIllegalStateErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupServiceUnavailableException
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApplicationControllerAdvice(
    val logguer: MonProjetSupLogger,
) {
    @ExceptionHandler(MonProjetSupInternalErrorException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleInternalError(domainError: MonProjetSupInternalErrorException): ProblemDetail {
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        logguer.logException(domainError, status.value())
        val reponse = ProblemDetail.forStatus(status)
        reponse.title = domainError.code
        reponse.detail = domainError.msg
        return reponse
    }

    @ExceptionHandler(MonProjetSupIllegalStateErrorException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleIllegalState(domainError: MonProjetSupIllegalStateErrorException): ProblemDetail {
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        logguer.logException(domainError, status.value())
        val reponse = ProblemDetail.forStatus(status)
        reponse.title = domainError.code
        reponse.detail = domainError.msg
        return reponse
    }

    @ExceptionHandler(MonProjetSupServiceUnavailableException::class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    fun handleServiceUnavailable(domainError: MonProjetSupServiceUnavailableException): ProblemDetail {
        val status = HttpStatus.SERVICE_UNAVAILABLE
        logguer.logException(domainError, status.value())
        val reponse = ProblemDetail.forStatus(status)
        reponse.title = domainError.code
        reponse.detail = domainError.msg
        return reponse
    }

    @ExceptionHandler(MonProjetSupBadRequestException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRequest(domainError: MonProjetSupBadRequestException): ProblemDetail {
        val status = HttpStatus.BAD_REQUEST
        logguer.logException(domainError, status.value())
        val reponse = ProblemDetail.forStatus(status)
        reponse.title = domainError.code
        reponse.detail = domainError.msg
        return reponse
    }

    @ExceptionHandler(MonProjetSupNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(domainError: MonProjetSupNotFoundException): ProblemDetail {
        val status = HttpStatus.NOT_FOUND
        logguer.logException(domainError, status.value())
        val reponse = ProblemDetail.forStatus(status)
        reponse.title = domainError.code
        reponse.detail = domainError.msg
        return reponse
    }

    @ExceptionHandler(MonProjetSupForbiddenException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleForbidden(domainError: MonProjetSupForbiddenException): ProblemDetail {
        val status = HttpStatus.FORBIDDEN
        logguer.logException(domainError, status.value())
        val reponse = ProblemDetail.forStatus(status)
        reponse.title = domainError.code
        reponse.detail = domainError.msg
        return reponse
    }
}
