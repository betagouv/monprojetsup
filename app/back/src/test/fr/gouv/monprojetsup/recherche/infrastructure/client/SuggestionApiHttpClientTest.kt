package fr.gouv.monprojetsup.recherche.infrastructure.client

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.commun.helper.MockitoHelper
import fr.gouv.monprojetsup.recherche.domain.entity.AffinitesPourProfil
import fr.gouv.monprojetsup.recherche.domain.entity.FormationAvecSonAffinite
import fr.gouv.monprojetsup.recherche.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.recherche.domain.entity.Specialite
import fr.gouv.monprojetsup.recherche.domain.port.SpecialitesRepository
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.BDDMockito.given
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.net.ConnectException

class SuggestionApiHttpClientTest {
    @Mock
    lateinit var httpClient: OkHttpClient

    @Mock
    lateinit var specialitesRepository: SpecialitesRepository

    @Captor
    lateinit var requeteCaptor: ArgumentCaptor<Request>

    private val objectMapper = ObjectMapper()

    private lateinit var suggestionApiHttpClient: SuggestionApiHttpClient

    private val unProfil =
        ProfilEleve(
            id = "adcf627c-36dd-4df5-897b-159443a6d49c",
            classe = "terminale",
            bac = "Générale",
            dureeEtudesPrevue = "options_ouvertes",
            alternance = "pas_interesse",
            villesPreferees = listOf("Paris"),
            specialites = listOf("1001", "1049"),
            centresInterets = listOf("T_ROME_2092381917", "T_IDEO2_4812"),
            moyenneGenerale = 14f,
            metiersChoisis = listOf("MET_123", "MET_456"),
            formationsChoisies = listOf("fl1234", "fl5678"),
            domainesInterets = listOf("T_ITM_1054", "T_ITM_1534", "T_ITM_1248", "T_ITM_1351"),
        )

    @BeforeEach
    fun before() {
        MockitoAnnotations.openMocks(this)
        suggestionApiHttpClient =
            SuggestionApiHttpClient(
                baseUrl = "http://localhost:8080",
                objectMapper = objectMapper,
                httpClient = httpClient,
                specialitesRepository = specialitesRepository,
            )
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
            Response.Builder().code(200).message("OK").protocol(Protocol.HTTP_1_1)
                .request(Request.Builder().url(url).build())
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
    fun `recupererLesAffinitees - doit envoyer la bonne requete`() {
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
        `when`(httpClient.newCall(MockitoHelper.capture(requeteCaptor))).thenReturn(callMock)
        val reponse =
            Response.Builder().code(200).message("OK").protocol(Protocol.HTTP_1_1)
                .request(Request.Builder().url(url).build())
                .body(reponseBody).build()
        given(callMock.execute()).willReturn(reponse)
        given(specialitesRepository.recupererLesSpecialites(listOf("1001", "1049")))
            .willReturn(
                listOf(
                    Specialite(
                        id = "1001",
                        label = "Sciences de la vie et de la Terre",
                    ),
                    Specialite(
                        id = "1049",
                        label = "Mathématiques",
                    ),
                ),
            )

        // When
        suggestionApiHttpClient.recupererLesAffinitees(unProfil)

        // Then
        assertThat(requeteCaptor.value.url.toString()).isEqualTo("http://localhost:8080/affinites")

        val buffer = Buffer()
        requeteCaptor.value.body?.writeTo(buffer)
        val requete = objectMapper.readValue(buffer.readUtf8(), JsonNode::class.java)
        val requeteAttendue =
            objectMapper.readValue(
                """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "Générale",
                    "duree": "indiff",
                    "apprentissage": "D",
                    "geo_pref": [
                      "Paris"
                    ],
                    "spe_classes": [
                      "Sciences de la vie et de la Terre",
                      "Mathématiques"
                    ],
                    "interests": [
                      "T_ROME_2092381917",
                      "T_IDEO2_4812",
                      "T_ITM_1054",
                      "T_ITM_1534",
                      "T_ITM_1248",
                      "T_ITM_1351"
                    ],
                    "moygen": "14.0",
                    "choices": [
                      {
                        "fl": "MET_123",
                        "status": 1,
                        "date": null
                      },
                      {
                        "fl": "MET_456",
                        "status": 1,
                        "date": null
                      },
                      {
                        "fl": "fl1234",
                        "status": 1,
                        "date": null
                      },
                      {
                        "fl": "fl5678",
                        "status": 1,
                        "date": null
                      }
                    ]
                  }
                }
                """.trimIndent(),
                JsonNode::class.java,
            )
        assertThat(requete).isEqualTo(requeteAttendue)
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
            Response.Builder().code(200).message("OK").protocol(Protocol.HTTP_1_1)
                .request(Request.Builder().url(url).build())
                .body(reponseBody).build()
        given(callMock.execute()).willReturn(reponse)

        // When & Then
        assertThatThrownBy {
            suggestionApiHttpClient.recupererLesAffinitees(unProfil)
        }.isInstanceOf(MonProjetSupInternalErrorException::class.java)
            .hasMessage("Erreur lors de la désérialisation de la réponse de l'API de suggestions")
    }
}
