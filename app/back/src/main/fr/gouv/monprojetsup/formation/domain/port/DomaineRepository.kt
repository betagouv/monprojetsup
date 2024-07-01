package fr.gouv.monprojetsup.formation.domain.port

import fr.gouv.monprojetsup.formation.domain.entity.Domaine

interface DomaineRepository {
    fun recupererLesDomaines(ids: List<String>): List<Domaine>
}
