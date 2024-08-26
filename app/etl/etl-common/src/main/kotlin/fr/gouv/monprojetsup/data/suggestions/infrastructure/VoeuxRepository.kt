package fr.gouv.monprojetsup.data.suggestions.infrastructure

import fr.gouv.monprojetsup.data.app.formation.entity.VoeuEntity
import fr.gouv.monprojetsup.data.domain.port.VoeuxPort
import fr.gouv.monprojetsup.data.infrastructure.psup.DescriptifVoeu
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
