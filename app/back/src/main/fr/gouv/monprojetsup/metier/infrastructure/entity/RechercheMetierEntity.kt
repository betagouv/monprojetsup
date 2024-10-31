package fr.gouv.monprojetsup.metier.infrastructure.entity

import fr.gouv.monprojetsup.metier.domain.entity.MetierCourt
import fr.gouv.monprojetsup.metier.domain.entity.ResultatRechercheMetierCourt
import jakarta.persistence.Column

data class RechercheMetierEntity(
    @Column(name = "id", nullable = false)
    val id: String,
    @Column(name = "label", nullable = false)
    val label: String,
    @Column(name = "mot_dans_le_descriptif", nullable = false)
    val motDansLeDescriptif: Boolean,
    @Column(name = "label_contient_mot", nullable = false)
    val labelContientMot: Boolean,
    @Column(name = "prefix_dans_label", nullable = false)
    val prefixDansLabel: Boolean,
    @Column(name = "similarite_label_decoupe", nullable = false)
    val similariteLabelDecoupe: Double,
) {
    fun toResultatRechercheMetierCourt() =
        ResultatRechercheMetierCourt(
            metier = MetierCourt(id = id, nom = label),
            score =
                ResultatRechercheMetierCourt.ScoreMot(
                    motDansLeDescriptif = motDansLeDescriptif,
                    labelContientMot = labelContientMot,
                    prefixDansLabel = prefixDansLabel,
                    similariteLabelDecoupe = (similariteLabelDecoupe * 100).toInt(),
                ),
        )
}
