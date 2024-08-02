package fr.gouv.monprojetsup.data.suggestions.entity

import fr.gouv.monprojetsup.suggestions.domain.model.Candidat
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "sugg_candidats")
class SuggestionsCandidatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    fun toCandidat() : Candidat{
        return Candidat(ArrayList(voeux))
    }

    constructor(c : Candidat) {
        this.voeux = c.voeux
    }

    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    val voeux : List<String>

}
