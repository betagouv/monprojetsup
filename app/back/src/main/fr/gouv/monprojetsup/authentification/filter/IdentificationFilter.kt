package fr.gouv.monprojetsup.authentification.filter

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilConnnecte
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.authentification.usecase.RecupererEleveService
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupForbiddenException
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
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
    val recupererEleveService: RecupererEleveService,
    val mpsLogger: MonProjetSupLogger,
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
            mpsLogger.info("JWT_CLAIMS", getAllClaims(jwtToken))
            val idIndividu = getIdIndividu(jwtToken)
            if (idIndividu != null) {
                val portfolioId = getM7Id(jwtToken)
                val estExpert = estExpert(jwtToken)
                mpsLogger.info("IS_EXPERT", if (estExpert) "expert" else "non expert")
                val eleve = recupererEleveService.recupererEleve(idIndividu)
                if (eleve is ProfilEleve.AvecProfilExistant) {
                    eleve.portfolioId = portfolioId
                    eleve.estExpert = estExpert
                }
                val authenticationEleve =
                    UsernamePasswordAuthenticationToken(eleve, null, mutableListOf(GRANTED_AUTHORITY_UTILISATEUR))
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

    private fun getM7Id(token: Jwt): String? = token.getClaim<String?>("m7_id")

    protected fun estExpert(token: Jwt): Boolean {
        return token.hasClaim("profile") && token.getClaim<String>("profile") == "expert"
    }

    @Throws(MonProjetSupForbiddenException::class)
    protected fun getAllClaims(token: Jwt): String {
        return token.claims.map { it.toString() }.joinToString(" | ")
    }
}
