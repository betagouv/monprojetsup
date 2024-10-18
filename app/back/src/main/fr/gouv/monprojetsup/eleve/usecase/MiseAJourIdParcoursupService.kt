package fr.gouv.monprojetsup.eleve.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupBadRequestException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.eleve.domain.entity.ParametresPourRecupererToken
import fr.gouv.monprojetsup.eleve.domain.port.CompteParcoursupRepository
import fr.gouv.monprojetsup.parcoursup.domain.port.IParcoursupAuthentHttpClient
import org.springframework.stereotype.Service

@Service
class MiseAJourIdParcoursupService(
    private val compteParcoursupRepository: CompteParcoursupRepository,
    private val parcoursupAuthentHttpClient: IParcoursupAuthentHttpClient,
) {
    @Throws(MonProjetSupNotFoundException::class, MonProjetSupBadRequestException::class)
    fun mettreAJourIdParcoursup(
        profil: ProfilEleve.Identifie,
        parametresPourRecupererToken: ParametresPourRecupererToken,
    ) {
        val idEleve = parcoursupAuthentHttpClient.recupererIdParcoursupEleve(parametresPourRecupererToken)
        compteParcoursupRepository.enregistrerIdCompteParcoursup(profil.id, idEleve)
    }
}
