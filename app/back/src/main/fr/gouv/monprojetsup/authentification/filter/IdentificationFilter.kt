package fr.gouv.monprojetsup.authentification.filter

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilConnnecte
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
        private const val AUTHORITY_UTILISATEUR = "UTILISATEUR_AUTHENTIFIE"
        val GRANTED_AUTHORITY_UTILISATEUR = GrantedAuthority { AUTHORITY_UTILISATEUR }
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val jwtToken = getToken()
        jwtToken?.let {
            val idIndividu = getIdIndividu(jwtToken)
            if (idIndividu != null) {
                val eleve = eleveRepository.recupererUnEleve(idIndividu)
                val authenticationEleve = UsernamePasswordAuthenticationToken(eleve, null, mutableListOf(GRANTED_AUTHORITY_UTILISATEUR))
                SecurityContextHolder.getContext().authentication = authenticationEleve
            } else {
                val authenticationToken = UsernamePasswordAuthenticationToken(ProfilConnnecte, null, null)
                SecurityContextHolder.getContext().authentication = authenticationToken
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

    private fun getIdIndividu(token: Jwt): String? = token.getClaim<String>("sub")
}
