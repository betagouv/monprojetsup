package fr.gouv.monprojetsup.suggestions.infrastructure.repository

import fr.gouv.monprojetsup.suggestions.domain.entity.Edge
import fr.gouv.monprojetsup.suggestions.domain.port.EdgesPort
import fr.gouv.monprojetsup.suggestions.infrastructure.entity.EdgeEntity
import org.jetbrains.annotations.Unmodifiable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional




interface EdgeJPARepository : JpaRepository<EdgeEntity, String> {
    fun findByTyp(typ: Int): List<EdgeEntity>

    fun findBySrc(src: String): List<EdgeEntity>

}

@Repository
class EdgesRepository(
            val edgeJPARepository : EdgeJPARepository )
    : EdgesPort()
{
    @Transactional(readOnly = true)
    override fun retrieveEdgesOfType(type: Int): MutableList<Edge> {
        return edgeJPARepository.findByTyp(type).map { it.toEdge() }.toMutableList()
    }

    @Transactional(readOnly = true)
    override fun getOutgoingEdges(src: String?): MutableList<String> {
        return edgeJPARepository.findBySrc(src!!).map { it.dst }.toMutableList()
    }
}
