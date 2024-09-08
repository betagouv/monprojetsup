package fr.gouv.monprojetsup.data.etl.loaders

import fr.gouv.monprojetsup.data.TestData
import org.apache.commons.lang3.tuple.Pair
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [LoadersTest::class, DataSources::class])
class EdgesTest {

    @Autowired
    lateinit var sources : DataSources

    private lateinit var edgesMetiersFormations: List<Pair<String, String>>
    private lateinit var edgesFormationsDomaines: List<Pair<String, String>>

    @BeforeEach
    fun setUp() {
        // Given
        val formationsIdeoSansfiche = OnisepDataLoader.loadFormationsSimplesIdeo()
        val formationsIdeoAvecFiche = OnisepDataLoader.loadFichesFormationsIdeo()
        val sousDomainesWeb = ArrayList(OnisepDataLoader.loadDomainesSousDomaines())

        // When
        val formationsIdeoDuSup = OnisepDataLoader.extractFormationsIdeoDuSup(
            formationsIdeoSansfiche,
            formationsIdeoAvecFiche
        )
        val filieresPsupToFormationsMetiersIdeo =
            OnisepDataLoader.loadPsupToIdeoCorrespondance(
                sources,
                formationsIdeoDuSup
            )

        val edgesFormations = OnisepDataLoader.getEdgesFormations(
            sousDomainesWeb,
            formationsIdeoDuSup,
            filieresPsupToFormationsMetiersIdeo
        )
        edgesFormationsDomaines = edgesFormations.left
        edgesMetiersFormations = edgesFormations.right
    }

    @Test
    fun `Chaque formation mps a au moins un domaine associé`() {
        Assertions.assertThat(edgesFormationsDomaines).isNotEmpty()
    }
    @Test
    fun `Chaque formation mps a au moins un metier associé`() {
        Assertions.assertThat(edgesMetiersFormations).isNotEmpty()
    }

    @Test
    fun `Les formations de référence ont au moins un domaine et un métier associé`() {
        val formationsWithAtLeastOneDomaine = edgesFormationsDomaines.map { it.left }.toSet()
        val formationsWithAtLEastOneMetier = edgesMetiersFormations.map { it.right }.toSet()
        Assertions.assertThat(TestData.psupToIdeoReference.keys).allSatisfy { psup ->
            Assertions.assertThat(formationsWithAtLeastOneDomaine).contains(psup)
            Assertions.assertThat(formationsWithAtLEastOneMetier).contains(psup)
        }
    }



}