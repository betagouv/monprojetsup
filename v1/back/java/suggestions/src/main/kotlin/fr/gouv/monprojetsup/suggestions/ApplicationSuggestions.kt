package fr.gouv.monprojetsup.suggestions

import fr.gouv.monprojetsup.common.server.MyServiceException
import fr.gouv.monprojetsup.suggestions.server.SuggestionServer
import fr.gouv.monprojetsup.common.server.WritePidToFile
import jakarta.servlet.http.HttpServletRequest
import jdk.jshell.spi.ExecutionControl
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.badRequest
import org.springframework.http.ResponseEntity.internalServerError
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
