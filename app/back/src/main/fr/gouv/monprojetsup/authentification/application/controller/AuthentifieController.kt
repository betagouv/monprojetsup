package fr.gouv.monprojetsup.authentification.application.controller

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilConnecte
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEnseignant
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilUtilisateur
import fr.gouv.monprojetsup.authentification.filter.IdentificationFilter.Companion.GRANTED_AUTHORITY_ELEVE
import fr.gouv.monprojetsup.authentification.filter.IdentificationFilter.Companion.GRANTED_AUTHORITY_ENSEIGNANT
import fr.gouv.monprojetsup.commun.erreur.domain.EleveSansCompteException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupForbiddenException
import org.springframework.security.core.context.SecurityContextHolder

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
    protected fun recupererEleveIdentifie(): ProfilEleve.Identifie {
        val authentification = SecurityContextHolder.getContext().authentication
        return when (val utilisateur = authentification.principal) {
            is ProfilEleve.Identifie -> utilisateur
            is ProfilEleve.SansCompte -> throw EleveSansCompteException()
            else -> throw MonProjetSupForbiddenException("UTILISATEUR_PAS_ELEVE", "L'utilisateur connecté n'est pas un élève identifié")
        }
    }

    @Throws(MonProjetSupForbiddenException::class)
    protected fun recupererUtilisateur(): ProfilUtilisateur {
        val authentification = SecurityContextHolder.getContext().authentication
        return when {
            authentification.authorities.contains(GRANTED_AUTHORITY_ELEVE) -> authentification.principal as ProfilEleve
            authentification.authorities.contains(GRANTED_AUTHORITY_ENSEIGNANT) -> authentification.principal as ProfilEnseignant
            else -> authentification.principal as ProfilConnecte
        }
    }
}
