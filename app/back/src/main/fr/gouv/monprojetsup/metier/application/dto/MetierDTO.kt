package fr.gouv.monprojetsup.metier.application.dto

import fr.gouv.monprojetsup.commun.lien.application.dto.LienDTO
import fr.gouv.monprojetsup.formation.application.dto.FormationCourteDTO
import fr.gouv.monprojetsup.metier.domain.entity.Metier
import fr.gouv.monprojetsup.metier.domain.entity.MetierAvecSesFormations

data class MetierAvecSesFormationsDTO(
    val id: String,
    val nom: String,
    val descriptif: String?,
    val liens: List<LienDTO>,
    val formations: List<FormationCourteDTO>,
) {
    constructor(metier: MetierAvecSesFormations) : this(
        id = metier.id,
        nom = metier.nom,
        descriptif = metier.descriptif,
        liens = metier.liens.map { LienDTO(it) },
        formations = metier.formations.map { FormationCourteDTO(it) },
    )
}

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
