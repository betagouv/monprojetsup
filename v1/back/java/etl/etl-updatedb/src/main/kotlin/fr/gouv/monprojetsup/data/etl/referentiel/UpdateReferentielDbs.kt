package fr.gouv.monprojetsup.data.etl.referentiel

import fr.gouv.monprojetsup.data.app.infrastructure.BaccalaureatDb
import fr.gouv.monprojetsup.data.app.infrastructure.DomainesCategoryDb
import fr.gouv.monprojetsup.data.app.infrastructure.DomainesDb
import fr.gouv.monprojetsup.data.app.referentiel.entity.BaccalaureatEntity
import fr.gouv.monprojetsup.data.app.referentiel.entity.CategorieDomaineEntity
import fr.gouv.monprojetsup.data.app.referentiel.entity.DomaineEntity
import fr.gouv.monprojetsup.suggestions.infrastructure.DataSources
import fr.gouv.monprojetsup.suggestions.infrastructure.model.bacs.BacsLoader
import fr.gouv.monprojetsup.suggestions.infrastructure.model.thematiques.Thematiques
import fr.gouv.monprojetsup.suggestions.tools.csv.CsvTools
import org.springframework.stereotype.Component

@Component
class UpdateReferentielDbs(
    private val baccalaureatBDD: BaccalaureatDb,
    private val domainesDb: DomainesDb,
    private val domainesCategoriesDb: DomainesCategoryDb,
    private val dataSources: DataSources
) {
    fun updateReferentielDbs() {
        updateBaccalaureatDb()
        updateBaccalaureatSpecialiteDb()
        updateCategorieDomaineDb()
        updateDomainesDb()
        updateInteretCategorieDb()
        updateInteretDb()
        updateInteretSousCategorieDb()
        updateSpecialiteDb()
    }

    private fun updateSpecialiteDb() {
        TODO("Not yet implemented")
    }

    private fun updateInteretSousCategorieDb() {
        TODO("Not yet implemented")
    }

    private fun updateInteretDb() {
        TODO("Not yet implemented")
    }

    private fun updateInteretCategorieDb() {
        TODO("Not yet implemented")
    }

    private fun updateCategorieDomaineDb() {
        TODO("Not yet implemented")
    }

    private fun updateBaccalaureatSpecialiteDb() {
        TODO("Not yet implemented")
    }

    private fun updateDomainesDb() {

        domainesDb.deleteAll()
        domainesCategoriesDb.deleteAll()

        val categories = loadThematiques()
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

    fun loadThematiques(): List<Thematiques.Category> {
        val groupes: MutableMap<String, Thematiques.Category> = HashMap()
        val categories: MutableList<Thematiques.Category> = ArrayList()

        var groupe = ""
        var emojig: String? = ""
        for (stringStringMap in CsvTools.readCSV(
            dataSources.getSourceDataFilePath(DataSources.THEMATIQUES_REGROUPEMENTS_PATH),
            '\t'
        )) {
            val id = stringStringMap["id"].orEmpty()
            if(id.isEmpty()) continue
            val regroupement = stringStringMap["regroupement"].orEmpty().trim { it <= ' ' }
            if (regroupement.isNotEmpty()) {
                groupe = regroupement
                val emojiGroupe = stringStringMap["Emoji"].orEmpty()
                if (emojiGroupe.isNotEmpty()) {
                    emojig = emojiGroupe
                } else {
                    throw java.lang.RuntimeException("Groupe " + groupe + " sans emoji dans " + DataSources.THEMATIQUES_REGROUPEMENTS_PATH)
                }
            }
            val emoji = stringStringMap.getOrDefault("Emojis", "").trim { it <= ' ' }
            val label = stringStringMap.getOrDefault("label", "").trim { it <= ' ' }
            if (groupe.isEmpty()) throw java.lang.RuntimeException("Groupe vide dans " + DataSources.THEMATIQUES_REGROUPEMENTS_PATH)
            if (emojig.orEmpty().isEmpty()) throw java.lang.RuntimeException("Groupe sans emoji dans " + DataSources.THEMATIQUES_REGROUPEMENTS_PATH)
            var cat = groupes[groupe]
            if (cat == null) {
                cat = Thematiques.Category(groupe, emojig, ArrayList())
                groupes[groupe] = cat
                categories.add(cat)
            }
            cat.items.add(Thematiques.Item(id, label, emoji))
        }
        return categories
    }

    private fun updateBaccalaureatDb() {

        baccalaureatBDD.deleteAll()
        val bacs = BacsLoader.load(dataSources)
        bacs.forEach { baccalaureat ->
            val entity = BaccalaureatEntity()
            entity.id = baccalaureat.key
            entity.nom = baccalaureat.label
            entity.idExterne = baccalaureat.key
            baccalaureatBDD.save(entity)
        }

    }


}