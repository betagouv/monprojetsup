package fr.gouv.monprojetsup.data.etl.suggestions

import fr.gouv.monprojetsup.data.app.infrastructure.*
import fr.gouv.monprojetsup.data.etl.formation.UpdateFormationDbs
import fr.gouv.monprojetsup.suggestions.domain.Constants
import fr.gouv.monprojetsup.suggestions.domain.labels.Labels
import fr.gouv.monprojetsup.suggestions.domain.model.*
import fr.gouv.monprojetsup.suggestions.domain.port.*
import fr.gouv.monprojetsup.suggestions.infrastructure.DataSources
import fr.gouv.monprojetsup.suggestions.infrastructure.model.cities.CitiesExternal
import fr.gouv.monprojetsup.suggestions.infrastructure.model.cities.Coords
import fr.gouv.monprojetsup.suggestions.infrastructure.model.descriptifs.DescriptifsLoader
import fr.gouv.monprojetsup.suggestions.infrastructure.model.formations.Formation
import fr.gouv.monprojetsup.suggestions.infrastructure.model.specialites.SpecialitesLoader
import fr.gouv.monprojetsup.suggestions.infrastructure.model.stats.PsupStatistiques
import fr.gouv.monprojetsup.suggestions.infrastructure.model.tags.TagsSources
import fr.gouv.monprojetsup.suggestions.infrastructure.onisep.OnisepData
import fr.gouv.monprojetsup.suggestions.infrastructure.psup.PsupData
import fr.gouv.monprojetsup.suggestions.infrastructure.rome.RomeData
import fr.gouv.monprojetsup.suggestions.tools.Serialisation
import org.apache.commons.lang3.tuple.Pair
import java.io.IOException
import java.util.*
import java.util.logging.Logger

class UpdateSuggestionsDbs(
    private val dataSources: DataSources,
    private val candidatsPort: CandidatsPort,
    private val voeuxPort: VoeuxPort,
    private val villesPort: VillesPort,
    private val matieresPort: MatieresPort,
    private val labelsPort: LabelsPort,
    private val formationsPort: FormationsPort,
    private val edgesPort: EdgesPort,
    private val updateFormationDbs: UpdateFormationDbs,
) {

    private val logger: Logger = Logger.getLogger(UpdateSuggestionsDbs::class.java.simpleName)

    internal fun updateSuggestionDbs() {


        logger.info("Chargement des données A) bd parcoursup")
        val psupData: PsupData = Serialisation.fromZippedJson(
            dataSources.getSourceDataFilePath(DataSources.BACK_PSUP_DATA_FILENAME),
            PsupData::class.java
        )
        psupData.cleanup()

        /*
        logger.info("Chargement des données B) carte parcoursup")
        val carte: JsonCarte = Serialisation.fromJsonFile(
            dataSources.getSourceDataFilePath(CARTE_JSON_PATH),
            JsonCarte::class.java
        )*/

        logger.info("Chargement des données C) onisep")
        val onisepData = OnisepData.fromFiles(dataSources)

        logger.info("Chargement des données D) ROME")
        val romeData = RomeData.load(dataSources)
        onisepData.insertRomeData(romeData.centresInterest())

        val statistiques =
            Serialisation.fromZippedJson(
                /* path = */ dataSources.getSourceDataFilePath(DataSources.STATS_BACK_SRC_FILENAME),
                /* type = */ PsupStatistiques::class.java
            )

        val grpToFormations = psupData.getFormationToVoeux()
        val formationsToGrp = HashMap<String,String>()
        grpToFormations.forEach { (key, value) ->
            value.forEach { f ->
                formationsToGrp[Constants.gTaCodToFrontId(f.gTaCod)] = key
            }
        }

        psupData.filActives.addAll(statistiques.lasFlCodes)

        val psupKeyToMpsKey = Collections.unmodifiableMap(psupData.getPsupKeyToMpsKey())

        val mpsKeyToPsupKeys = HashMap<String,MutableSet<String>>()
        psupKeyToMpsKey.forEach { (s, s2) ->
            mpsKeyToPsupKeys.computeIfAbsent(s2) { HashSet() }.add(s)
        }

        val specialites = SpecialitesLoader.load(
            statistiques,
            dataSources
        )


        /* can be deleted after next data update */
        statistiques.removeSmallPopulations()
        statistiques.rebuildMiddle50()
        statistiques.createGroupAdmisStatistique(
            psupKeyToMpsKey
        )
        statistiques.createGroupAdmisStatistique(
            PsupData.getGtaToLasMapping(psupData)
        )

        val labels = Labels.getLabels(
            statistiques.nomsFilieres,
            psupData,
            onisepData,
            psupData.psupKeyToMpsKey
        )
        val debugLabels = HashMap(labels)
        mpsKeyToPsupKeys.forEach { (key, value) ->
            if(value.size >= 2)
                debugLabels[key] = labels[key] + Constants.GROUPE_INFIX + value.toString()
        }

        val formationsMps = updateFormationDbs.computeFormationsMps(psupData, statistiques.lasFlCodes)
        val nbFormations = HashMap<String,Int>()
        val capacities = HashMap<String,Int>()
        grpToFormations.forEach { (key, value) ->
            nbFormations[key] = value.size
            capacities[key] = value.stream()
                .mapToInt { f: Formation -> f.capacite }
                .sum()
        }
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

        val passKey = Constants.gFlCodToFrontId(Constants.PASS_FL_COD)
        val metiersPass = metiersVersFormations
            .filter { it.value.contains(passKey) }
            .map { it.key }
            .toSet()

        val formationsVersMetiers = HashMap<String,MutableSet<String>>()

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

        val tagsSources = TagsSources.loadTagsSources(
            psupData.getPsupKeyToMpsKey(),
            dataSources
        )

        //le référentiel est formations front
        formationsMps.forEach { formation ->
            val label: String = labels.getOrDefault(formation, formation)
            tagsSources.add(label, formation)
            if (label.contains("L1")) {
                tagsSources.add("licence", formation)
            }
            if (label.lowercase(Locale.getDefault()).contains("infirmier")) {
                tagsSources.add("IFSI", formation)
            }
            metiersVersFormations[formation]?.forEach { metier ->
                val labelMetier = labels[metier]
                if(labelMetier != null) {
                    tagsSources.add(
                        labelMetier,
                        formation
                    )
                }
            }
        }
        tagsSources.normalize()

        //7. villes
        updateVillesDb(dataSources)

        //5. voeux
        val voeux = psupData.getVoeux(formationsMps)
        voeuxPort.saveAll(voeux)

        //1. candidats
        val candidats = psupData.voeuxParCandidat.map { voeuxCandidat -> Candidat(voeuxCandidat) }
        candidatsPort.saveAll(candidats)

        //2. edges
        updateEdgesDb(onisepData, psupData)

        //3. labels
        labelsPort.saveAll(labels, debugLabels)

        //4. matieres
        matieresPort.saveAll(statistiques.matieres.entries.map {
            Matiere(
                it.key,
                it.value,
                specialites.isSpecialite(it.key),
                specialites.getBacs(it.key)
            ) }
        )

        //6. formations
        updateFormationsDb(
            psupData,
            statistiques,
            formationsMps,
            debugLabels,
            capacities,
            voeux,
            mpsKeyToPsupKeys,
            formationsVersMetiers,
            labels

        )

    }
    private fun updateVillesDb(dataSources: DataSources) {
        villesPort.saveAll(loadCitiesBack(dataSources))
    }


    private fun updateFormationsDb(
        backPsupData: PsupData,
        statistiques: PsupStatistiques,
        formationsMps: List<String>,
        debugLabels: HashMap<String, String>,
        capacities: HashMap<String, Int>,
        voeux: List<Voeu>,
        mpsKeyToPsupKeys: HashMap<String, MutableSet<String>>,
        formationsVersMetiers: HashMap<String, MutableSet<String>>,
        labels: Map<String, String>
    ) {
        val formations = HashSet<fr.gouv.monprojetsup.suggestions.domain.model.Formation>()
        val psupKeyToMpsKey = backPsupData.psupKeyToMpsKey
        val apprentissage = backPsupData.getApprentissage()
        val lasToGeneric = backPsupData.lasToGeneric

        formationsMps.forEach { mpsKey ->
            val voeuxFormation = voeux.filter { it.formation == mpsKey }
            val psupKeys = mpsKeyToPsupKeys.getOrDefault(mpsKey, setOf(mpsKey))
            if(psupKeys.isEmpty()) throw RuntimeException("Pas de clé psup pour $mpsKey")
            val metiers = formationsVersMetiers[mpsKey] ?: HashSet()
            val stats = StatsFormation(
                statistiques.getStatsMoyGenParBac(mpsKey),
                statistiques.getNbAdmisParBac(mpsKey),
                statistiques.getPctAdmisParBac(mpsKey)?: mapOf(),
                statistiques.getNbAdmisParSpec(mpsKey) ?: mapOf(),
                statistiques.getPctAdmisParSpec(mpsKey)?: mapOf(),
                backPsupData.getStatsFilSim(psupKeys)?: mapOf()
            )
            val duree =
                psupKeys
                    .map { fps -> backPsupData.getDuree(fps, psupKeyToMpsKey, lasToGeneric.keys) }
                    .filterNotNull()
                    .minOrNull()

            if(duree !=  null) {
                val formation = Formation(
                    mpsKey,
                    labels.getOrDefault(mpsKey, mpsKey),
                    debugLabels.getOrDefault(mpsKey, mpsKey),
                    capacities.getOrDefault(mpsKey, 0),
                    apprentissage.contains(mpsKey),
                    duree,//psupKeys is non empty
                    lasToGeneric[mpsKey],
                    voeuxFormation,
                    ArrayList(metiers),
                    stats,
                    ArrayList(psupKeys)
                )
                formations.add(formation)
            } else {
                logger.info("Skipped formation $mpsKey because of missing duration")
            }
        }
        formationsPort.saveAll(formations)
    }

    private fun updateEdgesDb(onisepData: OnisepData, psupData : PsupData) {
        val psupKeyToMpsKey = psupData.psupKeyToMpsKey
        val lasToGeneric = psupData.lasToGeneric
        val lasToPass = psupData.lasToPass
        edgesPort.saveAll(onisepData.edgesInteretsMetiers, EdgesPort.TYPE_EDGE_INTERET_METIER)
        edgesPort.saveAll(onisepData.edgesFilieresThematiques, EdgesPort.TYPE_EDGE_FILIERES_THEMATIQUES)
        edgesPort.saveAll(onisepData.edgesThematiquesMetiers, EdgesPort.TYPE_EDGE_THEMATIQUES_METIERS)
        edgesPort.saveAll(onisepData.edgesSecteursMetiers, EdgesPort.TYPE_EDGE_SECTEURS_METIERS)
        edgesPort.saveAll(onisepData.edgesMetiersAssocies, EdgesPort.TYPE_EDGE_METIERS_ASSOCIES)
        edgesPort.saveAll(psupKeyToMpsKey, EdgesPort.TYPE_EDGE_FILIERES_GROUPES)
        edgesPort.saveAll(lasToGeneric, EdgesPort.TYPE_EDGE_LAS_TO_GENERIC)
        edgesPort.saveAll(lasToPass, EdgesPort.TYPE_EDGE_LAS_TO_PASS)
        edgesPort.saveAll(onisepData.edgesInteretsToInterets, EdgesPort.TYPE_EDGE_INTEREST_TO_INTEREST)
    }



    @Throws(IOException::class)
    fun loadCitiesBack(sources: DataSources): List<Ville> {
        logger.info("Double indexation des villes")
        val citiesOld = Serialisation.fromJsonFile(
            sources.getSourceDataFilePath(DataSources.CITIES_FILE_PATH),
            CitiesExternal::class.java
        )

        val mByDpt: MutableMap<String, Pair<String, MutableList<Coords>>> = HashMap()
        citiesOld.cities()
            .filter { c -> c.zip_code != null }
            .forEach { c ->
                var key = c.name()
                val zipcode = c.zip_code().toInt()
                key += zipcode / 1000
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
        mByDpt.values.forEach{ value: Pair<String, MutableList<Coords>> ->
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
                            Ville("i" + c.insee_code(), gpsCoords)
                        )
                    }
                }
            }
        }
        return cities
    }



}