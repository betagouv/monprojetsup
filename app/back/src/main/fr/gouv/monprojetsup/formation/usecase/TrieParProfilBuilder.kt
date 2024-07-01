package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.formation.domain.entity.Metier
import fr.gouv.monprojetsup.formation.domain.entity.MetierDetaille
import fr.gouv.monprojetsup.formation.domain.entity.TripletAffectation

object TrieParProfilBuilder {
    fun getNomCommunesTriesParAffinites(
        tripletsAffectation: List<TripletAffectation>,
        communesFavorites: List<String>?,
    ): List<String> {
        val communesDesAffectations = tripletsAffectation.map { it.commune }.distinct()
        val communesTrieesParAffinites: MutableList<String> = mutableListOf()
        communesFavorites?.forEach { commune ->
            if (communesDesAffectations.contains(commune)) {
                communesTrieesParAffinites.add(commune)
            }
        }
        communesDesAffectations.forEach { commune ->
            if (!communesTrieesParAffinites.contains(commune)) {
                communesTrieesParAffinites.add(commune)
            }
        }
        return communesTrieesParAffinites
    }

    fun getNomMetiersTriesParAffinites(
        metiers: List<Metier>,
        idsMetierTriesParAffinite: List<String>,
    ): List<String> {
        val orderById = idsMetierTriesParAffinite.mapIndexed { index, idMetier -> Pair(idMetier, index) }.toMap()
        val metiersTries = metiers.sortedBy { orderById[it.id] }
        return metiersTries.map { it.nom }
    }

    fun trierMetiersParAffinites(
        metiers: List<MetierDetaille>,
        idsMetierTriesParAffinite: List<String>,
    ): List<MetierDetaille> {
        val orderById = idsMetierTriesParAffinite.mapIndexed { index, idMetier -> Pair(idMetier, index) }.toMap()
        val metiersTries = metiers.sortedBy { orderById[it.id] }
        return metiersTries
    }
}
