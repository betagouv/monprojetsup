package fr.gouv.monprojetsup.recherche.domain.entity

data class FormationPourProfil(
    val id: String,
    val nom: String,
    val tauxAffinite: Float,
    val metiersTriesParAffinites: List<String>,
    val communesTrieesParAffinites: List<String>,
)
