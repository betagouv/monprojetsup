package fr.gouv.monprojetsup.metier.domain.port

import fr.gouv.monprojetsup.metier.domain.entity.Metier
import fr.gouv.monprojetsup.metier.domain.entity.MetierAvecSesFormations
import fr.gouv.monprojetsup.metier.domain.entity.MetierCourt

interface MetierRepository {
    fun recupererMetiersDeFormations(
        idsFormations: List<String>,
        obsoletesInclus: Boolean,
    ): Map<String, List<Metier>>

    fun recupererLesMetiers(ids: List<String>): List<Metier>

    fun recupererLesMetiersAvecSesFormations(ids: List<String>): List<MetierAvecSesFormations>

    fun recupererIdsMetiersInexistants(ids: List<String>): List<String>

    fun recupererLesMetiersCourts(ids: List<String>): List<MetierCourt>
}
