package fr.gouv.monprojetsup.etl

import fr.gouv.monprojetsup.data.DataSources
import fr.gouv.monprojetsup.data.UrlsUpdater
import fr.gouv.monprojetsup.data.las.LasPassHelpers.getGtaToLasMapping
import fr.gouv.monprojetsup.data.model.attendus.GrilleAnalyse
import fr.gouv.monprojetsup.data.model.descriptifs.DescriptifsLoader
import fr.gouv.monprojetsup.data.model.specialites.SpecialitesLoader
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques
import fr.gouv.monprojetsup.data.model.tags.TagsSources
import fr.gouv.monprojetsup.data.model.thematiques.Thematiques
import fr.gouv.monprojetsup.data.onisep.OnisepData
import fr.gouv.monprojetsup.data.psup.PsupData
import fr.gouv.monprojetsup.data.rome.RomeData
import fr.gouv.monprojetsup.data.tools.csv.CsvTools
import fr.gouv.monprojetsup.formation.infrastructure.entity.*
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.stereotype.Component
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
	private val dataSources: DataSources
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
			TODO("Not yet implemented")
		/*

		//if neeeded???

		  		val carte = fr.gouv.monprojetsup.data.tools.Serialisation.fromJsonFile(
			dataSources.getSourceDataFilePath(DataSources.CARTE_JSON_PATH),
			fr.gouv.parcoursup.carte.modele.modele.JsonCarte::class.java
		)


		LOGGER.info("Chargement de " + DataSources.CITIES_FILE_PATH)
		val cities = fr.gouv.monprojetsup.data.model.cities.CitiesLoader.loadCitiesBack(dataSources)

		//END if neeeded???


		//ajout des secteurs d'activité
        onisepData.fichesMetiers().metiers().metier().forEach(fiche -> {
            String keyMetier = cleanup(fiche.identifiant());
            if(fiche.secteurs_activite() != null && fiche.secteurs_activite().secteur_activite() != null) {
                fiche.secteurs_activite().secteur_activite().forEach(secteur -> {
                    String keySecteur = cleanup(secteur.id());
                    edgesKeys.put(keyMetier, keySecteur, true, 1.0);
                });

                if(fiche.metiers_associes() != null && fiche.metiers_associes().metier_associe() != null) {
                    fiche.metiers_associes().metier_associe().forEach(metierAssocie -> {
                        String keyMetierAssocie = cleanup(metierAssocie.id());
                        edgesKeys.put(keyMetier, keyMetierAssocie, true, 0.75);
                    });
                }
            }
        });
		*/

		/*
		filieres front
		//suppression des filières en app couvertes par une filière sans app,
        toErase.addAll(
                backPsupData.getFormationsenAppAvecEquivalentSansApp(filActives)
                        .stream().map(Constants::gFlCodToFrontId)
                        .collect(Collectors.toSet())
        );
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
			/*
        log.info("Double indexation des villes");
        CitiesBack cities = ServerData.cities;
        Distances.init(cities);*/

			//init dures from psup data

			//init candidatesMetiers
			/*            val result = candidatesMetiers.get(key);
            if(result != null) return result;
            Set<String> candidates = new HashSet<>(
                    getCandidatesMetiers(key) // onisepData.edgesMetiersFilieres().getSuccessors(key).keySet()
            );
            if (isLas(key)) {
                String key2 = getGenericFromLas(key); // lasCorrespondance.lasToGeneric().get(key)
        if (key2 != null) {
                    candidates.addAll(getMetiersfromfiliere(key2)); //onisepData.edgesMetiersFilieres().getSuccessors(key2).keySet()
                    candidates.addAll(getPassMetiers());//(key2) onisepData.edgesMetiersFilieres().getSuccessors(gFlCodToFrontId(PASS_FL_COD)).keySet()
                }
            }
            candidates.addAll(getMetiersfromGroup(key));
                //if (reverseFlGroups.containsKey(key)) {
                //candidates.addAll(reverseFlGroups.get(key).stream().flatMap(g -> onisepData.edgesMetiersFilieres().getSuccessors(g).keySet().stream()).toList());
                //}
            candidatesMetiers.put(key, candidates);
            return candidates;

         //liensSecteursMetiers

        //lasFilieres

        //specialites
        ServerData.specialites.specialites()

        //bacs with spcialites
        ServerData.specialites.specialitesParBac().keySet()

        //apprentissage
          backPsupData.formations().filieres.values().forEach(filiere -> {
            String key = FILIERE_PREFIX + filiere.gFlCod;
            if (filiere.apprentissage) {
                apprentissage.add(key);
                String origKey = FILIERE_PREFIX + filiere.gFlCodeFi;
                apprentissage.add(origKey);
                apprentissage.add(flGroups.getOrDefault(origKey, origKey));
            }
        });

        //descriptifs
                / * UpdateFrontData.DataContainer.loadDescriptifs(
                onisepData,
                backPsupData.getCorrespondances(),
                statistiques.getLASCorrespondance().lasToGeneric()
        );

         */

			//throw new NotImplementedException("Not implemented yet");

	}

	private fun updateFormationsDb() {
		LOGGER.info("Chargement de " + dataSources.getSourceDataFilePath(
			DataSources.BACK_PSUP_DATA_FILENAME))
		val psupData = fr.gouv.monprojetsup.data.tools.Serialisation.fromZippedJson(
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
		val stats = fr.gouv.monprojetsup.data.tools.Serialisation.fromZippedJson(
			dataSources.getSourceDataFilePath(DataSources.STATS_BACK_SRC_FILENAME),
			fr.gouv.monprojetsup.data.model.stats.PsupStatistiques::class.java
		)

		stats.createGroupAdmisStatistique(psupData.correspondances)
		stats.createGroupAdmisStatistique(
			getGtaToLasMapping(
				psupData,
				stats
			)
		)

		LOGGER.info("Maj des données Onisep (noms des filières et urls)")
		stats.updateLabels(onisepData, psupData, stats.lasCorrespondance.lasToGeneric)


		updateFormationsDb(psupData, stats, onisepData)
	}

	private fun updateFormationsDb(
		psupData: PsupData,
		stats: PsupStatistiques,
		onisepData: OnisepData
	) {
		//le référentiel est formations front
		val formations = fr.gouv.monprojetsup.data.ServerData.computeFilieresFront(
			psupData,
			stats.lasFlCodes
		)

		LOGGER.info("Calcul des correspondance")
		val groups = psupData.correspondances
		val reverseGroups = revert(groups)
		val lasCorrespondance = stats.lasCorrespondance

		LOGGER.info("Génération des descriptifs")
		val descriptifs =
            DescriptifsLoader.loadDescriptifs(
                onisepData,
                groups,
                lasCorrespondance.lasToGeneric,
                dataSources
            )

		val attendus = fr.gouv.monprojetsup.data.model.attendus.Attendus.getAttendus(
			psupData,
			stats,
			SpecialitesLoader.load(stats, dataSources),
			false
		)

		LOGGER.info("Ajout des liens metiers")
		val links = HashMap<String, fr.gouv.monprojetsup.data.model.descriptifs.Descriptifs.Link>()
		stats.liensOnisep.forEach { (key: String, value: String?) ->
			links[key] = fr.gouv.monprojetsup.data.model.descriptifs.Descriptifs.toAvenirs(value, stats.labels.getOrDefault(key, ""))
		}
		val urls = UrlsUpdater.updateUrls(
			onisepData,
			links,
			lasCorrespondance.lasToGeneric,
			descriptifs
		)

		val grilles = GrilleAnalyse.getGrilles(psupData)

		val tagsSources = TagsSources.loadTagsSources(groups, dataSources).getKeyToTags()

		formationsdb.deleteAll()
		formations.forEach { flCod ->
			val entity = FormationDetailleeEntity()
			entity.id = flCod
			val label = stats.labels[flCod] ?: throw RuntimeException("Pas de label pour la formation $flCod")
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

			entity.formationsAssociees = ArrayList(reverseGroups.getOrDefault(flCod, setOf(flCod)))

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
		val bacs = fr.gouv.monprojetsup.data.model.bacs.BacsLoader.load(dataSources)
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

		val categories = loadthematiques()
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

	fun loadthematiques(): List<Thematiques.Category> {
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


}