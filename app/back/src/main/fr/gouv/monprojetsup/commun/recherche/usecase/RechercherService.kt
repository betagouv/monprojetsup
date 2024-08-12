package fr.gouv.monprojetsup.commun.recherche.usecase

import fr.gouv.monprojetsup.commun.recherche.entity.EntiteRecherchee
import kotlin.math.min

abstract class RechercherService<T : EntiteRecherchee> {
    protected fun rechercher(
        recherche: String,
        nombreMaximaleDeResultats: Int,
        tailleMinimumRecherche: Int,
    ): List<T> {
        val regexNonAlphaNumericAvecAccent = Regex("[^0-9A-Za-zÀ-ÖØ-öø-ÿ]")
        val motsRecherches = recherche.split(regexNonAlphaNumericAvecAccent).filter { it.length >= tailleMinimumRecherche }.distinct()
        val resultats = mutableListOf<T>()
        motsRecherches.forEach { mot ->
            resultats.addAll(rechercherPourUnMot(mot))
        }
        val selecteur = resultats.groupingBy { it.id }.eachCount()
        resultats.sortByDescending { selecteur[it.id] }
        val resultatsDistincts = resultats.distinct()
        return resultatsDistincts.subList(0, min(nombreMaximaleDeResultats, resultatsDistincts.size))
    }

    abstract fun rechercherPourUnMot(mot: String): List<T>
}
