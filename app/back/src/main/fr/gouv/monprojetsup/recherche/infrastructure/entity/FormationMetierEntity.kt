package fr.gouv.monprojetsup.recherche.infrastructure.entity

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
@Table(name = "join_metier_formation")
class FormationMetierEntity {
    @EmbeddedId
    lateinit var id: FormationMetierId

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_id", insertable = false, updatable = false)
    lateinit var formation: FormationEntity

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metier_id", insertable = false, updatable = false)
    lateinit var metier: MetierEntity
}

@Embeddable
class FormationMetierId : Serializable {
    @Column(name = "metier_id")
    lateinit var metierId: String

    @Column(name = "formation_id")
    lateinit var idFormation: String
}
