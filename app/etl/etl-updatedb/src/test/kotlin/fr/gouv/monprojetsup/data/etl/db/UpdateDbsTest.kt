package fr.gouv.monprojetsup.data.etl.db

import fr.gouv.monprojetsup.data.TestData
import fr.gouv.monprojetsup.data.etl.formation.FormationDb
import fr.gouv.monprojetsup.data.etl.formation.UpdateFormationDbs
import fr.gouv.monprojetsup.data.etl.formation.VillesVoeuxDb
import fr.gouv.monprojetsup.data.etl.formationmetier.UpdateFormationMetierDbs
import fr.gouv.monprojetsup.data.etl.metier.UpdateMetierDbs
import fr.gouv.monprojetsup.data.etl.referentiel.BaccalaureatSpecialiteDb
import fr.gouv.monprojetsup.data.etl.referentiel.UpdateReferentielDbs
import fr.gouv.monprojetsup.data.etl.suggestions.SuggestionsCandidatsDb
import fr.gouv.monprojetsup.data.etl.suggestions.SuggestionsEdgesDb
import fr.gouv.monprojetsup.data.etl.suggestions.SuggestionsVillesDb
import fr.gouv.monprojetsup.data.etl.suggestions.UpdateSuggestionsDbs
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource

@TestPropertySource(properties = [
    "logging.level.org.hibernate.SQL=WARN",
    "logging.level.org.hibernate.type.descriptor.sql.BasicBinder=WARN"
])
class UpdateDbsTest : BDDRepositoryTest() {

    @Nested
    inner class UpdateFormationsDbTest {

        @Autowired
        lateinit var formationsdb: FormationDb

        @Autowired
        lateinit var updateReferentielsDbs: UpdateReferentielDbs

        @Autowired
        lateinit var villesVoeuxDb: VillesVoeuxDb

        @Autowired
        lateinit var updateFormationDbs: UpdateFormationDbs

        @BeforeEach
        fun init() {
            updateReferentielsDbs.updateBaccalaureatDb()
        }

        @Test
        @Tag("resource-intensive-test")
        fun `La table ville voeux est correctement remplie`() {
            assertDoesNotThrow { updateFormationDbs.updateVillesVoeuxDb() }
            val villesVoeux = villesVoeuxDb.findAll()
            assertThat(villesVoeux).isNotEmpty
        }

        @Test
        fun `Les tables des formations est correctement remplie`() {
            assertDoesNotThrow { updateFormationDbs.updateFormationsDb() }
            val formations = formationsdb.findAll()
            assertThat(formations).isNotEmpty
            val nbFormations = formations.size
            assertThat(formations.filter { !it.integrityCheck() }.map { it.id }).hasSizeLessThanOrEqualTo(TestData.MAX_PCT_FORMATIONS_ECHOUANT_AU_TEST_INTEGRITE * nbFormations / 100)
        }


        @Test
        fun `Les stats incluent l'indice tous bacs`() {
            //When
            val formation = formationsdb.findAll()
            assertThat(formation).allSatisfy { it.stats.toStats().inclutTousBacs() }
        }

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


    @Nested
    inner class UpdateMetiersTest {

        @Autowired
        lateinit var updateMetierDbs: UpdateMetierDbs

        @Test
        fun `Doit réussir à mettre à jour les referentiels et vider les tables`() {
            assertDoesNotThrow { updateMetierDbs.update() }
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

        private fun update() {
            updateFormationsMetiersDbs.clearAll()
            updateFormationDbs.updateFormationsDb()
            updateMetierDbs.update()
            updateFormationsMetiersDbs.update()
        }
        @Test
        fun `Doit réussir à mettre à jour les liens formations métiers`() {
            assertDoesNotThrow { update() }
        }
    }

    @Nested
    @ActiveProfiles("test")
    inner class UpdateSuggestionsTest  {

        @Autowired
        lateinit var updateSuggestionsDbs: UpdateSuggestionsDbs

        @Autowired
        lateinit var villesDb: SuggestionsVillesDb

        @Autowired
        lateinit var candidatsDb: SuggestionsCandidatsDb

        @Autowired
        lateinit var edgesDb: SuggestionsEdgesDb

        @Test
        @Tag("resource-intensive-test")
        fun `Doit réussir à mettre à jour les tables sugg_`() {
            assertDoesNotThrow { updateSuggestionsDbs.updateSuggestionDbs(voeuxOntChange) }
        }

        @Test
        fun `La table des villes doit inclure Soulac-sur-Mer`() {
            updateSuggestionsDbs.updateVillesDb()
            val ville1 = villesDb.findById(TestData.VILLE_SOULAC_SUR_MER_INSEE_CODE)
            assertThat(ville1).isPresent
        }

        @Test
        @Tag("resource-intensive-test")
        fun `La table des candidats doit être non vide`() {
            updateSuggestionsDbs.updateCandidatsDb()
            assertThat(candidatsDb.findAll()).isNotEmpty
        }

        @Test
        fun `La table des edges doit contenir suffisamment d arètes`() {
            updateSuggestionsDbs.updateEdgesDb()
            assertThat(edgesDb.count()).isGreaterThanOrEqualTo(TestData.MIN_NB_ARETES_SUGGESTIONS_GRAPH)
        }

    }

}
