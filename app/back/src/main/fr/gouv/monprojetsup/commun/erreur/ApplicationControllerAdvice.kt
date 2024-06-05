package fr.gouv.monprojetsup.commun.erreur

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetIllegalStateErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApplicationControllerAdvice {
    @ExceptionHandler(MonProjetSupInternalErrorException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleInternalError(domainError: MonProjetSupInternalErrorException): ProblemDetail {
        val reponse = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        reponse.title = domainError.code
        reponse.detail = domainError.msg
        return reponse
    }

    @ExceptionHandler(MonProjetIllegalStateErrorException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleIllegalState(domainError: MonProjetIllegalStateErrorException): ProblemDetail {
        val reponse = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        reponse.title = domainError.code
        reponse.detail = domainError.msg
        return reponse
    }

    @ExceptionHandler(MonProjetSupNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(domainError: MonProjetSupNotFoundException): ProblemDetail {
        val reponse = ProblemDetail.forStatus(HttpStatus.NOT_FOUND)
        reponse.title = domainError.code
        reponse.detail = domainError.msg
        return reponse
    }
}
