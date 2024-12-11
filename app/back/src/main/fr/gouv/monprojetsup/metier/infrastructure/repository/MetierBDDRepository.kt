package fr.gouv.monprojetsup.metier.infrastructure.repository

import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import fr.gouv.monprojetsup.metier.domain.entity.Metier
import fr.gouv.monprojetsup.metier.domain.entity.MetierAvecSesFormations
import fr.gouv.monprojetsup.metier.domain.entity.MetierCourt
import fr.gouv.monprojetsup.metier.domain.port.MetierRepository
import fr.gouv.monprojetsup.metier.infrastructure.entity.JoinFormationMetierQuery
import fr.gouv.monprojetsup.metier.infrastructure.entity.JoinMetierFormationQuery
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class MetierBDDRepository(
    private val metierJPARepository: MetierJPARepository,
    private val metierCourtJPARepository: MetierCourtJPARepository,
    private val entityManager: EntityManager,
    private val logger: MonProjetSupLogger,
) : MetierRepository {
    override fun recupererMetiersDeFormations(
        idsFormations: List<String>,
        obsoletesInclus: Boolean,
    ): Map<String, List<Metier>> {
        val query =
            if (obsoletesInclus) {
                entityManager.createQuery(
                    """
                    SELECT new fr.gouv.monprojetsup.metier.infrastructure.entity.JoinFormationMetierQuery(jointure.id.idFormation, metier) 
                    FROM JoinFormationMetierEntity jointure 
                    JOIN MetierEntity metier ON metier.id = jointure.id.idMetier
                    WHERE jointure.id.idFormation IN :idsFormations
                    """.trimIndent(),
                    JoinFormationMetierQuery::class.java,
                )
            } else {
                entityManager.createQuery(
                    """
                    SELECT new fr.gouv.monprojetsup.metier.infrastructure.entity.JoinFormationMetierQuery(jointure.id.idFormation, metier) 
                    FROM JoinFormationMetierEntity jointure 
                    JOIN MetierEntity metier ON metier.id = jointure.id.idMetier
                    WHERE jointure.id.idFormation IN :idsFormations AND metier.obsolete = false
                    """.trimIndent(),
                    JoinFormationMetierQuery::class.java,
                )
            }
        val metiers = query.setParameter("idsFormations", idsFormations).resultList.groupBy { it.idFormation }
        return idsFormations.associateWith { idFormation -> metiers[idFormation]?.map { it.toMetier() } ?: emptyList() }
    }

    override fun recupererLesMetiersAvecSesFormations(ids: List<String>): List<MetierAvecSesFormations> {
        val formationsParIdMetier =
            entityManager.createQuery(
                """
                SELECT new fr.gouv.monprojetsup.metier.infrastructure.entity.JoinMetierFormationQuery(metier, formation) 
                FROM MetierEntity metier 
                LEFT JOIN JoinFormationMetierEntity jointure ON metier.id = jointure.id.idMetier
                LEFT JOIN FormationCourteEntity formation ON formation.id = jointure.id.idFormation
                WHERE metier.id IN :ids
                """.trimIndent(),
                JoinMetierFormationQuery::class.java,
            ).setParameter("ids", ids).resultList.groupBy { it.metier.id }

        return ids.mapNotNull { idMetier ->
            val jointuresEtFormations = formationsParIdMetier.getOrDefault(idMetier, null)?.takeUnless { it.isEmpty() }
            if (jointuresEtFormations != null) {
                val metier = jointuresEtFormations.first().metier
                val formations = jointuresEtFormations.mapNotNull { it.formation?.toFormationCourte() }
                MetierAvecSesFormations(
                    id = metier.id,
                    nom = metier.label,
                    descriptif = metier.descriptifGeneral,
                    liens = metier.liens.map { it.toLien() },
                    formations = formations,
                )
            } else {
                logguerMetierAbsent(idMetierAbsent = idMetier)
                null
            }
        }
    }

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

    override fun recupererLesMetiersCourts(ids: List<String>): List<MetierCourt> {
        return metierCourtJPARepository.findAllByIdIn(ids).map { it.toMetierCourt() }
    }

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
