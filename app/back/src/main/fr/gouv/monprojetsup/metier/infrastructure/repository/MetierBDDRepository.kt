package fr.gouv.monprojetsup.metier.infrastructure.repository

import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import fr.gouv.monprojetsup.metier.domain.entity.Metier
import fr.gouv.monprojetsup.metier.domain.entity.MetierAvecSesFormations
import fr.gouv.monprojetsup.metier.domain.port.MetierRepository
import fr.gouv.monprojetsup.metier.infrastructure.entity.JoinFormationMetierEntity
import fr.gouv.monprojetsup.metier.infrastructure.entity.JoinMetierFormationEntity
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class MetierBDDRepository(
    private val metierJPARepository: MetierJPARepository,
    private val entityManager: EntityManager,
    private val logger: MonProjetSupLogger,
) : MetierRepository {
    @Transactional(readOnly = true)
    override fun recupererMetiersDeFormations(idsFormations: List<String>): Map<String, List<Metier>> {
        val metiers =
            entityManager.createQuery(
                """
                SELECT jointure 
                FROM JoinFormationMetierEntity jointure 
                JOIN MetierEntity metier ON metier.id = jointure.id.idMetier
                WHERE jointure.id.idFormation IN :idsFormations
                """.trimIndent(),
                JoinFormationMetierEntity::class.java,
            ).setParameter("idsFormations", idsFormations)
                .resultList.groupBy { it.id.idFormation }
        return idsFormations.associateWith { metiers[it]?.map { it.metier.toMetier() } ?: emptyList() }
    }

    @Transactional(readOnly = true)
    override fun recupererLesMetiersAvecSesFormations(ids: List<String>): List<MetierAvecSesFormations> {
        val metiers = metierJPARepository.findAllByIdIn(ids)
        val formation =
            entityManager.createQuery(
                """
                SELECT jointure 
                FROM JoinMetierFormationEntity jointure 
                JOIN FormationCourteEntity formation ON formation.id = jointure.id.idFormation
                """.trimIndent(),
                JoinMetierFormationEntity::class.java,
            ).resultList.groupBy { it.id.idMetier }

        return ids.mapNotNull { idMetier ->
            val metier = metiers.firstOrNull { it.id == idMetier }
            if (metier == null) {
                logguerMetierAbsent(idMetierAbsent = idMetier)
            }
            metier?.let {
                MetierAvecSesFormations(
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
    override fun recupererLesMetiers(ids: List<String>): List<Metier> {
        val metiers = metierJPARepository.findAllByIdIn(ids)
        return ids.mapNotNull { idMetier ->
            val metier = metiers.firstOrNull { it.id == idMetier }
            if (metier == null) {
                logguerMetierAbsent(idMetierAbsent = idMetier)
            }
            metier?.toMetier()
        }
    }

    @Transactional(readOnly = true)
    override fun recupererIdsMetiersInexistants(ids: List<String>): List<String> {
        val existingIds = metierJPARepository.findExistingIds(ids)
        return ids.filterNot { existingIds.contains(it) }
    }

    private fun logguerMetierAbsent(idMetierAbsent: String) {
        logger.error(
            type = "METIER_ABSENT_BDD",
            message = "Le métier $idMetierAbsent n'est pas présent en base",
            parametres = mapOf("metierAbsent" to idMetierAbsent),
        )
    }
}
