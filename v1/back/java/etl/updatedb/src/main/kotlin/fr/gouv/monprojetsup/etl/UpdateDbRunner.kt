package fr.gouv.monprojetsup.etl

import fr.gouv.monprojetsup.data.UrlsUpdater
import fr.gouv.monprojetsup.data.app.entity.*
import fr.gouv.monprojetsup.data.app.infrastructure.app.*
import fr.gouv.monprojetsup.suggestions.domain.Constants
import fr.gouv.monprojetsup.suggestions.domain.model.Candidat
import fr.gouv.monprojetsup.suggestions.domain.model.StatsFormation
import fr.gouv.monprojetsup.suggestions.domain.model.Voeu
import fr.gouv.monprojetsup.suggestions.domain.port.*
import fr.gouv.monprojetsup.suggestions.infrastructure.DataSources
import fr.gouv.monprojetsup.suggestions.infrastructure.DataSources.STATS_BACK_SRC_FILENAME
import fr.gouv.monprojetsup.suggestions.infrastructure.model.attendus.Attendus
import fr.gouv.monprojetsup.suggestions.infrastructure.model.attendus.GrilleAnalyse
import fr.gouv.monprojetsup.suggestions.infrastructure.model.bacs.BacsLoader
import fr.gouv.monprojetsup.suggestions.infrastructure.model.cities.CitiesBack
import fr.gouv.monprojetsup.suggestions.infrastructure.model.descriptifs.DescriptifsFormations
import fr.gouv.monprojetsup.suggestions.infrastructure.model.descriptifs.DescriptifsLoader
import fr.gouv.monprojetsup.suggestions.infrastructure.model.formations.Formation
import fr.gouv.monprojetsup.suggestions.infrastructure.model.specialites.SpecialitesLoader
import fr.gouv.monprojetsup.suggestions.infrastructure.model.stats.PsupStatistiques
import fr.gouv.monprojetsup.suggestions.infrastructure.model.tags.TagsSources
import fr.gouv.monprojetsup.suggestions.infrastructure.model.thematiques.Thematiques
import fr.gouv.monprojetsup.suggestions.infrastructure.onisep.OnisepData
import fr.gouv.monprojetsup.suggestions.infrastructure.psup.PsupData
import fr.gouv.monprojetsup.suggestions.infrastructure.psup.PsupData.getGtaToLasMapping
import fr.gouv.monprojetsup.suggestions.infrastructure.rome.RomeData
import fr.gouv.monprojetsup.suggestions.poc.BackEndData
import fr.gouv.monprojetsup.suggestions.poc.ServerData
import fr.gouv.monprojetsup.suggestions.tools.Serialisation
import fr.gouv.monprojetsup.suggestions.tools.csv.CsvTools
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.stereotype.Component
import java.util.*
import java.util.logging.Logger

@SpringBootApplication
@ComponentScan(basePackages = ["fr.gouv.monprojetsup"])
@EntityScan("fr.gouv.monprojetsup.formation.infrastructure.entity")
class UpdateDbRunner

	fun main(args: Array<String>) {
		runApplication<UpdateDbRunner>(*args)
	}

@Component
@ConditionalOnProperty(name = ["etl.runner.enabled"], havingValue = "true", matchIfMissing = true)
class EtlApplicationRunner(
	private val baccalaureatBDD: BaccalaureatDb,
	private val criteresDb: CriteresDb,
	private val domainesDb : DomainesDb,
	private val domainesCategoriesDb : DomainesCategoryDb,
	private val formationsdb : FormationsDb,
	private val dataSources: DataSources,
	private val candidatsPort: CandidatsPort,
	private val voeuxPort: VoeuxPort,
	private val matieresPort: MatieresPort,
	private val labelsPort: LabelsPort,
	private val formationsPort: FormationsPort,
	private val edgesPort: EdgesPort
) : CommandLineRunner {


	private val LOGGER: Logger = Logger.getLogger(UpdateDbRunner::class.java.simpleName)
	override fun run(vararg args: String?) {
		println("Hello, World! ApplicationEtl has started.")

		updateBaccalaureatDb()

		updateCriteresDb()

		updateDomainesDb()

		updateFormationsDb()

		updateSuggestionsDbs()

	}

	private fun updateSuggestionsDbs() {

		//log.info("Loading server data...");
		val backendData: BackEndData = Serialisation.fromZippedJson(
			dataSources.backDataFilePath,
			BackEndData::class.java
		)
		val onisepData = backendData.onisepData
		val backPsupData = backendData.psupData
		backPsupData.cleanup() //should be useless but it does not harm...
		val statistiques =
			Serialisation.fromZippedJson(
				/* path = */ dataSources.getSourceDataFilePath(STATS_BACK_SRC_FILENAME),
				/* type = */ PsupStatistiques::class.java
			)

		val grpToFormations = backPsupData.getFormationToVoeux()
		val formationsToGrp = HashMap<String,String>()
		grpToFormations.forEach { (key, value) ->
			value.forEach { f ->
				formationsToGrp[Constants.gTaCodToFrontId(f.gTaCod)] = key
			}
		}

		val cities = CitiesBack(backendData.cities.cities)

		backPsupData.filActives.addAll(statistiques.lasFlCodes)

		val psupKeyToMpsKey = Collections.unmodifiableMap(backPsupData.getPsupKeyToMpsKey())

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
			getGtaToLasMapping(backPsupData)
		)

		statistiques.updateLabels(onisepData, backPsupData)
		val debugLabels = HashMap(statistiques.labels)
		mpsKeyToPsupKeys.forEach { (key, value) ->
			debugLabels[key] = statistiques.labels[key] + ServerData.GROUPE_INFIX + value.toString()
		}

		val filieresFront = computeFilieresFront(backPsupData, statistiques.lasFlCodes)
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
			backPsupData.psupKeyToMpsKey,
			backPsupData.lasMpsKeys,
			dataSources
		)

		val metiersVersFormations = onisepData.getMetiersVersFormationsExtendedWithGroupsAndLASAndDescriptifs(
			backPsupData.psupKeyToMpsKey,
			backPsupData.genericToLas,
			descriptifs
		)

		val passKey = Constants.gFlCodToFrontId(Constants.PASS_FL_COD)
		val metiersPass = metiersVersFormations
			.filter { it.value.contains(passKey) }
			.map { it.key }
			.toSet()

		val formationsVersMetiers = HashMap<String,MutableSet<String>>()

		val lasMpsKeys = backPsupData.lasMpsKeys
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
			backPsupData.getPsupKeyToMpsKey(),
			dataSources
		)
		filieresFront.forEach { formation ->
				val label: String = statistiques.labels.getOrDefault(formation, formation)
				tagsSources.add(label, formation)
				if (label.contains("L1")) {
					tagsSources.add("licence", formation)
				}
				if (label.lowercase(Locale.getDefault()).contains("infirmier")) {
					tagsSources.add("IFSI", formation)
				}
				metiersVersFormations[formation]?.forEach { metier ->
					val label = statistiques.labels[metier]
					if(label != null) {
						tagsSources.add(
							label,
							formation
						)
					}
				}
		}
		tagsSources.normalize()


		//1. candidats
		val candidats = backPsupData.voeuxParCandidat.map { voeux -> Candidat(voeux) }
		candidatsPort.saveAll(candidats)

		//2. edges
		updateEdgesDb(onisepData, backPsupData);

		//3. labels
		//4. matieres
		//5. voeux
		val voeux = backPsupData.voeux
		voeuxPort.saveAll(voeux)


		//6. formations
		updatesFormationsDb(
			backPsupData,
			statistiques,
			filieresFront,
			debugLabels,
			capacities,
			voeux,
			mpsKeyToPsupKeys,
			formationsVersMetiers
		)

		//Serialisation.toJsonFile("tagsSources.json", ServerData.tagsSources, true)


		//Candidats

			TODO("Not yet implemented")
		/*

		//if neeeded???

		  		val carte = fr.gouv.monprojetsup.data.tools.Serialisation.fromJsonFile(
			dataSources.getSourceDataFilePath(DataSources.CARTE_JSON_PATH),
			fr.gouv.parcoursup.carte.modele.modele.JsonCarte::class.java
		)


		LOGGER.info("Chargement de " + DataSources.CITIES_FILE_PATH)
		val cities = fr.gouv.monprojetsup.data.model.cities.CitiesLoader.loadCitiesBack(dataSources)

		//END if needed???


		*/


		/* interests <--> interests */

		//@PostConstruct
		//fun load() {
			//load filieresFront

			/*
                  this.statistiques = new PsupStatistiques();
        ServerData.statistiques.labels = Serialisation.fromJsonFile(
                "labelsDebug.json",
                Map.class
        );*/

			//load labels

			//load relatedInterests
			/* onisepData.interets().expansion().getOrDefault(key, Collections.emptyList())*/

			//load filieres similaires backpsupdata
			//inherit groups but not too much....

			//init formations
			//includes both fl and fr and gta codes
			//        /*        List<Formation> fors = Collections.emptyList();
			//        ///attention aux groupes
			//        if (flKey.startsWith(FILIERE_PREFIX)) {
			//            fors = SuggestionsData.getFormationsFromFil(flKey);
			//        } else if (flKey.startsWith((Constants.FORMATION_PREFIX))) {
			//            int gTaCod = Integer.parseInt(flKey.substring(2));
			//            Formation f = SuggestionsData.getFormation(gTaCod);
			//            if (f != null) {
			//                fors = List.of(f);
			//            }
			//        }
			//        */
			//        //should include groups


			//        throw new NotImplementedException("Not implemented yet");

			//init cities
		/*log.info("Double indexation des villes");
        CitiesBack cities = ServerData.cities;
        Distances.init(cities);

		//init dures from psup data

		//liensSecteursMetiers

        //lasFilieres

        //nbAdmisParSpecialite
        ServerData.nbAdmisParSpecialite.nbAdmisParSpecialite()

        //bacs with spcialites
        ServerData.nbAdmisParSpecialite.specialitesParBac().keySet()



        //descriptifs
                / * UpdateFrontData.DataContainer.loadDescriptifs(
                onisepData,
                backPsupData.getCorrespondances(),
                statistiques.getLASCorrespondance().lasToGeneric()
        );

         */

			//throw new NotImplementedException("Not implemented yet");

	}

	private fun updatesFormationsDb(
		backPsupData: PsupData,
		statistiques: PsupStatistiques,
		filieresFront: List<String>,
		debugLabels: HashMap<String, String>,
		capacities: HashMap<String, Int>,
		voeux: List<Voeu>,
		mpsKeyToPsupKeys: HashMap<String, MutableSet<String>>,
		formationsVersMetiers: HashMap<String, MutableSet<String>>
	) {
		val formations = HashSet<fr.gouv.monprojetsup.suggestions.domain.model.Formation>()
		val psupKeyToMpsKey = backPsupData.psupKeyToMpsKey
		val apprentissage = backPsupData.getApprentissage()
		val lasToGeneric = backPsupData.lasToGeneric

		filieresFront.forEach { mpsKey ->
			val voeuxFormation = voeux.filter { it.formation == mpsKey }
			val psupKeys = mpsKeyToPsupKeys.getOrDefault(mpsKey, setOf(mpsKey))
			val metiers = formationsVersMetiers[mpsKey] ?: HashSet()
			val stats = StatsFormation(
				statistiques.getStatsMoyGenParBac(mpsKey),
				statistiques.getNbAdmisParBac(mpsKey),
				statistiques.getStatsSpec(mpsKey),
				backPsupData.getStatsFilSim(psupKeys)
			)
			val formation = fr.gouv.monprojetsup.suggestions.domain.model.Formation(
				mpsKey,
				statistiques.labels.getOrDefault(mpsKey,mpsKey),
				debugLabels.getOrDefault(mpsKey,mpsKey),
				capacities.getOrDefault(mpsKey,0),
				apprentissage.contains(mpsKey),
				psupKeys.map { fps -> backPsupData.getDuree(fps, psupKeyToMpsKey) }.minOrNull() ?: 5,
				lasToGeneric[mpsKey],
				voeuxFormation,
				ArrayList(metiers),
				stats,
				ArrayList(psupKeys)
				)
			formations.add(formation)
		}
		formationsPort.saveAll(formations)
	}

	private fun updateEdgesDb(onisepData: OnisepData, psupData : PsupData) {
		val psupKeyToMpsKey = psupData.psupKeyToMpsKey
		val lasToGeneric = psupData.lasToGeneric;
		val lasToPass = psupData.lasToPass;
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


	private fun updateFormationsDb() {
		LOGGER.info("Chargement de " + dataSources.getSourceDataFilePath(
			DataSources.BACK_PSUP_DATA_FILENAME))
		val psupData = Serialisation.fromZippedJson(
			dataSources.getSourceDataFilePath(DataSources.BACK_PSUP_DATA_FILENAME),
			PsupData::class.java
		)
		psupData.cleanup()

		LOGGER.info("Chargement des données Onisep")
		val onisepData = OnisepData.fromFiles(dataSources)

		LOGGER.info("Chargement des données ROME")
		val romeData = RomeData.load(dataSources)
		LOGGER.info("Insertion des données ROME dans les données Onisep")
		onisepData.insertRomeData(romeData.centresInterest) //before updateLabels

		LOGGER.info("Chargement des stats depuis " + DataSources.STATS_BACK_SRC_FILENAME)
		val stats = Serialisation.fromZippedJson(
			dataSources.getSourceDataFilePath(DataSources.STATS_BACK_SRC_FILENAME),
			PsupStatistiques::class.java
		)

		stats.createGroupAdmisStatistique(psupData.psupKeyToMpsKey)
		stats.createGroupAdmisStatistique(getGtaToLasMapping(psupData))

		LOGGER.info("Maj des données Onisep (noms des filières et urls)")
		stats.updateLabels(onisepData, psupData)


		updateFormationsDb(psupData, stats, onisepData)
	}

	private fun updateFormationsDb(
		psupData: PsupData,
		statistiques: PsupStatistiques,
		onisepData: OnisepData
	) {
		//le référentiel est formations front
		val formations = computeFilieresFront(
			psupData,
			statistiques.lasFlCodes
		)

		LOGGER.info("Calcul des correspondance")
		val psupKeyToMpsKey = psupData.psupKeyToMpsKey
		val mpsKeyToPsupKeys = revert(psupKeyToMpsKey)

		LOGGER.info("Génération des descriptifs")
		val descriptifs =
            DescriptifsLoader.loadDescriptifs(
                onisepData,
                psupKeyToMpsKey,
				psupData.getLasMpsKeys(),
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

		LOGGER.info("Ajout des liens metiers")
		val links = HashMap<String, DescriptifsFormations.Link>()
		statistiques.liensOnisep.forEach { (key, value) ->
			links[key] = DescriptifsFormations.toAvenirs(value, statistiques.labels.getOrDefault(key, ""))
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

		formationsdb.deleteAll()
		formations.forEach { flCod ->
			val entity = FormationDetailleeEntity()
			entity.id = flCod
			val label = statistiques.labels[flCod] ?: throw RuntimeException("Pas de label pour la formation $flCod")
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
			entity.metiers = listOf()

			formationsdb.save(entity)
		}

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


	private fun updateBaccalaureatDb() {

		baccalaureatBDD.deleteAll()
		val bacs = BacsLoader.load(dataSources)
		bacs.forEach { baccalaureat ->
			val entity = BaccalaureatEntity()
			entity.id = baccalaureat.key
			entity.nom = baccalaureat.label
			entity.idExterne = baccalaureat.key
			baccalaureatBDD.save(entity)
		}

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

	private fun updateDomainesDb() {

		domainesDb.deleteAll()
		domainesCategoriesDb.deleteAll()

		val categories = loadThematiques()
		var i = 0
		categories.forEach { categorie ->
			val entity = DomaineCategoryEntity()
			entity.id = "cat_" + i++
			entity.nom = categorie.label
			entity.emoji = categorie.emoji
			domainesCategoriesDb.save(entity)
			categorie.items.forEach { domaine ->
				val domaineEntity = DomaineEntity()
				domaineEntity.id = domaine.key
				domaineEntity.nom = domaine.label
				domaineEntity.emoji = domaine.emoji
				domaineEntity.idCategorie = entity.id
				domainesDb.save(domaineEntity)
			}
		}
	}

	fun loadThematiques(): List<Thematiques.Category> {
		val groupes: MutableMap<String, Thematiques.Category> = HashMap()
		val categories: MutableList<Thematiques.Category> = java.util.ArrayList()

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
				cat = Thematiques.Category(groupe, emojig, java.util.ArrayList())
				groupes[groupe] = cat
				categories.add(cat)
			}
			cat.items.add(Thematiques.Item(id, label, emoji))
		}
		return categories
	}

	fun computeFilieresFront(
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

}