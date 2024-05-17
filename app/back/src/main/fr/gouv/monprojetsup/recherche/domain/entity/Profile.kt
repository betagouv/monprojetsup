package fr.gouv.monprojetsup.recherche.domain.entity

data class Profile(
    val niveau: String,
    val bac: String,
    val duree: String,
    val apprentissage: String,
    val preferencesGeographique: List<String>,
    val specialites: List<String>,
    val interets: List<String>,
    val moyenneGenerale: Float,
    val choix: List<Suggestion>,
)
