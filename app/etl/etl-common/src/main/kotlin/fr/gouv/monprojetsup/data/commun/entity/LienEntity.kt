package fr.gouv.monprojetsup.data.commun.entity

import java.io.Serializable

data class LienEntity(
    val nom: String,
    val url: String,
) : Serializable
