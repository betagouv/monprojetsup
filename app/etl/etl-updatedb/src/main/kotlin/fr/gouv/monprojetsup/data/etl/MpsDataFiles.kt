package fr.gouv.monprojetsup.data.etl

import fr.gouv.monprojetsup.data.domain.Constants
import fr.gouv.monprojetsup.data.domain.model.*
import fr.gouv.monprojetsup.data.domain.model.attendus.Attendus
import fr.gouv.monprojetsup.data.domain.model.attendus.GrilleAnalyse
import fr.gouv.monprojetsup.data.domain.model.bacs.Bac
import fr.gouv.monprojetsup.data.domain.model.cities.CitiesExternal
import fr.gouv.monprojetsup.data.domain.model.cities.Coords
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
import fr.gouv.monprojetsup.data.etl.labels.Labels
import fr.gouv.monprojetsup.data.etl.loaders.*
import fr.gouv.monprojetsup.data.formation.entity.MoyenneGeneraleAdmisId
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_FILIERES_GROUPES
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_FILIERES_THEMATIQUES
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_INTEREST_TO_INTEREST
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_INTERET_METIER
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_LAS_TO_GENERIC
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_LAS_TO_PASS
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_METIERS_ASSOCIES
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_SECTEURS_METIERS
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity.Companion.TYPE_EDGE_THEMATIQUES_METIERS
import fr.gouv.monprojetsup.data.tools.Serialisation
import jakarta.annotation.PostConstruct
import org.apache.commons.lang3.tuple.Pair
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
        statistiques = psupData.stats

        logger.info("Chargement des données Onisep")
        onisepData =
            OnisepDataLoader.fromFiles(dataSources)

        logger.info("Chargement des données ROME")
        romeData = RomeDataLoader.load(dataSources)
        logger.info("Insertion des données ROME dans les données Onisep")
        onisepData.insertRomeData(romeData.centresInterest) //before updateLabels

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
                        cities.add(
                            Ville(c.zip_code(), gpsCoords)
                        )
                        cities.add(
                            Ville(
                                "i" + c.insee_code(),
                                gpsCoords
                            )
                        )
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
            val resultInt = HashSet(psupData.filActives)//environ 750 (incluant apprentissage)
            resultInt.addAll(psupData.lasFlCodes)

            val result = HashSet(resultInt.stream()
                .map { cle -> Constants.gFlCodToFrontId(cle) }
                .toList()
            )

            //on supprime du résultat les formations regroupées et on ajoute les groupes
            val flGroups = psupData.psupKeyToMpsKey//environ 589 obtenus en groupant et en ajoutant les las
            result.removeAll(flGroups.keys)
            result.addAll(flGroups.values)

            //on veut au moins un voeu psup par formations indexées dans mps
            val groupesWithAtLeastOneFormation = psupData.formationToVoeux.keys
            result.retainAll(groupesWithAtLeastOneFormation)

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

            result[id] = StatsFormation(
                statistiques.getStatsMoyGenParBac(id),
                statistiques.getNbAdmisParBac(id),
                statistiques.getPctAdmisParBac(id) ?: mapOf(),
                statistiques.getNbAdmisParSpec(id) ?: mapOf(),
                statistiques.getPctAdmisParSpec(id) ?: mapOf(),
                psupData.getStatsFilSim(psupKeys)
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
        result.addAll(getEdges(onisepData.edgesInteretsMetiers, TYPE_EDGE_INTERET_METIER))
        result.addAll(getEdges(onisepData.edgesFilieresThematiques, TYPE_EDGE_FILIERES_THEMATIQUES))
        result.addAll(getEdges(onisepData.edgesThematiquesMetiers, TYPE_EDGE_THEMATIQUES_METIERS))
        result.addAll(getEdges(onisepData.edgesSecteursMetiers, TYPE_EDGE_SECTEURS_METIERS))
        result.addAll(getEdges(onisepData.edgesMetiersAssocies, TYPE_EDGE_METIERS_ASSOCIES))
        result.addAll(getEdges(psupKeyToMpsKey, TYPE_EDGE_FILIERES_GROUPES))
        result.addAll(getEdges(lasToGeneric, TYPE_EDGE_LAS_TO_GENERIC))
        result.addAll(getEdges(lasToPass, TYPE_EDGE_LAS_TO_PASS))
        result.addAll(getEdges(onisepData.edgesInteretsToInterets, TYPE_EDGE_INTEREST_TO_INTEREST))
        return result
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
            var duree = psupData.getDuree(id, mpsKeyToPsupKeys, lasKeys)
            if(duree == null && id.startsWith(Constants.FILIERE_PREFIX)) {
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
        val annee = statistiques.getAnnee()
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
        return psupData.bacs;
    }

    override fun getThematiques(): List<CategorieThematiques> {
        val groupes: MutableMap<String, CategorieThematiques> = HashMap()
        val categories: MutableList<CategorieThematiques> = ArrayList()

        var groupe = ""
        var emojig: String? = ""
        val listStringStringMap = ThematiquesLoader.loadThematiquesMps(dataSources)
        for (stringStringMap in listStringStringMap)
        {
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
        val groupes = InteretsLoader.loadInterets(dataSources)

        return Interets(interetsOnisep, groupes)

    }



}
