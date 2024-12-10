package fr.gouv.monprojetsup.data.etl.db

import fr.gouv.monprojetsup.data.Constants
import fr.gouv.monprojetsup.data.TestData
import fr.gouv.monprojetsup.data.etl.BatchUpdate
import fr.gouv.monprojetsup.data.etl.formation.FormationDb
import fr.gouv.monprojetsup.data.etl.formation.UpdateFormationDbs
import fr.gouv.monprojetsup.data.etl.formation.VillesVoeuxDb
import fr.gouv.monprojetsup.data.etl.formation.VoeuxDb
import fr.gouv.monprojetsup.data.etl.formationmetier.UpdateFormationMetierDbs
import fr.gouv.monprojetsup.data.etl.metier.UpdateMetierDbs
import fr.gouv.monprojetsup.data.etl.referentiel.BaccalaureatDb
import fr.gouv.monprojetsup.data.etl.referentiel.BaccalaureatSpecialiteDb
import fr.gouv.monprojetsup.data.etl.referentiel.UpdateReferentielDbs
import fr.gouv.monprojetsup.data.etl.suggestions.SuggestionsCandidatsDb
import fr.gouv.monprojetsup.data.etl.suggestions.SuggestionsEdgesDb
import fr.gouv.monprojetsup.data.etl.suggestions.SuggestionsVillesDb
import fr.gouv.monprojetsup.data.etl.suggestions.UpdateSuggestionsDbs
import fr.gouv.monprojetsup.data.formation.entity.FormationEntity
import fr.gouv.monprojetsup.data.formation.entity.VoeuEntity
import fr.gouv.monprojetsup.data.model.psup.DescriptifVoeu
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertNotNull

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
        lateinit var voeuxDb: VoeuxDb

        @Autowired
        lateinit var updateReferentielsDbs: UpdateReferentielDbs

        @Autowired
        lateinit var villesVoeuxDb: VillesVoeuxDb

        @Autowired
        lateinit var updateFormationDbs: UpdateFormationDbs

        @Autowired
        lateinit var batchUpdate: BatchUpdate

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
            assertThat(villesVoeux.filter { it.idVille == Constants.CODE_COMMUNE_INSEE_PARIS_VINGTIEME }).isNotEmpty()
        }

        @Test
        @Tag("resource-intensive-test")
        fun `La tables des formations est correctement remplie`() {
            assertDoesNotThrow { updateFormationDbs.updateFormationsDb() }
            assertDoesNotThrow { updateFormationDbs.updateVoeuxDb() }
            val formations = formationsdb.findAll()
            assertThat(formations).isNotEmpty
            val nbFormations = formations.size
            assertThat(formations.filter { !it.integrityCheck() }.map { it.id }).hasSizeLessThanOrEqualTo(TestData.MAX_PCT_FORMATIONS_ECHOUANT_AU_TEST_INTEGRITE * nbFormations / 100)
        }


        @Test
        @Tag("resource-intensive-test")
        fun `Une formation ou un voeu qui disparait est marqué obsolete`() {
            //When
            val formation =  FormationEntity().apply {
                id = "obsolete"
                label = "label"
                liens = listOf()
                obsolete = false
            }
            batchUpdate.upsertEntities(listOf(formation))

            val voeu = VoeuEntity().apply {
                id = "obsolete"
                nom = "label"
                commune = ""
                codeCommune = ""
                lat = 0.0
                lng = 0.0
                obsolete = false
                descriptif = DescriptifVoeu(0,0,0,"","","","")
                capacite = 0
                obsolete = false
            }
            batchUpdate.upsertEntities(listOf(voeu))

            updateFormationDbs.updateFormationsDb()
            updateFormationDbs.updateVoeuxDb()

            val formation2 = formationsdb.findById("obsolete").orElse(null)
            assertNotNull(formation2)
            assertThat(formation2.obsolete)

            val voeu2 = voeuxDb.findById("obsolete").orElse(null)
            assertNotNull(voeu2)
            assertThat(voeu2.obsolete)
        }

    }

    @Nested
    inner class UpdateReferentielsTest {

        @Autowired
        lateinit var updateReferentielDbs: UpdateReferentielDbs

        @Autowired
        lateinit var baccalaureatDb: BaccalaureatDb

        @Autowired
        lateinit var specialiteDb: BaccalaureatSpecialiteDb

        @Autowired
        lateinit var specialitesBacDb: BaccalaureatSpecialiteDb

        @BeforeEach
        fun init() {
            updateReferentielDbs.updateReferentielDbs()
        }

        @Test
        @Tag("resource-intensive-test")
        fun `Les table bacs et specialites doivent être non vide`() {
            val bacs = baccalaureatDb.findAll()
            val specialites = specialiteDb.findAll()
            val specialitesBacs = specialitesBacDb.findAll()
            assertThat(bacs).isNotEmpty
            assertThat(specialites).isNotEmpty
            assertThat(specialitesBacs).isNotEmpty
        }
    }


    @Nested
    inner class UpdateMetiersTest {

        @Autowired
        lateinit var updateMetierDbs: UpdateMetierDbs

        @Test
        @Tag("resource-intensive-test")
        fun `Doit réussir à mettre à jour le référentiel des métiers`() {
            assertDoesNotThrow { updateMetierDbs.update() }
        }

    }

    @Tag("resource-intensive-test")
    @Nested
    inner class UpdateFormationsMetiersTest {

        @Autowired
        lateinit var updateFormationDbs: UpdateFormationDbs

        @Autowired
        lateinit var updateMetierDbs: UpdateMetierDbs

        @Autowired
        lateinit var updateFormationsMetiersDbs: UpdateFormationMetierDbs

        private fun update() {
            updateFormationDbs.updateFormationsDb()
            updateMetierDbs.update()
            updateFormationsMetiersDbs.update()
        }

        @Tag("resource-intensive-test")
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
        lateinit var paniersVoeuxdb: SuggestionsCandidatsDb

        @Autowired
        lateinit var edgesDb: SuggestionsEdgesDb

        @Test
        @Tag("resource-intensive-test")
        fun `Doit réussir à mettre à jour les tables sugg_`() {
            assertDoesNotThrow { updateSuggestionsDbs.updateSuggestionDbs(false) }
        }

        @Test
        @Tag("resource-intensive-test")
        fun `La table des villes doit inclure Soulac-sur-Mer`() {
            updateSuggestionsDbs.updateVillesDb()
            val ville1 = villesDb.findById(TestData.VILLE_SOULAC_SUR_MER_INSEE_CODE)
            assertThat(ville1).isPresent
        }

        @Test
        @Tag("resource-intensive-test")
        fun `La table des paniers de voeux doit être non vide`() {
            updateSuggestionsDbs.updatePaniersVoeuxDb()
            assertThat(paniersVoeuxdb.findAll()).isNotEmpty
        }

        @Test
        fun `La table des edges doit contenir suffisamment d arètes`() {
            updateSuggestionsDbs.updateEdgesDb()
            assertThat(edgesDb.count()).isGreaterThanOrEqualTo(TestData.MIN_NB_ARETES_SUGGESTIONS_GRAPH)
        }


    }

}
