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
                    FROM ref_formation
                             LEFT JOIN LATERAL unnest(mots_clefs) AS t(i) ON true
                )
                SELECT id,
                       label
                FROM expanded_keywords
                WHERE unaccent(lower(label)) LIKE unaccent(lower(:mot_recherche_inclus_dans_un_mot))
                   OR unaccent(lower(mot_clef)) LIKE unaccent(lower(:mot_recherche_inclus_dans_un_mot))
                   OR similarity(unaccent(lower(label)), unaccent(lower(:mot_recherche_strict))) > 0.2
                   OR similarity(unaccent(lower(mot_clef)), unaccent(lower(:mot_recherche_strict))) > 0.3
                GROUP BY id,
                         label,
                         descriptif_general,
                         descriptif_attendu,
                         descriptif_conseils,
                         descriptif_diplome,
                         mots_clefs
                ORDER BY CASE   
                         WHEN unaccent(lower(label)) LIKE unaccent(lower(:mot_recherche_strict)) THEN 1
                         WHEN unaccent(lower(label)) LIKE unaccent(lower(:mot_recherche_strict_entre_parentheses)) THEN 2
                         WHEN unaccent(lower(label)) LIKE unaccent(lower(:mot_recherche_en_debut_de_phrase)) THEN 3
                         WHEN unaccent(lower(label)) LIKE unaccent(lower(:mot_recherche_en_fin_de_phrase)) THEN 4
                         WHEN unaccent(lower(label)) LIKE unaccent(lower(:mot_recherche_inclus_dans_une_phrase)) THEN 5
                         WHEN unaccent(lower(label)) LIKE unaccent(lower(:mot_recherche_inclus_dans_un_mot)) THEN 6 
                         ELSE (-90 * similarity(unaccent(lower(label)), unaccent(lower(:mot_recherche_strict)))) + 10                                               
                             END,
                         id;
                """.trimIndent(),
                FormationCourteEntity::class.java,
            )
                .setParameter("mot_recherche_strict", motRecherche)
                .setParameter("mot_recherche_en_debut_de_phrase", "$motRecherche %")
                .setParameter("mot_recherche_en_fin_de_phrase", "% $motRecherche")
                .setParameter("mot_recherche_inclus_dans_une_phrase", "% $motRecherche %")
                .setParameter("mot_recherche_strict_entre_parentheses", "%($motRecherche)%")
                .setParameter("mot_recherche_inclus_dans_un_mot", "%$motRecherche%")
                .resultList
        return resulat.map { (it as FormationCourteEntity).toFormationCourte() }
    }
}
