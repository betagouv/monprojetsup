package fr.gouv.monprojetsup.data.suggestions.infrastructure

import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsLabelEntity
import fr.gouv.monprojetsup.suggestions.domain.port.LabelsPort
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
class LabelsRepository(
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
    @Cacheable(value = ["myCache"], key = "#id")
    override fun retrieveLabel(id: String): Optional<String> {
        return repo.findById(id).map { it.label }
    }

    @Transactional(readOnly = true)
    @Cacheable(value = ["myCache"], key = "#id")
    override fun retrieveDebugLabel(id: String): Optional<String> {
        return repo.findById(id).map { (it.labelDebug ?: it.label) }
    }

    override fun saveAll(
        labels: Map<String, String>,
        debugLabels: Map<String, String>
    ) {
        repo.saveAll(
            labels.entries.map { SuggestionsLabelEntity(it.key, it.value, debugLabels[it.key]) }
        )
    }

}