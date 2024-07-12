package fr.gouv.monprojetsup.referentiel.application.dto

import fr.gouv.monprojetsup.referentiel.domain.entity.InteretCategorie

data class InteretCategorieDTO(
    val id: String,
    val nom: String,
    val emoji: String,
) {
    constructor(interetCategorie: InteretCategorie) : this(
        id = interetCategorie.id,
        nom = interetCategorie.nom,
        emoji = interetCategorie.emoji,
    )
}
