package fr.gouv.monprojetsup.suggestions.infrastructure.repository

import fr.gouv.monprojetsup.suggestions.domain.port.LabelsPort
import fr.gouv.monprojetsup.suggestions.infrastructure.entity.FormationEntity
import fr.gouv.monprojetsup.suggestions.infrastructure.entity.LabelEntity
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*


interface LabelsJPARepository : JpaRepository<LabelEntity, String> {
    override fun findAll(): MutableList<LabelEntity>

    override fun findById(key: String): Optional<LabelEntity>

}


@Repository
class LabelsRepository(
    private val repo : LabelsJPARepository )
    : LabelsPort()
{
    @Transactional(readOnly = true)
    override fun retrieveLabels(): Map<String, String> {
        return repo.findAll().associate { it.id to it.label}
    }

    @Transactional(readOnly = true)
    @Cacheable(value = ["myCache"], key = "#id")
    override fun retrieveLabel(id: String): Optional<String> {
        return repo.findById(id).map { it.label }
    }

}