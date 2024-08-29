package fr.gouv.monprojetsup.suggestions.infrastructure

import fr.gouv.monprojetsup.data.domain.model.psup.DescriptifVoeu
import fr.gouv.monprojetsup.data.formation.entity.VoeuEntity
import fr.gouv.monprojetsup.suggestions.port.VoeuxPort
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

}
