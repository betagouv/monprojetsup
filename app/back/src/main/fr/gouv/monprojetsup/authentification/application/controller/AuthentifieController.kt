package fr.gouv.monprojetsup.authentification.application.controller

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupForbiddenException
import fr.gouv.monprojetsup.commun.erreur.domain.eleveSansCompteException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

abstract class AuthentifieController {
    @Throws(MonProjetSupForbiddenException::class)
    protected fun recupererEleve(): ProfilEleve {
        val authentification = SecurityContextHolder.getContext().authentication
        return when (val utilisateur = authentification.principal) {
            is ProfilEleve -> utilisateur
            else -> throw MonProjetSupForbiddenException("UTILISATEUR_PAS_ELEVE", "L'utilisateur connecté n'est pas un élève")
        }
    }

    @Throws(MonProjetSupForbiddenException::class)
    protected fun recupererEleveAvecProfilExistant(): ProfilEleve.AvecProfilExistant {
        val authentification = SecurityContextHolder.getContext().authentication
        return when (val utilisateur = authentification.principal) {
            is ProfilEleve.AvecProfilExistant -> utilisateur
            is ProfilEleve.SansCompte -> throw eleveSansCompteException()
            else -> throw MonProjetSupForbiddenException("UTILISATEUR_PAS_ELEVE", "L'utilisateur connecté n'est pas un élève identifié")
        }
    }

    @Throws(MonProjetSupForbiddenException::class)
    protected fun estExpert(): Boolean {
        val authentification = SecurityContextHolder.getContext().authentication

        if (authentification is JwtAuthenticationToken) {
            val jwt = authentification.getToken()

            return jwt.hasClaim("EXPERT")
        }
        return false
    }
}
