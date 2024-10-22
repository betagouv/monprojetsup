package fr.gouv.monprojetsup.metier.domain.entity

data class ResultatRechercheMetierCourt(
    val metier: MetierCourt,
    val score: ScoreMot,
) {
    data class ScoreMot(
        val motDansLeDescriptif: Boolean,
        val labelContientMot: Boolean,
        val infixDansLabel: Boolean,
        val similariteLabelDecoupe: Int,
    )
}
