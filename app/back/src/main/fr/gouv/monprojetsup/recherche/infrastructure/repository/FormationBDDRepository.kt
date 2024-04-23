package fr.gouv.monprojetsup.recherche.infrastructure.repository

import fr.gouv.monprojetsup.recherche.domain.entity.Formation
import fr.gouv.monprojetsup.recherche.domain.entity.Metier
import fr.gouv.monprojetsup.recherche.domain.port.FormationRepository
import fr.gouv.monprojetsup.recherche.infrastructure.entity.FormationEntity
import org.springframework.stereotype.Repository

@Repository
class FormationBDDRepository(
    val formationMetierJPARepository: FormationMetierJPARepository,
) : FormationRepository {
    override fun recupererLesFormationsAvecLeursMetiers(idsFormations: List<String>): Map<Formation, List<Metier>> {
        val pairesMetierFormation = formationMetierJPARepository.findAllByIdFormationIdIn(idsFormations).groupBy { it.formation }
        return pairesMetierFormation.map { (formationEntity: FormationEntity, metierEntities) ->
            formationEntity.toFormation() to metierEntities.map { it.metier.toMetier() }
        }.toMap()
    }
}
