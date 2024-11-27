package fr.gouv.monprojetsup.data.etl.port

import fr.gouv.monprojetsup.data.Constants.gFlCodToMpsId
import fr.gouv.monprojetsup.data.Constants.gFrCodToMpsId
import fr.gouv.monprojetsup.data.TestData
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class FormationsMpsTests : DataPortTest() {


    @Test
    fun `Il y a un nombre important de formations MPS`() {
        val formationsIds = mpsDataPort.getFormationsMpsIds()
        assertThat(formationsIds).hasSizeGreaterThanOrEqualTo(TestData.MIN_NB_FORMATIONS_MPS)
    }

    @Test
    fun `Au moins un mot clé par formation`() {
        val formationsIds = mpsDataPort.getFormationsMpsIds()
        assert(formationsIds.isNotEmpty())
        val motsCles = mpsDataPort.getMotsClesFormations()
        assertThat(formationsIds).allMatch { motsCles.containsKey(it) && motsCles[it]!!.isNotEmpty() }

    }

    @Test
    fun `au plus 1 formation n'a pas de durée`() {
        val formationsIds = mpsDataPort.getFormationsMpsIds()
        assert(formationsIds.isNotEmpty())
        val durees = mpsDataPort.getDurees()
        val nbSansDuree = formationsIds.count { durees[it] == null }
        assertThat(nbSansDuree).isLessThan(TestData.MAX_NB_FORMATIONS_SANS_DUREE)

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
        fun `80 pourcent des formations ont au moins un métier associé`() {
            val formationKeys = mpsDataPort.getFormationsMpsIds()
            val formationsSansMetier =
                mpsDataPort.getFormationsVersMetiersEtMetiersAssocies().filter { it.value.isEmpty() }.keys
            assertThat(formationKeys).isNotEmpty()
            assertThat(formationsSansMetier).hasSizeLessThanOrEqualTo( 20 * formationKeys.size / 100)
        }


        @Test
        fun `CUPGE - Droit-économie-gestion hérite de tous les métiers des écoles de commerce`() {
            //c'est le mapping CPGE - licence qui crée les associations métiers
            val mpsIds = mpsDataPort.getFormationsMpsIds()
            val cupgeEcoGestionMpsId = gFrCodToMpsId(TestData.CUPGE_ECO_GESTION_PSUP_FR_COD)
            assertThat(mpsIds).contains(cupgeEcoGestionMpsId)

            val ecoleCommerceMpsId =
                listOf(TestData.ECOLE_COMMERCE_PSUP_FL_COD1, TestData.ECOLE_COMMERCE_PSUP_FL_COD2,TestData.ECOLE_COMMERCE_PSUP_FL_COD3)
                    .map { gFlCodToMpsId(it) }
            assertThat(mpsIds).containsAll(ecoleCommerceMpsId)

            val formationsVersMetiers = mpsDataPort.getFormationsVersMetiersEtMetiersAssocies()

            val metiersEcoleCommerce = ecoleCommerceMpsId.flatMap { formationsVersMetiers[it].orEmpty() }
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
            gFrCodToMpsId(TestData.CUPGE_ECO_SCIENCES_TECHNO_SANTE_PSUP_FR_COD)
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
    inner class AdmissionStatsTests {


        @Test
        fun `le bac NC apparait dans la liste des bacs`() {
            val keys = HashSet(mpsDataPort.getBacs().map { b -> b.key })
            assertThat(keys).contains(PsupStatistiques.TOUS_BACS_CODE_MPS)
        }

        @Test
        fun `tous les bacs de toutes les stats sont connus`() {
            val keys = HashSet(mpsDataPort.getBacs().map { b -> b.key })
            val statsKeys = mpsDataPort.getStatsFormation().flatMap { it.value.nbAdmisParBac.keys }
            assertThat(keys).containsAll(statsKeys)
        }


        @Test
        fun `la plupart des formations hors apprentissage ont des stats non vides`() {
            val stats = mpsDataPort.getStatsFormation()
            val mpsids = mpsDataPort.getFormationsMpsIds()
            val labels = mpsDataPort.getFormationsLabels()

            //pas de stats avec des index farfelus
            assertThat(mpsids.toSet()).containsAll(stats.keys)

            val nbTotalHosApprentissage = labels.filter { !it.value.contains("apprentissage", ignoreCase = true) }.size
            val formationsSansStatsNb = stats.filter { !it.value.hasStatsAdmissions() }.keys

            assertThat(formationsSansStatsNb).hasSizeLessThanOrEqualTo(TestData.MAX_PCT_FORMATIONS_SANS_STATS_HORS_APPRENTISSAGE * nbTotalHosApprentissage / 100)

        }

        @Test
        fun `la plupart des formations ont des stats complètes`() {
            val stats = mpsDataPort.getStatsFormation()
            val mpsids = mpsDataPort.getFormationsMpsIds()

            //pas de stats avec des index farfelus
            assertThat(mpsids.toSet()).containsAll(stats.keys)

            val nbTotal = mpsids.size
            val formationsSansStats = stats.filter { !it.value.hasFullStats() }.keys

            assertThat(formationsSansStats).hasSizeLessThanOrEqualTo(TestData.MAX_PCT_FORMATIONS_SANS_STATS_COMPLETES * nbTotal / 100)

        }

        @Test
        fun `LA plupart des des las ont des stats non vides`() {
            val lasKeys = mpsDataPort.getLasToGenericIdMapping().keys
            val lasSansStatsAdmissions = mpsDataPort.getStatsFormation()
                .filter { lasKeys.contains(it.key) }
                .filter { !it.value.hasStatsAdmissions() }
                .map { it.key }
            val nbTotal = lasKeys.size
            assertThat(lasSansStatsAdmissions).hasSizeLessThanOrEqualTo(TestData.MAX_PCT_LAS_AVEC_STATS_VIDES * nbTotal / 100)
        }


    }

}