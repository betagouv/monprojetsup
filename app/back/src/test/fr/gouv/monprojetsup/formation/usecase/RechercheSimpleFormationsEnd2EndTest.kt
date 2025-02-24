package fr.gouv.monprojetsup.formation.usecase

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.MockitoAnnotations
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.core.env.Environment
import org.springframework.core.io.Resource
import org.springframework.test.web.servlet.MockMvc


data class RechercheScenario(
    val recherche: String,
    val premierRésultatAttendu: String?,
    val résultatAttendusParmiLesCinqPremiers: List<String>?,
)

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RechercheSimpleFormationsEnd2EndTest(
    @Autowired val mvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper,
    @Autowired val environment: Environment,
) {

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    private val logger = LoggerFactory.getLogger(RechercheSimpleFormationsEnd2EndTest::class.java)

    @Value("\${recherchesSimpleFormationReferencePath}")
    lateinit var resourceFile: Resource

    // Initialize the file path dynamically before tests
    @org.junit.jupiter.api.BeforeAll
    fun setUp() {
        testFile = resourceFile
    }

    companion object {
        private const val ENDPOINT_RECHERCHE = "/api/v1/public/formations/recherche/succincte"

        private lateinit var testFile: Resource

        @JvmStatic
        fun provideScenarios(): List<RechercheScenario> {
            val type = object : TypeToken<List<RechercheScenario>>() {}.type
            testFile.file.bufferedReader().use { reader ->
                val gsonBuilder = GsonBuilder()
                //gsonBuilder.registerTypeAdapter(ImmutablePair::class.java, ImmutablePairDeserializer())
                return gsonBuilder.create().fromJson(reader, type)
            }
        }
    }

    @ParameterizedTest
    @MethodSource("provideScenarios")
    fun `les résultats des recherches sont conformes aux résultats de référence`(scenario: RechercheScenario) {
        assumeTrue(environment.matchesProfiles("withRealData"), "Test ignoré car le profile Spring Boot 'withRealData' est inactif")
    }
}
