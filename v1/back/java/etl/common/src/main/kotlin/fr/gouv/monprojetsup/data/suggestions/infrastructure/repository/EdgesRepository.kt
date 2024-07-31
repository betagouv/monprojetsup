package fr.gouv.monprojetsup.data.suggestions.infrastructure.repository

import fr.gouv.monprojetsup.data.suggestions.infrastructure.entity.EdgeEntity
import fr.gouv.monprojetsup.suggestions.domain.model.Edge
import fr.gouv.monprojetsup.suggestions.domain.port.EdgesPort
import fr.gouv.monprojetsup.suggestions.infrastructure.model.Edges
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


interface EdgeJPARepository : JpaRepository<EdgeEntity, String> {
    fun findByTyp(typ: Int): List<EdgeEntity>

    fun findBySrc(src: String): List<EdgeEntity>

}

@Repository
class EdgesRepository(
            val edgeJPARepository : EdgeJPARepository
)
    : EdgesPort()
{
    @Transactional(readOnly = true)
    override fun retrieveEdgesOfType(type: Int): MutableList<Edge> {
        return edgeJPARepository.findByTyp(type).map { it.toEdge() }.toMutableList()
    }

    @Transactional(readOnly = true)
    override fun getOutgoingEdges(src: String): MutableList<String> {
        return edgeJPARepository.findBySrc(src).map { it.dst }.toMutableList()
    }

    override fun getOutgoingEdges(src: String, type: Int): MutableList<String> {
        return edgeJPARepository.findBySrc(src).filter { it.type == type }.map { it.dst }.toMutableList()
    }

    override fun saveAll(edges: Edges, type: Int) {
        val newEdges = HashSet<EdgeEntity>()
        edges.edges().forEach {
            edge -> edge.value.forEach {
                target -> newEdges.add(EdgeEntity(edge.key, target, type))
            }
        }
        edgeJPARepository.saveAll(newEdges)
    }

    override fun saveAll(edges: Map<String, String>, type: Int) {
        val newEdges = HashSet<EdgeEntity>()
        edges.forEach { edge -> newEdges.add(EdgeEntity(edge.key, edge.value, type)) }
        edgeJPARepository.saveAll(newEdges)
    }

}
