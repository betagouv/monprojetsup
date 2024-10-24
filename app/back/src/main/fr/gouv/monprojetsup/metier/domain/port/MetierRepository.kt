package fr.gouv.monprojetsup.metier.domain.port

import fr.gouv.monprojetsup.metier.domain.entity.Metier
import fr.gouv.monprojetsup.metier.domain.entity.MetierAvecSesFormations

interface MetierRepository {
    fun recupererMetiersDeFormations(idsFormations: List<String>): Map<String, List<Metier>>

    fun recupererLesMetiers(ids: List<String>): List<Metier>

    fun recupererLesMetiersAvecSesFormations(ids: List<String>): List<MetierAvecSesFormations>

    fun recupererIdsMetiersInexistants(ids: List<String>): List<String>
}
