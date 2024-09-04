package fr.gouv.monprojetsup.suggestions.infrastructure

import fr.gouv.monprojetsup.data.formationmetier.entity.FormationMetierEntity
import fr.gouv.monprojetsup.suggestions.port.FormationsMetiersPort
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


interface FormationsMetiersJpaRepository : JpaRepository<FormationMetierEntity, String> {
    @Query("SELECT id_metier FROM ref_join_formation_metier WHERE id_formation = ?1", nativeQuery = true)
    fun getMetiersOfFormation(formationId: String) :  List<String>

}

@Repository
open class FormationsMetiersDb(
    private val db : FormationsMetiersJpaRepository,
) : FormationsMetiersPort
{
    @Transactional(readOnly = true)
    override fun findAll(): List<FormationMetierEntity> {
        return db.findAll()
    }

    @Transactional(readOnly = true)
    @Cacheable("getMetiersOfFormation")
    override fun getMetiersOfFormation(formationID: String): List<String> {
        return db.getMetiersOfFormation(formationID)
    }

}