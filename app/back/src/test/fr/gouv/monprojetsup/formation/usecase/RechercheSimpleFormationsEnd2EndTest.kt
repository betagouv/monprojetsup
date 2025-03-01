package fr.gouv.monprojetsup.formation.usecase

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.formation.application.controller.FormationController.Companion.TAILLE_MINIMUM_RECHERCHE
import fr.gouv.monprojetsup.formation.application.dto.FormationsCourtesDTO
import fr.gouv.monprojetsup.formation.application.dto.RechercheFormationsDTO
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.BDDMockito.given
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.env.Environment
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

data class RechercheScenario(
    val recherche: String,
    val premierRésultatAttendu: String?,
    val résultatAttendusParmiLesCinqPremiers: List<String>?,
)

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
// @Tag("withRealData")
@EnabledIfSystemProperty(named = "spring.profiles.active", matches = "test-withRealData")
class RechercheSimpleFormationsEnd2EndTest(
    @Autowired val mvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper,
    @Autowired val environment: Environment,
) {
    @MockBean
    lateinit var suggestionsFormationsService: SuggestionsFormationsService

    @Autowired
    lateinit var rechercherFormation: RechercherFormationsService

    @Autowired
    lateinit var ordonnerRechercheFormationsBuilder: OrdonnerRechercheFormationsBuilder

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

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
                return gsonBuilder.create().fromJson(reader, type)
            }
        }
    }

    @ParameterizedTest
    @MethodSource("provideScenarios")
    fun `les scores sont conformes aux résultats de référence`(scenario: RechercheScenario) {
        assumeTrue(environment.matchesProfiles("test-withRealData"), "Test ignoré car le profile Spring Boot 'withRealData' est inactif")

        // Given

        // When
        val resultat = rechercherFormation.rechercheLesFormationsAvecLeurScoreCorrespondantes(scenario.recherche, TAILLE_MINIMUM_RECHERCHE)

        val resultatsTries = ordonnerRechercheFormationsBuilder.trierParScore(resultat)

        // Then
        if (scenario.premierRésultatAttendu != null) {
            assertThat(resultatsTries).isNotEmpty()
            assertThat(resultatsTries[0].id).isEqualTo(scenario.premierRésultatAttendu)
        }
    }

    @ParameterizedTest
    @MethodSource("provideScenarios")
    fun `les résultats des recherches sont conformes aux résultats de référence`(scenario: RechercheScenario) {
        assumeTrue(environment.matchesProfiles("test-withRealData"), "Test ignoré car le profile Spring Boot 'withRealData' est inactif")

        // Given
        given(suggestionsFormationsService.recupererLesSuggestionsPourUnProfil(ProfilEleve.AvecProfilExistant(""))).willReturn(
            SuggestionsPourUnProfil(),
        )

        // When
        val resultat = getResultatsRecherche(scenario)

        // Then
        if (scenario.premierRésultatAttendu != null) {
            assert(resultat.formations.any { it.id == scenario.premierRésultatAttendu }) {
                "Le premier résultat de la recherche n'est pas conforme"
            }
        }
        if (scenario.résultatAttendusParmiLesCinqPremiers != null) {
            val cinqPremiersResultats = resultat.formations.take(5).map { it.id }.toSet()
            assert(
                cinqPremiersResultats.containsAll(scenario.résultatAttendusParmiLesCinqPremiers),
            ) {
                "Aucun des résultats attendus parmi les cinq premiers n'est présent"
            }
        }
    }

    private fun getResultatsRecherche(scenario: RechercheScenario): FormationsCourtesDTO {
        val requete =
            Gson().toJson(
                RechercheFormationsDTO(
                    recherche = scenario.recherche,
                    profil = null,
                    numeroDePage = 1,
                ),
            )
        val resultat =
            mvc.perform(
                post(ENDPOINT_RECHERCHE).accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
        val body = resultat.response.contentAsString
        return objectMapper.readValue(body, FormationsCourtesDTO::class.java)
    }
}
