package fr.gouv.monprojetsup.recherche.domain.entity

data class FormationDetaillee(
    val id: String,
    val nom: String,
    val descriptifGeneral: String?,
    val descriptifAttendus: String?,
    val descriptifDiplome: String?,
    val descriptifConseils: String?,
    val pointsAttendus: List<String>?,
    val formationsAssociees: List<String>?,
    val liens: List<String>?,
    val valeurCriteresAnalyseCandidature: List<Int>,
    val metiers: List<MetierDetaille>,
)
