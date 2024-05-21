package fr.gouv.monprojetsup.recherche.domain.port

import fr.gouv.monprojetsup.recherche.domain.entity.TripletAffectation

interface TripletAffectationRepository {
    fun recupererLesTripletsAffectationDeFormations(idsFormations: List<String>): Map<String, List<TripletAffectation>>
}
