package fr.gouv.monprojetsup.data.etl.suggestions

import fr.gouv.monprojetsup.data.etl.BatchUpdate
import fr.gouv.monprojetsup.data.etl.MpsDataPort
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsCandidatEntity
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsLabelEntity
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsVilleEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import java.util.logging.Logger


@Repository
interface SuggestionsCandidatsDb :
    JpaRepository<SuggestionsCandidatEntity, String>

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

    internal fun updateSuggestionDbs(voeuxOntChange: Boolean) {

        if(voeuxOntChange) {
            logger.info("Mise à jour des voeux candidats")
            updateCandidatsDb()
        }

        logger.info("Mise à jour des edges")
        updateEdgesDb()

        logger.info("Mise à jour des labels")
        updateLabelsDb()

        logger.info("Mise à jour des villes")
        updateVillesDb()

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

    internal fun updateCandidatsDb() {
        val entities = mpsDataPort.getVoeuxParCandidat()
            .map { SuggestionsCandidatEntity(it) }
        batchUpdate.setEntities(
            SuggestionsCandidatEntity::class.simpleName!!,
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