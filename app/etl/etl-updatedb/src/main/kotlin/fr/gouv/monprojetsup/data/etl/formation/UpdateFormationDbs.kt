package fr.gouv.monprojetsup.data.etl.formation

import fr.gouv.monprojetsup.data.commun.entity.LienEntity
import fr.gouv.monprojetsup.data.domain.model.attendus.GrilleAnalyse
import fr.gouv.monprojetsup.data.etl.MpsDataPort
import fr.gouv.monprojetsup.data.formation.entity.CritereAnalyseCandidatureEntity
import fr.gouv.monprojetsup.data.formation.entity.FormationEntity
import fr.gouv.monprojetsup.data.formation.entity.MoyenneGeneraleAdmisEntity
import fr.gouv.monprojetsup.data.formation.entity.VoeuEntity
import fr.gouv.monprojetsup.data.formationmetier.entity.FormationMetierEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import java.util.logging.Logger

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

@Component
class UpdateFormationDbs(
    private val criteresDb: CriteresDb,
    private val formationsdb: FormationDb,
    private val joinFormationsMetiersdb: JoinFormationMetierDb,
    private val moyennesGeneralesAdmisDb: MoyennesGeneralesAdmisDb,
    private val voeuxDb: VoeuxDb,
    private val mpsDataPort: MpsDataPort
) {

    private val logger: Logger = Logger.getLogger(UpdateFormationDbs::class.java.simpleName)

    internal fun updateFormationDbs() {
        logger.info("Mise à jour de formations and voeux dbs")
        updateFormationsAndVoeuxDb()
        logger.info("Mise à jour de criteres dbs")
        updateCriteresDb()
        logger.info("Mise à jour de moyennes generales admis dbs")
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


    private fun updateFormationsAndVoeuxDb() {

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
            entity.descriptifGeneral = descriptifs.getDescriptifGeneralFront(id)
            entity.descriptifDiplome = descriptifs.getDescriptifDiplomeFront(id)
            entity.descriptifAttendus = attendus[id]
            entity.descriptifConseils = conseils[id]

            entity.formationsAssociees = mpsKeyToPsupKeys.getOrDefault(id, setOf(id)).toList()
            entity.formationsIdeo = mpsKeyToIdeoKeys[id].orEmpty()

            val grille = grilles[id]
            if (grille == null) {
                entity.criteresAnalyse = listOf()
            } else {
                entity.criteresAnalyse = grille.criteresFront
            }

            val urlListe = liens.getOrDefault(id, ArrayList())
            entity.liens = urlListe.map { link ->
                LienEntity(link.label, link.uri)
            }.toCollection(ArrayList())

            entity.motsClefs = tagsSources.getOrDefault(id, listOf(label))

            val voeuxFormation = voeux.filter { it.formation == id }

            entity.labelDetails = debugLabels.getOrDefault(id, id)
            entity.capacite = capacitesAccueil.getOrDefault(id, 0)
            entity.apprentissage = apprentissage.contains(id)
            entity.las = lasToGeneric[id]
            entity.voeux = voeuxFormation.map { VoeuEntity(it) }.toList()
            entity.voeuxIds = voeuxFormation.map { it.id }

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