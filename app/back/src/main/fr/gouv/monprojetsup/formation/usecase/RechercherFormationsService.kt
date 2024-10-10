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
            val recherches: Map<FormationCourte, Double> =
                rechercheFormationRepository.rechercherUneFormation(mot).associate {
                    it.formation to calculScore(it, mot)
                }
            recherches.forEach { entry ->
                val formation = entry.key
                val score = entry.value
                resultats[formation]?.let { scoreActuel ->
                    val nouveauScore = scoreActuel + score
                    resultats.put(formation, nouveauScore)
                } ?: resultats.put(formation, score)
            }
        }
        return resultats.entries.sortedByDescending { it.value }.associate { it.toPair() }
    }

    private fun calculScore(
        resultat: ResultatRechercheFormationCourte,
        mot: String,
    ): Double {
        val score =
            resultat.scoreLabel?.let {
                when {
                    it.motExactPresent -> 200.0
                    it.motExactPresentDebutPhrase -> 150.0
                    it.motExactPresentFin -> 145.0
                    it.motExactMilieu -> 140.0
                    it.sequencePresenteMot -> 110.0
                    else -> it.pourcentageMot
                }
            } ?: resultat.scoreMotClef?.let {
                val coefficient =
                    when {
                        it.motExactPresent -> 0.85
                        it.motExactPresentDebutPhrase || it.motExactPresentFin || it.motExactMilieu -> 0.8
                        it.sequencePresenteMot -> 0.75
                        else -> 0.7
                    }
                coefficient * it.pourcentageMot
            } ?: 0.0
        val coefficientLongueurMotRecherche = 1.0 + (1.0 / (10.0 * mot.length.toDouble()))
        return score * coefficientLongueurMotRecherche
    }

    private fun filtrerMotsRecherches(
        recherche: String,
        tailleMinimumRecherche: Int,
    ): List<String> {
        val regexNonAlphaNumericAvecAccent = Regex(REGEX_NON_ALPHA_NUMERIC_AVEC_ACCENT)
        return recherche
            .split(regexNonAlphaNumericAvecAccent)
            .filter { it.length >= tailleMinimumRecherche }
            .distinct()
            .filterNot { MOTS_VIDES.contains(it) }
    }

    companion object {
        private val MOTS_VIDES =
            listOf(
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
