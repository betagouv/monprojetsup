package fr.gouv.monprojetsup.commun.hateoas.domain.entity

data class Hateoas<T>(
    val pageActuelle: Int,
    val pageSuivante: Int?,
    val premierePage: Int,
    val dernierePage: Int,
    val listeCoupee: List<T>,
)
