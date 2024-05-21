package fr.gouv.monprojetsup

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@OpenAPIDefinition(servers = [Server(url = "/")])
@SpringBootApplication
class MonProjetSupApplication

fun main(args: Array<String>) {
    runApplication<MonProjetSupApplication>(*args)
}
