package fr.gouv.monprojetsup.suggestions.infrastructure

import fr.gouv.monprojetsup.data.formation.entity.VoeuEntity
import fr.gouv.monprojetsup.data.model.LatLng
import fr.gouv.monprojetsup.data.model.psup.DescriptifVoeu
import fr.gouv.monprojetsup.suggestions.port.VoeuxPort
import org.apache.commons.lang3.tuple.Pair
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


@Transactional(readOnly = true)
interface VoeuxJPARepository : JpaRepository<VoeuEntity, String>

@Repository
open class VoeuxRepository(
    private val repo : VoeuxJPARepository
)
    : VoeuxPort
{
    override fun retrieveDescriptifs(): Map<String, DescriptifVoeu?> {
        return repo.findAll().associate { it.id to it.descriptif}
    }

    override fun retrieveCoords(voeuxIds: List<String>): List<Pair<String, LatLng>> {
        return repo.findAllById(voeuxIds)
            .filter { it.lat != null && it.lng != null }
            .map { Pair.of(it.id, LatLng(it.lat!!,it.lng!!)) }
    }

}
