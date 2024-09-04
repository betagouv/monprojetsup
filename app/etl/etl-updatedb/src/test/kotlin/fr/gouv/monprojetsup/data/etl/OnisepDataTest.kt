package fr.gouv.monprojetsup.data.etl

import fr.gouv.monprojetsup.data.domain.model.onisep.OnisepData
import fr.gouv.monprojetsup.data.etl.loaders.DataSources
import fr.gouv.monprojetsup.data.etl.loaders.OnisepDataLoader
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ActiveProfiles


@SpringBootTest(classes =
    [OnisepDataTest.OnisepDataTestConfig::class]
)
@ActiveProfiles("test")
class OnisepDataTest {

    private lateinit var onisepData: OnisepData

    @Autowired
    private lateinit var dataSources: DataSources

    @BeforeEach
    fun setUp() {
        onisepData = OnisepDataLoader.fromFiles(dataSources)
    }

    @Nested
    inner class OnisepDataTest {
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
    }

    @Configuration
    internal open class OnisepDataTestConfig {
        @Bean
        open fun dataSources(): DataSources {
            return DataSources()
        }
    }

}