package fr.gouv.monprojetsup.data.etl.loaders

import fr.gouv.monprojetsup.data.TestData
import fr.gouv.monprojetsup.data.domain.Constants
import org.apache.commons.lang3.tuple.Pair
import org.assertj.core.api.Assertions.assertThat
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
        val sousDomainesWeb = ArrayList(OnisepDataLoader.loadDomainesSousDomaines(sources))
        val formationsIdeoDuSup = OnisepDataLoader.loadFormationsIdeoDuSup(sources)

        val filieresPsupToFormationsMetiersIdeo =
            OnisepDataLoader.loadPsupToIdeoCorrespondance(
                sources,
                formationsIdeoDuSup
            )

        val edgesFormations = OnisepDataLoader.getEdgesFormations(
            sousDomainesWeb,
            filieresPsupToFormationsMetiersIdeo
        )
        edgesFormationsDomaines = edgesFormations.left
        edgesMetiersFormations = edgesFormations.right
    }

    @Test
    fun `Chaque formation mps a au moins un domaine associé`() {
        assertThat(edgesFormationsDomaines).isNotEmpty()
    }
    @Test
    fun `Chaque formation mps a au moins un metier associé`() {
        assertThat(edgesMetiersFormations).isNotEmpty()
    }

    @Test
    fun `Les formations de référence ont au moins un domaine et un métier associé`() {
        val formationsWithAtLeastOneDomaine = edgesFormationsDomaines.map { it.left }.toSet()
        val formationsWithAtLEastOneMetier = edgesMetiersFormations.map { it.right }.toSet()
        assertThat(TestData.psupToIdeoReference.keys).allSatisfy { psup ->
            assertThat(formationsWithAtLeastOneDomaine).contains(psup)
            assertThat(formationsWithAtLEastOneMetier).contains(psup)
        }
        assertThat(listOf(
            Constants.gFlCodToMpsId(TestData.CUPGE_ECO_GESTION_PSUP_FL_COD1),
            Constants.gFlCodToMpsId(TestData.CUPGE_ECO_GESTION_PSUP_FL_COD2)
        )).allSatisfy { psup ->
                assertThat(formationsWithAtLeastOneDomaine).contains(psup)
                assertThat(formationsWithAtLEastOneMetier).contains(psup)
            }
    }



}