package fr.gouv.monprojetsup.data.app.domain

data class FormationDetaillee(
    val id: String,
    val nom: String,
    val descriptifGeneral: String?,
    val descriptifAttendus: String?,
    val descriptifDiplome: String?,
    val descriptifConseils: String?,
    val formationsAssociees: List<String>,
    val liens: List<Lien>,
    val valeurCriteresAnalyseCandidature: List<Int>,
    val metiers: List<MetierDetaille>,
)
