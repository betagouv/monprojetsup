package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.commun.recherche.usecase.FiltrerRechercheBuilder
import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import fr.gouv.monprojetsup.formation.domain.entity.ResultatRechercheFormationCourte
import fr.gouv.monprojetsup.formation.domain.port.RechercheFormationRepository
import org.springframework.stereotype.Service

@Service
class RechercherFormationsService(
    private val rechercheFormationRepository: RechercheFormationRepository,
    private val filtrerRechercheBuilder: FiltrerRechercheBuilder,
) {
    fun rechercheLesFormationsAvecLeurScoreCorrespondantes(
        recherche: String,
        tailleMinimumRecherche: Int,
    ): Map<FormationCourte, Int> {
        val motsRecherches = filtrerRechercheBuilder.filtrerMotsRecherches(recherche, tailleMinimumRecherche)
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
                    it.motEnPrefix -> 110
                    else -> it.pourcentageMot
                }
            } ?: resultat.scoreMotClef?.let {
                val coefficient =
                    when {
                        it.motExact -> 0.85
                        it.motExactPresentDebutPhrase || it.motExactPresentFin || it.motExactMilieu -> 0.84
                        it.motEnPrefix -> 0.83
                        else -> 0.8
                    }
                (coefficient * it.pourcentageMot).toInt()
            } ?: 0
        return score
    }
}
