package fr.gouv.monprojetsup.data.etl.loaders

import fr.gouv.monprojetsup.data.TestData
import fr.gouv.monprojetsup.data.domain.Constants
import fr.gouv.monprojetsup.data.domain.model.onisep.OnisepData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles


@SpringBootTest(classes = [OnisepDataLoaderTest::class, DataSources::class] )
@ActiveProfiles("test")
class OnisepDataLoaderTest {

    private lateinit var onisepData: OnisepData

    @Autowired
    private lateinit var dataSources: DataSources

    @BeforeEach
    fun setUp() {
        onisepData = OnisepDataLoader.fromFiles(dataSources)
    }

    @Test
    fun `Doit réussir à charger les données Onisep`() {
        assertThat(onisepData.formationsIdeo).isNotEmpty()
        assertThat(onisepData.metiersIdeo).isNotEmpty()
    }

    @Test
    fun `Doit réussir à étendre les intérêts`() {
        val interets = onisepData.interets
        val expansion = interets.itemVersGroupe
        assertThat(expansion).isNotEmpty()
    }

    @Test
    fun `CUPGE Eco Gestion a au moins un metier`() {
        assertThat(onisepData.edgesMetiersFormations).anyMatch { it.right == Constants.gFlCodToMpsId(TestData.CUPGE_ECO_GESTION_PSUP_FL_COD1) }
        assertThat(onisepData.edgesMetiersFormations).anyMatch { it.right == Constants.gFlCodToMpsId(TestData.CUPGE_ECO_GESTION_PSUP_FL_COD2) }
    }

    @Test
    fun `Doit réussir à récupérer les formations ideo `() {
        // Given
        val formations = OnisepDataLoader.loadFormationsSimplesIdeo(dataSources)
        // When
        // Then
        assertThat(formations).isNotEmpty()
    }

    @Test
    fun `Doit réussir à récupérer les fiches formations ideo `() {
        // Given
        val fiches = OnisepDataLoader.loadFichesFormationsIdeo(dataSources)
        // When
        // Then
        assertThat(fiches).isNotEmpty()
    }

    @Test
    fun `Doit réussir à récupérer les métiers ideo`() {
        // Given
        val metiers = OnisepDataLoader.loadMetiersSimplesIdeo(dataSources)
        // When
        // Then
        assertThat(metiers).isNotEmpty()
    }

    @Test
    fun `Doit réussir à récupérer les fiches métiers ideo  `() {
        // Given
        val metiers = OnisepDataLoader.loadFichesMetiersIDeo(dataSources)
        // When
        // Then
        assertThat(metiers).isNotEmpty()
    }

    @Test
    fun `Toutes les fiches métiers ideo ont un descriptif`() {
        // Given
        val metiers = OnisepDataLoader.loadFichesMetiersIDeo(dataSources)
        // When
        // Then
        assertThat(metiers.map{ m -> m.descriptif}).allMatch { it.isNotBlank() }
    }


    @Test fun `Doit réussir à charger les intérêts, qui ne contiennent pas le terme je veux`() {
        val interets = OnisepDataLoader.loadInterets(dataSources)
        val labels = interets.groupeInterets.flatMap { it.items }.map { it.frontLabel}
        assertThat(labels).isNotEmpty
        assertThat(labels).allMatch { !it.contains("je veux") }
    }

    @Test
    fun `Il y a suffisamment de lignes dans la correspondance psup Onisep`() {
        assertThat(onisepData.filieresToFormationsOnisep.stream().map { l -> l.gFlCod }).hasSizeGreaterThanOrEqualTo(TestData.MIN_NB_CORR_PSUP_IDEO)
    }


}