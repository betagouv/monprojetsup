package fr.gouv.monprojetsup.data.suggestions.entity

import fr.gouv.monprojetsup.data.domain.model.LatLng
import fr.gouv.monprojetsup.data.domain.model.Ville
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "suggestions_villes")
class SuggestionsVilleEntity {

    constructor()

    constructor(it: Ville) {
        this.id = it.id
        this.coords = it.coords
    }

    @Id
    var id: String = ""

    @JdbcTypeCode(SqlTypes.JSON)
    var coords : List<LatLng>  = listOf()

}