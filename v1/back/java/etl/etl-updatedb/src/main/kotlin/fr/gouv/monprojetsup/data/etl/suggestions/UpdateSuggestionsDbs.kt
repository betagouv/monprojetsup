package fr.gouv.monprojetsup.data.etl.suggestions

import fr.gouv.monprojetsup.data.MpsDataPort
import fr.gouv.monprojetsup.data.app.infrastructure.*
import fr.gouv.monprojetsup.data.domain.Constants
import fr.gouv.monprojetsup.data.domain.model.*
import fr.gouv.monprojetsup.data.domain.port.*
import fr.gouv.monprojetsup.data.infrastructure.DataSources
import fr.gouv.monprojetsup.data.infrastructure.model.cities.CitiesExternal
import fr.gouv.monprojetsup.data.infrastructure.model.cities.Coords
import fr.gouv.monprojetsup.data.tools.Serialisation
import org.apache.commons.lang3.tuple.Pair
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.*
import java.util.logging.Logger

@Component
class UpdateSuggestionsDbs(
    private val dataSources: DataSources,
    private val candidatsPort: CandidatsPort,
    private val villesPort: VillesPort,
    private val matieresPort: MatieresPort,
    private val labelsPort: LabelsPort,
    private val edgesPort: EdgesPort,
    private val mpsDataPort: MpsDataPort
) {

    private val logger: Logger = Logger.getLogger(UpdateSuggestionsDbs::class.java.simpleName)

    internal fun updateSuggestionDbs() {

        //1. candidats
        updateCandidatsDb()

        //2. edges
        updateEdgesDb()

        //3. labels
        updateLabelsDb()

        //4. matieres
        updateMatieresDb()

        //5. villes
        updateVillesDb()

    }

    private fun updateMatieresDb() {
        matieresPort.saveAll(mpsDataPort.getMatieres())
    }

    private fun updateLabelsDb() {
        val labels = mpsDataPort.getLabels()
        val debugLabels = HashMap(labels)
        val mpsKeyToPsupKeys = mpsDataPort.getMpsIdToPsupFlIds()
        mpsKeyToPsupKeys.forEach { (key, value) ->
            if(value.size >= 2)
                debugLabels[key] = labels[key] + Constants.GROUPE_INFIX + value.toString()
        }
        labelsPort.saveAll(labels, debugLabels)

    }

    private fun updateCandidatsDb() {
        val candidats = mpsDataPort.getVoeuxParCandidat().map { voeuxCandidat ->
            Candidat(
                voeuxCandidat
            )
        }
        candidatsPort.saveAll(candidats)
    }


    private fun updateVillesDb() {
        val cities = loadCitiesBack()
        villesPort.saveAll(cities)
    }

    /*

    private fun updateFormationsDb(
        psupData: PsupData,
        statistiques: PsupStatistiques,
        onisepData: OnisepData
    ) {
        val psupKeyToMpsKey = psupData.psupKeyToMpsKey
        val apprentissage = psupData.getApprentissage()
        val lasToGeneric = psupData.lasToGeneric

        val formationsMps = mpsDataPort.getFormationsMpsIds()

        val voeux = psupData.getVoeux(formationsMps)

        val labels = Labels.getLabels(
            statistiques.nomsFilieres,
            psupData,
            onisepData
        )

        val mpsKeyToPsupKeys = psupData.getMpsKeyToPsupKeys()

        val debugLabels = HashMap(labels)
        mpsKeyToPsupKeys.forEach { (key, value) ->
            if(value.size >= 2)
                debugLabels[key] = labels[key] + Constants.GROUPE_INFIX + value.toString()
        }

        val capacities = HashMap<String,Int>()
        val nbFormations = HashMap<String,Int>()
        val grpToFormations = psupData.getFormationToVoeux()
        grpToFormations.forEach { (key, value) ->
            nbFormations[key] = value.size
            capacities[key] = value.stream()
                .mapToInt { f: Formation -> f.capacite }
                .sum()
        }

        val formationsVersMetiers = getFormationsVersMetiers(onisepData, psupData)

        val formations = ArrayList<fr.gouv.monprojetsup.data.domain.model.Formation>()
        formationsMps.forEach { mpsKey ->
            val voeuxFormation = voeux.filter { it.formation == mpsKey }
            val psupKeys = mpsKeyToPsupKeys.getOrDefault(mpsKey, setOf(mpsKey))
            if(psupKeys.isEmpty()) throw RuntimeException("Pas de clé psup pour $mpsKey")
            val metiers = formationsVersMetiers[mpsKey] ?: HashSet()
            val stats = StatsFormation(
                statistiques.getStatsMoyGenParBac(mpsKey),
                statistiques.getNbAdmisParBac(mpsKey),
                statistiques.getPctAdmisParBac(mpsKey) ?: mapOf(),
                statistiques.getNbAdmisParSpec(mpsKey) ?: mapOf(),
                statistiques.getPctAdmisParSpec(mpsKey) ?: mapOf(),
                psupData.getStatsFilSim(psupKeys) ?: mapOf()
            )
            val duree =
                psupKeys.mapNotNull { fps -> psupData.getDuree(fps, psupKeyToMpsKey, lasToGeneric.keys) }
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

     */

    private fun updateEdgesDb() {
        val edges = mpsDataPort.getEdges()
        edgesPort.saveAll(edges)
    }



    @Throws(IOException::class)
    fun loadCitiesBack(): List<Ville> {
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



}