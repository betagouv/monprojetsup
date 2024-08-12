package fr.gouv.monprojetsup.metier.infrastructure.repository

import fr.gouv.monprojetsup.metier.domain.entity.MetierCourt
import fr.gouv.monprojetsup.metier.domain.port.RechercheMetierRepository
import fr.gouv.monprojetsup.metier.infrastructure.dto.MetierCourtEntity
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
                WITH metiers_similarites AS
                         (SELECT id,
                                 label,
                                 descriptif_general,
                                 COALESCE(unaccent(label), '')              AS label_clean,
                                 COALESCE(unaccent(descriptif_general), '') AS descriptif_clean,
                                 similarity(COALESCE(unaccent(label), '') , unaccent(:mot_recherche)) * 1.2 AS similarite_label,
                                 similarity(COALESCE(unaccent(descriptif_general), ''), unaccent(:mot_recherche)) AS similmarite_decriptif
                          FROM metier)
                SELECT id,
                       label
                FROM metiers_similarites
                WHERE ((to_tsvector('french', label_clean) @@ plainto_tsquery('french', unaccent(:mot_recherche))
                   OR to_tsvector('french', descriptif_clean) @@ plainto_tsquery('french', unaccent(:mot_recherche))
                   OR unaccent(lower(label)) LIKE unaccent(lower(:mot_recherche_inclus)))
                    AND (similarite_label + similmarite_decriptif) > 0.025)
                ORDER BY (similarite_label + similmarite_decriptif) DESC;
                """.trimIndent(),
                MetierCourtEntity::class.java,
            )
                .setParameter("mot_recherche", motRecherche)
                .setParameter("mot_recherche_inclus", "%$motRecherche%")
                .resultList
        return resulat.map { (it as MetierCourtEntity).toMetierCourt() }
    }
}
