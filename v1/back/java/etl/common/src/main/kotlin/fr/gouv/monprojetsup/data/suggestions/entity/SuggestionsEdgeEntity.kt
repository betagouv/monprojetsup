package fr.gouv.monprojetsup.data.suggestions.entity

import fr.gouv.monprojetsup.suggestions.domain.model.Edge
import jakarta.persistence.*


@Entity
@Table(name = "sugg_edges")
class SuggestionsEdgeEntity(key: String, target: String, type: Int) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

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
