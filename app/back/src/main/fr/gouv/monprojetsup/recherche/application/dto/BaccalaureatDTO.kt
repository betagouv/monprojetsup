package fr.gouv.monprojetsup.recherche.application.dto

import fr.gouv.monprojetsup.recherche.domain.entity.Baccalaureat

data class BaccalaureatDTO(
    val id: String,
    val nom: String,
) {
    constructor(baccalaureat: Baccalaureat) : this(
        id = baccalaureat.id,
        nom = baccalaureat.nom,
    )
}
