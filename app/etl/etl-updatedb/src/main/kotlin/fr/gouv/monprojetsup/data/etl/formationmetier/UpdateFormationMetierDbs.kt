package fr.gouv.monprojetsup.data.etl.formationmetier

import fr.gouv.monprojetsup.data.etl.BatchUpdate
import fr.gouv.monprojetsup.data.etl.MpsDataPort
import fr.gouv.monprojetsup.data.formationmetier.entity.FormationMetierEntity
import fr.gouv.monprojetsup.data.formationmetier.entity.FormationMetierEntityId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository

@Repository
interface JoinFormationMetiersDb :
    JpaRepository<FormationMetierEntity, String>

@Component
class UpdateFormationMetierDbs(
    private val mpsDataPort: MpsDataPort,
    private val batchUpdate: BatchUpdate
) {

    fun update() {

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
                FormationMetierEntity().apply {
                    id = FormationMetierEntityId(formationId, metierId)
                    idFormation = formationId
                    idMetier = metierId
                }
            }

        batchUpdate.setEntities(
            FormationMetierEntity::class.simpleName!!,
            entities
        )
    }

}