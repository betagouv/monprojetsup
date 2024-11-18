package fr.gouv.monprojetsup.formation.infrastructure.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.io.Serializable

@Entity
@Table(name = "ref_join_formation_voeu")
class JoinFormationVoeuEntity {
    @EmbeddedId
    lateinit var id: JoinFormationVoeuId
}

@Embeddable
data class JoinFormationVoeuId(
    @Column(name = "id_formation", nullable = false)
    val idFormation: String = "",
    @Column(name = "id_voeu", nullable = false)
    val idVoeu: String = "",
) : Serializable
