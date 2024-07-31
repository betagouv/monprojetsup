package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import fr.gouv.monprojetsup.formation.domain.port.RechercheFormationRepository
import fr.gouv.monprojetsup.formation.infrastructure.entity.FormationCourteEntity
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class RechercheFormationBDDRepository(
    val entityManager: EntityManager,
) : RechercheFormationRepository {
    @Transactional(readOnly = true)
    override fun rechercherUneFormation(motRecherche: String): List<FormationCourte> {
        val resulat =
            entityManager.createNativeQuery(
                """
                WITH expanded_keywords AS (
                    SELECT  id,
                            label,
                            descriptif_general,
                            descriptif_attendu,
                            descriptif_conseils,
                            descriptif_diplome,
                            mots_clefs,
                            t.i as mot_clef
                    FROM formation
                    LEFT JOIN LATERAL unnest(mots_clefs) AS t(i) ON true)
                SELECT id,
                       label
                FROM expanded_keywords
                WHERE unaccent(lower(label)) LIKE unaccent(lower(:mot_recherche))
                   OR unaccent(lower(mot_clef)) LIKE unaccent(lower(:mot_recherche))
                GROUP BY id,
                         label,
                         descriptif_general,
                         descriptif_attendu,
                         descriptif_conseils,
                         descriptif_diplome,
                         mots_clefs
                ORDER BY CASE
                             WHEN unaccent(lower(label)) LIKE unaccent(lower(:mot_recherche)) THEN 1
                             ELSE 2
                             END,
                         id;
                """.trimIndent(),
                FormationCourteEntity::class.java,
            )
                .setParameter("mot_recherche", "%$motRecherche%")
                .resultList
        return resulat.map { (it as FormationCourteEntity).toFormationCourte() }
    }
}