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
        clearAll()
        updateMetierDb()
    }


    private fun updateMetierDb() {
        val descriptifs = mpsDataPort.getDescriptifs()
        val labels = mpsDataPort.getMetiersLabels()
        val liens = mpsDataPort.getLiens()

        val entities = ArrayList<MetierEntity>()
        val metiersAssocies = mpsDataPort.getMetiersAssociesLabels()

        val metiersIds = mpsDataPort.getMetiersMpsIds()
        metiersIds.forEach { metierId ->
            val label = labels[metierId]
            if(label != null) {
                val entity = MetierEntity()
                entity.id = metierId
                entity.label = label
                val descriptifSansMetiersAssocies = descriptifs.getDescriptifGeneralFront(metierId)
                val autresMetiers = metiersAssocies[metierId].orEmpty()
                if(autresMetiers.isNotEmpty()) {
                    entity.descriptifGeneral = descriptifSansMetiersAssocies + "\n\nMétiers associés :" + autresMetiers.joinToString(", ")
                } else {
                    entity.descriptifGeneral = descriptifSansMetiersAssocies
                }
                val liensMetier = liens[metierId]
                if(liensMetier != null) {
                    entity.liens.addAll(liensMetier.map { LienEntity(it.label, it.uri) })
                }
                entities.add(entity)
            }
        }
        metiersDb.deleteAll()
        metiersDb.saveAll(entities)
    }


    fun clearAll() {
        metiersDb.deleteAll()
        metiersDb.deleteAll()
    }

}