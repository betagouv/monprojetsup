package fr.gouv.monprojetsup.data.etl.port

import fr.gouv.monprojetsup.data.TestData
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques.TOUS_BACS_CODE_LEGACY
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques.TOUS_BACS_CODE_MPS
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MpsDataPortTest : DataPortTest(){


    @Test
    fun `Doit réussir à accéder aux différents ports de données`() {
        mpsDataPort.getFormationsMpsIds()
        mpsDataPort.getMetiersMpsIds()
        mpsDataPort.getMatieres()
        mpsDataPort.getFormationsLabels()
        mpsDataPort.getMetiersLabels()
        mpsDataPort.getMpsIdToPsupFlIds()
        mpsDataPort.getMoyennesGeneralesAdmis()
        mpsDataPort.getFormationsMpsIds()
        mpsDataPort.getApprentissage()
        mpsDataPort.getLasToGenericIdMapping()
        mpsDataPort.getVoeux()
        mpsDataPort.getDebugLabels()
        mpsDataPort.getCapacitesAccueil()
        mpsDataPort.getFormationsVersMetiersEtMetiersAssocies()
        mpsDataPort.getBacs()
        mpsDataPort.getSpecialites()
    }


    @Test
    fun `peu de voeux n'ont pas de coordonnées gps`() {
        val voeux = mpsDataPort.getVoeux()
        assert(voeux.isNotEmpty())
        val nbSansCoordonnees = voeux.flatMap { it.value }.count { it.lat == null || it.lng == null }
        assertThat(nbSansCoordonnees).isLessThan(TestData.MAX_NB_VOEUX_SANS_COORDONNEES_GPS)
    }


    @Test
    fun `la liste des bacs contient TOUS_BACS_CODE_MPS et ne contient pas TOUS_BACS_CODE_LEGACY`() {
        val bacs = mpsDataPort.getBacs()
        assertThat(bacs.filter { b -> b.key == TOUS_BACS_CODE_LEGACY }).isEmpty()
        assertThat(bacs.filter { b -> b.key == TOUS_BACS_CODE_MPS }).hasSize(1)
    }

    @Nested
    inner class SpecialitesTests {

        @Test
        fun `la liste des spécialites a une entrée TOUS_BACS_CODE_MPS`() {
            val specialitesParBac = mpsDataPort.getSpecialites().specialitesParBac
            assertThat(specialitesParBac.filter { b -> b.key == TOUS_BACS_CODE_MPS }).hasSize(1)
            assertThat(specialitesParBac.filter { b -> b.key == TOUS_BACS_CODE_LEGACY }).hasSize(0)
        }

    }

    @Test
    fun `Les bacs sont chargés correctemetnt`() {
        assertThat(mpsDataPort.getBacs()).isNotEmpty
        assertThat(mpsDataPort.getBacs()).anyMatch { it.key == "Générale" }
    }

    @Test
    fun `Les elements des aretes atome element sont en lien avec les référentiels`() {
        val edgesAtomeElement = mpsDataPort.getEdges().filter { it.third == SuggestionsEdgeEntity.TYPE_EDGE_ATOME_ELEMENT }
        val idRef = HashSet<String>()
        idRef.addAll(mpsDataPort.getDomaines().elementIds)
        idRef.addAll(mpsDataPort.getInterets().elementIds)
        val idElementsAppearingInEdges : Set<String> = edgesAtomeElement.map { it.second }.toSet()
        assertThat(idElementsAppearingInEdges).allMatch { idRef.contains(it) }
    }


}