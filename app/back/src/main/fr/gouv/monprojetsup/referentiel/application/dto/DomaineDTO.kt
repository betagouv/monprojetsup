package fr.gouv.monprojetsup.referentiel.application.dto

import fr.gouv.monprojetsup.referentiel.domain.entity.Domaine

data class DomaineDTO(
    val id: String,
    val nom: String,
    val description: String?,
    val emoji: String,
) {
    constructor(domaine: Domaine) : this(
        id = domaine.id,
        nom = domaine.nom,
        description = domaine.description,
        emoji = domaine.emoji,
    )
}
