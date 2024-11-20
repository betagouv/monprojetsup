package fr.gouv.monprojetsup.data.etl

import fr.gouv.monprojetsup.data.Constants
import fr.gouv.monprojetsup.data.Constants.CARTE_PARCOURSUP_PREFIX_URI
import fr.gouv.monprojetsup.data.Constants.DIAGNOSTICS_OUTPUT_DIR
import fr.gouv.monprojetsup.data.Constants.EXPLORER_AVENIRS_URL
import fr.gouv.monprojetsup.data.Constants.LABEL_ARTICLE_PAS_LAS
import fr.gouv.monprojetsup.data.Constants.LAS_CONSTANT
import fr.gouv.monprojetsup.data.Constants.ONISEP_URL1
import fr.gouv.monprojetsup.data.Constants.ONISEP_URL2
import fr.gouv.monprojetsup.data.Constants.PASS_FL_COD
import fr.gouv.monprojetsup.data.Constants.URL_ARTICLE_PAS_LAS
import fr.gouv.monprojetsup.data.Constants.gFlCodToMpsId
import fr.gouv.monprojetsup.data.Constants.gFrCodToMpsId
import fr.gouv.monprojetsup.data.Constants.isFiliere
import fr.gouv.monprojetsup.data.Constants.isMetier
import fr.gouv.monprojetsup.data.Constants.mpsIdToGFlCod
import fr.gouv.monprojetsup.data.etl.labels.Labels
import fr.gouv.monprojetsup.data.etl.loaders.CsvTools
import fr.gouv.monprojetsup.data.etl.loaders.DataSources
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.LIENS_MPS_PATH_HEADER_EXTRAS
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.LIENS_MPS_PATH_HEADER_ID
import fr.gouv.monprojetsup.data.etl.loaders.DataSources.LIENS_MPS_PATH_HEADER_IGNORER
import fr.gouv.monprojetsup.data.etl.loaders.DescriptifsLoader
import fr.gouv.monprojetsup.data.etl.loaders.OnisepDataLoader
import fr.gouv.monprojetsup.data.etl.loaders.OnisepDataLoader.loadLiensFormationsMpsDomainesMps
import fr.gouv.monprojetsup.data.etl.loaders.SpecialitesLoader
import fr.gouv.monprojetsup.data.formation.entity.MoyenneGeneraleAdmisId
import fr.gouv.monprojetsup.data.model.Candidat
import fr.gouv.monprojetsup.data.model.LatLng
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
import fr.gouv.monprojetsup.data.model.liens.UrlsUpdater.IDEO_HOTLINE
import fr.gouv.monprojetsup.data.model.onisep.OnisepData
import fr.gouv.monprojetsup.data.model.psup.AdmissionStats
import fr.gouv.monprojetsup.data.model.psup.PsupData
import fr.gouv.monprojetsup.data.model.specialites.Specialites
import fr.gouv.monprojetsup.data.model.tags.TagsFormations
import fr.gouv.monprojetsup.data.model.taxonomie.Taxonomie
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_ATOME_ELEMENT
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_DOMAINES_METIERS
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_FORMATIONS_PSUP_DOMAINES
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_FORMATION_PSUP_TO_FORMATION_MPS
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_INTERET_METIER
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_LAS_TO_GENERIC
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_LAS_TO_PASS
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_METIERS_ASSOCIES
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_METIERS_FORMATIONS_PSUP
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_SECTEURS_METIERS
import fr.gouv.monprojetsup.data.tools.CsvTools.getWriter
import fr.gouv.monprojetsup.data.tools.CsvTools.readCSV
import fr.gouv.monprojetsup.data.tools.Serialisation
import jakarta.annotation.PostConstruct
import org.apache.commons.lang3.tuple.Pair
import org.springframework.stereotype.Component
import java.util.*
import java.util.logging.Logger


@Component
class MpsDataFromFiles(
    private val dataSources: DataSources
) : MpsDataPort {

    private lateinit var psupData: PsupData
    private lateinit var  onisepData: OnisepData
    private lateinit var  statistiques: AdmissionStats

    private var descriptifs : DescriptifsFormationsMetiers? = null
    private var specialites : Specialites? = null
    private var formationsMpsIds : List<String>? = null

    private val logger: Logger = Logger.getLogger(MpsDataFromFiles::class.java.simpleName)
    @PostConstruct
    private fun load() {
        logger.info("Chargement de " + dataSources.getSourceDataFilePath(
            DataSources.BACK_PSUP_DATA_FILENAME))
        psupData = Serialisation.fromZippedJson(
            dataSources.getSourceDataFilePath(DataSources.BACK_PSUP_DATA_FILENAME),
            PsupData::class.java
        )
        psupData.initDurees()
        statistiques = psupData.buildStats()

        logger.info("Chargement des données Onisep et Rome")
        onisepData = OnisepDataLoader.fromFiles(dataSources)

        val logLiens = OnisepDataLoader.exportDiagnosticsLiens(getLabels())
        exportLiensFormationsMetiersDiagnostics(getLabels(), logLiens)
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


    override fun getLabels(): Map<String, String> {
        return Labels.getLabels(
            psupData,
            onisepData
        )
    }

    private fun getLabelsOriginauxPsup(): MutableMap<String, String> {
        return Labels.getLabelsOriginauxPsup(
            psupData
        )
    }


    override fun getDebugLabels(): Map<String, String> {
        return Labels.getDebugLabels(
            psupData,
            onisepData
        )
    }


    override fun getFormationsLabels(): Map<String, String> {
        return Labels.getFormationsLabels(
            psupData,
            false
        )
    }

    override fun getMetiersLabels(): Map<String, String> {
        return onisepData.getMetiersLabels(
            false
        )
    }

    override fun getMetiersAssociesLabels(): Map<String, List<String>> {
        return onisepData.getMetiersAssociesLabels()
    }

    override fun getMpsIdToIdeoIds(): Map<String, List<String>> {

        val psupToIdeo = onisepData.filieresToFormationsOnisep
            .associate { Pair(gFlCodToMpsId(it.gFlCod)!!, it.ideoFormationsIds!!) }

        val mpsKeyToPsupKeys = psupData.mpsKeyToPsupKeys
        val las = getLasToGenericIdMapping()
        val result = HashMap<String, List<String>>()
        getFormationsMpsIds().forEach { mpsId ->
            result[mpsId] =
                mpsKeyToPsupKeys.getOrDefault(mpsId, listOf(mpsId)).
                flatMap { psupToIdeo[it].orEmpty() }.toList()
        }
        //ajout des las
        las.forEach { (las, generic) ->
            val genericIdeos = psupToIdeo[generic].orEmpty()
            result[las] = genericIdeos
        }
        return result
    }

    override fun exportDiagnostics() {
        exportResumesManquants()
        exportLiens()
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
        getLasToGenericIdMapping().forEach { (las, generic) ->
            result[las] = result.getOrDefault(generic, generic)
        }
        return result
    }

    private fun exportLiens() {
        val ignorer = getLiensMpsIgnorer()
        val extras = getLiensMpsExtras()
        val labels = getLabels()

        CsvTools.getWriter(DIAGNOSTICS_OUTPUT_DIR + "liens.csv").use { csv ->
            val headers = listOf(
                LIENS_MPS_PATH_HEADER_ID,
                "label",
                "en ligne labels",
                "en ligne urls",
                LIENS_MPS_PATH_HEADER_EXTRAS,
                "onisep data labels",
                "onisep data urls",
                LIENS_MPS_PATH_HEADER_IGNORER,
                "analyse"
            )
            csv.appendHeaders(headers)
            val liens = getLiens()
            for(id in getFormationsMpsIds().sortedBy { labels.getOrDefault(it,it) }) {
                val nextLine = listOf(
                    id,
                    labels[id].orEmpty(),
                    liens[id].orEmpty().filter { !it.source.contains(CARTE_PSUP) }.joinToString("\n") { it.label},
                    liens[id].orEmpty().filter { !it.source.contains(CARTE_PSUP) }.joinToString("\n") { it.uri },
                    extras[id].orEmpty().joinToString("\n"),
                    liens[id].orEmpty().filter { l -> l.source.contains(IDEO_HOTLINE) }.joinToString("\n") { it.label},
                    liens[id].orEmpty().filter { l -> l.source.contains(IDEO_HOTLINE) }.joinToString("\n") { it.uri },
                    ignorer[id].orEmpty().joinToString("\n"),
                    ""
                )
                csv.append(nextLine)
            }
        }
    }

    private fun exportResumesManquants() {
        val lines = CsvTools.readCSV(dataSources.getSourceDataFilePath(DataSources.RESUMES_MPS_PATH), ',')

        CsvTools.getWriter(DIAGNOSTICS_OUTPUT_DIR + "resumes.csv").use { csv ->
            val headers = listOf(
                "code filiere",
                "intitulé web",
                "code type formation",
                "intitule type formation,",
                "url onisep",
                "url psup",
                "resume type formation",
                "resume filiere",
                "Retours à Onisep"
            )
            csv.appendHeaders(headers)
            val codesFilieres = mutableSetOf<String>()
            for (line in lines) {
                val nextLine = mutableListOf<String>()
                codesFilieres.add(line["code filiere"].orEmpty())
                for (header in headers) {
                    nextLine.add(line[header].orEmpty())
                }
                csv.append(nextLine)
            }
            val las = getLasToGenericIdMapping().keys
            val missingCodesExceptLas = getFormationsMpsIds().filter { it !in codesFilieres && it !in las }

            val labels = getLabels()
            val liens = getLiens()
            for (code in missingCodesExceptLas) {
                val liensOnisep =
                    liens[code].orEmpty().filter { it.uri.contains("avenirs") }.map { it.uri }.distinct()
                        .joinToString("\n")
                val liensPsup =
                    liens[code].orEmpty().filter { it.uri.contains("parcoursup") }.map { it.uri }.distinct()
                        .joinToString("\n")
                val autresLiens =
                    liens[code].orEmpty().filter { !it.uri.contains("avenirs") && !it.uri.contains("parcoursup") }
                        .distinct().joinToString("\n")
                val nextLine = listOf(
                    code,
                    labels[code].orEmpty(),
                    "",
                    "",
                    liensOnisep,
                    "",//url corrections
                    autresLiens,
                    liensPsup,//url psup
                    "",//resume type formation
                    "",//resume filiere
                    ""
                )
                csv.append(nextLine)
            }
            val nextLineLas = listOf(
                gFlCodToMpsId(LAS_CONSTANT),
                LABEL_ARTICLE_PAS_LAS,
                "",
                "",
                URL_ARTICLE_PAS_LAS,
                "",//url corrections
                CARTE_PARCOURSUP_PREFIX_URI + listOf("las", "accès", "santé").joinToString("%20"),
                "",//resume type formation
                "",//resume filiere
                "",
                ""
            )
            csv.append(nextLineLas)
        }

        CsvTools.getWriter(DIAGNOSTICS_OUTPUT_DIR + "resumesManquants.csv").use { csv ->
            val headers = listOf("code filiere")
            csv.appendHeaders(headers)
            val codesFilieres = mutableSetOf<String>()
            codesFilieres.addAll(lines.map { it["code filiere"].orEmpty() })
            val las = getLasToGenericIdMapping().keys
            val missingCodesExceptLas = getFormationsMpsIds().filter { it !in codesFilieres && it !in las }

            val labels = getLabels()
            val liens = getLiens()
            for (code in missingCodesExceptLas) {
                val liensOnisep =
                    liens[code].orEmpty().filter { it.uri.contains("avenirs") }.map { it.uri }.distinct()
                        .joinToString("\n")
                val liensPsup =
                    liens[code].orEmpty().filter { it.uri.contains("parcoursup") }.map { it.uri }.distinct()
                        .joinToString("\n")
                val autresLiens =
                    liens[code].orEmpty().filter { !it.uri.contains("avenirs") && !it.uri.contains("parcoursup") }
                        .distinct().joinToString("\n")
                val nextLine = listOf(
                    code,
                    labels[code].orEmpty(),
                    "",
                    "",
                    liensOnisep,
                    "",//url corrections
                    liensPsup,//url psup
                    "",//resume type formation
                    "",//resume filiere
                    autresLiens,
                    ""
                )
                csv.append(nextLine)
            }
        }
    }

    override fun getDescriptifs(): DescriptifsFormationsMetiers {
        if(descriptifs == null) {
            descriptifs = DescriptifsLoader.loadDescriptifs(
                onisepData,
                psupData.lasToGeneric,
                dataSources
            )
        }
        return descriptifs!!
    }

    override fun getSpecialites(): Specialites {
        if(specialites == null) {
            specialites = SpecialitesLoader.load(
                dataSources,
                psupData.getSpesBacs()
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
        val labels = getLabels()
        val allPsupKeys = mpsIds.flatMap { mpsKeyToPsupKeys.getOrDefault(it, setOf(it))}
        val allTexts = allPsupKeys.associateWith { psupKey -> attendusPsup[psupKey]?.attendusFront.orEmpty() }
        return mergeAttendusOrConseils(allTexts, mpsIds, mpsKeyToPsupKeys, labels)
    }

    override fun getConseils(): Map<String, String> {
        val attendusPsup =  Attendus.getAttendusSimplifies(
            psupData
        )
        val mpsIds = getFormationsMpsIds()
        val mpsKeyToPsupKeys = psupData.mpsKeyToPsupKeys
        val labels = getLabels()
        val allPsupKeys = mpsIds.flatMap { mpsKeyToPsupKeys.getOrDefault(it, setOf(it))}
        val allTexts = allPsupKeys.associateWith { psupKey -> attendusPsup[psupKey]?.conseilsFront.orEmpty() }
        return mergeAttendusOrConseils(allTexts, mpsIds, mpsKeyToPsupKeys, labels)
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
            dataSources.getSourceDataFilePath(DataSources.CITIES_FILE_PATH),
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
                        if(c.insee_code != null) {
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
        val urls = UrlsUpdater.updateUrls(
            onisepData.metiersIdeo,
            psupData.liensOnisep,
            psupData.lasToGeneric,
            psupData.psupKeyToMpsKey,
            getFormationsMpsIds(),
            getLabels(),
            getLabelsOriginauxPsup(),
            getLiensMpsIgnorer(),
            getLiensMpsExtras(),
        )
        return urls
    }


    private fun getLiensMpsIgnorer(): Map<String, Collection<String>> {
        val lines = CsvTools.readCSV(
            dataSources.getSourceDataFilePath(DataSources.LIENS_MPS_PATH),
            ','
        )
        val result = HashMap<String, Collection<String>>()
        //key	label	onisep	corrections	extras
        for (line in lines) {
            val key = line[LIENS_MPS_PATH_HEADER_ID] ?: throw java.lang.RuntimeException("Empty $LIENS_MPS_PATH_HEADER_ID in $line")
            val urls = line[LIENS_MPS_PATH_HEADER_IGNORER] ?: throw java.lang.RuntimeException("Empty $LIENS_MPS_PATH_HEADER_IGNORER in $line")
            val urlList = urls.split("\n")
            result[key] = urlList
        }
        return result
    }
    private fun getLiensMpsExtras(): Map<String, Collection<String>> {
        val lines = CsvTools.readCSV(
            dataSources.getSourceDataFilePath(DataSources.LIENS_MPS_PATH),
            ','
        )
        val result = HashMap<String, Collection<String>>()
        for (line in lines) {
            val key = line[LIENS_MPS_PATH_HEADER_ID] ?: throw java.lang.RuntimeException("Empty $LIENS_MPS_PATH_HEADER_ID in $line")
            val urls = line[LIENS_MPS_PATH_HEADER_EXTRAS] ?: throw java.lang.RuntimeException("Empty $LIENS_MPS_PATH_HEADER_EXTRAS in $line")
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

        val motsCleMps = Serialisation.fromJsonFile(dataSources.getSourceDataFilePath(DataSources.MOTS_CLES_MPS_PATH), TagsFormations::class.java)
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
        mpsIds.forEach { formation ->
            val label = labels.getOrDefault(formation, formation)
            motsClesPsup.add(label, formation)
            //recherche par clé
            motsClesPsup.add(formation + "x", formation)
            if (label.contains("L1")) {
                motsClesPsup.add("licence", formation)
            }
            if (label.lowercase().contains("infirmier")) {
                motsClesPsup.add("IFSI", formation)
            }
            if(mpsToIdeo.containsKey(formation)) {
                val ideoKeys = mpsToIdeo[formation].orEmpty()
                ideoKeys.forEach { ideoKey ->
                    val formationIdeo = formationsIdeo[ideoKey]
                    if (formationIdeo != null) {
                        motsClesPsup.add(formationIdeo.motsCles, formation)
                    }
                }
            }
            formationsVersMetiers[formation]?.forEach { idMetierOuMetierAssocie ->
                val labelMetier = labels[idMetierOuMetierAssocie]
                if(labelMetier != null) {
                    motsClesPsup.add(labelMetier, formation)
                }
            }
        }
        motsClesPsup.extendToGroups(psupData.psupKeyToMpsKey)
        motsClesPsup.normalize()

        return motsClesPsup.getKeyToTags()
    }

    override fun getMetiersMpsIds(): List<String> {
        return onisepData.metiersIdeo.asSequence().map { it.ideo() }.toList().sorted()
            .toList()
    }

    override fun getFormationsMpsIds(): List<String> {
        if(formationsMpsIds == null) {
            val result = HashSet(psupData.formationsMpsIds)
            val toRemove = readCSV(dataSources.getSourceDataFilePath(DataSources.MPS_FORMATIONS_EXCLUES_PATH),',')
                .filter { it.isNotEmpty() }
                .map { it[DataSources.MPS_FORMATIONS_EXCLUES_HEADER].toString() }
                .toSet()
            result.removeAll(toRemove)
            formationsMpsIds = ArrayList(result)
        }
        return formationsMpsIds!!
    }

    override fun getApprentissage() : Collection<String> {
        return psupData.getApprentissage().entries.filter { it.value > 0 }.map { it.key }
    }

    override fun getApprentissagePct() : Map<String,Int> {
        return psupData.getApprentissage()
    }

    override fun getLasToGenericIdMapping() : Map<String,String> {
        return psupData.lasToGeneric
    }

    override fun getVoeux(): Map<String, Collection<Voeu>> {
        val formationsMps = getFormationsMpsIds()
        return psupData.getVoeuxGroupedByFormation(formationsMps)
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

    override fun getFormationsVersMetiersEtMetiersAssocies(): Map<String, Set<String>> {
        val metiersVersFormations = getMetiersVersFormationsExtendedWithGroupsAndLAS(
            onisepData.edgesMetiersFormations,
            psupData.psupKeyToMpsKey,
            psupData.genericToLas
        )

        val psupKeyToMpsKey = psupData.psupKeyToMpsKey

        val passKey = gFlCodToMpsId(PASS_FL_COD)
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

    /**
     * metiers vers filieres
     * @return a map metiers -> filieres
     */
    private fun getMetiersVersFormationsExtendedWithGroupsAndLAS(
        edgesMetiersFormations: List<Pair<String, String>>,
        psupKeyToMpsKey: Map<String?, String>,
        genericToLas: Map<String?, String>
    ): Map<String, Set<String>> {
        val metiersVersFormations: MutableMap<String, MutableSet<String>> = HashMap()

        edgesMetiersFormations.forEach { p ->
            metiersVersFormations.computeIfAbsent(p.left) { HashSet() }.add(p.right)
        }

        metiersVersFormations.keys.removeIf { k -> !isMetier(k) }

        metiersVersFormations.values.forEach { strings ->
            strings.removeIf { s -> !isFiliere(s) }
        }


        /* ajouts des las aux metiers PASS.
        * Remarque: c'est refait côté suggestions.... */
        val passKey = gFlCodToMpsId(PASS_FL_COD)
        val metiersPass = metiersVersFormations.entries
                .filter { e ->  e.value.contains(passKey) }
                .map { z -> z.key }
                .toSet()
        metiersPass.forEach { m ->
            metiersVersFormations.computeIfAbsent(m) { HashSet() }.addAll(genericToLas.values)
        }
        metiersVersFormations.entries.forEach { e ->
            val mpsFormationsKeysBase = HashSet(e.value)
            val mpsFormationsKeys = HashSet(mpsFormationsKeysBase)
            mpsFormationsKeysBase.forEach { mpsKey ->
                /* ajouts des groupes génériques aux metiers des formations correspondantes */
                mpsFormationsKeys.add(psupKeyToMpsKey.getOrDefault(mpsKey, mpsKey))
                /* ajouts des las aux metiers des génériques correspondants */
                if (genericToLas.containsKey(mpsKey)) {
                    mpsFormationsKeys.add(genericToLas[mpsKey])
                }
            }
            e.setValue(mpsFormationsKeys)
        }
        return metiersVersFormations
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
        val ids = getFormationsMpsIds()
        val mpsKeyToPsupKeys = psupData.mpsKeyToPsupKeys
        return ids.associateWith { mpsKeyToPsupKeys.getOrDefault(it, setOf(it)) }
    }

    override fun getPsupIdToMpsId(): Map<String, String> {
        return psupData.psupKeyToMpsKey
    }

    override fun getVoeuxParCandidat(): List<Candidat> {
        return psupData.voeuxParCandidat
    }

    override fun getLasToPasIdMapping(): Map<String, String> {
        return psupData.lasToPass
    }

    override fun getEdges(): List<Triple<String, String, Int>> {
        val result = ArrayList<Triple<String, String, Int>>()

        val psupToMps = HashMap(getPsupIdToMpsId())
        psupToMps.values.retainAll(getFormationsMpsIds().toSet())

        val lasToGeneric = getLasToGenericIdMapping()
        val lasToPass = getLasToPasIdMapping()

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
        result.addAll(getEdges(lasToGeneric, TYPE_EDGE_LAS_TO_GENERIC))
        result.addAll(getEdges(lasToPass, TYPE_EDGE_LAS_TO_PASS))

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

    private fun getEdges(edges: Map<String, String>, type: Int): List<Triple<String, String, Int>> {
        return edges.entries.map { (src, dst) -> Triple(src, dst, type) }
    }


    override fun getDurees(): Map<String, Int?> {
        val ids = getFormationsMpsIds()
        val mpsKeyToPsupKeys = psupData.mpsKeyToPsupKeys
        val lasKeys = psupData.lasToGeneric.keys
        val result = HashMap<String,Int?>()
        ids.forEach { id ->
            var duree = psupData.getDuree(id, mpsKeyToPsupKeys, lasKeys)
            if(duree == null && Constants.isPsupFiliere(id)) {
                try {
                    val codeFilierePsup = mpsIdToGFlCod(id)
                    val filiere = psupData.filieres()[codeFilierePsup]
                    if(filiere != null) {
                        duree = psupData.getDuree(filiere)
                    }
                } catch (e: NumberFormatException) {
                    //ignore
                }
            }
            result[id] = duree
        }
        return result
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
        return psupData.bacs
    }

    override fun getDomaines(): Taxonomie {
        return onisepData.domaines
    }


    override fun getInterets() : Taxonomie {
        return onisepData.interets

    }



}
