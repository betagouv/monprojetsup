package fr.gouv.monprojetsup.commun

import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.test.context.support.WithSecurityContext
import org.springframework.security.test.context.support.WithSecurityContextFactory
import java.time.Instant

@Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = ConnecteAvecUnEnseignantSecurityContextFactory::class)
annotation class ConnecteAvecUnEnseignant(val idEnseignant: String)

internal class ConnecteAvecUnEnseignantSecurityContextFactory : WithSecurityContextFactory<ConnecteAvecUnEnseignant> {
    override fun createSecurityContext(annotation: ConnecteAvecUnEnseignant): SecurityContext {
        return SecurityContextHolder.createEmptyContext().apply {
            authentication = buildJwtAuthenticationTokenEnseignant(annotation.idEnseignant)
        }
    }

    private fun buildJwtAuthenticationTokenEnseignant(idEnseignant: String) =
        JwtAuthenticationToken(
            Jwt
                .withTokenValue("eyblablabla")
                .header("alg", "RS256")
                .header("typ", "JWT")
                .claim("iat", Instant.now())
                .claim("sub", idEnseignant)
                .claim("profile", "APP-EDU")
                .claim("name", "Léa Dupond")
                .claim("preferred_username", "enseignant")
                .claim("given_name", "Léa")
                .claim("family_name", "Dupond")
                .claim("email", "enseignant@example.com")
                .build(),
        )
}
