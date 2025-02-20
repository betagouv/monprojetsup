package fr.gouv.monprojetsup.authentification.application.controller

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilUtilisateur
import fr.gouv.monprojetsup.authentification.filter.IdentificationFilter.Companion.GRANTED_AUTHORITY_UTILISATEUR
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupForbiddenException
import org.springframework.security.core.context.SecurityContextHolder

abstract class AuthentifieOuPasController {
    @Throws(MonProjetSupForbiddenException::class)
    protected fun recupererEleve(): ProfilEleve? {
        val authentification = SecurityContextHolder.getContext().authentication
        return when (val utilisateur = authentification.principal) {
            is ProfilEleve -> utilisateur
            else -> null
        }
    }

    @Throws(MonProjetSupForbiddenException::class)
    protected fun recupererEleveAvecProfilExistant(): ProfilEleve.AvecProfilExistant? {
        val authentification = SecurityContextHolder.getContext().authentication
        return when (val utilisateur = authentification.principal) {
            is ProfilEleve.AvecProfilExistant -> utilisateur
            else -> null
        }
    }

    @Throws(MonProjetSupForbiddenException::class)
    protected fun recupererUtilisateur(): ProfilUtilisateur? {
        val authentification = SecurityContextHolder.getContext().authentication
        return when {
            authentification.authorities.contains(GRANTED_AUTHORITY_UTILISATEUR) -> authentification.principal as ProfilEleve
            else -> null
        }
    }
}
