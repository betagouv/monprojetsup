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
    private val baccalaureatBd: BaccalaureatDb,
    private val specialitesDb: SpecialitesDb,
    private val specialitesBacDb: BaccalaureatSpecialiteDb,
    private val domainesIdeoDb: DomainesIdeoDb,
    private val domainesDb: DomainesDb,
    private val domainesCategoriesDb: DomainesCategoryDb,
    private val interetsDb: InteretsDb,
    private val interetsCategorieDb: InteretsCategoryDb,
    private val interetsSousCategorieDb: InteretsSousCategorieDb,
    private val mpsDataPort: MpsDataPort
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
        //delete table already using bacs foreign keys
        //baccalaureatBd.deleteAll() bot doing that becaus eit would break a constraint on profil_eleve
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
        interetsSousCategorieDb.deleteAll()
        interetsCategorieDb.deleteAll()

        interets.categories.forEach { categorie ->
            val entity = InteretCategorieEntity()
            val groupeInteretId = categorie.id
            entity.id = groupeInteretId
            entity.nom = categorie.label
            entity.emoji = categorie.emoji
            interetsCategorieDb.save(entity)
            categorie.elements.forEach { element ->
                val sousCategorie = InteretSousCategorieEntity()
                sousCategorie.id = element.id
                sousCategorie.nom = element.label
                sousCategorie.emoji = element.emoji
                sousCategorie.idCategorie = groupeInteretId
                interetsSousCategorieDb.save(sousCategorie)
                element.atomes.forEach { (key,label) ->
                    val interet = InteretEntity()
                    interet.id = key
                    interet.nom = label
                    interet.idSousCategorie = sousCategorie.id
                    interetsDb.save(interet)
                }
            }
        }
    }

    private fun updateDomainesDbs() {

        domainesIdeoDb.deleteAll()
        domainesDb.deleteAll()
        domainesCategoriesDb.deleteAll()

        val domaines = mpsDataPort.getDomaines()
        domaines.categories.forEach { categorie ->
            val entity = DomainesCategorieEntity()
            val groupeId = categorie.id
            entity.id = groupeId
            entity.nom = categorie.label
            entity.emoji = categorie.emoji
            domainesCategoriesDb.save(entity)
            categorie.elements.forEach { element ->
                val domaine = DomaineEntity()
                domaine.id = element.id
                domaine.nom = element.label
                domaine.emoji = element.emoji
                domaine.idCategorie = groupeId
                domainesDb.save(domaine)
                element.atomes.forEach { (key,label) ->
                    val domaineIdeo = DomaineIdeoEntity()
                    domaineIdeo.id = key
                    domaineIdeo.nom = label
                    domaineIdeo.idDomaineMps = domaine.id
                    domainesIdeoDb.save(domaineIdeo)
                }
            }
        }
    }

    fun clearAll() {

        //in this order to avoid foreign key constraint errors

        specialitesBacDb.deleteAll()
        specialitesDb.deleteAll()
        //baccalaureatBd.deleteAll()

        domainesDb.deleteAll()
        domainesCategoriesDb.deleteAll()

        interetsDb.deleteAll()
        interetsSousCategorieDb.deleteAll()
        interetsCategorieDb.deleteAll()
    }


}