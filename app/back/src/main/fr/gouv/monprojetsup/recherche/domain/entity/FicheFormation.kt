package fr.gouv.monprojetsup.recherche.domain.entity

sealed class FicheFormation(
    open val id: String,
    open val nom: String,
    open val formationsAssociees: List<String>?,
    open val descriptifFormation: String?,
    open val descriptifDiplome: String?,
    open val descriptifAttendus: String?,
    open val descriptifConseils: String?,
    open val criteresAdmission: CriteresAdmission?,
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
        override val descriptifFormation: String?,
        override val descriptifDiplome: String?,
        override val descriptifAttendus: String?,
        override val descriptifConseils: String?,
        override val criteresAdmission: CriteresAdmission?,
        override val liens: List<String>?,
        override val tauxAffinite: Float,
        val metiersTriesParAffinites: List<MetierDetaille>,
        val communesTrieesParAffinites: List<String>,
        override val explications: ExplicationsSuggestion?,
    ) : FicheFormation(
            id = id,
            nom = nom,
            formationsAssociees = formationsAssociees,
            descriptifFormation = descriptifFormation,
            descriptifDiplome = descriptifDiplome,
            descriptifConseils = descriptifConseils,
            descriptifAttendus = descriptifAttendus,
            criteresAdmission = criteresAdmission,
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
        override val descriptifFormation: String?,
        override val descriptifDiplome: String?,
        override val descriptifAttendus: String?,
        override val descriptifConseils: String?,
        override val criteresAdmission: CriteresAdmission?,
        override val liens: List<String>?,
        override val metiers: List<MetierDetaille>,
        override val communes: List<String>,
    ) : FicheFormation(
            id = id,
            nom = nom,
            formationsAssociees = formationsAssociees,
            descriptifFormation = descriptifFormation,
            descriptifDiplome = descriptifDiplome,
            descriptifConseils = descriptifConseils,
            descriptifAttendus = descriptifAttendus,
            criteresAdmission = criteresAdmission,
            liens = liens,
            tauxAffinite = null,
            metiers = metiers,
            communes = communes,
            explications = null,
        )
}

data class ExplicationsSuggestion(
    val geographique: List<ExplicationGeographique>? = null,
    val formationsSimilaires: List<String>? = null,
    val dureeEtudesPrevue: ChoixDureeEtudesPrevue? = null,
    val alternance: ChoixAlternance? = null,
    val interets: List<String>? = null,
    val specialitesChoisies: Map<String, Double>? = null,
    val typeBaccalaureat: TypeBaccalaureat? = null,
    val autoEvaluationMoyenne: AutoEvaluationMoyenne? = null,
    val tags: List<Tag>? = null,
    val tagsCourts: List<String>? = null,
)

data class Tag(
    val noeuds: List<String>?,
    val poid: Double?,
)

data class AutoEvaluationMoyenne(
    val moyenne: Double?,
    val mediane: Centilles?,
    val bacUtilise: String?,
)

data class TypeBaccalaureat(
    val nomBaccalaureat: String?,
    val pourcentage: Int?,
)

data class Centilles(
    val rangEch25: Int?,
    val rangEch50: Int?,
    val rangEch75: Int?,
    val rangEch10: Int?,
    val rangEch90: Int?,
)

data class ExplicationGeographique(
    val ville: String,
    val distanceKm: Int,
)

data class CriteresAdmission(
    val principauxPoints: List<String>?,
    val moyenneGenerale: MoyenneGenerale?,
)

data class MoyenneGenerale(
    val centille5eme: Float,
    val centille25eme: Float,
    val centille75eme: Float,
    val centille95eme: Float,
)
