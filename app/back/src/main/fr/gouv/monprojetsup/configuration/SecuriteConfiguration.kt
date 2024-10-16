package fr.gouv.monprojetsup.configuration

import fr.gouv.monprojetsup.authentification.filter.IdentificationFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecuriteConfiguration {
    @Autowired
    lateinit var identificationFilter: IdentificationFilter

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            authorizeHttpRequests {
                authorize("/v3/api-docs/**", permitAll)
                authorize("/swagger-resources/*", permitAll)
                authorize("/swagger-ui/**", permitAll)
                authorize("/actuator/**", permitAll)
                authorize("/api/v1/formations/**", authenticated)
                authorize("/api/v1/metiers/**", authenticated)
                authorize("/api/v1/profil/**", authenticated)
                authorize("/api/v1/referentiel/**", authenticated)
                authorize(anyRequest, authenticated)
            }
            oauth2ResourceServer { jwt {} }
            csrf { disable() }
            addFilterAfter<BearerTokenAuthenticationFilter>(identificationFilter)
        }
        return http.getOrBuild()
    }
}
