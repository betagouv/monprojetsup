package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.commun.recherche.usecase.RechercherService
import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import fr.gouv.monprojetsup.formation.domain.port.RechercheFormationRepository
import org.springframework.stereotype.Service

@Service
class RechercherFormationsService(
    private val rechercheFormationRepository: RechercheFormationRepository,
) : RechercherService<FormationCourte>() {
    fun rechercheLesFormationsCorrespondantes(
        recherche: String,
        tailleMinimumRecherche: Int,
    ): List<FormationCourte> {
        return rechercher(
            recherche = recherche,
            tailleMinimumRecherche = tailleMinimumRecherche,
        )
    }

    override fun rechercherPourUnMot(mot: String): List<FormationCourte> {
        return rechercheFormationRepository.rechercherUneFormation(mot)
    }
}
