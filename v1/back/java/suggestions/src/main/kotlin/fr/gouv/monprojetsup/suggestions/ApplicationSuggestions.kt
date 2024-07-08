package fr.gouv.monprojetsup.suggestions

import fr.gouv.monprojetsup.suggestions.server.SuggestionServer
import fr.gouv.monprojetsup.suggestions.server.WritePidToFile
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication


/**
 * Version of the API used to implement the service,
 * used in the controllers to define the context of the endpoints
 */
const val API_VERSION = "1.2"

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
