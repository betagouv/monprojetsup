package fr.gouv.monprojetsup.configuration

import fr.gouv.monprojetsup.parcoursup.infrastructure.client.ParcoursupJwtDecoder
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder

@Configuration
class HttpClientConfiguration {
    @Bean
    fun okHttpClient() = OkHttpClient.Builder().build()

    @Bean
    fun parcoursupJwtDecoder(
        @Value("\${parcoursup.authent.url}")
        urlAuthentification: String,
    ): ParcoursupJwtDecoder {
        val decoder = NimbusJwtDecoder.withJwkSetUri(urlAuthentification + URL_JWKS).build()
        return ParcoursupJwtDecoder(decoder)
    }

    companion object {
        private const val URL_JWKS = "/oauth2/jwks"
    }
}
