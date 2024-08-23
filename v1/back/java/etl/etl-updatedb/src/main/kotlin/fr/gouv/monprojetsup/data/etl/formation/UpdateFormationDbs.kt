package fr.gouv.monprojetsup.data.etl.formation

import fr.gouv.monprojetsup.data.MpsDataPort
import fr.gouv.monprojetsup.data.app.commun.entity.LienEntity
import fr.gouv.monprojetsup.data.app.formation.entity.CritereAnalyseCandidatureEntity
import fr.gouv.monprojetsup.data.app.formation.entity.FormationEntity
import fr.gouv.monprojetsup.data.app.formation.entity.MoyenneGeneraleAdmisEntity
import fr.gouv.monprojetsup.data.app.infrastructure.CriteresDb
import fr.gouv.monprojetsup.data.app.infrastructure.FormationDb
import fr.gouv.monprojetsup.data.app.infrastructure.MoyennesGeneralesAdmisDb
import fr.gouv.monprojetsup.data.domain.port.VoeuxPort
import fr.gouv.monprojetsup.data.infrastructure.model.attendus.GrilleAnalyse
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
class UpdateFormationDbs(
    private val criteresDb: CriteresDb,
    private val formationsdb : FormationDb,
    private val moyennesGeneralesAdmisDb: MoyennesGeneralesAdmisDb,
    private val mpsDataPort : MpsDataPort,
    private val voeuxPort: VoeuxPort
    ) {

    private val logger: Logger = Logger.getLogger(UpdateFormationDbs::class.java.simpleName)

    internal fun updateFormationDbs() {
        updateFormationsDb()
        updateCriteresDb()
        updateMoyennesGeneralesAdmisDb()
        updateVoeuxDb()
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

    private fun updateVoeuxDb() {
        val voeux = mpsDataPort.getVoeux()
        voeuxPort.saveAll(voeux)
    }


    private fun updateFormationsDb() {

        val labels = mpsDataPort.getLabels()
        val descriptifs = mpsDataPort.getDescriptifs()
        val attendus = mpsDataPort.getAttendus()
        val liens = mpsDataPort.getLiens()
        val grilles = mpsDataPort.getGrilles()
        val tagsSources = mpsDataPort.getTagsSources()
        val formationsMpsIds = mpsDataPort.getFormationsMpsIds()
        val apprentissage = mpsDataPort.getApprentissage()
        val lasToGeneric = mpsDataPort.getLasToGenericIdMapping()
        val voeux = mpsDataPort.getVoeux()
        val debugLabels = mpsDataPort.getDebugLabels()
        val capacitesAccueil = mpsDataPort.getCapacitesAccueil()
        val formationsVersMetiers = mpsDataPort.getFormationsVersMetiers()
        val stats = mpsDataPort.getStatsFormation()
        val durees = mpsDataPort.getDurees()
        val mpsKeyToPsupKeys = mpsDataPort.getMpsIdToPsupFlIds()

        val entities = ArrayList<FormationEntity>()
        formationsMpsIds.forEach { id ->
            val entity = FormationEntity()
            entity.id = id
            val label = labels[id] ?: throw RuntimeException("Pas de label pour la formation $id")
            entity.label = label
            entity.descriptifGeneral = descriptifs.getDescriptifGeneralFront(id)
            entity.descriptifDiplome = descriptifs.getDescriptifDiplomeFront(id)
            val attendusFormation = attendus[id]
            if (attendusFormation == null) {
                entity.descriptifConseils = null
                entity.descriptifAttendus = null
            } else {
                entity.descriptifConseils = attendusFormation.getConseilsFront()
                entity.descriptifAttendus = attendusFormation.getAttendusFront()
            }

            entity.formationsAssociees = ArrayList(mpsKeyToPsupKeys.getOrDefault(id, setOf(id)))

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

            val motsClefs = tagsSources[id]
            if (motsClefs == null) {
                entity.motsClefs = listOf()
            } else {
                entity.motsClefs = motsClefs
            }

            val voeuxFormation = voeux.filter { it.formation == id }
            val metiers = formationsVersMetiers[id] ?: java.util.HashSet()

            entity.labelDetails = debugLabels.getOrDefault(id, id)
            entity.capacite = capacitesAccueil.getOrDefault(id, 0)
            entity.apprentissage = apprentissage.contains(id)
            entity.las = lasToGeneric[id]
            entity.voeuxIds = voeuxFormation.map { it.id }.toList()
            entity.metiers = metiers.toList()

            val statsFormation = stats[id]
            if(statsFormation != null) {
                entity.stats = FormationEntity.StatsEntity(statsFormation)
            } else {
                logger.info("formation $id n'a pas de stats")
                entity.stats = FormationEntity.StatsEntity()
            }
            entity.psupFlIds = mpsKeyToPsupKeys.getOrDefault(id, setOf()).toList()

            val duree = durees[id]
            if(duree !=  null) {
                entity.duree = duree
            } else {
                logger.info("formation $id n'a pas de duree")
                entity.duree = 3
            }

            entities.add(entity)

        }
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


}