package fr.gouv.monprojetsup.etl

import fr.gouv.monprojetsup.data.DataSources
import fr.gouv.monprojetsup.data.ServerData
import fr.gouv.monprojetsup.data.ServerData.getGtaToLasMapping
import fr.gouv.monprojetsup.data.analysis.ServerDataAnalysis
import fr.gouv.monprojetsup.data.config.DataServerConfig
import fr.gouv.monprojetsup.data.model.attendus.Attendus
import fr.gouv.monprojetsup.data.model.attendus.GrilleAnalyse
import fr.gouv.monprojetsup.data.model.bacs.BacsLoader
import fr.gouv.monprojetsup.data.model.cities.CitiesLoader
import fr.gouv.monprojetsup.data.model.descriptifs.Descriptifs
import fr.gouv.monprojetsup.data.model.specialites.SpecialitesLoader
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques
import fr.gouv.monprojetsup.data.model.tags.TagsSources
import fr.gouv.monprojetsup.data.model.thematiques.Thematiques.loadthematiques
import fr.gouv.monprojetsup.data.tools.Serialisation
import fr.gouv.monprojetsup.data.update.UpdateFrontData.DataContainer
import fr.gouv.monprojetsup.data.update.onisep.OnisepData
import fr.gouv.monprojetsup.data.update.psup.PsupData
import fr.gouv.monprojetsup.data.update.rome.RomeData
import fr.gouv.monprojetsup.formation.infrastructure.entity.*
import fr.gouv.parcoursup.carte.modele.modele.JsonCarte
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.ComponentScan
import org.springframework.stereotype.Component
import java.util.*
import java.util.logging.Logger
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

@SpringBootApplication
@ComponentScan(basePackages = ["fr.gouv.monprojetsup"])
@EntityScan("fr.gouv.monprojetsup.formation.infrastructure.entity")
class ApplicationEtl

	fun main(args: Array<String>) {
		runApplication<ApplicationEtl>(*args)
	}

@Component
@ConditionalOnProperty(name = ["etl.runner.enabled"], havingValue = "true", matchIfMissing = true)
class EtlApplicationRunner : CommandLineRunner {

	@Autowired
	lateinit var baccalaureatBDD: BaccalaureatDb

	@Autowired
	lateinit var criteresDb: CriteresDb

	@Autowired
	lateinit var domainesDb : DomainesDb

	@Autowired
	lateinit var domainesCategoriesDb : DomainesCategoryDb

	@Autowired
	lateinit var formationsdb : FormationsDb

	@Autowired
	lateinit var context: ConfigurableApplicationContext

	@Value("\${dataRootDirectory}")
	lateinit var dataRootDirectory: String

	private val LOGGER: Logger = Logger.getLogger(ApplicationEtl::class.java.simpleName)
	override fun run(vararg args: String?) {
		println("Hello, World! ApplicationEtl has started.")

		DataServerConfig.setDataRootDirectory(dataRootDirectory)

		updateBaccalaureatDb()

		updateCriteresDb()

		updateDomainesDb()

		updateFormationsDb()

		updateSuggestionsDbs()

		context.close()
	}

	private fun updateSuggestionsDbs() {
			TODO("Not yet implemented")
		/*        //ajout des secteurs d'activité
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
	}

	private fun updateFormationsDb() {
		LOGGER.info("Chargement de " + DataSources.getSourceDataFilePath(DataSources.BACK_PSUP_DATA_FILENAME))
		val psupData = Serialisation.fromZippedJson(
			DataSources.getSourceDataFilePath(DataSources.BACK_PSUP_DATA_FILENAME),
			PsupData::class.java
		)
		psupData.cleanup()

		LOGGER.info("Chargement des données Onisep")
		val onisepData = OnisepData.fromFiles()

		LOGGER.info("Chargement des données ROME")
		val romeData = RomeData.load()
		LOGGER.info("Insertion des données ROME dans les données Onisep")
		onisepData.insertRomeData(romeData.centresInterest) //before updateLabels

		val carte = Serialisation.fromJsonFile(
			DataSources.getSourceDataFilePath(ServerDataAnalysis.CARTE_JSON_PATH),
			JsonCarte::class.java
		)

		LOGGER.info("Chargement des stats depuis " + DataSources.STATS_BACK_SRC_FILENAME)
		val stats = Serialisation.fromZippedJson(
			DataSources.getSourceDataFilePath(DataSources.STATS_BACK_SRC_FILENAME),
			PsupStatistiques::class.java
		)

		stats.createGroupAdmisStatistique(psupData.correspondances)
		stats.createGroupAdmisStatistique(getGtaToLasMapping(psupData, stats))

		LOGGER.info("Maj des données Onisep (noms des filières et urls)")
		stats.updateLabels(onisepData, psupData, stats.lasCorrespondance.lasToGeneric)


		updateFormationsDb(psupData, stats, onisepData, carte)
	}

	private fun updateFormationsDb(
		psupData: PsupData,
		stats: PsupStatistiques,
		onisepData: OnisepData,
		carte: JsonCarte
	) {
		//le référentiel est formations front
		val formations = ServerData.computeFilieresFront(
			psupData,
			stats.lasFlCodes
		)

		LOGGER.info("Calcul des correspondance")
		val groups = psupData.correspondances
		val reverseGroups = revert(groups)
		val lasCorrespondance = stats.lasCorrespondance

		LOGGER.info("Génération des descriptifs")
		val descriptifs = DataContainer.loadDescriptifs(onisepData, groups, lasCorrespondance.lasToGeneric)

		val attendus = Attendus.getAttendus(
			psupData,
			stats,
			SpecialitesLoader.load(ServerData.statistiques),
			false
		)

		LOGGER.info("Chargement de " + DataSources.CITIES_FILE_PATH)
		val cities = CitiesLoader.loadCitiesBack()

		LOGGER.info("Ajout des liens metiers")
		val links = HashMap<String, Descriptifs.Link>()
		stats.liensOnisep.forEach { (key: String, value: String?) ->
			links[key] = Descriptifs.toAvenirs(value, stats.labels.getOrDefault(key, ""))
		}
		val urls = DataContainer.updateLinks(onisepData, links, lasCorrespondance.lasToGeneric, descriptifs)

		val grilles = GrilleAnalyse.getGrilles(psupData)

		val tagsSources = TagsSources.load(groups).getKeyToTags()

		formationsdb.deleteAll()
		formations.forEach { flCod ->
			val entity = FormationDetailleeEntity()
			entity.id = flCod
			val label = stats.labels.get(flCod) ?: throw RuntimeException("Pas de label pour la formation $flCod")
			entity.label = label
			entity.descriptifGeneral = descriptifs.getDescriptifGeneralFront(flCod)
			entity.descriptifDiplome = descriptifs.getDescriptifDiplomeFront(flCod)
			val attendus = attendus.get(flCod)
			if (attendus == null) {
				entity.descriptifConseils = null
				entity.descriptifAttendus = null
			} else {
				entity.descriptifConseils = attendus.getConseilsFront()
				entity.descriptifAttendus = attendus.getAttendusFront()
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

			val motsClefs = tagsSources.get(flCod)
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
		val bacs = BacsLoader.load()
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

		val categories = loadthematiques();
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

}