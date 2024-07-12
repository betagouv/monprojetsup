package fr.gouv.monprojetsup.commun.lien.application.dto

import fr.gouv.monprojetsup.commun.lien.domain.entity.Lien

data class LienDTO(
    val nom: String,
    val url: String,
) {
    constructor(lien: Lien) : this(
        nom = lien.nom,
        url = lien.url,
    )
}
