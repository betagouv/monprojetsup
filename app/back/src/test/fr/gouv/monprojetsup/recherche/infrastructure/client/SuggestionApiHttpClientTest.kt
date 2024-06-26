package fr.gouv.monprojetsup.recherche.infrastructure.client

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetIllegalStateErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.commun.helper.MockitoHelper
import fr.gouv.monprojetsup.recherche.domain.entity.AffiniteSpecialite
import fr.gouv.monprojetsup.recherche.domain.entity.AffinitesPourProfil
import fr.gouv.monprojetsup.recherche.domain.entity.AffinitesPourProfil.FormationAvecSonAffinite
import fr.gouv.monprojetsup.recherche.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.recherche.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.recherche.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.recherche.domain.entity.ExplicationGeographique
import fr.gouv.monprojetsup.recherche.domain.entity.ExplicationsSuggestion
import fr.gouv.monprojetsup.recherche.domain.entity.ExplicationsSuggestion.AutoEvaluationMoyenne
import fr.gouv.monprojetsup.recherche.domain.entity.ExplicationsSuggestion.TypeBaccalaureat
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
import org.junit.jupiter.api.Nested
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
            classe = ChoixNiveau.TERMINALE,
            bac = "Générale",
            dureeEtudesPrevue = ChoixDureeEtudesPrevue.OPTIONS_OUVERTES,
            alternance = ChoixAlternance.PAS_INTERESSE,
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

    @Nested
    inner class RecupererLesAffinitees {
        @Test
        fun `doit retourner les AffinitesPourProfil avec les formations triées par affinitées`() {
            // Given
            val url = "http://localhost:8080/suggestions"
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
        fun `doit envoyer la bonne requete`() {
            // Given
            val url = "http://localhost:8080/suggestions"
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
            assertThat(requeteCaptor.value.url.toString()).isEqualTo("http://localhost:8080/suggestions")

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
        fun `si l'appel échoue, doit throw MonProjetSupInternalErrorException`() {
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
                    msg = "Erreur lors de la connexion à l'API de suggestions à l'url http://localhost:8080/suggestions",
                    origine = exception,
                ),
            )
        }

        @Test
        fun `si la désérialisation échoue, doit throw MonProjetSupInternalErrorException`() {
            // Given
            val url = "http://localhost:8080/suggestions"
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
                .hasMessage(
                    "Erreur lors de la désérialisation de la réponse de l'API de suggestions pour la classe " +
                        "fr.gouv.monprojetsup.recherche.infrastructure.dto.AffinitesProfilReponseDTO",
                )
        }
    }

    @Nested
    inner class RecupererLesExplications {
        @Test
        fun `doit retourner les explications pour la formation donnée`() {
            // Given
            val url = "http://localhost:8080/explanations"
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val reponseBody =
                """
                {
                  "header": {
                    "status": 0
                  },
                  "liste": [
                    {
                      "key": "fl2014",
                      "explanations": [
                        {
                          "dur": {
                            "option": "long"
                          }
                        },
                        {
                          "app": {
                            "option": "A"
                          }
                        },
                        {
                          "simi": {
                            "fl": "fl1",
                            "p": 0.15
                          }
                        },
                        {
                          "simi": {
                            "fl": "fl7",
                            "p": 0.30
                          }
                        },
                        {
                          "tbac": {
                            "percentage": 18,
                            "bac": "Général"
                          }
                        },
                        {
                          "moygen": {
                            "moy": 14.5,
                            "middle50": {
                              "rangEch10": 10,
                              "rangEch25": 12,
                              "rangEch50": 14,
                              "rangEch75": 16,
                              "rangEch90": 17
                            },
                            "bacUtilise": "Général"
                          }
                        },
                        {
                          "spec": {
                            "stats": [
                              {
                                "spe": "specialiteA",
                                "pct": 12
                              },
                              {
                                "spe": "specialiteB",
                                "pct": 10
                              },
                              {
                                "spe": "specialiteC",
                                "pct": 89
                              }
                            ]
                          }
                        },
                        {
                          "geo": [
                            {
                              "distance": 1,
                              "city": "Nantes",
                              "form": "ta5580"
                            },
                            {
                              "distance": 85,
                              "city": "Nantes",
                              "form": "ta5888"
                            }
                          ]
                        },
                        {
                          "geo": [
                            {
                              "distance": 20,
                              "city": "Paris",
                              "form": "ta5589"
                            },
                            {
                              "distance": 25,
                              "city": "Paris",
                              "form": "ta5890"
                            }
                          ]
                        },
                        {
                          "tags": {
                            "ns": [
                              "T_ROME_731379930",
                              "T_IDEO2_4812",
                              "T_ROME_803089798"
                            ]
                          }
                        }
                      ],
                      "examples": [
                        "MET_361",
                        "MET_592",
                        "MET_871",
                        "MET_342",
                        "MET_884",
                        "MET_634"
                      ]
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

            // When
            val result = suggestionApiHttpClient.recupererLesExplications(unProfil, "fl2014")

            // Then
            val attendu =
                ExplicationsSuggestion(
                    geographique =
                        listOf(
                            ExplicationGeographique(
                                ville = "Nantes",
                                distanceKm = 1,
                            ),
                            ExplicationGeographique(
                                ville = "Nantes",
                                distanceKm = 85,
                            ),
                            ExplicationGeographique(
                                ville = "Paris",
                                distanceKm = 20,
                            ),
                            ExplicationGeographique(
                                ville = "Paris",
                                distanceKm = 25,
                            ),
                        ),
                    formationsSimilaires = listOf("fl1", "fl7"),
                    dureeEtudesPrevue = ChoixDureeEtudesPrevue.LONGUE,
                    alternance = ChoixAlternance.TRES_INTERESSE,
                    specialitesChoisies =
                        listOf(
                            AffiniteSpecialite("specialiteA", 12),
                            AffiniteSpecialite("specialiteB", 10),
                            AffiniteSpecialite("specialiteC", 89),
                        ),
                    typeBaccalaureat =
                        TypeBaccalaureat(
                            nomBaccalaureat = "Général",
                            pourcentage = 18,
                        ),
                    autoEvaluationMoyenne =
                        AutoEvaluationMoyenne(
                            moyenneAutoEvalue = 14.5f,
                            rangs =
                                ExplicationsSuggestion.RangsEchellons(
                                    rangEch25 = 12,
                                    rangEch50 = 14,
                                    rangEch75 = 16,
                                    rangEch10 = 10,
                                    rangEch90 = 17,
                                ),
                            bacUtilise = "Général",
                        ),
                    interetsEtDomainesChoisis =
                        listOf(
                            "T_ROME_731379930",
                            "T_IDEO2_4812",
                            "T_ROME_803089798",
                        ),
                )
            assertThat(result).isEqualTo(attendu)
        }

        @Test
        fun `doit retourner les explications pour la formation donnée avec a null les données manquantes`() {
            // Given
            val url = "http://localhost:8080/explanations"
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val reponseBody =
                """
                {
                  "header": {
                    "status": 0
                  },
                  "liste": [
                    {
                      "key": "fl2014",
                      "explanations": [
                        {
                          "dur": {
                            "option": "long"
                          }
                        },
                        {
                          "geo": [
                            {
                              "distance": 1,
                              "city": "Nantes",
                              "form": "ta5580"
                            },
                            {
                              "distance": 85,
                              "city": "Nantes",
                              "form": "ta5888"
                            }
                          ]
                        },
                        {
                          "tags": {
                            "ns": [
                              "T_ROME_731379930",
                              "T_IDEO2_4812",
                              "T_ROME_803089798"
                            ]
                          }
                        }
                      ],
                      "examples": [
                        "MET_361",
                        "MET_592",
                        "MET_871",
                        "MET_342",
                        "MET_884",
                        "MET_634"
                      ]
                    },
                    {
                      "key": "fl2015",
                      "explanations": [
                        {
                          "dur": {
                            "option": "long"
                          }
                        }
                      ],
                      "examples": [
                        "MET_639",
                        "MET_292",
                        "MET_890",
                        "MET_277",
                        "MET_51",
                        "MET_85",
                        "MET_423",
                        "MET_43",
                        "MET_278",
                        "MET_431",
                        "MET_557"
                      ]
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

            // When
            val result = suggestionApiHttpClient.recupererLesExplications(unProfil, "fl2014")

            // Then
            val attendu =
                ExplicationsSuggestion(
                    geographique =
                        listOf(
                            ExplicationGeographique(
                                ville = "Nantes",
                                distanceKm = 1,
                            ),
                            ExplicationGeographique(
                                ville = "Nantes",
                                distanceKm = 85,
                            ),
                        ),
                    dureeEtudesPrevue = ChoixDureeEtudesPrevue.LONGUE,
                    interetsEtDomainesChoisis = listOf("T_ROME_731379930", "T_IDEO2_4812", "T_ROME_803089798"),
                )
            assertThat(result).isEqualTo(attendu)
        }

        @Test
        fun `doit envoyer la bonne requête`() {
            // Given
            val url = "http://localhost:8080/explanations"
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val reponseBody =
                """
                {
                  "header": {
                    "status": 0
                  },
                  "liste": [
                    {
                      "key": "fl2014",
                      "explanations": [
                        {
                          "dur": {
                            "option": "long"
                          }
                        },
                        {
                          "geo": [
                            {
                              "distance": 1,
                              "city": "Nantes",
                              "form": "ta5580"
                            },
                            {
                              "distance": 85,
                              "city": "Nantes",
                              "form": "ta5888"
                            }
                          ]
                        },
                        {
                          "tags": {
                            "ns": [
                              "T_ROME_731379930",
                              "T_IDEO2_4812",
                              "T_ROME_803089798"
                            ]
                          }
                        }
                      ],
                      "examples": [
                        "MET_361",
                        "MET_592",
                        "MET_871",
                        "MET_342",
                        "MET_884",
                        "MET_634"
                      ]
                    },
                    {
                      "key": "fl2015",
                      "explanations": [
                        {
                          "dur": {
                            "option": "long"
                          }
                        }
                      ],
                      "examples": [
                        "MET_639",
                        "MET_292",
                        "MET_890",
                        "MET_277",
                        "MET_51",
                        "MET_85",
                        "MET_423",
                        "MET_43",
                        "MET_278",
                        "MET_431",
                        "MET_557"
                      ]
                    }
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
            suggestionApiHttpClient.recupererLesExplications(unProfil, "fl2014")

            // Then
            assertThat(requeteCaptor.value.url.toString()).isEqualTo("http://localhost:8080/explanations")

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
                      },
                      "keys":["fl2014"]
                    }
                    """.trimIndent(),
                    JsonNode::class.java,
                )
            assertThat(requete).isEqualTo(requeteAttendue)
        }

        @Test
        fun `si la formation n'est pas dans la liste retournée, doit throw MonProjetIllegalStateErrorException`() {
            // Given
            val url = "http://localhost:8080/explanations"
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val reponseBody =
                """
                {
                  "header": {
                    "status": 0
                  },
                  "liste": [
                    {
                      "key": "fl2014",
                      "explanations": [
                        {
                          "dur": {
                            "option": "long"
                          }
                        },
                        {
                          "geo": [
                            {
                              "distance": 1,
                              "city": "Nantes",
                              "form": "ta5580"
                            },
                            {
                              "distance": 85,
                              "city": "Nantes",
                              "form": "ta5888"
                            }
                          ]
                        },
                        {
                          "tags": {
                            "ns": [
                              "T_ROME_731379930",
                              "T_IDEO2_4812",
                              "T_ROME_803089798"
                            ]
                          }
                        }
                      ],
                      "examples": [
                        "MET_361",
                        "MET_592",
                        "MET_871",
                        "MET_342",
                        "MET_884",
                        "MET_634"
                      ]
                    },
                    {
                      "key": "fl2015",
                      "explanations": [
                        {
                          "dur": {
                            "option": "long"
                          }
                        }
                      ],
                      "examples": [
                        "MET_639",
                        "MET_292",
                        "MET_890",
                        "MET_277",
                        "MET_51",
                        "MET_85",
                        "MET_423",
                        "MET_43",
                        "MET_278",
                        "MET_431",
                        "MET_557"
                      ]
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
                suggestionApiHttpClient.recupererLesExplications(unProfil, "fl1")
            }.isEqualTo(
                MonProjetIllegalStateErrorException(
                    code = "ERREUR_API_SUGGESTIONS_EXPLANATION",
                    msg = "La clé de la formation fl1 n'est pas présente dans la liste retournée par l'API suggestion",
                ),
            )
        }

        @Test
        fun `si l'appel échoue, doit throw MonProjetSupInternalErrorException`() {
            // Given
            val callMock = mock(Call::class.java)
            given(httpClient.newCall(MockitoHelper.anyObject())).willReturn(callMock)
            val exception = ConnectException("Erreur de connexion")
            given(callMock.execute()).willThrow(exception)

            // When & Then
            assertThatThrownBy {
                suggestionApiHttpClient.recupererLesExplications(unProfil, "fl1")
            }.isEqualTo(
                MonProjetSupInternalErrorException(
                    code = "ERREUR_API_SUGGESTIONS_CONNEXION",
                    msg = "Erreur lors de la connexion à l'API de suggestions à l'url http://localhost:8080/explanations",
                    origine = exception,
                ),
            )
        }

        @Test
        fun `si la désérialisation échoue, doit throw MonProjetSupInternalErrorException`() {
            // Given
            val url = "http://localhost:8080/explanations"
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val reponseBody =
                """
                {
                  "header": {
                    "status": 0
                  }
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
                suggestionApiHttpClient.recupererLesExplications(unProfil, "fl1")
            }.isInstanceOf(MonProjetSupInternalErrorException::class.java)
                .hasMessage(
                    "Erreur lors de la désérialisation de la réponse de l'API de suggestions pour la classe " +
                        "fr.gouv.monprojetsup.recherche.infrastructure.dto.ExplicationFormationPourUnProfilReponseDTO",
                )
        }
    }
}
