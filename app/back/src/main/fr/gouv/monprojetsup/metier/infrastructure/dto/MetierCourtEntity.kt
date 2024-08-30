package fr.gouv.monprojetsup.metier.infrastructure.dto

import fr.gouv.monprojetsup.metier.domain.entity.MetierCourt
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "ref_metier")
class MetierCourtEntity {
    @Id
    @Column(name = "id", nullable = false)
    lateinit var id: String

    @Column(name = "label", nullable = false)
    lateinit var label: String

    fun toMetierCourt() = MetierCourt(id = id, nom = label)
}
