package fr.gouv.monprojetsup.referentiel.domain.entity

data class Interet(
    val id: String,
    val nom: String,
)

data class InteretSousCategorie(
    val id: String,
    val nom: String,
    val description: String?,
    val emoji: String,
) {
    val label: Label
        get() = Label(id, nom)
}

data class InteretCategorie(
    val id: String,
    val nom: String,
    val emoji: String,
)
