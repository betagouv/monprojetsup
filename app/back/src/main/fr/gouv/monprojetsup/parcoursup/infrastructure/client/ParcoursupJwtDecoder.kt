package fr.gouv.monprojetsup.parcoursup.infrastructure.client

import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtException

class ParcoursupJwtDecoder(private val jwtDecoder: JwtDecoder) {
    @Throws(JwtException::class)
    fun decode(token: String?): Jwt {
        return jwtDecoder.decode(token)
    }
}
