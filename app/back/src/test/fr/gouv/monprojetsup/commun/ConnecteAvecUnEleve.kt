package fr.gouv.monprojetsup.commun

import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.test.context.support.WithSecurityContext
import org.springframework.security.test.context.support.WithSecurityContextFactory
import java.time.Instant

@Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = ConnecteAvecUnEleveSecurityContextFactory::class)
annotation class ConnecteAvecUnEleve(val idEleve: String)

internal class ConnecteAvecUnEleveSecurityContextFactory : WithSecurityContextFactory<ConnecteAvecUnEleve> {
    override fun createSecurityContext(annotation: ConnecteAvecUnEleve): SecurityContext {
        return SecurityContextHolder.createEmptyContext().apply {
            authentication = buildJwtAuthenticationTokenEleve(annotation.idEleve)
        }
    }

    private fun buildJwtAuthenticationTokenEleve(idEleve: String) =
        JwtAuthenticationToken(
            Jwt
                .withTokenValue("eyblablabla")
                .header("alg", "RS256")
                .header("typ", "JWT")
                .claim("iat", Instant.now())
                .claim("sub", idEleve)
                .claim("profile", "APP-SEC")
                .claim("name", "Léo Dupont")
                .claim("preferred_username", "eleve")
                .claim("given_name", "Léo")
                .claim("family_name", "Dupont")
                .claim("email", "eleve@example.com")
                .build(),
        )
}
