package fr.gouv.monprojetsup.data.etl.metier

import fr.gouv.monprojetsup.data.commun.entity.LienEntity
import fr.gouv.monprojetsup.data.etl.MpsDataPort
import fr.gouv.monprojetsup.data.metier.entity.MetierEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository

@Repository
interface MetiersDb :
    JpaRepository<MetierEntity, String>

@Component
class UpdateMetierDbs(
    private val metiersDb: MetiersDb,
    private val mpsDataPort : MpsDataPort
) {

    fun updateMetierDbs() {
        val descriptifs = mpsDataPort.getDescriptifs()
        val labels = mpsDataPort.getLabels()
        val metiersIds = mpsDataPort.getFormationsVersMetiers().flatMap { (_,b) -> b }.toSet()
        val liens = mpsDataPort.getLiens()

        val entities = ArrayList<MetierEntity>()
        metiersIds.forEach { metierId ->
            val label = labels[metierId]
            if(label != null) {
                val entity = MetierEntity()
                entity.id = metierId
                entity.label = label
                entity.descriptifGeneral = descriptifs.getDescriptifGeneralFront(metierId)
                val liensMetier = liens[metierId]
                if(liensMetier != null) {
                    entity.liens.addAll(liensMetier.map { LienEntity(it.uri, it.label) })
                }
                entities.add(entity)
            }
        }
        metiersDb.deleteAll()
        metiersDb.saveAll(entities)
    }

    fun clearAll() {
        metiersDb.deleteAll()
    }

}