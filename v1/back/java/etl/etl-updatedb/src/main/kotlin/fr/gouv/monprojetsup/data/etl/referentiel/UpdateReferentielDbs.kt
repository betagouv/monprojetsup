package fr.gouv.monprojetsup.data.etl.referentiel

import fr.gouv.monprojetsup.data.MpsDataPort
import fr.gouv.monprojetsup.data.app.infrastructure.*
import fr.gouv.monprojetsup.data.app.referentiel.entity.*
import org.springframework.stereotype.Component

@Component
class UpdateReferentielDbs(
    private val baccalaureatBd: BaccalaureatDb,
    private val specialitesDb: SpecialitesDb,
    private val bacSpecDb: BaccalaureatSpecialiteDb,
    private val domainesDb: DomainesDb,
    private val domainesCategoriesDb: DomainesCategoryDb,
    private val interetsDb: InteretsDb,
    private val interetsCategorieDb: InteretsCategoryDb,
    private val interetsSousCategorieDb: InteretsSousCategorieDb,
    private val mpsDataPort: MpsDataPort
) {
    fun updateReferentielDbs() {
        updateBaccalaureatDb()
        updateSpecialiteDb()
        updateBaccalaureatSpecialiteDb()
        updateDomainesDbs()
        updateInteretDbs()
    }

    private fun updateBaccalaureatDb() {
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
        val specialites = mpsDataPort.getSpecialites()
        specialitesDb.deleteAll()
        specialites.specialites.forEach { specialite ->
            val entity = SpecialiteEntity()
            entity.id = specialite.key.toString()
            entity.label = specialite.value
            specialitesDb.save(entity)
        }
    }

    private fun updateBaccalaureatSpecialiteDb() {
        val specialites = mpsDataPort.getSpecialites()
        bacSpecDb.deleteAll()
        specialites.specialitesParBac.forEach {
            it.value.forEach { bacSpec ->
                val entity = BaccalaureatSpecialiteEntity()
                entity.idBaccalaureat = it.key
                entity.idSpecialite = bacSpec.toString()
                bacSpecDb.save(entity)
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





}