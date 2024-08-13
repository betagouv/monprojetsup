package fr.gouv.monprojetsup.data.suggestions.entity

import fr.gouv.monprojetsup.suggestions.domain.model.Matiere
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "suggestions_matieres")
class SuggestionsMatiereEntity {

    constructor()

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
    var id: Int = 0

    var label: String = ""

    var estSpecialite: Boolean = false

    @JdbcTypeCode(SqlTypes.ARRAY)
    var bacs: List<String> = listOf()

}