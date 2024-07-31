package fr.gouv.monprojetsup.data.suggestions.infrastructure.entity

import fr.gouv.monprojetsup.suggestions.domain.model.Candidat
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "candidats")
class CandidatEntity {
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
