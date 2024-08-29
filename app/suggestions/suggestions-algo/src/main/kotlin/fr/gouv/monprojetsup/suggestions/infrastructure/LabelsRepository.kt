package fr.gouv.monprojetsup.suggestions.infrastructure

import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsLabelEntity
import fr.gouv.monprojetsup.suggestions.port.LabelsPort
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*


interface LabelsJPARepository : JpaRepository<SuggestionsLabelEntity, String> {
    override fun findAll(): MutableList<SuggestionsLabelEntity>

    override fun findById(key: String): Optional<SuggestionsLabelEntity>

}


@Repository
open class LabelsRepository(
    private val repo : LabelsJPARepository
)
    : LabelsPort
{
    @Transactional(readOnly = true)
    override fun retrieveLabels(): Map<String, String> {
        return repo.findAll().associate { it.id to it.label}
    }

    @Transactional(readOnly = true)
    override fun retrieveDebugLabels(): Map<String, String> {
        return repo.findAll().associate { it.id to (it.labelDebug ?: it.label) }
    }

    @Transactional(readOnly = true)
    @Cacheable(value = ["myCache"])
    override fun retrieveLabel(id: String): Optional<String> {
        return repo.findById(id).map { it.label }
    }

    @Transactional(readOnly = true)
    @Cacheable(value = ["myCache"])
    override fun retrieveDebugLabel(id: String): Optional<String> {
        return repo.findById(id).map { (it.labelDebug ?: it.label) }
    }

}