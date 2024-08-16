package fr.gouv.monprojetsup.data.etl.formation

import fr.gouv.monprojetsup.data.UrlsUpdater
import fr.gouv.monprojetsup.data.app.commun.entity.LienEntity
import fr.gouv.monprojetsup.data.app.formation.entity.CritereAnalyseCandidatureEntity
import fr.gouv.monprojetsup.data.app.formation.entity.FormationDetailleeEntity
import fr.gouv.monprojetsup.data.app.infrastructure.CriteresDb
import fr.gouv.monprojetsup.data.app.infrastructure.FormationsDb
import fr.gouv.monprojetsup.suggestions.domain.Constants
import fr.gouv.monprojetsup.suggestions.domain.labels.Labels
import fr.gouv.monprojetsup.suggestions.infrastructure.DataSources
import fr.gouv.monprojetsup.suggestions.infrastructure.model.attendus.Attendus
import fr.gouv.monprojetsup.suggestions.infrastructure.model.attendus.GrilleAnalyse
import fr.gouv.monprojetsup.suggestions.infrastructure.model.descriptifs.DescriptifsFormations
import fr.gouv.monprojetsup.suggestions.infrastructure.model.descriptifs.DescriptifsLoader
import fr.gouv.monprojetsup.suggestions.infrastructure.model.specialites.SpecialitesLoader
import fr.gouv.monprojetsup.suggestions.infrastructure.model.stats.PsupStatistiques
import fr.gouv.monprojetsup.suggestions.infrastructure.model.tags.TagsSources
import fr.gouv.monprojetsup.suggestions.infrastructure.onisep.OnisepData
import fr.gouv.monprojetsup.suggestions.infrastructure.psup.PsupData
import fr.gouv.monprojetsup.suggestions.infrastructure.rome.RomeData
import fr.gouv.monprojetsup.suggestions.tools.Serialisation
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
class UpdateFormationDbs(
    private val criteresDb: CriteresDb,
    private val formationsdb : FormationsDb,
    private val dataSources: DataSources
) {

    internal fun updateFormationDbs() {
        updateFormationsDb()
        updateCriteresDb()
        //peupler MoyennesGeneralesAdmisDb
        //peupler TripletsAffectationDb
    }

    private val logger: Logger = Logger.getLogger(UpdateFormationDbs::class.java.simpleName)

    private fun updateFormationsDb() {
        logger.info("Chargement de " + dataSources.getSourceDataFilePath(
            DataSources.BACK_PSUP_DATA_FILENAME))
        val psupData = Serialisation.fromZippedJson(
            dataSources.getSourceDataFilePath(DataSources.BACK_PSUP_DATA_FILENAME),
            PsupData::class.java
        )
        psupData.cleanup()

        logger.info("Chargement des données Onisep")
        val onisepData = OnisepData.fromFiles(dataSources)

        logger.info("Chargement des données ROME")
        val romeData = RomeData.load(dataSources)
        logger.info("Insertion des données ROME dans les données Onisep")
        onisepData.insertRomeData(romeData.centresInterest) //before updateLabels

        logger.info("Chargement des stats depuis " + DataSources.STATS_BACK_SRC_FILENAME)
        val stats = Serialisation.fromZippedJson(
            dataSources.getSourceDataFilePath(DataSources.STATS_BACK_SRC_FILENAME),
            PsupStatistiques::class.java
        )

        stats.createGroupAdmisStatistique(psupData.psupKeyToMpsKey)
        stats.createGroupAdmisStatistique(PsupData.getGtaToLasMapping(psupData))


        updateFormationsDb(psupData, stats, onisepData)
    }

    private fun updateFormationsDb(
        psupData: PsupData,
        statistiques: PsupStatistiques,
        onisepData: OnisepData
    ) {

        val labels = Labels.getLabels(
            statistiques.nomsFilieres,
            psupData,
            onisepData,
            psupData.psupKeyToMpsKey
        )



        logger.info("Calcul des correspondance")
        val psupKeyToMpsKey = psupData.psupKeyToMpsKey
        val mpsKeyToPsupKeys = revert(psupKeyToMpsKey)

        logger.info("Génération des descriptifs")
        val descriptifs =
            DescriptifsLoader.loadDescriptifs(
                onisepData,
                psupKeyToMpsKey,
                psupData.lasToGeneric,
                dataSources
            )

        val specialites = SpecialitesLoader.load(
            statistiques,
            dataSources
        )

        val attendus = Attendus.getAttendus(
            psupData,
            statistiques,
            specialites,
            false
        )

        logger.info("Ajout des liens metiers")
        val links = HashMap<String, DescriptifsFormations.Link>()
        statistiques.liensOnisep.forEach { (key, value) ->
            links[key] = DescriptifsFormations.toAvenirs(value, labels.getOrDefault(key, ""))
        }
        val urls = UrlsUpdater.updateUrls(
            onisepData,
            links,
            psupData.lasMpsKeys,
            descriptifs,
            psupKeyToMpsKey
        )

        val grilles = GrilleAnalyse.getGrilles(psupData)

        val tagsSources = TagsSources.loadTagsSources(psupKeyToMpsKey, dataSources).getKeyToTags()

        val formationsMps = computeFormationsMps(
            psupData,
            statistiques.lasFlCodes
        )
        formationsdb.deleteAll()
        val entities = ArrayList<FormationDetailleeEntity>()
        formationsMps.forEach { flCod ->
            val entity = FormationDetailleeEntity()
            entity.id = flCod
            val label = labels[flCod] ?: throw RuntimeException("Pas de label pour la formation $flCod")
            entity.label = label
            entity.descriptifGeneral = descriptifs.getDescriptifGeneralFront(flCod)
            entity.descriptifDiplome = descriptifs.getDescriptifDiplomeFront(flCod)
            val attendusFormation = attendus[flCod]
            if (attendusFormation == null) {
                entity.descriptifConseils = null
                entity.descriptifAttendus = null
            } else {
                entity.descriptifConseils = attendusFormation.getConseilsFront()
                entity.descriptifAttendus = attendusFormation.getAttendusFront()
            }

            entity.formationsAssociees = ArrayList(mpsKeyToPsupKeys.getOrDefault(flCod, setOf(flCod)))

            val grille = grilles[flCod]
            if (grille == null) {
                entity.criteresAnalyse = listOf()
            } else {
                entity.criteresAnalyse = grille.criteresFront
            }

            val urlListe = urls.getOrDefault(flCod, ArrayList())
            entity.liens = urlListe.map { link ->
                LienEntity(link.label, link.uri)
            }.toCollection(ArrayList())

            val motsClefs = tagsSources[flCod]
            if (motsClefs == null) {
                entity.motsClefs = listOf()
            } else {
                entity.motsClefs = motsClefs
            }

            entities.add(entity)
        }
        formationsdb.saveAll(entities)
    }

    private fun revert(groups: Map<String, String>): Map<String,Set<String>> {
        val reverseGroups = HashMap<String, MutableSet<String>>()
        groups.forEach { (key: String, value: String) ->
            reverseGroups.computeIfAbsent(
                value
            ) { HashSet() }.add(key)
        }
        return reverseGroups

    }

    fun computeFormationsMps(
        backendData: PsupData,
        lasFlCodes: Collection<Int>
    ): List<String> {
        val resultInt = HashSet(backendData.filActives)
        val flGroups = backendData.psupKeyToMpsKey
        val groupesWithAtLeastOneFormation = backendData.formationToVoeux.keys

        resultInt.addAll(lasFlCodes)

        val result = ArrayList(resultInt.stream()
            .map { cle -> Constants.gFlCodToFrontId(cle) }
            .toList()
        )
        result.removeAll(flGroups.keys)
        result.addAll(flGroups.values)
        result.retainAll(groupesWithAtLeastOneFormation)
        result.sort()
        return result

    }

    private fun updateCriteresDb() {
        //clear the grilles db
        criteresDb.deleteAll()

        //insert grilles in criteresBDDRepository
        var i = 0
        GrilleAnalyse.labelsFront.forEach { triple ->
            val entity = CritereAnalyseCandidatureEntity()
            entity.id = triple.left
            entity.index = i++
            entity.nom = triple.right
            criteresDb.save(entity)
        }
    }


}