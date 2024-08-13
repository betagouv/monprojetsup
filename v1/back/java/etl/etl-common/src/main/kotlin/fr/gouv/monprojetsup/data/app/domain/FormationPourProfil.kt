package fr.gouv.monprojetsup.data.app.domain

data class FormationPourProfil(
    val id: String,
    val nom: String,
    val tauxAffinite: Float,
    val metiersTriesParAffinites: List<String>,
    val communesTrieesParAffinites: List<String>,
)
