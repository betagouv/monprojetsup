package fr.gouv.monprojetsup.formation.domain.entity

import fr.gouv.monprojetsup.commun.domain.entity.Lien

data class Formation(
    val id: String,
    val nom: String,
    val descriptifGeneral: String?,
    val descriptifAttendus: String?,
    val descriptifDiplome: String?,
    val descriptifConseils: String?,
    val formationsAssociees: List<String>,
    val liens: List<Lien>,
    val valeurCriteresAnalyseCandidature: List<Int>,
)
