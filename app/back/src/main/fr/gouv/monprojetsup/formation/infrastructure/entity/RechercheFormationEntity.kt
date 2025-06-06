package fr.gouv.monprojetsup.formation.infrastructure.entity

import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import fr.gouv.monprojetsup.formation.domain.entity.ResultatRechercheFormationCourte
import jakarta.persistence.Column

data class RechercheFormationEntity(
    @Column(name = "scores_label_id", nullable = true)
    val scoresLabelId: String?,
    @Column(name = "scores_label_label", nullable = true)
    val scoresLabelLabel: String?,
    @Column(name = "scores_keywords_id", nullable = true)
    val scoresKeywordsId: String?,
    @Column(name = "scores_keywords_label", nullable = true)
    val scoresKeywordsLabel: String?,
    @Column(name = "label_decoupe", nullable = true)
    val labelDecoupe: String?,
    @Column(name = "label_exact", nullable = true)
    val labelExact: Boolean?,
    @Column(name = "label_exact_debut", nullable = true)
    val labelExactDebut: Boolean?,
    @Column(name = "label_exact_fin", nullable = true)
    val labelExactFin: Boolean?,
    @Column(name = "label_exact_milieu", nullable = true)
    val labelExactMilieu: Boolean?,
    @Column(name = "label_en_prefix", nullable = true)
    val labelEnPrefix: Boolean?,
    @Column(name = "pourcentage_label_decoupe", nullable = true)
    val pourcentageLabelDecoupe: Double?,
    @Column(name = "mot_clef", nullable = true)
    val motClef: String?,
    @Column(name = "mot_cle_exact", nullable = true)
    val motCleExact: Boolean?,
    @Column(name = "mot_cle_exact_debut", nullable = true)
    val motCleExactDebut: Boolean?,
    @Column(name = "mot_cle_exact_fin", nullable = true)
    val motCleExactFin: Boolean?,
    @Column(name = "mot_cle_exact_milieu", nullable = true)
    val motCleExactMilieu: Boolean?,
    @Column(name = "mot_cle_en_prefix", nullable = true)
    val motCleEnPrefix: Boolean?,
    @Column(name = "pourcentage_mot_cle", nullable = true)
    val pourcentageMotCle: Double?,
) {
    fun toRechercheFormationCourte() =
        ResultatRechercheFormationCourte(
            formation =
                scoresLabelId?.let {
                    FormationCourte(it, scoresLabelLabel!!)
                } ?: FormationCourte(scoresKeywordsId!!, scoresKeywordsLabel!!),
            scoreLabel =
                labelDecoupe?.let {
                    ResultatRechercheFormationCourte.ScoreMot(
                        mot = it,
                        motExact = labelExact!!,
                        motExactPresentDebutPhrase = labelExactDebut!!,
                        motExactPresentFin = labelExactFin!!,
                        motExactMilieu = labelExactMilieu!!,
                        motEnPrefix = labelEnPrefix!!,
                        pourcentageMot = pourcentageLabelDecoupe!!.toInt(),
                    )
                },
            scoreMotClef =
                motClef?.let {
                    ResultatRechercheFormationCourte.ScoreMot(
                        mot = it,
                        motExact = motCleExact!!,
                        motExactPresentDebutPhrase = motCleExactDebut!!,
                        motExactPresentFin = motCleExactFin!!,
                        motExactMilieu = motCleExactMilieu!!,
                        motEnPrefix = motCleEnPrefix!!,
                        pourcentageMot = pourcentageMotCle!!.toInt(),
                    )
                },
        )
}
