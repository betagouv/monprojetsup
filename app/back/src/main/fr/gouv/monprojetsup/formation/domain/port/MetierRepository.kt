package fr.gouv.monprojetsup.formation.domain.port

import fr.gouv.monprojetsup.metier.domain.entity.Metier

interface MetierRepository {
    fun recupererLesMetiersDetailles(ids: List<String>): List<Metier>
}
