package fr.gouv.monprojetsup.app


import fr.gouv.monprojetsup.app.server.MyService
import fr.gouv.monprojetsup.app.server.WebServer
import fr.gouv.monprojetsup.common.server.MyServiceException
import fr.gouv.monprojetsup.common.server.WritePidToFile
import jakarta.servlet.http.HttpServletRequest
import jdk.jshell.spi.ExecutionControl.UserException
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@SpringBootApplication
@EnableMongoRepositories
class ApplicationMps

/**
 * Version of the API used to implement the service,
 * used in the controllers to define the context of the endpoints
 */
const val API_VERSION = "1.1"

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

@ControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {


    //this is thrown by MyService.handleRequestAndExceptions
    @ExceptionHandler(MyServiceException::class)
    protected fun handleMyServiceException(
        e: MyServiceException,
        httpServletRequest: HttpServletRequest
    ): ResponseEntity<Any> {
        return ResponseEntity.internalServerError().body(e.message)
    }

}





