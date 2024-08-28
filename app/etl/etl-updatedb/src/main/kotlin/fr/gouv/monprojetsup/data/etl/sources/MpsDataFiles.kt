package fr.gouv.monprojetsup.data.etl.sources

import com.google.gson.reflect.TypeToken
import fr.gouv.monprojetsup.data.domain.Constants
import fr.gouv.monprojetsup.data.domain.model.Matiere
import fr.gouv.monprojetsup.data.domain.model.StatsFormation
import fr.gouv.monprojetsup.data.domain.model.Voeu
import fr.gouv.monprojetsup.data.domain.model.attendus.Attendus
import fr.gouv.monprojetsup.data.domain.model.attendus.GrilleAnalyse
import fr.gouv.monprojetsup.data.domain.model.bacs.Bac
import fr.gouv.monprojetsup.data.domain.model.descriptifs.DescriptifsFormationsMetiers
import fr.gouv.monprojetsup.data.domain.model.formations.Formation
import fr.gouv.monprojetsup.data.domain.model.graph.Edges
import fr.gouv.monprojetsup.data.domain.model.interets.Interets
import fr.gouv.monprojetsup.data.domain.model.liens.UrlsUpdater
import fr.gouv.monprojetsup.data.domain.model.onisep.InteretsOnisep
import fr.gouv.monprojetsup.data.domain.model.onisep.OnisepData
import fr.gouv.monprojetsup.data.domain.model.psup.PsupData
import fr.gouv.monprojetsup.data.domain.model.rome.RomeData
import fr.gouv.monprojetsup.data.domain.model.specialites.Specialites
import fr.gouv.monprojetsup.data.domain.model.stats.PsupStatistiques
import fr.gouv.monprojetsup.data.domain.model.thematiques.CategorieThematiques
import fr.gouv.monprojetsup.data.domain.port.EdgesPort
import fr.gouv.monprojetsup.data.etl.csv.CsvTools
import fr.gouv.monprojetsup.data.etl.loaders.DescriptifsLoader
import fr.gouv.monprojetsup.data.etl.loaders.OnisepDataLoader
import fr.gouv.monprojetsup.data.etl.loaders.RomeDataLoader
import fr.gouv.monprojetsup.data.etl.loaders.SpecialitesLoader
import fr.gouv.monprojetsup.data.etl.suggestions.labels.Labels
import fr.gouv.monprojetsup.data.formation.entity.MoyenneGeneraleAdmisId
import fr.gouv.monprojetsup.data.tools.Serialisation
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import java.util.*
import java.util.logging.Logger


@Component
class MpsDataFiles(
    private val dataSources: DataSources
) : MpsDataPort {

    private lateinit var psupData: PsupData
    private lateinit var  onisepData: OnisepData
    private lateinit var  romeData: RomeData
    private lateinit var  statistiques: PsupStatistiques

    private var descriptifs : DescriptifsFormationsMetiers? = null
    private var specialites : Specialites? = null
    private var formationsMpsIds : List<String>? = null

    private val logger: Logger = Logger.getLogger(MpsDataFiles::class.java.simpleName)
    @PostConstruct
    private fun load() {
        logger.info("Chargement de " + dataSources.getSourceDataFilePath(
            DataSources.BACK_PSUP_DATA_FILENAME))
        psupData = Serialisation.fromZippedJson(
            dataSources.getSourceDataFilePath(DataSources.BACK_PSUP_DATA_FILENAME),
            PsupData::class.java
        )
        psupData.cleanup()

        logger.info("Chargement des données Onisep")
        onisepData =
            OnisepDataLoader.fromFiles(dataSources)

        logger.info("Chargement des données ROME")
        romeData = RomeDataLoader.load(dataSources)
        logger.info("Insertion des données ROME dans les données Onisep")
        onisepData.insertRomeData(romeData.centresInterest) //before updateLabels

        logger.info("Chargement des stats depuis " + DataSources.STATS_BACK_SRC_FILENAME)
        statistiques = Serialisation.fromZippedJson(
            dataSources.getSourceDataFilePath(DataSources.STATS_BACK_SRC_FILENAME),
            PsupStatistiques::class.java
        )

        statistiques.removeSmallPopulations()
        statistiques.rebuildMiddle50()
        statistiques.createGroupAdmisStatistique(psupData.psupKeyToMpsKey)
        statistiques.createGroupAdmisStatistique(PsupData.getGtaToLasMapping(psupData))

        psupData.filActives.addAll(psupData.lasFlCodes)

        //useless for suggestions
    /*specs
    /*
        logger.info("Chargement des données B) carte parcoursup")
        val carte: JsonCarte = Serialisation.fromJsonFile(
            dataSources.getSourceDataFilePath(CARTE_JSON_PATH),
            JsonCarte::class.java
        )*/processTagSources(psupData, onisepData, statistiques)
        */

    }

    override fun getLabels(): Map<String, String> {
        return Labels.getLabels(
            psupData,
            onisepData
        )

    }

    override fun getDescriptifs(): DescriptifsFormationsMetiers {
        if(descriptifs == null) {
            descriptifs = DescriptifsLoader.loadDescriptifs(
                onisepData,
                psupData.psupKeyToMpsKey,
                psupData.lasToGeneric,
                dataSources
            )
        }
        return descriptifs!!
    }

    override fun getSpecialites(): Specialites {
        if(specialites == null) {
            specialites = SpecialitesLoader.load(
                statistiques,
                dataSources
            )
        }
        return specialites!!
    }

    override fun getAttendus(): Map<String, String> {
        val attendusPsup =  Attendus.getAttendusSimplifies(
            psupData
        )
        val mpsIds = getFormationsMpsIds()
        val mpsKeyToPsupKeys = psupData.mpsKeyToPsupKeys
        val result = HashMap<String, String>()
        mpsIds.forEach { id ->
            val psupKeys = mpsKeyToPsupKeys.getOrDefault(id, setOf(id))
            val attendusId = psupKeys.asSequence().map { attendusPsup[it] }.filterNotNull().map { att -> att.attendusFront }.distinct().joinToString("\n\n")
            if(attendusId.isNotBlank() && !attendusId.contains("null")) {
                result[id] = attendusId
            }
        }
        return result
    }

    override fun getConseils(): Map<String, String> {
        val attendusPsup =  Attendus.getAttendusSimplifies(
            psupData
        )
        val mpsIds = getFormationsMpsIds()
        val mpsKeyToPsupKeys = psupData.mpsKeyToPsupKeys
        val result = HashMap<String, String>()
        mpsIds.forEach { id ->
            val psupKeys = mpsKeyToPsupKeys.getOrDefault(id, setOf(id))
            val attendusId = psupKeys.map { attendusPsup[it] }.filterNotNull().map { att -> att.conseilsFront }.distinct().joinToString("\n\n")
            if(attendusId.isNotBlank() && !attendusId.contains("null")) {
                result[id] = attendusId
            }
        }
        return result
    }

    override fun getLiens(): Map<String, List<DescriptifsFormationsMetiers.Link>> {
        val urls = UrlsUpdater.updateUrls(
            onisepData,
            psupData.liensOnisep,
            psupData.lasToGeneric,
            getDescriptifs(),
            psupData.psupKeyToMpsKey,
            getFormationsMpsIds(),
            getLabels()
        )
        return urls
    }

    override fun getGrilles(): Map<String, GrilleAnalyse> {
        return GrilleAnalyse.getGrilles(psupData)
    }

    override fun getMotsCles(): Map<String, List<String>> {

        //log.info("Chargement des sources des mots-clés, et extension via la correspondance");
        val motsCles = psupData.motsCles;

        motsCles.sources.computeIfAbsent(
            Constants.PASS_MOT_CLE
        ) { HashSet() }.add(Constants.gFlCodToFrontId(Constants.PASS_FL_COD))

        motsCles.extendToGroups(psupData.psupKeyToMpsKey)

        val formationsVersMetiers = getFormationsVersMetiers()
        val mpsIds = getFormationsMpsIds()
        val labels = getLabels()

        //le référentiel des formations front
        mpsIds.forEach { formation ->
            val label = labels.getOrDefault(formation, formation)
            motsCles.add(label, formation)
            if (label.contains("L1")) {
                motsCles.add("licence", formation)
            }
            if (label.lowercase(Locale.getDefault()).contains("infirmier")) {
                motsCles.add("IFSI", formation)
            }
            formationsVersMetiers[formation]?.forEach { metier ->
                val labelMetier = labels[metier]
                if(labelMetier != null) {
                    motsCles.add(
                        labelMetier,
                        formation
                    )
                }
            }
        }
        motsCles.extendToGroups(psupData.psupKeyToMpsKey)
        motsCles.normalize()

        return motsCles.getKeyToTags()
    }

    override fun getFormationsMpsIds(): List<String> {
        if(formationsMpsIds == null) {
            val resultInt = HashSet(psupData.filActives)
            val flGroups = psupData.psupKeyToMpsKey
            val groupesWithAtLeastOneFormation = psupData.formationToVoeux.keys

            resultInt.addAll(psupData.lasFlCodes)

            val result = ArrayList(resultInt.stream()
                .map { cle -> Constants.gFlCodToFrontId(cle) }
                .toList()
            )
            result.removeAll(flGroups.keys)
            result.addAll(flGroups.values)
            result.retainAll(groupesWithAtLeastOneFormation)
            result.sort()
            formationsMpsIds =  result
        }
        return formationsMpsIds!!
    }

    override fun getApprentissage() : Collection<String> {
        return psupData.getApprentissage()
    }

    override fun getLasToGenericIdMapping() : Map<String,String> {
        return psupData.lasToGeneric
    }

    override fun getVoeux(): List<Voeu> {
        val formationsMps = getFormationsMpsIds()
        return psupData.getVoeux(formationsMps)
    }


    override fun getDebugLabels(): Map<String, String> {
        val mpsKeyToPsupKeys = psupData.mpsKeyToPsupKeys
        val labels = getLabels()
        val debugLabels = HashMap(labels)
        mpsKeyToPsupKeys.forEach { (key, value) ->
            if(value.size >= 2)
                debugLabels[key] = labels[key] + Constants.GROUPE_INFIX + value.toString()
        }
        return debugLabels
    }

    override fun getCapacitesAccueil(): Map<String, Int> {
        val result = HashMap<String, Int>()
        val formationsToVoeux = psupData.getFormationToVoeux()
        formationsToVoeux.forEach { (key, value) ->
            result[key] = value.stream()
                .mapToInt { f: Formation -> f.capacite }
                .sum()
        }
        return result

    }

    override fun getFormationsVersMetiers(): Map<String, Set<String>> {
        val descriptifs = DescriptifsLoader.loadDescriptifs(
            onisepData,
            psupData.psupKeyToMpsKey,
            psupData.lasToGeneric,
            dataSources
        )

        val metiersVersFormations = onisepData.getMetiersVersFormationsExtendedWithGroupsAndLASAndDescriptifs(
            psupData.psupKeyToMpsKey,
            psupData.genericToLas,
            descriptifs
        )

        val psupKeyToMpsKey = psupData.psupKeyToMpsKey

        val passKey = Constants.gFlCodToFrontId(Constants.PASS_FL_COD)
        val metiersPass = metiersVersFormations
            .filter { it.value.contains(passKey) }
            .map { it.key }
            .toSet()

        val formationsVersMetiers = HashMap<String, MutableSet<String>>()
        val lasMpsKeys = psupData.lasMpsKeys
        metiersVersFormations.forEach { (metier, formations) ->
            formations.forEach { f ->
                val metiers = formationsVersMetiers.computeIfAbsent(f) { _ -> HashSet() }
                metiers.add(metier)
                if (lasMpsKeys.contains(f)) {
                    metiers.addAll(metiersPass)
                }
                val father = psupKeyToMpsKey[f]
                if(father != null) {
                    val metiersFather = formationsVersMetiers.computeIfAbsent(father) { _ -> HashSet() }
                    metiersFather.addAll(metiers)
                }
            }
        }

        return  formationsVersMetiers
    }

    override fun getStatsFormation(): Map<String, StatsFormation> {
        val ids = getFormationsMpsIds()
        val result = HashMap<String, StatsFormation>()
        val mpsKeyToPsupKeys = psupData.mpsKeyToPsupKeys
        ids.forEach { id ->

            val psupKeys = mpsKeyToPsupKeys.getOrDefault(id, setOf(id))
            if (psupKeys.isEmpty()) throw RuntimeException("Pas de clé psup pour $id")

            result.put(
                id,
                StatsFormation(
                    statistiques.getStatsMoyGenParBac(id),
                    statistiques.getNbAdmisParBac(id),
                    statistiques.getPctAdmisParBac(id) ?: mapOf(),
                    statistiques.getNbAdmisParSpec(id) ?: mapOf(),
                    statistiques.getPctAdmisParSpec(id) ?: mapOf(),
                    psupData.getStatsFilSim(psupKeys) ?: mapOf()
                )
            )
        }
        return result
    }

    override fun getMpsIdToPsupFlIds(): Map<String, Collection<String>> {
        return psupData.mpsKeyToPsupKeys
    }
    override fun getPsupIdToMpsId(): Map<String, String> {
        return psupData.psupKeyToMpsKey
    }

    override fun getVoeuxParCandidat(): List<Set<Int>> {
        return psupData.voeuxParCandidat
    }

    override fun getLasToPasIdMapping(): Map<String, String> {
        return psupData.lasToPass
    }

    override fun getEdges(): List<Triple<String, String, Int>> {
        val result = ArrayList<Triple<String, String, Int>>()
        val psupKeyToMpsKey = getPsupIdToMpsId()
        val lasToGeneric = getLasToGenericIdMapping()
        val lasToPass = getLasToPasIdMapping()
        result.addAll(getEdges(onisepData.edgesInteretsMetiers, EdgesPort.TYPE_EDGE_INTERET_METIER))
        result.addAll(getEdges(onisepData.edgesFilieresThematiques, EdgesPort.TYPE_EDGE_FILIERES_THEMATIQUES))
        result.addAll(getEdges(onisepData.edgesThematiquesMetiers, EdgesPort.TYPE_EDGE_THEMATIQUES_METIERS))
        result.addAll(getEdges(onisepData.edgesSecteursMetiers, EdgesPort.TYPE_EDGE_SECTEURS_METIERS))
        result.addAll(getEdges(onisepData.edgesMetiersAssocies, EdgesPort.TYPE_EDGE_METIERS_ASSOCIES))
        result.addAll(getEdges(psupKeyToMpsKey, EdgesPort.TYPE_EDGE_FILIERES_GROUPES))
        result.addAll(getEdges(lasToGeneric, EdgesPort.TYPE_EDGE_LAS_TO_GENERIC))
        result.addAll(getEdges(lasToPass, EdgesPort.TYPE_EDGE_LAS_TO_PASS))
        result.addAll(getEdges(onisepData.edgesInteretsToInterets, EdgesPort.TYPE_EDGE_INTEREST_TO_INTEREST))
        return result
    }

    override fun getMatieres(): List<Matiere> {
        if(specialites == null) {
            specialites = getSpecialites()
        }
        val specs = specialites!!
        return statistiques.matieres.entries.map {
            Matiere(
                it.key,
                it.value,
                specs.isSpecialite(it.key),
                specs.getBacs(it.key)
            )
        }
    }

    private fun getEdges(edges: Map<String, String>, type: Int): List<Triple<String, String, Int>> {
        return edges.entries.map { (src, dst) -> Triple(src, dst, type) }
    }

    private fun getEdges(
        edges: Edges,
        type: Int
    ) : List<Triple<String, String, Int>> {
        return edges.edges().flatMap { (src, dsts) ->
            dsts.map { dst -> Triple(src, dst, type) }
        }
    }


    override fun getDurees(): Map<String, Int?> {
        val ids = getFormationsMpsIds()
        val mpsKeyToPsupKeys = psupData.mpsKeyToPsupKeys
        val lasKeys = psupData.lasToGeneric.keys
        val result = HashMap<String,Int?>()
        ids.forEach { id ->
            result[id] = psupData.getDuree(id, mpsKeyToPsupKeys, lasKeys)
        }
        return result
    }

    override fun getMoyennesGeneralesAdmis(): Map<MoyenneGeneraleAdmisId, List<Int>> {
        val annee = statistiques.getAnnee()
        val stats = getStatsFormation()
        val result = HashMap<MoyenneGeneraleAdmisId, List<Int>>()
        stats.forEach {
            val formationId = it.key
            val admissions = it.value.admissions
            admissions.forEach { (bac, stat) ->
                val id = MoyenneGeneraleAdmisId(annee.toString(), formationId, bac)
                val frequencesCumulees = stat.frequencesCumulees.toList()
                result[id] = frequencesCumulees
            }
        }
        return result
    }

    override fun getBacs(): List<Bac> {
        val type = object : TypeToken<List<Bac?>?>() {}.type
        return Serialisation.fromJsonFile<List<Bac>>(dataSources.getSourceDataFilePath(DataSources.BACS_FILENAME), type)
    }

    override fun getThematiques(): List<CategorieThematiques> {
        val groupes: MutableMap<String, CategorieThematiques> = HashMap()
        val categories: MutableList<CategorieThematiques> = ArrayList()

        var groupe = ""
        var emojig: String? = ""
        for (stringStringMap in CsvTools.readCSV(
            dataSources.getSourceDataFilePath(DataSources.THEMATIQUES_REGROUPEMENTS_PATH),
            '\t'
        )) {
            val id = stringStringMap["id"].orEmpty()
            if(id.isEmpty()) continue
            val regroupement = stringStringMap["regroupement"].orEmpty().trim { it <= ' ' }
            if (regroupement.isNotEmpty()) {
                groupe = regroupement
                val emojiGroupe = stringStringMap["Emoji"].orEmpty()
                if (emojiGroupe.isNotEmpty()) {
                    emojig = emojiGroupe
                } else {
                    throw java.lang.RuntimeException("Groupe " + groupe + " sans emoji dans " + DataSources.THEMATIQUES_REGROUPEMENTS_PATH)
                }
            }
            val emoji = stringStringMap.getOrDefault("Emojis", "").trim { it <= ' ' }
            val label = stringStringMap.getOrDefault("label", "").trim { it <= ' ' }
            if (groupe.isEmpty()) throw java.lang.RuntimeException("Groupe vide dans " + DataSources.THEMATIQUES_REGROUPEMENTS_PATH)
            if (emojig.orEmpty().isEmpty()) throw java.lang.RuntimeException("Groupe sans emoji dans " + DataSources.THEMATIQUES_REGROUPEMENTS_PATH)
            var cat = groupes[groupe]
            if (cat == null) {
                cat =
                    CategorieThematiques(
                        groupe,
                        emojig,
                        ArrayList()
                    )
                groupes[groupe] = cat
                categories.add(cat)
            }
            cat.items.add(
                CategorieThematiques.Item(
                    id,
                    label,
                    emoji
                )
            )
        }
        return categories
    }


    override fun getInterets() : Interets {
        val interetsOnisep = Serialisation.fromJsonFile(
            dataSources.getSourceDataFilePath(
                DataSources.INTERETS_PATH
            ),
            InteretsOnisep::class.java
        )
        val groupes = CsvTools.readCSV(dataSources.getSourceDataFilePath(DataSources.INTERETS_GROUPES_PATH), '\t')

        return Interets(interetsOnisep, groupes)

    }



}
