package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.formation.domain.entity.TripletAffectation
import fr.gouv.monprojetsup.formation.domain.port.TripletAffectationRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class TripletAffectationBDDRepository(
    val tripletAffectationJPARepository: TripletAffectationJPARepository,
) : TripletAffectationRepository {
    @Transactional(readOnly = true)
    override fun recupererTripletsAffectation(idsTripletsAffectation: List<String>): Map<String, List<TripletAffectation>> {
        return tripletAffectationJPARepository.findAllByIdIn(idsTripletsAffectation).groupBy { it.idFormation }
            .map { it.key to it.value.map { entity -> entity.toTripletAffectation() } }.toMap()
    }

    @Transactional(readOnly = true)
    override fun recupererLesTripletsAffectationDeFormations(idsFormations: List<String>): Map<String, List<TripletAffectation>> {
        val tripletsAffectation =
            tripletAffectationJPARepository.findAllByIdFormationIn(idsFormations).groupBy { it.idFormation }
        return idsFormations.associateWith { idFormation ->
            tripletsAffectation[idFormation]?.map { it.toTripletAffectation() } ?: emptyList()
        }
    }

    @Transactional(readOnly = true)
    override fun recupererLesTripletsAffectationDUneFormation(idFormation: String): List<TripletAffectation> {
        return tripletAffectationJPARepository.findAllByIdFormation(idFormation).map {
            it.toTripletAffectation()
        }
    }
}
