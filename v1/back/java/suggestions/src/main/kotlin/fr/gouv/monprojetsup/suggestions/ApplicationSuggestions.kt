package fr.gouv.monprojetsup.suggestions

import fr.gouv.monprojetsup.common.server.MyServiceException
import fr.gouv.monprojetsup.suggestions.server.SuggestionServer
import fr.gouv.monprojetsup.common.server.WritePidToFile
import jakarta.servlet.http.HttpServletRequest
import jdk.jshell.spi.ExecutionControl
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


/**
 * Version of the API used to implement the service,
 * used in the controllers to define the context of the endpoints
 */
const val API_VERSION = "1.1"

/**
 * Base path for the service
 */
const val BASE_PATH = "/api/$API_VERSION"
@SpringBootApplication
class ApplicationSuggestions

/**
 * Main function
 */
fun main(args: Array<String>) {
	WritePidToFile.write("sugg")


	//runApplication<ApplicationSuggestions>(*args)

	val app = SpringApplication(ApplicationSuggestions::class.java)
	val context = app.run(*args)
	val webServer = context.getBean(SuggestionServer::class.java)

	webServer.init()

}

@ControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {

	@ExceptionHandler(Exception::class)
	protected fun handleException(
		e: Exception,
		httpServletRequest: HttpServletRequest
	): ResponseEntity<Any> {
		if(e is ExecutionControl.UserException) return ResponseEntity.badRequest().body(e.message)
		if(e is MyServiceException) return ResponseEntity.internalServerError().body(e.message)
		else return ResponseEntity.badRequest().body(e.message)
	}
}

