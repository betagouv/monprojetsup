package fr.gouv.monprojetsup.formation.domain.port

import fr.gouv.monprojetsup.formation.domain.entity.TripletAffectation

interface TripletAffectationRepository {
    fun recupererLesTripletsAffectationDeFormations(idsFormations: List<String>): Map<String, List<TripletAffectation>>

    fun recupererLesTripletsAffectationDUneFormation(idFormation: String): List<TripletAffectation>
}
