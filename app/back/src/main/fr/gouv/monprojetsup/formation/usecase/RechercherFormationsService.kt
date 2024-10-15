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
    fun rechercheLesFormationsAvecLeurScoreCorrespondantes(
        recherche: String,
        tailleMinimumRecherche: Int,
    ): Map<FormationCourte, Int> {
        val motsRecherches = filtrerMotsRecherches(recherche, tailleMinimumRecherche)
        val resultats = mutableMapOf<FormationCourte, Int>()
        motsRecherches.forEach { mot ->
            val recherches = rechercheFormationRepository.rechercherUneFormation(mot)
            val recherchesAvecScores = recherches.associate { it.formation to calculerScore(it) }
            additionnerLesScoresDesFormationsDejaRetournees(recherchesAvecScores, resultats)
        }
        return resultats
    }

    private fun additionnerLesScoresDesFormationsDejaRetournees(
        recherchesAvecScores: Map<FormationCourte, Int>,
        resultats: MutableMap<FormationCourte, Int>,
    ) {
        recherchesAvecScores.forEach { entry ->
            val formation = entry.key
            val score = entry.value
            resultats[formation]?.let { scoreActuel ->
                val nouveauScore = scoreActuel + score
                resultats.put(formation, nouveauScore)
            } ?: resultats.put(formation, score)
        }
    }

    private fun calculerScore(resultat: ResultatRechercheFormationCourte): Int {
        val score =
            resultat.scoreLabel?.let {
                when {
                    it.motExact -> 150
                    it.motExactPresentDebutPhrase || it.motExactPresentFin || it.motExactMilieu -> 130
                    it.sequencePresenteMot -> 110
                    else -> it.pourcentageMot
                }
            } ?: resultat.scoreMotClef?.let {
                val coefficient =
                    when {
                        it.motExact -> 0.85
                        it.motExactPresentDebutPhrase || it.motExactPresentFin || it.motExactMilieu -> 0.84
                        it.sequencePresenteMot -> 0.83
                        else -> 0.8
                    }
                (coefficient * it.pourcentageMot).toInt()
            } ?: 0
        return score
    }

    private fun filtrerMotsRecherches(
        recherche: String,
        tailleMinimumRecherche: Int,
    ): List<String> {
        val regexNonAlphaNumericAvecAccent = Regex(REGEX_NON_ALPHA_NUMERIC_AVEC_ACCENT)
        return recherche
            .split(regexNonAlphaNumericAvecAccent)
            .distinct()
            .filter { it.length >= tailleMinimumRecherche }
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
