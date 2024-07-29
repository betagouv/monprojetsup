package fr.gouv.monprojetsup.authentification.application.controller

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEnseignant
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilUtilisateur
import fr.gouv.monprojetsup.authentification.filter.IdentificationFilter.Companion.GRANTED_AUTHORITY_ELEVE
import fr.gouv.monprojetsup.authentification.filter.IdentificationFilter.Companion.GRANTED_AUTHORITY_ENSEIGNANT
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupForbiddenException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

abstract class AuthentifieController {
    @Throws(MonProjetSupForbiddenException::class)
    protected fun recupererEleve(): ProfilEleve {
        val authentification = SecurityContextHolder.getContext().authentication
        return deserialiseEleve(authentification)
    }

    @Throws(MonProjetSupForbiddenException::class)
    protected fun recupererUtilisateur(): ProfilUtilisateur {
        val authentification = SecurityContextHolder.getContext().authentication
        if (authentification.authorities.contains(GRANTED_AUTHORITY_ELEVE)) {
            return deserialiseEleve(authentification)
        } else if (authentification.authorities.contains(GRANTED_AUTHORITY_ENSEIGNANT)) {
            return authentification.principal as ProfilEnseignant
        }
        throw MonProjetSupForbiddenException("UTILISATEUR_NON_RECONNU", "L'utilisateur connecté n'est pas un élève ni un enseigannt")
    }

    @Throws(MonProjetSupForbiddenException::class)
    private fun deserialiseEleve(authentification: Authentication): ProfilEleve {
        try {
            return authentification.principal as ProfilEleve
        } catch (e: ClassCastException) {
            throw MonProjetSupForbiddenException("UTILISATEUR_PAS_ELEVE", "L'utilisateur connecté n'est pas un élève")
        }
    }
}
