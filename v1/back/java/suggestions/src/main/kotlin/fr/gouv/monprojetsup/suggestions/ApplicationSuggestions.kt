package fr.gouv.monprojetsup.suggestions

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


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

fun main(args: Array<String>) {
	runApplication<ApplicationSuggestions>(*args)
}
