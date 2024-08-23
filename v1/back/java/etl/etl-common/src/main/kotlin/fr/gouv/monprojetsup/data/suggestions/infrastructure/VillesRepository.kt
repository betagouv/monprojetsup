package fr.gouv.monprojetsup.data.suggestions.infrastructure

import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsVilleEntity
import fr.gouv.monprojetsup.data.domain.model.LatLng
import fr.gouv.monprojetsup.data.domain.model.Ville
import fr.gouv.monprojetsup.data.domain.port.VillesPort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


interface VillesJPARepository : JpaRepository<SuggestionsVilleEntity, String> {

}

@Repository
open class VillesRepository(
    private val repo : VillesJPARepository
)
    : VillesPort
{
    override fun getCoords(id: String): List<LatLng> {
        return repo.findById(id).map { it.coords }.orElse(listOf())
    }

    override fun saveAll(villes: MutableList<Ville>) {
        repo.saveAll(villes.map { SuggestionsVilleEntity(it) })
    }


}
