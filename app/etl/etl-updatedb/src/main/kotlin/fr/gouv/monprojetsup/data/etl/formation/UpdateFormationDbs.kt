package fr.gouv.monprojetsup.data.etl.formation

import fr.gouv.monprojetsup.data.Constants
import fr.gouv.monprojetsup.data.commun.entity.LienEntity
import fr.gouv.monprojetsup.data.etl.BatchUpdate
import fr.gouv.monprojetsup.data.etl.MpsDataPort
import fr.gouv.monprojetsup.data.formation.entity.CritereAnalyseCandidatureEntity
import fr.gouv.monprojetsup.data.formation.entity.FormationEntity
import fr.gouv.monprojetsup.data.formation.entity.FormationVoeuEntity
import fr.gouv.monprojetsup.data.formation.entity.MoyenneGeneraleAdmisEntity
import fr.gouv.monprojetsup.data.formation.entity.VilleVoeuxEntity
import fr.gouv.monprojetsup.data.formation.entity.VoeuEntity
import fr.gouv.monprojetsup.data.formationmetier.entity.FormationMetierEntity
import fr.gouv.monprojetsup.data.model.LatLng
import fr.gouv.monprojetsup.data.model.attendus.GrilleAnalyse
import fr.gouv.monprojetsup.data.tools.GeodeticDistance
import org.hibernate.Transaction
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import java.util.logging.Logger
import kotlin.math.roundToInt


@Repository
interface CriteresDb :
    JpaRepository<CritereAnalyseCandidatureEntity, String>

@Repository
interface MoyennesGeneralesAdmisDb :
    JpaRepository<MoyenneGeneraleAdmisEntity, String>

@Repository
interface VoeuxDb :
    JpaRepository<VoeuEntity, String>

@Repository
interface FormationDb : JpaRepository<FormationEntity, String>

@Repository
interface FormationVoeuDb : JpaRepository<FormationVoeuEntity, String>

@Repository
interface JoinFormationMetierDb : JpaRepository<FormationMetierEntity, String>

@Repository
interface VillesVoeuxDb : JpaRepository<VilleVoeuxEntity, String>

@Component
class UpdateFormationDbs(
    private val criteresDb: CriteresDb,
    private val moyennesGeneralesAdmisDb: MoyennesGeneralesAdmisDb,
    private val mpsDataPort: MpsDataPort,
    private val batchUpdate: BatchUpdate,
    private val voeuxDb: VoeuxDb,
    private val formationDb: FormationDb,
    private val formationVoeuxDb: FormationVoeuDb,
    private val environment: Environment,
    private val sessionFactory: org.hibernate.SessionFactory
) {

    private val logger: Logger = Logger.getLogger(UpdateFormationDbs::class.java.simpleName)

    @Value("\${mps.minimalTestDataSet}")
    var minimalTestDataSet : Boolean = false

    internal fun update() {

        if(isMinimalTestDatasetModeActive()) {
            logger.info("Génération d'un dataset minimal pour les tests")
            batchUpdate.clearEntities(MoyenneGeneraleAdmisEntity::class.simpleName!!)
            batchUpdate.clearEntities(FormationMetierEntity::class.simpleName!!)
            batchUpdate.clearEntities(FormationVoeuEntity::class.simpleName!!)
            batchUpdate.clearEntities(VoeuEntity::class.simpleName!!)
            batchUpdate.clearEntities(VilleVoeuxEntity::class.simpleName!!)
            batchUpdate.clearEntities(FormationEntity::class.simpleName!!)
        }

        logger.info("Mise à jour de la table des formations")
        updateFormationsDb()

        logger.info("Mise à jour de la table des critères d'admission")
        updateCriteresDb()
        logger.info("Mise à jour de la table des moyennes générales des admis")
        updateMoyennesGeneralesAdmisDb()
    }

    private fun updateMoyennesGeneralesAdmisDb() {
        val data = mpsDataPort.getMoyennesGeneralesAdmis()
        val entities = ArrayList<MoyenneGeneraleAdmisEntity>()
        data.forEach { (id, frequencesCumulees) ->
            val entity = MoyenneGeneraleAdmisEntity()
            entity.id = id
            entity.frequencesCumulees = frequencesCumulees
            entities.add(entity)
        }
        moyennesGeneralesAdmisDb.deleteAll()
        moyennesGeneralesAdmisDb.saveAll(entities)
    }

    fun updateVoeuxDb(): Boolean {
        if(isMinimalTestDatasetModeActive()) {
            batchUpdate.clearEntities(FormationVoeuEntity::class.simpleName!!)
            batchUpdate.clearEntities(VoeuEntity::class.simpleName!!)
        }
        val formationsMpsIds = mpsDataPort.getFormationsMpsIds()
        val voeux = mpsDataPort.getVoeux()
        val voeuxEntities = HashMap<String, VoeuEntity>()
        val formationsVoeuxEntities = ArrayList<FormationVoeuEntity>()
        formationsMpsIds.forEach { id ->
            val voeuxFormation = voeux.getOrDefault(id, listOf()).sortedBy { it.libelle }
            voeuxFormation.forEach { voeuxEntities[it.id] = VoeuEntity(it) }
            formationsVoeuxEntities.addAll(
                voeuxFormation.map {
                    FormationVoeuEntity(id, it.id)
                }
            )
        }

        val voeuxIds : Set<String> = HashSet(voeuxEntities.keys)

        val nbFormationsVoeuxBefore = formationVoeuxDb.findAll().count()
        val nbFormationsVoeuxAfter = formationsVoeuxEntities.count()
        val changementNbVoeux = nbFormationsVoeuxBefore != nbFormationsVoeuxAfter

        val voeuxObsoletes = HashSet(voeuxDb.findAll())
        voeuxObsoletes.removeIf { voeuxIds.contains(it.id) }
        if (voeuxObsoletes.isNotEmpty()) {
            logger.warning("Marquage de ${voeuxObsoletes.count()} voeux obsoletes")
            voeuxObsoletes.forEach { it.obsolete = true }
            batchUpdate.upsertEntities(voeuxObsoletes)
        }

        batchUpdate.clearEntities(FormationVoeuEntity::class.simpleName!!)

        logger.warning("Insertion et mise à jour de ${voeuxEntities.count()} voeux")
        batchUpdate.upsertEntities(voeuxEntities.values)

        logger.warning("Insertion et mise à jour de ${formationsVoeuxEntities.count()} paires formations voeux")
        batchUpdate.setEntities(FormationVoeuEntity::class.simpleName!!, formationsVoeuxEntities)

        return changementNbVoeux

    }

     fun updateFormationsDb() {

         val labels = mpsDataPort.getFormationsLabels()
         val descriptifs = mpsDataPort.getDescriptifs()
         val attendus = mpsDataPort.getAttendus()
         val conseils = mpsDataPort.getConseils()
         val liens = mpsDataPort.getLiens()
         val grilles = mpsDataPort.getGrilles()
         val tagsSources = mpsDataPort.getMotsClesFormations()
         val formationsMpsIds = mpsDataPort.getFormationsMpsIds()
         val apprentissage = mpsDataPort.getApprentissage()
         val apprentissagePct = mpsDataPort.getApprentissagePct()
         val formationToTypeformation = mpsDataPort.getFormationToTypeformation()
         val debugLabels = mpsDataPort.getDebugLabels()
         val capacitesAccueil = mpsDataPort.getCapacitesAccueil()
         val stats = mpsDataPort.getStatsFormation()
         val etudesCourtes = mpsDataPort.getCompatEtudesCourtes()
         val etudesLongues = mpsDataPort.getCompatEtudesLongues()
         val mpsKeyToPsupKeys = mpsDataPort.getMpsIdToPsupFlIds()
         val mpsKeyToIdeoKeys = mpsDataPort.getMpsIdToIdeoIds()

         val formationsObsoletes = HashSet(formationDb.findAll())
         formationsObsoletes.removeIf { f -> formationsMpsIds.contains(f.id) }
         if(formationsObsoletes.isNotEmpty()) {
             logger.warning("Marquage de ${formationsObsoletes.count()} formations obsoletes")
             formationsObsoletes.forEach { it.obsolete = true }
             batchUpdate.upsertEntities(formationsObsoletes)
         }

         val formationEntities = ArrayList<FormationEntity>()
         formationsMpsIds.forEach { id ->
             val label = labels[id] ?: throw RuntimeException("Pas de label pour la formation $id")
             val entity = FormationEntity()
             entity.id = id
             entity.obsolete = false
             entity.label = label
             entity.typeFormation = formationToTypeformation.getOrDefault(id, id)
             entity.descriptifGeneral = descriptifs.getDescriptifGeneralFront(id).orEmpty()
             entity.descriptifDiplome = descriptifs.getDescriptifDiplomeFront(id).orEmpty()
             entity.descriptifAttendus = attendus[id].orEmpty()
             entity.descriptifConseils = conseils[id].orEmpty()
             entity.formationsAssociees = mpsKeyToPsupKeys.getOrDefault(id, setOf(id)).toList().sorted()
             entity.formationsIdeo = mpsKeyToIdeoKeys[id].orEmpty().sorted()

             val grille = grilles[id]
             if (grille == null) {
                 entity.criteresAnalyse = listOf()
             } else {
                 entity.criteresAnalyse = grille.criteresFront
             }

             val urlListe = liens.getOrDefault(id, ArrayList())
             entity.liens = urlListe
                 .map { link -> Pair(link.label, link.uri) }
                 .distinct()
                 .map { pair -> LienEntity(pair.first, pair.second) }

             val motsClefs = tagsSources.getOrDefault(id, listOf(label))
             val motsClefsCourts = motsClefs.filter { it.length <= 300 }
             val motsClefsLongs = motsClefs.filter { it.length > 300 }
             if (motsClefsCourts.size != motsClefs.size) {
                 logger.warning("formation $id a des mots clefs trop longs $motsClefsLongs")
             }
             entity.motsClefs = motsClefsCourts


             entity.labelDetails = debugLabels.getOrDefault(id, id)
             entity.capacite = capacitesAccueil.getOrDefault(id, 0)
             entity.apprentissage = apprentissage.contains(id)
             entity.apprentissagePct = apprentissagePct.getOrDefault(id,0)


             val statsFormation = stats[id]
             if (statsFormation != null) {
                 entity.stats = FormationEntity.StatsEntity(statsFormation)
             } else {
                 logger.info("formation $id n'a pas de stats")
                 entity.stats = FormationEntity.StatsEntity()
             }

             entity.compatibleEtudeCourtes = etudesCourtes.contains(id)
             entity.compatibleEtudeLongues = etudesLongues.contains(id)

             formationEntities.add(entity)
         }

         logger.warning("Insertion et mise à jour de ${formationEntities.count()} formations")
         batchUpdate.upsertEntities(formationEntities)
     }


    fun isTestSuggestionsProfileActive(): Boolean {
        return environment.activeProfiles.contains("test")
                || environment.activeProfiles.contains("test_suggestions")
    }

    fun isMinimalTestDatasetModeActive(): Boolean {
        return minimalTestDataSet
    }

    fun updateVillesVoeuxDb() {

        sessionFactory.openStatelessSession().use { statelessSession ->
            val transaction: Transaction = statelessSession.beginTransaction()

            val onlyParis20 = isTestSuggestionsProfileActive()

            val cities = mpsDataPort.getCities()
                .sortedBy { it.nom }
                .filter { !onlyParis20 || it.codeInsee == Constants.CODE_COMMUNE_INSEE_PARIS_VINGTIEME }
                .associateBy { it.codeInsee }
                .values

            val voeux = mpsDataPort.getVoeux().flatMap { it.value }.toList()

            var letter = '_'
            cities.forEach { city ->
                val newLetter = city.nom.first()
                if (newLetter != letter) {
                    logger.info("Calcul des distances pour les villes commençant par $newLetter")
                    letter = newLetter
                }

                val distances = voeux
                    .associate { voeu ->
                        voeu.id to geodeticDistance(voeu.coords(), city.coords)
                    }
                    .filter { it.value <= Constants.MAX_DISTANCE_VILLE_VOEU_KM}
                val newEntity = VilleVoeuxEntity().apply {
                    idVille = city.codeInsee
                    distancesVoeuxKm = distances
                }
                statelessSession.upsert(newEntity)
            }

            transaction.commit()
        }
    }

    /**
     * @return distance en km
     */
    private fun geodeticDistance(coord: LatLng?, coords: List<LatLng>): Int {
        if(coord == null) {
            return Int.MAX_VALUE
        }
        return coords.minOfOrNull { coord2 ->
            (GeodeticDistance.geodeticDistance(
                coord.lat,
                coord.lng,
                coord2.lat,
                coord2.lng
            ) / 1000.0).roundToInt()
        } ?: Int.MAX_VALUE
    }


    private fun updateCriteresDb() {
        val criteres = ArrayList<CritereAnalyseCandidatureEntity>()
        var i = 0
        GrilleAnalyse.labelsFront.forEach { triple ->
            val entity = CritereAnalyseCandidatureEntity()
            entity.id = triple.left
            entity.index = i++
            entity.nom = triple.right
            criteres.add(entity)
        }
        criteresDb.deleteAll()
        criteresDb.saveAll(criteres)
    }

}