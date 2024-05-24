package fr.gouv.monprojetsup.recherche.domain.entity

data class ProfilEleve(
    val id: String,
    val classe: String?,
    val bac: String?,
    val specialites: List<String>?,
    val domainesInterets: List<String>?,
    val centresInterets: List<String>?,
    val metiersChoisis: List<String>?,
    val dureeEtudesPrevue: String?,
    val alternance: String?,
    val villesPreferees: List<String>?,
    val formationsChoisies: List<String>?,
    val moyenneGenerale: Float?,
)
