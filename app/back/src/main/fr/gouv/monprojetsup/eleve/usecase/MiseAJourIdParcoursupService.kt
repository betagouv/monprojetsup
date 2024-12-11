package fr.gouv.monprojetsup.eleve.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupBadRequestException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.eleve.domain.entity.ParametresPourRecupererToken
import fr.gouv.monprojetsup.eleve.domain.port.CompteParcoursupRepository
import fr.gouv.monprojetsup.parcoursup.domain.port.ParcoursupAuthentClient
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MiseAJourIdParcoursupService(
    private val compteParcoursupRepository: CompteParcoursupRepository,
    private val parcoursupAuthentHttpClient: ParcoursupAuthentClient,
) {
    @Transactional(readOnly = false)
    @Throws(MonProjetSupNotFoundException::class, MonProjetSupBadRequestException::class)
    fun mettreAJourIdParcoursup(
        profil: ProfilEleve.AvecProfilExistant,
        parametresPourRecupererToken: ParametresPourRecupererToken,
    ) {
        val idEleve = parcoursupAuthentHttpClient.recupererIdParcoursupEleve(parametresPourRecupererToken)
        compteParcoursupRepository.enregistrerIdCompteParcoursup(profil.id, idEleve)
    }
}
