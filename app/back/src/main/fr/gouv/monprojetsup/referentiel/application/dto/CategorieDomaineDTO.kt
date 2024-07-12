package fr.gouv.monprojetsup.referentiel.application.dto

import fr.gouv.monprojetsup.referentiel.domain.entity.CategorieDomaine

data class CategorieDomaineDTO(
    val id: String,
    val nom: String,
    val emoji: String,
) {
    constructor(categorieDomaine: CategorieDomaine) : this(
        id = categorieDomaine.id,
        nom = categorieDomaine.nom,
        emoji = categorieDomaine.emoji,
    )
}
