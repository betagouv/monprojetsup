package fr.gouv.monprojetsup.suggestions.infrastructure

import fr.gouv.monprojetsup.data.domain.model.Formation
import fr.gouv.monprojetsup.data.formation.entity.FormationEntity
import fr.gouv.monprojetsup.suggestions.port.FormationsPort
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*


interface FormationJpaRepository : JpaRepository<FormationEntity, String>

@Repository
open class FormationDb(
    private val db : FormationJpaRepository
) : FormationsPort
{
    @Transactional(readOnly = true)
    override fun retrieveFormations(): Map<String, Formation> {
        return db.findAll().associate { it.id to it.toFormation() }
    }

    @Transactional(readOnly = true)
    override fun findAll(): List<FormationEntity> {
        return db.findAll()
    }

    @Transactional(readOnly = true)
    @Cacheable("retrieveFormation")
    override fun retrieveFormation(id: String): Optional<Formation> {
        return db.findById(id).map { it.toFormation() }
    }

}