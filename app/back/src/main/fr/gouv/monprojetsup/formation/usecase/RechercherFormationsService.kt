package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.commun.utilitaires.retirerAccents
import fr.gouv.monprojetsup.formation.application.controller.FormationController.Companion.TAILLE_MINIMUM_RECHERCHE
import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import fr.gouv.monprojetsup.formation.domain.port.RechercheFormationRepository
import org.springframework.stereotype.Service

@Service
class RechercherFormationsService(
    private val rechercheFormationRepository: RechercheFormationRepository,
) {
    fun rechercheLesFormationsCorrespondantes(recherche: String): List<FormationCourte> {
        val motsRecherches =
            recherche.retirerAccents().lowercase().split(regex = Regex("[^0-9a-zA-Z]")).filter {
                it.length >= TAILLE_MINIMUM_RECHERCHE
            }.distinct()
        val formations = mutableListOf<FormationCourte>()
        motsRecherches.forEach { mot ->
            formations.addAll(rechercheFormationRepository.rechercherUneFormation(mot))
        }
        return formations.distinct()
    }
}