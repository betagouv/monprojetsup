package fr.gouv.monprojetsup.recherche.domain.entity

sealed class FicheFormation(
    open val id: String,
    open val nom: String,
    open val descriptifGeneral: String?,
    open val descriptifAttendus: String?,
    open val descriptifDiplome: String?,
    open val descriptifConseils: String?,
    open val formationsAssociees: List<String>?,
    open val liens: List<String>?,
    open val metiers: List<MetierDetaille>,
    open val communes: List<String>,
    open val criteresAnalyseCandidature: List<CriteresAnalyseCandidature>,
) {
    data class FicheFormationSansProfil(
        override val id: String,
        override val nom: String,
        override val descriptifGeneral: String?,
        override val descriptifAttendus: String?,
        override val descriptifDiplome: String?,
        override val descriptifConseils: String?,
        override val formationsAssociees: List<String>?,
        override val liens: List<String>?,
        override val metiers: List<MetierDetaille>,
        override val communes: List<String>,
        override val criteresAnalyseCandidature: List<CriteresAnalyseCandidature>,
    ) : FicheFormation(
            id = id,
            nom = nom,
            descriptifGeneral = descriptifGeneral,
            descriptifAttendus = descriptifAttendus,
            descriptifDiplome = descriptifDiplome,
            descriptifConseils = descriptifConseils,
            formationsAssociees = formationsAssociees,
            liens = liens,
            metiers = metiers,
            communes = communes,
            criteresAnalyseCandidature = criteresAnalyseCandidature,
        )

    data class FicheFormationPourProfil(
        override val id: String,
        override val nom: String,
        override val descriptifGeneral: String?,
        override val descriptifAttendus: String?,
        override val descriptifDiplome: String?,
        override val descriptifConseils: String?,
        override val formationsAssociees: List<String>?,
        override val liens: List<String>?,
        override val criteresAnalyseCandidature: List<CriteresAnalyseCandidature>,
        val tauxAffinite: Int,
        val metiersTriesParAffinites: List<MetierDetaille>,
        val communesTrieesParAffinites: List<String>,
        val explications: ExplicationsSuggestionDetaillees,
    ) : FicheFormation(
            id = id,
            nom = nom,
            descriptifGeneral = descriptifGeneral,
            descriptifAttendus = descriptifAttendus,
            descriptifDiplome = descriptifDiplome,
            descriptifConseils = descriptifConseils,
            formationsAssociees = formationsAssociees,
            liens = liens,
            metiers = metiersTriesParAffinites,
            communes = communesTrieesParAffinites,
            criteresAnalyseCandidature = criteresAnalyseCandidature,
        ) {
        data class ExplicationAutoEvaluationMoyenne(
            val baccalaureatUtilise: Baccalaureat,
            val moyenneAutoEvalue: Float,
            val basIntervalleNotes: Float,
            val hautIntervalleNotes: Float,
        )

        data class ExplicationTypeBaccalaureat(
            val baccalaureat: Baccalaureat,
            val pourcentage: Int,
        )

        data class MoyenneGeneraleDesAdmis(
            val baccalaureat: Baccalaureat?,
            val centiles: List<Centile>,
        ) {
            data class Centile(
                val centile: Int,
                val note: Float,
            )
        }
    }
}
