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
                    WITH expanded_keywords AS (SELECT id,
                                                      label,
                                                      descriptif_general,
                                                      descriptif_attendu,
                                                      descriptif_conseils,
                                                      descriptif_diplome,
                                                      mots_clefs,
                                                      t.i as mot_clef
                                               FROM ref_formation
                                                        LEFT JOIN LATERAL unnest(mots_clefs) AS t(i) ON true),
                         scores AS (SELECT id,
                                           label,
                                           mot_clef,
                                           unaccent(lower(label)) LIKE unaccent(lower(:mot_recherche_en_debut_de_phrase))                AS mot_exact_debut_label,
                                           unaccent(lower(label)) LIKE unaccent(lower(:mot_recherche_en_fin_de_phrase))                AS mot_exact_fin_label,
                                           unaccent(lower(label)) LIKE unaccent(lower(:mot_recherche_inclus_dans_une_phrase)) OR
                                           unaccent(lower(label)) LIKE unaccent(lower(:mot_recherche_strict_entre_parentheses))              AS mot_exact_milieu_label,
                                           unaccent(lower(label)) LIKE unaccent(lower(:mot_recherche_inclus_dans_un_mot))                as sequence_presente_label,
                                           100 * similarity(unaccent(lower(label)), unaccent(lower(:mot_recherche_strict)))    AS pourcentage_label,
                                           unaccent(lower(mot_clef)) LIKE unaccent(lower(:mot_recherche_strict))               AS mot_cle_exact,
                                           unaccent(lower(mot_clef)) LIKE unaccent(lower(:mot_recherche_en_debut_de_phrase))             AS mot_cle_exact_debut,
                                           unaccent(lower(mot_clef)) LIKE unaccent(lower(:mot_recherche_en_fin_de_phrase))             AS mot_cle_exact_fin,
                                           unaccent(lower(mot_clef)) LIKE unaccent(lower(:mot_recherche_inclus_dans_une_phrase)) OR
                                           unaccent(lower(mot_clef)) LIKE unaccent(lower(:mot_recherche_strict_entre_parentheses))           AS mot_cle_exact_milieu,
                                           unaccent(lower(mot_clef)) LIKE unaccent(lower(:mot_recherche_inclus_dans_un_mot))             as sequence_presente_mot_cle,
                                           100 * similarity(unaccent(lower(mot_clef)), unaccent(lower(:mot_recherche_strict))) AS pourcentage_mot_cle
                                    FROM expanded_keywords
                                    WHERE unaccent(lower(label)) LIKE unaccent(lower(:mot_recherche_inclus_dans_un_mot))
                                       OR unaccent(lower(mot_clef)) LIKE unaccent(lower(:mot_recherche_inclus_dans_un_mot))
                                       OR similarity(unaccent(lower(label)), unaccent(lower(:mot_recherche_strict))) > 0.2
                                       OR similarity(unaccent(lower(mot_clef)), unaccent(lower(:mot_recherche_strict))) > 0.3
                                    ORDER BY pourcentage_label DESC)
                    SELECT id,
                           label,
                           mot_clef,
                           mot_exact_debut_label,
                           mot_exact_fin_label,
                           mot_exact_milieu_label,
                           sequence_presente_label,
                           pourcentage_label,
                           mot_cle_exact,
                           mot_cle_exact_debut,
                           mot_cle_exact_fin,
                           mot_cle_exact_milieu,
                           sequence_presente_mot_cle,
                           pourcentage_mot_cle
                    FROM (SELECT *, ROW_NUMBER() OVER (PARTITION BY id ORDER BY pourcentage_mot_cle DESC) AS numero_ligne
                          FROM scores) t
                    WHERE numero_ligne = 1;
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
