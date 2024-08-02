package fr.gouv.monprojetsup.data.suggestions.entity

import fr.gouv.monprojetsup.suggestions.domain.model.Matiere
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "sugg_matieres")
class SuggestionsMatiereEntity {

    constructor(matiere: Matiere) {
        this.id = matiere.id
        this.label = matiere.label
        this.estSpecialite = matiere.estSpecialite
        this.bacs = matiere.bacs
    }

    fun toMatiere() : Matiere {
        return Matiere(
            id,
            label,
            estSpecialite,
            bacs
        )
    }

    @Id
    @Column
    val id: Int

    @Column
    val label: String

    @Column
    val estSpecialite: Boolean

    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    val bacs: List<String>

}