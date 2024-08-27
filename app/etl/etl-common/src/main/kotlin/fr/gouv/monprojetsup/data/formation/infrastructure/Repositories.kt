package fr.gouv.monprojetsup.data.formation.infrastructure

import fr.gouv.monprojetsup.data.domain.model.Formation
import fr.gouv.monprojetsup.data.domain.port.FormationsPort
import fr.gouv.monprojetsup.data.formation.entity.CritereAnalyseCandidatureEntity
import fr.gouv.monprojetsup.data.formation.entity.FormationEntity
import fr.gouv.monprojetsup.data.formation.entity.MoyenneGeneraleAdmisEntity
import fr.gouv.monprojetsup.data.formation.entity.VoeuEntity
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface CriteresDb :
    JpaRepository<CritereAnalyseCandidatureEntity, String>

@Repository
interface MoyennesGeneralesAdmisDb :
    JpaRepository<MoyenneGeneraleAdmisEntity, String>

@Repository
interface TripletsAffectationDb :
    JpaRepository<VoeuEntity, String>

interface FormationJpaRepository : JpaRepository<FormationEntity, String>

@Repository
open class FormationDb(
    private val db : FormationJpaRepository
)
    : FormationsPort
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
    @Cacheable(value = ["myCache"], key = "#id")
    override fun retrieveFormation(id: String): Optional<Formation> {
        return db.findById(id).map { it.toFormation() }
    }

    override fun deleteAll() {
        db.deleteAll()
    }

    override fun saveAll(entities: List<FormationEntity>) {
        db.saveAll(entities)
    }

}