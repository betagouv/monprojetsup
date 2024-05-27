package fr.gouv.monprojetsup.recherche.application.dto

data class RecupererFormationReponseDTO(
    val formation: FormationDetailleDTO,
    val explications: ExplicationsDTO?,
)

data class FormationDetailleDTO(
    val id: String,
    val nom: String,
    val formationsAssociees: List<String>?,
    val detailFormation: String?,
    val detailDiplome: String?,
    val criteresAdmission: CriteresAdmissionDTO?,
    val conseils: String?,
    val liens: List<String>?,
    val villes: List<String>?,
    val metiers: List<MetierDetailleDTO>?,
    val tauxAffinite: Float?,
)

data class MetierDetailleDTO(
    val id: String,
    val nom: String,
    val descriptif: String?,
    val lienOnisep: String?,
)

data class ExplicationsDTO(
    val geographique: List<ExplicationGeographiqueDTO>?,
    val similaires: List<String>?,
    val dureeEtudesPrevue: String?,
    val alternance: String?,
    val interets: String?,
)

data class ExplicationGeographiqueDTO(
    val nom: String,
    val distanceKm: Int,
)

data class CriteresAdmissionDTO(
    val principauxPoints: List<String>?,
    val moyenneGenerale: MoyenneGeneraleDTO?,
    val attenduFormation: String?,
)

data class MoyenneGeneraleDTO(
    val centille5eme: Float,
    val centille25eme: Float,
    val centille75eme: Float,
    val centille95eme: Float,
)
