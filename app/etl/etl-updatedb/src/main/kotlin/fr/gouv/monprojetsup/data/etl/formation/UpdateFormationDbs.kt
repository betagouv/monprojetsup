package fr.gouv.monprojetsup.data.etl.formation

import fr.gouv.monprojetsup.data.Constants
import fr.gouv.monprojetsup.data.commun.entity.LienEntity
import fr.gouv.monprojetsup.data.etl.BatchUpdate
import fr.gouv.monprojetsup.data.etl.MpsDataPort
import fr.gouv.monprojetsup.data.formation.entity.CritereAnalyseCandidatureEntity
import fr.gouv.monprojetsup.data.formation.entity.FormationEntity
import fr.gouv.monprojetsup.data.formation.entity.MoyenneGeneraleAdmisEntity
import fr.gouv.monprojetsup.data.formation.entity.VilleVoeuxEntity
import fr.gouv.monprojetsup.data.formation.entity.VoeuEntity
import fr.gouv.monprojetsup.data.formationmetier.entity.FormationMetierEntity
import fr.gouv.monprojetsup.data.model.LatLng
import fr.gouv.monprojetsup.data.model.attendus.GrilleAnalyse
import fr.gouv.monprojetsup.data.tools.GeodeticDistance
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
interface JoinFormationMetierDb : JpaRepository<FormationMetierEntity, String>

@Repository
interface VillesVoeuxDb : JpaRepository<VilleVoeuxEntity, String>

@Component
class UpdateFormationDbs(
    private val criteresDb: CriteresDb,
    private val moyennesGeneralesAdmisDb: MoyennesGeneralesAdmisDb,
    private val mpsDataPort: MpsDataPort,
    private val batchUpdate: BatchUpdate,
    private val villesVoeuxDb: VillesVoeuxDb,
    private val voeuxDb: VoeuxDb,
    private val formationDb: FormationDb
) {

    private val logger: Logger = Logger.getLogger(UpdateFormationDbs::class.java.simpleName)

    internal fun update() {
        val voeuxOntChange = checkVoeuxOuFormationsOntChange()
        if(voeuxOntChange) {
            logger.info("Mise à jour de la table des formations")
            updateFormationsDb()
            logger.info("Mise à jour de la table des voeux")
            updateVoeuxDb()
            logger.info("Mise à jour de la table de correspondance ville voeux")
            updateVillesVoeuxDb()
        } else {
            logger.info("Mise à jour de la table des formations")
            updateFormationsDb()
        }
        logger.info("Mise à jour de la table des critères d'admission")
        updateCriteresDb()
        logger.info("Mise à jour de la table des moyennes générales des admis")
        updateMoyennesGeneralesAdmisDb()
    }

    public fun checkVoeuxOuFormationsOntChange(): Boolean {
        val anciensVoeux = voeuxDb.findAll()
            .filter { it.url.isNotEmpty() }//force la mise à jour lors de la migration V1_35
            .map { Pair(it.idFormation, it.id) }.toSet()
        val formationsMps = mpsDataPort.getFormationsMpsIds()
        val nouveauxVoeux = mpsDataPort.getVoeux()
            .filter { e -> formationsMps.contains(e.key)}
            .flatMap { e -> e.value.map { Pair(it.formation, it.id) } }
            .toSet()
        return anciensVoeux != nouveauxVoeux
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

    fun updateVoeuxDb() {
        val formationsMpsIds = mpsDataPort.getFormationsMpsIds()
        val voeux = mpsDataPort.getVoeux()
        val voeuxEntities = ArrayList<VoeuEntity>()
        formationsMpsIds.forEach { id ->
            val voeuxFormation = voeux.getOrDefault(id, listOf()).sortedBy { it.libelle }
            voeuxEntities.addAll(voeuxFormation.map { VoeuEntity(it) })
        }

        val voeuxIds = voeuxEntities.map { it.id }.toSet()

        val voeuxObsoletes = HashSet(voeuxDb.findAll())
        voeuxObsoletes.removeIf { voeuxIds.contains(it.id) }
        if(voeuxObsoletes.isNotEmpty()) {
            logger.warning("Marquage de ${voeuxObsoletes.count()} voeux obsoletes")
            voeuxObsoletes.forEach { it.obsolete = true }
            batchUpdate.upsertEntities(voeuxObsoletes)
        }

        logger.warning("Insertion et mise à jour de ${voeuxEntities.count()} voeux")
        batchUpdate.upsertEntities(voeuxEntities)

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
         val lasToGeneric = mpsDataPort.getLasToGenericIdMapping()
         val formationToTypeformation = mpsDataPort.getFormationToTypeformation()
         val debugLabels = mpsDataPort.getDebugLabels()
         val capacitesAccueil = mpsDataPort.getCapacitesAccueil()
         val stats = mpsDataPort.getStatsFormation()
         val durees = mpsDataPort.getDurees()
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
                 .sortedBy { it.first }
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
             entity.las = lasToGeneric[id]


             val statsFormation = stats[id]
             if (statsFormation != null) {
                 entity.stats = FormationEntity.StatsEntity(statsFormation)
             } else {
                 logger.info("formation $id n'a pas de stats")
                 entity.stats = FormationEntity.StatsEntity()
             }

             val duree = durees[id]
             if (duree != null) {
                 entity.duree = duree
             } else {
                 logger.info("formation $id n'a pas de duree")
                 entity.duree = 3
             }
             formationEntities.add(entity)
         }

         logger.warning("Insertion et mise à jour de ${formationEntities.count()} formations")
         batchUpdate.upsertEntities(formationEntities)
     }

    fun updateVillesVoeuxDb() {
        val cities = mpsDataPort.getCities()
            .sortedBy { it.nom }
            .associateBy { it.codeInsee }
            .values

        val voeux = mpsDataPort.getVoeux().flatMap { it.value }.toList()

        logger.info("Récupération des paires villes voeux actuelles")
        val villesVoeuxActuels = villesVoeuxDb.findAll().associateBy { v -> v.idVille }

        var letter = '_'

        val entities = ArrayList<VilleVoeuxEntity>()
        cities.forEach { city ->
            val newLetter = city.nom.first()
            if(newLetter != letter) {
                logger.info("Calcul des distances pour les villes commençant par $newLetter")
                letter = newLetter
                if(entities.isNotEmpty()) {
                    logger.info("Enregistrement des correspondances villes-voeux commençant par $letter")
                    batchUpdate.upsertEntities(entities)
                    entities.clear()
                }
            }
            val currentEntity = villesVoeuxActuels[city.codeInsee]

            val voeuxAlreadyKnow = currentEntity?.distancesVoeuxKm?.keys.orEmpty()

            val nouvellesDistances = voeux
                .filter { v -> !voeuxAlreadyKnow.contains(v.id) }
                .map { voeu ->
                voeu.id to geodeticDistance(voeu.coords(), city.coords)
            }
                .filter { it.second <= Constants.MAX_DISTANCE_VILLE_VOEU_KM }
                .toMap()
            if(nouvellesDistances.isNotEmpty()) {
                val distances = HashMap(currentEntity?.distancesVoeuxKm.orEmpty())
                distances.putAll(nouvellesDistances)
                val newEntity = VilleVoeuxEntity().apply {
                    idVille = city.codeInsee
                    distancesVoeuxKm = distances
                }
                entities.add(newEntity)
            }
        }
        logger.info("Sauvegarde des correspondances villes-voeux commençant par $letter")
        batchUpdate.upsertEntities(entities)
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