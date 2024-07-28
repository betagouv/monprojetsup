package fr.gouv.monprojetsup.data.infrastructure.repository

import fr.gouv.monprojetsup.data.domain.entity.Formation
import fr.gouv.monprojetsup.data.domain.port.FormationsPort
import fr.gouv.monprojetsup.data.domain.port.LabelsPort
import fr.gouv.monprojetsup.data.infrastructure.entity.FormationEntity
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*


interface FormationsJPARepository : JpaRepository<FormationEntity, String> {
    override fun findAll(): MutableList<FormationEntity>

    override fun findById(key: String): Optional<FormationEntity>

}


@Repository
class FormationRepository(
    private val repo : FormationsJPARepository
)
    : FormationsPort()
{
    @Transactional(readOnly = true)
    override fun retrieveFormations(): Map<String, Formation> {
        return repo.findAll().associate { it.id to it.toFormation() }
    }

    @Transactional(readOnly = true)
    @Cacheable(value = ["myCache"], key = "#id")
    override fun retrieveFormation(id: String): Optional<Formation> {
        return repo.findById(id).map { it.toFormation() }
    }

}