package fr.gouv.monprojetsup.data.etl

import fr.gouv.monprojetsup.data.etl.formation.FormationDb
import fr.gouv.monprojetsup.data.etl.formation.UpdateFormationDbs
import fr.gouv.monprojetsup.data.etl.metier.UpdateMetierDbs
import fr.gouv.monprojetsup.data.etl.referentiel.BaccalaureatSpecialiteDb
import fr.gouv.monprojetsup.data.etl.referentiel.UpdateReferentielDbs
import fr.gouv.monprojetsup.data.etl.suggestions.UpdateSuggestionsDbs
import fr.gouv.monprojetsup.data.formation.entity.FormationEntity
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired

class UpdateDbsTest : BDDRepositoryTest() {

    @Nested
    inner class UpdateFormationsDbTest {

        @Autowired
        lateinit var formationsdb : FormationDb

        @Autowired
        lateinit var updateFormationDbs: UpdateFormationDbs

        @Autowired
        lateinit var mpsDataPort: MpsDataPort

        @Test
        fun `Doit réussir à mettre à jour et vider les tables`() {
            assertDoesNotThrow { updateFormationDbs.clearAll() }
            assertDoesNotThrow { updateFormationDbs.updateFormationDbs() }
            assertDoesNotThrow { updateFormationDbs.clearAll() }
        }
        @Test
        fun `Doit réussir à sauver une entity formation avec des descriptifs longs`() {
            // When
            val entity = FormationEntity()
            entity.id = "fl640"
            entity.apprentissage = true
            entity.capacite = 1251
            entity.criteresAnalyse = listOf(3)
            entity.descriptifAttendus = null
            entity.descriptifConseils = null
            entity.descriptifDiplome = null
            entity.descriptifGeneral =
                """
            Tu aimes le sport, et tu as envie de transmettre cette passion sans avoir besoin de faire des
            années d’études ? Tu es au bon endroit. Grâce à cette formation,
            tu vas apprendre à devenir éducateur ou moniteur sportif
            (tu as le choix entre le diplôme en animation et celui en sport), et tu auras aussi des notions
            de gestion si un jour tu as envie de participer à l’organisation d’une structure.
            En plus, il s’agit d’une formation en alternance, donc tu vas vite commencer à exercer ton métier.
            Cette formation est disponible dans plus de 40 spécialités dont
            [la liste est disponible ici](https://www.sports.gouv.fr/bpjeps-21).
            Pour y accéder, tu devras avoir un niveau de base dans la spécialité que tu souhaites enseigner,
                mais il ne s''agit pas d''une formation pour devenir sportif de haut niveau !
                Où se former ? Dans des centres spécifiques, comme des fédérations sportives, des organismes privés,
            des établissements nationaux. Tu trouveras les différents centres sur ParcourSup.
            """
            entity.duree = 3
            entity.formationsAssociees = listOf()

            entity.label = "BPJEPS"
            entity.labelDetails =
                """BPJEPS groupe [fl640052, fl640032, fl640010, fl640034, fl640012, fl650036, 
                    fl640035, fl640016, fl640015, fl650032, fl640039, fl650052, fl640021, fl650007, 
                    fl650028, fl650005, fl640022, fl650004, fl650025, fl640002, fl650024, fl650002, 
                    fl640004, fl650022, fl640007, fl640028]
                    """

            // Then
            assertDoesNotThrow { formationsdb.saveAll(listOf(entity)) }
        }

        @Test
        fun `Plus de 95 pour 100 des formations en base doivent réussir le test d'intégrité`() {
            updateFormationDbs.updateFormationDbs()
            val formations = formationsdb.findAll()
            val nbFormations = formations.size
            val nbFormationsIntegres = formations.count { it.integrityCheck() }
            assert( nbFormationsIntegres > 95 * nbFormations / 100)

        }


    @Nested
    inner class UpdateReferentielsTest {

        @Autowired
        lateinit var updateReferentielDbs: UpdateReferentielDbs

        @Autowired
        lateinit var specialitesBacDb: BaccalaureatSpecialiteDb

        @Test
        fun `Doit réussir à mettre à jour les referentiels et vider les tables`() {
            assertDoesNotThrow { updateReferentielDbs.clearAll() }
            assertDoesNotThrow { updateReferentielDbs.updateReferentielDbs() }
            assertDoesNotThrow { updateReferentielDbs.clearAll() }
        }

        @Test
        fun `La table join_baccalaureat_specialite doit être non vide`() {
            updateReferentielDbs.updateReferentielDbs()
            val items = specialitesBacDb.findAll()
            assert(items.isNotEmpty())
        }
    }

    }

    @Nested
    inner class UpdateMetiersTest {

        @Autowired
        lateinit var updateMetierDbs: UpdateMetierDbs

        @Test
        fun `Doit réussir à mettre à jour les referentiels et vider les tables`() {
            assertDoesNotThrow { updateMetierDbs.clearAll() }
            assertDoesNotThrow { updateMetierDbs.updateMetierDbs() }
            assertDoesNotThrow { updateMetierDbs.clearAll() }
        }

    }

    @Nested
    inner class UpdateSuggestionsTest {

        @Autowired
        lateinit var updateSuggestionsDbs: UpdateSuggestionsDbs

        @Test
        fun `Doit réussir à mettre à jour les referentiels et vider les tables`() {
            assertDoesNotThrow { updateSuggestionsDbs.clearAll() }
            assertDoesNotThrow { updateSuggestionsDbs.updateSuggestionDbs() }
            assertDoesNotThrow { updateSuggestionsDbs.clearAll() }
        }

    }


}
