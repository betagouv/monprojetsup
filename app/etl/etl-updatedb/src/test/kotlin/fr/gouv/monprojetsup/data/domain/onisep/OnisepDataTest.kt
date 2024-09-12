package fr.gouv.monprojetsup.data.domain.onisep

import fr.gouv.monprojetsup.data.TestData
import fr.gouv.monprojetsup.data.domain.Constants
import fr.gouv.monprojetsup.data.domain.model.onisep.OnisepData
import fr.gouv.monprojetsup.data.etl.loaders.DataSources
import fr.gouv.monprojetsup.data.etl.loaders.OnisepDataLoader
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles


@SpringBootTest(classes = [OnisepDataTest::class, DataSources::class] )
@ActiveProfiles("test")
class OnisepDataTest {

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


}