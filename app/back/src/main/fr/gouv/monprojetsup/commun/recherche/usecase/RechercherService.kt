package fr.gouv.monprojetsup.commun.recherche.usecase

import fr.gouv.monprojetsup.commun.recherche.entity.EntiteRecherchee

abstract class RechercherService<T : EntiteRecherchee> {
    protected fun rechercher(
        recherche: String,
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
        return resultats.distinct()
    }

    abstract fun rechercherPourUnMot(mot: String): List<T>
}
