package fr.gouv.monprojetsup.data.infrastructure.entity

import fr.gouv.monprojetsup.data.domain.entity.Edge
import jakarta.annotation.Nonnull
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table


@Entity
@Table(name = "liens")
class EdgeEntity {

    @Column(nullable = false)
    val src: String = ""

    @Column(nullable = false)
    val dst: String = ""

    @Column(nullable = false)
    var type: String = ""

    fun toEdge() =
        Edge(
            src,
            dst,
        )
}
