package fr.gouv.monprojetsup.recherche.infrastructure.repository

import fr.gouv.monprojetsup.recherche.domain.entity.Formation
import fr.gouv.monprojetsup.recherche.domain.entity.Metier
import fr.gouv.monprojetsup.recherche.domain.port.FormationRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class FormationBDDRepository(
    val formationMetierJPARepository: FormationMetierJPARepository,
) : FormationRepository {
    @Transactional(readOnly = true)
    override fun recupererLesFormationsAvecLeursMetiers(idsFormations: List<String>): Map<Formation, List<Metier>> {
        val formations = formationMetierJPARepository.findAllByIdIn(idsFormations)
        return formations.associate { formationEntity ->
            formationEntity.toFormation() to formationEntity.metiers.map { it.metier.toMetier() }
        }
    }
}
