package fr.gouv.monprojetsup.data.app.domain

data class MetierDetaille(
    val id: String,
    val nom: String,
    val descriptif: String?,
    val liens: List<Lien>,
)
