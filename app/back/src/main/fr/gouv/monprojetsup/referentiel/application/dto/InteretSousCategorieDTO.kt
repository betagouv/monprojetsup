package fr.gouv.monprojetsup.referentiel.application.dto

import fr.gouv.monprojetsup.referentiel.domain.entity.InteretSousCategorie

data class InteretSousCategorieDTO(
    val id: String,
    val nom: String,
    val description: String?,
    val emoji: String,
) {
    constructor(interetCategorie: InteretSousCategorie) : this(
        id = interetCategorie.id,
        nom = interetCategorie.nom,
        description = interetCategorie.description,
        emoji = interetCategorie.emoji,
    )
}
