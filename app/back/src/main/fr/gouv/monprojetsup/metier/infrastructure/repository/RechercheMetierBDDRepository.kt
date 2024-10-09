package fr.gouv.monprojetsup.metier.infrastructure.repository

import fr.gouv.monprojetsup.metier.domain.entity.MetierCourt
import fr.gouv.monprojetsup.metier.domain.port.RechercheMetierRepository
import fr.gouv.monprojetsup.metier.infrastructure.entity.MetierCourtEntity
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class RechercheMetierBDDRepository(
    private val entityManager: EntityManager,
) : RechercheMetierRepository {
    @Transactional(readOnly = true)
    override fun rechercherMetiersCourts(motRecherche: String): List<MetierCourt> {
        val resulat =
            entityManager.createNativeQuery(
                """
                WITH metiers_clean AS
                         (SELECT id,
                                 label,
                                 descriptif_general,
                                 COALESCE(lower(unaccent(label)), '')              AS label_clean,
                                 COALESCE(lower(unaccent(descriptif_general)), '') AS descriptif_clean
                          FROM ref_metier),
                     metiers_decoupes AS
                         (SELECT id,
                                 label,
                                 to_tsvector('french', descriptif_clean) @@
                                 plainto_tsquery('french', unaccent(lower(:mot_recherche)))    as ts_vector_descriptif,
                                 unaccent(lower(label)) LIKE unaccent(lower(:mot_recherche_inclus)) as label_like,
                                 regexp_split_to_table(label, '[ ()-]|/[^ ]+')                       as label_decoupe
                          FROM metiers_clean),
                     metiers_similaire AS (SELECT id,
                                                  label,
                                                  label_like,
                                                  ts_vector_descriptif,
                                                  label_decoupe,
                                                  similarity(COALESCE(unaccent(label_decoupe), ''), unaccent(:mot_recherche)) AS similarite_label_decoupe
                                           FROM metiers_decoupes)
                SELECT id,
                       label,
                       CASE
                            WHEN similarite_label_decoupe = 1 THEN 1                                
                            WHEN label_like THEN 1.01
                            WHEN similarite_label_decoupe > 0.25 THEN 12 - (11 * similarite_label_decoupe)
                            WHEN ts_vector_descriptif THEN 11
                            ELSE 32 - 80 * similarite_label_decoupe END AS rank
                FROM metiers_similaire
                WHERE ts_vector_descriptif
                   OR label_like
                   OR similarite_label_decoupe > 0.2
                GROUP BY id, label, rank
                ORDER BY rank
                """.trimIndent(),
                MetierCourtEntity::class.java,
            )
                .setParameter("mot_recherche", motRecherche)
                .setParameter("mot_recherche_inclus", "%$motRecherche%")
                .resultList
        return resulat.map { (it as MetierCourtEntity).toMetierCourt() }
    }
}
