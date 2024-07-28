package fr.gouv.monprojetsup.data.infrastructure.entity

import fr.gouv.monprojetsup.data.domain.entity.Matiere
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "matieres")
class MatiereEntity {
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
    val id: Int = 0

    @Column
    val label: String = ""

    @Column
    val estSpecialite: Boolean = false

    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    val bacs: List<String> = emptyList()

}