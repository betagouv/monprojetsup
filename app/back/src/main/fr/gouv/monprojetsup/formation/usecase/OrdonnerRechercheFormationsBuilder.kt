package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil.FormationAvecSonAffinite
import org.springframework.stereotype.Component

@Component
class OrdonnerRechercheFormationsBuilder {
    fun trierParScore(resultats: Map<FormationCourte, Double>): List<FormationCourte> {
        return resultats.entries.sortedByDescending { it.value }.associate { it.toPair() }.map { it.key }
    }

    fun trierParScoreEtSelonSuggestionsProfil(
        resultats: Map<FormationCourte, Double>,
        formationsAvecLeurAffinite: List<FormationAvecSonAffinite>,
    ): List<FormationCourte> {
        return trierParScoreRecherchePuisParAffinite(formationsAvecLeurAffinite, resultats)
    }

    private fun trierParScoreRecherchePuisParAffinite(
        formationsAvecLeurAffinite: List<FormationAvecSonAffinite>,
        resultats: Map<FormationCourte, Double>,
    ): List<FormationCourte> {
        val comparateur = creerComparateur(formationsAvecLeurAffinite)
        return resultats.toList()
            .sortedWith(
                compareByDescending<Pair<FormationCourte, Double>> { it.second }
                    .thenComparing { p1, p2 -> comparateur.compare(p1.first.id, p2.first.id) }
                    .thenComparing { p1, p2 -> p1.first.id.compareTo(p2.first.id) },
            ).map { it.first }
    }

    private fun creerComparateur(formationsOrdonnees: List<FormationAvecSonAffinite>): java.util.Comparator<String> {
        val mapAvecLesIndex = formationsOrdonnees.associate { it.idFormation to it.tauxAffinite }
        return Comparator { s1, s2 ->
            val index1 = mapAvecLesIndex[s1] ?: -1.0f
            val index2 = mapAvecLesIndex[s2] ?: -1.0f
            index2.compareTo(index1)
        }
    }
}
