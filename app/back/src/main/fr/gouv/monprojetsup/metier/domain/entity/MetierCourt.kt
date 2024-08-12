package fr.gouv.monprojetsup.metier.domain.entity

import fr.gouv.monprojetsup.commun.recherche.entity.EntiteRecherchee

data class MetierCourt(
    override val id: String,
    val nom: String,
) : EntiteRecherchee(id)
