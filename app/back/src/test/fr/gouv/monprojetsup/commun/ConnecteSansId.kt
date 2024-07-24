package fr.gouv.monprojetsup.commun

import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.test.context.support.WithSecurityContext
import org.springframework.security.test.context.support.WithSecurityContextFactory
import java.time.Instant

@Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = ConnecteSansIdSecurityContextFactory::class)
annotation class ConnecteSansId

internal class ConnecteSansIdSecurityContextFactory : WithSecurityContextFactory<ConnecteSansId> {
    override fun createSecurityContext(annotation: ConnecteSansId): SecurityContext {
        return SecurityContextHolder.createEmptyContext().apply {
            authentication = buildJwtAuthenticationToken()
        }
    }

    private fun buildJwtAuthenticationToken() =
        JwtAuthenticationToken(
            Jwt
                .withTokenValue("eyblablabla")
                .header("alg", "RS256")
                .header("typ", "JWT")
                .claim("iat", Instant.now())
                .build(),
        )
}
