package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.formation.domain.entity.ResultatRechercheFormationCourte
import fr.gouv.monprojetsup.formation.domain.port.RechercheFormationRepository
import fr.gouv.monprojetsup.formation.infrastructure.entity.RechercheFormationEntity
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import java.text.Normalizer
import java.util.Locale
import java.util.regex.Pattern

@Repository
class RechercheFormationBDDRepository(
    val entityManager: EntityManager,
) : RechercheFormationRepository {
    fun removeAccents(input: String): String {
        val normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(normalized).replaceAll("")
    }

    override fun rechercherUneFormation(motRecherche: String): List<ResultatRechercheFormationCourte> {
        val motRechercheSansAccents = removeAccents(motRecherche.lowercase(Locale.getDefault()))
        val resultat =
            entityManager.createNativeQuery(
                """
                WITH 
                     scores_keywords AS (SELECT id,
                                                label,
                                                mot_clef,
                                                mot_clef LIKE :mot_recherche_strict                                AS mot_cle_exact,
                                                mot_clef LIKE :mot_recherche_en_debut_de_phrase                                                  AS mot_cle_exact_debut,
                                                mot_clef LIKE :mot_recherche_en_fin_de_phrase                                                     AS mot_cle_exact_fin,
                                                mot_clef LIKE :mot_recherche_inclus_dans_une_phrase OR
                                                mot_clef ~ :mot_recherche_strict_entre_parentheses                                             AS mot_cle_exact_milieu,
                                                mot_clef ~ :mot_recherche_inclus_prefix                               AS mot_cle_en_prefix,
                                                100 * similarity(mot_clef,
                                                                 :mot_recherche_strict)                                             AS pourcentage_mot_cle,
                                                ROW_NUMBER() OVER (PARTITION BY id ORDER BY 100 *
                                                                                            similarity(mot_clef,
                                                                                                       :mot_recherche_strict) DESC) AS numero_ligne_keyword
                                         FROM expanded_keywords
                                         WHERE mot_clef ~ :mot_recherche_inclus_prefix
                                            OR similarity(mot_clef, :mot_recherche_strict) > 0.3),
                     scores_label AS (SELECT id,
                                             label,
                                             label_decoupe,
                                             label_sans_accents LIKE :mot_recherche_strict                                   AS label_exact,
                                             label_sans_accents LIKE :mot_recherche_en_debut_de_phrase                       AS label_exact_debut,
                                             label_sans_accents LIKE :mot_recherche_en_fin_de_phrase                         AS label_exact_fin,
                                                label_sans_accents LIKE :mot_recherche_inclus_dans_une_phrase 
                                             OR
                                                 label_sans_accents ~ :mot_recherche_strict_entre_parentheses                                             
                                                                                                                AS label_exact_milieu,
                                             label_sans_accents ~ :mot_recherche_inclus_prefix                            AS label_en_prefix,
                                             100 *  similarity(label_decoupe, :mot_recherche_strict)            AS pourcentage_label_decoupe,
                                             ROW_NUMBER() OVER (PARTITION BY id ORDER BY 100 *
                                                                                         similarity(label_decoupe,
                                                                                                    :mot_recherche_strict) DESC) 
                                                                                                                AS numero_ligne_label
                                      FROM expanded_label
                                      WHERE 
                                      (
                                           label_sans_accents ~ :mot_recherche_inclus_prefix
                                           OR similarity(label_decoupe, :mot_recherche_strict) > 0.3)
                                          )
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
                .setParameter("mot_recherche_strict", motRechercheSansAccents)
                .setParameter("mot_recherche_en_debut_de_phrase", "$motRechercheSansAccents %")
                .setParameter("mot_recherche_en_fin_de_phrase", "% $motRechercheSansAccents")
                .setParameter("mot_recherche_inclus_dans_une_phrase", "% $motRechercheSansAccents %")
                .setParameter("mot_recherche_strict_entre_parentheses", "%($motRechercheSansAccents)%")
                .setParameter("mot_recherche_inclus_prefix", "(^| |[''()\\ -])$motRechercheSansAccents")
                .resultList
        return resultat.map { (it as RechercheFormationEntity).toRechercheFormationCourte() }
    }
}
