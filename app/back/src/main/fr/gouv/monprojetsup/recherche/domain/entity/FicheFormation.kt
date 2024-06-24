package fr.gouv.monprojetsup.recherche.domain.entity

sealed class FicheFormation(
    open val formation: FormationDetaillee,
    open val communes: List<String>,
    open val criteresAnalyseCandidature: List<CriteresAnalyseCandidature>,
    open val tauxAffinite: Int?,
) {
    data class FicheFormationSansProfil(
        override val formation: FormationDetaillee,
        override val communes: List<String>,
        override val criteresAnalyseCandidature: List<CriteresAnalyseCandidature>,
    ) : FicheFormation(
            formation = formation,
            tauxAffinite = null,
            communes = communes,
            criteresAnalyseCandidature = criteresAnalyseCandidature,
        )

    data class FicheFormationPourProfil(
        override val formation: FormationDetaillee,
        override val tauxAffinite: Int?,
        override val criteresAnalyseCandidature: List<CriteresAnalyseCandidature>,
        val moyenneGeneraleDesAdmis: MoyenneGeneraleDesAdmis?,
        val explications: ExplicationsSuggestion?,
        val formationsSimilaires: List<Formation>?,
        val interets: List<InteretSousCategorie>?,
        val domaines: List<Domaine>?,
        val explicationAutoEvaluationMoyenne: ExplicationAutoEvaluationMoyenne?,
        val explicationTypeBaccalaureat: ExplicationTypeBaccalaureat?,
        val metiersTriesParAffinites: List<MetierDetaille>,
        val communesTrieesParAffinites: List<String>,
    ) : FicheFormation(
            formation = formation.copy(metiers = metiersTriesParAffinites),
            tauxAffinite = tauxAffinite,
            communes = communesTrieesParAffinites,
            criteresAnalyseCandidature = criteresAnalyseCandidature,
        ) {
        data class ExplicationAutoEvaluationMoyenne(
            val baccalaureat: Baccalaureat,
            val moyenneAutoEvalue: Float,
            val basIntervalleNotes: Float,
            val hautIntervalleNotes: Float,
        )

        data class ExplicationTypeBaccalaureat(
            val baccalaureat: Baccalaureat,
            val pourcentage: Int,
        )

        data class MoyenneGeneraleDesAdmis(
            val idBaccalaureat: String?,
            val nomBaccalaureat: String?,
            val centiles: List<Centile>,
        ) {
            data class Centile(
                val centile: Int,
                val note: Float,
            )
        }
    }
}
