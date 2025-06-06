package fr.gouv.monprojetsup.suggestions.infrastructure

import fr.gouv.monprojetsup.data.formation.entity.FormationVoeuEntity
import fr.gouv.monprojetsup.suggestions.port.FormationsVoeuxPort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


interface FormationsVoeuxJpaRepository : JpaRepository<FormationVoeuEntity, String> {
    @Query("SELECT id_formation FROM ref_join_formation_voeu WHERE id_voeu = ?1", nativeQuery = true)
    fun getFormationsOfVoeu(voeuId: String) :  List<String>

    @Query("SELECT id_voeu FROM ref_join_formation_voeu WHERE id_formation = ?1", nativeQuery = true)
    fun getVoeuxOfFormation(voeuId: String) :  List<String>
}

@Repository
open class FormationsVoeuxsDb(
    private val db : FormationsVoeuxJpaRepository,
) : FormationsVoeuxPort
{
    @Transactional(readOnly = true)
    override fun findAll(): List<FormationVoeuEntity> {
        return db.findAll()
    }

    @Transactional(readOnly = true)
    override fun getFormationsOfVoeu(voeuId: String): List<String> {
        return db.getFormationsOfVoeu(voeuId)
    }

    @Transactional(readOnly = true)
    override fun getVoeuxOfFormation(formationId: String): List<String> {
        return db.getVoeuxOfFormation(formationId)
    }

}