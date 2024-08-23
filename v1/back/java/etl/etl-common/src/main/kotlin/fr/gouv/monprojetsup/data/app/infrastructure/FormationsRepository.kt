package fr.gouv.monprojetsup.data.app.infrastructure

import fr.gouv.monprojetsup.data.app.formation.entity.FormationEntity
import fr.gouv.monprojetsup.data.domain.model.Formation
import fr.gouv.monprojetsup.data.domain.port.FormationsPort
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*





@Repository
open class FormationDb(
    private val db : JpaRepository<FormationEntity, String>
)
    : FormationsPort
{
    @Transactional(readOnly = true)
    override fun retrieveFormations(): Map<String, Formation> {
        return db.findAll().associate { it.id to it.toFormation() }
    }

    @Transactional(readOnly = true)
    @Cacheable(value = ["myCache"], key = "#id")
    override fun retrieveFormation(id: String): Optional<Formation> {
        return db.findById(id).map { it.toFormation() }
    }

    override fun deleteAll() {
        db.deleteAll()
    }

    override fun saveAll(entities: MutableList<FormationEntity>) {
        db.saveAll(entities)
    }

    /*
    @Transactional(readOnly = false)
    override fun saveAll(formations: Collection<Formation>) {
        val entities = formations.map { SuggestionsFormationEntity(it) }
        repo.saveAll(entities)
    }
    */

}