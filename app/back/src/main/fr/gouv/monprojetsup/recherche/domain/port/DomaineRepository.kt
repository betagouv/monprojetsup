package fr.gouv.monprojetsup.recherche.domain.port

import fr.gouv.monprojetsup.recherche.domain.entity.Domaine

interface DomaineRepository {
    fun recupererLesDomaines(ids: List<String>): List<Domaine>
}
