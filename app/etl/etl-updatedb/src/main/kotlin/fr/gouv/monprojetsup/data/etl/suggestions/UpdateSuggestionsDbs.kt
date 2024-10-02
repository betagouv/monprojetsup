package fr.gouv.monprojetsup.data.etl.suggestions

import fr.gouv.monprojetsup.data.etl.MpsDataPort
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsCandidatEntity
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsLabelEntity
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsMatiereEntity
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsVilleEntity
import org.hibernate.StatelessSession
import org.hibernate.Transaction
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
    private val mpsDataPort: MpsDataPort,
    private val sessionFactory: org.hibernate.SessionFactory
) {

    private val logger: Logger = Logger.getLogger(UpdateSuggestionsDbs::class.java.simpleName)

    internal fun updateSuggestionDbs() {

        logger.info("Mise à jour des voeux candidats")
        updateCandidatsDb()

        logger.info("Mise à jour des edges")
        updateEdgesDb()

        logger.info("Mise à jour des labels")
        updateLabelsDb()

        logger.info("Mise à jour des matieres")
        updateMatieresDb()

        logger.info("Mise à jour des villes")
        updateVillesDb()

    }

    private fun updateMatieresDb() {
        matieresPort.deleteAll()
        matieresPort.saveAll(mpsDataPort.getMatieres().map { SuggestionsMatiereEntity(it) })
    }

    private fun updateLabelsDb() {
        val labels = mpsDataPort.getLabels()
        val debugLabels = mpsDataPort.getDebugLabels()
        /*
        val debugLabels = HashMap(labels)
        val mpsKeyToPsupKeys = mpsDataPort.getMpsIdToPsupFlIds()
        mpsKeyToPsupKeys.forEach { (key, value) ->
            if(value.size >= 2)
                debugLabels[key] = labels[key] + Constants.GROUPE_INFIX + value.toString()
        }*/
        labelsPort.deleteAll()
        labelsPort.saveAll(labels.entries.map { SuggestionsLabelEntity(it.key, it.value, debugLabels[it.key]) })
    }

    internal fun updateCandidatsDb() {

        val statelessSession: StatelessSession = sessionFactory.openStatelessSession()
        val transaction: Transaction = statelessSession.beginTransaction()
        val hql = "DELETE FROM ${SuggestionsCandidatEntity::class.simpleName}"
        val query = statelessSession.createMutationQuery(hql)
        query.executeUpdate()

        mpsDataPort.getVoeuxParCandidat()
            .map { SuggestionsCandidatEntity(it) }
            .forEach(statelessSession::insert)

        transaction.commit()
        statelessSession.close()
    }


    internal fun updateVillesDb() {

        val statelessSession: StatelessSession = sessionFactory.openStatelessSession()
        val transaction: Transaction = statelessSession.beginTransaction()
        val hql = "DELETE FROM ${SuggestionsVilleEntity::class.simpleName}"
        val query = statelessSession.createMutationQuery(hql)
        query.executeUpdate()

        mpsDataPort.getCities()
            .flatMap {  SuggestionsVilleEntity.getEntities(it) }
            .associateBy { it.id }
            .values
            .forEach(statelessSession::insert)

        transaction.commit()
        statelessSession.close()

    }

    internal fun updateEdgesDb() {

        val statelessSession: StatelessSession = sessionFactory.openStatelessSession()
        val transaction: Transaction = statelessSession.beginTransaction()
        val hql = "DELETE FROM ${SuggestionsEdgeEntity::class.simpleName}"
        val query = statelessSession.createMutationQuery(hql)
        query.executeUpdate()

        mpsDataPort.getEdges()
            .map { SuggestionsEdgeEntity(it.first, it.second, it.third) }
            .forEach(statelessSession::insert)

        transaction.commit()
        statelessSession.close()

    }


    fun clearAll() {
        candidatsPort.deleteAll()
        villesPort.deleteAll()
        matieresPort.deleteAll()
        labelsPort.deleteAll()
        edgesPort.deleteAll()
    }


}