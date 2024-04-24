package fr.gouv.monprojetsup.app

import fr.gouv.monprojetsup.app.server.MyService
import fr.gouv.monprojetsup.common.server.MyServiceException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest


@ControllerAdvice
class GlobalExceptionHandler {

    // Generic exception handler
    @ExceptionHandler(Exception::class)
    fun handleGlobalException(ex: Exception, request: WebRequest?): ResponseEntity<*> {
        return ResponseEntity<Any>(ex, HttpStatus.INTERNAL_SERVER_ERROR)
    }
    // MyService exception handler
    @ExceptionHandler(MyServiceException::class)
    fun handleRMyServiceException(ex: MyServiceException, request: WebRequest?): ResponseEntity<*> {
        val inner = ex.cause;
        val response = MyService.handleAnException(inner, ex.request, if (request != null) request.toString() else null);

        return ResponseEntity<Any>(response, HttpStatus.OK)
    }

}