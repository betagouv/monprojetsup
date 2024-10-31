package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.commun.Constantes.REGEX_NON_ALPHA_NUMERIC_AVEC_ACCENT
import fr.gouv.monprojetsup.formation.domain.entity.ResultatRechercheFormationCourte
import fr.gouv.monprojetsup.formation.domain.port.RechercheFormationRepository
import fr.gouv.monprojetsup.formation.infrastructure.entity.RechercheFormationEntity
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class RechercheFormationBDDRepository(
    val entityManager: EntityManager,
) : RechercheFormationRepository {
    @Transactional(readOnly = true)
    override fun rechercherUneFormation(motRecherche: String): List<ResultatRechercheFormationCourte> {
        val resulat =
            entityManager.createNativeQuery(
                """
                WITH expanded_keywords AS (SELECT id,
                                                  label,
                                                  descriptif_general,
                                                  descriptif_attendu,
                                                  descriptif_conseils,
                                                  descriptif_diplome,
                                                  mots_clefs,
                                                  t.i as mot_clef
                                           FROM ref_formation
                                                    LEFT JOIN LATERAL unnest(mots_clefs) AS t(i) ON true
                                           WHERE obsolete = false),
                     scores_keywords AS (SELECT id,
                                                label,
                                                mot_clef,
                                                unaccent(lower(mot_clef)) LIKE unaccent(lower(:mot_recherche_strict))                                AS mot_cle_exact,
                                                unaccent(lower(mot_clef)) LIKE
                                                unaccent(lower(:mot_recherche_en_debut_de_phrase))                                                   AS mot_cle_exact_debut,
                                                unaccent(lower(mot_clef)) LIKE
                                                unaccent(lower(:mot_recherche_en_fin_de_phrase))                                                     AS mot_cle_exact_fin,
                                                unaccent(lower(mot_clef)) LIKE unaccent(lower(:mot_recherche_inclus_dans_une_phrase)) OR
                                                unaccent(lower(mot_clef)) LIKE
                                                unaccent(lower(:mot_recherche_strict_entre_parentheses))                                             AS mot_cle_exact_milieu,
                                                unaccent(lower(mot_clef)) LIKE unaccent(lower(:mot_recherche_inclus_prefix))                                AS mot_cle_en_prefix,
                                                100 * similarity(unaccent(lower(mot_clef)),
                                                                 unaccent(lower(:mot_recherche_strict)))                                             AS pourcentage_mot_cle,
                                                ROW_NUMBER() OVER (PARTITION BY id ORDER BY 100 *
                                                                                            similarity(unaccent(lower(mot_clef)),
                                                                                                       unaccent(lower(:mot_recherche_strict))) DESC) AS numero_ligne_keyword
                                         FROM expanded_keywords
                                         WHERE unaccent(lower(mot_clef)) LIKE unaccent(lower(:mot_recherche_inclus_prefix))
                                            OR similarity(unaccent(lower(mot_clef)), unaccent(lower(:mot_recherche_strict))) > 0.3),
                     expanded_label as (SELECT id,
                                               label,
                                               regexp_split_to_table(label, :regex_non_alpha_numeric_avec_accent) as label_decoupe
                                        FROM ref_formation
                                        WHERE obsolete = false),
                     scores_label AS (SELECT id,
                                             label,
                                             label_decoupe,
                                             unaccent(lower(label)) LIKE unaccent(lower(:mot_recherche_strict))                                   AS label_exact,
                                             unaccent(lower(label)) LIKE
                                             unaccent(lower(:mot_recherche_en_debut_de_phrase))                                                   AS label_exact_debut,
                                             unaccent(lower(label)) LIKE unaccent(lower(:mot_recherche_en_fin_de_phrase))                         AS label_exact_fin,
                                             unaccent(lower(label)) LIKE unaccent(lower(:mot_recherche_inclus_dans_une_phrase)) OR
                                             unaccent(lower(label)) LIKE
                                             unaccent(lower(:mot_recherche_strict_entre_parentheses))                                             AS label_exact_milieu,
                                             unaccent(lower(label)) LIKE unaccent(lower(:mot_recherche_inclus_prefix))                                   AS label_en_prefix,
                                             100 *
                                             similarity(unaccent(lower(label_decoupe)),
                                                        unaccent(lower(:mot_recherche_strict)))                                                   AS pourcentage_label_decoupe,
                                             ROW_NUMBER() OVER (PARTITION BY id ORDER BY 100 *
                                                                                         similarity(unaccent(lower(label_decoupe)),
                                                                                                    unaccent(lower(:mot_recherche_strict))) DESC) AS numero_ligne_label
                                      FROM expanded_label
                                      WHERE unaccent(lower(label)) LIKE unaccent(lower(:mot_recherche_inclus_prefix))
                                         OR similarity(unaccent(lower(label_decoupe)), unaccent(lower(:mot_recherche_strict))) > 0.3)
                SELECT scores_label.id       AS scores_label_id,
                       scores_label.label    AS scores_label_label,
                       scores_keywords.id    AS scores_keywords_id,
                       scores_keywords.label AS scores_keywords_label,
                       label_decoupe,
                       label_exact,
                       label_exact_debut,
                       label_exact_fin,
                       label_exact_milieu,
                       label_en_prefix,
                       pourcentage_label_decoupe,
                       mot_clef,
                       mot_cle_exact,
                       mot_cle_exact_debut,
                       mot_cle_exact_fin,
                       mot_cle_exact_milieu,
                       mot_cle_en_prefix,
                       pourcentage_mot_cle
                FROM scores_label
                         FULL JOIN scores_keywords ON scores_label.id = scores_keywords.id
                WHERE (numero_ligne_keyword = 1 OR numero_ligne_keyword IS NULL)
                  AND (numero_ligne_label = 1 OR numero_ligne_label IS NULL)
                ORDER BY pourcentage_label_decoupe DESC NULLS LAST, scores_label.id, scores_keywords.id;
                """.trimIndent(),
                RechercheFormationEntity::class.java,
            )
                .setParameter("mot_recherche_strict", motRecherche)
                .setParameter("mot_recherche_en_debut_de_phrase", "$motRecherche %")
                .setParameter("mot_recherche_en_fin_de_phrase", "% $motRecherche")
                .setParameter("mot_recherche_inclus_dans_une_phrase", "% $motRecherche %")
                .setParameter("mot_recherche_strict_entre_parentheses", "%($motRecherche)%")
                .setParameter("mot_recherche_inclus_prefix", "%( |^|[''()\\ -])$motRecherche%")
                .setParameter("regex_non_alpha_numeric_avec_accent", REGEX_NON_ALPHA_NUMERIC_AVEC_ACCENT)
                .resultList
        return resulat.map { (it as RechercheFormationEntity).toRechercheFormationCourte() }
    }
}
