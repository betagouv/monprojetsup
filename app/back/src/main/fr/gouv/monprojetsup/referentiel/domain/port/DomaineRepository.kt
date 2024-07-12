package fr.gouv.monprojetsup.referentiel.domain.port

import fr.gouv.monprojetsup.referentiel.domain.entity.Domaine

interface DomaineRepository {
    fun recupererLesDomaines(ids: List<String>): List<Domaine>
}
