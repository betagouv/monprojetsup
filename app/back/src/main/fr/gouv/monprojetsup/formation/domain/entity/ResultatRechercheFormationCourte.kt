package fr.gouv.monprojetsup.formation.domain.entity

data class ResultatRechercheFormationCourte(
    val formation: FormationCourte,
    val scoreLabel: ScoreMot?,
    val scoreMotClef: ScoreMot?,
) {
    data class ScoreMot(
        val mot: String,
        val motExact: Boolean,
        val motExactPresentDebutPhrase: Boolean,
        val motExactPresentFin: Boolean,
        val motExactMilieu: Boolean,
        val motEnPrefix: Boolean,
        val pourcentageMot: Int,
    )
}
