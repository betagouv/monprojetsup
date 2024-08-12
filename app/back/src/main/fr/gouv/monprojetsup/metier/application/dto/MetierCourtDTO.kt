package fr.gouv.monprojetsup.metier.application.dto

import fr.gouv.monprojetsup.metier.domain.entity.MetierCourt

data class MetierCourtDTO(
    val id: String,
    val nom: String,
) {
    constructor(metierCourt: MetierCourt) : this(id = metierCourt.id, nom = metierCourt.nom)
}
