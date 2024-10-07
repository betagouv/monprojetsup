package fr.gouv.monprojetsup.eleve.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupBadRequestException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.eleve.domain.port.CompteParcoursupRepository
import fr.gouv.monprojetsup.parcoursup.infrastructure.client.ParcoursupApiHttpClient
import org.springframework.stereotype.Service

@Service
class MiseAJourIdParcoursupService(
    private val compteParcoursupRepository: CompteParcoursupRepository,
    private val parcoursupApiHttpClient: ParcoursupApiHttpClient,
) {
    @Throws(MonProjetSupNotFoundException::class, MonProjetSupBadRequestException::class)
    fun mettreAJourIdParcoursup(
        profil: ProfilEleve.Identifie,
        jwt: String,
    ) {
        val idEleve = parcoursupApiHttpClient.recupererIdParcoursupEleve(jwt)
        compteParcoursupRepository.enregistrerIdCompteParcoursup(profil.id, idEleve)
    }
}
