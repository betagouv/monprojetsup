package fr.gouv.monprojetsup.authentification.filter

import fr.gouv.monprojetsup.authentification.domain.Profil
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.eleve.domain.port.EleveRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class IdentificationFilter(
    val eleveRepository: EleveRepository,
) : OncePerRequestFilter() {
    companion object {
        const val AUTHORITY_ELEVE = "ELEVE_AUTHENTIFIE"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val jwtToken = getToken()
        if (jwtToken != null) {
            val profil = getProfile(jwtToken)
            val idIndividu = getIdIndividu(jwtToken)
            if (profil == Profil.ELEVE && idIndividu != null) {
                val eleve =
                    try {
                        eleveRepository.recupererUnEleve(idIndividu)
                    } catch (e: MonProjetSupNotFoundException) {
                        eleveRepository.creerUnEleve(idIndividu)
                    }
                val authentication = UsernamePasswordAuthenticationToken(eleve, null, mutableListOf(GrantedAuthority { AUTHORITY_ELEVE }))
                SecurityContextHolder.getContext().authentication = authentication
            }
        }
        filterChain.doFilter(request, response)
    }

    private fun getToken(): Jwt? {
        return try {
            val jwtAuthentication = SecurityContextHolder.getContext().authentication as JwtAuthenticationToken
            jwtAuthentication.token
        } catch (e: Exception) {
            null
        }
    }

    private fun getIdIndividu(token: Jwt): String? = token.getClaim("sid")

    private fun getProfile(token: Jwt) = Profil.deserialise(token.getClaim("profile"))
}
