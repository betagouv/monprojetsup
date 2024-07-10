package fr.gouv.monprojetsup.app


import fr.gouv.monprojetsup.app.server.WebServer
import fr.gouv.monprojetsup.app.server.WritePidToFile
import fr.gouv.monprojetsup.etl.EtlApplicationRunner
import fr.gouv.monprojetsup.etl.FormationsDb
import fr.gouv.monprojetsup.suggestions.SuggestionApplicationRunner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Primary
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.stereotype.Component


@EnableMongoRepositories
@SpringBootApplication
@ComponentScan(
    excludeFilters = [
        ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = [EtlApplicationRunner::class, SuggestionApplicationRunner::class]
        )
    ]
)
class ApplicationMps

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
class ApplicationMpsV1

fun main(args: Array<String>) {
    runApplication<ApplicationMpsV1>(*args)
}

@Component
@Primary
class AppMyApplicationRunner : ApplicationRunner {

    @Autowired
    lateinit var webServer : WebServer

    override fun run(args: ApplicationArguments) {

        WritePidToFile.write("mps")

        webServer.init()

    }
}





