package fr.gouv.monprojetsup.formation.domain.port

import fr.gouv.monprojetsup.formation.domain.entity.MetierDetaille

interface MetierRepository {
    fun recupererLesMetiersDetailles(ids: List<String>): List<MetierDetaille>
}
