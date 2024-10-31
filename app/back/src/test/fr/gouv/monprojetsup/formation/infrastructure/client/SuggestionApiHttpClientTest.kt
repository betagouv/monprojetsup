package fr.gouv.monprojetsup.formation.infrastructure.client

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.commun.helper.MockitoHelper
import fr.gouv.monprojetsup.eleve.domain.entity.VoeuFormation
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationGeographique
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionEtExemplesMetiers
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionEtExemplesMetiers.AffiniteSpecialite
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionEtExemplesMetiers.AutoEvaluationMoyenne
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionEtExemplesMetiers.TypeBaccalaureat
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil.FormationAvecSonAffinite
import fr.gouv.monprojetsup.formation.entity.Communes
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.referentiel.domain.entity.SituationAvanceeProjetSup
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
import org.mockito.BDDMockito.then
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
    lateinit var logger: MonProjetSupLogger

    @Captor
    lateinit var requeteCaptor: ArgumentCaptor<Request>

    private val objectMapper = ObjectMapper()

    private lateinit var suggestionApiHttpClient: SuggestionApiHttpClient

    private val unProfil =
        ProfilEleve.AvecProfilExistant(
            id = "adcf627c-36dd-4df5-897b-159443a6d49c",
            situation = SituationAvanceeProjetSup.PROJET_PRECIS,
            classe = ChoixNiveau.TERMINALE,
            baccalaureat = "Générale",
            dureeEtudesPrevue = ChoixDureeEtudesPrevue.INDIFFERENT,
            alternance = ChoixAlternance.PAS_INTERESSE,
            communesFavorites = listOf(Communes.PARIS15EME),
            specialites = listOf("mat1001", "mat1049"),
            centresInterets = listOf("T_ROME_2092381917", "T_IDEO2_4812"),
            moyenneGenerale = 14f,
            metiersFavoris = listOf("MET_123", "MET_456"),
            formationsFavorites =
                listOf(
                    VoeuFormation(
                        idFormation = "fl1234",
                        niveauAmbition = 1,
                        voeuxChoisis = emptyList(),
                        priseDeNote = null,
                    ),
                    VoeuFormation(
                        idFormation = "fl5678",
                        niveauAmbition = 3,
                        voeuxChoisis = listOf("ta1", "ta2"),
                        priseDeNote = "Mon voeu préféré",
                    ),
                ),
            domainesInterets = listOf("T_ITM_1054", "T_ITM_1534", "T_ITM_1248", "T_ITM_1351"),
            corbeilleFormations = listOf("fl0001"),
            compteParcoursupLie = true,
        )

    @BeforeEach
    fun before() {
        MockitoAnnotations.openMocks(this)
        suggestionApiHttpClient =
            SuggestionApiHttpClient(
                baseUrl = "http://localhost:8080",
                objectMapper = objectMapper,
                httpClient = httpClient,
                logger = logger,
            )
    }

    @Nested
    inner class RecupererLesSuggestions {
        @Test
        fun `doit retourner les AffinitesPourProfil avec les formations sans les trier par affinitées`() {
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
            val result = suggestionApiHttpClient.recupererLesSuggestions(unProfil)

            // Then
            val attendu =
                SuggestionsPourUnProfil(
                    formations =
                        listOf(
                            FormationAvecSonAffinite(idFormation = "fl2009", tauxAffinite = 0.7486587f),
                            FormationAvecSonAffinite(idFormation = "fr22", tauxAffinite = 0.7782054f),
                            FormationAvecSonAffinite(idFormation = "fl830", tauxAffinite = 0.3785463f),
                            FormationAvecSonAffinite(idFormation = "fl2016", tauxAffinite = 0.7217561f),
                            FormationAvecSonAffinite(idFormation = "fl2096", tauxAffinite = 0.49477f),
                            FormationAvecSonAffinite(idFormation = "fl877", tauxAffinite = 0.0f),
                            FormationAvecSonAffinite(idFormation = "fl2051", tauxAffinite = 0.4817011f),
                            FormationAvecSonAffinite(idFormation = "fl2089", tauxAffinite = 0.4567504f),
                            FormationAvecSonAffinite(idFormation = "fl2060", tauxAffinite = 0.3869467f),
                            FormationAvecSonAffinite(idFormation = "fl680002", tauxAffinite = 0.9f),
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
            assertThat(result.formations).isNotEmpty
            assertThat(result.metiersTriesParAffinites).isNotEmpty
            assertThat(result.metiersTriesParAffinites).contains("MET_611")
            assertThat(result.formations.map { f -> f.idFormation }).contains("fr22")
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

            // When
            suggestionApiHttpClient.recupererLesSuggestions(unProfil)

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
                          "75115"
                        ],
                        "spe_classes": [
                          "mat1001",
                          "mat1049"
                        ],
                        "interests": [
                          "T_ROME_2092381917",
                          "T_IDEO2_4812",
                          "T_ITM_1054",
                          "T_ITM_1534",
                          "T_ITM_1248",
                          "T_ITM_1351"
                        ],
                        "moygen": "28",
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
                          },
                          {
                            "fl": "fl0001",
                            "status": 2,
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
                suggestionApiHttpClient.recupererLesSuggestions(unProfil)
            }.isEqualTo(
                MonProjetSupInternalErrorException(
                    code = "ERREUR_APPEL_API",
                    msg = "Erreur lors de la connexion à l'API à l'url http://localhost:8080/suggestions",
                    origine = exception,
                ),
            )
        }

        @Test
        fun `si la désérialisation échoue, doit throw MonProjetSupInternalErrorException`() {
            // Given
            val url = "http://localhost:8080/suggestions"
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val stringBody =
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
                """.trimIndent()
            val reponseBody = stringBody.toResponseBody(mediaType)
            val callMock = mock(Call::class.java)
            given(httpClient.newCall(MockitoHelper.anyObject())).willReturn(callMock)
            val reponse =
                Response.Builder().code(200).message("OK").protocol(Protocol.HTTP_1_1)
                    .request(Request.Builder().url(url).build())
                    .body(reponseBody).build()
            given(callMock.execute()).willReturn(reponse)

            // When & Then
            assertThatThrownBy {
                suggestionApiHttpClient.recupererLesSuggestions(unProfil)
            }.isInstanceOf(MonProjetSupInternalErrorException::class.java)
                .hasMessage(
                    "Erreur lors de la désérialisation de la réponse de l'API à l'url " +
                        "http://localhost:8080/suggestions pour le body suivant : $stringBody",
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
                            "moy": 29,
                            "middle50": {
                              "rangEch10": 20,
                              "rangEch25": 27,
                              "rangEch50": 31,
                              "rangEch75": 33,
                              "rangEch90": 35
                            },
                            "bacUtilise": "Général"
                          }
                        },
                        {
                          "spec": {
                            "stats": [
                              {
                                "spe": "mat001",
                                "pct": 12
                              },
                              {
                                "spe": "mat002",
                                "pct": 10
                              },
                              {
                                "spe": "mat003",
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
                              "MET.612",
                              "T_IDEO2_4812",
                              "MET.483",
                              "T_ROME_803089798",
                              "MET.461"
                            ]
                          }
                        },
                        {
                          "debug": {
                            "expl": "Pas de stats spécialités pour cette filiere"
                          }
                        },
                        {
                          "debug": {
                            "expl": "Pas de stats pour cette filiere"
                          }
                        },
                        {
                          "debug": {
                            "expl": "preférences apprentissage 1 * (1 - 1E-5) + 1E-5"
                          }
                        },
                        {
                          "debug": {
                            "expl": "moyenne générale 1 * (1 - 1E-1) + 1E-1"
                          }
                        },
                        {
                          "debug": {
                            "expl": "EDS 0.5 * (1 - 1E-1) + 1E-1"
                          }
                        },
                        {
                          "debug": {
                            "expl": "type de bac 0.01 * (1 - 1E-8) + 1E-8"
                          }
                        },
                        {
                          "debug": {
                            "expl": "durée 0 * (1 - 1E-4) + 1E-4"
                          }
                        },
                        {
                          "debug": {
                            "expl": "préférences géographiques 0 * (1 - 1E-4) + 1E-4"
                          }
                        },
                        {
                          "debug": {
                            "expl": "similarité avec autres favoris 0 * (1 - 1E-3) + 1E-3"
                          }
                        },
                        {
                          "debug": {
                            "expl": "proximité intérêts et favoris 0 * (1 - 1E-8) + 1E-8"
                          }
                        },
                        {
                          "debug": {
                            "expl": "Scores de diversité: {OFFRE_FORMATION=0.0}"
                          }
                        },
                        {
                          "debug": {
                            "expl": "Score Total pour fl210 :5.5E-22 obtenu comme le produit de [  1 (preférences apprentissage) ,  1 (moyenne générale) ,  0.55 (EDS) ,  0.01 (type de bac) ,  0.0001 (durée) ,  0.0001 (préférences géographiques) ,  0.001 (similarité avec autres favoris) ,  0 (proximité intérêts et favoris) ,  ]"
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
            val result = suggestionApiHttpClient.recupererLesExplications(unProfil, listOf("fl2014"))

            // Then
            val attendu =
                mapOf(
                    "fl2014" to
                        ExplicationsSuggestionEtExemplesMetiers(
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
                                    AffiniteSpecialite(idSpecialite = "mat001", pourcentage = 12),
                                    AffiniteSpecialite(idSpecialite = "mat002", pourcentage = 10),
                                    AffiniteSpecialite(idSpecialite = "mat003", pourcentage = 89),
                                ),
                            typeBaccalaureat =
                                TypeBaccalaureat(
                                    nomBaccalaureat = "Général",
                                    pourcentage = 18,
                                ),
                            detailsCalculScore =
                                listOf(
                                    "Pas de stats spécialités pour cette filiere",
                                    "Pas de stats pour cette filiere",
                                    "preférences apprentissage 1 * (1 - 1E-5) + 1E-5",
                                    "moyenne générale 1 * (1 - 1E-1) + 1E-1",
                                    "EDS 0.5 * (1 - 1E-1) + 1E-1",
                                    "type de bac 0.01 * (1 - 1E-8) + 1E-8",
                                    "durée 0 * (1 - 1E-4) + 1E-4",
                                    "préférences géographiques 0 * (1 - 1E-4) + 1E-4",
                                    "similarité avec autres favoris 0 * (1 - 1E-3) + 1E-3",
                                    "proximité intérêts et favoris 0 * (1 - 1E-8) + 1E-8",
                                    "Scores de diversité: {OFFRE_FORMATION=0.0}",
                                    "Score Total pour fl210 :5.5E-22 obtenu comme le produit de [  1 (preférences apprentissage) ,  " +
                                        "1 (moyenne générale) ,  0.55 (EDS) ,  0.01 (type de bac) ,  0.0001 (durée) ,  " +
                                        "0.0001 (préférences géographiques) ,  0.001 (similarité avec autres favoris) ,  " +
                                        "0 (proximité intérêts et favoris) ,  ]",
                                ),
                            autoEvaluationMoyenne =
                                AutoEvaluationMoyenne(
                                    echellonDeLaMoyenneAutoEvalue = 29,
                                    rangs =
                                        ExplicationsSuggestionEtExemplesMetiers.RangsEchellons(
                                            rangEch10 = 20,
                                            rangEch25 = 27,
                                            rangEch50 = 31,
                                            rangEch75 = 33,
                                            rangEch90 = 35,
                                        ),
                                    baccalaureatUtilise = "Général",
                                ),
                            interetsDomainesMetiersChoisis =
                                listOf(
                                    "T_ROME_731379930",
                                    "MET.612",
                                    "T_IDEO2_4812",
                                    "MET.483",
                                    "T_ROME_803089798",
                                    "MET.461",
                                ),
                            exemplesDeMetiers =
                                listOf(
                                    "MET_361",
                                    "MET_592",
                                    "MET_871",
                                    "MET_342",
                                    "MET_884",
                                    "MET_634",
                                ),
                        ),
                )
            assertThat(result).isEqualTo(attendu)
        }

        @Test
        fun `doit retourner les explications pour les formations données avec à null les données manquantes`() {
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
            val result = suggestionApiHttpClient.recupererLesExplications(unProfil, listOf("fl2014", "fl2015"))

            // Then
            val attendu =
                mapOf(
                    "fl2014" to
                        ExplicationsSuggestionEtExemplesMetiers(
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
                            interetsDomainesMetiersChoisis =
                                listOf(
                                    "T_ROME_731379930",
                                    "T_IDEO2_4812",
                                    "T_ROME_803089798",
                                ),
                            exemplesDeMetiers =
                                listOf(
                                    "MET_361",
                                    "MET_592",
                                    "MET_871",
                                    "MET_342",
                                    "MET_884",
                                    "MET_634",
                                ),
                        ),
                    "fl2015" to
                        ExplicationsSuggestionEtExemplesMetiers(
                            dureeEtudesPrevue = ChoixDureeEtudesPrevue.LONGUE,
                            exemplesDeMetiers =
                                listOf(
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
                                    "MET_557",
                                ),
                        ),
                )
            assertThat(result).usingRecursiveComparison().isEqualTo(attendu)
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

            // When
            suggestionApiHttpClient.recupererLesExplications(unProfil, listOf("fl2014"))

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
                          "75115"
                        ],
                        "spe_classes": [
                          "mat1001",
                          "mat1049"
                        ],
                        "interests": [
                          "T_ROME_2092381917",
                          "T_IDEO2_4812",
                          "T_ITM_1054",
                          "T_ITM_1534",
                          "T_ITM_1248",
                          "T_ITM_1351"
                        ],
                        "moygen": "28",
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
                          },
                          {
                            "fl": "fl0001",
                            "status": 2,
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
        fun `si une formation n'est pas dans la liste retournée, doit log une erreur et renvoyer la liste avec ses explications à null`() {
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
            val resultat = suggestionApiHttpClient.recupererLesExplications(unProfil, listOf("fl1"))

            // Then
            assertThat(resultat).isEqualTo(
                mapOf(
                    "fl1" to null,
                ),
            )
            then(
                logger,
            ).should().error(
                type = "FORMATIONS_SANS_EXPLICATIONS",
                message =
                    "Les formations [fl1] n'ont pas d'explications renvoyées par l'API suggestion pour le profil " +
                        "élève avec l'id adcf627c-36dd-4df5-897b-159443a6d49c",
                parametres = mapOf("formationsSansExplications" to listOf("fl1"), "idEleve" to "adcf627c-36dd-4df5-897b-159443a6d49c"),
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
                suggestionApiHttpClient.recupererLesExplications(unProfil, listOf("fl1"))
            }.isEqualTo(
                MonProjetSupInternalErrorException(
                    code = "ERREUR_APPEL_API",
                    msg = "Erreur lors de la connexion à l'API à l'url http://localhost:8080/explanations",
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
                suggestionApiHttpClient.recupererLesExplications(unProfil, listOf("fl1"))
            }.isInstanceOf(MonProjetSupInternalErrorException::class.java)
                .hasMessage(
                    "Erreur lors de la désérialisation de la réponse de l'API à l'url " +
                        "http://localhost:8080/explanations pour le body suivant : {\n" +
                        "  \"header\": {\n" +
                        "    \"status\": 0\n" +
                        "  }\n" +
                        "}",
                )
        }
    }
}
