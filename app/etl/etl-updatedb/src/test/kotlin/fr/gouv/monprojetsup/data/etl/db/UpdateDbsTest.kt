package fr.gouv.monprojetsup.data.etl.db

import fr.gouv.monprojetsup.data.TestData
import fr.gouv.monprojetsup.data.etl.formation.FormationDb
import fr.gouv.monprojetsup.data.etl.formation.UpdateFormationDbs
import fr.gouv.monprojetsup.data.etl.formationmetier.UpdateFormationMetierDbs
import fr.gouv.monprojetsup.data.etl.metier.UpdateMetierDbs
import fr.gouv.monprojetsup.data.etl.referentiel.BaccalaureatSpecialiteDb
import fr.gouv.monprojetsup.data.etl.referentiel.UpdateReferentielDbs
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
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
        lateinit var updateFormationDbs: UpdateFormationDbs

        @BeforeEach
        fun init() {
            updateReferentielsDbs.updateBaccalaureatDb()
        }

        @Test
        fun `Doit réussir à mettre à jour et vider les tables`() {
            assertDoesNotThrow { updateFormationDbs.updateFormationDbs() }
            assertThat(formationsdb.findAll()).isNotEmpty
        }

        @Test
        fun `La plupart des formations en base doivent réussir le test d'intégrité`() {
            updateFormationDbs.updateFormationDbs()
            val formations = formationsdb.findAll()
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

        private fun update() {
            updateFormationsMetiersDbs.clearAll()
            updateFormationDbs.updateFormationDbs()
            updateMetierDbs.updateMetierDbs()
            updateFormationsMetiersDbs.update()
        }
        @Test
        fun `Doit réussir à mettre à jour les liens formations métiers`() {
            assertDoesNotThrow { update() }

        }


    }


}
