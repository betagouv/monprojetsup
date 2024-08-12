package fr.gouv.monprojetsup.metier.usecase

import fr.gouv.monprojetsup.commun.recherche.usecase.RechercherService
import fr.gouv.monprojetsup.metier.domain.entity.MetierCourt
import fr.gouv.monprojetsup.metier.domain.port.RechercheMetierRepository
import org.springframework.stereotype.Service

@Service
class RechercherMetiersService(
    private val rechercheMetierRepository: RechercheMetierRepository,
) : RechercherService<MetierCourt>() {
    fun rechercherMetiers(
        recherche: String,
        nombreMaximaleDeMetier: Int,
        tailleMinimumRecherche: Int,
    ): List<MetierCourt> {
        return rechercher(
            recherche = recherche,
            nombreMaximaleDeResultats = nombreMaximaleDeMetier,
            tailleMinimumRecherche = tailleMinimumRecherche,
        )
    }

    override fun rechercherPourUnMot(mot: String): List<MetierCourt> {
        return rechercheMetierRepository.rechercherMetiersCourts(mot)
    }
}
