package fr.gouv.monprojetsup.suggestions.server.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class SwaggerConfig {

    @Value("\${swaggerUrl}")
    private val swaggerUrl = "http://localhost:8004"

    @Bean
    fun config(): OpenAPI {
        return OpenAPI()
            .addServersItem(serverInfo())
    }

    private fun serverInfo(): Server {
        return Server()
            .url(swaggerUrl)
    }
}