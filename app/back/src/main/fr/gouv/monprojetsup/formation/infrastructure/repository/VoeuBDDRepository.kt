package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.formation.domain.entity.Voeu
import fr.gouv.monprojetsup.formation.domain.port.VoeuRepository
import fr.gouv.monprojetsup.formation.infrastructure.entity.JoinFormationVoeuQuery
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class VoeuBDDRepository(
    val voeuJPARepository: VoeuJPARepository,
    val entityManager: EntityManager,
) : VoeuRepository {
    @Transactional(readOnly = true)
    override fun recupererVoeux(idsVoeux: List<String>): Map<String, List<Voeu>> {
        return findAllByIdVoeuIn(idsVoeux).groupBy { it.idFormation }
            .map { it.key to it.value.map { entity -> entity.toVoeu() } }.toMap()
    }

    @Transactional(readOnly = true)
    override fun recupererLesVoeuxDeFormations(
        idsFormations: List<String>,
        obsoletesInclus: Boolean,
    ): Map<String, List<Voeu>> {
        val voeux =
            if (obsoletesInclus) {
                findAllByIdFormationIn(idsFormations)
            } else {
                findAllByIdFormationInNotObsolete(idsFormations)
            }
        val voeuxGroupesParFormation = voeux.groupBy { it.idFormation }
        return idsFormations.associateWith { idFormation ->
            voeuxGroupesParFormation[idFormation]?.map { it.toVoeu() } ?: emptyList()
        }
    }

    @Transactional(readOnly = true)
    override fun recupererLesVoeuxDUneFormation(
        idFormation: String,
        obsoletesInclus: Boolean,
    ): List<Voeu> {
        val formations = listOf(idFormation)
        return (
            if (obsoletesInclus) {
                findAllByIdFormationIn(formations)
            } else {
                findAllByIdFormationInNotObsolete(formations)
            }
        ).map { it.toVoeu() }
    }

    @Transactional(readOnly = true)
    override fun recupererIdsVoeuxInexistants(idsVoeux: List<String>): List<String> {
        val existingIds = voeuJPARepository.findExistingIds(idsVoeux)
        return idsVoeux.filterNot { existingIds.contains(it) }
    }

    private fun findAllByIdVoeuIn(idsVoeux: List<String>): List<JoinFormationVoeuQuery> {
        return entityManager.createQuery(
            """
                SELECT new fr.gouv.monprojetsup.formation.infrastructure.entity.JoinFormationVoeuQuery(j ,v)
                FROM JoinFormationVoeuEntity j
                JOIN VoeuEntity v ON j.id.idVoeu = v.id
                WHERE j.id.idVoeu IN :idsVoeux
            """,
            JoinFormationVoeuQuery::class.java,
        ).setParameter("idsVoeux", idsVoeux).resultList
    }

    private fun findAllByIdFormationIn(idsFormations: List<String>): List<JoinFormationVoeuQuery> {
        return entityManager.createQuery(
            """
                SELECT new fr.gouv.monprojetsup.formation.infrastructure.entity.JoinFormationVoeuQuery(j ,v)
                FROM JoinFormationVoeuEntity j
                JOIN VoeuEntity v ON j.id.idVoeu = v.id
                WHERE j.id.idFormation IN :idsFormations
            """,
            JoinFormationVoeuQuery::class.java,
        ).setParameter("idsFormations", idsFormations).resultList
    }

    private fun findAllByIdFormationInNotObsolete(idsFormations: List<String>): List<JoinFormationVoeuQuery> {
        return entityManager.createQuery(
            """
                SELECT new fr.gouv.monprojetsup.formation.infrastructure.entity.JoinFormationVoeuQuery(j ,v)
                FROM JoinFormationVoeuEntity j
                JOIN VoeuEntity v ON j.id.idVoeu = v.id
                WHERE j.id.idFormation IN :idsFormations
                AND v.obsolete = false
            """,
            JoinFormationVoeuQuery::class.java,
        ).setParameter("idsFormations", idsFormations).resultList
    }
}
