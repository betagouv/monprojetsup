package fr.gouv.monprojetsup.data.suggestions.infrastructure.entity

import fr.gouv.monprojetsup.suggestions.domain.model.Edge
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table


@Entity
@Table(name = "liens")
class EdgeEntity(key: String,target: String, type: Int) {

    @Column(nullable = false)
    val src: String = key

    @Column(nullable = false)
    val dst: String = target

    @Column(nullable = false)
    val type: Int = type

    fun toEdge() =
        Edge(
            src,
            dst,
        )
}
