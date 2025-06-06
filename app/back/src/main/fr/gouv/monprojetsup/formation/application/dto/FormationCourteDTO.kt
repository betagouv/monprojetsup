package fr.gouv.monprojetsup.formation.application.dto

import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte

data class FormationCourteDTO(
    val id: String,
    val nom: String,
) {
    @Suppress("unused")
    private constructor() : this(id = "", nom = "")

    constructor(formationCourte: FormationCourte) : this(id = formationCourte.id, nom = formationCourte.nom)
}
