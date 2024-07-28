package fr.gouv.monprojetsup.data.infrastructure.repository

import fr.gouv.monprojetsup.data.model.descriptifs.Descriptifs
import fr.gouv.monprojetsup.data.domain.entity.Edge
import fr.gouv.monprojetsup.data.domain.port.EdgesPort
import fr.gouv.monprojetsup.data.domain.port.VoeuxPort
import fr.gouv.monprojetsup.data.infrastructure.entity.EdgeEntity
import fr.gouv.monprojetsup.data.infrastructure.entity.VoeuEntity
import org.jetbrains.annotations.Unmodifiable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional




interface VoeuxJPARepository : JpaRepository<VoeuEntity, String> {

}

@Repository
class VoeuxRepository(
            val repo : VoeuxJPARepository
)
    : VoeuxPort()
{
    override fun retrieveDescriptifs(): Map<String, Descriptifs.Descriptif?> {

        return repo.findAll().associate { it.id to it.descriptif}

    }

}
