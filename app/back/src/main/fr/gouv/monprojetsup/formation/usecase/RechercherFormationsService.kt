package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.commun.Constantes.REGEX_NON_ALPHA_NUMERIC_AVEC_ACCENT
import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import fr.gouv.monprojetsup.formation.domain.entity.ResultatRechercheFormationCourte
import fr.gouv.monprojetsup.formation.domain.port.RechercheFormationRepository
import org.springframework.stereotype.Service

@Service
class RechercherFormationsService(
    private val rechercheFormationRepository: RechercheFormationRepository,
) {
    fun rechercheLesFormationsCorrespondantes(
        recherche: String,
        tailleMinimumRecherche: Int,
    ): Map<FormationCourte, Double> {
        val motsRecherches = filtrerMotsRecherches(recherche, tailleMinimumRecherche)
        val resultats = mutableMapOf<FormationCourte, Double>()
        motsRecherches.forEach { mot ->
            val recherches: Map<FormationCourte, Double> = rechercheFormationRepository.rechercherUneFormation(mot).map {
                it.formation to calculScore(it)
            }.toMap()
            recherches.forEach {
                resultats[it.key] = resultats.getOrPut(it.key) { 0.0 } + it.value
            }
            resultats.putAll(recherches)
        }
        return resultats.entries.sortedBy { it.value }.associate { it.toPair() }
    }

    private fun calculScore(resultat: ResultatRechercheFormationCourte): Double {
        return resultat.scoreLabel?.let {
            val coefficient = when {
                it.motExactPresent -> 1.5
                it.motExactPresentDebutPhrase -> 1.3
                it.motExactPresentFin -> 1.25
                it.motExactMilieu -> 1.2
                it.sequencePresenteMot -> 1.1
                else -> 1.0
            }
            coefficient * it.pourcentageMot
        } ?: resultat.scoreMotClef?.let {
            val coefficient = when {
                it.motExactPresent -> 0.75
                it.motExactPresentDebutPhrase || it.motExactPresentFin || it.motExactMilieu -> 0.7
                it.sequencePresenteMot -> 0.65
                else -> 0.6
            }
            coefficient * it.pourcentageMot
        } ?: 0.0
    }

    private fun filtrerMotsRecherches(recherche: String, tailleMinimumRecherche: Int): List<String> {
        val regexNonAlphaNumericAvecAccent = Regex(REGEX_NON_ALPHA_NUMERIC_AVEC_ACCENT)
        return recherche
            .split(regexNonAlphaNumericAvecAccent)
            .filter { it.length >= tailleMinimumRecherche }
            .distinct()
            .filterNot { MOTS_VIDES.contains(it) }
    }

    companion object {
        private val MOTS_VIDES = listOf(
            "le",
            "la",
            "les",
            "aux",
            "un",
            "une",
            "des",
            "du",
            "des",
            "de",
            "en",
            "sur",
            "sous",
            "dans",
            "chez",
            "par",
            "pour",
            "avec",
            "sans",
            "contre",
            "entre",
            "parmi",
            "vers",
            "derrière",
            "devant",
            "après",
            "avant",
            "autour",
            "et",
            "ou",
            "mais",
            "donc",
            "ni",
            "car",
            "que",
            "quand",
            "comme",
            "puisque",
            "quoique",
            "mon",
            "ma",
            "mes",
            "ton",
            "ta",
            "tes",
            "son",
            "sa",
            "ses",
            "notre",
            "nos",
            "votre",
            "vos",
            "leur",
            "leurs",
            "ce",
            "cet",
            "cette",
            "ces",
            "qui",
            "que",
            "quoi",
            "dont",
            "lequel",
            "laquelle",
            "lesquels",
            "lesquelles",
        )
    }


}
