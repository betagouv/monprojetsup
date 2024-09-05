package fr.gouv.monprojetsup.data.etl.db

import fr.gouv.monprojetsup.data.etl.formation.FormationDb
import fr.gouv.monprojetsup.data.etl.formation.UpdateFormationDbs
import fr.gouv.monprojetsup.data.etl.formationmetier.UpdateFormationMetierDbs
import fr.gouv.monprojetsup.data.etl.metier.UpdateMetierDbs
import fr.gouv.monprojetsup.data.etl.referentiel.BaccalaureatSpecialiteDb
import fr.gouv.monprojetsup.data.etl.referentiel.UpdateReferentielDbs
import fr.gouv.monprojetsup.data.etl.suggestions.UpdateSuggestionsDbs
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
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
        lateinit var updateReferentielsDbs: UpdateReferentielDbs

        @Autowired
        lateinit var updateFormationDbs: UpdateFormationDbs

        @BeforeEach
        fun init() {
            updateReferentielsDbs.updateBaccalaureatDb()
        }

        @Test
        fun `Doit réussir à mettre à jour et vider les tables`() {
            assertDoesNotThrow { updateFormationDbs.updateFormationDbs() }
        }

        @Test
        fun `Plus de 95 pour 100 des formations en base doivent réussir le test d'intégrité`() {
            updateFormationDbs.updateFormationDbs()
            val formations = formationsdb.findAll()
            val nbFormations = formations.size
            assertThat(formations.filter { it.integrityCheck() }).hasSizeGreaterThanOrEqualTo(95 * nbFormations / 100)
        }


    @Nested
    inner class UpdateReferentielsTest {

        @Autowired
        lateinit var updateReferentielDbs: UpdateReferentielDbs

        @Autowired
        lateinit var specialitesBacDb: BaccalaureatSpecialiteDb

        @Test
        fun `Doit réussir à mettre à jour les referentiels et vider les tables`() {
            assertDoesNotThrow { updateReferentielDbs.updateReferentielDbs() }
        }

        @Test
        fun `La table join_baccalaureat_specialite doit être non vide`() {
            updateReferentielDbs.updateReferentielDbs()
            assertThat(specialitesBacDb.findAll()).isNotEmpty
        }
    }

    }

    @Nested
    inner class UpdateMetiersTest {

        @Autowired
        lateinit var updateMetierDbs: UpdateMetierDbs

        @Test
        fun `Doit réussir à mettre à jour les referentiels et vider les tables`() {
            assertDoesNotThrow { updateMetierDbs.updateMetierDbs() }
        }

    }

    @Nested
    inner class UpdateFormationsMetiersTest {

        @Autowired
        lateinit var updateFormationDbs: UpdateFormationDbs

        @Autowired
        lateinit var updateMetierDbs: UpdateMetierDbs

        @Autowired
        lateinit var updateFormationsMetiersDbs: UpdateFormationMetierDbs

        @Test
        fun `Doit réussir à mettre à jour les liens formations métiers`() {
            assertDoesNotThrow { updateFormationsMetiersDbs.clearAll() }
            assertDoesNotThrow { updateFormationDbs.updateFormationDbs() }
            assertDoesNotThrow { updateMetierDbs.updateMetierDbs() }
            assertDoesNotThrow { updateFormationsMetiersDbs.update() }
        }

    }


    @Nested
    inner class UpdateSuggestionsTest {

        @Autowired
        lateinit var updateSuggestionsDbs: UpdateSuggestionsDbs

        @Test
        fun `Doit réussir à mettre à jour les tables`() {
            assertDoesNotThrow { updateSuggestionsDbs.updateSuggestionDbs() }
        }

    }


}
