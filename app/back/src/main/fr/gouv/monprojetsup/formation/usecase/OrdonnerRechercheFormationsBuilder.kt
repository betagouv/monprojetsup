package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil.FormationAvecSonAffinite
import org.springframework.stereotype.Component

@Component
class OrdonnerRechercheFormationsBuilder {
    fun trierParScore(resultats: Map<FormationCourte, Int>): List<FormationCourte> {
        return resultats.entries.sortedByDescending { it.value }.associate { it.toPair() }.map { it.key }
    }

    fun trierParScoreEtSelonSuggestionsProfil(
        resultats: Map<FormationCourte, Int>,
        formationsAvecLeurAffinite: List<FormationAvecSonAffinite>,
    ): List<FormationCourte> {
        val formationsOrdonnees = formationsAvecLeurAffinite.sortedByDescending { it.tauxAffinite }.map { it.idFormation }
        return filtrerParScorePuisParIndex(formationsOrdonnees, resultats)
    }

    private fun filtrerParScorePuisParIndex(
        formationsOrdonnees: List<String>,
        resultats: Map<FormationCourte, Int>,
    ): List<FormationCourte> {
        val comparateur =
            Comparator<String> { s1, s2 ->
                val index1 = formationsOrdonnees.indexOf(s1)
                val index2 = formationsOrdonnees.indexOf(s2)
                index1.compareTo(index2)
            }

        return resultats.toList()
            .sortedWith(
                compareByDescending<Pair<FormationCourte, Int>> { it.second }
                    .thenComparing { p1, p2 -> comparateur.compare(p1.first.id, p2.first.id) },
            ).map { it.first }
    }
}
