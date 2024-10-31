package fr.gouv.monprojetsup.metier.domain.entity

data class ResultatRechercheMetierCourt(
    val metier: MetierCourt,
    val score: ScoreMot,
) {
    data class ScoreMot(
        val motDansLeDescriptif: Boolean,
        val labelContientMot: Boolean,
        val prefixDansLabel: Boolean,
        val similariteLabelDecoupe: Int,
    )
}
