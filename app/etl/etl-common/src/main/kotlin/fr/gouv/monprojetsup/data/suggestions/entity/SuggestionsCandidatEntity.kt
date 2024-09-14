package fr.gouv.monprojetsup.data.suggestions.entity

import fr.gouv.monprojetsup.data.model.Candidat
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "sugg_candidats")
class SuggestionsCandidatEntity {

    constructor()

    @Id
    @GeneratedValue
    val id: Long = 0

    fun toCandidat() : Candidat {
        return Candidat(bac,ArrayList(voeux))
    }

    constructor(c : Candidat) {
        this.bac = c.bac
        this.voeux = ArrayList(c.voeux)
    }

    lateinit var bac : String

    @JdbcTypeCode(SqlTypes.ARRAY)
    lateinit var voeux : List<String>

}
