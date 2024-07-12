package fr.gouv.monprojetsup.referentiel.application.dto

import fr.gouv.monprojetsup.referentiel.domain.entity.Specialite

data class SpecialitesDTO(
    val id: String,
    val nom: String,
) {
    constructor(specialite: Specialite) : this(
        id = specialite.id,
        nom = specialite.label,
    )
}
