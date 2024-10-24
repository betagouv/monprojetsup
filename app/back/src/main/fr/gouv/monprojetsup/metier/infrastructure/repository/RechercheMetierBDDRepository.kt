package fr.gouv.monprojetsup.metier.infrastructure.repository

import fr.gouv.monprojetsup.commun.Constantes.REGEX_NON_ALPHA_NUMERIC_AVEC_ACCENT
import fr.gouv.monprojetsup.metier.domain.entity.ResultatRechercheMetierCourt
import fr.gouv.monprojetsup.metier.domain.port.RechercheMetierRepository
import fr.gouv.monprojetsup.metier.infrastructure.entity.RechercheMetierEntity
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class RechercheMetierBDDRepository(
    private val entityManager: EntityManager,
) : RechercheMetierRepository {
    @Transactional(readOnly = true)
    override fun rechercherMetiersCourts(motRecherche: String): List<ResultatRechercheMetierCourt> {
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
                                 plainto_tsquery('french', unaccent(lower(:mot_recherche)))          as mot_dans_le_descriptif,
                                 regexp_split_to_table(label_clean, :regex_non_alpha_numeric_avec_accent) as label_decoupe
                          FROM metiers_clean),
                     metiers_similaire AS (SELECT id,
                                                  label,
                                                  label_decoupe,
                                                  mot_dans_le_descriptif,
                                                  unaccent(lower(label_decoupe)) LIKE unaccent(lower(:mot_recherche))                           AS label_contient_mot,
                                                  unaccent(lower(label_decoupe)) LIKE unaccent(lower(:mot_recherche_infix))                     AS infix_dans_label,
                                                  similarity(COALESCE(unaccent(lower(label_decoupe)), ''),
                                                             unaccent(lower(:mot_recherche)))                                                   AS similarite_label_decoupe,
                                                  ROW_NUMBER() OVER (PARTITION BY id ORDER BY 100 *
                                                                                              similarity(unaccent(lower(label_decoupe)),
                                                                                                         unaccent(lower(:mot_recherche))) DESC) AS numero_ligne_label
                                           FROM metiers_decoupes)
                SELECT id,
                       label,
                       mot_dans_le_descriptif,
                       label_contient_mot,
                       infix_dans_label,
                       similarite_label_decoupe
                FROM metiers_similaire
                WHERE (mot_dans_le_descriptif
                    OR label_contient_mot
                    OR infix_dans_label
                    OR similarite_label_decoupe > 0.4)
                  AND numero_ligne_label = 1;                    
                """.trimIndent(),
                RechercheMetierEntity::class.java,
            )
                .setParameter("mot_recherche", motRecherche)
                .setParameter("mot_recherche_infix", "$motRecherche%")
                .setParameter("regex_non_alpha_numeric_avec_accent", REGEX_NON_ALPHA_NUMERIC_AVEC_ACCENT)
                .resultList
        return resulat.map { (it as RechercheMetierEntity).toResultatRechercheMetierCourt() }
    }
}
