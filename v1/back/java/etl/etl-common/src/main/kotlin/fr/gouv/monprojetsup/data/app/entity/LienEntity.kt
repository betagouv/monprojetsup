package fr.gouv.monprojetsup.data.app.entity

import fr.gouv.monprojetsup.data.app.domain.Lien
import java.io.Serializable

data class LienEntity(
    val nom: String,
    val url: String,
) : Serializable {
    fun toLien() = Lien(nom = nom, url = url)
}
