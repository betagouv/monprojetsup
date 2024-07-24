package fr.gouv.monprojetsup.suggestions.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@EnableCaching
@ComponentScan(basePackages = ["fr.gouv.monprojetsup.suggestions"])
class ApplicationSuggestions

/**
 * Version of the API used to implement the service,
 * used in the controllers to define the context of the endpoints
 */
const val API_VERSION = "1.2"

/**
 * Base path for the service
 */
const val BASE_PATH = "/api/$API_VERSION"

/**
 * Main function
 */
fun main(args: Array<String>) {
    WritePidToFile.write("sugg")
    runApplication<ApplicationSuggestions>(*args)
}
