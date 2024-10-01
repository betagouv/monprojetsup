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
    fun `Le label d'un lien contient au plus une occurrence de Onisep`() {
        val liens = mpsDataPort.getLiens().flatMap { it.value }
        val liensAvecDeuxOccurencesOnisep = liens.filter { Regex(Regex.escape("Onisep")).findAll(it.label).count() >= 2 }
        assertThat(liensAvecDeuxOccurencesOnisep).isEmpty()
    }

    @Test
    fun `Les elements des aretes sont en lien avec les référentiels`() {
        val edges = mpsDataPort.getEdges()

        val idAtomesDomaines = mpsDataPort.getDomaines().atomesIds
        val idAtomesInterets = mpsDataPort.getInterets().atomesIds
        val idAtomes = HashSet<String>()
        idAtomes.addAll(idAtomesDomaines)
        idAtomes.addAll(idAtomesInterets)

        val idDomaines = mpsDataPort.getDomaines().elementIds
        val idInterets = mpsDataPort.getInterets().elementIds
        val idElements = HashSet<String>()
        idElements.addAll(idDomaines)
        idElements.addAll(idInterets)

        val idFormations = mpsDataPort.getFormationsMpsIds()
        val idMetiers = mpsDataPort.getMetiersMpsIds()


        assertThat( idAtomesInterets ).containsAll(
            edges
                .filter { it.third == SuggestionsEdgeEntity.TYPE_EDGE_INTERET_METIER }
                .map { it.first }.toSet()
        )
        assertThat( idMetiers ).containsAll(
            edges
                .filter { it.third == SuggestionsEdgeEntity.TYPE_EDGE_INTERET_METIER }
                .map { it.second }.toSet()
        )
        assertThat( idAtomesDomaines ).containsAll(
            edges
                .filter { it.third == SuggestionsEdgeEntity.TYPE_EDGE_DOMAINES_METIERS }
                .map { it.first }.toSet()
        )
        assertThat( idMetiers ).containsAll(
            edges
                .filter { it.third == SuggestionsEdgeEntity.TYPE_EDGE_DOMAINES_METIERS }
                .map { it.second }.toSet()
        )
        assertThat( idAtomes ).containsAll(
            edges
                .filter { it.third == SuggestionsEdgeEntity.TYPE_EDGE_ATOME_ELEMENT }
                .map { it.first }.toSet()
        )
        assertThat( idElements ).containsAll(
            edges
                .filter { it.third == SuggestionsEdgeEntity.TYPE_EDGE_ATOME_ELEMENT }
                .map { it.second }.toSet()
        )
        assertThat( idFormations ).containsAll(
            edges
                .filter { it.third == SuggestionsEdgeEntity.TYPE_EDGE_FORMATION_PSUP_TO_FORMATION_MPS }
                .map { it.second }.toSet()
        )

        /*        const val TYPE_EDGE_INTERET_METIER: Int = 0
        const val TYPE_EDGE_FORMATIONS_PSUP_DOMAINES: Int = 1
        const val TYPE_EDGE_DOMAINES_METIERS: Int = 2
        const val TYPE_EDGE_SECTEURS_METIERS: Int = 3
        const val TYPE_EDGE_METIERS_ASSOCIES: Int = 4
        const val TYPE_EDGE_FORMATION_PSUP_TO_FORMATION_MPS: Int = 5
        const val TYPE_EDGE_LAS_TO_GENERIC: Int = 6
        const val TYPE_EDGE_LAS_TO_PASS: Int = 7
        const val TYPE_EDGE_ATOME_ELEMENT: Int = 8
        const val TYPE_EDGE_METIERS_FORMATIONS_PSUP: Int = 9
        */
    }


}