package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.formation.domain.entity.Voeu
import fr.gouv.monprojetsup.formation.domain.port.VoeuRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class VoeuBDDRepository(
    val voeuJPARepository: VoeuJPARepository,
) : VoeuRepository {
    @Transactional(readOnly = true)
    override fun recupererVoeux(idsVoeux: List<String>): Map<String, List<Voeu>> {
        return voeuJPARepository.findAllByIdIn(idsVoeux).groupBy { it.idFormation }
            .map { it.key to it.value.map { entity -> entity.toVoeu() } }.toMap()
    }

    @Transactional(readOnly = true)
    override fun recupererLesVoeuxDeFormations(
        idsFormations: List<String>,
        obsoletesInclus: Boolean,
    ): Map<String, List<Voeu>> {
        val voeux =
            (
                if (obsoletesInclus) {
                    voeuJPARepository.findAllByIdFormationIn(idsFormations)
                } else {
                    voeuJPARepository.findAllByIdFormationInAndObsolete(
                        idsFormations,
                        false,
                    )
                }
            ).groupBy { it.idFormation }
        return idsFormations.associateWith { idFormation ->
            voeux[idFormation]?.map { it.toVoeu() } ?: emptyList()
        }
    }

    @Transactional(readOnly = true)
    override fun recupererLesVoeuxDUneFormation(idFormation: String): List<Voeu> {
        return voeuJPARepository.findAllByIdFormation(idFormation).map {
            it.toVoeu()
        }
    }

    @Transactional(readOnly = true)
    override fun recupererIdsVoeuxInexistants(idsVoeux: List<String>): List<String> {
        val existingIds = voeuJPARepository.findExistingIds(idsVoeux)
        return idsVoeux.filterNot { existingIds.contains(it) }
    }
}
