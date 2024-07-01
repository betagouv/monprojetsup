package fr.gouv.monprojetsup.formation.application.dto

import fr.gouv.monprojetsup.formation.domain.entity.Baccalaureat

data class BaccalaureatDTO(
    val id: String,
    val nom: String,
) {
    constructor(baccalaureat: Baccalaureat) : this(
        id = baccalaureat.id,
        nom = baccalaureat.nom,
    )
}
