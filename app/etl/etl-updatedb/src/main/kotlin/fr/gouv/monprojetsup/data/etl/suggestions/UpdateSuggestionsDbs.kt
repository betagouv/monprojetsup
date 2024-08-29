package fr.gouv.monprojetsup.data.etl.suggestions

import fr.gouv.monprojetsup.data.domain.Constants
import fr.gouv.monprojetsup.data.domain.model.Candidat
import fr.gouv.monprojetsup.data.etl.MpsDataPort
import fr.gouv.monprojetsup.data.suggestions.entity.*
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
interface SuggestionsMatieresDb :
    JpaRepository<SuggestionsMatiereEntity, String>

@Repository
interface SuggestionsLabelsDb :
    JpaRepository<SuggestionsLabelEntity, String>

@Repository
interface SuggestionsEdgesDb :
    JpaRepository<SuggestionsEdgeEntity, String>

@Component
class UpdateSuggestionsDbs(
    private val candidatsPort: SuggestionsCandidatsDb,
    private val villesPort: SuggestionsVillesDb,
    private val matieresPort: SuggestionsMatieresDb,
    private val labelsPort: SuggestionsLabelsDb,
    private val edgesPort: SuggestionsEdgesDb,
    private val mpsDataPort: MpsDataPort
) {

    private val logger: Logger = Logger.getLogger(UpdateSuggestionsDbs::class.java.simpleName)

    internal fun updateSuggestionDbs() {

        logger.info("Updating candidats db")
        updateCandidatsDb()

        logger.info("Updating edges db")
        updateEdgesDb()

        logger.info("Updating labels db")
        updateLabelsDb()

        logger.info("Updating matieres db")
        updateMatieresDb()

        logger.info("Updating villes db")
        updateVillesDb()

    }

    private fun updateMatieresDb() {
        matieresPort.deleteAll()
        matieresPort.saveAll(mpsDataPort.getMatieres().map { SuggestionsMatiereEntity(it) })
    }

    private fun updateLabelsDb() {
        val labels = mpsDataPort.getLabels()
        val debugLabels = HashMap(labels)
        val mpsKeyToPsupKeys = mpsDataPort.getMpsIdToPsupFlIds()
        mpsKeyToPsupKeys.forEach { (key, value) ->
            if(value.size >= 2)
                debugLabels[key] = labels[key] + Constants.GROUPE_INFIX + value.toString()
        }
        labelsPort.deleteAll()
        labelsPort.saveAll(labels.entries.map { SuggestionsLabelEntity(it.key, it.value, debugLabels[it.key]) })
    }

    private fun updateCandidatsDb() {
        val candidats = mpsDataPort.getVoeuxParCandidat().map { voeuxCandidat ->
            Candidat(
                voeuxCandidat
            )
        }
        candidatsPort.deleteAll()
        candidatsPort.saveAll(candidats.map { SuggestionsCandidatEntity(it) })
    }


    private fun updateVillesDb() {
        val cities = mpsDataPort.getCities()
        villesPort.deleteAll()
        villesPort.saveAll(cities.map { SuggestionsVilleEntity(it) })
    }

    private fun updateEdgesDb() {
        val edges = mpsDataPort.getEdges()
        edgesPort.deleteAll()
        edgesPort.saveAll(edges.map { SuggestionsEdgeEntity(it.first, it.second, it.third) })
    }


    fun clearAll() {
        candidatsPort.deleteAll()
        villesPort.deleteAll()
        matieresPort.deleteAll()
        labelsPort.deleteAll()
        edgesPort.deleteAll()
    }


}