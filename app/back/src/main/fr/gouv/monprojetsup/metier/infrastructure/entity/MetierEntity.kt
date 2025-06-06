package fr.gouv.monprojetsup.metier.infrastructure.entity

import fr.gouv.monprojetsup.commun.lien.infrastructure.entity.LienEntity
import fr.gouv.monprojetsup.metier.domain.entity.Metier
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "ref_metier")
class MetierEntity {
    @Id
    @Column(name = "id", nullable = false)
    lateinit var id: String

    @Column(name = "label", nullable = false)
    lateinit var label: String

    @Column(name = "descriptif_general", nullable = true)
    var descriptifGeneral: String? = null

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "liens", columnDefinition = "jsonb")
    var liens = arrayListOf<LienEntity>()

    @Column(name = "obsolete", nullable = false)
    var obsolete: Boolean = false

    fun toMetier(): Metier {
        return Metier(
            id = id,
            nom = label,
            descriptif = descriptifGeneral,
            liens = liens.map { it.toLien() },
        )
    }
}
