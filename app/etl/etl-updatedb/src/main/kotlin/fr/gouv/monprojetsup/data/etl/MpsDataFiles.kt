package fr.gouv.monprojetsup.data.etl

import com.opencsv.CSVWriterBuilder
import fr.gouv.monprojetsup.data.carte.algos.Filiere.LAS_CONSTANT
import fr.gouv.monprojetsup.data.domain.Constants
import fr.gouv.monprojetsup.data.domain.Constants.FORMATION_PSUP_EXCLUES
import fr.gouv.monprojetsup.data.domain.Constants.gFlCodToFrontId
import fr.gouv.monprojetsup.data.domain.Helpers.isFiliere
import fr.gouv.monprojetsup.data.domain.Helpers.isMetier
import fr.gouv.monprojetsup.data.domain.model.*
import fr.gouv.monprojetsup.data.domain.model.attendus.Attendus
import fr.gouv.monprojetsup.data.domain.model.attendus.GrilleAnalyse
import fr.gouv.monprojetsup.data.domain.model.bacs.Bac
import fr.gouv.monprojetsup.data.domain.model.cities.CitiesExternal
import fr.gouv.monprojetsup.data.domain.model.cities.Coords
import fr.gouv.monprojetsup.data.domain.model.descriptifs.DescriptifsFormationsMetiers
import fr.gouv.monprojetsup.data.domain.model.formations.Formation
import fr.gouv.monprojetsup.data.domain.model.interets.Interets
import fr.gouv.monprojetsup.data.domain.model.liens.UrlsUpdater
import fr.gouv.monprojetsup.data.domain.model.onisep.OnisepData
import fr.gouv.monprojetsup.data.domain.model.psup.PsupData
import fr.gouv.monprojetsup.data.domain.model.specialites.Specialites
import fr.gouv.monprojetsup.data.domain.model.stats.PsupStatistiques
import fr.gouv.monprojetsup.data.domain.model.stats.PsupStatistiques.*
import fr.gouv.monprojetsup.data.domain.model.thematiques.CategorieThematiques
import fr.gouv.monprojetsup.data.etl.labels.Labels
import fr.gouv.monprojetsup.data.etl.loaders.*
import fr.gouv.monprojetsup.data.formation.entity.MoyenneGeneraleAdmisId
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_DOMAINES_METIERS
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_FORMATIONS_DOMAINES
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_INTERET_GROUPE_INTERET
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_INTERET_METIER
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_LAS_TO_GENERIC
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_LAS_TO_PASS
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_METIERS_ASSOCIES
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_PSUP_KEY_TO_MPS_KEY
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_SECTEURS_METIERS
import fr.gouv.monprojetsup.data.tools.Serialisation
import jakarta.annotation.PostConstruct
import org.apache.commons.lang3.tuple.Pair
import org.springframework.stereotype.Component
import java.io.File
import java.util.*
import java.util.logging.Logger


@Component
class MpsDataFiles(
    private val dataSources: DataSources
) : MpsDataPort {

    private lateinit var psupData: PsupData
    private lateinit var  onisepData: OnisepData
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
        statistiques = psupData.stats

        logger.info("Chargement des données Onisep et Rome")
        onisepData = OnisepDataLoader.fromFiles(dataSources)

        statistiques.restrictToBacs(getBacs().map { it.key }.toSet())

    }

    override fun getLabels(): Map<String, String> {
        return Labels.getLabels(
            psupData,
            onisepData
        )
    }

    override fun getFormationsLabels(): Map<String, String> {
        return Labels.getFormationsLabels(
            psupData
        )
    }

    override fun getMetiersLabels(): Map<String, String> {
        return Labels.getMetiersLabels(
            onisepData
        )
    }

    override fun getMetiersAssociesLabels(): Map<String, List<String>> {
        return onisepData.getMetiersAssociesLabels()
    }

    override fun getMpsIdToIdeoIds(): Map<String, List<String>> {
        val psupToIdeo = onisepData.filieresToFormationsOnisep.associate { it.gFlCod to it.ideoFormationsIds }
        val las = getLasToGenericIdMapping()
        val result = HashMap<String, List<String>>()
        getFormationsMpsIds().forEach { mpsId ->
            result[mpsId] = psupData.mpsKeyToPsupKeys.getOrDefault(mpsId, listOf(mpsId)).flatMap { psupToIdeo[it].orEmpty() }.toList()
        }
        //ajout des las
        las.forEach { (las, generic) ->
            val genericIdeos = psupToIdeo[generic].orEmpty()
            result[las] = genericIdeos
        }
        return result
    }

    override fun exportDiagnostic() {
        val lines = CsvTools.readCSV(dataSources.getSourceDataFilePath(DataSources.RESUMES_MPS_PATH), ',')

        val outputFile = "resumesManquants.csv"
        val file = File(outputFile)
        //create a stream to write the diagnostic
        file.bufferedWriter().use { writer ->
            val csvWriter = CSVWriterBuilder(writer).build() // will produce a CSVWriter
            val headers = listOf(
                "code filiere",
                "intitulé web",
                "code type formation",
                "intitule type formation,",
                "url onisep",
                "URL corrections",
                "url psup",
                "resume type formation",
                "resume filiere",
                "Liens supplémentaires",
                "Retours à Onisep"
            )
            csvWriter.writeNext(headers.toTypedArray())
            val codesFilieres = mutableSetOf<String>()
            for (line in lines) {
                val nextLine = mutableListOf<String>()
                codesFilieres.add(line["code filiere"].orEmpty())
                for (header in headers) {
                    nextLine.add(line[header].orEmpty())
                }
                csvWriter.writeNext(nextLine.toTypedArray())
            }
            val las = getLasToGenericIdMapping().keys
            val missingCodesExceptLas = getFormationsMpsIds().filter { it !in codesFilieres && it !in las }

            val labels = getLabels()
            val liens = getLiens()
            for (code in missingCodesExceptLas) {
                val liensOnisep = liens[code].orEmpty().filter { it.uri.contains("avenirs") }.map { it.uri }.distinct().joinToString("\n")
                val liensPsup = liens[code].orEmpty().filter { it.uri.contains("parcoursup") }.map { it.uri }.distinct().joinToString("\n")
                val autresLiens = liens[code].orEmpty().filter { !it.uri.contains("avenirs") && !it.uri.contains("parcoursup") }.distinct().joinToString("\n")
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
                    "")
                csvWriter.writeNext(nextLine.toTypedArray())
            }
            val nextLineLas = listOf(
                gFlCodToFrontId(LAS_CONSTANT),
                Constants.LABEL_ARTICLE_PAS_LAS,
                "",
                "",
                Constants.URL_ARTICLE_PAS_LAS,
                "",//url corrections
                Constants.CARTE_PARCOURSUP_PREFIX_URI + listOf("las","accès","santé").joinToString("%20" ),
                "",//resume type formation
                "",//resume filiere
                "",
                "")
            csvWriter.writeNext(nextLineLas.toTypedArray())
        }
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
            val attendusId = psupKeys.asSequence().map { attendusPsup[it] }.filterNotNull().map { att -> att.conseilsFront }.distinct().joinToString("\n\n")
            if(attendusId.isNotBlank() && !attendusId.contains("null")) {
                result[id] = attendusId
            }
        }
        return result
    }

    override fun getCities(): List<Ville> {
        logger.info("Double indexation des villes")
        val citiesOld = Serialisation.fromJsonFile(
            dataSources.getSourceDataFilePath(DataSources.CITIES_FILE_PATH),
            CitiesExternal::class.java
        )

        val mByDpt: MutableMap<String, Pair<String, MutableList<Coords>>> = HashMap()
        citiesOld.cities()
            .filter { c -> c.zip_code != null }
            .forEach { c ->
                var key = c.name()
                key += c.zip_code().toInt() / 1000
                val toto = mByDpt.computeIfAbsent(key) { _ ->
                    Pair.of(
                        c.name(),
                        ArrayList()
                    )
                }
                toto.right.add(
                    Coords(
                        c.zip_code(),
                        c.insee_code(),
                        c.gps_lat(),
                        c.gps_lng()
                    )
                )
            }
        val cities: MutableList<Ville> = ArrayList()
        mByDpt.values.forEach { value: Pair<String, MutableList<Coords>> ->
            val name = value.left
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
                    cities.add(Ville(name, gpsCoords))
                    coords.forEach { c: Coords ->
                        if(c.insee_code != null) {
                            cities.add(
                                Ville(
                                    c.insee_code(),
                                    gpsCoords
                                )
                            )
                        }
                    }
                }
            }
        }
        return cities
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

    override fun getMotsClesFormations(): Map<String, List<String>> {

        //log.info("Chargement des sources des mots-clés, et extension via la correspondance");
        val motsCles = psupData.motsCles

        motsCles.sources.computeIfAbsent(
            Constants.PASS_MOT_CLE
        ) { HashSet() }.add(gFlCodToFrontId(Constants.PASS_FL_COD))

        motsCles.extendToGroups(psupData.psupKeyToMpsKey)

        val labels = getLabels()

        val formationsVersMetiers = getFormationsVersMetiersEtMetiersAssocies()

        val mpsToIdeo = getMpsIdToIdeoIds()

        val formationsIdeo = onisepData.formationsIdeo.associateBy { it.ideo }

        val mpsIds = getFormationsMpsIds()

        //le référentiel des formations front
        mpsIds.forEach { formation ->
            val label = labels.getOrDefault(formation, formation)
            motsCles.add(label, formation)
            //recherche par clé
            motsCles.add(formation + "x", formation)
            if (label.contains("L1")) {
                motsCles.add("licence", formation)
            }
            if (label.lowercase(Locale.getDefault()).contains("infirmier")) {
                motsCles.add("IFSI", formation)
            }
            if(mpsToIdeo.containsKey(formation)) {
                val ideoKeys = mpsToIdeo[formation].orEmpty()
                ideoKeys.forEach { ideoKey ->
                    val formationIdeo = formationsIdeo[ideoKey]
                    if (formationIdeo != null) {
                        motsCles.add(formationIdeo.motsCles, formation)
                    }
                }
            }
            formationsVersMetiers[formation]?.forEach { idMetierOuMetierAssocie ->
                val labelMetier = labels[idMetierOuMetierAssocie]
                if(labelMetier != null) {
                    motsCles.add(labelMetier, formation)
                }
            }
        }
        motsCles.extendToGroups(psupData.psupKeyToMpsKey)
        motsCles.normalize()

        return motsCles.getKeyToTags()
    }

    override fun getMetiersMpsIds(): List<String> {
        return onisepData.metiersIdeo.asSequence().map { it.idMps() }.toList().sorted()
            .toList()
    }

    override fun getFormationsMpsIds(): List<String> {
        if(formationsMpsIds == null) {
            val resultInt = HashSet(psupData.filActives)//environ 750 (incluant apprentissage)
            resultInt.addAll(psupData.lasFlCodes)

            val result = HashSet(resultInt.stream()
                .map { cle -> gFlCodToFrontId(cle) }
                .toList()
            )

            //on supprime du résultat les formations regroupées et on ajoute les groupes
            val flGroups = psupData.psupKeyToMpsKey//environ 589 obtenus en groupant et en ajoutant les las
            result.removeAll(flGroups.keys)
            result.addAll(flGroups.values)

            //on veut au moins un voeu psup par formations indexées dans mps
            val groupesWithAtLeastOneFormation = psupData.formationToVoeux.keys
            result.retainAll(groupesWithAtLeastOneFormation)

            result.removeAll(FORMATION_PSUP_EXCLUES);

            val sorted = result.toList().sorted()
            formationsMpsIds =  sorted
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

    override fun getFormationsVersMetiersEtMetiersAssocies(): Map<String, Set<String>> {
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

        val passKey = gFlCodToFrontId(Constants.PASS_FL_COD)
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
            val stat = StatsFormation(
                replaceKey(statistiques.getStatsMoyGenParBac(id), TOUS_BACS_CODE_LEGACY,TOUS_BACS_CODE_MPS),
                replaceKey(statistiques.getNbAdmisParBac(id), TOUS_BACS_CODE_LEGACY,TOUS_BACS_CODE_MPS),
                replaceKey(statistiques.getPctAdmisParBac(id), TOUS_BACS_CODE_LEGACY,TOUS_BACS_CODE_MPS),
                statistiques.getNbAdmisParSpec(id) ?: mapOf(),
                statistiques.getPctAdmisParSpec(id) ?: mapOf(),
                psupData.getStatsFilSim(psupKeys)
            )
            result[id] = stat
        }
        return result
    }

    @Suppress("SameParameterValue")
    private fun <T> replaceKey(m: Map<String,T>?, oldKey: String, newKey: String):  Map<String, T> {
        if(m == null) return mapOf()
        val result = HashMap<String, T>()
        result.putAll(m)
        val value = result[oldKey]
        if (value != null) {
            result[newKey] = value
            result.remove(oldKey)
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
        val psupKeyToMpsKey = getPsupIdToMpsId()
        val lasToGeneric = getLasToGenericIdMapping()
        val lasToPass = getLasToPasIdMapping()

        result.addAll(getEdges(onisepData.edgesInteretsToInterets, TYPE_EDGE_INTERET_GROUPE_INTERET))
        result.addAll(getEdges(onisepData.edgesInteretsMetiers, TYPE_EDGE_INTERET_METIER))
        result.addAll(getEdges(onisepData.edgesFormationsDomaines, TYPE_EDGE_FORMATIONS_DOMAINES))
        result.addAll(getEdges(onisepData.edgesDomainesMetiers, TYPE_EDGE_DOMAINES_METIERS))
        result.addAll(getEdges(onisepData.edgesSecteursMetiers, TYPE_EDGE_SECTEURS_METIERS))
        result.addAll(getEdges(onisepData.edgesMetiersAssocies, TYPE_EDGE_METIERS_ASSOCIES))
        result.addAll(getEdges(psupKeyToMpsKey, TYPE_EDGE_PSUP_KEY_TO_MPS_KEY))
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

    override fun getMatieres(): List<Matiere> {
        val specs = getSpecialites()
        val result = ArrayList(
            statistiques.matieres.entries
                .filter { !specs.isSpecialite(it.key) }
                .map {
            Matiere(
                Matiere.idPsupToIdMps(it.key),
                it.key,
                it.value,
                false,
                listOf()
            )}
        )
        result.addAll(specs.toMatieres())
        return result
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
            if(duree == null && isFiliere(id)) {
                try {
                    val codeFilierePsup = Integer.parseInt(id.substring(Constants.FILIERE_PREFIX.length))
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
        val annee = statistiques.annee
        val stats = getStatsFormation()
        val result = HashMap<MoyenneGeneraleAdmisId, List<Int>>()
        stats.values.forEach{ stat -> stat.restrictToBacs(getBacs().map { it.key }) }
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
        val result = ArrayList(psupData.bacs)
        result.removeIf { it.key == TOUS_BACS_CODE_LEGACY }
        result.add(Bac(TOUS_BACS_CODE_MPS, TOUS_BACS_CODE_FRONT_LIBELLE))
        return result
    }

    override fun getDomaines(): List<CategorieThematiques> {
        return DomainesMpsLoader.loadDomainesMps(dataSources)
    }


    override fun getInterets() : Interets {
        return onisepData.interets

    }



}
