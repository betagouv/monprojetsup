package fr.gouv.monprojetsup.suggestions

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
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