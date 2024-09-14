package fr.gouv.monprojetsup.data.suggestions.entity

import fr.gouv.monprojetsup.data.model.Matiere
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "sugg_matieres")
class SuggestionsMatiereEntity {

    constructor()

    constructor(matiere: Matiere) {
        this.id = matiere.idMps
        this.idPsup = matiere.idPsup
        this.label = matiere.label
        this.estSpecialite = matiere.estSpecialite
        this.bacs = matiere.bacs
    }

    fun toMatiere() : Matiere {
        return Matiere(
            id,
            idPsup,
            label,
            estSpecialite,
            bacs
        )
    }

    @Id
    lateinit var id: String

    var idPsup: Int = 0

    lateinit var label: String

    var estSpecialite: Boolean = false

    @JdbcTypeCode(SqlTypes.ARRAY)
    var bacs: List<String> = listOf()

}