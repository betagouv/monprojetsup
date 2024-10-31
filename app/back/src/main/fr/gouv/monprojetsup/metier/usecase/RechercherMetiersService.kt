package fr.gouv.monprojetsup.metier.usecase

import fr.gouv.monprojetsup.commun.recherche.usecase.FiltrerRechercheBuilder
import fr.gouv.monprojetsup.metier.domain.entity.MetierCourt
import fr.gouv.monprojetsup.metier.domain.entity.ResultatRechercheMetierCourt
import fr.gouv.monprojetsup.metier.domain.port.RechercheMetierRepository
import org.springframework.stereotype.Service

@Service
class RechercherMetiersService(
    private val rechercheMetierRepository: RechercheMetierRepository,
    private val filtrerRechercheBuilder: FiltrerRechercheBuilder,
) {
    fun rechercherMetiersTriesParScores(
        recherche: String,
        tailleMinimumRecherche: Int,
    ): List<MetierCourt> {
        val motsRecherches = filtrerRechercheBuilder.filtrerMotsRecherches(recherche, tailleMinimumRecherche)
        val resultats = mutableListOf<ResultatRechercheMetierCourt>()
        motsRecherches.forEach { mot ->
            val resultatsRechercheMot = rechercheMetierRepository.rechercherMetiersCourts(mot)
            resultats.addAll(resultatsRechercheMot)
        }
        val nombreDeMetiersrecherches = motsRecherches.size
        val metiersCorrespondantAToutesLesRecherches =
            if (nombreDeMetiersrecherches == 1) {
                resultats
            } else {
                val metiersEtLeurNombreDApparitions =
                    resultats.groupingBy { it.metier }.eachCount().filterValues { it == nombreDeMetiersrecherches }
                resultats.filter { metiersEtLeurNombreDApparitions.contains(it.metier) }
            }

        val resultatsTries =
            metiersCorrespondantAToutesLesRecherches.groupBy { it.metier }.map { entry ->
                entry.key to calculerScoreTotal(entry.value)
            }.sortedByDescending { it.second }
        return resultatsTries.map { it.first }
    }

    private fun calculerScoreTotal(resultatsRechercheMetierCourt: List<ResultatRechercheMetierCourt>): Int {
        var score = 0
        resultatsRechercheMetierCourt.forEach {
            score += calculerScore(it.score)
        }
        return score
    }

    private fun calculerScore(score: ResultatRechercheMetierCourt.ScoreMot) =
        when {
            score.labelContientMot -> 110
            score.prefixDansLabel -> 100
            else -> score.similariteLabelDecoupe + (if (score.motDansLeDescriptif) 20 else 0)
        }
}
