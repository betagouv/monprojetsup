package fr.gouv.monprojetsup.metier.infrastructure.entity

import fr.gouv.monprojetsup.commun.lien.infrastructure.entity.LienEntity
import fr.gouv.monprojetsup.metier.domain.entity.Metier
import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Type

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

    @Type(JsonType::class)
    @Column(name = "liens", columnDefinition = "jsonb")
    var liens = arrayListOf<LienEntity>()

    fun toMetier(): Metier {
        return Metier(
            id = id,
            nom = label,
            descriptif = descriptifGeneral,
            liens = liens.map { it.toLien() },
        )
    }
}
