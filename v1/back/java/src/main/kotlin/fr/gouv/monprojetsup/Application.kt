package fr.gouv.monprojetsup

import fr.gouv.monprojetsup.tools.server.MyService
import fr.gouv.monprojetsup.tools.server.MyServiceException
import fr.gouv.monprojetsup.web.server.WebServer
import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths


@SpringBootApplication
@EnableMongoRepositories
class Application

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

    writePidtoFile()
    val app = SpringApplication(Application::class.java)
    val context = app.run(*args)
    val webServer = context.getBean(WebServer::class.java)

    val loadOnlyDB = false;
    webServer.init(loadOnlyDB)

}

fun writePidtoFile() {
    // Get the current process handle
    val currentProcess = ProcessHandle.current()

    // Get the PID of the current process
    val pid = currentProcess.pid()

    // Define the path to the file where the PID should be written
    val filePath = "mps.pid"

    try {
        // Write the PID to the file
        Files.writeString(Paths.get(filePath), pid.toString())
        println("Successfully wrote PID to file: $filePath")
    } catch (e: IOException) {
        System.err.println("An error occurred while writing the PID to file: ")
    }

}

@ControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {

    //this is thrown by MyService.handleRequestAndExceptions
    @ExceptionHandler(MyServiceException::class)
    protected fun handleMyServiceException(
        e: MyServiceException,
        httpServletRequest: HttpServletRequest
    ): ResponseEntity<Any> {
        val response = MyService.handleException(e.cause, e.request, httpServletRequest.requestURI)
        return ResponseEntity.ok().body(response)
    }

}





