package fr.gouv.monprojetsup.data.etl.suggestions

import fr.gouv.monprojetsup.data.etl.BatchUpdate
import fr.gouv.monprojetsup.data.etl.MpsDataPort
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsLabelEntity
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsPaniersVoeuxEntity
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsProfilEntity
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsVilleEntity
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import java.util.logging.Logger


@Repository
interface SuggestionsCandidatsDb :
    JpaRepository<SuggestionsPaniersVoeuxEntity, String>

@Repository
interface SuggestionsVillesDb :
    JpaRepository<SuggestionsVilleEntity, String>

@Repository
interface SuggestionsLabelsDb :
    JpaRepository<SuggestionsLabelEntity, String>

@Repository
interface SuggestionsEdgesDb :
    JpaRepository<SuggestionsEdgeEntity, String>

@Component
class UpdateSuggestionsDbs(
    private val mpsDataPort: MpsDataPort,
    private val batchUpdate: BatchUpdate
) {

    private val logger: Logger = Logger.getLogger(UpdateSuggestionsDbs::class.java.simpleName)

    @Value("\${mps.minimalTestDataSet}")
    var minimalTestDataSet : Boolean = false

    @Value("\${mps.data.reference.table.expert}")
    var expertReferenceTable: String = ""

    @Value("\${mps.data.reference.table.lyceen}")
    var lyceenReferenceTable: String = ""

    internal fun updateSuggestionDbs(voeuxOntChange: Boolean) {

        logger.info("Copie des profils de référence experts")
        updateProfiles(expertReferenceTable, "expert")

        logger.info("Copie des profils de référence lycéens")
        updateProfiles(lyceenReferenceTable, "lyceen")

        if(minimalTestDataSet) {
            batchUpdate.clearEntities(SuggestionsVilleEntity::class.simpleName!!)
            batchUpdate.clearEntities(SuggestionsPaniersVoeuxEntity::class.simpleName!!)
            batchUpdate.clearEntities(SuggestionsEdgeEntity::class.simpleName!!)
            batchUpdate.clearEntities(SuggestionsLabelEntity::class.simpleName!!)
        }

        if (voeuxOntChange || minimalTestDataSet) {
            logger.info("Mise à jour des paniers de voeux")
            updatePaniersVoeuxDb()
        }

        logger.info("Mise à jour des edges")
        updateEdgesDb()

        logger.info("Mise à jour des labels")
        updateLabelsDb()

        logger.info("Mise à jour des villes")
        updateVillesDb()

    }


    private fun updateProfiles(tableName: String, source: String) {

        //clear source table
        batchUpdate.clearEntities(SuggestionsProfilEntity::class.simpleName!!)
        val bacs = mpsDataPort.getBacs().map { it.key }.toSet()
        //load data from csv
        val entities = mpsDataPort.getProfilsReference(source)
            .mapIndexed { i, x -> SuggestionsProfilEntity(i,x, bacs) }
        //write to table
        batchUpdate.setEntities(
            SuggestionsProfilEntity::class.simpleName!!,
            entities
        )
        //copy data to destination table with a native sql query
        batchUpdate.setTableContent(
            SuggestionsProfilEntity.TABLE_NAME,
            tableName
        )


    }


    private fun updateLabelsDb() {
        val labels = mpsDataPort.getLabels()
        val debugLabels = mpsDataPort.getDebugLabels()

        val entities = labels.entries
            .map { SuggestionsLabelEntity(it.key, it.value, debugLabels[it.key]) }
            .associateBy { it.id }
            .values

        batchUpdate.upsertEntities(entities)
    }

    internal fun updatePaniersVoeuxDb() {
        val entities = mpsDataPort.getPaniersVoeux()
            .map { SuggestionsPaniersVoeuxEntity(it) }
        batchUpdate.setEntities(
            SuggestionsPaniersVoeuxEntity::class.simpleName!!,
            entities
        )
    }


    internal fun updateVillesDb() {
        val entities = mpsDataPort.getCities()
            .map {  SuggestionsVilleEntity.toEntity(it) }
            .associateBy { it.id }
            .values
        batchUpdate.upsertEntities(entities)
    }

    internal fun updateEdgesDb() {
        val entities = mpsDataPort.getEdges()
            .map { SuggestionsEdgeEntity(it.first, it.second, it.third) }
        batchUpdate.setEntities(
            SuggestionsEdgeEntity::class.simpleName!!,
            entities
        )
    }

}