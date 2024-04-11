package fr.gouv.monprojetsup.suggestions

import fr.gouv.monprojetsup.data.services.GetSimpleStatsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ServiceConfig {

    //for some reason this is not automatically imported when scanning the etl
    @Bean
    fun getSimpleStatsService(): GetSimpleStatsService {
        return GetSimpleStatsService()
    }

}