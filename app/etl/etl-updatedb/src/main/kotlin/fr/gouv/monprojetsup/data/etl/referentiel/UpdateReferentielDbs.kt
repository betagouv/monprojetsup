package fr.gouv.monprojetsup.data.etl.referentiel

import fr.gouv.monprojetsup.data.etl.MpsDataPort
import fr.gouv.monprojetsup.data.referentiel.entity.*
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
    JpaRepository<CategorieDomaineEntity, String>

@Repository
interface DomainesDb :
    JpaRepository<DomaineEntity, String>

@Repository
interface DomainesCategoryDb :
    JpaRepository<CategorieDomaineEntity, String>

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
    private val baccalaureatBd: BaccalaureatDb,
    private val specialitesDb: SpecialitesDb,
    private val specialitesBacDb: BaccalaureatSpecialiteDb,
    private val domainesDb: DomainesDb,
    private val domainesCategoriesDb: DomainesCategoryDb,
    private val interetsDb: InteretsDb,
    private val interetsCategorieDb: InteretsCategoryDb,
    private val interetsSousCategorieDb: InteretsSousCategorieDb,
    private val mpsDataPort: MpsDataPort
) {

    private val logger: Logger = Logger.getLogger(UpdateReferentielDbs::class.java.simpleName)

    fun updateReferentielDbs() {
        logger.info("Updating bacs db")
        updateBaccalaureatDb()
        logger.info("Updating specialites db")
        updateSpecialiteDb()
        logger.info("Updating bacs specialites db")
        updateBaccalaureatSpecialiteDb()
        logger.info("Updating domaines db")
        updateDomainesDbs()
        logger.info("Updating interets db")
        updateInteretDbs()
    }

    private fun updateBaccalaureatDb() {
        //delete table already using bacs foreign keys
        baccalaureatBd.deleteAll()
        val bacs = mpsDataPort.getBacs()
        bacs.forEach { baccalaureat ->
            val entity = BaccalaureatEntity()
            entity.id = baccalaureat.key
            entity.nom = baccalaureat.label
            entity.idExterne = baccalaureat.key
            baccalaureatBd.save(entity)
        }
    }

    private fun updateSpecialiteDb() {
        val specialites = mpsDataPort.getSpecialites().toMatieres()
        specialitesDb.deleteAll()
        specialites.forEach { matiere ->
            val entity = SpecialiteEntity()
            entity.id = matiere.idMps
            entity.label = matiere.label
            specialitesDb.save(entity)
        }
    }

    private fun updateBaccalaureatSpecialiteDb() {
        val specialites = mpsDataPort.getSpecialites()
        val bacsKeys = mpsDataPort.getBacs().map { b -> b.key}.toSet()
        specialitesBacDb.deleteAll()
        specialites.specialitesParBac
            .filter { s -> bacsKeys.contains(s.key) }
            .forEach {
            it.value.forEach { bacSpec ->
                val entity = BaccalaureatSpecialiteEntity()
                entity.id = BaccalaureatSpecialiteId(
                    it.key,
                    bacSpec
                )
                specialitesBacDb.save(entity)
            }
        }
    }

    private fun updateInteretDbs() {
        val interets = mpsDataPort.getInterets()
        interetsDb.deleteAll()
        interets.groupeInterets.forEach { groupeInteret ->
            val entity = InteretCategorieEntity()
            val ids = groupeInteret.items.flatMap { item -> item.keys }.joinToString( "_")
            entity.id = ids
            entity.idCategorie = ids
            entity.nom = groupeInteret.label
            entity.emoji = groupeInteret.emoji
            interetsCategorieDb.save(entity)
            groupeInteret.items.forEach { item ->
                val sousCategorie = InteretSousCategorieEntity()
                sousCategorie.id = item.keys.joinToString("_")
                sousCategorie.nom = item.label
                sousCategorie.emoji = item.emoji
                sousCategorie.idCategorie = ids
                interetsSousCategorieDb.save(sousCategorie)
                item.keys.forEach { key ->
                    val interet = InteretEntity()
                    interet.id = key
                    interet.nom = key
                    interet.idSousCategorie = sousCategorie.id
                    interetsDb.save(interet)
                }
            }
        }
    }

    private fun updateDomainesDbs() {

        domainesDb.deleteAll()
        domainesCategoriesDb.deleteAll()

        val categories = mpsDataPort.getThematiques()
        var i = 0
        categories.forEach { categorie ->
            val entity = CategorieDomaineEntity()
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

    fun clearAll() {

        //in this order to avoid foreign key constraint errors

        specialitesBacDb.deleteAll()
        specialitesDb.deleteAll()
        baccalaureatBd.deleteAll()

        domainesDb.deleteAll()
        domainesCategoriesDb.deleteAll()

        interetsDb.deleteAll()
        interetsSousCategorieDb.deleteAll()
        interetsCategorieDb.deleteAll()
    }


}