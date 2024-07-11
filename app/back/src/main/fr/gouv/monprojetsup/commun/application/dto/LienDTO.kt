package fr.gouv.monprojetsup.commun.application.dto

import fr.gouv.monprojetsup.commun.domain.entity.Lien

data class LienDTO(
    val nom: String,
    val url: String,
) {
    constructor(lien: Lien) : this(
        nom = lien.nom,
        url = lien.url,
    )
}
