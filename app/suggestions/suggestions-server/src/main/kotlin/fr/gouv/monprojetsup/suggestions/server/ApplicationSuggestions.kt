package fr.gouv.monprojetsup.suggestions.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.data.jpa.repository.config.EnableJpaRepositories


@Configuration
@PropertySource("classpath:application.properties")
@PropertySource("classpath:secret.properties")
class Config {

}

@SpringBootApplication
@EnableCaching
@ComponentScan(basePackages = ["fr.gouv.monprojetsup"])
@EntityScan(basePackages = [
    "fr.gouv.monprojetsup.data"]
)
@EnableJpaRepositories(basePackages = [
    "fr.gouv.monprojetsup.data.app.infrastructure",
    "fr.gouv.monprojetsup.data.suggestions.infrastructure"]
)
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
