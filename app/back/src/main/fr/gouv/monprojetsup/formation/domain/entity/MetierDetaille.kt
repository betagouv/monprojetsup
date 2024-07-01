package fr.gouv.monprojetsup.formation.domain.entity

data class MetierDetaille(
    val id: String,
    val nom: String,
    val descriptif: String?,
    val liens: List<Lien>,
)
