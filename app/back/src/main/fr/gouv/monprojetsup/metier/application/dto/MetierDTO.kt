package fr.gouv.monprojetsup.metier.application.dto

import fr.gouv.monprojetsup.commun.application.dto.LienDTO
import fr.gouv.monprojetsup.metier.domain.entity.Metier

data class MetierDTO(
    val id: String,
    val nom: String,
    val descriptif: String?,
    val liens: List<LienDTO>,
) {
    constructor(metier: Metier) : this(
        id = metier.id,
        nom = metier.nom,
        descriptif = metier.descriptif,
        liens = metier.liens.map { LienDTO(it) },
    )
}
