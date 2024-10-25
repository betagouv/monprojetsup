package fr.gouv.monprojetsup.data.etl.metier

import fr.gouv.monprojetsup.data.commun.entity.LienEntity
import fr.gouv.monprojetsup.data.etl.BatchUpdate
import fr.gouv.monprojetsup.data.etl.MpsDataPort
import fr.gouv.monprojetsup.data.metier.entity.MetierEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import java.util.logging.Logger

@Repository
interface MetiersDb :
    JpaRepository<MetierEntity, String>

@Component
class UpdateMetierDbs(
    private val mpsDataPort: MpsDataPort,
    private val batchUpdate: BatchUpdate,
    private val metiersDb: MetiersDb

) {

    private val logger: Logger = Logger.getLogger(UpdateMetierDbs::class.java.simpleName)

    fun update() {
        updateMetierDb()
    }


    private fun updateMetierDb() {
        val descriptifs = mpsDataPort.getDescriptifs()
        val labels = mpsDataPort.getMetiersLabels()
        val liens = mpsDataPort.getLiens()
        val metiersAssocies = mpsDataPort.getMetiersAssociesLabels()

        val entities =
            mpsDataPort.getMetiersMpsIds().map { metierId ->
                    val label = labels[metierId]
                    val entity = MetierEntity()
                    if (label != null) {
                        entity.id = metierId
                        entity.label = label
                        entity.descriptifGeneral = descriptifs.getDescriptifGeneralFront(metierId)

                        val motsCles = arrayListOf(label)
                        motsCles.addAll(metiersAssocies[metierId].orEmpty())
                        entity.motsCles = motsCles

                        val liensMetier = liens[metierId]
                        if (liensMetier != null) {
                            entity.liens.addAll(liensMetier.map { LienEntity(it.label, it.uri) })
                        }
                    }
                    entity
                }

        val metiersIds =  entities.map { it.id }.toSet()

        val metiersObsoletes = HashSet(metiersDb.findAll())
        metiersObsoletes.removeIf { metiersIds.contains(it.id) }
        if(metiersObsoletes.isNotEmpty()) {
            logger.warning("Marquage de ${metiersObsoletes.count()} métiers obsoletes")
            metiersObsoletes.forEach { it.obsolete = true }
            batchUpdate.upsertEntities(metiersObsoletes)
        }

        logger.warning("Insertion et mise à jour de ${entities.count()} métiers")
        batchUpdate.upsertEntities(entities)

    }


}