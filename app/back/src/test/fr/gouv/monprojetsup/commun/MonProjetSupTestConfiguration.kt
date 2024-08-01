package fr.gouv.monprojetsup.commun

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.test.context.support.WithSecurityContextFactory

@TestConfiguration
class MonProjetSupTestConfiguration {
    @Bean
    fun withSecurityContextFactory(): WithSecurityContextFactory<ConnecteAvecUnEleve> {
        return ConnecteAvecUnEleveSecurityContextFactory()
    }
}
