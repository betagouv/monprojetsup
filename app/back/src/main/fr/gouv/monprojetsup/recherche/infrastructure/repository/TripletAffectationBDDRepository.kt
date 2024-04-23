package fr.gouv.monprojetsup.recherche.infrastructure.repository

import fr.gouv.monprojetsup.recherche.domain.entity.TripletAffectation
import fr.gouv.monprojetsup.recherche.domain.port.TripletAffectationRepository
import org.springframework.stereotype.Repository

@Repository
class TripletAffectationBDDRepository(
    val tripletAffectationJPARepository: TripletAffectationJPARepository,
) : TripletAffectationRepository {
    override fun recupererLesTripletsAffectationDeFormations(idsFormations: List<String>): Map<String, List<TripletAffectation>> {
        val tripletsAffectation = tripletAffectationJPARepository.findAllByIdFormationIn(idsFormations).groupBy { it.idFormation }
        return tripletsAffectation.map { (idFormation: String, tripletsAffectation) ->
            idFormation to tripletsAffectation.map { it.toTripletAffectation() }
        }.toMap()
    }
}
