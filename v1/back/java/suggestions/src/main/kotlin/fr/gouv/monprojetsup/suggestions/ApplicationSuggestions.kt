package fr.gouv.monprojetsup.suggestions

import fr.gouv.monprojetsup.suggestions.server.SuggestionServer
import fr.gouv.monprojetsup.suggestions.server.WritePidToFile
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component


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

	runApplication<ApplicationSuggestions>(*args)

}

@Component
@ConditionalOnProperty(name = ["suggestions.runner.enabled"], havingValue = "true", matchIfMissing = true)
class SuggestionApplicationRunner : ApplicationRunner {

	@Autowired
	lateinit var webServer : SuggestionServer

	override fun run(args: org.springframework.boot.ApplicationArguments) {
		webServer.init()
	}

}
