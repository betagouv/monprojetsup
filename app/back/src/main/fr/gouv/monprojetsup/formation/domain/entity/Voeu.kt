package fr.gouv.monprojetsup.formation.domain.entity

import fr.gouv.monprojetsup.eleve.domain.entity.Commune

data class Voeu(
    val id: String,
    val nom: String,
    val commune: Commune,
)
