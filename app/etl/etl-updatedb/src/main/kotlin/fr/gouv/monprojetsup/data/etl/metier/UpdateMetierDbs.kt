package fr.gouv.monprojetsup.data.etl.metier

import fr.gouv.monprojetsup.data.commun.entity.LienEntity
import fr.gouv.monprojetsup.data.etl.sources.MpsDataPort
import fr.gouv.monprojetsup.data.metier.entity.MetierEntity
import fr.gouv.monprojetsup.data.metier.infrastructure.MetiersDb
import org.springframework.stereotype.Component

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

}