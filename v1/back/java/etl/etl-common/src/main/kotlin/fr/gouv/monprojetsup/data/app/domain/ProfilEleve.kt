package fr.gouv.monprojetsup.data.app.domain

data class ProfilEleve(
    val id: String,
    val classe: ChoixNiveau,
    val bac: String?,
    val specialites: List<String>?,
    val domainesInterets: List<String>?,
    val centresInterets: List<String>?,
    val metiersChoisis: List<String>?,
    val dureeEtudesPrevue: ChoixDureeEtudesPrevue,
    val alternance: fr.gouv.monprojetsup.data.app.domain.ChoixAlternance,
    val villesPreferees: List<String>?,
    val formationsChoisies: List<String>?,
    val moyenneGenerale: Float?,
)
