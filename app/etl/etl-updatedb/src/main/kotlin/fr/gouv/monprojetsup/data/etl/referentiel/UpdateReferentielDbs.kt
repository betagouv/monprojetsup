package fr.gouv.monprojetsup.data.etl.referentiel

import fr.gouv.monprojetsup.data.etl.BatchUpdate
import fr.gouv.monprojetsup.data.etl.MpsDataPort
import fr.gouv.monprojetsup.data.referentiel.entity.BaccalaureatEntity
import fr.gouv.monprojetsup.data.referentiel.entity.BaccalaureatSpecialiteEntity
import fr.gouv.monprojetsup.data.referentiel.entity.BaccalaureatSpecialiteId
import fr.gouv.monprojetsup.data.referentiel.entity.DomaineEntity
import fr.gouv.monprojetsup.data.referentiel.entity.DomaineIdeoEntity
import fr.gouv.monprojetsup.data.referentiel.entity.DomainesCategorieEntity
import fr.gouv.monprojetsup.data.referentiel.entity.InteretCategorieEntity
import fr.gouv.monprojetsup.data.referentiel.entity.InteretEntity
import fr.gouv.monprojetsup.data.referentiel.entity.InteretSousCategorieEntity
import fr.gouv.monprojetsup.data.referentiel.entity.SpecialiteEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import java.util.logging.Logger

@Repository
interface BaccalaureatDb :
    JpaRepository<BaccalaureatEntity, String>

@Repository
interface SpecialitesDb :
    JpaRepository<SpecialiteEntity, String>

@Repository
interface BaccalaureatSpecialiteDb :
    JpaRepository<BaccalaureatSpecialiteEntity, String>

@Repository
interface CategorieDomaineDb :
    JpaRepository<DomainesCategorieEntity, String>

@Repository
interface DomainesDb :
    JpaRepository<DomaineEntity, String>

@Repository
interface DomainesIdeoDb :
    JpaRepository<DomaineIdeoEntity, String>

@Repository
interface DomainesCategoryDb :
    JpaRepository<DomainesCategorieEntity, String>

@Repository
interface InteretsDb :
    JpaRepository<InteretEntity, String>

@Repository
interface InteretsCategoryDb :
    JpaRepository<InteretCategorieEntity, String>

@Repository
interface InteretsSousCategorieDb :
    JpaRepository<InteretSousCategorieEntity, String>

@Component
class UpdateReferentielDbs(
    private val mpsDataPort: MpsDataPort,
    private val batchUpdate: BatchUpdate
) {

    private val logger: Logger = Logger.getLogger(UpdateReferentielDbs::class.java.simpleName)

    fun updateReferentielDbs() {
        logger.info("Mise à jour de bacs db")
        updateBaccalaureatDb()
        logger.info("Mise à jour de specialites db")
        updateSpecialiteDb()
        logger.info("Mise à jour de bacs specialites db")
        updateBaccalaureatSpecialiteDb()
        logger.info("Mise à jour de secteursActivite db")
        updateDomainesDbs()
        logger.info("Mise à jour de interets db")
        updateInteretDbs()
    }

    internal fun updateBaccalaureatDb() {
        val bacs = mpsDataPort.getBacs()
        batchUpdate.upsertEntities(
            bacs.map { baccalaureat ->
                BaccalaureatEntity().apply {
                    id = baccalaureat.key
                    nom = baccalaureat.label
                    idExterne = baccalaureat.key
                    idCarteParcoursup = baccalaureat.idCarteParcoursup()
                }
            })
    }

    private fun updateSpecialiteDb() {
        val specialites = mpsDataPort.getSpecialites().toSpecialites()
        batchUpdate.upsertEntities(
            specialites.map { matiere ->
                SpecialiteEntity().apply {
                    id = matiere.idMps
                    label = matiere.label
                }
            }
        )
    }

    private fun updateBaccalaureatSpecialiteDb() {
        val specialites = mpsDataPort.getSpecialites()
        val bacsKeys = mpsDataPort.getBacs().map { it.key }.toSet()
        val entities = specialites.specialitesParBac
            .filter { s -> bacsKeys.contains(s.key) }
            .flatMap {
                it.value.map { bacSpec ->
                    BaccalaureatSpecialiteEntity().apply {
                        id = BaccalaureatSpecialiteId(
                            it.key,
                            bacSpec
                        )
                        idSpecialite = it.key
                        idBaccalaureat = bacSpec
                    }
                }
            }
        batchUpdate.setEntities(BaccalaureatSpecialiteEntity::class.simpleName!!, entities)
    }

    private fun updateInteretDbs() {
        val interets = mpsDataPort.getInterets()

        val listInteretsCategorie = ArrayList<InteretCategorieEntity>()
        val listInteretsSousCategorie = ArrayList<InteretSousCategorieEntity>()
        val listInterets = ArrayList<InteretEntity>()

        interets.categories.forEach { categorie ->
            val entity = InteretCategorieEntity()
            val groupeInteretId = categorie.id
            entity.id = groupeInteretId
            entity.nom = categorie.label
            entity.emoji = categorie.emoji
            listInteretsCategorie.add(entity)
            categorie.elements.forEach { element ->
                val sousCategorie = InteretSousCategorieEntity()
                sousCategorie.id = element.id
                sousCategorie.nom = element.label
                sousCategorie.emoji = element.emoji
                sousCategorie.idCategorie = groupeInteretId
                listInteretsSousCategorie.add(sousCategorie)
                element.atomes.forEach { (key,label) ->
                    val interet = InteretEntity()
                    interet.id = key
                    interet.nom = label
                    interet.idSousCategorie = sousCategorie.id
                    listInterets.add(interet)
                }
            }
        }

        batchUpdate.upsertEntities(listInteretsCategorie)
        batchUpdate.upsertEntities(listInteretsSousCategorie)
        batchUpdate.upsertEntities(listInterets)

    }

    private fun updateDomainesDbs() {

        val listDomaineCategorie = ArrayList<DomainesCategorieEntity>()
        val listDomaineIdeo = ArrayList<DomaineIdeoEntity>()
        val listDomaine = ArrayList<DomaineEntity>()

        val domaines = mpsDataPort.getDomaines()
        domaines.categories.forEach { categorie ->
            val entity = DomainesCategorieEntity()
            val groupeId = categorie.id
            entity.id = groupeId
            entity.nom = categorie.label
            entity.emoji = categorie.emoji
            listDomaineCategorie.add(entity)
            categorie.elements.forEach { element ->
                val domaine = DomaineEntity().apply {
                    this.id = element.id
                    this.nom = element.label
                    this.emoji = element.emoji
                    this.description = element.description
                    this.idCategorie = groupeId
                }
                listDomaine.add(domaine)
                element.atomes.forEach { (key,label) ->
                    val domaineIdeo = DomaineIdeoEntity()
                    domaineIdeo.id = key
                    domaineIdeo.nom = label
                    domaineIdeo.idDomaineMps = domaine.id
                    listDomaineIdeo.add(domaineIdeo)
                }
            }
        }

        batchUpdate.upsertEntities(listDomaineCategorie)
        batchUpdate.upsertEntities(listDomaineIdeo)
        batchUpdate.upsertEntities(listDomaine)

    }


}