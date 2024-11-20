package fr.gouv.monprojetsup.eleve.infrastructure.entity

import fr.gouv.monprojetsup.eleve.domain.entity.CommuneFavorite
import java.io.Serializable

data class CommuneEntity(
    val codeInsee: String,
    val nom: String,
    val latitude: Double,
    val longitude: Double,
) : Serializable {
    constructor(communeFavorite: CommuneFavorite) : this(
        codeInsee = communeFavorite.codeInsee,
        nom = communeFavorite.nom,
        latitude = communeFavorite.latitude,
        longitude = communeFavorite.longitude,
    )

    fun toCommune() =
        CommuneFavorite(
            codeInsee = codeInsee,
            nom = nom,
            latitude = latitude,
            longitude = longitude,
        )
}
