package fr.gouv.monprojetsup.data.suggestions.entity

import fr.gouv.monprojetsup.suggestions.domain.model.Candidat
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "suggestions_candidats")
class SuggestionsCandidatEntity {

    constructor()

    @Id
    @GeneratedValue
    val id: Long = 0

    fun toCandidat() : Candidat{
        return Candidat(ArrayList(voeux))
    }

    constructor(c : Candidat) {
        this.voeux = c.voeux
    }

    @JdbcTypeCode(SqlTypes.ARRAY)
    var voeux : List<String> = listOf()

}
