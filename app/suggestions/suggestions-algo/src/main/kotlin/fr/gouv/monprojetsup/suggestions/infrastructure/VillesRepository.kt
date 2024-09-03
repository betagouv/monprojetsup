package fr.gouv.monprojetsup.suggestions.infrastructure

import fr.gouv.monprojetsup.data.domain.model.LatLng
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsVilleEntity
import fr.gouv.monprojetsup.suggestions.port.VillesPort
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


interface VillesJPARepository : JpaRepository<SuggestionsVilleEntity, String>

@Repository
open class VillesRepository(
    private val repo : VillesJPARepository
)
    : VillesPort
{

    @Transactional(readOnly = true)
    @Cacheable(value = ["repositories"])
    override fun getCoords(id: String): List<LatLng> {
        return repo.findById(id).map { it.coords }.orElse(listOf())
    }

}
