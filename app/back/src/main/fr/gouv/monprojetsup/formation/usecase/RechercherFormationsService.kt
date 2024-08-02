package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.formation.application.controller.FormationController.Companion.TAILLE_MINIMUM_RECHERCHE
import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import fr.gouv.monprojetsup.formation.domain.port.RechercheFormationRepository
import org.springframework.stereotype.Service
import kotlin.math.min

@Service
class RechercherFormationsService(
    private val rechercheFormationRepository: RechercheFormationRepository,
) {
    fun rechercheLesFormationsCorrespondantes(
        recherche: String,
        nombreMaximaleDeFormation: Int,
    ): List<FormationCourte> {
        val regexNonAlphaNumericAvecAccent = Regex("[^0-9A-Za-zÀ-ÖØ-öø-ÿ]")
        val motsRecherches = recherche.split(regexNonAlphaNumericAvecAccent).filter { it.length >= TAILLE_MINIMUM_RECHERCHE }.distinct()
        val formations = mutableListOf<FormationCourte>()
        motsRecherches.forEach { mot ->
            formations.addAll(rechercheFormationRepository.rechercherUneFormation(mot))
        }
        val selecteur = formations.groupingBy { it.id }.eachCount()
        formations.sortByDescending { selecteur[it.id] }
        val formationDisctinctes = formations.distinct()
        return formationDisctinctes.subList(0, min(nombreMaximaleDeFormation, formationDisctinctes.size))
    }
}
