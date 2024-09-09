package fr.gouv.monprojetsup.metier.infrastructure.repository

import fr.gouv.monprojetsup.metier.domain.entity.Metier
import fr.gouv.monprojetsup.metier.domain.port.MetierRepository
import fr.gouv.monprojetsup.metier.infrastructure.entity.JoinFormationMetierEntity
import jakarta.persistence.EntityManager
import org.slf4j.Logger
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class MetierBDDRepository(
    private val metierJPARepository: MetierJPARepository,
    private val entityManager: EntityManager,
    private val logger: Logger,
) : MetierRepository {
    @Transactional(readOnly = true)
    override fun recupererLesMetiersDetailles(ids: List<String>): List<Metier> {
        val metiers = metierJPARepository.findAllByIdIn(ids)
        val formation =
            entityManager.createQuery(
                """
                SELECT jointure 
                FROM JoinFormationMetierEntity jointure 
                JOIN FormationCourteEntity formation ON formation.id = jointure.id.idFormation
                """.trimIndent(),
                JoinFormationMetierEntity::class.java,
            ).resultList.groupBy { it.id.idMetier }

        return ids.mapNotNull { idMetier ->
            val metier = metiers.firstOrNull { it.id == idMetier }
            if (metier == null) {
                logger.error("Le métier $idMetier n'est pas présent en base")
            }
            metier?.let {
                Metier(
                    id = metier.id,
                    nom = metier.label,
                    descriptif = metier.descriptifGeneral,
                    liens = metier.liens.map { it.toLien() },
                    formations = formation[metier.id]?.map { it.formation.toFormationCourte() } ?: emptyList(),
                )
            }
        }
    }

    @Transactional(readOnly = true)
    override fun verifierMetiersExistent(ids: List<String>): Boolean {
        return metierJPARepository.countAllByIdIn(ids) == ids.size
    }
}
