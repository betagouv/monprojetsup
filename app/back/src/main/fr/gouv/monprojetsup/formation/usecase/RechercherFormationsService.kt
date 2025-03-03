package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.commun.recherche.usecase.FiltrerRechercheBuilder
import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import fr.gouv.monprojetsup.formation.domain.entity.ResultatRechercheFormationCourte
import fr.gouv.monprojetsup.formation.domain.port.RechercheFormationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RechercherFormationsService(
    private val rechercheFormationRepository: RechercheFormationRepository,
    private val filtrerRechercheBuilder: FiltrerRechercheBuilder,
) {
    @Transactional(readOnly = true)
    fun rechercheLesFormationsAvecLeurScoreCorrespondantes(
        recherche: String,
        tailleMinimumRecherche: Int,
    ): Map<FormationCourte, Int> {
        val motsRecherches = filtrerRechercheBuilder.filtrerMotsRecherches(recherche, tailleMinimumRecherche)
        val candidatsParMots = motsRecherches.map { rechercheFormationRepository.rechercherUneFormation(it) }
        val resultats = candidatsParMots.flatMap { it.map { itt -> itt.formation } }.distinct().associateWith { 1.0f }.toMutableMap()
        candidatsParMots.forEach { candidats ->
            val candidatsAvecScores = candidats.associate { it.formation to calculerScore(it) }
            val scoreMaximum = candidatsAvecScores.values.filter { it >= 1 }.maxOrNull() ?: 1
            mettreAJourLesResultatsAvecLesNouveausScores(candidatsAvecScores, scoreMaximum, resultats)
        }
        return resultats.map { it.key to (100  * it.value).toInt() }.toMap()
    }

    private fun mettreAJourLesResultatsAvecLesNouveausScores(
        recherchesAvecScores: Map<FormationCourte, Int>,
        scoreMaximum: Int,
        resultats: MutableMap<FormationCourte, Float>,
    ) {
        resultats.keys.forEach { formation ->
            val scoreActuel = resultats[formation] ?: 0f
            val score = Math.max(1e-9f, 1.0f * recherchesAvecScores.getOrDefault(formation, 0) / scoreMaximum.toFloat())
            resultats[formation] = scoreActuel * score
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
