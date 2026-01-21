package fr.gouv.monprojetsup.data.etl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import fr.gouv.monprojetsup.data.Constants
import fr.gouv.monprojetsup.data.Constants.DIAGNOSTICS_INFO_OUTPUT_DIR
import fr.gouv.monprojetsup.data.Constants.DIAGNOSTICS_OUTPUT_DIR
import fr.gouv.monprojetsup.data.Constants.DIAGNOSTICS_WARN_OUTPUT_DIR
import fr.gouv.monprojetsup.data.Constants.EXPLORER_AVENIRS_URL
import fr.gouv.monprojetsup.data.Constants.LAS_MPS_ID
import fr.gouv.monprojetsup.data.Constants.ONISEP_URL1
import fr.gouv.monprojetsup.data.Constants.ONISEP_URL2
import fr.gouv.monprojetsup.data.Constants.PASS_FL_COD
import fr.gouv.monprojetsup.data.Constants.gFlCodToMpsId
import fr.gouv.monprojetsup.data.Constants.gFrCodToMpsId
import fr.gouv.monprojetsup.data.Constants.gTaCodToMpsId
import fr.gouv.monprojetsup.data.Constants.isFiliere
import fr.gouv.monprojetsup.data.Constants.isMetier
import fr.gouv.monprojetsup.data.Constants.isVoeu
import fr.gouv.monprojetsup.data.Constants.mpsIdToGFlCod
import fr.gouv.monprojetsup.data.RemoteFileAccess
import fr.gouv.monprojetsup.data.etl.labels.Labels
import fr.gouv.monprojetsup.data.etl.loaders.CsvTools
import fr.gouv.monprojetsup.data.etl.loaders.DataSources
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.BACK_PSUP_DATA_FILENAME
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.CITIES_FILE_PATH
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.LIENS_MPS_PATH
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.LIENS_MPS_PATH_HEADER_EXTRAS
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.LIENS_MPS_PATH_HEADER_ID
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.MOTS_CLES_MPS_PATH
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.MPS_FORMATIONS_EXCLUES_HEADER
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.MPS_FORMATIONS_EXCLUES_PATH
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.PROFILS_REFERENCE_EXPERT_MPS_PATH
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.PROFILS_REFERENCE_LYCEEN_MPS_PATH
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.REMOTE_SHEET_COLUMNS_ATTENDUS
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.REMOTE_SHEET_COLUMNS_CONSEILS
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.REMOTE_SHEET_COLUMNS_DESCRIPTION
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.REMOTE_SHEET_COLUMNS_ETUDES_COURTES
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.REMOTE_SHEET_COLUMNS_ETUDES_LONGUES
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.REMOTE_SHEET_COLUMNS_EXTRA_IDEO_IDS
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.REMOTE_SHEET_COLUMNS_EXTRA_METIERS_IDEO_IDS
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.REMOTE_SHEET_COLUMNS_GENERIC_ID
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.REMOTE_SHEET_COLUMNS_LABEL
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.REMOTE_SHEET_COLUMNS_LIENS
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.REMOTE_SHEET_COLUMNS_MOTS_CLES
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.REMOTE_SHEET_COLUMNS_MPS_ID
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.REMOTE_SHEET_COLUMNS_PSUP_IDS
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.REMOTE_SHEET_FEUILLE_FICHES_ID
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.REMOTE_SHEET_FEUILLE_GENERIQUES_ID
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.RESUMES_GENERIQUE_ID_HEADER
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.RESUMES_MPS_ID_HEADER
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.RESUMES_MPS_PATH
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.RESUMES_RESUME_GENERIQUE_HEADER
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.RESUMES_RESUME_PRINCIPAL_HEADER
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.STATS_PSUP_DATA_FILENAME
import fr.gouv.monprojetsup.data.etl.loaders.DescriptifsLoader
import fr.gouv.monprojetsup.data.etl.loaders.OnisepDataLoader
import fr.gouv.monprojetsup.data.etl.loaders.OnisepDataLoader.loadLiensFormationsMpsDomainesMps
import fr.gouv.monprojetsup.data.etl.loaders.SpecialitesLoader
import fr.gouv.monprojetsup.data.formation.entity.MoyenneGeneraleAdmisId
import fr.gouv.monprojetsup.data.model.LatLng
import fr.gouv.monprojetsup.data.model.PanierVoeux
import fr.gouv.monprojetsup.data.model.StatsFormation
import fr.gouv.monprojetsup.data.model.Ville
import fr.gouv.monprojetsup.data.model.Voeu
import fr.gouv.monprojetsup.data.model.attendus.Attendus
import fr.gouv.monprojetsup.data.model.attendus.GrilleAnalyse
import fr.gouv.monprojetsup.data.model.bacs.Bac
import fr.gouv.monprojetsup.data.model.cities.CitiesExternal
import fr.gouv.monprojetsup.data.model.cities.Coords
import fr.gouv.monprojetsup.data.model.descriptifs.DescriptifsFormationsMetiers
import fr.gouv.monprojetsup.data.model.formations.Formation
import fr.gouv.monprojetsup.data.model.liens.UrlsUpdater
import fr.gouv.monprojetsup.data.model.liens.UrlsUpdater.CARTE_PSUP
import fr.gouv.monprojetsup.data.model.onisep.OnisepData
import fr.gouv.monprojetsup.data.model.psup.AdmissionStats
import fr.gouv.monprojetsup.data.model.psup.PsupData
import fr.gouv.monprojetsup.data.model.specialites.Specialites
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques
import fr.gouv.monprojetsup.data.model.tags.TagsFormations
import fr.gouv.monprojetsup.data.model.taxonomie.Taxonomie
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_ATOME_ELEMENT
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_DOMAINES_METIERS
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_FORMATIONS_PSUP_DOMAINES
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_FORMATION_PSUP_TO_FORMATION_MPS
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_INTERET_METIER
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_METIERS_ASSOCIES
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_METIERS_FORMATIONS_PSUP
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_SECTEURS_METIERS
import fr.gouv.monprojetsup.data.tools.CsvTools.getWriter
import fr.gouv.monprojetsup.data.tools.CsvTools.readCSV
import fr.gouv.monprojetsup.data.tools.Serialisation
import jakarta.annotation.PostConstruct
import org.apache.commons.lang3.tuple.Pair
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.file.Path
import java.util.*
import java.util.logging.Logger


data class RemoteSheet(
    val range : String,
    val majorDimension: String,
    val values: List<List<String>>
) {
    fun getValuesOfColumn(columnHeader: String): Map<String,String> {
        val index = values.firstOrNull()?.indexOf(columnHeader)
            ?: throw IllegalArgumentException("Column header '$columnHeader' not found in values")
        return values.drop(1)
            .filter { it.isNotEmpty() && it[0].isNotBlank() }
            .associate { it[0] to it.getOrElse(index) { "" } }
    }

    fun lines(): List<Map<String, String>> {
        if (values.isEmpty()) return emptyList()
        val headers = values[0]
        return values.drop(1)
            .filter { it.isNotEmpty() && it[0].isNotBlank() }
            .map { line ->
            headers.mapIndexed { index, header -> header to line.getOrElse(index) { "" } }.toMap()
        }
    }

}

@Component
class MpsDataFromFiles(
    private val dataSources: DataSources
) : MpsDataPort {

    private lateinit var psupData: PsupData
    private lateinit var onisepData: OnisepData
    private lateinit var statistiques: AdmissionStats
    private lateinit var formationsRemoteSheet: RemoteSheet
    private lateinit var formationsGeneriquesRemoteSheet: RemoteSheet
    private lateinit var formationsMpsIds : List<String>
    private lateinit var specialites : Specialites
    private lateinit var descriptifs : DescriptifsFormationsMetiers

    @Value("\${mps.data.google.use-api}")
    var useRemoteSheet : Boolean = false
    @Value("\${mps.data.google.api.uri}")
    lateinit var remoteSheetsApiUri : String
    @Value("\${mps.data.google.api.key}")
    lateinit var remoteSheetsApiKey : String
    @Value("\${mps.data.google.api.spreadsheet-id}")
    lateinit var remoteSheetsSpreadSheetId : String

    @Value("\${mps.minimalTestDataSet}")
    var minimalTestDataSet : Boolean = false

    private val logger: Logger = Logger.getLogger(MpsDataFromFiles::class.java.simpleName)
    @PostConstruct
    private fun load() {
        logger.info(
            "Chargement de " + dataSources.getSourceDataFilePath(
                BACK_PSUP_DATA_FILENAME
            )
        )
        psupData = Serialisation.fromLargeZippedJson(
            Path.of(dataSources.getSourceDataFilePath(BACK_PSUP_DATA_FILENAME)),
            PsupData::class.java
        )
        val psupStats = Serialisation.fromLargeZippedJson(
            Path.of(dataSources.getSourceDataFilePath(STATS_PSUP_DATA_FILENAME)),
            PsupStatistiques::class.java
        )
        psupData.inject(psupStats)
        psupData.initDurees()
        statistiques = psupData.buildStats()

        logger.info("Chargement des données Onisep et Rome")
        onisepData = OnisepDataLoader.fromFiles(dataSources)

        if (useRemoteSheet) {
            logger.info("Chargement des formations MPS depuis la feuille de calcul distante")
            val stream = RemoteFileAccess.getRemoteStream(getRemoteSheetUri(REMOTE_SHEET_FEUILLE_FICHES_ID))
            formationsRemoteSheet = ObjectMapper().registerKotlinModule().readValue(stream, RemoteSheet::class.java)
            check(formationsRemoteSheet.values.isNotEmpty() && formationsRemoteSheet.values[0].isNotEmpty()) { "No data found in the Sheet " }

            logger.info("Chargement des formations génériques MPS depuis la feuille de calcul distante")
            val stream2 = RemoteFileAccess.getRemoteStream(getRemoteSheetUri(REMOTE_SHEET_FEUILLE_GENERIQUES_ID))
            formationsGeneriquesRemoteSheet = ObjectMapper().registerKotlinModule().readValue(stream2, RemoteSheet::class.java)
            check(formationsGeneriquesRemoteSheet.values.isNotEmpty() && formationsGeneriquesRemoteSheet.values[0].isNotEmpty() ) { "No data found in the Sheet " }
        } else {
            logger.info("L'utilisation de la feuille de calcul distante est désactivée.")
        }

        val unsortedIds = loadMpsIds().filter { it.length >= 2 }. filter { it.substring(2).toIntOrNull() != null }
        formationsMpsIds = unsortedIds.sortedBy {  it.substring(2).toInt() }
        descriptifs = loadDescriptifs()
        specialites = SpecialitesLoader.load(
            dataSources,
            psupData.getSpesBacs()
        )

    }

    private fun loadMpsIds(): List<String> {
        //computeMpsIds
        return if (useRemoteSheet) {
            formationsRemoteSheet.getValuesOfColumn(REMOTE_SHEET_COLUMNS_MPS_ID).values.filter { it.isNotEmpty() }.toList()
        } else {
            val result = HashSet(psupData.formationsMpsIds)
            val toRemove = readCSV(dataSources.getSourceDataFilePath(MPS_FORMATIONS_EXCLUES_PATH), ',')
                .filter { it.isNotEmpty() }
                .map { it[MPS_FORMATIONS_EXCLUES_HEADER].toString() }
                .toSet()
            result.removeAll(toRemove)
            if (minimalTestDataSet) result.removeIf { !it.endsWith("11") }
            result.toList()
        }
    }

    private fun loadDescriptifs(): DescriptifsFormationsMetiers {
        val result = DescriptifsFormationsMetiers()
        DescriptifsLoader.injectFichesMetiers(onisepData.metiersIdeo, result)
        if (useRemoteSheet) {
            val linesFormations = formationsRemoteSheet.lines()
            val descriptifsGeneriques = formationsGeneriquesRemoteSheet.getValuesOfColumn(REMOTE_SHEET_COLUMNS_DESCRIPTION)
            DescriptifsLoader.addMpsdescriptifsFromRemoteSheet(
                result,
                linesFormations,
                descriptifsGeneriques,
                REMOTE_SHEET_COLUMNS_MPS_ID,
                REMOTE_SHEET_COLUMNS_GENERIC_ID,
                REMOTE_SHEET_COLUMNS_DESCRIPTION
            )
        } else {
            val lines = CsvTools.readCSV(
                dataSources.getSourceDataFilePath(RESUMES_MPS_PATH),
                ','
            )
            DescriptifsLoader.addMpsdescriptifsFromFile(
                result,
                lines,
                RESUMES_MPS_ID_HEADER,
                RESUMES_GENERIQUE_ID_HEADER,
                RESUMES_RESUME_GENERIQUE_HEADER,
                RESUMES_RESUME_PRINCIPAL_HEADER
            )
        }
        return result
    }


    override fun getLabels(): Map<String, String> {
        val formationsMpsIds = getFormationsMpsIds()
        val metiersMpsIds = getMetiersMpsIds()
        val voeuxIds = getVoeux().flatMap { it.value.map { v -> v.id } }
        val result = Labels.getLabels(
            psupData,
            onisepData,
            getSpecialites().toSpecialitesList()
        )
            .filter { !isFiliere(it.key) || formationsMpsIds.contains(it.key) }
            .filter { !isMetier(it.key) || metiersMpsIds.contains(it.key) }
            .filter { !isVoeu(it.key) || voeuxIds.contains(it.key) }
            .toMutableMap()
        if(useRemoteSheet) {
            result.putAll(formationsRemoteSheet.getValuesOfColumn(REMOTE_SHEET_COLUMNS_LABEL))
        }
        return result
    }

    override fun getDescriptifs(): DescriptifsFormationsMetiers {
        return  descriptifs
    }

    override fun getSpecialites(): Specialites {
        return specialites
    }


    override fun getDebugLabels(): Map<String, String> {
        val formationsMpsIds = getFormationsMpsIds()
        val metiersMpsIds = getMetiersMpsIds()
        val voeuxIds = getVoeux().keys
        return Labels.getDebugLabels(
            psupData,
            onisepData,
            getSpecialites().toSpecialitesList()
        )
            .filter { !isFiliere(it.key) || formationsMpsIds.contains(it.key) }
            .filter { !isMetier(it.key) || metiersMpsIds.contains(it.key) }
            .filter { !isVoeu(it.key) || voeuxIds.contains(it.key) }

    }

    private fun getLabelsOriginauxPsup(): MutableMap<String, String> {
        return Labels.getLabelsOriginauxPsup(
            psupData
        )
    }

    override fun getFormationsLabels(): Map<String, String> {
        return if(useRemoteSheet) {
            formationsRemoteSheet.getValuesOfColumn(REMOTE_SHEET_COLUMNS_LABEL)
        } else {
            val formationsMpsIds = getFormationsMpsIds()
            Labels.getFormationsLabels(
                psupData,
                false
            ).filter { formationsMpsIds.contains(it.key) }
        }
    }

    override fun getMetiersLabels(): Map<String, String> {
        val metiersMps = getMetiersMpsIds()
        return onisepData.getMetiersLabels(
            false
        ).filter { metiersMps.contains(it.key) }
    }

    override fun getMetiersAssociesLabels(): Map<String, List<String>> {
        val metiersMps = getMetiersMpsIds()
        return onisepData.getMetiersAssociesLabels()
            .filter { metiersMps.contains(it.key) }
    }

    override fun getMpsIdToIdeoIds(): Map<String, List<String>> {

        val mpsToPsup = psupData.mpsKeyToPsupKeys
        val psupToIdeo = onisepData.filieresToFormationsOnisep
            .associate { Pair(gFlCodToMpsId(it.gFlCod)!!, it.ideoFormationsIds!!) }

        val result = HashMap<String, List<String>>()
        getFormationsMpsIds().forEach { mpsId ->
            result[mpsId] =
                mpsToPsup.getOrDefault(mpsId, listOf(mpsId)).flatMap { psupToIdeo[it].orEmpty() }.toList()
        }
        if (useRemoteSheet) {
            formationsRemoteSheet.getValuesOfColumn(REMOTE_SHEET_COLUMNS_EXTRA_IDEO_IDS)
                .forEach { (mpsId, extraIds) ->
                    val extraIdeos = extraIds.split(";")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }
                    if (extraIdeos.isNotEmpty()) {
                        result[mpsId] = result.getOrDefault(mpsId, emptyList()) + extraIdeos
                    }
                }
        }
        return result
    }


    override fun getFormationToTypeformation(): Map<String, String> {
        val result = HashMap<String, String>()
        psupData.formations.filieres.values.forEach{ f ->
            result[gFlCodToMpsId(f.gFlCod)] = gFrCodToMpsId(f.gFrCod)
            if(f.gFlCodeFi > 0) {
                result[gFlCodToMpsId(f.gFlCodeFi)] = gFrCodToMpsId(f.gFrCod)
            }
            result[gFrCodToMpsId(f.gFrCod)] = gFrCodToMpsId(f.gFrCod)
        }
        return result
    }


    override fun getProfilsReference(source: String): List<Map<String,String>> {
        when(source) {
            "expert" -> {
                return CsvTools.readCSV(dataSources.getSourceDataFilePath(PROFILS_REFERENCE_EXPERT_MPS_PATH), ',')
            }
            "lyceen" -> {
                return CsvTools.readCSV(dataSources.getSourceDataFilePath(PROFILS_REFERENCE_LYCEEN_MPS_PATH), ',')
            }
        }
        throw IllegalArgumentException("Source de profils inconnue: $source")
    }

    override fun getCompatEtudesCourtes(): Set<String> {
        return if(useRemoteSheet) {
            formationsRemoteSheet.getValuesOfColumn(REMOTE_SHEET_COLUMNS_ETUDES_COURTES).filter { it.value.isNotBlank() }.keys
        } else {
            val durees = getDurees()
            durees.entries.filter { it.value != null && it.value!! <= 3 }.map { it.key }.toSet()
        }
    }

    override fun getCompatEtudesLongues(): Set<String> {
        return if(useRemoteSheet) {
            formationsRemoteSheet.getValuesOfColumn(REMOTE_SHEET_COLUMNS_ETUDES_LONGUES).filter { it.value.isNotBlank() }.keys
        } else {
            val durees = getDurees()
            durees.entries.filter { it.value != null && it.value!! >= 3 }.map { it.key }.toSet()
        }
    }

    override fun getAttendus(): Map<String, String> {
        return if(useRemoteSheet) {
            formationsRemoteSheet.getValuesOfColumn(REMOTE_SHEET_COLUMNS_ATTENDUS)
        } else {
            val attendusPsup = Attendus.getAttendusSimplifies(
                psupData
            )
            val mpsIds = getFormationsMpsIds()
            val mpsKeyToPsupKeys = psupData.mpsKeyToPsupKeys
            val labels = getLabels()
            val allPsupKeys = mpsIds.flatMap { mpsKeyToPsupKeys.getOrDefault(it, setOf(it)) }
            val allTexts = allPsupKeys.associateWith { psupKey -> attendusPsup[psupKey]?.attendusFront.orEmpty() }
            mergeAttendusOrConseils(allTexts, mpsIds, mpsKeyToPsupKeys, labels)
        }
    }

    override fun getConseils(): Map<String, String> {
        return if(useRemoteSheet) {
            formationsRemoteSheet.getValuesOfColumn(REMOTE_SHEET_COLUMNS_CONSEILS)
        } else {
            val attendusPsup = Attendus.getAttendusSimplifies(
                psupData
            )
            val mpsIds = getFormationsMpsIds()
            val mpsKeyToPsupKeys = psupData.mpsKeyToPsupKeys
            val labels = getLabels()
            val allPsupKeys = mpsIds.flatMap { mpsKeyToPsupKeys.getOrDefault(it, setOf(it)) }
            val allTexts = allPsupKeys.associateWith { psupKey -> attendusPsup[psupKey]?.conseilsFront.orEmpty() }
            mergeAttendusOrConseils(allTexts, mpsIds, mpsKeyToPsupKeys, labels)
        }
    }

    private fun mergeAttendusOrConseils(
        allTexts: Map<String, String>,
        mpsIds: List<String>,
        mpsKeyToPsupKeys: Map<String, MutableSet<String>>,
        labels: Map<String, String>
    ): Map<String, String> {
        val result = HashMap<String, String>()
        mpsIds.forEach { id ->
            val psupKeys = mpsKeyToPsupKeys.getOrDefault(id, setOf(id))
            val formuleVersLibelles = HashMap<String, MutableList<String>>()
            psupKeys.forEach { psupKey ->
                val formule = allTexts[psupKey]
                val libelle = labels.getOrDefault(psupKey, "")
                if (formule != null && !formule.contains("null") && formule.isNotBlank() && libelle.isNotBlank()) {
                    val l = formuleVersLibelles.computeIfAbsent(formule) { ArrayList() }
                    l.add(libelle)
                }
            }
            val texte: String = if (formuleVersLibelles.size <= 1) {
                formuleVersLibelles.keys.firstOrNull().orEmpty()
            } else {
                formuleVersLibelles.entries.joinToString("\n\n") { (formule, libelles) ->
                    val libellesTexte = libelles.joinToString(" - ")
                    "$libellesTexte: $formule"
                }
            }
            if (texte.isNotBlank()) {
                result[id] = texte
            }
        }
        return result
    }


    override fun getCities(): List<Ville> {
        val citiesOld = Serialisation.fromJsonFile(
            dataSources.getSourceDataFilePath(CITIES_FILE_PATH),
            CitiesExternal::class.java
        )

        //indexation département --> villes du département
        val mByDpt = HashMap<String, Pair<String, MutableList<Coords>>>()
        citiesOld.cities()
            .filter { c -> c.zip_code != null }
            .forEach { c ->
                var key = c.name()
                key += c.zip_code().toInt() / 1000
                val paireNomCoords = mByDpt.computeIfAbsent(key) { _ ->
                    Pair.of(
                        c.name(),
                        ArrayList()
                    )
                }
                paireNomCoords.right.add(
                    Coords(
                        c.zip_code(),
                        c.insee_code(),
                        c.gps_lat(),
                        c.gps_lng()
                    )
                )
            }
        val cities: HashMap<String, Ville> = HashMap()
        mByDpt.values.forEach { value: Pair<String, MutableList<Coords>> ->
            //dans un même département on regroupe toutes les coordonnées à nom fixé.
            //Par exemple Lyon regroupe différents code insee pour ses différents arrondissements.
            val nom = value.left
            val coords = value.right
            if (coords != null) {
                val gpsCoords: List<LatLng> = coords
                    .filter { it.gps_lat != null && it.gps_lng != null }
                    .map {
                        LatLng(
                            it.gps_lat,
                            it.gps_lng
                        )
                    }
                if (gpsCoords.isNotEmpty()) {
                    coords.forEach { c: Coords ->
                        if(
                            c.insee_code != null
                            && (!minimalTestDataSet || c.insee_code.endsWith("20"))
                        ) {
                            cities[c.insee_code] = Ville(
                                c.insee_code,
                                nom,
                                gpsCoords
                            )
                        }
                    }
                }
            }
        }
        val voeuxSansCommune = getVoeux().flatMap { it.value }.filter { !cities.containsKey(it.codeCommune) }
        voeuxSansCommune.forEach { v ->
            if(v.lat != null && v.lng != null) {
                cities[v.codeCommune] = Ville(
                    v.codeCommune,
                    v.commune,
                    listOf(LatLng(v.lat!!, v.lng!!))
                )
            }
        }
        return cities.values.toList()
    }

    override fun getLiens(): Map<String, List<DescriptifsFormationsMetiers.Link>> {

        val extraLiens = if(useRemoteSheet) {
            getLiensMpsExtras(formationsRemoteSheet.lines(), REMOTE_SHEET_COLUMNS_MPS_ID, REMOTE_SHEET_COLUMNS_LIENS)
        } else {
            val lines = CsvTools.readCSV(
                dataSources.getSourceDataFilePath(LIENS_MPS_PATH),
                ','
            )
            getLiensMpsExtras(lines, LIENS_MPS_PATH_HEADER_ID, LIENS_MPS_PATH_HEADER_EXTRAS)
        }
        return UrlsUpdater.updateUrls(
            onisepData.metiersIdeo,
            getMpsIdToIdeoIds(),
            psupData.psupKeyToMpsKey,
            onisepData.liensCarteParcoursup,
            getFormationsMpsIds(),
            getLabels(),
            getLabelsOriginauxPsup(),
            extraLiens
        )
    }


    private fun getLiensMpsExtras(
        lines: List<Map<String, String>>,
        mpsIdHeader: String,
        liensHeader: String
    ): Map<String, Collection<String>> {
        val result = HashMap<String, Collection<String>>()
        for (line in lines) {
            val key = line[mpsIdHeader] ?: throw java.lang.RuntimeException("Empty $mpsIdHeader in $line")
            val urls = line[liensHeader] ?: throw java.lang.RuntimeException("Empty $liensHeader in $line")
            val urlList = urls
                .split("\n")
                .map { s -> s.trim()}
                .map { s -> s.replace(ONISEP_URL1,EXPLORER_AVENIRS_URL) }
                .map { s -> s.replace(ONISEP_URL2,EXPLORER_AVENIRS_URL) }
            result[key] = urlList
        }
        return result
    }

    override fun getGrilles(): Map<String, GrilleAnalyse> {
        return GrilleAnalyse.getGrilles(psupData)
    }

    override fun getMotsClesFormations(): Map<String, List<String>> {

        //log.info("Chargement des sources des mots-clés, et extension via la correspondance");
        val motsClesPsup = psupData.motsCles

        val motsCleMps = Serialisation.fromJsonFile(dataSources.getSourceDataFilePath(MOTS_CLES_MPS_PATH), TagsFormations::class.java)
        motsCleMps.tags.forEach { (key, value) ->
            motsClesPsup.add(value, key)
        }

        motsClesPsup.extendToGroups(psupData.psupKeyToMpsKey)

        val labels = getLabels()

        val formationsVersMetiers = getFormationsVersMetiersEtMetiersAssocies()

        val mpsToIdeo = getMpsIdToIdeoIds()

        val formationsIdeo = onisepData.formationsIdeo.associateBy { it.ideo }

        val mpsIds = getFormationsMpsIds()

        //le référentiel des formations front
        mpsIds.forEach { mpsId ->
            val label = labels.getOrDefault(mpsId, mpsId)
            motsClesPsup.add(label, mpsId)
            //recherche par clé
            motsClesPsup.add(mpsId + "x", mpsId)
            if(mpsToIdeo.containsKey(mpsId)) {
                val ideoKeys = mpsToIdeo[mpsId].orEmpty()
                ideoKeys.forEach { ideoKey ->
                    val formationIdeo = formationsIdeo[ideoKey]
                    if (formationIdeo != null) {
                        motsClesPsup.add(formationIdeo.motsCles, mpsId)
                    }
                }
            }
            formationsVersMetiers[mpsId]?.forEach { idMetierOuMetierAssocie ->
                val labelMetier = labels[idMetierOuMetierAssocie]
                if(labelMetier != null) {
                    motsClesPsup.add(labelMetier, mpsId)
                }
            }
        }
        motsClesPsup.extendToGroups(psupData.psupKeyToMpsKey)

        val extras = if(useRemoteSheet) {
            formationsRemoteSheet.getValuesOfColumn(REMOTE_SHEET_COLUMNS_MOTS_CLES)
        } else {
            mapOf("fl550001" to "IFSI")
        }
        extras.forEach { (mpsId, motsCles) ->
            motsCles.split(";").map { it.trim() }.filter { it.isNotBlank() }.forEach { motCle ->
                motsClesPsup.add(motCle, mpsId)
            }
        }

        motsClesPsup.normalize()
        return motsClesPsup.getKeyToTags()
    }

    override fun getMetiersMpsIds(): List<String> {
        return onisepData.metiersIdeo.asSequence()
            .map { it.ideo() }.toList().sorted()
            .filter { !minimalTestDataSet || it.endsWith("7") }
            .toList()
    }

    override fun getFormationsMpsIds(): List<String> {
        return formationsMpsIds
    }

    override fun getApprentissage() : Collection<String> {
        return psupData.getApprentissage().entries.filter { it.value > 0 }.map { it.key }
    }

    override fun getApprentissagePct() : Map<String,Int> {
        return psupData.getApprentissage()
    }

    override fun getVoeux(): Map<String, Collection<Voeu>> {
        val formationsMps = getFormationsMpsIds()
        return psupData.getVoeuxGroupedByFormation(formationsMps)
            .entries
            .associate{ it.key to it.value.filter { itt -> !minimalTestDataSet || itt.id.endsWith("7") } }
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

    /**
     * metiers vers filieres
     * @return a map metiers -> filieres
     */
    private fun getMetiersVersFormationsExtendedWithGroups(
        edgesMetiersFormations: List<Pair<String, String>>,
        psupKeyToMpsKey: Map<String?, String>
    ): Map<String, Set<String>> {
        val metiersVersFormations: MutableMap<String, MutableSet<String>> = HashMap()

        edgesMetiersFormations.forEach { p ->
            metiersVersFormations.computeIfAbsent(p.left) { HashSet() }.add(p.right)
        }

        metiersVersFormations.keys.removeIf { k -> !isMetier(k) }

        metiersVersFormations.values.forEach { strings ->
            strings.removeIf { s -> !isFiliere(s) }
        }

        /* ajouts des las aux metiers PASS.*/
        val passKey = gFlCodToMpsId(PASS_FL_COD)
        val metiersPass = metiersVersFormations.entries
                .filter { e ->  e.value.contains(passKey) }
                .map { z -> z.key }
                .toSet()
        metiersPass.forEach { m ->
            metiersVersFormations.computeIfAbsent(m) { HashSet() }.add(LAS_MPS_ID)
        }
        metiersVersFormations.entries.forEach { e ->
            val mpsFormationsKeysBase = HashSet(e.value)
            val mpsFormationsKeys = HashSet(mpsFormationsKeysBase)
            mpsFormationsKeysBase.forEach { mpsKey ->
                /* ajouts des groupes génériques aux metiers des formations correspondantes */
                mpsFormationsKeys.add(psupKeyToMpsKey.getOrDefault(mpsKey, mpsKey))
            }
            e.setValue(mpsFormationsKeys)
        }

        if(useRemoteSheet) {
            formationsRemoteSheet.getValuesOfColumn(REMOTE_SHEET_COLUMNS_EXTRA_METIERS_IDEO_IDS)
                .forEach { (mpsId, extraIds) ->
                    extraIds.split(";").map { it.trim() }.filter { it.isNotBlank() }.forEach {
                        extraId -> metiersVersFormations.computeIfAbsent(extraId) { HashSet() }.add(mpsId)
                    }
                }
        }

        return metiersVersFormations
    }

    override fun getFormationsVersMetiersEtMetiersAssocies(): Map<String, Set<String>> {
        val metiersVersFormations = getMetiersVersFormationsExtendedWithGroups(
            onisepData.edgesMetiersFormations,
            psupData.psupKeyToMpsKey
        )
        val psupKeyToMpsKey = psupData.psupKeyToMpsKey
        val formationsVersMetiers = HashMap<String, MutableSet<String>>()
        metiersVersFormations.forEach { (metier, formations) ->
            formations.forEach { f ->
                val metiers = formationsVersMetiers.computeIfAbsent(f) { _ -> HashSet() }
                metiers.add(metier)
                val father = psupKeyToMpsKey[f]
                if(father != null) {
                    val metiersFather = formationsVersMetiers.computeIfAbsent(father) { _ -> HashSet() }
                    metiersFather.addAll(metiers)
                }
            }
        }
        if(useRemoteSheet) {
            formationsRemoteSheet.getValuesOfColumn(REMOTE_SHEET_COLUMNS_EXTRA_METIERS_IDEO_IDS)
                .forEach { (mpsId, extraIds) ->
                    extraIds.split(";").map { it.trim() }.filter { it.isNotBlank() }.forEach {
                            extraId -> formationsVersMetiers.computeIfAbsent(mpsId) { HashSet() }.add(extraId)
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
            val stat = StatsFormation(
                statistiques.getStatsMoyGenParBac(id).entries.associateByTo(HashMap(), { it.key }, { it.value.middle50 }),
                statistiques.getNbAdmisParBac(id),
                statistiques.getPctAdmisParSpec(id),
                psupData.getStatsFilSim(psupKeys)
            )
            result[id] = stat
        }
        return result
    }

    override fun getMpsIdToPsupFlIds(): Map<String, Collection<String>> {
        return if(useRemoteSheet) {
            formationsRemoteSheet.getValuesOfColumn(REMOTE_SHEET_COLUMNS_PSUP_IDS)
                .mapValues { it.value.split(";").map { s -> s.trim() }.filter { s -> s.isNotBlank() } }
        } else {
            val ids = getFormationsMpsIds()
            val mpsKeyToPsupKeys = psupData.mpsKeyToPsupKeys
            ids.associateWith { mpsKeyToPsupKeys.getOrDefault(it, setOf(it)) }
        }
    }

    override fun getPsupIdToMpsId(): Map<String, String> {
        return getMpsIdToPsupFlIds().entries.flatMap { it.value.map { itt -> Pair(it.key, itt) } }
            .associate { it.second to it.first }
    }

    override fun getPaniersVoeux(): List<PanierVoeux> {
        val idVoeuxConnus = getVoeux().values.flatten()
            .map { it.id }.distinct().toSet()
        return psupData.voeuxParCandidat
            .map { p ->
            val id = p.bac
            val voeux = p.voeux.filter { v -> idVoeuxConnus.contains(v) }
            PanierVoeux(id, voeux, p.lettres)
        }.filter { it.voeux.isNotEmpty() }.take(if (minimalTestDataSet) 500 else Int.MAX_VALUE)
    }

    override fun getEdges(): List<Triple<String, String, Int>> {
        val result = ArrayList<Triple<String, String, Int>>()

        val psupToMps = HashMap(getPsupIdToMpsId())
        psupToMps.values.retainAll(getFormationsMpsIds().toSet())

        result.addAll(getEdges(onisepData.edgesAtomeToElement, TYPE_EDGE_ATOME_ELEMENT))
        result.addAll(getEdges(onisepData.edgesInteretsMetiers, TYPE_EDGE_INTERET_METIER))
        result.addAll(getEdges(onisepData.edgesFormationsDomaines, TYPE_EDGE_FORMATIONS_PSUP_DOMAINES))

        //injection patch JMB
        val mpsFormationsMpsDomaines = loadLiensFormationsMpsDomainesMps(dataSources)
        mpsFormationsMpsDomaines.forEach{ (src, dsts) ->
            dsts.forEach{
                result.add(Triple(src, it, TYPE_EDGE_FORMATIONS_PSUP_DOMAINES))
            }
        }

        result.addAll(getEdges(onisepData.edgesMetiersFormations, TYPE_EDGE_METIERS_FORMATIONS_PSUP))
        result.addAll(getEdges(onisepData.edgesDomainesMetiers, TYPE_EDGE_DOMAINES_METIERS))
        result.addAll(getEdges(onisepData.edgesSecteursMetiers, TYPE_EDGE_SECTEURS_METIERS))
        result.addAll(getEdges(onisepData.edgesMetiersAssocies, TYPE_EDGE_METIERS_ASSOCIES))
        result.addAll(getEdges(psupToMps, TYPE_EDGE_FORMATION_PSUP_TO_FORMATION_MPS))

        val metiersIds = getMetiersMpsIds()
        result.removeIf { (src, _, _) -> isMetier(src) && !metiersIds.contains(src) }
        result.removeIf { (_, dst, _) -> isMetier(dst) && !metiersIds.contains(dst) }

        return result
    }

    private fun getEdges(
        m: List<Pair<String, String>>,
        t: Int
    ): Collection<Triple<String, String, Int>> {
        return m.map { (src, dst) -> Triple(src, dst, t) }
    }

    @Suppress("SameParameterValue")
    private fun getEdges(edges: Map<String, String>, type: Int): List<Triple<String, String, Int>> {
        return edges.entries.map { (src, dst) -> Triple(src, dst, type) }
    }


     private fun getDurees(): Map<String, Int?> {
        return if(useRemoteSheet) {
            val lines = formationsRemoteSheet.lines()
             lines.associate {
                val duree = if (!it[REMOTE_SHEET_COLUMNS_ETUDES_LONGUES].isNullOrBlank()) {
                        5
                    } else if (!it[REMOTE_SHEET_COLUMNS_ETUDES_COURTES].isNullOrBlank()) {
                        1
                    } else {
                        3
                    }
                it.getOrElse(REMOTE_SHEET_COLUMNS_MPS_ID){""} to duree
            }
        } else {
            val ids = getFormationsMpsIds()
            val mpsKeyToPsupKeys = psupData.mpsKeyToPsupKeys
            val result = HashMap<String, Int?>()
            ids.forEach { id ->
                var duree = psupData.getDuree(id, mpsKeyToPsupKeys)
                if (duree == null && Constants.isPsupFiliere(id)) {
                    try {
                        val codeFilierePsup = mpsIdToGFlCod(id)
                        val filiere = psupData.filieres()[codeFilierePsup]
                        if (filiere != null) {
                            duree = psupData.getDuree(filiere)
                        }
                    } catch (e: NumberFormatException) {
                        //ignore
                    }
                }
                result[id] = duree
            }
            result
        }
    }

    override fun getMoyennesGeneralesAdmis(): Map<MoyenneGeneraleAdmisId, List<Int>> {
        val annee = statistiques.annee.toString()
        val result = HashMap<MoyenneGeneraleAdmisId, List<Int>>()
        val bacs = getBacs().map { it.key }.toSet()

        getFormationsMpsIds().forEach { id ->
            statistiques.getStatsMoyGenParBac(id).forEach { (bac, stat) ->
                if(bacs.contains(bac)) {
                    val moyGenId = MoyenneGeneraleAdmisId(annee, id, bac)
                    result[moyGenId] = stat.frequencesCumulees.toList()
                }
            }
        }

        return result
    }

    override fun getBacs(): List<Bac> {
        val result = ArrayList(psupData.bacs)
        if(!result.any { it.key == PsupStatistiques.TOUS_BACS_CODE_MPS }) {
            result.add(Bac(PsupStatistiques.TOUS_BACS_CODE_MPS, "Non communiqué"))
        }
        return result
    }

    override fun getDomaines(): Taxonomie {
        return onisepData.domaines
    }


    override fun getInterets() : Taxonomie {
        return onisepData.interets

    }

    private fun exportLiensFormationsMetiersDiagnostics(
        labels: Map<String, String>,
        logLiens: MutableMap<Pair<String, String>, MutableList<String>>
    ) {
        val filename = DIAGNOSTICS_OUTPUT_DIR + "formations_metiers_checked.csv"

        if(!java.io.File(filename).exists()) {
            logger.info("En l'absence du fichier $filename, pas de vérification des liens")
            return
        }

        logLiens.keys.removeIf { it.left.startsWith("FOR.") }

        val original = HashMap<Pair<String, String>, Map<String, String>>()

        val checked: MutableSet<Pair<String, String>> = HashSet()
        val error = HashSet<Pair<String, String>>()
        val csv = CsvTools.readCSV(filename)
        for (strMap in csv) {
            if (strMap.values.stream().allMatch { it.isBlank() }) continue

            val idMps = strMap["id formation MPS"] ?: throw java.lang.RuntimeException("id formation MPS manquant")

            val idMetier = strMap["id metier IDEO"] ?: throw java.lang.RuntimeException("id formation MPS manquant")

            val commentaire = strMap["commentaire"] ?: throw java.lang.RuntimeException("commentaire manquant")

            val pair = Pair.of(idMps.trim(), idMetier.trim())

            if (commentaire.lowercase(Locale.getDefault()).contains("ok")) {
                checked.add(pair)
            } else if (commentaire.lowercase(Locale.getDefault()).contains("supp")) {
                error.add(pair)
            }
            original[pair] = strMap
        }

        val mpsToIdeo = getMpsIdToIdeoIds()
        val capacites = getCapacitesAccueil()
        val metiersformations = onisepData.edgesMetiersFormations
        ///turn into map
        val formationsEdgesSortedMap = metiersformations.groupBy { it.right }.toMap()
        val formationsEdgesSorted = formationsEdgesSortedMap.entries.sortedBy { - capacites.getOrDefault(it.key,0) }

        getWriter(DIAGNOSTICS_OUTPUT_DIR + "formations_metiers_audit.csv").use { tools ->
            tools.appendHeaders(
                listOf(
                    "Documentaliste",
                    "id formation MPS",
                    "nom formation MPS",
                    "id metier IDEO",
                    "nom metier IDEO",
                    "ids formations IDEO",
                    "noms formations IDEO",
                    "capacite accueil",
                    "commentaire",
                    "sources"
                )
            )

            for (keyLabelEdgesFormation in formationsEdgesSorted) {
                val idFormationMps = keyLabelEdgesFormation.key
                val nomFormationMps = labels[idFormationMps]

                val idsFormationsIdeo = mpsToIdeo[idFormationMps].orEmpty()
                var nomsFormationsIdeo: String =
                    java.lang.String.join(
                        "\n", idsFormationsIdeo.stream()
                            .distinct()
                            .map { s -> labels.getOrDefault(s,s) }
                            .sorted()
                            .toList()
                    )
                if(nomsFormationsIdeo.length > 500) {
                    nomsFormationsIdeo = nomsFormationsIdeo.substring(0,500) + "\n..."
                }

                val listeMetiers = keyLabelEdgesFormation.value.map { it.left }.filter { it.startsWith("MET.") }.distinct().sorted()

                val capacite = capacites.getOrDefault(idFormationMps, 0)
                for (idMetier in listeMetiers) {
                    val p = Pair.of(idFormationMps, idMetier)
                    val data = original.getOrDefault(p, mapOf())
                    val sources = if(error.contains(p)) {
                        val liste = logLiens.getOrDefault(p, mutableListOf()).filter { it.isNotBlank() }
                        liste.joinToString("\n")
                    } else {
                        ""
                    }
                    tools.append(
                        listOf(
                            data.getOrDefault("Documentaliste", ""),
                            idFormationMps,
                            nomFormationMps,
                            idMetier,
                            labels[idMetier],
                            java.lang.String.join(" ; ", idsFormationsIdeo),
                            nomsFormationsIdeo,
                            capacite.toString(),
                            data.getOrDefault("commentaire", ""),
                            sources
                        )
                    )
                }
            }
        }
    }

    private fun exportFormationsMpsAvecDescriptifVide() {
        val descriptifs = getDescriptifs()
        val descriptifsVide =
            descriptifs.keyToDescriptifs().entries.filter { it.value.descriptifGeneralFront.isNullOrBlank() }
                .map { it.key }.toSet()
        if (descriptifsVide.isEmpty()) {
            logger.info("Aucune filière PSUP avec descriptif vide")
            return
        } else {
            val labels = getLabels()
            CsvTools.getWriter(DIAGNOSTICS_OUTPUT_DIR + "formations_mps_descriptif_vide.csv").use { csv ->
                csv.appendHeaders(
                    listOf(
                        "code",
                        "libellé",
                    )
                )
                descriptifsVide.forEach { flCodStr: String ->
                    val label = labels.getOrDefault(flCodStr, flCodStr)
                    csv.append(
                        listOf(
                            flCodStr,
                            label
                        )
                    )
                }
            }
        }
    }


    private fun exportLiens() {
        val labels = getLabels()
        CsvTools.getWriter(DIAGNOSTICS_OUTPUT_DIR + "liens2.csv").use { csv ->
            val headers = listOf(
                "id",
                "formation",
                "label",
                "url"
            )
            csv.appendHeaders(headers)
            val liens = getLiens()
            for(id in getFormationsMpsIds().sortedBy { labels.getOrDefault(it,it) }) {
                val label = labels[id].orEmpty()
                for(lien in liens[id].orEmpty().filter { !it.source.contains(CARTE_PSUP) }) {
                    val nextLine = listOf(
                        id,
                        label,
                        lien.label,
                        lien.uri
                    )
                    csv.append(nextLine)
                }
            }
        }

    }

    private fun exportFormationsSansVoeux() {
        val formationsIds = getFormationsMpsIds()
        assert(formationsIds.isNotEmpty())
        val formationsAvecVoeux = getVoeux().filter { it.value.isNotEmpty() }.map { it.key }.toSet()
        val formationSansVoeux = formationsIds - formationsAvecVoeux
        if(formationSansVoeux.isEmpty()) {
            logger.info("Aucune fiche mps sans voeux")
            return
        } else {
            createCsvListeOfMpsFormations(DIAGNOSTICS_WARN_OUTPUT_DIR + "fiches_mps_sans_voeux.csv", formationSansVoeux)
        }
    }

    private fun exportVoeuxSansFormation() {
        val mpsIds = getFormationsMpsIds()
        val psupIndextoMpsIndex = psupData.getPsupKeyToMpsKey()

        val voeuxOrphelins = psupData.formations.formations.values.filter {
            val mpsids = ArrayList<String?>()
            val candidateMpsKey = gFlCodToMpsId(it!!.gFlCod)
            var mpsKey: String? = psupIndextoMpsIndex.get(candidateMpsKey)
            if (mpsKey == null && mpsIds.contains(candidateMpsKey)) {
                mpsKey = candidateMpsKey
            }
            if (mpsKey != null) {
                mpsids.add(mpsKey)
            }
            if (it.isLAS()) {
                mpsids.add(LAS_MPS_ID)
            }
            if (it.isPPPE()) {
                mpsids.add(Constants.PPPE_MPS_ID)
            }
            mpsids.isEmpty()
        }
        if(voeuxOrphelins.isEmpty()) {
            logger.info("Aucun voeu sans fiche mps")
            return
        } else {
            CsvTools.getWriter(DIAGNOSTICS_WARN_OUTPUT_DIR + "voeux_psup_sans_fiche_mps.csv").use { csv ->
                csv.appendHeaders(
                    listOf(
                        "ta_cod",
                        "fl_cod",
                        "libellé",
                    )
                )
                voeuxOrphelins.sortedBy { it.gFlCod }.forEach {
                    csv.append(
                        listOf(
                            gTaCodToMpsId(it.gTaCod),
                            gFlCodToMpsId(it.gFlCod),
                            it.libelle
                        )
                    )
                }
            }
            CsvTools.getWriter(DIAGNOSTICS_WARN_OUTPUT_DIR + "filieres_psup_sans_fiche_mps.csv").use { csv ->
                csv.appendHeaders(
                    listOf(
                        "fl_cod",
                        "libellé",
                    )
                )
                val filieresPsupOrphelines = voeuxOrphelins.map { it.gFlCod }.distinct()
                    .map { it to (psupData.formations.filieres[it]?.libelle ?: "Filière inconnue") }
                    .sortedBy { it.second }
                filieresPsupOrphelines.forEach {
                    csv.append(
                        listOf(
                            gFlCodToMpsId(it.first),
                            it.second
                        )
                    )
                }
            }
        }
    }

    private fun createCsvListeOfMpsFormations(filename : String, formationsIds: Collection<String>) {
        val labels = getLabels()
        CsvTools.getWriter(filename).use { csv ->
            csv.appendHeaders(
                listOf(
                    "mps_id",
                    "label"
                )
            )
            formationsIds.forEach { mpsId ->
                csv.append(
                    listOf(
                        mpsId,
                        labels.getOrDefault(mpsId, "mpsId")
                    )
                )
            }
        }
    }
    private fun exportFormationsSansLiens() {
        val formationsIds = getFormationsMpsIds()
        val liens = getLiens()

        val formationsSansLiens = formationsIds.filter { liens.getOrDefault(it, listOf()).isEmpty() }
        if(formationsSansLiens.isEmpty()) {
            logger.info("Aucune fiche mps sans lien")
            return
        } else {
            createCsvListeOfMpsFormations(DIAGNOSTICS_WARN_OUTPUT_DIR + "fiche_mps_sans_liens.csv", formationsSansLiens)
        }
    }

    private fun getRemoteSheetUri(feuilleId: String) = "$remoteSheetsApiUri/$remoteSheetsSpreadSheetId/values/$feuilleId?key=$remoteSheetsApiKey"


    private fun exportRemoteSheets() {
        CsvTools.getWriter(DIAGNOSTICS_INFO_OUTPUT_DIR + "remote_sheet_fiches.csv").use { csv ->
            csv.appendHeaders(
                listOf(
                    REMOTE_SHEET_COLUMNS_MPS_ID,
                    REMOTE_SHEET_COLUMNS_LABEL,
                    REMOTE_SHEET_COLUMNS_GENERIC_ID,
                    REMOTE_SHEET_COLUMNS_PSUP_IDS,
                    REMOTE_SHEET_COLUMNS_DESCRIPTION,
                    REMOTE_SHEET_COLUMNS_LIENS,
                    REMOTE_SHEET_COLUMNS_EXTRA_IDEO_IDS,
                    REMOTE_SHEET_COLUMNS_EXTRA_METIERS_IDEO_IDS,
                    REMOTE_SHEET_COLUMNS_MOTS_CLES,
                    REMOTE_SHEET_COLUMNS_ATTENDUS,
                    REMOTE_SHEET_COLUMNS_CONSEILS,
                    REMOTE_SHEET_COLUMNS_ETUDES_COURTES,
                    REMOTE_SHEET_COLUMNS_ETUDES_LONGUES
                )
            )
            val mpsIds = getFormationsMpsIds()
            val labels = getLabels()
            val resumes = CsvTools.readCSV(
                dataSources.getSourceDataFilePath(RESUMES_MPS_PATH),
                ','
            )
            val liens = CsvTools.readCSV(
                dataSources.getSourceDataFilePath(LIENS_MPS_PATH),
                ','
            )
            val descriptifs = getDescriptifs()
            val mpsIdsToPsupIds = getMpsIdToPsupFlIds()
            val attendus = getAttendus()
            val conseils = getConseils()
            val courtes = getCompatEtudesCourtes()
            val longues = getCompatEtudesLongues()
            mpsIds.forEach { mpsId ->
                val resume = resumes.find { it[RESUMES_MPS_ID_HEADER] == mpsId }.orEmpty()
                val lien = liens.find { it[LIENS_MPS_PATH_HEADER_ID] == mpsId }.orEmpty()
                val estOkEtudesCourtes = courtes.contains(mpsId)
                val estOkEtudesLongues = longues.contains(mpsId)
                csv.append(
                    listOf(
                        mpsId,
                        labels[mpsId].orEmpty(),
                        resume[RESUMES_GENERIQUE_ID_HEADER].orEmpty(),
                        mpsIdsToPsupIds[mpsId]?.joinToString(" ; ") ?: "",
                        descriptifs.getDescriptifGeneralFront(mpsId),
                        lien[LIENS_MPS_PATH_HEADER_EXTRAS].orEmpty(),
                        "",//REMOTE_SHEET_COLUMNS_EXTRA_IDEO_IDS
                        "",//REMOTE_SHEET_COLUMNS_EXTRA_METIERS_IDEO_IDS
                        "",//REMOTE_SHEET_COLUMNS_MOTS_CLES
                        attendus[mpsId].orEmpty(),
                        conseils[mpsId].orEmpty(),
                        if (estOkEtudesCourtes) "X" else "",
                        if (estOkEtudesLongues) "X" else "",
                    )
                )

            }
        }

        val resumesGeneriques = HashMap<String, Pair<String,String>>()
        val resumes = CsvTools.readCSV(
            dataSources.getSourceDataFilePath(RESUMES_MPS_PATH),
            ','
        )
        for (line in resumes) {
            val frCod = line[RESUMES_GENERIQUE_ID_HEADER]
            val descFormation = line[RESUMES_RESUME_GENERIQUE_HEADER]
            if (frCod != null && descFormation != null && frCod.isNotBlank() && descFormation.isNotBlank()) {
                resumesGeneriques[frCod] = Pair.of(descFormation.trim(),line["intitule type formation"])
            }
        }
        CsvTools.getWriter(DIAGNOSTICS_OUTPUT_DIR + "remote_sheet_generiques.csv").use { csv ->
            csv.appendHeaders(
                listOf(
                    REMOTE_SHEET_COLUMNS_GENERIC_ID,
                    REMOTE_SHEET_COLUMNS_LABEL,
                    REMOTE_SHEET_COLUMNS_DESCRIPTION,
                )
            )
            resumesGeneriques.forEach { (id, description) ->
                csv.append(
                    listOf(
                        id, description.right, description.left
                    )
                )
            }
        }

    }

    override fun exportDiagnostics() {
        val logLiens = OnisepDataLoader.exportDiagnosticsLiens(getLabels())
        exportLiensFormationsMetiersDiagnostics(getLabels(), logLiens)
        exportFormationsSansVoeux()
        exportFormationsSansLiens()
        exportVoeuxSansFormation()
        exportFormationsMpsAvecDescriptifVide()
        exportRemoteSheets()
        exportLiens()
    }



}
