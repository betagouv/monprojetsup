package fr.gouv.monprojetsup.data.etl

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource("classpath:application.properties")
@PropertySource("classpath:secret.properties")
class Config {


}