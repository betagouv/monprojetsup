package fr.gouv.monprojetsup.formation.domain.entity

import fr.gouv.monprojetsup.commun.recherche.entity.EntiteRecherchee

data class FormationCourte(
    override val id: String,
    val nom: String,
) : EntiteRecherchee(id)
