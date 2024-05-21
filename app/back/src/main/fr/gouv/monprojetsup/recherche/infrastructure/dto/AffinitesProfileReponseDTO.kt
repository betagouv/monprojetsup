package fr.gouv.monprojetsup.recherche.infrastructure.dto

data class AffinitesProfileReponseDTO(
    val affinites: List<AffinitesDTO>,
    val metiers: List<String>,
)

data class AffinitesDTO(
    val key: String,
    val affinite: Float,
)
