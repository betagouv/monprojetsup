package fr.gouv.monprojetsup.data.etl.port

import fr.gouv.monprojetsup.data.TestData
import fr.gouv.monprojetsup.data.etl.MpsDataPort
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques.TOUS_BACS_CODE_LEGACY
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques.TOUS_BACS_CODE_MPS
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest(classes = [MpsDataPort::class] )
class MpsDataPortTest {

    @Autowired
    lateinit var mpsDataPort: MpsDataPort

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


}