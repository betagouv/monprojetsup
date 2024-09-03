package fr.gouv.monprojetsup.suggestions.infrastructure

import fr.gouv.monprojetsup.data.domain.model.Edge
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity
import fr.gouv.monprojetsup.suggestions.port.EdgesPort
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


interface EdgeJPARepository : JpaRepository<SuggestionsEdgeEntity, String> {
    fun findByType(typ: Int): List<SuggestionsEdgeEntity>

    fun findBySrc(src: String): List<SuggestionsEdgeEntity>

}

@Repository
open class EdgesRepository(
    private val edgeJPARepository : EdgeJPARepository
)
    : EdgesPort()
{
    @Transactional(readOnly = true)
    @Cacheable(value = ["repositories"])
    override fun retrieveEdgesOfType(type: Int): MutableList<Edge> {
        return edgeJPARepository.findByType(type).map { it.toEdge() }.toMutableList()
    }

}
