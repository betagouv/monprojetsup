package fr.gouv.monprojetsup.data.etl.port

import fr.gouv.monprojetsup.data.domain.Constants
import fr.gouv.monprojetsup.data.etl.MpsDataPort
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class FormationsMpsTests {

    @Autowired
    lateinit var mpsDataPort: MpsDataPort

    @Test
    fun `Il y a au moins 200 formations MPS`() {
        val formationsIds = mpsDataPort.getFormationsMpsIds()
        assertThat(formationsIds).hasSizeGreaterThanOrEqualTo(200)
    }

    @Test
    fun `Au moins un mot clé par formation`() {
        val formationsIds = mpsDataPort.getFormationsMpsIds()
        assert(formationsIds.isNotEmpty())
        val motsCles = mpsDataPort.getMotsClesFormations()
        assertThat(formationsIds).allMatch { motsCles.containsKey(it) && motsCles[it]!!.isNotEmpty() }

    }

    @Test
    fun `au plus 10 formations n'ont pas de durée`() {
        val formationsIds = mpsDataPort.getFormationsMpsIds()
        assert(formationsIds.isNotEmpty())
        val durees = mpsDataPort.getDurees()
        val nbSansDuree = formationsIds.count { durees[it] == null }
        assertThat(nbSansDuree).isLessThan(10)

    }


    @Nested
    inner class CritersTests {
        @Test
        fun `Les grilles d'analyse doivent être non vides`() {
            val grilles = mpsDataPort.getGrilles()
            assertThat(grilles).isNotEmpty()
        }


        @Test
        fun `au plus la moitié des formations n'a pas d'attendus`() {
            val formationsIds = mpsDataPort.getFormationsMpsIds()
            assert(formationsIds.isNotEmpty())
            val attendus = mpsDataPort.getAttendus()
            val nbSansAttendus = formationsIds.count { attendus[it] == null }
            assertThat(nbSansAttendus).isLessThan(formationsIds.size / 2)
        }

        @Test
        fun `au plus la moitié des formations n'a pas de conseils`() {
            val formationsIds = mpsDataPort.getFormationsMpsIds()
            assert(formationsIds.isNotEmpty())
            val conseils = mpsDataPort.getConseils()
            val nbSansAttendus = formationsIds.count { conseils[it] == null }
            assertThat(nbSansAttendus).isLessThan(formationsIds.size / 2)
        }

    }


    @Nested
    inner class MetiersDesFormationsTests {
        @Test
        fun `90 pourcent des formations ont au moins un métier associé`() {
            val formationKeys = mpsDataPort.getFormationsMpsIds()
            val formationsAvecAuMoinsUnMetier =
                mpsDataPort.getFormationsVersMetiersEtMetiersAssocies().filter { it.value.isNotEmpty() }.keys
            assertThat(formationKeys).isNotEmpty()
            val pctOk = 100 * formationsAvecAuMoinsUnMetier.size / formationKeys.size
            assertThat(pctOk).isGreaterThan(90)
        }


        @Test
        fun `CUPGE - Droit-économie-gestion hérite de tous les métiers des écoles de commerce`() {
            //c'est le mapping CPGE - licence qui crée les assciations métiers
            val mpsIds = mpsDataPort.getFormationsMpsIds()
            val cupgeEcoGestionMpsId = Constants.gFrCodToMpsId(Constants.CUPGE_ECO_GESTION_PSUP_FR_COD)
            assertThat(mpsIds).contains(cupgeEcoGestionMpsId)

            val ecoleCommerceMpsId = Constants.gFrCodToMpsId(Constants.ECOLE_COMMERCE_PSUP_FR_COD)
            assertThat(mpsIds).contains(ecoleCommerceMpsId)

            val formationsVersMetiers = mpsDataPort.getFormationsVersMetiersEtMetiersAssocies()

            val metiersEcoleCommerce = formationsVersMetiers[ecoleCommerceMpsId]
            assertThat(metiersEcoleCommerce).isNotNull()
            assertThat(metiersEcoleCommerce).isNotEmpty()

            val metiersCupge = formationsVersMetiers[cupgeEcoGestionMpsId]
            assertThat(metiersCupge).isNotNull()

            assertThat(metiersCupge).containsAll(metiersEcoleCommerce)

        }

    }

    @Test
    fun `CUPGE - Sciences, technologie, santé ne contient pas le mot-clé commerce international `() {
        val mpsIds = mpsDataPort.getFormationsMpsIds()
        val cupgeSciencesTechnoSanteMpsId =
            Constants.gFrCodToMpsId(Constants.CUPGE_ECO_SCIENCES_TECHNO_SANTE_PSUP_FR_COD)
        assertThat(mpsIds).contains(cupgeSciencesTechnoSanteMpsId)
        assertThat(mpsDataPort.getMotsClesFormations()[cupgeSciencesTechnoSanteMpsId]).doesNotContain("commerce international")
    }

    @Nested
    inner class LiensTests {
        @Test
        fun `Au moins un lien par formation`() {
            val formationsIds = mpsDataPort.getFormationsMpsIds()
            assert(formationsIds.isNotEmpty())
            val liens = mpsDataPort.getLiens()
            assertThat(formationsIds).allMatch { liens.containsKey(it) && liens[it]!!.isNotEmpty() }
        }

        @Test
        fun `Au moins un voeu par formation`() {
            val formationsIds = mpsDataPort.getFormationsMpsIds()
            assert(formationsIds.isNotEmpty())
            val voeux = mpsDataPort.getVoeux()
            assertThat(formationsIds).allMatch { voeux.containsKey(it) && voeux[it]!!.isNotEmpty() }
        }

        @Test
        fun `les liens ne sont pas dupliqués`() {
            mpsDataPort.getLiens().values
                .map { liens -> liens.map { l -> l.uri } }
                .forEach { uris -> assertThat(uris).doesNotHaveDuplicates() }
        }

    }

    @Nested
    inner class StatistiquesTests {

        @Test
        fun `tous les bacs de toutes les stats sont connus`() {
            val keys = mpsDataPort.getBacs().map { b -> b.key }
            val statsKeys = mpsDataPort.getStatsFormation().flatMap { it.value.nbAdmisParBac.keys }
            assertThat(keys).containsAll(statsKeys)
        }


        @Test
        fun `au moins 90 pour cent des formations hors apprentissage ont des stats non vides`() {
            val stats = mpsDataPort.getStatsFormation()
            val mpsids = mpsDataPort.getFormationsMpsIds()
            val labels = mpsDataPort.getFormationsLabels()

            //pas de stats avec des index farfelus
            assertThat(mpsids.toSet()).containsAll(stats.keys)

            val nbTotalHosApprentissage = labels.filter { !it.value.contains("apprentissage", ignoreCase = true) }.size
            val formationsSansStatsNb = stats.filter { !it.value.hasStatsAdmissions() }.keys

            assertThat(formationsSansStatsNb).hasSizeLessThanOrEqualTo(5 * nbTotalHosApprentissage / 100)

        }

        @Test
        fun `au moins 60 pour cent des formations ont des stats complètes`() {
            val stats = mpsDataPort.getStatsFormation()
            val mpsids = mpsDataPort.getFormationsMpsIds()

            //pas de stats avec des index farfelus
            assertThat(mpsids.toSet()).containsAll(stats.keys)

            val nbTotal = mpsids.size
            val formationsAvecStatsNb = stats.filter { it.value.hasFullStats() }.keys

            assertThat(formationsAvecStatsNb).hasSizeGreaterThanOrEqualTo(60 * nbTotal / 100)

        }

        @Test
        fun `Au moins 70 pour cent des las ont des stats non vides`() {
            val lasKeys = mpsDataPort.getLasToGenericIdMapping().keys
            val lasSansStatsAdmissions = mpsDataPort.getStatsFormation()
                .filter { lasKeys.contains(it.key) }
                .filter { !it.value.hasStatsAdmissions() }
                .map { it.key }
            val nbTotal = lasKeys.size
            assertThat(lasSansStatsAdmissions).hasSizeLessThanOrEqualTo(30 * nbTotal / 100)
        }


    }

}