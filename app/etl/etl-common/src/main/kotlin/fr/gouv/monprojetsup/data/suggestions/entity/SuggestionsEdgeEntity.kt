package fr.gouv.monprojetsup.data.suggestions.entity

import fr.gouv.monprojetsup.data.domain.model.Edge
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table


@Entity
@Table(name = "suggestions_edges")
class SuggestionsEdgeEntity {

    constructor()
    constructor(key: String, target: String, type: Int) {
        this.src = key
        this.dst = target
        this.type = type
    }

    @Id
    @GeneratedValue
    var id: Long = 0

    var src: String = ""

    var dst: String = ""

    var type: Int = 0

    fun toEdge() =
        Edge(
            src,
            dst,
        )
}
