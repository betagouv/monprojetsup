package fr.gouv.monprojetsup.data.etl.port

import fr.gouv.monprojetsup.data.TestData
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsEdgeEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MpsDataPortTest : DataPortTest(){


    @Test
    fun `Doit réussir à accéder aux différents ports de données`() {
        mpsDataPort.getFormationsMpsIds()
        mpsDataPort.getMetiersMpsIds()
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
    fun `Les bacs sont chargés correctemetnt`() {
        assertThat(mpsDataPort.getBacs()).isNotEmpty
        assertThat(mpsDataPort.getBacs()).anyMatch { it.key == "Générale" }
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

    @Test
    fun `Les labels des formations ne contiennent pas L1`() {
        val formationsLabels = mpsDataPort.getFormationsLabels().values
        assertThat(formationsLabels).noneMatch { l -> l.contains("L1") }
    }

    @Test
    fun `Les attendus ne sont pas trop longs`() {
        val attendus = mpsDataPort.getAttendus()
        assertThat(attendus.entries).allSatisfy { e -> e.value.length < 200 }
    }

    @Test
    fun `Les conseils ne sont pas trop longs`() {
        val conseils = mpsDataPort.getConseils()
        assertThat(conseils.entries).allSatisfy { e -> e.value.length < 200 }
    }

}