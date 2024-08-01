package fr.gouv.monprojetsup.authentification.filter

import fr.gouv.monprojetsup.authentification.domain.entity.Profil
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilConnecte
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEnseignant
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
        const val AUTHORITY_ENSEIGNANT = "ENSEIGNANT_AUTHENTIFIE"
        val GRANTED_AUTHORITY_ELEVE = GrantedAuthority { AUTHORITY_ELEVE }
        val GRANTED_AUTHORITY_ENSEIGNANT = GrantedAuthority { AUTHORITY_ENSEIGNANT }
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
                val eleve = eleveRepository.recupererUnEleve(idIndividu)
                val authenticationEleve = UsernamePasswordAuthenticationToken(eleve, null, mutableListOf(GRANTED_AUTHORITY_ELEVE))
                SecurityContextHolder.getContext().authentication = authenticationEleve
            } else if (profil == Profil.ENSEIGNANT && idIndividu != null) {
                val authenticationEnseignant =
                    UsernamePasswordAuthenticationToken(ProfilEnseignant, null, mutableListOf(GRANTED_AUTHORITY_ENSEIGNANT))
                SecurityContextHolder.getContext().authentication = authenticationEnseignant
            } else {
                val authenticationToken = UsernamePasswordAuthenticationToken(ProfilConnecte, null, null)
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

    private fun getIdIndividu(token: Jwt): String? = token.getClaim("sub")

    private fun getProfile(token: Jwt) = Profil.deserialise(token.getClaim("profile"))
}
