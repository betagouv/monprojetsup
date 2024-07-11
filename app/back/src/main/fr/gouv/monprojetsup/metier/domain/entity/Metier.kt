package fr.gouv.monprojetsup.metier.domain.entity

import fr.gouv.monprojetsup.commun.domain.entity.Lien

data class Metier(
    val id: String,
    val nom: String,
    val descriptif: String?,
    val liens: List<Lien>,
)
