package fr.gouv.monprojetsup.recherche.domain.entity

sealed class FicheFormation(
    open val id: String,
    open val nom: String,
    open val formationsAssociees: List<String>?,
    open val detailFormation: String?,
    open val detailDiplome: String?,
    open val criteresAdmission: CriteresAdmission?,
    open val conseils: String?,
    open val liens: List<String>?,
    open val tauxAffinite: Float?,
    open val metiers: List<MetierDetaille>,
    open val communes: List<String>,
    open val explications: ExplicationsSuggestion?,
) {
    data class FicheFormationPourProfil(
        override val id: String,
        override val nom: String,
        override val formationsAssociees: List<String>?,
        override val detailFormation: String?,
        override val detailDiplome: String?,
        override val criteresAdmission: CriteresAdmission?,
        override val conseils: String?,
        override val liens: List<String>?,
        override val tauxAffinite: Float,
        val metiersTriesParAffinites: List<MetierDetaille>,
        val communesTrieesParAffinites: List<String>,
        override val explications: ExplicationsSuggestion?,
    ) : FicheFormation(
            id = id,
            nom = nom,
            formationsAssociees = formationsAssociees,
            detailFormation = detailFormation,
            detailDiplome = detailDiplome,
            criteresAdmission = criteresAdmission,
            conseils = conseils,
            liens = liens,
            tauxAffinite = tauxAffinite,
            metiers = metiersTriesParAffinites,
            communes = communesTrieesParAffinites,
            explications = explications,
        )

    data class FicheFormationSansProfil(
        override val id: String,
        override val nom: String,
        override val formationsAssociees: List<String>?,
        override val detailFormation: String?,
        override val detailDiplome: String?,
        override val criteresAdmission: CriteresAdmission?,
        override val conseils: String?,
        override val liens: List<String>?,
        override val metiers: List<MetierDetaille>,
        override val communes: List<String>,
    ) : FicheFormation(
            id = id,
            nom = nom,
            formationsAssociees = formationsAssociees,
            detailFormation = detailFormation,
            detailDiplome = detailDiplome,
            criteresAdmission = criteresAdmission,
            conseils = conseils,
            liens = liens,
            tauxAffinite = null,
            metiers = metiers,
            communes = communes,
            explications = null,
        )
}

data class ExplicationsSuggestion(
    val geographique: List<ExplicationGeographique>?,
    val similaires: List<String>?,
    val dureeEtudesPrevue: String?,
    val alternance: String?,
    val interets: String?,
)

data class ExplicationGeographique(
    val ville: String,
    val distanceKm: Int,
)

data class CriteresAdmission(
    val principauxPoints: List<String>?,
    val moyenneGenerale: MoyenneGenerale?,
    val attenduFormation: String?,
)

data class MoyenneGenerale(
    val centille5eme: Float,
    val centille25eme: Float,
    val centille75eme: Float,
    val centille95eme: Float,
)
