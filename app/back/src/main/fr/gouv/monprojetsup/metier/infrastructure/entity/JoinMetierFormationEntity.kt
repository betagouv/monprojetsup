package fr.gouv.monprojetsup.metier.infrastructure.entity

import fr.gouv.monprojetsup.formation.infrastructure.entity.FormationCourteEntity
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.io.Serializable

data class JoinMetierFormationQuery(
    val metier: MetierEntity,
    val formation: FormationCourteEntity?,
)

data class JoinFormationMetierQuery(
    val idFormation: String,
    val metierEntity: MetierEntity,
) {
    fun toMetier() = metierEntity.toMetier()
}

@Entity
@Table(name = "ref_join_formation_metier")
class JoinFormationMetierEntity {
    @EmbeddedId
    lateinit var id: JoinFormationMetierId
}

@Embeddable
data class JoinFormationMetierId(
    @Column(name = "id_formation", nullable = false)
    val idFormation: String = "",
    @Column(name = "id_metier", nullable = false)
    val idMetier: String = "",
) : Serializable
