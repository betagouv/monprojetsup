package fr.gouv.monprojetsup.app


import fr.gouv.monprojetsup.app.server.WebServer
import fr.gouv.monprojetsup.app.server.WritePidToFile
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories


@SpringBootApplication
@EnableMongoRepositories
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

/**
 * Main function
 */
fun main(args: Array<String>) {

    WritePidToFile.write("mps")

    val app = SpringApplication(ApplicationMps::class.java)
    val context = app.run(*args)
    val webServer = context.getBean(WebServer::class.java)

    val loadOnlyDB = false
    webServer.init(loadOnlyDB)

}






