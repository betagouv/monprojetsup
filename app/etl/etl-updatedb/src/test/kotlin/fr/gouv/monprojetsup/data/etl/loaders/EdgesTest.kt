package fr.gouv.monprojetsup.data.etl.loaders

import fr.gouv.monprojetsup.data.Constants
import fr.gouv.monprojetsup.data.TestData
import org.apache.commons.lang3.tuple.Pair
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [DataSources::class])
class EdgesTest {

    @Autowired
    lateinit var sources: DataSources


    fun getEdgesFormationsDomaines(): List<Pair<String, String>> {
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
        return edgesFormations.left
    }

    fun getEdgesMetiersFormations(): List<Pair<String, String>> {
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
        return edgesFormations.right
    }

    @Test
    fun `Au moins 100 formations ont un lien avec un domaine`() {
        assertThat(getEdgesFormationsDomaines().map { it.left }).hasSizeGreaterThanOrEqualTo(100)
    }

    @Test
    fun `Au moins 100 formations ont un lien avec un metier`() {
        assertThat(getEdgesMetiersFormations().map { it.right }).hasSizeGreaterThanOrEqualTo(100)
    }

    @Test
    fun `Les formations de référence ont au moins un domaine et un métier associé`() {
        val formationsWithAtLeastOneDomaine = getEdgesFormationsDomaines().map { it.left }.toSet()
        val formationsWithAtLEastOneMetier = getEdgesMetiersFormations().map { it.right }.toSet()
        assertThat(TestData.psupToIdeoReference.keys).allSatisfy { psup ->
            assertThat(formationsWithAtLeastOneDomaine).contains(psup)
            assertThat(formationsWithAtLEastOneMetier).contains(psup)
        }
        assertThat(
            listOf(
                Constants.gFlCodToMpsId(TestData.CUPGE_ECO_GESTION_PSUP_FL_COD1),
                Constants.gFlCodToMpsId(TestData.CUPGE_ECO_GESTION_PSUP_FL_COD2)
            )
        ).allSatisfy { psup ->
            assertThat(formationsWithAtLeastOneDomaine).contains(psup)
            assertThat(formationsWithAtLEastOneMetier).contains(psup)
        }
    }


}