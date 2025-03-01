package fr.gouv.monprojetsup.data.suggestions.entity

import fr.gouv.monprojetsup.data.model.PanierVoeux
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "sugg_paniers_voeux")
class SuggestionsPaniersVoeuxEntity {

    constructor()

    @Id
    @GeneratedValue
    val id: Long = 0

    fun toCandidat() : PanierVoeux {
        return PanierVoeux(bac, ArrayList(voeux))
    }

    constructor(c : PanierVoeux) {
        this.bac = c.bac
        this.voeux = ArrayList(c.voeux)
    }

    lateinit var bac : String

    @JdbcTypeCode(SqlTypes.ARRAY)
    lateinit var voeux : List<String>

}
