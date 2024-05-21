package fr.gouv.monprojetsup.recherche.infrastructure.repository

import fr.gouv.monprojetsup.recherche.domain.entity.TripletAffectation
import fr.gouv.monprojetsup.recherche.domain.port.TripletAffectationRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class TripletAffectationBDDRepository(
    val tripletAffectationJPARepository: TripletAffectationJPARepository,
) : TripletAffectationRepository {
    @Transactional(readOnly = true)
    override fun recupererLesTripletsAffectationDeFormations(idsFormations: List<String>): Map<String, List<TripletAffectation>> {
        val tripletsAffectation =
            tripletAffectationJPARepository.findAllByIdFormationIn(idsFormations).groupBy { it.idFormation }
        return idsFormations.associateWith { idFormation ->
            tripletsAffectation[idFormation]?.map { it.toTripletAffectation() } ?: emptyList()
        }
    }
}
