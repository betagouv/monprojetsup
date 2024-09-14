package fr.gouv.monprojetsup.data.suggestions.entity

import fr.gouv.monprojetsup.data.model.Edge
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table


@Entity
@Table(name = "sugg_edges")
class SuggestionsEdgeEntity {

    constructor()
    constructor(key: String, target: String, type: Int) {
        this.src = key
        this.dst = target
        this.type = type
    }

    companion object {
        const val TYPE_EDGE_INTERET_METIER: Int = 0
        const val TYPE_EDGE_FORMATIONS_PSUP_DOMAINES: Int = 1
        const val TYPE_EDGE_DOMAINES_METIERS: Int = 2
        const val TYPE_EDGE_SECTEURS_METIERS: Int = 3
        const val TYPE_EDGE_METIERS_ASSOCIES: Int = 4
        const val TYPE_EDGE_FORMATION_PSUP_TO_FORMATION_MPS: Int = 5
        const val TYPE_EDGE_LAS_TO_GENERIC: Int = 6
        const val TYPE_EDGE_LAS_TO_PASS: Int = 7
        const val TYPE_EDGE_INTERET_GROUPE_INTERET: Int = 8
        const val TYPE_EDGE_METIERS_FORMATIONS_PSUP: Int = 9
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
