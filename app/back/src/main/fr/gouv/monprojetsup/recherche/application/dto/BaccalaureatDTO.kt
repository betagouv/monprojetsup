package fr.gouv.monprojetsup.recherche.application.dto

import fr.gouv.monprojetsup.recherche.domain.entity.Baccalaureat

data class BaccalaureatDTO(
    val id: String,
    val nom: String,
) {
    companion object {
        fun fromBaccalaureat(baccalaureat: Baccalaureat): BaccalaureatDTO {
            return BaccalaureatDTO(
                id = baccalaureat.id,
                nom = baccalaureat.nom,
            )
        }
    }
}
