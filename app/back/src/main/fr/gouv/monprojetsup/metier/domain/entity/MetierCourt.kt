package fr.gouv.monprojetsup.metier.domain.entity

import fr.gouv.monprojetsup.referentiel.domain.entity.Label

data class MetierCourt(
    val id: String,
    val nom: String,
) {
    fun toLabel() = Label(id, nom)
}
