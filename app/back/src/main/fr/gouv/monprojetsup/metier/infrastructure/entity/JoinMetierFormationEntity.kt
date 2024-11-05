package fr.gouv.monprojetsup.metier.infrastructure.entity

import fr.gouv.monprojetsup.formation.infrastructure.entity.FormationCourteEntity
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.io.Serializable

@Entity
@Table(name = "ref_join_formation_metier")
class JoinMetierFormationEntity {
    @EmbeddedId
    lateinit var id: JoinFormationMetierId

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_formation", nullable = false, insertable = false, updatable = false)
    lateinit var formation: FormationCourteEntity
}

@Entity
@Table(name = "ref_join_formation_metier")
class JoinFormationMetierEntity {
    @EmbeddedId
    lateinit var id: JoinFormationMetierId

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_metier", nullable = false, insertable = false, updatable = false)
    lateinit var metier: MetierEntity
}

@Embeddable
data class JoinFormationMetierId(
    @Column(name = "id_formation", nullable = false)
    val idFormation: String = "",
    @Column(name = "id_metier", nullable = false)
    val idMetier: String = "",
) : Serializable
