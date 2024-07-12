package fr.gouv.monprojetsup.formation.entity

import fr.gouv.monprojetsup.eleve.domain.entity.Commune

object Communes {
    val GRENOBLE =
        Commune(
            codeInsee = "38185",
            nom = "Grenoble",
            longitude = 5.71667f,
            latitude = 45.16667f,
        )

    val STRASBOURG =
        Commune(
            codeInsee = "80688",
            nom = "Strasbourg",
            longitude = 1.666667f,
            latitude = 50.266666f,
        )

    val PARIS =
        Commune(
            codeInsee = "75015",
            nom = "Paris",
            longitude = 2.2885659f,
            latitude = 48.851227f,
        )
    val MARSEILLE =
        Commune(
            codeInsee = "13200",
            nom = "Marseille",
            latitude = 43.300000f,
            longitude = 5.400000f,
        )

    val LYON =
        Commune(
            codeInsee = "69380",
            nom = "Lyon",
            latitude = 45.75f,
            longitude = 4.85f,
        )
    val CAEN =
        Commune(
            codeInsee = "14118",
            nom = "Caen",
            latitude = 49.183334f,
            longitude = -0.350000f,
        )
}
