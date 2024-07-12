package fr.gouv.monprojetsup.formation.domain.entity

data class ProfilEleve(
    val id: String,
    val classe: ChoixNiveau,
    val bac: String?,
    val specialites: List<String>?,
    val domainesInterets: List<String>?,
    val centresInterets: List<String>?,
    val metiersChoisis: List<String>?,
    val dureeEtudesPrevue: ChoixDureeEtudesPrevue,
    val alternance: ChoixAlternance,
    val communesPreferees: List<Commune>?,
    val formationsChoisies: List<String>?,
    val moyenneGenerale: Float?,
)
