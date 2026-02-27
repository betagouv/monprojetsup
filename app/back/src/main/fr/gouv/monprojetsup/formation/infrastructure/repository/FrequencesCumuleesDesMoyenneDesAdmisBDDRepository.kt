package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.formation.domain.port.FrequencesCumuleesDesMoyenneDesAdmisRepository
import fr.gouv.monprojetsup.formation.infrastructure.entity.MoyenneGeneraleAdmisEntity
import fr.gouv.monprojetsup.formation.infrastructure.entity.MoyenneGeneraleAdmisQuery
import fr.gouv.monprojetsup.referentiel.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.referentiel.infrastructure.entity.BaccalaureatEntity
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class FrequencesCumuleesDesMoyenneDesAdmisBDDRepository(
    private val entityManager: EntityManager,
) : FrequencesCumuleesDesMoyenneDesAdmisRepository {
    override fun recupererFrequencesCumuleesParBacs(): Map<Baccalaureat, List<Int>> {
        return findAllBaccalaureatIdNotIn(
            idsBaccalaureatsExclus = idsBaccalaureatsExclus,
        ).map { entry ->
            val listeDesFrequencesCumulesPourUnBac = entry.value.map { it.frequencesCumulees }
            val sommeDesFrequencesCumulees =
                (0 until listeDesFrequencesCumulesPourUnBac[0].size).map { index ->
                    listeDesFrequencesCumulesPourUnBac.sumOf { it[index] }
                }
            entry.key.toBaccalaureat() to sommeDesFrequencesCumulees
        }.toMap()
    }

    override fun recupererFrequencesCumuleesDeTousLesBacs(idFormation: String): Map<Baccalaureat, List<Int>> {
        return findAllByAnneeAndIdFormationAndBaccalaureatIdNotIn(
            idFormation = idFormation,
            idsBaccalaureatsExclus = idsBaccalaureatsExclus,
        )
    }

    override fun recupererFrequencesCumuleesDeTousLesBacs(idsFormations: List<String>): Map<String, Map<Baccalaureat, List<Int>>> {
        val groupementParIdFormation =
            findAllByAnneeAndIdFormationInAndBaccalaureatIdNotIn(
                idsFormations = idsFormations,
                idsBaccalaureatsExclus = idsBaccalaureatsExclus,
            ).groupBy { it.moyenneGeneraleAdmisEntity.id.idFormation }
        return idsFormations.associateWith { idFormation ->
            groupementParIdFormation[idFormation]?.associate { entity ->
                entity.baccalaureatEntity.toBaccalaureat() to entity.moyenneGeneraleAdmisEntity.frequencesCumulees
            } ?: emptyMap()
        }
    }

    private fun findAllBaccalaureatIdNotIn(
        idsBaccalaureatsExclus: List<String>,
    ): Map<BaccalaureatEntity, List<MoyenneGeneraleAdmisEntity>> {
        return entityManager.createQuery(
            """
            SELECT new fr.gouv.monprojetsup.formation.infrastructure.entity.MoyenneGeneraleAdmisQuery(b, m)
            FROM MoyenneGeneraleAdmisEntity m
            JOIN BaccalaureatEntity b ON m.id.idBaccalaureat = b.id
            WHERE m.id.idBaccalaureat NOT IN :idsBaccalaureatsExclus
            """,
            MoyenneGeneraleAdmisQuery::class.java,
        ).setParameter("idsBaccalaureatsExclus", idsBaccalaureatsExclus)
            .resultList
            .groupBy { it.baccalaureatEntity }
            .map { entry -> entry.key to entry.value.map { it.moyenneGeneraleAdmisEntity } }
            .toMap()
    }

    private fun findAllByAnneeAndIdFormationAndBaccalaureatIdNotIn(
        idFormation: String,
        idsBaccalaureatsExclus: List<String>,
    ): Map<Baccalaureat, List<Int>> {
        return entityManager.createQuery(
            """
            SELECT new fr.gouv.monprojetsup.formation.infrastructure.entity.MoyenneGeneraleAdmisQuery(b, m)
            FROM MoyenneGeneraleAdmisEntity m
            JOIN BaccalaureatEntity b ON m.id.idBaccalaureat = b.id
            WHERE m.id.idBaccalaureat NOT IN :idsBaccalaureatsExclus
            AND m.id.idFormation = :idFormation
            """,
            MoyenneGeneraleAdmisQuery::class.java,
        ).setParameter("idsBaccalaureatsExclus", idsBaccalaureatsExclus)
            .setParameter("idFormation", idFormation)
            .resultList
            .associate { entry -> entry.baccalaureatEntity.toBaccalaureat() to entry.moyenneGeneraleAdmisEntity.frequencesCumulees }
    }

    fun findAllByAnneeAndIdFormationInAndBaccalaureatIdNotIn(
        idsFormations: List<String>,
        idsBaccalaureatsExclus: List<String>,
    ): List<MoyenneGeneraleAdmisQuery> {
        return entityManager.createQuery(
            """
            SELECT new fr.gouv.monprojetsup.formation.infrastructure.entity.MoyenneGeneraleAdmisQuery(b, m)
            FROM MoyenneGeneraleAdmisEntity m
            JOIN BaccalaureatEntity b ON m.id.idBaccalaureat = b.id
            WHERE m.id.idBaccalaureat NOT IN :idsBaccalaureatsExclus
            AND m.id.idFormation IN :idsFormation
            """,
            MoyenneGeneraleAdmisQuery::class.java,
        ).setParameter("idsBaccalaureatsExclus", idsBaccalaureatsExclus)
            .setParameter("idsFormation", idsFormations)
            .resultList
    }

    companion object {
        private val idsBaccalaureatsExclus = listOf("NC")
    }
}
