package fr.gouv.monprojetsup.suggestions.infrastructure.repository

import fr.gouv.monprojetsup.suggestions.data.model.descriptifs.Descriptifs
import fr.gouv.monprojetsup.suggestions.domain.entity.Edge
import fr.gouv.monprojetsup.suggestions.domain.port.EdgesPort
import fr.gouv.monprojetsup.suggestions.domain.port.VoeuxPort
import fr.gouv.monprojetsup.suggestions.infrastructure.entity.EdgeEntity
import fr.gouv.monprojetsup.suggestions.infrastructure.entity.VoeuEntity
import org.jetbrains.annotations.Unmodifiable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional




interface VoeuxJPARepository : JpaRepository<VoeuEntity, String> {
    fun findByTyp(typ: Int): List<EdgeEntity>

    fun findBySrc(src: String): List<EdgeEntity>

}

@Repository
class VoeuxRepository(
            val repo : VoeuxJPARepository )
    : VoeuxPort()
{
    override fun retrieveDescriptifs(): Map<String, Descriptifs.Descriptif?> {

        return repo.findAll().associate { it.id to it.descriptif}

    }

}
