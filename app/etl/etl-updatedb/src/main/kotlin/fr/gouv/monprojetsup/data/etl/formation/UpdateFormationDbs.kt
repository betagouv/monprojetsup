package fr.gouv.monprojetsup.data.etl.formation

import fr.gouv.monprojetsup.data.commun.entity.LienEntity
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
    private val formationsdb: FormationDb,
    private val joinFormationsMetiersdb: JoinFormationMetierDb,
    private val moyennesGeneralesAdmisDb: MoyennesGeneralesAdmisDb,
    private val voeuxDb: VoeuxDb,
    private val villesVoeuxDb: VillesVoeuxDb,
    private val mpsDataPort: MpsDataPort
) {

    private val logger: Logger = Logger.getLogger(UpdateFormationDbs::class.java.simpleName)

    internal fun update() {
        logger.info("Mise à jour de la table des formations et de la table des voeux")
        updateFormationsAndVoeuxDb()
        logger.info("Mise à jour de la table de correspondance ville formations")
        updateVillesAndFormationsDb()
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


     fun updateFormationsAndVoeuxDb() {

        val labels = mpsDataPort.getFormationsLabels()
        val descriptifs = mpsDataPort.getDescriptifs()
        val attendus = mpsDataPort.getAttendus()
        val conseils = mpsDataPort.getConseils()
        val liens = mpsDataPort.getLiens()
        val grilles = mpsDataPort.getGrilles()
        val tagsSources = mpsDataPort.getMotsClesFormations()
        val formationsMpsIds = mpsDataPort.getFormationsMpsIds()
        val apprentissage = mpsDataPort.getApprentissage()
        val lasToGeneric = mpsDataPort.getLasToGenericIdMapping()
        val voeux = mpsDataPort.getVoeux()
        val debugLabels = mpsDataPort.getDebugLabels()
        val capacitesAccueil = mpsDataPort.getCapacitesAccueil()
        val stats = mpsDataPort.getStatsFormation()
        val durees = mpsDataPort.getDurees()
        val mpsKeyToPsupKeys = mpsDataPort.getMpsIdToPsupFlIds()
        val mpsKeyToIdeoKeys = mpsDataPort.getMpsIdToIdeoIds()

        val entities = ArrayList<FormationEntity>()

        formationsMpsIds.forEach { id ->
            val entity = FormationEntity()
            entity.id = id
            val label = labels[id] ?: throw RuntimeException("Pas de label pour la formation $id")
            entity.label = label
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
            if(motsClefsCourts.size != motsClefs.size) {
                logger.warning("formation $id a des mots clefs trop longs $motsClefsLongs")
            }
            entity.motsClefs = motsClefsCourts

            val voeuxFormation = voeux.getOrDefault(id, listOf()).sortedBy { it.libelle }

            entity.labelDetails = debugLabels.getOrDefault(id, id)
            entity.capacite = capacitesAccueil.getOrDefault(id, 0)
            entity.apprentissage = apprentissage.contains(id)
            entity.las = lasToGeneric[id]

            entity.voeux = voeuxFormation.map { VoeuEntity(it) }

            val statsFormation = stats[id]
            if(statsFormation != null) {
                entity.stats = FormationEntity.StatsEntity(statsFormation)
            } else {
                logger.info("formation $id n'a pas de stats")
                entity.stats = FormationEntity.StatsEntity()
            }

            val duree = durees[id]
            if(duree !=  null) {
                entity.duree = duree
            } else {
                logger.info("formation $id n'a pas de duree")
                entity.duree = 3
            }
            entities.add(entity)
        }
        joinFormationsMetiersdb.deleteAll()

        formationsdb.deleteAll()
        formationsdb.saveAll(entities)
    }

    private fun updateVillesAndFormationsDb() {
        val cities = mpsDataPort.getCities().sortedBy { it.nom }

        val voeux = mpsDataPort.getVoeux().flatMap { it.value }.toList()

        val entities = ArrayList<VilleVoeuxEntity>()
        var letter = '_'
        cities.forEach { city ->
            val newLetter = city.nom.first()
            if(newLetter != letter) {
                logger.info("traitement des villes commençant par ${newLetter}")
                letter = newLetter
            }
            val distances = voeux.map { voeu ->
                Pair(voeu.id, geodeticDistance(voeu.coords(), city.coords))
            }
            val voeuxVille10km = distances.filter {  it.second <= 10 }.map { it.first }.toList()
            val voeuxVille30km = distances.filter { it.second <= 30 }.map { it.first }.toList()
            entities.add(VilleVoeuxEntity().apply {
                idVille = city.nom
                voeuxMoinsDe10km = voeuxVille10km
                voeuxMoinsDe30km = voeuxVille30km
            })
            entities.add(VilleVoeuxEntity().apply {
                idVille = city.codeInsee
                voeuxMoinsDe10km = voeuxVille10km
                voeuxMoinsDe30km = voeuxVille30km
            })
        }
        villesVoeuxDb.deleteAll()
        villesVoeuxDb.saveAll(entities)
    }

    /**
     * @return distance en km
     */
    private fun geodeticDistance(coord: LatLng?, coords: List<LatLng>): Int {
        if(coord == null) {
            return Int.MAX_VALUE
        }
        return coords.map { coord2 ->
            (GeodeticDistance.geodeticDistance(
            coord.lat,
            coord.lng,
            coord2.lat,
            coord2.lng) / 1000.0).roundToInt()
        }.minOrNull() ?: Int.MAX_VALUE
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

    fun clearAll() {
        moyennesGeneralesAdmisDb.deleteAll()
        voeuxDb.deleteAll()//nécessaire sinon violation de contrainte non null de VoeuEntity.idFormation
        formationsdb.deleteAll()
        criteresDb.deleteAll()
    }


}