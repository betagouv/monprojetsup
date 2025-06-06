package fr.gouv.monprojetsup.eleve.infrastructure.entity

import fr.gouv.monprojetsup.eleve.domain.entity.VoeuFavori
import java.io.Serializable

data class VoeuFavoriEntity(
    val idVoeu: String,
    val estFavoriParcoursup: Boolean,
) : Serializable {
    constructor(voeu: VoeuFavori) : this(
        idVoeu = voeu.idVoeu,
        estFavoriParcoursup = voeu.estFavoriParcoursup,
    )

    fun toVoeuFavori() =
        VoeuFavori(
            idVoeu = idVoeu,
            estFavoriParcoursup = estFavoriParcoursup,
        )
}
