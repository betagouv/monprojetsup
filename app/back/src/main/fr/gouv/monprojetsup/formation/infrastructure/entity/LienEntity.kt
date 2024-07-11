package fr.gouv.monprojetsup.formation.infrastructure.entity

import fr.gouv.monprojetsup.commun.domain.entity.Lien
import java.io.Serializable

data class LienEntity(
    val nom: String,
    val url: String,
) : Serializable {
    fun toLien() = Lien(nom = nom, url = url)
}
