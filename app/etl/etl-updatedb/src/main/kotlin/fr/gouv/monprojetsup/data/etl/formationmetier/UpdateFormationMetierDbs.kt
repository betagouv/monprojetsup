package fr.gouv.monprojetsup.data.etl.formationmetier

import fr.gouv.monprojetsup.data.commun.entity.LienEntity
import fr.gouv.monprojetsup.data.etl.MpsDataPort
import fr.gouv.monprojetsup.data.formationmetier.entity.FormationMetierEntity
import fr.gouv.monprojetsup.data.formationmetier.entity.FormationMetierEntityId
import fr.gouv.monprojetsup.data.metier.entity.MetierEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository

@Repository
interface JoinFormationMetiersDb :
    JpaRepository<FormationMetierEntity, String>

@Component
class UpdateFormationMetierDbs(
    private val joinFormationMetiersDb: JoinFormationMetiersDb,
    private val mpsDataPort : MpsDataPort
) {

    fun update() {
        clearAll()

        val formationsVersMetiers = mpsDataPort.getFormationsVersMetiersEtMetiersAssocies()

        val formationRefIds = mpsDataPort.getFormationsMpsIds()
        val metiersRefIds = mpsDataPort.getMetiersMpsIds()

        //on garantit l'unicité des paires
        val entities = formationsVersMetiers.entries
            .flatMap { (formationId, metiersIds) ->
                metiersIds.map { metierId ->
                    Pair(formationId, metierId)
                }
            }.filter { (formationId, metierId) ->
                formationRefIds.contains(formationId) && metiersRefIds.contains(metierId)
            }.distinct()
            .map { (formationId, metierId) ->
                val entity = FormationMetierEntity()
                val id = FormationMetierEntityId()
                id.idFormation = formationId
                id.idMetier = metierId
                entity.id = id
                entity.idFormation = formationId
                entity.idMetier = metierId
                entity
            }

        joinFormationMetiersDb.deleteAll()
        joinFormationMetiersDb.saveAll(entities)

    }

    fun clearAll() {
        joinFormationMetiersDb.deleteAll()
    }

}