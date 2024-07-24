package fr.gouv.monprojetsup.suggestions.infrastructure.entity

import fr.gouv.monprojetsup.suggestions.domain.entity.Edge
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
