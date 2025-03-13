package fr.gouv.monprojetsup.formation.domain.entity

import fr.gouv.monprojetsup.referentiel.domain.entity.Label

data class FormationCourte(
    val id: String,
    val nom: String,
) {
    val label: Label
        get() = Label(id, nom)
}
