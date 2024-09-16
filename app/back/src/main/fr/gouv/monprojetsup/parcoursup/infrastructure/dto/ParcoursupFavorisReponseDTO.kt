package fr.gouv.monprojetsup.parcoursup.infrastructure.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import fr.gouv.monprojetsup.parcoursup.domain.entity.FavorisParcoursup

@JsonIgnoreProperties(ignoreUnknown = true)
data class ParcoursupFavorisReponseDTO(
    @JsonProperty(value = "id_compte", required = true)
    val idCompte: Int,
    @JsonProperty(value = "id_formation", required = true)
    val idFormation: Int,
    @JsonProperty(value = "commentaire")
    val commentaire: String?,
    @JsonProperty(value = "notation")
    val notation: Int,
) {
    fun toFavorisParcoursup(): FavorisParcoursup {
        return FavorisParcoursup(
            idTripletAffectation = "ta$idFormation",
            commentaire = null,
            notation = 0,
        )
    }
}
