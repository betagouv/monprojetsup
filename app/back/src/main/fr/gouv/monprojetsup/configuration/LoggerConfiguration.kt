package fr.gouv.monprojetsup.configuration

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LoggerConfiguration {
    @Bean
    fun logger(): Logger = LoggerFactory.getLogger("default")
}
