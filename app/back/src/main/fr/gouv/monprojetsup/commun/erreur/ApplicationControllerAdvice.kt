package fr.gouv.monprojetsup.commun.erreur

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApplicationControllerAdvice {
    @ExceptionHandler(MonProjetSupInternalErrorException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleNotFound(domainError: MonProjetSupInternalErrorException): ProblemDetail {
        val reponse = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        reponse.title = domainError.code
        reponse.detail = domainError.msg
        return reponse
    }
}
