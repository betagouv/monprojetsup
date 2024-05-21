package fr.gouv.monprojetsup.recherche.infrastructure.client

import com.fasterxml.jackson.databind.ObjectMapper
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.commun.helper.MockitoHelper
import fr.gouv.monprojetsup.recherche.domain.entity.AffinitesPourProfil
import fr.gouv.monprojetsup.recherche.domain.entity.FormationAvecSonAffinite
import fr.gouv.monprojetsup.recherche.domain.entity.Profile
import fr.gouv.monprojetsup.recherche.domain.entity.Suggestion
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import java.net.ConnectException

class SuggestionApiHttpClientTest {
    @Mock
    lateinit var httpClient: OkHttpClient

    private val objectMapper = ObjectMapper()

    lateinit var suggestionApiHttpClient: SuggestionApiHttpClient

    private val unProfil =
        Profile(
            niveau = "term",
            bac = "Générale",
            duree = "long",
            apprentissage = "C",
            preferencesGeographique = listOf("Soulac-sur-Mer", "Nantes"),
            specialites = listOf("Sciences de la vie et de la Terre", "Mathématiques"),
            interets =
                listOf(
                    "T_ITM_1054",
                    "T_ITM_1534",
                    "T_ITM_1248",
                    "T_ITM_1351",
                    "T_ROME_2092381917",
                    "T_IDEO2_4812",
                ),
            moyenneGenerale = 14f,
            choix =
                listOf(
                    Suggestion(
                        fl = "fl2014",
                        status = 1,
                        date = "string",
                    ),
                ),
        )

    @BeforeEach
    fun before() {
        MockitoAnnotations.openMocks(this)
        suggestionApiHttpClient = SuggestionApiHttpClient(httpClient, "http://localhost:8080", objectMapper)
    }

    @Test
    fun `recupererLesAffinitees - doit retourner les AffinitesPourProfil avec les formations triées par affinitées`() {
        // Given
        val url = "http://localhost:8080/affinites"
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val reponseBody =
            """
            {
              "header": {
                "status": 0
              },
              "affinites": [
                {
                  "key": "fl2009",
                  "affinite": 0.7486587
                },
                {
                  "key": "fr22",
                  "affinite": 0.7782054
                },
                {
                  "key": "fl830",
                  "affinite": 0.3785463
                },
                {
                  "key": "fl2016",
                  "affinite": 0.7217561
                },
                {
                  "key": "fl2096",
                  "affinite": 0.49477
                },
                {
                  "key": "fl877",
                  "affinite": 0
                },
                {
                  "key": "fl2051",
                  "affinite": 0.4817011
                },
                {
                  "key": "fl2089",
                  "affinite": 0.4567504
                },
                {
                  "key": "fl2060",
                  "affinite": 0.3869467
                },
                {
                  "key": "fl680002",
                  "affinite": 0.9
                }
              ],
              "metiers": [
                "MET_611",
                "MET_610",
                "MET_613",
                "MET_628",
                "MET_192",
                "MET_104",
                "MET_984",
                "MET_7772",
                "MET_409",
                "MET_1164",
                "MET_1166",
                "MET_1165",
                "MET_1168",
                "MET_1167",
                "MET_1169",
                "MET_262",
                "MET_194",
                "MET_178",
                "MET_655",
                "MET_654",
                "MET_420",
                "MET_630"
              ]
            }
            """.trimIndent().toResponseBody(mediaType)
        val callMock = mock(Call::class.java)
        given(httpClient.newCall(MockitoHelper.anyObject())).willReturn(callMock)
        val reponse =
            Response.Builder().code(200).message("OK").protocol(Protocol.HTTP_1_1).request(Request.Builder().url(url).build())
                .body(reponseBody).build()
        given(callMock.execute()).willReturn(reponse)

        // When
        val result = suggestionApiHttpClient.recupererLesAffinitees(unProfil)

        // Then
        val attendu =
            AffinitesPourProfil(
                formations =
                    listOf(
                        FormationAvecSonAffinite(idFormation = "fl680002", tauxAffinite = 0.9f),
                        FormationAvecSonAffinite(idFormation = "fr22", tauxAffinite = 0.7782054f),
                        FormationAvecSonAffinite(idFormation = "fl2009", tauxAffinite = 0.7486587f),
                        FormationAvecSonAffinite(idFormation = "fl2016", tauxAffinite = 0.7217561f),
                        FormationAvecSonAffinite(idFormation = "fl2096", tauxAffinite = 0.49477f),
                        FormationAvecSonAffinite(idFormation = "fl2051", tauxAffinite = 0.4817011f),
                        FormationAvecSonAffinite(idFormation = "fl2089", tauxAffinite = 0.4567504f),
                        FormationAvecSonAffinite(idFormation = "fl2060", tauxAffinite = 0.3869467f),
                        FormationAvecSonAffinite(idFormation = "fl830", tauxAffinite = 0.3785463f),
                        FormationAvecSonAffinite(idFormation = "fl877", tauxAffinite = 0.0f),
                    ),
                metiersTriesParAffinites =
                    listOf(
                        "MET_611",
                        "MET_610",
                        "MET_613",
                        "MET_628",
                        "MET_192",
                        "MET_104",
                        "MET_984",
                        "MET_7772",
                        "MET_409",
                        "MET_1164",
                        "MET_1166",
                        "MET_1165",
                        "MET_1168",
                        "MET_1167",
                        "MET_1169",
                        "MET_262",
                        "MET_194",
                        "MET_178",
                        "MET_655",
                        "MET_654",
                        "MET_420",
                        "MET_630",
                    ),
            )
        assertThat(result).isEqualTo(attendu)
    }

    @Test
    fun `recupererLesAffinitees - si l'appel échoue, doit throw MonProjetSupInternalErrorException`() {
        // Given
        val callMock = mock(Call::class.java)
        given(httpClient.newCall(MockitoHelper.anyObject())).willReturn(callMock)
        val exception = ConnectException("Erreur de connexion")
        given(callMock.execute()).willThrow(exception)

        // When & Then
        assertThatThrownBy {
            suggestionApiHttpClient.recupererLesAffinitees(unProfil)
        }.isEqualTo(
            MonProjetSupInternalErrorException(
                code = "ERREUR_API_SUGGESTIONS_CONNEXION",
                msg = "Erreur lors de la connexion à l'API de suggestions",
                origine = exception,
            ),
        )
    }

    @Test
    fun `recupererLesAffinitees - si la désérialisation échoue, doit throw MonProjetSupInternalErrorException`() {
        // Given
        val url = "http://localhost:8080/affinites"
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val reponseBody =
            """
            {
              "header": {
                "status": 0
              },
              "affinites": [
                {
                  "key": "fl2009",
                  "affinite": 0.7486587
                },
                {
                  "key": "fr22",
                  "affinite": 0.7782054
                },
                {
                  "key": "fl830",
                  "affinite": 0.3785463
                },
                {
                  "key": "fl2016",
                  "affinite": 0.7217561
                },
                {
                  "key": "fl2096",
                  "affinite": 0.49477
                },
                {
                  "key": "fl877",
                  "affinite": 0
                },
                {
                  "key": "fl2051",
                  "affinite": 0.4817011
                },
                {
                  "key": "fl2089",
                  "affinite": 0.4567504
                },
                {
                  "key": "fl2060",
                  "affinite": 0.3869467
                },
                {
                  "key": "fl680002",
                  "affinite": 0.9
                }
              ]
            }
            """.trimIndent().toResponseBody(mediaType)
        val callMock = mock(Call::class.java)
        given(httpClient.newCall(MockitoHelper.anyObject())).willReturn(callMock)
        val reponse =
            Response.Builder().code(200).message("OK").protocol(Protocol.HTTP_1_1).request(Request.Builder().url(url).build())
                .body(reponseBody).build()
        given(callMock.execute()).willReturn(reponse)

        // When & Then
        assertThatThrownBy {
            suggestionApiHttpClient.recupererLesAffinitees(unProfil)
        }.isInstanceOf(MonProjetSupInternalErrorException::class.java)
            .hasMessage("Erreur lors de la désérialisation de la réponse de l'API de suggestions")
    }
}
