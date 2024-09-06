package fr.gouv.monprojetsup.data.suggestions.entity

import fr.gouv.monprojetsup.data.domain.model.LatLng
import fr.gouv.monprojetsup.data.domain.model.Ville
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "sugg_villes")
class SuggestionsVilleEntity {

    constructor()

    fun toVille(): Ville {
        return Ville(this.insee, this.nom, this.coords)
    }

    @Id
    //code insee ou nom de la ville (double indexation)
    lateinit var id: String

    lateinit var nom: String

    lateinit var insee: String

    @JdbcTypeCode(SqlTypes.JSON)
    lateinit var coords : List<LatLng>

    companion object {
        fun getEntities(it: Ville): Iterable<SuggestionsVilleEntity> {
            return listOf(
                SuggestionsVilleEntity().apply {
                    id = it.codeInsee
                    nom = it.nom
                    insee = it.codeInsee
                    coords = it.coords
                },
                SuggestionsVilleEntity().apply {
                    id = it.nom
                    nom = it.nom
                    insee = it.codeInsee
                    coords = it.coords
                }
            )
        }
    }

}