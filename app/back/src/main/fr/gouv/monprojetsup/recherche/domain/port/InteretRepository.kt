package fr.gouv.monprojetsup.recherche.domain.port

import fr.gouv.monprojetsup.recherche.domain.entity.Interet

interface InteretRepository {
    fun recupererLesInterets(ids: List<String>): List<Interet>
}
