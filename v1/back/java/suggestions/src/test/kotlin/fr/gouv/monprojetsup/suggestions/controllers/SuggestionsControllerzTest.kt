import fr.gouv.monprojetsup.suggestions.ApplicationSuggestions
import fr.gouv.monprojetsup.suggestions.controllers.SuggestionsControllerz
import fr.gouv.monprojetsup.suggestions.security.SecurityConfig
import fr.gouv.monprojetsup.suggestions.server.SuggestionServer
import fr.gouv.monprojetsup.suggestions.services.GetExplanationsAndExamplesService
import fr.gouv.monprojetsup.suggestions.services.GetFormationsOfInterestService
import fr.gouv.monprojetsup.suggestions.services.GetSuggestionsService
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ContextConfiguration(
    classes = [
        SecurityConfig::class,
        ApplicationSuggestions::class,
        GetExplanationsAndExamplesService::class,
        GetFormationsOfInterestService::class,
        GetSuggestionsService::class,
        SuggestionServer::class,
    ]
)
@WebMvcTest(controllers = [SuggestionsControllerz::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SuggestionsControllerzTest(
    @Autowired val mvc: MockMvc,
) {

    @Autowired
    lateinit var webServer: SuggestionServer

    @BeforeAll
    fun setUp() {
        webServer.init()
    }

    @Nested
    inner class `Quand on appelle la route des suggestions` {

        @Test
        fun `pour l'élève louis_seance2_t1, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "Générale",
                    "duree": "long",
                    "apprentissage": "C",
                    "geo_pref": [
                      "Paris"
                    ],
                    "spe_classes": [
                      "Mathématiques",
                      "Physique-Chimie"
                    ],
                    "scores": {
                      "T_IDEO2_4819": 1,
                      "T_ITM_950": 1,
                      "T_IDEO2_4812": 1,
                      "T_IDEO2_4813": 1,
                      "T_ROME_1951356737": 1,
                      "T_IDEO2_4825": 1,
                      "T_ITM_1044": 1,
                      "T_ROME_2092381917": 1,
                      "T_ITM_1020": 1,
                      "T_IDEO2_4820": 1,
                      "T_ROME_637471645": 1,
                      "T_ITM_1238": 1,
                      "T_ITM_636": 1,
                      "T_ROME_803089798": 1,
                      "T_ITM_1112": 1
                    },
                    "moygen": "15",
                    "choices": {
                      "SEC_4835": {
                        "fl": "SEC_4835",
                        "date": "2024-01-24T10:38:07.795667616",
                        "status": 1
                      },
                      "MET_622": {
                        "fl": "MET_622",
                        "date": "2024-01-24T10:38:10.745023431",
                        "status": 1
                      },
                      "fl18": {
                        "fl": "fl18",
                        "date": "2024-01-24T10:38:19.638066554",
                        "status": 1
                      },
                      "MET_592": {
                        "fl": "MET_592",
                        "date": "2024-01-26T14:45:33.319042975",
                        "status": 1
                      },
                      "MET_468": {
                        "fl": "MET_468",
                        "date": "2024-01-26T14:45:39.270695996",
                        "status": 1
                      },
                      "MET_667": {
                        "fl": "MET_667",
                        "date": "2024-01-26T14:45:47.245584210",
                        "status": 1
                      },
                      "fl2033": {
                        "fl": "fl2033",
                        "date": "2024-01-26T14:45:56.968800678",
                        "status": 1
                      },
                      "fl2022": {
                        "fl": "fl2022",
                        "date": "2024-01-26T14:45:57.905581972",
                        "status": 1
                      },
                      "MET_461": {
                        "fl": "MET_461",
                        "date": "2024-01-26T14:46:11.705739633",
                        "status": 1
                      },
                      "MET_880": {
                        "fl": "MET_880",
                        "date": "2024-01-26T14:46:12.676100448",
                        "status": 1
                      },
                      "MET_624": {
                        "fl": "MET_624",
                        "date": "2024-01-26T14:46:13.747529186",
                        "status": 1
                      },
                      "MET_687": {
                        "fl": "MET_687",
                        "date": "2024-01-26T14:46:15.345909609",
                        "status": 1
                      },
                      "fl2046": {
                        "fl": "fl2046",
                        "date": "2024-01-26T14:46:28.528467861",
                        "status": 1
                      },
                      "fl2032": {
                        "fl": "fl2032",
                        "date": "2024-01-26T14:46:43.989185168",
                        "status": 1
                      },
                      "fl1002046": {
                        "fl": "fl1002046",
                        "date": "2024-01-26T14:48:58.008347928",
                        "status": 2
                      },
                      "fl2044": {
                        "fl": "fl2044",
                        "date": "2024-01-26T14:49:15.818905063",
                        "status": 2
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                    {
                      "header": {
                        "status": 0
                      },
                      "suggestions": {
                        "suggestions": [
                          {
                            "fl": "fl680002"
                          },
                          {
                            "fl": "fl210"
                          },
                          {
                            "fl": "fr22"
                          },
                          {
                            "fl": "fr83"
                          },
                          {
                            "fl": "fl13"
                          },
                          {
                            "fl": "SEC_4846"
                          },
                          {
                            "fl": "SEC_4848"
                          },
                          {
                            "fl": "SEC_4854"
                          },
                          {
                            "fl": "SEC_4855"
                          },
                          {
                            "fl": "SEC_4865"
                          }
                        ]
                      }
                    }
                """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève G Maths SVT 14 de moyenne, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "Générale",
                    "duree": "indiff",
                    "apprentissage": "C",
                    "geo_pref": [
                      "Rennes"
                    ],
                    "spe_classes": [
                      "Mathématiques",
                      "Sciences de la vie et de la Terre"
                    ],
                    "scores": {
                      "T_IDEO2_4812": 1,
                      "T_ROME_609891024": 1,
                      "T_IDEO2_4817": 1,
                      "T_IDEO2_4815": 1,
                      "T_IDEO2_4807": 1,
                      "T_ROME_1088162470": 1,
                      "T_ROME_860291826": 1,
                      "T_ROME_1316643679": 1,
                      "T_ROME_731379930": 1,
                      "T_IDEO2_4824": 1,
                      "T_ITM_1180": 1,
                      "T_ITM_PERSO4": 1,
                      "T_ITM_1044": 1,
                      "T_ITM_1491": 1
                    },
                    "moygen": "14",
                    "choices": {
                      "MET_171": {
                        "fl": "MET_171",
                        "status": 1
                      },
                      "MET_163": {
                        "fl": "MET_163",
                        "status": 1
                      },
                      "MET_332": {
                        "fl": "MET_332",
                        "status": 1
                      },
                      "MET_343": {
                        "fl": "MET_343",
                        "status": 2
                      },
                      "MET_288": {
                        "fl": "MET_288",
                        "status": 2
                      },
                      "MET_771": {
                        "fl": "MET_771",
                        "status": 2
                      },
                      "MET_7860": {
                        "fl": "MET_7860",
                        "status": 2
                      },
                      "fl2047": {
                        "fl": "fl2047",
                        "status": 1
                      },
                      "MET_419": {
                        "fl": "MET_419",
                        "status": 1
                      },
                      "MET_869": {
                        "fl": "MET_869",
                        "status": 2
                      },
                      "MET_828": {
                        "fl": "MET_828",
                        "status": 2
                      },
                      "MET_7858": {
                        "fl": "MET_7858",
                        "status": 2
                      },
                      "MET_827": {
                        "fl": "MET_827",
                        "status": 2
                      },
                      "MET_884": {
                        "fl": "MET_884",
                        "status": 2
                      },
                      "MET_344": {
                        "fl": "MET_344",
                        "status": 2
                      },
                      "MET_311": {
                        "fl": "MET_311",
                        "status": 2
                      },
                      "MET_201": {
                        "fl": "MET_201",
                        "status": 2
                      },
                      "MET_787": {
                        "fl": "MET_787",
                        "status": 1
                      },
                      "MET_699": {
                        "fl": "MET_699",
                        "status": 2
                      },
                      "MET_215": {
                        "fl": "MET_215",
                        "status": 2
                      },
                      "MET_335": {
                        "fl": "MET_335",
                        "status": 1
                      },
                      "MET_812": {
                        "fl": "MET_812",
                        "status": 2
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fl1000210"
                                  },
                                  {
                                    "fl": "fl810015"
                                  },
                                  {
                                    "fl": "fl2090"
                                  },
                                  {
                                    "fl": "fl1002046"
                                  },
                                  {
                                    "fl": "SEC_4846"
                                  },
                                  {
                                    "fl": "SEC_4869"
                                  },
                                  {
                                    "fl": "SEC_4845"
                                  },
                                  {
                                    "fl": "SEC_4832"
                                  },
                                  {
                                    "fl": "SEC_4856"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève G Maths Physique 15 de moyenne, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "Générale",
                    "duree": "long",
                    "apprentissage": "C",
                    "geo_pref": [
                      "Rennes"
                    ],
                    "spe_classes": [
                      "Mathématiques",
                      "Physique-Chimie"
                    ],
                    "scores": {
                      "T_IDEO2_4818": 1,
                      "T_IDEO2_4813": 1,
                      "T_IDEO2_4810": 1,
                      "T_IDEO2_4816": 1,
                      "T_IDEO2_4814": 1,
                      "T_ROME_898671777": 1,
                      "T_ROME_1573349427": 1,
                      "T_ROME_58088585": 1,
                      "T_ROME_2018646295": 1,
                      "T_ROME_1959553899": 1,
                      "T_ROME_1547781503": 1,
                      "T_IDEO2_4829": 1,
                      "T_ROME_269720073": 1,
                      "T_ROME_2092381917": 1,
                      "T_IDEO2_4805": 1,
                      "T_IDEO2_4824": 1,
                      "T_ITM_PERSO6": 1,
                      "T_ITM_PERSO1": 1,
                      "T_ITM_1112": 1,
                      "T_ITM_611": 1,
                      "T_ITM_1180": 1,
                      "T_ITM_918": 1,
                      "T_ITM_794": 1,
                      "T_ITM_1420": 1
                    },
                    "moygen": "15",
                    "choices": {
                      "SEC_4838": {
                        "fl": "SEC_4838",
                        "status": 2
                      },
                      "SEC_4859": {
                        "fl": "SEC_4859",
                        "status": 2
                      },
                      "SEC_4836": {
                        "fl": "SEC_4836",
                        "status": 1
                      },
                      "SEC_4858": {
                        "fl": "SEC_4858",
                        "status": 2
                      },
                      "SEC_4857": {
                        "fl": "SEC_4857",
                        "status": 1
                      },
                      "SEC_4835": {
                        "fl": "SEC_4835",
                        "status": 2
                      },
                      "SEC_4834": {
                        "fl": "SEC_4834",
                        "status": 2
                      },
                      "SEC_4856": {
                        "fl": "SEC_4856",
                        "status": 2
                      },
                      "fl210": {
                        "fl": "fl210",
                        "status": 1
                      },
                      "SEC_4855": {
                        "fl": "SEC_4855",
                        "status": 2
                      },
                      "SEC_4833": {
                        "fl": "SEC_4833",
                        "status": 2
                      },
                      "SEC_4832": {
                        "fl": "SEC_4832",
                        "status": 2
                      },
                      "SEC_5668": {
                        "fl": "SEC_5668",
                        "status": 2
                      },
                      "SEC_4854": {
                        "fl": "SEC_4854",
                        "status": 2
                      },
                      "fl230": {
                        "fl": "fl230",
                        "status": 2
                      },
                      "MET_61": {
                        "fl": "MET_61",
                        "status": 1
                      },
                      "SEC_4853": {
                        "fl": "SEC_4853",
                        "status": 2
                      },
                      "SEC_4831": {
                        "fl": "SEC_4831",
                        "status": 2
                      },
                      "fl251": {
                        "fl": "fl251",
                        "status": 1
                      },
                      "fl12": {
                        "fl": "fl12",
                        "status": 1
                      },
                      "fl250": {
                        "fl": "fl250",
                        "status": 1
                      },
                      "fl11": {
                        "fl": "fl11",
                        "status": 1
                      },
                      "SEC_4830": {
                        "fl": "SEC_4830",
                        "status": 1
                      },
                      "SEC_4852": {
                        "fl": "SEC_4852",
                        "status": 2
                      },
                      "SEC_4851": {
                        "fl": "SEC_4851",
                        "status": 2
                      },
                      "SEC_4850": {
                        "fl": "SEC_4850",
                        "status": 2
                      },
                      "SEC_4870": {
                        "fl": "SEC_4870",
                        "status": 2
                      },
                      "MET_931": {
                        "fl": "MET_931",
                        "status": 1
                      },
                      "SEC_4848": {
                        "fl": "SEC_4848",
                        "status": 2
                      },
                      "SEC_4869": {
                        "fl": "SEC_4869",
                        "status": 1
                      },
                      "SEC_4846": {
                        "fl": "SEC_4846",
                        "status": 2
                      },
                      "SEC_4868": {
                        "fl": "SEC_4868",
                        "status": 2
                      },
                      "SEC_4845": {
                        "fl": "SEC_4845",
                        "status": 1
                      },
                      "SEC_4867": {
                        "fl": "SEC_4867",
                        "status": 2
                      },
                      "SEC_4844": {
                        "fl": "SEC_4844",
                        "status": 1
                      },
                      "SEC_4865": {
                        "fl": "SEC_4865",
                        "status": 2
                      },
                      "SEC_4843": {
                        "fl": "SEC_4843",
                        "status": 2
                      },
                      "SEC_5712": {
                        "fl": "SEC_5712",
                        "status": 2
                      },
                      "SEC_4864": {
                        "fl": "SEC_4864",
                        "status": 2
                      },
                      "MET_947": {
                        "fl": "MET_947",
                        "status": 1
                      },
                      "SEC_4863": {
                        "fl": "SEC_4863",
                        "status": 2
                      },
                      "SEC_4841": {
                        "fl": "SEC_4841",
                        "status": 2
                      },
                      "SEC_4840": {
                        "fl": "SEC_4840",
                        "status": 2
                      },
                      "SEC_4861": {
                        "fl": "SEC_4861",
                        "status": 2
                      },
                      "SEC_4860": {
                        "fl": "SEC_4860",
                        "status": 2
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fr83"
                                  },
                                  {
                                    "fl": "fr22"
                                  },
                                  {
                                    "fl": "fl13"
                                  },
                                  {
                                    "fl": "fl1000210"
                                  },
                                  {
                                    "fl": "SEC_4847"
                                  },
                                  {
                                    "fl": "SEC_4862"
                                  },
                                  {
                                    "fl": "SEC_4849"
                                  },
                                  {
                                    "fl": "SEC_4871"
                                  },
                                  {
                                    "fl": "SEC_4866"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève G sans ens spe 11,5 moy, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "Générale",
                    "duree": "indiff",
                    "apprentissage": "C",
                    "geo_pref": [],
                    "spe_classes": [],
                    "scores": {
                      "T_IDEO2_4814": 1,
                      "T_ROME_326548351": 1,
                      "T_ROME_2018646295": 1,
                      "T_IDEO2_4808": 1,
                      "T_ROME_2092381917": 1,
                      "T_ITM_PERSO3": 1,
                      "T_ITM_1238": 1,
                      "T_ITM_1284": 1,
                      "T_ITM_1169": 1
                    },
                    "moygen": "11.5",
                    "choices": {
                      "SEC_4838": {
                        "fl": "SEC_4838",
                        "status": 2
                      },
                      "SEC_4837": {
                        "fl": "SEC_4837",
                        "status": 2
                      },
                      "SEC_4836": {
                        "fl": "SEC_4836",
                        "status": 2
                      },
                      "SEC_4835": {
                        "fl": "SEC_4835",
                        "status": 2
                      },
                      "SEC_4834": {
                        "fl": "SEC_4834",
                        "status": 2
                      },
                      "SEC_4833": {
                        "fl": "SEC_4833",
                        "status": 2
                      },
                      "SEC_4832": {
                        "fl": "SEC_4832",
                        "status": 2
                      },
                      "MET_453": {
                        "fl": "MET_453",
                        "status": 1
                      },
                      "SEC_4831": {
                        "fl": "SEC_4831",
                        "status": 2
                      },
                      "SEC_4839": {
                        "fl": "SEC_4839",
                        "status": 1
                      },
                      "SEC_4830": {
                        "fl": "SEC_4830",
                        "status": 2
                      },
                      "SEC_4871": {
                        "fl": "SEC_4871",
                        "status": 2
                      },
                      "SEC_4870": {
                        "fl": "SEC_4870",
                        "status": 2
                      },
                      "SEC_4869": {
                        "fl": "SEC_4869",
                        "status": 2
                      },
                      "SEC_4868": {
                        "fl": "SEC_4868",
                        "status": 2
                      },
                      "SEC_4867": {
                        "fl": "SEC_4867",
                        "status": 2
                      },
                      "SEC_4866": {
                        "fl": "SEC_4866",
                        "status": 1
                      },
                      "SEC_4865": {
                        "fl": "SEC_4865",
                        "status": 1
                      },
                      "SEC_5712": {
                        "fl": "SEC_5712",
                        "status": 2
                      },
                      "SEC_4864": {
                        "fl": "SEC_4864",
                        "status": 2
                      },
                      "SEC_5711": {
                        "fl": "SEC_5711",
                        "status": 1
                      },
                      "MET_7772": {
                        "fl": "MET_7772",
                        "status": 1
                      },
                      "fl810502": {
                        "fl": "fl810502",
                        "status": 1
                      },
                      "SEC_4863": {
                        "fl": "SEC_4863",
                        "status": 2
                      },
                      "SEC_4862": {
                        "fl": "SEC_4862",
                        "status": 2
                      },
                      "SEC_4861": {
                        "fl": "SEC_4861",
                        "status": 2
                      },
                      "MET_149": {
                        "fl": "MET_149",
                        "status": 1
                      },
                      "SEC_4860": {
                        "fl": "SEC_4860",
                        "status": 2
                      },
                      "SEC_4859": {
                        "fl": "SEC_4859",
                        "status": 1
                      },
                      "SEC_4858": {
                        "fl": "SEC_4858",
                        "status": 2
                      },
                      "MET_195": {
                        "fl": "MET_195",
                        "status": 1
                      },
                      "SEC_4857": {
                        "fl": "SEC_4857",
                        "status": 2
                      },
                      "SEC_4856": {
                        "fl": "SEC_4856",
                        "status": 1
                      },
                      "SEC_4855": {
                        "fl": "SEC_4855",
                        "status": 2
                      },
                      "SEC_5668": {
                        "fl": "SEC_5668",
                        "status": 2
                      },
                      "SEC_4854": {
                        "fl": "SEC_4854",
                        "status": 2
                      },
                      "SEC_4853": {
                        "fl": "SEC_4853",
                        "status": 2
                      },
                      "fl811502": {
                        "fl": "fl811502",
                        "status": 2
                      },
                      "SEC_4852": {
                        "fl": "SEC_4852",
                        "status": 2
                      },
                      "SEC_4851": {
                        "fl": "SEC_4851",
                        "status": 2
                      },
                      "SEC_4850": {
                        "fl": "SEC_4850",
                        "status": 2
                      },
                      "MET_59": {
                        "fl": "MET_59",
                        "status": 1
                      },
                      "SEC_4849": {
                        "fl": "SEC_4849",
                        "status": 2
                      },
                      "SEC_4848": {
                        "fl": "SEC_4848",
                        "status": 2
                      },
                      "SEC_4847": {
                        "fl": "SEC_4847",
                        "status": 2
                      },
                      "SEC_4846": {
                        "fl": "SEC_4846",
                        "status": 2
                      },
                      "SEC_4845": {
                        "fl": "SEC_4845",
                        "status": 2
                      },
                      "SEC_4844": {
                        "fl": "SEC_4844",
                        "status": 2
                      },
                      "SEC_4843": {
                        "fl": "SEC_4843",
                        "status": 2
                      },
                      "SEC_4842": {
                        "fl": "SEC_4842",
                        "status": 1
                      },
                      "SEC_4841": {
                        "fl": "SEC_4841",
                        "status": 2
                      },
                      "SEC_4840": {
                        "fl": "SEC_4840",
                        "status": 2
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl2006"
                                  },
                                  {
                                    "fl": "fl680003"
                                  },
                                  {
                                    "fl": "fl241"
                                  },
                                  {
                                    "fl": "fl486"
                                  },
                                  {
                                    "fl": "fl680002"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève G sans ens spe 15 moy (fictif), il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "Générale",
                    "duree": "indiff",
                    "apprentissage": "C",
                    "geo_pref": [],
                    "spe_classes": [],
                    "scores": {
                      "T_IDEO2_4814": 1,
                      "T_ROME_326548351": 1,
                      "T_ROME_2018646295": 1,
                      "T_IDEO2_4808": 1,
                      "T_ROME_2092381917": 1,
                      "T_ITM_PERSO3": 1,
                      "T_ITM_1238": 1,
                      "T_ITM_1284": 1,
                      "T_ITM_1169": 1
                    },
                    "moygen": "15",
                    "choices": {
                      "SEC_4838": {
                        "fl": "SEC_4838",
                        "status": 2
                      },
                      "SEC_4837": {
                        "fl": "SEC_4837",
                        "status": 2
                      },
                      "SEC_4836": {
                        "fl": "SEC_4836",
                        "status": 2
                      },
                      "SEC_4835": {
                        "fl": "SEC_4835",
                        "status": 2
                      },
                      "SEC_4834": {
                        "fl": "SEC_4834",
                        "status": 2
                      },
                      "SEC_4833": {
                        "fl": "SEC_4833",
                        "status": 2
                      },
                      "SEC_4832": {
                        "fl": "SEC_4832",
                        "status": 2
                      },
                      "MET_453": {
                        "fl": "MET_453",
                        "status": 1
                      },
                      "SEC_4831": {
                        "fl": "SEC_4831",
                        "status": 2
                      },
                      "SEC_4839": {
                        "fl": "SEC_4839",
                        "status": 1
                      },
                      "SEC_4830": {
                        "fl": "SEC_4830",
                        "status": 2
                      },
                      "SEC_4871": {
                        "fl": "SEC_4871",
                        "status": 2
                      },
                      "SEC_4870": {
                        "fl": "SEC_4870",
                        "status": 2
                      },
                      "SEC_4869": {
                        "fl": "SEC_4869",
                        "status": 2
                      },
                      "SEC_4868": {
                        "fl": "SEC_4868",
                        "status": 2
                      },
                      "SEC_4867": {
                        "fl": "SEC_4867",
                        "status": 2
                      },
                      "SEC_4866": {
                        "fl": "SEC_4866",
                        "status": 1
                      },
                      "SEC_4865": {
                        "fl": "SEC_4865",
                        "status": 1
                      },
                      "SEC_5712": {
                        "fl": "SEC_5712",
                        "status": 2
                      },
                      "SEC_4864": {
                        "fl": "SEC_4864",
                        "status": 2
                      },
                      "SEC_5711": {
                        "fl": "SEC_5711",
                        "status": 1
                      },
                      "MET_7772": {
                        "fl": "MET_7772",
                        "status": 1
                      },
                      "fl810502": {
                        "fl": "fl810502",
                        "status": 1
                      },
                      "SEC_4863": {
                        "fl": "SEC_4863",
                        "status": 2
                      },
                      "SEC_4862": {
                        "fl": "SEC_4862",
                        "status": 2
                      },
                      "SEC_4861": {
                        "fl": "SEC_4861",
                        "status": 2
                      },
                      "MET_149": {
                        "fl": "MET_149",
                        "status": 1
                      },
                      "SEC_4860": {
                        "fl": "SEC_4860",
                        "status": 2
                      },
                      "SEC_4859": {
                        "fl": "SEC_4859",
                        "status": 1
                      },
                      "SEC_4858": {
                        "fl": "SEC_4858",
                        "status": 2
                      },
                      "MET_195": {
                        "fl": "MET_195",
                        "status": 1
                      },
                      "SEC_4857": {
                        "fl": "SEC_4857",
                        "status": 2
                      },
                      "SEC_4856": {
                        "fl": "SEC_4856",
                        "status": 1
                      },
                      "SEC_4855": {
                        "fl": "SEC_4855",
                        "status": 2
                      },
                      "SEC_5668": {
                        "fl": "SEC_5668",
                        "status": 2
                      },
                      "SEC_4854": {
                        "fl": "SEC_4854",
                        "status": 2
                      },
                      "SEC_4853": {
                        "fl": "SEC_4853",
                        "status": 2
                      },
                      "fl811502": {
                        "fl": "fl811502",
                        "status": 2
                      },
                      "SEC_4852": {
                        "fl": "SEC_4852",
                        "status": 2
                      },
                      "SEC_4851": {
                        "fl": "SEC_4851",
                        "status": 2
                      },
                      "SEC_4850": {
                        "fl": "SEC_4850",
                        "status": 2
                      },
                      "MET_59": {
                        "fl": "MET_59",
                        "status": 1
                      },
                      "SEC_4849": {
                        "fl": "SEC_4849",
                        "status": 2
                      },
                      "SEC_4848": {
                        "fl": "SEC_4848",
                        "status": 2
                      },
                      "SEC_4847": {
                        "fl": "SEC_4847",
                        "status": 2
                      },
                      "SEC_4846": {
                        "fl": "SEC_4846",
                        "status": 2
                      },
                      "SEC_4845": {
                        "fl": "SEC_4845",
                        "status": 2
                      },
                      "SEC_4844": {
                        "fl": "SEC_4844",
                        "status": 2
                      },
                      "SEC_4843": {
                        "fl": "SEC_4843",
                        "status": 2
                      },
                      "SEC_4842": {
                        "fl": "SEC_4842",
                        "status": 1
                      },
                      "SEC_4841": {
                        "fl": "SEC_4841",
                        "status": 2
                      },
                      "SEC_4840": {
                        "fl": "SEC_4840",
                        "status": 2
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl242"
                                  },
                                  {
                                    "fl": "fr85"
                                  },
                                  {
                                    "fl": "fl25"
                                  },
                                  {
                                    "fl": "fl680003"
                                  },
                                  {
                                    "fl": "fl52"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève G 12,5 de moyenne, Cinéma Audiovisuel, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "Générale",
                    "duree": "indiff",
                    "apprentissage": "B",
                    "geo_pref": [
                      "Paris",
                      "Lille",
                      "Montpellier",
                      "Lyon",
                      "Toulouse",
                      "Bordeaux"
                    ],
                    "spe_classes": [
                      "Cinéma-Audiovisuel"
                    ],
                    "scores": {
                      "T_ROME_934089965": 1,
                      "T_IDEO2_4818": 1,
                      "T_IDEO2_4810": 1,
                      "T_ITM_1420": 1,
                      "T_ITM_1030": 1,
                      "T_ITM_918": 1
                    },
                    "moygen": "12.5",
                    "choices": {
                      "fl452": {
                        "fl": "fl452",
                        "status": 1
                      },
                      "MET_585": {
                        "fl": "MET_585",
                        "status": 1
                      },
                      "MET_621": {
                        "fl": "MET_621",
                        "status": 1
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl2010"
                                  },
                                  {
                                    "fl": "fl691142"
                                  },
                                  {
                                    "fl": "fl382"
                                  },
                                  {
                                    "fl": "fl454"
                                  },
                                  {
                                    "fl": "fl2027"
                                  },
                                  {
                                    "fl": "SEC_4838"
                                  },
                                  {
                                    "fl": "SEC_4858"
                                  },
                                  {
                                    "fl": "SEC_5712"
                                  },
                                  {
                                    "fl": "SEC_4850"
                                  },
                                  {
                                    "fl": "SEC_4864"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève STMG 12 de moyenne, Mercatique et Droit éco gestion, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "STMG",
                    "duree": "indiff",
                    "apprentissage": "C",
                    "geo_pref": [
                      "Grenoble",
                      "Paris",
                      "Lyon",
                      "Suisse"
                    ],
                    "spe_classes": [
                      "Mercatique",
                      "Droit et Economie"
                    ],
                    "scores": {
                      "T_IDEO2_4813": 1,
                      "T_IDEO2_4820": 1,
                      "T_IDEO2_4817": 1,
                      "T_ROME_762517279": 1,
                      "T_ROME_2027610093": 1,
                      "T_ROME_934089965": 1,
                      "T_IDEO2_4821": 1,
                      "T_ROME_313545038": 1,
                      "T_IDEO2_4809": 1,
                      "T_ITM_PERSO3": 1,
                      "T_ITM_796": 1,
                      "T_ITM_918": 1,
                      "T_ITM_957": 1
                    },
                    "moygen": "12",
                    "choices": {
                      "MET_93": {
                        "fl": "MET_93",
                        "status": 1
                      },
                      "MET_450": {
                        "fl": "MET_450",
                        "status": 1
                      },
                      "SEC_4865": {
                        "fl": "SEC_4865",
                        "status": 1
                      },
                      "SEC_5668": {
                        "fl": "SEC_5668",
                        "status": 1
                      },
                      "MET_540": {
                        "fl": "MET_540",
                        "status": 1
                      },
                      "SEC_4864": {
                        "fl": "SEC_4864",
                        "status": 1
                      },
                      "MET_8": {
                        "fl": "MET_8",
                        "status": 1
                      },
                      "MET_66": {
                        "fl": "MET_66",
                        "status": 1
                      },
                      "MET_314": {
                        "fl": "MET_314",
                        "status": 1
                      },
                      "MET_802": {
                        "fl": "MET_802",
                        "status": 1
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fl241"
                                  },
                                  {
                                    "fl": "fl23"
                                  },
                                  {
                                    "fl": "fl2023"
                                  },
                                  {
                                    "fl": "fl810508"
                                  },
                                  {
                                    "fl": "SEC_4848"
                                  },
                                  {
                                    "fl": "SEC_4852"
                                  },
                                  {
                                    "fl": "SEC_4863"
                                  },
                                  {
                                    "fl": "SEC_4858"
                                  },
                                  {
                                    "fl": "SEC_4851"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève Bon élève G commerce et international, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "Générale",
                    "duree": "court",
                    "apprentissage": "C",
                    "geo_pref": [
                      "Paris"
                    ],
                    "spe_classes": [
                      "Langues, littératures et cultures étrangères et régionales",
                      "Sciences Economiques et Sociales"
                    ],
                    "scores": {
                      "T_ROME_58088585": 1,
                      "T_IDEO2_4818": 1,
                      "T_ROME_860291826": 1,
                      "T_ROME_934089965": 1,
                      "T_IDEO2_4811": 1,
                      "T_ROME_898671777": 1,
                      "T_IDEO2_4817": 1,
                      "T_ITM_PERSO3": 1,
                      "T_ITM_PERSO1": 1,
                      "T_ITM_1020": 1,
                      "T_ITM_918": 1,
                      "T_ITM_1238": 1,
                      "T_ITM_1025": 1,
                      "T_ROME_749075906": 1,
                      "T_ITM_1420": 1
                    },
                    "moygen": "14",
                    "choices": {
                      "MET_270": {
                        "fl": "MET_270",
                        "status": 1
                      },
                      "SEC_4848": {
                        "fl": "SEC_4848",
                        "status": 2
                      },
                      "SEC_4858": {
                        "fl": "SEC_4858",
                        "status": 2
                      },
                      "SEC_4846": {
                        "fl": "SEC_4846",
                        "status": 2
                      },
                      "SEC_4845": {
                        "fl": "SEC_4845",
                        "status": 2
                      },
                      "SEC_4833": {
                        "fl": "SEC_4833",
                        "status": 2
                      },
                      "SEC_4865": {
                        "fl": "SEC_4865",
                        "status": 1
                      },
                      "SEC_5711": {
                        "fl": "SEC_5711",
                        "status": 1
                      },
                      "fr89200": {
                        "fl": "fr89200",
                        "status": 1
                      },
                      "SEC_4852": {
                        "fl": "SEC_4852",
                        "status": 2
                      },
                      "SEC_4863": {
                        "fl": "SEC_4863",
                        "status": 2
                      },
                      "SEC_4830": {
                        "fl": "SEC_4830",
                        "status": 2
                      },
                      "SEC_4851": {
                        "fl": "SEC_4851",
                        "status": 1
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl2022"
                                  },
                                  {
                                    "fl": "fl2003"
                                  },
                                  {
                                    "fl": "fl2018"
                                  },
                                  {
                                    "fl": "fl2006"
                                  },
                                  {
                                    "fl": "fl2002"
                                  },
                                  {
                                    "fl": "SEC_5668"
                                  },
                                  {
                                    "fl": "SEC_5712"
                                  },
                                  {
                                    "fl": "SEC_4838"
                                  },
                                  {
                                    "fl": "SEC_4864"
                                  },
                                  {
                                    "fl": "SEC_4835"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève Bon élève G scientifique, ingénieur, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "Générale",
                    "duree": "long",
                    "geo_pref": [
                      "Paris"
                    ],
                    "spe_classes": [
                      "Mathématiques",
                      "Physique-Chimie"
                    ],
                    "scores": {
                      "T_IDEO2_4809": 1,
                      "T_ITM_794": 1,
                      "T_ROME_860291826": 1,
                      "T_ROME_326548351": 1,
                      "T_ITM_611": 1,
                      "T_ROME_1825212206": 1,
                      "T_IDEO2_4812": 1,
                      "T_IDEO2_4813": 1,
                      "T_IDEO2_4816": 1,
                      "T_IDEO2_4805": 1,
                      "T_IDEO2_4825": 1,
                      "T_IDEO2_4826": 1,
                      "T_ITM_1054": 1,
                      "T_ROME_2092381917": 1,
                      "T_ROME_762517279": 1,
                      "T_ROME_600174291": 1,
                      "T_ROME_1573349427": 1,
                      "T_ROME_1547781503": 1,
                      "T_ROME_2027610093": 1,
                      "T_ROME_1046112128": 1,
                      "T_ITM_1112": 1,
                      "T_ITM_1420": 1
                    },
                    "moygen": "14",
                    "choices": {
                      "SEC_4848": {
                        "fl": "SEC_4848",
                        "status": 2
                      },
                      "fl2033": {
                        "fl": "fl2033",
                        "status": 1
                      },
                      "SEC_4846": {
                        "fl": "SEC_4846",
                        "status": 1
                      },
                      "SEC_4857": {
                        "fl": "SEC_4857",
                        "status": 2
                      },
                      "MET_880": {
                        "fl": "MET_880",
                        "status": 1
                      },
                      "SEC_4845": {
                        "fl": "SEC_4845",
                        "status": 1
                      },
                      "SEC_4867": {
                        "fl": "SEC_4867",
                        "status": 2
                      },
                      "SEC_4833": {
                        "fl": "SEC_4833",
                        "status": 1
                      },
                      "SEC_4852": {
                        "fl": "SEC_4852",
                        "status": 2
                      },
                      "fl11": {
                        "fl": "fl11",
                        "status": 1
                      },
                      "SEC_4851": {
                        "fl": "SEC_4851",
                        "status": 2
                      },
                      "MET_800": {
                        "fl": "MET_800",
                        "status": 1
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fl240"
                                  },
                                  {
                                    "fl": "fr83"
                                  },
                                  {
                                    "fl": "fl2100"
                                  },
                                  {
                                    "fl": "fl2022"
                                  },
                                  {
                                    "fl": "SEC_4869"
                                  },
                                  {
                                    "fl": "SEC_4832"
                                  },
                                  {
                                    "fl": "SEC_4863"
                                  },
                                  {
                                    "fl": "SEC_4855"
                                  },
                                  {
                                    "fl": "SEC_4859"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève STMG moyenne inconnue management et business, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "STMG",
                    "duree": "court",
                    "apprentissage": "C",
                    "geo_pref": [],
                    "spe_classes": [
                      "Droit et Economie",
                      "Gestion et Finance"
                    ],
                    "scores": {
                      "T_IDEO2_4812": 1,
                      "T_IDEO2_4811": 1,
                      "T_IDEO2_4816": 1,
                      "T_IDEO2_4817": 1,
                      "T_IDEO2_4814": 1,
                      "T_ROME_1814691478": 1,
                      "T_IDEO2_4820": 1,
                      "T_ROME_637471645": 1,
                      "T_ROME_749075906": 1,
                      "T_IDEO2_4808": 1,
                      "T_ROME_860291826": 1,
                      "T_ROME_313545038": 1,
                      "T_ROME_2018646295": 1,
                      "T_ROME_803089798": 1,
                      "T_ITM_PERSO3": 1,
                      "T_ITM_1112": 1,
                      "T_ITM_636": 1,
                      "T_ITM_807": 1,
                      "T_ITM_1238": 1,
                      "T_ITM_957": 1,
                      "T_ITM_950": 1,
                      "T_ITM_1055": 1,
                      "T_IDEO2_4813": 1
                    },
                    "moygen": "",
                    "choices": {
                      "SEC_4838": {
                        "fl": "SEC_4838",
                        "status": 2
                      },
                      "SEC_4837": {
                        "fl": "SEC_4837",
                        "status": 2
                      },
                      "SEC_4836": {
                        "fl": "SEC_4836",
                        "status": 2
                      },
                      "SEC_4835": {
                        "fl": "SEC_4835",
                        "status": 1
                      },
                      "SEC_4834": {
                        "fl": "SEC_4834",
                        "status": 2
                      },
                      "SEC_4833": {
                        "fl": "SEC_4833",
                        "status": 1
                      },
                      "SEC_4832": {
                        "fl": "SEC_4832",
                        "status": 2
                      },
                      "SEC_4831": {
                        "fl": "SEC_4831",
                        "status": 2
                      },
                      "SEC_4839": {
                        "fl": "SEC_4839",
                        "status": 2
                      },
                      "SEC_4830": {
                        "fl": "SEC_4830",
                        "status": 2
                      },
                      "MET_410": {
                        "fl": "MET_410",
                        "status": 1
                      },
                      "SEC_4871": {
                        "fl": "SEC_4871",
                        "status": 2
                      },
                      "SEC_4870": {
                        "fl": "SEC_4870",
                        "status": 2
                      },
                      "SEC_4869": {
                        "fl": "SEC_4869",
                        "status": 2
                      },
                      "SEC_4868": {
                        "fl": "SEC_4868",
                        "status": 2
                      },
                      "SEC_4867": {
                        "fl": "SEC_4867",
                        "status": 2
                      },
                      "SEC_4866": {
                        "fl": "SEC_4866",
                        "status": 1
                      },
                      "SEC_4865": {
                        "fl": "SEC_4865",
                        "status": 1
                      },
                      "SEC_5712": {
                        "fl": "SEC_5712",
                        "status": 2
                      },
                      "SEC_4864": {
                        "fl": "SEC_4864",
                        "status": 1
                      },
                      "SEC_5711": {
                        "fl": "SEC_5711",
                        "status": 1
                      },
                      "MET_7772": {
                        "fl": "MET_7772",
                        "status": 1
                      },
                      "MET_826": {
                        "fl": "MET_826",
                        "status": 1
                      },
                      "MET_869": {
                        "fl": "MET_869",
                        "status": 2
                      },
                      "fl811511": {
                        "fl": "fl811511",
                        "status": 2
                      },
                      "SEC_4863": {
                        "fl": "SEC_4863",
                        "status": 2
                      },
                      "SEC_4862": {
                        "fl": "SEC_4862",
                        "status": 2
                      },
                      "SEC_4861": {
                        "fl": "SEC_4861",
                        "status": 2
                      },
                      "MET_468": {
                        "fl": "MET_468",
                        "status": 2
                      },
                      "MET_589": {
                        "fl": "MET_589",
                        "status": 2
                      },
                      "SEC_4860": {
                        "fl": "SEC_4860",
                        "status": 2
                      },
                      "SEC_4859": {
                        "fl": "SEC_4859",
                        "status": 2
                      },
                      "SEC_4858": {
                        "fl": "SEC_4858",
                        "status": 2
                      },
                      "SEC_4857": {
                        "fl": "SEC_4857",
                        "status": 2
                      },
                      "SEC_4856": {
                        "fl": "SEC_4856",
                        "status": 2
                      },
                      "SEC_4855": {
                        "fl": "SEC_4855",
                        "status": 2
                      },
                      "SEC_5668": {
                        "fl": "SEC_5668",
                        "status": 2
                      },
                      "SEC_4854": {
                        "fl": "SEC_4854",
                        "status": 1
                      },
                      "SEC_4853": {
                        "fl": "SEC_4853",
                        "status": 2
                      },
                      "MET_111": {
                        "fl": "MET_111",
                        "status": 2
                      },
                      "SEC_4852": {
                        "fl": "SEC_4852",
                        "status": 2
                      },
                      "SEC_4851": {
                        "fl": "SEC_4851",
                        "status": 1
                      },
                      "SEC_4850": {
                        "fl": "SEC_4850",
                        "status": 2
                      },
                      "MET_358": {
                        "fl": "MET_358",
                        "status": 2
                      },
                      "MET_798": {
                        "fl": "MET_798",
                        "status": 1
                      },
                      "MET_756": {
                        "fl": "MET_756",
                        "status": 1
                      },
                      "SEC_4849": {
                        "fl": "SEC_4849",
                        "status": 2
                      },
                      "SEC_4848": {
                        "fl": "SEC_4848",
                        "status": 2
                      },
                      "SEC_4847": {
                        "fl": "SEC_4847",
                        "status": 2
                      },
                      "SEC_4846": {
                        "fl": "SEC_4846",
                        "status": 2
                      },
                      "SEC_4845": {
                        "fl": "SEC_4845",
                        "status": 2
                      },
                      "SEC_4844": {
                        "fl": "SEC_4844",
                        "status": 2
                      },
                      "SEC_4843": {
                        "fl": "SEC_4843",
                        "status": 2
                      },
                      "SEC_4842": {
                        "fl": "SEC_4842",
                        "status": 1
                      },
                      "MET_209": {
                        "fl": "MET_209",
                        "status": 1
                      },
                      "SEC_4841": {
                        "fl": "SEC_4841",
                        "status": 1
                      },
                      "SEC_4840": {
                        "fl": "SEC_4840",
                        "status": 2
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl23"
                                  },
                                  {
                                    "fl": "fl241"
                                  },
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fl2006"
                                  },
                                  {
                                    "fl": "fl810502"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève Bon élève G art, éco, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "Générale",
                    "duree": "long",
                    "apprentissage": "B",
                    "geo_pref": [
                      "Lyon",
                      "Strasbourg",
                      "Nantes",
                      "Marseille"
                    ],
                    "spe_classes": [
                      "Sciences Economiques et Sociales",
                      "Arts Plastiques"
                    ],
                    "scores": {
                      "T_ROME_84652368": 1,
                      "T_IDEO2_4810": 1,
                      "T_IDEO2_4811": 1,
                      "T_IDEO2_4817": 1,
                      "T_IDEO2_4814": 1,
                      "T_ROME_1814691478": 1,
                      "T_IDEO2_4820": 1,
                      "T_ROME_1825212206": 1,
                      "T_ROME_898671777": 1,
                      "T_ROME_1573349427": 1,
                      "T_ROME_58088585": 1,
                      "T_ROME_860291826": 1,
                      "T_ROME_2018646295": 1,
                      "T_ROME_1951356737": 1,
                      "T_ROME_762517279": 1,
                      "T_ROME_2027610093": 1,
                      "T_ROME_1046112128": 1,
                      "T_IDEO2_4829": 1,
                      "T_ROME_269720073": 1,
                      "T_ROME_934089965": 1,
                      "T_IDEO2_4822": 1,
                      "T_ITM_PERSO9": 1,
                      "T_ITM_PERSO3": 1,
                      "T_ITM_PERSO1": 1,
                      "T_ITM_1020": 1,
                      "T_ITM_1025": 1,
                      "T_ITM_1491": 1,
                      "T_ITM_1238": 1,
                      "T_ITM_950": 1,
                      "T_ITM_936": 1,
                      "T_ITM_1044": 1
                    },
                    "moygen": "15.5",
                    "choices": {
                      "SEC_4859": {
                        "fl": "SEC_4859",
                        "status": 2
                      },
                      "SEC_4836": {
                        "fl": "SEC_4836",
                        "status": 1
                      },
                      "SEC_4858": {
                        "fl": "SEC_4858",
                        "status": 2
                      },
                      "SEC_4835": {
                        "fl": "SEC_4835",
                        "status": 2
                      },
                      "SEC_4856": {
                        "fl": "SEC_4856",
                        "status": 2
                      },
                      "SEC_4833": {
                        "fl": "SEC_4833",
                        "status": 2
                      },
                      "SEC_4855": {
                        "fl": "SEC_4855",
                        "status": 2
                      },
                      "SEC_5668": {
                        "fl": "SEC_5668",
                        "status": 2
                      },
                      "SEC_4832": {
                        "fl": "SEC_4832",
                        "status": 2
                      },
                      "MET_551": {
                        "fl": "MET_551",
                        "status": 2
                      },
                      "fl251": {
                        "fl": "fl251",
                        "status": 2
                      },
                      "fl250": {
                        "fl": "fl250",
                        "status": 1
                      },
                      "MET_718": {
                        "fl": "MET_718",
                        "status": 2
                      },
                      "SEC_4852": {
                        "fl": "SEC_4852",
                        "status": 2
                      },
                      "SEC_4830": {
                        "fl": "SEC_4830",
                        "status": 1
                      },
                      "SEC_4851": {
                        "fl": "SEC_4851",
                        "status": 2
                      },
                      "MET_611": {
                        "fl": "MET_611",
                        "status": 2
                      },
                      "SEC_4850": {
                        "fl": "SEC_4850",
                        "status": 2
                      },
                      "MET_478": {
                        "fl": "MET_478",
                        "status": 2
                      },
                      "MET_258": {
                        "fl": "MET_258",
                        "status": 2
                      },
                      "SEC_4870": {
                        "fl": "SEC_4870",
                        "status": 2
                      },
                      "MET_161": {
                        "fl": "MET_161",
                        "status": 1
                      },
                      "SEC_4848": {
                        "fl": "SEC_4848",
                        "status": 2
                      },
                      "SEC_4869": {
                        "fl": "SEC_4869",
                        "status": 2
                      },
                      "SEC_4846": {
                        "fl": "SEC_4846",
                        "status": 2
                      },
                      "SEC_4868": {
                        "fl": "SEC_4868",
                        "status": 2
                      },
                      "SEC_4845": {
                        "fl": "SEC_4845",
                        "status": 2
                      },
                      "SEC_4844": {
                        "fl": "SEC_4844",
                        "status": 1
                      },
                      "MET_563": {
                        "fl": "MET_563",
                        "status": 2
                      },
                      "SEC_4865": {
                        "fl": "SEC_4865",
                        "status": 2
                      },
                      "SEC_5712": {
                        "fl": "SEC_5712",
                        "status": 2
                      },
                      "SEC_4863": {
                        "fl": "SEC_4863",
                        "status": 2
                      },
                      "SEC_4862": {
                        "fl": "SEC_4862",
                        "status": 2
                      },
                      "SEC_4840": {
                        "fl": "SEC_4840",
                        "status": 1
                      },
                      "MET_864": {
                        "fl": "MET_864",
                        "status": 2
                      },
                      "SEC_4860": {
                        "fl": "SEC_4860",
                        "status": 2
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl680003"
                                  },
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fl51"
                                  },
                                  {
                                    "fl": "fl52"
                                  },
                                  {
                                    "fl": "fl2018"
                                  },
                                  {
                                    "fl": "SEC_4864"
                                  },
                                  {
                                    "fl": "SEC_4853"
                                  },
                                  {
                                    "fl": "SEC_4838"
                                  },
                                  {
                                    "fl": "SEC_4867"
                                  },
                                  {
                                    "fl": "SEC_4861"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève ProfileDTO1, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "Générale",
                    "duree": "long",
                    "apprentissage": "D",
                    "geo_pref": [
                      "Paris"
                    ],
                    "spe_classes": [
                      "Histoire-Géographie, Géopolitique et Sciences politiques",
                      "Humanités, Littérature et Philosophie"
                    ],
                    "scores": {
                      "T_ROME_762517279": 1,
                      "T_ROME_2027610093": 1,
                      "T_ROME_1046112128": 1,
                      "T_IDEO2_4809": 1,
                      "T_IDEO2_4818": 1,
                      "T_ITM_PERSO3": 1,
                      "T_ITM_1420": 1,
                      "T_ITM_1030": 1,
                      "T_ITM_1020": 1,
                      "T_ITM_918": 1,
                      "T_ITM_917": 1,
                      "T_ITM_950": 1,
                      "T_ITM_1284": 1
                    },
                    "moygen": "11.5",
                    "choices": {
                      "SEC_4838": {
                        "fl": "SEC_4838",
                        "status": 1
                      },
                      "SEC_4859": {
                        "fl": "SEC_4859",
                        "status": 2
                      },
                      "SEC_4858": {
                        "fl": "SEC_4858",
                        "status": 2
                      },
                      "SEC_4835": {
                        "fl": "SEC_4835",
                        "status": 2
                      },
                      "SEC_4833": {
                        "fl": "SEC_4833",
                        "status": 2
                      },
                      "SEC_4855": {
                        "fl": "SEC_4855",
                        "status": 2
                      },
                      "SEC_4854": {
                        "fl": "SEC_4854",
                        "status": 2
                      },
                      "SEC_4832": {
                        "fl": "SEC_4832",
                        "status": 2
                      },
                      "SEC_5668": {
                        "fl": "SEC_5668",
                        "status": 2
                      },
                      "fl2002": {
                        "fl": "fl2002",
                        "status": 1
                      },
                      "fr90": {
                        "fl": "fr90",
                        "status": 1
                      },
                      "SEC_4852": {
                        "fl": "SEC_4852",
                        "status": 2
                      },
                      "SEC_4851": {
                        "fl": "SEC_4851",
                        "status": 1
                      },
                      "MET_33": {
                        "fl": "MET_33",
                        "status": 1
                      },
                      "SEC_4850": {
                        "fl": "SEC_4850",
                        "status": 2
                      },
                      "SEC_4870": {
                        "fl": "SEC_4870",
                        "status": 2
                      },
                      "SEC_4848": {
                        "fl": "SEC_4848",
                        "status": 2
                      },
                      "SEC_4869": {
                        "fl": "SEC_4869",
                        "status": 2
                      },
                      "SEC_4868": {
                        "fl": "SEC_4868",
                        "status": 2
                      },
                      "SEC_4845": {
                        "fl": "SEC_4845",
                        "status": 2
                      },
                      "SEC_4866": {
                        "fl": "SEC_4866",
                        "status": 1
                      },
                      "SEC_5712": {
                        "fl": "SEC_5712",
                        "status": 1
                      },
                      "SEC_4865": {
                        "fl": "SEC_4865",
                        "status": 1
                      },
                      "SEC_4843": {
                        "fl": "SEC_4843",
                        "status": 2
                      },
                      "SEC_4864": {
                        "fl": "SEC_4864",
                        "status": 2
                      },
                      "SEC_4842": {
                        "fl": "SEC_4842",
                        "status": 1
                      },
                      "SEC_5711": {
                        "fl": "SEC_5711",
                        "status": 2
                      },
                      "SEC_4863": {
                        "fl": "SEC_4863",
                        "status": 2
                      },
                      "SEC_4841": {
                        "fl": "SEC_4841",
                        "status": 2
                      },
                      "MET_883": {
                        "fl": "MET_883",
                        "status": 1
                      },
                      "SEC_4840": {
                        "fl": "SEC_4840",
                        "status": 2
                      },
                      "SEC_4861": {
                        "fl": "SEC_4861",
                        "status": 2
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl443"
                                  },
                                  {
                                    "fl": "fl680003"
                                  },
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fl2010"
                                  },
                                  {
                                    "fl": "fl2006"
                                  },
                                  {
                                    "fl": "SEC_4846"
                                  },
                                  {
                                    "fl": "SEC_4856"
                                  },
                                  {
                                    "fl": "SEC_4837"
                                  },
                                  {
                                    "fl": "SEC_4871"
                                  },
                                  {
                                    "fl": "SEC_4831"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève ProfileDTO2, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "Générale",
                    "duree": "long",
                    "apprentissage": "C",
                    "geo_pref": [
                      "Toulouse",
                      "Bordeaux",
                      "Paris"
                    ],
                    "spe_classes": [
                      "Histoire-Géographie, Géopolitique et Sciences politiques",
                      "Sciences Economiques et Sociales"
                    ],
                    "scores": {
                      "T_ROME_934089965": 1,
                      "T_ROME_1951356737": 1,
                      "T_ROME_600174291": 1,
                      "T_IDEO2_4813": 1,
                      "T_IDEO2_4818": 1,
                      "T_IDEO2_4810": 1,
                      "T_ITM_1238": 1,
                      "T_ITM_950": 1,
                      "T_ITM_1491": 1,
                      "T_ITM_1039": 1,
                      "T_ITM_1030": 1
                    },
                    "moygen": "17.5",
                    "choices": {
                      "SEC_4838": {
                        "fl": "SEC_4838",
                        "status": 2
                      },
                      "MET_490": {
                        "fl": "MET_490",
                        "status": 2
                      },
                      "SEC_4837": {
                        "fl": "SEC_4837",
                        "status": 2
                      },
                      "SEC_4836": {
                        "fl": "SEC_4836",
                        "status": 2
                      },
                      "SEC_4835": {
                        "fl": "SEC_4835",
                        "status": 2
                      },
                      "SEC_4834": {
                        "fl": "SEC_4834",
                        "status": 2
                      },
                      "SEC_4833": {
                        "fl": "SEC_4833",
                        "status": 2
                      },
                      "SEC_4832": {
                        "fl": "SEC_4832",
                        "status": 2
                      },
                      "MET_453": {
                        "fl": "MET_453",
                        "status": 2
                      },
                      "SEC_4831": {
                        "fl": "SEC_4831",
                        "status": 2
                      },
                      "SEC_4839": {
                        "fl": "SEC_4839",
                        "status": 2
                      },
                      "fl2003": {
                        "fl": "fl2003",
                        "status": 1
                      },
                      "MET_7814": {
                        "fl": "MET_7814",
                        "status": 2
                      },
                      "MET_819": {
                        "fl": "MET_819",
                        "status": 2
                      },
                      "MET_213": {
                        "fl": "MET_213",
                        "status": 2
                      },
                      "SEC_4830": {
                        "fl": "SEC_4830",
                        "status": 2
                      },
                      "MET_410": {
                        "fl": "MET_410",
                        "status": 2
                      },
                      "MET_696": {
                        "fl": "MET_696",
                        "status": 2
                      },
                      "MET_179": {
                        "fl": "MET_179",
                        "status": 1
                      },
                      "MET_699": {
                        "fl": "MET_699",
                        "status": 2
                      },
                      "SEC_4871": {
                        "fl": "SEC_4871",
                        "status": 2
                      },
                      "fl2007": {
                        "fl": "fl2007",
                        "status": 1
                      },
                      "SEC_4870": {
                        "fl": "SEC_4870",
                        "status": 2
                      },
                      "MET_414": {
                        "fl": "MET_414",
                        "status": 1
                      },
                      "MET_615": {
                        "fl": "MET_615",
                        "status": 2
                      },
                      "SEC_4869": {
                        "fl": "SEC_4869",
                        "status": 2
                      },
                      "MET_91": {
                        "fl": "MET_91",
                        "status": 2
                      },
                      "SEC_4868": {
                        "fl": "SEC_4868",
                        "status": 2
                      },
                      "SEC_4867": {
                        "fl": "SEC_4867",
                        "status": 2
                      },
                      "MET_93": {
                        "fl": "MET_93",
                        "status": 2
                      },
                      "SEC_4866": {
                        "fl": "SEC_4866",
                        "status": 1
                      },
                      "MET_461": {
                        "fl": "MET_461",
                        "status": 2
                      },
                      "SEC_4865": {
                        "fl": "SEC_4865",
                        "status": 2
                      },
                      "SEC_5712": {
                        "fl": "SEC_5712",
                        "status": 1
                      },
                      "SEC_4864": {
                        "fl": "SEC_4864",
                        "status": 2
                      },
                      "fl2010": {
                        "fl": "fl2010",
                        "status": 1
                      },
                      "SEC_4863": {
                        "fl": "SEC_4863",
                        "status": 2
                      },
                      "MET_620": {
                        "fl": "MET_620",
                        "status": 2
                      },
                      "MET_301": {
                        "fl": "MET_301",
                        "status": 1
                      },
                      "SEC_4862": {
                        "fl": "SEC_4862",
                        "status": 2
                      },
                      "SEC_4861": {
                        "fl": "SEC_4861",
                        "status": 2
                      },
                      "MET_468": {
                        "fl": "MET_468",
                        "status": 2
                      },
                      "MET_742": {
                        "fl": "MET_742",
                        "status": 2
                      },
                      "SEC_4860": {
                        "fl": "SEC_4860",
                        "status": 2
                      },
                      "MET_544": {
                        "fl": "MET_544",
                        "status": 2
                      },
                      "MET_667": {
                        "fl": "MET_667",
                        "status": 2
                      },
                      "MET_150": {
                        "fl": "MET_150",
                        "status": 2
                      },
                      "MET_590": {
                        "fl": "MET_590",
                        "status": 2
                      },
                      "SEC_4859": {
                        "fl": "SEC_4859",
                        "status": 2
                      },
                      "MET_196": {
                        "fl": "MET_196",
                        "status": 2
                      },
                      "SEC_4858": {
                        "fl": "SEC_4858",
                        "status": 2
                      },
                      "SEC_4857": {
                        "fl": "SEC_4857",
                        "status": 2
                      },
                      "MET_195": {
                        "fl": "MET_195",
                        "status": 2
                      },
                      "SEC_4856": {
                        "fl": "SEC_4856",
                        "status": 2
                      },
                      "SEC_4855": {
                        "fl": "SEC_4855",
                        "status": 2
                      },
                      "MET_472": {
                        "fl": "MET_472",
                        "status": 2
                      },
                      "SEC_5668": {
                        "fl": "SEC_5668",
                        "status": 2
                      },
                      "SEC_4854": {
                        "fl": "SEC_4854",
                        "status": 2
                      },
                      "MET_354": {
                        "fl": "MET_354",
                        "status": 2
                      },
                      "MET_112": {
                        "fl": "MET_112",
                        "status": 2
                      },
                      "MET_673": {
                        "fl": "MET_673",
                        "status": 2
                      },
                      "SEC_4853": {
                        "fl": "SEC_4853",
                        "status": 2
                      },
                      "fr90": {
                        "fl": "fr90",
                        "status": 1
                      },
                      "SEC_4852": {
                        "fl": "SEC_4852",
                        "status": 2
                      },
                      "SEC_4851": {
                        "fl": "SEC_4851",
                        "status": 2
                      },
                      "SEC_4850": {
                        "fl": "SEC_4850",
                        "status": 2
                      },
                      "MET_316": {
                        "fl": "MET_316",
                        "status": 2
                      },
                      "SEC_4849": {
                        "fl": "SEC_4849",
                        "status": 2
                      },
                      "SEC_4848": {
                        "fl": "SEC_4848",
                        "status": 2
                      },
                      "SEC_4847": {
                        "fl": "SEC_4847",
                        "status": 2
                      },
                      "SEC_4846": {
                        "fl": "SEC_4846",
                        "status": 2
                      },
                      "SEC_4845": {
                        "fl": "SEC_4845",
                        "status": 2
                      },
                      "MET_880": {
                        "fl": "MET_880",
                        "status": 1
                      },
                      "SEC_4844": {
                        "fl": "SEC_4844",
                        "status": 2
                      },
                      "SEC_4843": {
                        "fl": "SEC_4843",
                        "status": 2
                      },
                      "MET_166": {
                        "fl": "MET_166",
                        "status": 2
                      },
                      "SEC_4842": {
                        "fl": "SEC_4842",
                        "status": 2
                      },
                      "MET_805": {
                        "fl": "MET_805",
                        "status": 2
                      },
                      "SEC_4841": {
                        "fl": "SEC_4841",
                        "status": 2
                      },
                      "MET_64": {
                        "fl": "MET_64",
                        "status": 2
                      },
                      "SEC_4840": {
                        "fl": "SEC_4840",
                        "status": 2
                      },
                      "MET_720": {
                        "fl": "MET_720",
                        "status": 2
                      },
                      "MET_445": {
                        "fl": "MET_445",
                        "status": 2
                      },
                      "MET_687": {
                        "fl": "MET_687",
                        "status": 1
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl680001"
                                  },
                                  {
                                    "fl": "fl680003"
                                  },
                                  {
                                    "fl": "fl32"
                                  },
                                  {
                                    "fl": "fl2089"
                                  },
                                  {
                                    "fl": "fl31"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève ProfileDTO3, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "Générale",
                    "duree": "long",
                    "apprentissage": "C",
                    "geo_pref": [
                      "Montpellier",
                      "Bordeaux"
                    ],
                    "spe_classes": [
                      "Sciences de la vie et de la Terre",
                      "Physique-Chimie"
                    ],
                    "scores": {
                      "T_IDEO2_4810": 1,
                      "T_IDEO2_4813": 1,
                      "T_ROME_637471645": 1,
                      "T_IDEO2_4809": 1,
                      "T_ROME_600174291": 1,
                      "T_ROME_1959553899": 1,
                      "T_ROME_1547781503": 1,
                      "T_ROME_2027610093": 1,
                      "T_ROME_269720073": 1,
                      "T_ROME_934089965": 1,
                      "T_ITM_PERSO6": 1,
                      "T_ITM_PERSO4": 1,
                      "T_ITM_611": 1
                    },
                    "moygen": "15.5",
                    "choices": {
                      "MET_716": {
                        "fl": "MET_716",
                        "status": 1
                      },
                      "fl1002046": {
                        "fl": "fl1002046",
                        "status": 2
                      },
                      "fl1002035": {
                        "fl": "fl1002035",
                        "status": 2
                      },
                      "MET_929": {
                        "fl": "MET_929",
                        "status": 1
                      },
                      "MET_880": {
                        "fl": "MET_880",
                        "status": 1
                      },
                      "fl1002037": {
                        "fl": "fl1002037",
                        "status": 2
                      },
                      "fl1002039": {
                        "fl": "fl1002039",
                        "status": 2
                      },
                      "fr22": {
                        "fl": "fr22",
                        "status": 2
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fl210"
                                  },
                                  {
                                    "fl": "fl2046"
                                  },
                                  {
                                    "fl": "fl2096"
                                  },
                                  {
                                    "fl": "fr83"
                                  },
                                  {
                                    "fl": "SEC_4846"
                                  },
                                  {
                                    "fl": "SEC_4845"
                                  },
                                  {
                                    "fl": "SEC_4848"
                                  },
                                  {
                                    "fl": "SEC_4833"
                                  },
                                  {
                                    "fl": "SEC_4857"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève ProfileDTO4, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "Générale",
                    "duree": "long",
                    "apprentissage": "D",
                    "geo_pref": [
                      "Lyon"
                    ],
                    "spe_classes": [
                      "Histoire-Géographie, Géopolitique et Sciences politiques",
                      "Sciences de la vie et de la Terre"
                    ],
                    "scores": {
                      "T_ROME_84652368": 1,
                      "T_IDEO2_4812": 1,
                      "T_ROME_1391567938": 1,
                      "T_IDEO2_4813": 1,
                      "T_IDEO2_4817": 1,
                      "T_IDEO2_4814": 1,
                      "T_ROME_1814691478": 1,
                      "T_IDEO2_4820": 1,
                      "T_IDEO2_4809": 1,
                      "T_ROME_1825212206": 1,
                      "T_IDEO2_4805": 1,
                      "T_ROME_860291826": 1,
                      "T_ROME_2018646295": 1,
                      "T_ROME_1951356737": 1,
                      "T_ROME_762517279": 1,
                      "T_ROME_1547781503": 1,
                      "T_ROME_2027610093": 1,
                      "T_ROME_803089798": 1,
                      "T_IDEO2_4824": 1,
                      "T_ROME_934089965": 1,
                      "T_ROME_1316643679": 1,
                      "T_ITM_PERSO4": 1,
                      "T_ITM_1030": 1,
                      "T_ITM_1039": 1,
                      "T_ITM_1020": 1,
                      "T_ITM_1025": 1,
                      "T_ITM_1238": 1,
                      "T_ITM_950": 1,
                      "T_ITM_1044": 1,
                      "T_ITM_1284": 1,
                      "T_ITM_762": 1
                    },
                    "moygen": "14.5",
                    "choices": {
                      "SEC_4838": {
                        "fl": "SEC_4838",
                        "status": 2
                      },
                      "SEC_4859": {
                        "fl": "SEC_4859",
                        "status": 2
                      },
                      "SEC_4837": {
                        "fl": "SEC_4837",
                        "status": 2
                      },
                      "SEC_4858": {
                        "fl": "SEC_4858",
                        "status": 1
                      },
                      "SEC_4836": {
                        "fl": "SEC_4836",
                        "status": 2
                      },
                      "SEC_4857": {
                        "fl": "SEC_4857",
                        "status": 1
                      },
                      "SEC_4835": {
                        "fl": "SEC_4835",
                        "status": 2
                      },
                      "SEC_4856": {
                        "fl": "SEC_4856",
                        "status": 2
                      },
                      "SEC_4834": {
                        "fl": "SEC_4834",
                        "status": 2
                      },
                      "SEC_4855": {
                        "fl": "SEC_4855",
                        "status": 2
                      },
                      "SEC_4833": {
                        "fl": "SEC_4833",
                        "status": 2
                      },
                      "SEC_4832": {
                        "fl": "SEC_4832",
                        "status": 2
                      },
                      "SEC_5668": {
                        "fl": "SEC_5668",
                        "status": 2
                      },
                      "SEC_4854": {
                        "fl": "SEC_4854",
                        "status": 2
                      },
                      "SEC_4831": {
                        "fl": "SEC_4831",
                        "status": 2
                      },
                      "SEC_4853": {
                        "fl": "SEC_4853",
                        "status": 2
                      },
                      "SEC_4839": {
                        "fl": "SEC_4839",
                        "status": 2
                      },
                      "fr90": {
                        "fl": "fr90",
                        "status": 1
                      },
                      "SEC_4852": {
                        "fl": "SEC_4852",
                        "status": 2
                      },
                      "SEC_4830": {
                        "fl": "SEC_4830",
                        "status": 2
                      },
                      "SEC_4851": {
                        "fl": "SEC_4851",
                        "status": 2
                      },
                      "SEC_4850": {
                        "fl": "SEC_4850",
                        "status": 2
                      },
                      "SEC_4871": {
                        "fl": "SEC_4871",
                        "status": 2
                      },
                      "fl2007": {
                        "fl": "fl2007",
                        "status": 1
                      },
                      "SEC_4870": {
                        "fl": "SEC_4870",
                        "status": 2
                      },
                      "SEC_4849": {
                        "fl": "SEC_4849",
                        "status": 2
                      },
                      "SEC_4848": {
                        "fl": "SEC_4848",
                        "status": 2
                      },
                      "SEC_4869": {
                        "fl": "SEC_4869",
                        "status": 2
                      },
                      "SEC_4847": {
                        "fl": "SEC_4847",
                        "status": 2
                      },
                      "SEC_4846": {
                        "fl": "SEC_4846",
                        "status": 1
                      },
                      "SEC_4868": {
                        "fl": "SEC_4868",
                        "status": 2
                      },
                      "SEC_4845": {
                        "fl": "SEC_4845",
                        "status": 1
                      },
                      "SEC_4867": {
                        "fl": "SEC_4867",
                        "status": 2
                      },
                      "SEC_4866": {
                        "fl": "SEC_4866",
                        "status": 1
                      },
                      "SEC_4844": {
                        "fl": "SEC_4844",
                        "status": 2
                      },
                      "SEC_4865": {
                        "fl": "SEC_4865",
                        "status": 2
                      },
                      "SEC_4843": {
                        "fl": "SEC_4843",
                        "status": 2
                      },
                      "SEC_5712": {
                        "fl": "SEC_5712",
                        "status": 1
                      },
                      "SEC_4864": {
                        "fl": "SEC_4864",
                        "status": 2
                      },
                      "SEC_4842": {
                        "fl": "SEC_4842",
                        "status": 2
                      },
                      "SEC_5711": {
                        "fl": "SEC_5711",
                        "status": 2
                      },
                      "SEC_4863": {
                        "fl": "SEC_4863",
                        "status": 2
                      },
                      "SEC_4841": {
                        "fl": "SEC_4841",
                        "status": 2
                      },
                      "SEC_4840": {
                        "fl": "SEC_4840",
                        "status": 2
                      },
                      "SEC_4862": {
                        "fl": "SEC_4862",
                        "status": 2
                      },
                      "SEC_4861": {
                        "fl": "SEC_4861",
                        "status": 2
                      },
                      "SEC_4860": {
                        "fl": "SEC_4860",
                        "status": 2
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl2089"
                                  },
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fl2022"
                                  },
                                  {
                                    "fl": "fl2018"
                                  },
                                  {
                                    "fl": "fl2046"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève ProfileDTO5, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "Générale",
                    "duree": "long",
                    "apprentissage": "A",
                    "geo_pref": [
                      "Corte"
                    ],
                    "spe_classes": [
                      "Histoire-Géographie, Géopolitique et Sciences politiques",
                      "Sciences Economiques et Sociales"
                    ],
                    "scores": {
                      "T_IDEO2_4818": 1,
                      "T_IDEO2_4812": 1,
                      "T_ROME_609891024": 1,
                      "T_IDEO2_4815": 1,
                      "T_IDEO2_4814": 1,
                      "T_ROME_1814691478": 1,
                      "T_IDEO2_4809": 1,
                      "T_IDEO2_4808": 1,
                      "T_ROME_1825212206": 1,
                      "T_IDEO2_4806": 1,
                      "T_ROME_898671777": 1,
                      "T_ROME_600174291": 1,
                      "T_ROME_58088585": 1,
                      "T_ROME_860291826": 1,
                      "T_ROME_313545038": 1,
                      "T_ROME_2018646295": 1,
                      "T_ROME_1959553899": 1,
                      "T_ROME_803089798": 1,
                      "T_ROME_1046112128": 1,
                      "T_IDEO2_4827": 1,
                      "T_ROME_731379930": 1,
                      "T_ITM_PERSO1": 1,
                      "T_ITM_636": 1,
                      "T_ITM_821": 1,
                      "T_ITM_1420": 1,
                      "T_ITM_1020": 1,
                      "T_ITM_917": 1,
                      "T_ITM_957": 1,
                      "T_ITM_936": 1
                    },
                    "moygen": "11.5",
                    "choices": {
                      "fl7": {
                        "fl": "fl7",
                        "status": 1
                      },
                      "SEC_4838": {
                        "fl": "SEC_4838",
                        "status": 1
                      },
                      "fl2020": {
                        "fl": "fl2020",
                        "status": 1
                      },
                      "SEC_5712": {
                        "fl": "SEC_5712",
                        "status": 1
                      },
                      "MET_677": {
                        "fl": "MET_677",
                        "status": 1
                      },
                      "MET_522": {
                        "fl": "MET_522",
                        "status": 1
                      },
                      "MET_557": {
                        "fl": "MET_557",
                        "status": 1
                      },
                      "MET_45": {
                        "fl": "MET_45",
                        "status": 1
                      },
                      "fl1002020": {
                        "fl": "fl1002020",
                        "status": 1
                      },
                      "fl2015": {
                        "fl": "fl2015",
                        "status": 1
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fl1002089"
                                  },
                                  {
                                    "fl": "fl1002023"
                                  },
                                  {
                                    "fl": "fl680003"
                                  },
                                  {
                                    "fl": "fl2023"
                                  },
                                  {
                                    "fl": "SEC_4858"
                                  },
                                  {
                                    "fl": "SEC_4846"
                                  },
                                  {
                                    "fl": "SEC_4864"
                                  },
                                  {
                                    "fl": "SEC_4865"
                                  },
                                  {
                                    "fl": "SEC_4835"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève ProfileDTO6, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "Générale",
                    "duree": "long",
                    "geo_pref": [
                      "Bordeaux",
                      "Bayonne",
                      "Toulouse"
                    ],
                    "spe_classes": [
                      "Sciences Economiques et Sociales",
                      "Langues, littératures et cultures étrangères et régionales"
                    ],
                    "scores": {
                      "T_IDEO2_4810": 1,
                      "T_IDEO2_4814": 1,
                      "T_ROME_2018646295": 1,
                      "T_IDEO2_4821": 1,
                      "T_ROME_934089965": 1,
                      "T_ITM_PERSO3": 1,
                      "T_ITM_821": 1,
                      "T_ITM_918": 1,
                      "T_ITM_957": 1
                    },
                    "moygen": "13.5",
                    "choices": {
                      "MET_270": {
                        "fl": "MET_270",
                        "status": 1
                      },
                      "MET_93": {
                        "fl": "MET_93",
                        "status": 1
                      },
                      "fl811504": {
                        "fl": "fl811504",
                        "status": 2
                      },
                      "MET_540": {
                        "fl": "MET_540",
                        "status": 1
                      },
                      "SEC_4864": {
                        "fl": "SEC_4864",
                        "status": 1
                      },
                      "fl810504": {
                        "fl": "fl810504",
                        "status": 1
                      },
                      "MET_66": {
                        "fl": "MET_66",
                        "status": 1
                      },
                      "MET_802": {
                        "fl": "MET_802",
                        "status": 1
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl680003"
                                  },
                                  {
                                    "fl": "fl240"
                                  },
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fl241"
                                  },
                                  {
                                    "fl": "fl2023"
                                  },
                                  {
                                    "fl": "SEC_4865"
                                  },
                                  {
                                    "fl": "SEC_4848"
                                  },
                                  {
                                    "fl": "SEC_5668"
                                  },
                                  {
                                    "fl": "SEC_4852"
                                  },
                                  {
                                    "fl": "SEC_4863"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève ProfileDTO7, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "Générale",
                    "duree": "long",
                    "apprentissage": "C",
                    "geo_pref": [
                      "Rennes"
                    ],
                    "spe_classes": [
                      "Mathématiques",
                      "Physique-Chimie"
                    ],
                    "scores": {
                      "T_IDEO2_4818": 1,
                      "T_IDEO2_4813": 1,
                      "T_IDEO2_4810": 1,
                      "T_IDEO2_4816": 1,
                      "T_IDEO2_4814": 1,
                      "T_ROME_898671777": 1,
                      "T_ROME_1573349427": 1,
                      "T_ROME_58088585": 1,
                      "T_ROME_2018646295": 1,
                      "T_ROME_1959553899": 1,
                      "T_ROME_1547781503": 1,
                      "T_IDEO2_4829": 1,
                      "T_ROME_269720073": 1,
                      "T_ROME_2092381917": 1,
                      "T_IDEO2_4805": 1,
                      "T_IDEO2_4824": 1,
                      "T_ITM_PERSO6": 1,
                      "T_ITM_PERSO1": 1,
                      "T_ITM_1112": 1,
                      "T_ITM_611": 1,
                      "T_ITM_1180": 1,
                      "T_ITM_918": 1,
                      "T_ITM_794": 1,
                      "T_ITM_1420": 1
                    },
                    "moygen": "15",
                    "choices": {
                      "SEC_4838": {
                        "fl": "SEC_4838",
                        "status": 2
                      },
                      "SEC_4859": {
                        "fl": "SEC_4859",
                        "status": 2
                      },
                      "SEC_4836": {
                        "fl": "SEC_4836",
                        "status": 1
                      },
                      "SEC_4858": {
                        "fl": "SEC_4858",
                        "status": 2
                      },
                      "SEC_4857": {
                        "fl": "SEC_4857",
                        "status": 1
                      },
                      "SEC_4835": {
                        "fl": "SEC_4835",
                        "status": 2
                      },
                      "SEC_4834": {
                        "fl": "SEC_4834",
                        "status": 2
                      },
                      "SEC_4856": {
                        "fl": "SEC_4856",
                        "status": 2
                      },
                      "fl210": {
                        "fl": "fl210",
                        "status": 1
                      },
                      "SEC_4855": {
                        "fl": "SEC_4855",
                        "status": 2
                      },
                      "SEC_4833": {
                        "fl": "SEC_4833",
                        "status": 2
                      },
                      "SEC_4832": {
                        "fl": "SEC_4832",
                        "status": 2
                      },
                      "SEC_5668": {
                        "fl": "SEC_5668",
                        "status": 2
                      },
                      "SEC_4854": {
                        "fl": "SEC_4854",
                        "status": 2
                      },
                      "fl230": {
                        "fl": "fl230",
                        "status": 2
                      },
                      "MET_61": {
                        "fl": "MET_61",
                        "status": 1
                      },
                      "SEC_4853": {
                        "fl": "SEC_4853",
                        "status": 2
                      },
                      "SEC_4831": {
                        "fl": "SEC_4831",
                        "status": 2
                      },
                      "fl251": {
                        "fl": "fl251",
                        "status": 1
                      },
                      "fl12": {
                        "fl": "fl12",
                        "status": 1
                      },
                      "fl250": {
                        "fl": "fl250",
                        "status": 1
                      },
                      "fl11": {
                        "fl": "fl11",
                        "status": 1
                      },
                      "SEC_4830": {
                        "fl": "SEC_4830",
                        "status": 1
                      },
                      "SEC_4852": {
                        "fl": "SEC_4852",
                        "status": 2
                      },
                      "SEC_4851": {
                        "fl": "SEC_4851",
                        "status": 2
                      },
                      "SEC_4850": {
                        "fl": "SEC_4850",
                        "status": 2
                      },
                      "SEC_4870": {
                        "fl": "SEC_4870",
                        "status": 2
                      },
                      "MET_931": {
                        "fl": "MET_931",
                        "status": 1
                      },
                      "SEC_4848": {
                        "fl": "SEC_4848",
                        "status": 2
                      },
                      "SEC_4869": {
                        "fl": "SEC_4869",
                        "status": 1
                      },
                      "SEC_4846": {
                        "fl": "SEC_4846",
                        "status": 2
                      },
                      "SEC_4868": {
                        "fl": "SEC_4868",
                        "status": 2
                      },
                      "SEC_4845": {
                        "fl": "SEC_4845",
                        "status": 1
                      },
                      "SEC_4867": {
                        "fl": "SEC_4867",
                        "status": 2
                      },
                      "SEC_4844": {
                        "fl": "SEC_4844",
                        "status": 1
                      },
                      "SEC_4865": {
                        "fl": "SEC_4865",
                        "status": 2
                      },
                      "SEC_4843": {
                        "fl": "SEC_4843",
                        "status": 2
                      },
                      "SEC_5712": {
                        "fl": "SEC_5712",
                        "status": 2
                      },
                      "SEC_4864": {
                        "fl": "SEC_4864",
                        "status": 2
                      },
                      "MET_947": {
                        "fl": "MET_947",
                        "status": 1
                      },
                      "SEC_4863": {
                        "fl": "SEC_4863",
                        "status": 2
                      },
                      "SEC_4841": {
                        "fl": "SEC_4841",
                        "status": 2
                      },
                      "SEC_4840": {
                        "fl": "SEC_4840",
                        "status": 2
                      },
                      "SEC_4861": {
                        "fl": "SEC_4861",
                        "status": 2
                      },
                      "SEC_4860": {
                        "fl": "SEC_4860",
                        "status": 2
                      }
                    }
                  }
                } 
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fr83"
                                  },
                                  {
                                    "fl": "fr22"
                                  },
                                  {
                                    "fl": "fl13"
                                  },
                                  {
                                    "fl": "fl1000210"
                                  },
                                  {
                                    "fl": "SEC_4847"
                                  },
                                  {
                                    "fl": "SEC_4862"
                                  },
                                  {
                                    "fl": "SEC_4871"
                                  },
                                  {
                                    "fl": "SEC_4849"
                                  },
                                  {
                                    "fl": "SEC_4866"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève ProfileDTO8, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "Générale",
                    "duree": "indiff",
                    "apprentissage": "B",
                    "geo_pref": [
                      "Saint-Brieuc",
                      "Brest",
                      "Rennes"
                    ],
                    "spe_classes": [
                      "Sciences de la vie et de la Terre",
                      "Mathématiques"
                    ],
                    "scores": {
                      "T_IDEO2_4813": 1,
                      "T_IDEO2_4806": 1,
                      "T_ROME_1573349427": 1,
                      "T_ROME_58088585": 1,
                      "T_ROME_1959553899": 1,
                      "T_ROME_1547781503": 1,
                      "T_ROME_2027610093": 1,
                      "T_IDEO2_4822": 1,
                      "T_IDEO2_4807": 1,
                      "T_ROME_1665443017": 1,
                      "T_ROME_1316643679": 1,
                      "T_ROME_731379930": 1,
                      "T_ROME_269720073": 1,
                      "T_ITM_PERSO4": 1,
                      "T_ITM_1180": 1,
                      "T_ITM_1248": 1
                    },
                    "moygen": "15.5",
                    "choices": {
                      "MET_570": {
                        "fl": "MET_570",
                        "status": 2
                      },
                      "SEC_4836": {
                        "fl": "SEC_4836",
                        "status": 2
                      },
                      "SEC_4835": {
                        "fl": "SEC_4835",
                        "status": 2
                      },
                      "SEC_4833": {
                        "fl": "SEC_4833",
                        "status": 2
                      },
                      "SEC_4832": {
                        "fl": "SEC_4832",
                        "status": 2
                      },
                      "MET_771": {
                        "fl": "MET_771",
                        "status": 1
                      },
                      "SEC_4831": {
                        "fl": "SEC_4831",
                        "status": 2
                      },
                      "MET_419": {
                        "fl": "MET_419",
                        "status": 1
                      },
                      "MET_38": {
                        "fl": "MET_38",
                        "status": 1
                      },
                      "SEC_4830": {
                        "fl": "SEC_4830",
                        "status": 2
                      },
                      "MET_773": {
                        "fl": "MET_773",
                        "status": 2
                      },
                      "MET_699": {
                        "fl": "MET_699",
                        "status": 1
                      },
                      "MET_812": {
                        "fl": "MET_812",
                        "status": 1
                      },
                      "SEC_4869": {
                        "fl": "SEC_4869",
                        "status": 1
                      },
                      "SEC_4868": {
                        "fl": "SEC_4868",
                        "status": 2
                      },
                      "SEC_4865": {
                        "fl": "SEC_4865",
                        "status": 2
                      },
                      "MET_908": {
                        "fl": "MET_908",
                        "status": 2
                      },
                      "SEC_4863": {
                        "fl": "SEC_4863",
                        "status": 2
                      },
                      "MET_465": {
                        "fl": "MET_465",
                        "status": 1
                      },
                      "MET_820": {
                        "fl": "MET_820",
                        "status": 1
                      },
                      "MET_589": {
                        "fl": "MET_589",
                        "status": 2
                      },
                      "SEC_4861": {
                        "fl": "SEC_4861",
                        "status": 2
                      },
                      "SEC_4860": {
                        "fl": "SEC_4860",
                        "status": 1
                      },
                      "MET_701": {
                        "fl": "MET_701",
                        "status": 1
                      },
                      "MET_824": {
                        "fl": "MET_824",
                        "status": 1
                      },
                      "MET_702": {
                        "fl": "MET_702",
                        "status": 1
                      },
                      "SEC_4859": {
                        "fl": "SEC_4859",
                        "status": 2
                      },
                      "SEC_4858": {
                        "fl": "SEC_4858",
                        "status": 2
                      },
                      "SEC_4857": {
                        "fl": "SEC_4857",
                        "status": 2
                      },
                      "SEC_4856": {
                        "fl": "SEC_4856",
                        "status": 2
                      },
                      "SEC_4855": {
                        "fl": "SEC_4855",
                        "status": 2
                      },
                      "SEC_4854": {
                        "fl": "SEC_4854",
                        "status": 2
                      },
                      "SEC_4853": {
                        "fl": "SEC_4853",
                        "status": 2
                      },
                      "fl810021": {
                        "fl": "fl810021",
                        "status": 1
                      },
                      "MET_716": {
                        "fl": "MET_716",
                        "status": 1
                      },
                      "SEC_4852": {
                        "fl": "SEC_4852",
                        "status": 2
                      },
                      "MET_911": {
                        "fl": "MET_911",
                        "status": 1
                      },
                      "SEC_4848": {
                        "fl": "SEC_4848",
                        "status": 2
                      },
                      "SEC_4847": {
                        "fl": "SEC_4847",
                        "status": 2
                      },
                      "SEC_4846": {
                        "fl": "SEC_4846",
                        "status": 1
                      },
                      "SEC_4845": {
                        "fl": "SEC_4845",
                        "status": 1
                      },
                      "SEC_4844": {
                        "fl": "SEC_4844",
                        "status": 2
                      },
                      "MET_288": {
                        "fl": "MET_288",
                        "status": 1
                      },
                      "SEC_4843": {
                        "fl": "SEC_4843",
                        "status": 2
                      },
                      "MET_929": {
                        "fl": "MET_929",
                        "status": 1
                      },
                      "MET_289": {
                        "fl": "MET_289",
                        "status": 1
                      },
                      "SEC_4840": {
                        "fl": "SEC_4840",
                        "status": 2
                      },
                      "MET_921": {
                        "fl": "MET_921",
                        "status": 1
                      },
                      "MET_602": {
                        "fl": "MET_602",
                        "status": 2
                      },
                      "MET_844": {
                        "fl": "MET_844",
                        "status": 1
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fl210"
                                  },
                                  {
                                    "fl": "fr22"
                                  },
                                  {
                                    "fl": "fl17"
                                  },
                                  {
                                    "fl": "fl680003"
                                  },
                                  {
                                    "fl": "SEC_4841"
                                  },
                                  {
                                    "fl": "SEC_5668"
                                  },
                                  {
                                    "fl": "SEC_4867"
                                  },
                                  {
                                    "fl": "SEC_4851"
                                  },
                                  {
                                    "fl": "SEC_4862"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève ProfileDTO9, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "Générale",
                    "duree": "long",
                    "apprentissage": "C",
                    "geo_pref": [
                      "Saint-Brieuc"
                    ],
                    "spe_classes": [
                      "Mathématiques",
                      "Physique-Chimie"
                    ],
                    "scores": {
                      "T_IDEO2_4818": 1,
                      "T_IDEO2_4813": 1,
                      "T_IDEO2_4816": 1,
                      "T_ROME_637471645": 1,
                      "T_IDEO2_4809": 1,
                      "T_ROME_600174291": 1,
                      "T_ROME_2027610093": 1,
                      "T_ITM_PERSO6": 1,
                      "T_ITM_1112": 1,
                      "T_ITM_636": 1,
                      "T_ITM_611": 1,
                      "T_ITM_1169": 1,
                      "T_ITM_1030": 1,
                      "T_ITM_936": 1,
                      "T_ITM_1067": 1,
                      "T_ITM_794": 1
                    },
                    "moygen": "14.5",
                    "choices": {
                      "MET_738": {
                        "fl": "MET_738",
                        "status": 2
                      },
                      "MET_617": {
                        "fl": "MET_617",
                        "status": 2
                      },
                      "MET_737": {
                        "fl": "MET_737",
                        "status": 1
                      },
                      "MET_75": {
                        "fl": "MET_75",
                        "status": 2
                      },
                      "MET_74": {
                        "fl": "MET_74",
                        "status": 2
                      },
                      "SEC_4871": {
                        "fl": "SEC_4871",
                        "status": 2
                      },
                      "SEC_4870": {
                        "fl": "SEC_4870",
                        "status": 2
                      },
                      "MET_79": {
                        "fl": "MET_79",
                        "status": 2
                      },
                      "MET_733": {
                        "fl": "MET_733",
                        "status": 2
                      },
                      "MET_612": {
                        "fl": "MET_612",
                        "status": 2
                      },
                      "SEC_4869": {
                        "fl": "SEC_4869",
                        "status": 2
                      },
                      "SEC_4868": {
                        "fl": "SEC_4868",
                        "status": 2
                      },
                      "SEC_4867": {
                        "fl": "SEC_4867",
                        "status": 1
                      },
                      "MET_461": {
                        "fl": "MET_461",
                        "status": 2
                      },
                      "SEC_4866": {
                        "fl": "SEC_4866",
                        "status": 2
                      },
                      "MET_95": {
                        "fl": "MET_95",
                        "status": 1
                      },
                      "SEC_4865": {
                        "fl": "SEC_4865",
                        "status": 2
                      },
                      "SEC_5712": {
                        "fl": "SEC_5712",
                        "status": 2
                      },
                      "MET_585": {
                        "fl": "MET_585",
                        "status": 2
                      },
                      "SEC_4864": {
                        "fl": "SEC_4864",
                        "status": 2
                      },
                      "SEC_5711": {
                        "fl": "SEC_5711",
                        "status": 2
                      },
                      "fl6": {
                        "fl": "fl6",
                        "status": 2
                      },
                      "SEC_4863": {
                        "fl": "SEC_4863",
                        "status": 2
                      },
                      "SEC_4862": {
                        "fl": "SEC_4862",
                        "status": 2
                      },
                      "SEC_4861": {
                        "fl": "SEC_4861",
                        "status": 1
                      },
                      "SEC_4860": {
                        "fl": "SEC_4860",
                        "status": 2
                      },
                      "MET_500": {
                        "fl": "MET_500",
                        "status": 2
                      },
                      "MET_863": {
                        "fl": "MET_863",
                        "status": 2
                      },
                      "MET_502": {
                        "fl": "MET_502",
                        "status": 2
                      },
                      "MET_626": {
                        "fl": "MET_626",
                        "status": 2
                      },
                      "MET_61": {
                        "fl": "MET_61",
                        "status": 2
                      },
                      "fl11": {
                        "fl": "fl11",
                        "status": 2
                      },
                      "MET_675": {
                        "fl": "MET_675",
                        "status": 2
                      },
                      "MET_727": {
                        "fl": "MET_727",
                        "status": 2
                      },
                      "MET_728": {
                        "fl": "MET_728",
                        "status": 2
                      },
                      "MET_721": {
                        "fl": "MET_721",
                        "status": 2
                      },
                      "MET_206": {
                        "fl": "MET_206",
                        "status": 1
                      },
                      "MET_602": {
                        "fl": "MET_602",
                        "status": 1
                      },
                      "MET_844": {
                        "fl": "MET_844",
                        "status": 1
                      },
                      "MET_843": {
                        "fl": "MET_843",
                        "status": 2
                      },
                      "MET_725": {
                        "fl": "MET_725",
                        "status": 2
                      },
                      "MET_603": {
                        "fl": "MET_603",
                        "status": 2
                      },
                      "SEC_4838": {
                        "fl": "SEC_4838",
                        "status": 2
                      },
                      "MET_491": {
                        "fl": "MET_491",
                        "status": 2
                      },
                      "SEC_4837": {
                        "fl": "SEC_4837",
                        "status": 2
                      },
                      "SEC_4836": {
                        "fl": "SEC_4836",
                        "status": 2
                      },
                      "SEC_4835": {
                        "fl": "SEC_4835",
                        "status": 1
                      },
                      "MET_492": {
                        "fl": "MET_492",
                        "status": 2
                      },
                      "SEC_4834": {
                        "fl": "SEC_4834",
                        "status": 2
                      },
                      "MET_770": {
                        "fl": "MET_770",
                        "status": 2
                      },
                      "SEC_4833": {
                        "fl": "SEC_4833",
                        "status": 2
                      },
                      "SEC_4832": {
                        "fl": "SEC_4832",
                        "status": 2
                      },
                      "SEC_4831": {
                        "fl": "SEC_4831",
                        "status": 2
                      },
                      "SEC_4839": {
                        "fl": "SEC_4839",
                        "status": 1
                      },
                      "MET_935": {
                        "fl": "MET_935",
                        "status": 2
                      },
                      "SEC_4830": {
                        "fl": "SEC_4830",
                        "status": 2
                      },
                      "MET_499": {
                        "fl": "MET_499",
                        "status": 2
                      },
                      "MET_894": {
                        "fl": "MET_894",
                        "status": 2
                      },
                      "MET_931": {
                        "fl": "MET_931",
                        "status": 1
                      },
                      "MET_933": {
                        "fl": "MET_933",
                        "status": 2
                      },
                      "MET_947": {
                        "fl": "MET_947",
                        "status": 2
                      },
                      "MET_826": {
                        "fl": "MET_826",
                        "status": 2
                      },
                      "MET_707": {
                        "fl": "MET_707",
                        "status": 2
                      },
                      "MET_949": {
                        "fl": "MET_949",
                        "status": 2
                      },
                      "MET_706": {
                        "fl": "MET_706",
                        "status": 2
                      },
                      "MET_943": {
                        "fl": "MET_943",
                        "status": 2
                      },
                      "MET_821": {
                        "fl": "MET_821",
                        "status": 2
                      },
                      "SEC_4859": {
                        "fl": "SEC_4859",
                        "status": 1
                      },
                      "SEC_4858": {
                        "fl": "SEC_4858",
                        "status": 2
                      },
                      "SEC_4857": {
                        "fl": "SEC_4857",
                        "status": 2
                      },
                      "SEC_4856": {
                        "fl": "SEC_4856",
                        "status": 2
                      },
                      "SEC_4855": {
                        "fl": "SEC_4855",
                        "status": 1
                      },
                      "SEC_5668": {
                        "fl": "SEC_5668",
                        "status": 2
                      },
                      "SEC_4854": {
                        "fl": "SEC_4854",
                        "status": 2
                      },
                      "SEC_4853": {
                        "fl": "SEC_4853",
                        "status": 2
                      },
                      "MET_518": {
                        "fl": "MET_518",
                        "status": 2
                      },
                      "MET_638": {
                        "fl": "MET_638",
                        "status": 2
                      },
                      "SEC_4852": {
                        "fl": "SEC_4852",
                        "status": 2
                      },
                      "MET_598": {
                        "fl": "MET_598",
                        "status": 1
                      },
                      "SEC_4851": {
                        "fl": "SEC_4851",
                        "status": 1
                      },
                      "SEC_4850": {
                        "fl": "SEC_4850",
                        "status": 2
                      },
                      "MET_633": {
                        "fl": "MET_633",
                        "status": 2
                      },
                      "MET_911": {
                        "fl": "MET_911",
                        "status": 1
                      },
                      "SEC_4849": {
                        "fl": "SEC_4849",
                        "status": 2
                      },
                      "SEC_4848": {
                        "fl": "SEC_4848",
                        "status": 2
                      },
                      "SEC_4847": {
                        "fl": "SEC_4847",
                        "status": 2
                      },
                      "SEC_4846": {
                        "fl": "SEC_4846",
                        "status": 2
                      },
                      "SEC_4845": {
                        "fl": "SEC_4845",
                        "status": 2
                      },
                      "SEC_4844": {
                        "fl": "SEC_4844",
                        "status": 2
                      },
                      "SEC_4843": {
                        "fl": "SEC_4843",
                        "status": 2
                      },
                      "MET_882": {
                        "fl": "MET_882",
                        "status": 2
                      },
                      "MET_761": {
                        "fl": "MET_761",
                        "status": 1
                      },
                      "MET_760": {
                        "fl": "MET_760",
                        "status": 1
                      },
                      "SEC_4842": {
                        "fl": "SEC_4842",
                        "status": 2
                      },
                      "MET_809": {
                        "fl": "MET_809",
                        "status": 2
                      },
                      "SEC_4841": {
                        "fl": "SEC_4841",
                        "status": 2
                      },
                      "SEC_4840": {
                        "fl": "SEC_4840",
                        "status": 2
                      },
                      "MET_920": {
                        "fl": "MET_920",
                        "status": 1
                      },
                      "MET_887": {
                        "fl": "MET_887",
                        "status": 2
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fl210"
                                  },
                                  {
                                    "fl": "fl680003"
                                  },
                                  {
                                    "fl": "fr83"
                                  },
                                  {
                                    "fl": "fr22"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève ProfileDTO10, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "Générale",
                    "duree": "indiff",
                    "apprentissage": "C",
                    "geo_pref": [
                      "Metz",
                      "Nancy",
                      "Strasbourg"
                    ],
                    "spe_classes": [
                      "Histoire-Géographie, Géopolitique et Sciences politiques",
                      "Sciences Economiques et Sociales"
                    ],
                    "scores": {
                      "T_IDEO2_4818": 1,
                      "T_IDEO2_4819": 1,
                      "T_IDEO2_4812": 1,
                      "T_ROME_1814691478": 1,
                      "T_IDEO2_4809": 1,
                      "T_IDEO2_4808": 1,
                      "T_ROME_1825212206": 1,
                      "T_IDEO2_4806": 1,
                      "T_ROME_1088162470": 1,
                      "T_ROME_600174291": 1,
                      "T_ROME_1991903888": 1,
                      "T_ROME_58088585": 1,
                      "T_ROME_313545038": 1,
                      "T_ROME_803089798": 1,
                      "T_IDEO2_4823": 1,
                      "T_IDEO2_4825": 1,
                      "T_ITM_611": 1,
                      "T_ITM_1169": 1,
                      "T_ITM_918": 1,
                      "T_ITM_1020": 1,
                      "T_ITM_1238": 1,
                      "T_ITM_950": 1,
                      "T_ITM_957": 1,
                      "T_ITM_1094": 1,
                      "T_ITM_936": 1,
                      "T_ITM_1284": 1
                    },
                    "moygen": "16",
                    "msgs": {},
                    "choices": {
                      "MET_691": {
                        "fl": "MET_691",
                        "status": 1
                      },
                      "MET_493": {
                        "fl": "MET_493",
                        "status": 2
                      },
                      "SEC_4847": {
                        "fl": "SEC_4847",
                        "status": 1
                      },
                      "SEC_4846": {
                        "fl": "SEC_4846",
                        "status": 1
                      },
                      "SEC_4845": {
                        "fl": "SEC_4845",
                        "status": 2
                      },
                      "SEC_4833": {
                        "fl": "SEC_4833",
                        "status": 2
                      },
                      "SEC_4839": {
                        "fl": "SEC_4839",
                        "status": 1
                      },
                      "fr89000": {
                        "fl": "fr89000",
                        "status": 1
                      },
                      "fl2003": {
                        "fl": "fl2003",
                        "status": 1
                      },
                      "fl2002": {
                        "fl": "fl2002",
                        "status": 1
                      },
                      "fl810512": {
                        "fl": "fl810512",
                        "status": 1
                      },
                      "SEC_4863": {
                        "fl": "SEC_4863",
                        "status": 2
                      },
                      "MET_313": {
                        "fl": "MET_313",
                        "status": 1
                      },
                      "fl2007": {
                        "fl": "fl2007",
                        "status": 1
                      },
                      "fl2018": {
                        "fl": "fl2018",
                        "status": 1
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl680003"
                                  },
                                  {
                                    "fl": "fl51"
                                  },
                                  {
                                    "fl": "fl2089"
                                  },
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fl25"
                                  },
                                  {
                                    "fl": "SEC_4848"
                                  },
                                  {
                                    "fl": "SEC_4869"
                                  },
                                  {
                                    "fl": "SEC_4852"
                                  },
                                  {
                                    "fl": "SEC_4866"
                                  },
                                  {
                                    "fl": "SEC_4865"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève ProfileDTO11, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "Générale",
                    "duree": "indiff",
                    "apprentissage": "C",
                    "geo_pref": [
                      "Montpellier",
                      "Paris",
                      "Nice",
                      "Barcelonne",
                      "Nancy",
                      "Metz"
                    ],
                    "spe_classes": [
                      "Histoire-Géographie, Géopolitique et Sciences politiques",
                      "Sciences Economiques et Sociales"
                    ],
                    "scores": {
                      "T_IDEO2_4812": 1,
                      "T_IDEO2_4810": 1,
                      "T_IDEO2_4811": 1,
                      "T_IDEO2_4817": 1,
                      "T_IDEO2_4809": 1,
                      "T_IDEO2_4814": 1,
                      "T_ROME_934089965": 1,
                      "T_ROME_731379930": 1,
                      "T_ITM_PERSO3": 1,
                      "T_ITM_821": 1,
                      "T_ITM_1044": 1
                    },
                    "moygen": "10",
                    "msgs": {},
                    "choices": {
                      "SEC_4848": {
                        "fl": "SEC_4848",
                        "status": 2
                      },
                      "SEC_4833": {
                        "fl": "SEC_4833",
                        "status": 2
                      },
                      "SEC_4865": {
                        "fl": "SEC_4865",
                        "status": 2
                      },
                      "SEC_4863": {
                        "fl": "SEC_4863",
                        "status": 2
                      },
                      "SEC_4852": {
                        "fl": "SEC_4852",
                        "status": 2
                      },
                      "SEC_4851": {
                        "fl": "SEC_4851",
                        "status": 2
                      },
                      "MET_267": {
                        "fl": "MET_267",
                        "status": 1
                      },
                      "SEC_4870": {
                        "fl": "SEC_4870",
                        "status": 1
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fl660006"
                                  },
                                  {
                                    "fl": "fl691091"
                                  },
                                  {
                                    "fl": "fl691095"
                                  },
                                  {
                                    "fl": "fl673"
                                  },
                                  {
                                    "fl": "SEC_5668"
                                  },
                                  {
                                    "fl": "SEC_4846"
                                  },
                                  {
                                    "fl": "SEC_4837"
                                  },
                                  {
                                    "fl": "SEC_4856"
                                  },
                                  {
                                    "fl": "SEC_4842"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève ProfileDTO12, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "Générale",
                    "duree": "indiff",
                    "apprentissage": "C",
                    "geo_pref": [],
                    "spe_classes": [
                      "Sciences de la vie et de la Terre",
                      "Physique-Chimie"
                    ],
                    "scores": {
                      "T_IDEO2_4813": 1,
                      "T_IDEO2_4812": 1,
                      "T_IDEO2_4810": 1,
                      "T_IDEO2_4809": 1,
                      "T_ROME_1547781503": 1,
                      "T_ROME_803089798": 1,
                      "T_ROME_1316643679": 1,
                      "T_ROME_731379930": 1,
                      "T_ROME_934089965": 1,
                      "T_ROME_2027610093": 1,
                      "T_ROME_762517279": 1,
                      "T_IDEO2_4806": 1,
                      "T_ITM_PERSO4": 1,
                      "T_ITM_1180": 1,
                      "T_ITM_1169": 1,
                      "T_ITM_918": 1,
                      "T_ITM_PERSO6": 1,
                      "T_ITM_1491": 1
                    },
                    "msgs": {},
                    "choices": {
                      "fl2047": {
                        "fl": "fl2047",
                        "status": 1
                      },
                      "MET_7820": {
                        "fl": "MET_7820",
                        "status": 1
                      },
                      "MET_344": {
                        "fl": "MET_344",
                        "status": 1
                      },
                      "MET_689": {
                        "fl": "MET_689",
                        "status": 1
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fl680003"
                                  },
                                  {
                                    "fl": "fl810509"
                                  },
                                  {
                                    "fl": "fl1000210"
                                  },
                                  {
                                    "fl": "fl1002051"
                                  },
                                  {
                                    "fl": "SEC_4846"
                                  },
                                  {
                                    "fl": "SEC_4869"
                                  },
                                  {
                                    "fl": "SEC_4845"
                                  },
                                  {
                                    "fl": "SEC_4856"
                                  },
                                  {
                                    "fl": "SEC_4832"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève ProfileDTO14, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "Générale",
                    "duree": "long",
                    "apprentissage": "D",
                    "geo_pref": [],
                    "spe_classes": [
                      "Langues, littératures et cultures étrangères et régionales",
                      "Sciences Economiques et Sociales"
                    ],
                    "scores": {
                      "T_IDEO2_4818": 1,
                      "T_IDEO2_4812": 1,
                      "T_IDEO2_4813": 1,
                      "T_ROME_1088162470": 1,
                      "T_IDEO2_4824": 1,
                      "T_ROME_934089965": 1,
                      "T_ROME_731379930": 1,
                      "T_ROME_1316643679": 1,
                      "T_ITM_PERSO4": 1,
                      "T_ITM_1180": 1,
                      "T_ITM_918": 1,
                      "T_ITM_1043": 1
                    },
                    "moygen": "13.5",
                    "msgs": {},
                    "choices": {
                      "SEC_4838": {
                        "fl": "SEC_4838",
                        "status": 2
                      },
                      "SEC_4837": {
                        "fl": "SEC_4837",
                        "status": 2
                      },
                      "SEC_4836": {
                        "fl": "SEC_4836",
                        "status": 2
                      },
                      "SEC_4835": {
                        "fl": "SEC_4835",
                        "status": 2
                      },
                      "SEC_4834": {
                        "fl": "SEC_4834",
                        "status": 2
                      },
                      "SEC_4833": {
                        "fl": "SEC_4833",
                        "status": 2
                      },
                      "SEC_4832": {
                        "fl": "SEC_4832",
                        "status": 1
                      },
                      "MET_892": {
                        "fl": "MET_892",
                        "status": 1
                      },
                      "SEC_4831": {
                        "fl": "SEC_4831",
                        "status": 2
                      },
                      "SEC_4839": {
                        "fl": "SEC_4839",
                        "status": 2
                      },
                      "MET_699": {
                        "fl": "MET_699",
                        "status": 1
                      },
                      "SEC_4871": {
                        "fl": "SEC_4871",
                        "status": 2
                      },
                      "SEC_4870": {
                        "fl": "SEC_4870",
                        "status": 1
                      },
                      "SEC_4869": {
                        "fl": "SEC_4869",
                        "status": 1
                      },
                      "SEC_4868": {
                        "fl": "SEC_4868",
                        "status": 2
                      },
                      "SEC_4867": {
                        "fl": "SEC_4867",
                        "status": 2
                      },
                      "SEC_4866": {
                        "fl": "SEC_4866",
                        "status": 2
                      },
                      "SEC_5712": {
                        "fl": "SEC_5712",
                        "status": 2
                      },
                      "SEC_4865": {
                        "fl": "SEC_4865",
                        "status": 2
                      },
                      "SEC_5711": {
                        "fl": "SEC_5711",
                        "status": 2
                      },
                      "MET_946": {
                        "fl": "MET_946",
                        "status": 1
                      },
                      "SEC_4863": {
                        "fl": "SEC_4863",
                        "status": 2
                      },
                      "SEC_4862": {
                        "fl": "SEC_4862",
                        "status": 2
                      },
                      "SEC_4861": {
                        "fl": "SEC_4861",
                        "status": 2
                      },
                      "SEC_4860": {
                        "fl": "SEC_4860",
                        "status": 2
                      },
                      "MET_109": {
                        "fl": "MET_109",
                        "status": 1
                      },
                      "SEC_4859": {
                        "fl": "SEC_4859",
                        "status": 2
                      },
                      "SEC_4858": {
                        "fl": "SEC_4858",
                        "status": 2
                      },
                      "SEC_4857": {
                        "fl": "SEC_4857",
                        "status": 2
                      },
                      "SEC_4856": {
                        "fl": "SEC_4856",
                        "status": 2
                      },
                      "SEC_4855": {
                        "fl": "SEC_4855",
                        "status": 2
                      },
                      "SEC_5668": {
                        "fl": "SEC_5668",
                        "status": 2
                      },
                      "SEC_4854": {
                        "fl": "SEC_4854",
                        "status": 2
                      },
                      "SEC_4853": {
                        "fl": "SEC_4853",
                        "status": 2
                      },
                      "SEC_4852": {
                        "fl": "SEC_4852",
                        "status": 2
                      },
                      "SEC_4851": {
                        "fl": "SEC_4851",
                        "status": 2
                      },
                      "SEC_4850": {
                        "fl": "SEC_4850",
                        "status": 2
                      },
                      "SEC_4849": {
                        "fl": "SEC_4849",
                        "status": 2
                      },
                      "SEC_4848": {
                        "fl": "SEC_4848",
                        "status": 2
                      },
                      "SEC_4847": {
                        "fl": "SEC_4847",
                        "status": 2
                      },
                      "SEC_4846": {
                        "fl": "SEC_4846",
                        "status": 2
                      },
                      "SEC_4845": {
                        "fl": "SEC_4845",
                        "status": 1
                      },
                      "SEC_4844": {
                        "fl": "SEC_4844",
                        "status": 2
                      },
                      "SEC_4843": {
                        "fl": "SEC_4843",
                        "status": 2
                      },
                      "MET_320": {
                        "fl": "MET_320",
                        "status": 1
                      },
                      "fl1002037": {
                        "fl": "fl1002037",
                        "status": 1
                      },
                      "fl1002039": {
                        "fl": "fl1002039",
                        "status": 1
                      },
                      "SEC_4841": {
                        "fl": "SEC_4841",
                        "status": 2
                      },
                      "SEC_4840": {
                        "fl": "SEC_4840",
                        "status": 2
                      },
                      "fl2037": {
                        "fl": "fl2037",
                        "status": 1
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fr22"
                                  },
                                  {
                                    "fl": "fl680003"
                                  },
                                  {
                                    "fl": "fl2035"
                                  },
                                  {
                                    "fl": "fl240"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève ProfileDTO15, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "STMG",
                    "duree": "indiff",
                    "apprentissage": "A",
                    "geo_pref": [
                      "Paris",
                      "Lyon",
                      "Mâcon",
                      "Villefranche"
                    ],
                    "spe_classes": [
                      "Droit et Economie",
                      "Mercatique"
                    ],
                    "scores": {
                      "T_IDEO2_4812": 1,
                      "T_IDEO2_4810": 1,
                      "T_IDEO2_4814": 1,
                      "T_ROME_1814691478": 1,
                      "T_IDEO2_4820": 1,
                      "T_IDEO2_4809": 1,
                      "T_IDEO2_4806": 1,
                      "T_ROME_1573349427": 1,
                      "T_ROME_58088585": 1,
                      "T_ROME_2018646295": 1,
                      "T_ROME_1951356737": 1,
                      "T_ROME_762517279": 1,
                      "T_ROME_2027610093": 1,
                      "T_IDEO2_4829": 1,
                      "T_ROME_934089965": 1,
                      "T_ITM_PERSO3": 1,
                      "T_ITM_PERSO1": 1,
                      "T_ITM_796": 1
                    },
                    "moygen": "12.5",
                    "msgs": {},
                    "choices": {
                      "SEC_4838": {
                        "fl": "SEC_4838",
                        "status": 2
                      },
                      "SEC_4837": {
                        "fl": "SEC_4837",
                        "status": 2
                      },
                      "SEC_4836": {
                        "fl": "SEC_4836",
                        "status": 2
                      },
                      "SEC_4835": {
                        "fl": "SEC_4835",
                        "status": 2
                      },
                      "SEC_4834": {
                        "fl": "SEC_4834",
                        "status": 2
                      },
                      "SEC_4833": {
                        "fl": "SEC_4833",
                        "status": 2
                      },
                      "SEC_4832": {
                        "fl": "SEC_4832",
                        "status": 2
                      },
                      "SEC_4831": {
                        "fl": "SEC_4831",
                        "status": 1
                      },
                      "SEC_4839": {
                        "fl": "SEC_4839",
                        "status": 2
                      },
                      "MET_539": {
                        "fl": "MET_539",
                        "status": 1
                      },
                      "fl1391": {
                        "fl": "fl1391",
                        "status": 1
                      },
                      "SEC_4830": {
                        "fl": "SEC_4830",
                        "status": 1
                      },
                      "SEC_4871": {
                        "fl": "SEC_4871",
                        "status": 2
                      },
                      "SEC_4870": {
                        "fl": "SEC_4870",
                        "status": 2
                      },
                      "SEC_4869": {
                        "fl": "SEC_4869",
                        "status": 1
                      },
                      "SEC_4868": {
                        "fl": "SEC_4868",
                        "status": 2
                      },
                      "SEC_4867": {
                        "fl": "SEC_4867",
                        "status": 2
                      },
                      "SEC_4866": {
                        "fl": "SEC_4866",
                        "status": 2
                      },
                      "SEC_4865": {
                        "fl": "SEC_4865",
                        "status": 1
                      },
                      "MET_541": {
                        "fl": "MET_541",
                        "status": 1
                      },
                      "SEC_5712": {
                        "fl": "SEC_5712",
                        "status": 2
                      },
                      "SEC_4864": {
                        "fl": "SEC_4864",
                        "status": 1
                      },
                      "SEC_5711": {
                        "fl": "SEC_5711",
                        "status": 2
                      },
                      "SEC_4863": {
                        "fl": "SEC_4863",
                        "status": 2
                      },
                      "SEC_4862": {
                        "fl": "SEC_4862",
                        "status": 2
                      },
                      "SEC_4861": {
                        "fl": "SEC_4861",
                        "status": 2
                      },
                      "SEC_4860": {
                        "fl": "SEC_4860",
                        "status": 2
                      },
                      "SEC_4859": {
                        "fl": "SEC_4859",
                        "status": 2
                      },
                      "SEC_4858": {
                        "fl": "SEC_4858",
                        "status": 2
                      },
                      "SEC_4857": {
                        "fl": "SEC_4857",
                        "status": 2
                      },
                      "SEC_4856": {
                        "fl": "SEC_4856",
                        "status": 2
                      },
                      "SEC_4855": {
                        "fl": "SEC_4855",
                        "status": 2
                      },
                      "SEC_5668": {
                        "fl": "SEC_5668",
                        "status": 1
                      },
                      "SEC_4854": {
                        "fl": "SEC_4854",
                        "status": 2
                      },
                      "SEC_4853": {
                        "fl": "SEC_4853",
                        "status": 1
                      },
                      "fl391": {
                        "fl": "fl391",
                        "status": 1
                      },
                      "fl390": {
                        "fl": "fl390",
                        "status": 1
                      },
                      "SEC_4852": {
                        "fl": "SEC_4852",
                        "status": 2
                      },
                      "SEC_4851": {
                        "fl": "SEC_4851",
                        "status": 2
                      },
                      "SEC_4850": {
                        "fl": "SEC_4850",
                        "status": 1
                      },
                      "SEC_4849": {
                        "fl": "SEC_4849",
                        "status": 2
                      },
                      "SEC_4848": {
                        "fl": "SEC_4848",
                        "status": 2
                      },
                      "SEC_4847": {
                        "fl": "SEC_4847",
                        "status": 2
                      },
                      "SEC_4846": {
                        "fl": "SEC_4846",
                        "status": 2
                      },
                      "SEC_4845": {
                        "fl": "SEC_4845",
                        "status": 1
                      },
                      "SEC_4844": {
                        "fl": "SEC_4844",
                        "status": 1
                      },
                      "fl11418": {
                        "fl": "fl11418",
                        "status": 1
                      },
                      "SEC_4843": {
                        "fl": "SEC_4843",
                        "status": 1
                      },
                      "SEC_4842": {
                        "fl": "SEC_4842",
                        "status": 2
                      },
                      "fl1390": {
                        "fl": "fl1390",
                        "status": 1
                      },
                      "SEC_4841": {
                        "fl": "SEC_4841",
                        "status": 2
                      },
                      "SEC_4840": {
                        "fl": "SEC_4840",
                        "status": 2
                      },
                      "MET_25": {
                        "fl": "MET_25",
                        "status": 1
                      },
                      "fl1388": {
                        "fl": "fl1388",
                        "status": 1
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl241"
                                  },
                                  {
                                    "fl": "fl23"
                                  },
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fl691134"
                                  },
                                  {
                                    "fl": "fl691052"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève ProfileDTO16, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "Générale",
                    "geo_pref": [
                      "Nice"
                    ],
                    "spe_classes": [
                      "Sciences Economiques et Sociales",
                      "Histoire-Géographie, Géopolitique et Sciences politiques"
                    ],
                    "scores": {
                      "T_IDEO2_4812": 1,
                      "T_IDEO2_4807": 1,
                      "T_IDEO2_4828": 1,
                      "T_ROME_731379930": 1,
                      "T_ROME_1316643679": 1,
                      "T_ITM_1180": 1
                    },
                    "moygen": "14",
                    "msgs": {},
                    "choices": {
                      "MET_221": {
                        "fl": "MET_221",
                        "status": 1
                      },
                      "MET_765": {
                        "fl": "MET_765",
                        "status": 1
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl2090"
                                  },
                                  {
                                    "fl": "fl550005"
                                  },
                                  {
                                    "fl": "fl810015"
                                  },
                                  {
                                    "fl": "fl550002"
                                  },
                                  {
                                    "fl": "fl550004"
                                  },
                                  {
                                    "fl": "SEC_4869"
                                  },
                                  {
                                    "fl": "SEC_4846"
                                  },
                                  {
                                    "fl": "SEC_4832"
                                  },
                                  {
                                    "fl": "SEC_4845"
                                  },
                                  {
                                    "fl": "SEC_4833"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève ProfileDTO17, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "Générale",
                    "duree": "long",
                    "apprentissage": "B",
                    "geo_pref": [
                      "Corte",
                      "Paris",
                      "Montpellier",
                      "Lyon"
                    ],
                    "spe_classes": [
                      "Langues, littératures et cultures étrangères et régionales",
                      "Histoire des Arts"
                    ],
                    "scores": {
                      "T_IDEO2_4818": 1,
                      "T_IDEO2_4812": 1,
                      "T_ROME_1391567938": 1,
                      "T_ROME_609891024": 1,
                      "T_IDEO2_4810": 1,
                      "T_IDEO2_4817": 1,
                      "T_IDEO2_4814": 1,
                      "T_IDEO2_4815": 1,
                      "T_ROME_1814691478": 1,
                      "T_IDEO2_4809": 1,
                      "T_ROME_1825212206": 1,
                      "T_ROME_803089798": 1,
                      "T_ROME_1046112128": 1,
                      "T_ROME_934089965": 1,
                      "T_IDEO2_4821": 1,
                      "T_ITM_PERSO1": 1,
                      "T_ITM_933": 1,
                      "T_ITM_917": 1,
                      "T_ITM_918": 1,
                      "T_ITM_821": 1
                    },
                    "moygen": "15.5",
                    "msgs": {},
                    "choices": {
                      "SEC_4846": {
                        "fl": "SEC_4846",
                        "status": 2
                      },
                      "SEC_4833": {
                        "fl": "SEC_4833",
                        "status": 1
                      },
                      "MET_51": {
                        "fl": "MET_51",
                        "status": 1
                      },
                      "SEC_5712": {
                        "fl": "SEC_5712",
                        "status": 1
                      },
                      "SEC_4865": {
                        "fl": "SEC_4865",
                        "status": 1
                      },
                      "SEC_4864": {
                        "fl": "SEC_4864",
                        "status": 1
                      },
                      "fl2029": {
                        "fl": "fl2029",
                        "status": 1
                      },
                      "SEC_4870": {
                        "fl": "SEC_4870",
                        "status": 1
                      },
                      "MET_557": {
                        "fl": "MET_557",
                        "status": 1
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl2028"
                                  },
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fl31"
                                  },
                                  {
                                    "fl": "fl680003"
                                  },
                                  {
                                    "fl": "fl32"
                                  },
                                  {
                                    "fl": "SEC_4858"
                                  },
                                  {
                                    "fl": "SEC_4863"
                                  },
                                  {
                                    "fl": "SEC_5668"
                                  },
                                  {
                                    "fl": "SEC_4848"
                                  },
                                  {
                                    "fl": "SEC_4852"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève ProfileDTO18, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "Générale",
                    "duree": "indiff",
                    "apprentissage": "B",
                    "geo_pref": [
                      "Paris",
                      "Lille",
                      "Montpellier",
                      "Lyon",
                      "Toulouse",
                      "Bordeaux"
                    ],
                    "spe_classes": [
                      "Cinéma-Audiovisuel"
                    ],
                    "scores": {
                      "T_ROME_934089965": 1,
                      "T_IDEO2_4818": 1,
                      "T_IDEO2_4810": 1,
                      "T_ITM_1420": 1,
                      "T_ITM_1030": 1,
                      "T_ITM_918": 1
                    },
                    "moygen": "12.5",
                    "msgs": {},
                    "choices": {
                      "fl452": {
                        "fl": "fl452",
                        "status": 1
                      },
                      "MET_585": {
                        "fl": "MET_585",
                        "status": 1
                      },
                      "MET_621": {
                        "fl": "MET_621",
                        "status": 1
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl2010"
                                  },
                                  {
                                    "fl": "fl691142"
                                  },
                                  {
                                    "fl": "fl382"
                                  },
                                  {
                                    "fl": "fl454"
                                  },
                                  {
                                    "fl": "fl2027"
                                  },
                                  {
                                    "fl": "SEC_4838"
                                  },
                                  {
                                    "fl": "SEC_4858"
                                  },
                                  {
                                    "fl": "SEC_5712"
                                  },
                                  {
                                    "fl": "SEC_4850"
                                  },
                                  {
                                    "fl": "SEC_4864"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève ProfileDTO19, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "Générale",
                    "duree": "indiff",
                    "apprentissage": "A",
                    "geo_pref": [
                      "Lyon"
                    ],
                    "spe_classes": [
                      "Arts Plastiques",
                      "Numérique et Sciences Informatiques"
                    ],
                    "scores": {
                      "T_IDEO2_4818": 1,
                      "T_IDEO2_4812": 1,
                      "T_ROME_1391567938": 1,
                      "T_IDEO2_4817": 1,
                      "T_ROME_1814691478": 1,
                      "T_ROME_637471645": 1,
                      "T_IDEO2_4809": 1,
                      "T_ROME_898671777": 1,
                      "T_IDEO2_4806": 1,
                      "T_ROME_600174291": 1,
                      "T_ROME_58088585": 1,
                      "T_ROME_1959553899": 1,
                      "T_ROME_762517279": 1,
                      "T_ROME_1547781503": 1,
                      "T_IDEO2_4829": 1,
                      "T_ROME_269720073": 1,
                      "T_IDEO2_4821": 1,
                      "T_IDEO2_4827": 1,
                      "T_IDEO2_4825": 1,
                      "T_ITM_1534": 1,
                      "T_ITM_PERSO1": 1,
                      "T_ITM_1020": 1,
                      "T_ITM_1025": 1,
                      "T_ITM_1238": 1,
                      "T_ITM_957": 1,
                      "T_ITM_936": 1
                    },
                    "moygen": "16",
                    "msgs": {},
                    "choices": {
                      "SEC_4859": {
                        "fl": "SEC_4859",
                        "status": 1
                      },
                      "SEC_4858": {
                        "fl": "SEC_4858",
                        "status": 2
                      },
                      "SEC_4835": {
                        "fl": "SEC_4835",
                        "status": 1
                      },
                      "MET_450": {
                        "fl": "MET_450",
                        "status": 1
                      },
                      "SEC_5668": {
                        "fl": "SEC_5668",
                        "status": 1
                      },
                      "fl271": {
                        "fl": "fl271",
                        "status": 1
                      },
                      "fl270": {
                        "fl": "fl270",
                        "status": 1
                      },
                      "fl850010": {
                        "fl": "fl850010",
                        "status": 1
                      },
                      "fl840009": {
                        "fl": "fl840009",
                        "status": 1
                      },
                      "SEC_4852": {
                        "fl": "SEC_4852",
                        "status": 2
                      },
                      "SEC_4830": {
                        "fl": "SEC_4830",
                        "status": 1
                      },
                      "fl840003": {
                        "fl": "fl840003",
                        "status": 1
                      },
                      "SEC_4848": {
                        "fl": "SEC_4848",
                        "status": 2
                      },
                      "SEC_4846": {
                        "fl": "SEC_4846",
                        "status": 2
                      },
                      "SEC_4867": {
                        "fl": "SEC_4867",
                        "status": 1
                      },
                      "fl850009": {
                        "fl": "fl850009",
                        "status": 1
                      },
                      "SEC_5712": {
                        "fl": "SEC_5712",
                        "status": 1
                      },
                      "SEC_4865": {
                        "fl": "SEC_4865",
                        "status": 2
                      },
                      "SEC_4864": {
                        "fl": "SEC_4864",
                        "status": 1
                      },
                      "MET_5": {
                        "fl": "MET_5",
                        "status": 1
                      },
                      "fl850003": {
                        "fl": "fl850003",
                        "status": 1
                      },
                      "MET_928": {
                        "fl": "MET_928",
                        "status": 1
                      },
                      "SEC_4863": {
                        "fl": "SEC_4863",
                        "status": 2
                      },
                      "fl840010": {
                        "fl": "fl840010",
                        "status": 1
                      },
                      "MET_568": {
                        "fl": "MET_568",
                        "status": 1
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl680003"
                                  },
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fl840004"
                                  },
                                  {
                                    "fl": "fl210"
                                  },
                                  {
                                    "fl": "fl19"
                                  },
                                  {
                                    "fl": "SEC_4855"
                                  },
                                  {
                                    "fl": "SEC_4861"
                                  },
                                  {
                                    "fl": "SEC_4844"
                                  },
                                  {
                                    "fl": "SEC_4838"
                                  },
                                  {
                                    "fl": "SEC_4860"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève Bon élève G art, lettres et langues, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "Générale",
                    "duree": "long",
                    "geo_pref": [
                      "Lyon",
                      "Clermont-Ferrand",
                      "Valence (16460)"
                    ],
                    "spe_classes": [
                      "Arts Plastiques",
                      "Langues, littératures et cultures étrangères et régionales"
                    ],
                    "scores": {
                      "T_IDEO2_4818": 1,
                      "T_IDEO2_4812": 1,
                      "T_IDEO2_4810": 1,
                      "T_IDEO2_4821": 1,
                      "T_ITM_PERSO1": 1,
                      "T_ITM_917": 1,
                      "T_ITM_918": 1
                    },
                    "moygen": "14",
                    "msgs": {},
                    "choices": {
                      "fl10427": {
                        "fl": "fl10427",
                        "status": 1
                      },
                      "MET_93": {
                        "fl": "MET_93",
                        "status": 1
                      },
                      "fl2053": {
                        "fl": "fl2053",
                        "status": 1
                      },
                      "MET_450": {
                        "fl": "MET_450",
                        "status": 1
                      },
                      "fl2030": {
                        "fl": "fl2030",
                        "status": 1
                      },
                      "fl2052": {
                        "fl": "fl2052",
                        "status": 1
                      },
                      "MET_453": {
                        "fl": "MET_453",
                        "status": 1
                      },
                      "MET_768": {
                        "fl": "MET_768",
                        "status": 1
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0,
                                "error": null,
                                "userMessage": null
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl2029"
                                  },
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fl2028"
                                  },
                                  {
                                    "fl": "fl680003"
                                  },
                                  {
                                    "fl": "fl31"
                                  },
                                  {
                                    "fl": "SEC_4858"
                                  },
                                  {
                                    "fl": "SEC_4830"
                                  },
                                  {
                                    "fl": "SEC_4865"
                                  },
                                  {
                                    "fl": "SEC_5712"
                                  },
                                  {
                                    "fl": "SEC_4844"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève ProfileDTO20, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "Générale",
                    "duree": "long",
                    "apprentissage": "C",
                    "geo_pref": [
                      "Paris",
                      "Lyon"
                    ],
                    "spe_classes": [
                      "Histoire-Géographie, Géopolitique et Sciences politiques",
                      "Langues, littératures et cultures étrangères et régionales"
                    ],
                    "scores": {
                      "T_IDEO2_4818": 1,
                      "T_IDEO2_4819": 1,
                      "T_IDEO2_4810": 1,
                      "T_IDEO2_4814": 1,
                      "T_ROME_749075906": 1,
                      "T_ROME_58088585": 1,
                      "T_ROME_2018646295": 1,
                      "T_ROME_762517279": 1,
                      "T_ROME_2027610093": 1,
                      "T_ITM_PERSO9": 1,
                      "T_ITM_821": 1,
                      "T_ITM_1030": 1,
                      "T_ITM_1020": 1,
                      "T_ITM_950": 1,
                      "T_ITM_917": 1,
                      "T_ITM_918": 1,
                      "T_ITM_933": 1,
                      "T_ITM_1043": 1
                    },
                    "moygen": "13",
                    "msgs": {},
                    "choices": {
                      "MET_590": {
                        "fl": "MET_590",
                        "status": 2
                      },
                      "MET_172": {
                        "fl": "MET_172",
                        "status": 2
                      },
                      "MET_451": {
                        "fl": "MET_451",
                        "status": 1
                      },
                      "MET_692": {
                        "fl": "MET_692",
                        "status": 1
                      },
                      "SEC_4833": {
                        "fl": "SEC_4833",
                        "status": 2
                      },
                      "MET_112": {
                        "fl": "MET_112",
                        "status": 2
                      },
                      "MET_192": {
                        "fl": "MET_192",
                        "status": 2
                      },
                      "MET_518": {
                        "fl": "MET_518",
                        "status": 2
                      },
                      "MET_918": {
                        "fl": "MET_918",
                        "status": 2
                      },
                      "MET_919": {
                        "fl": "MET_919",
                        "status": 1
                      },
                      "MET_31": {
                        "fl": "MET_31",
                        "status": 2
                      },
                      "MET_158": {
                        "fl": "MET_158",
                        "status": 2
                      },
                      "MET_774": {
                        "fl": "MET_774",
                        "status": 2
                      },
                      "MET_312": {
                        "fl": "MET_312",
                        "status": 2
                      },
                      "MET_52": {
                        "fl": "MET_52",
                        "status": 2
                      },
                      "MET_77": {
                        "fl": "MET_77",
                        "status": 2
                      },
                      "MET_413": {
                        "fl": "MET_413",
                        "status": 2
                      },
                      "MET_699": {
                        "fl": "MET_699",
                        "status": 2
                      },
                      "MET_879": {
                        "fl": "MET_879",
                        "status": 1
                      },
                      "MET_580": {
                        "fl": "MET_580",
                        "status": 2
                      },
                      "MET_261": {
                        "fl": "MET_261",
                        "status": 2
                      },
                      "MET_880": {
                        "fl": "MET_880",
                        "status": 2
                      },
                      "MET_73": {
                        "fl": "MET_73",
                        "status": 1
                      },
                      "MET_166": {
                        "fl": "MET_166",
                        "status": 2
                      },
                      "MET_441": {
                        "fl": "MET_441",
                        "status": 1
                      },
                      "MET_407": {
                        "fl": "MET_407",
                        "status": 2
                      },
                      "MET_7744": {
                        "fl": "MET_7744",
                        "status": 2
                      },
                      "MET_301": {
                        "fl": "MET_301",
                        "status": 1
                      },
                      "MET_784": {
                        "fl": "MET_784",
                        "status": 2
                      },
                      "MET_41": {
                        "fl": "MET_41",
                        "status": 2
                      },
                      "MET_465": {
                        "fl": "MET_465",
                        "status": 2
                      },
                      "SEC_4840": {
                        "fl": "SEC_4840",
                        "status": 2
                      },
                      "MET_941": {
                        "fl": "MET_941",
                        "status": 2
                      },
                      "MET_589": {
                        "fl": "MET_589",
                        "status": 2
                      },
                      "MET_820": {
                        "fl": "MET_820",
                        "status": 2
                      },
                      "MET_687": {
                        "fl": "MET_687",
                        "status": 1
                      },
                      "MET_445": {
                        "fl": "MET_445",
                        "status": 1
                      },
                      "MET_844": {
                        "fl": "MET_844",
                        "status": 2
                      },
                      "MET_701": {
                        "fl": "MET_701",
                        "status": 2
                      },
                      "MET_109": {
                        "fl": "MET_109",
                        "status": 2
                      },
                      "MET_449": {
                        "fl": "MET_449",
                        "status": 2
                      },
                      "MET_922": {
                        "fl": "MET_922",
                        "status": 1
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl680003"
                                  },
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fr22"
                                  },
                                  {
                                    "fl": "fl2029"
                                  },
                                  {
                                    "fl": "fl2012"
                                  },
                                  {
                                    "fl": "SEC_4846"
                                  },
                                  {
                                    "fl": "SEC_4870"
                                  },
                                  {
                                    "fl": "SEC_4858"
                                  },
                                  {
                                    "fl": "SEC_5712"
                                  },
                                  {
                                    "fl": "SEC_4845"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève ProfileDTO21, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "Générale",
                    "duree": "indiff",
                    "apprentissage": "C",
                    "geo_pref": [
                      "Lyon",
                      "Montpellier",
                      "Toulouse",
                      "Bordeaux"
                    ],
                    "spe_classes": [
                      "Arts Plastiques",
                      "Mathématiques"
                    ],
                    "scores": {
                      "T_IDEO2_4818": 1,
                      "T_IDEO2_4810": 1,
                      "T_ROME_898671777": 1,
                      "T_ROME_762517279": 1,
                      "T_ROME_2027610093": 1,
                      "T_IDEO2_4829": 1,
                      "T_ROME_269720073": 1,
                      "T_ROME_934089965": 1,
                      "T_IDEO2_4826": 1,
                      "T_ITM_PERSO1": 1,
                      "T_ITM_918": 1,
                      "T_ITM_1044": 1
                    },
                    "moygen": "",
                    "msgs": {},
                    "choices": {
                      "fl2024": {
                        "fl": "fl2024",
                        "status": 1
                      },
                      "fr90": {
                        "fl": "fr90",
                        "status": 1
                      },
                      "fl2030": {
                        "fl": "fl2030",
                        "status": 1
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl32"
                                  },
                                  {
                                    "fl": "fl31"
                                  },
                                  {
                                    "fl": "fl2010"
                                  },
                                  {
                                    "fl": "fl2029"
                                  },
                                  {
                                    "fl": "fl2028"
                                  },
                                  {
                                    "fl": "SEC_4858"
                                  },
                                  {
                                    "fl": "SEC_4846"
                                  },
                                  {
                                    "fl": "SEC_4830"
                                  },
                                  {
                                    "fl": "SEC_5712"
                                  },
                                  {
                                    "fl": "SEC_4865"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève ProfileDTO22, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "STMG",
                    "duree": "court",
                    "apprentissage": "A",
                    "geo_pref": [
                      "Poitiers"
                    ],
                    "spe_classes": [
                      "Mercatique",
                      "Droit et Economie"
                    ],
                    "scores": {
                      "T_ROME_637471645": 1,
                      "T_ROME_326548351": 1,
                      "T_IDEO2_4806": 1,
                      "T_ROME_1573349427": 1,
                      "T_ROME_1991903888": 1,
                      "T_ROME_1959553899": 1,
                      "T_IDEO2_4825": 1,
                      "T_IDEO2_4822": 1,
                      "T_IDEO2_4826": 1,
                      "T_ITM_PERSO7": 1,
                      "T_ITM_1534": 1,
                      "T_ITM_794": 1
                    },
                    "moygen": "12.5",
                    "msgs": {},
                    "choices": {
                      "MET_528": {
                        "fl": "MET_528",
                        "status": 1
                      },
                      "SEC_4859": {
                        "fl": "SEC_4859",
                        "status": 1
                      },
                      "MET_949": {
                        "fl": "MET_949",
                        "status": 1
                      },
                      "SEC_4867": {
                        "fl": "SEC_4867",
                        "status": 1
                      },
                      "fl11407": {
                        "fl": "fl11407",
                        "status": 1
                      },
                      "SEC_4831": {
                        "fl": "SEC_4831",
                        "status": 1
                      },
                      "MET_499": {
                        "fl": "MET_499",
                        "status": 1
                      },
                      "MET_401": {
                        "fl": "MET_401",
                        "status": 1
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fr78"
                                  },
                                  {
                                    "fl": "fl481006"
                                  },
                                  {
                                    "fl": "fl691029"
                                  },
                                  {
                                    "fl": "fl810012"
                                  },
                                  {
                                    "fl": "SEC_4855"
                                  },
                                  {
                                    "fl": "SEC_4861"
                                  },
                                  {
                                    "fl": "SEC_4868"
                                  },
                                  {
                                    "fl": "SEC_4843"
                                  },
                                  {
                                    "fl": "SEC_4848"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève ProfileDTO23, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "STMG",
                    "duree": "court",
                    "apprentissage": "C",
                    "geo_pref": [],
                    "spe_classes": [
                      "Gestion et Finance",
                      "Droit et Economie"
                    ],
                    "scores": {
                      "T_IDEO2_4812": 1,
                      "T_IDEO2_4814": 1,
                      "T_ITM_PERSO3": 1,
                      "T_ITM_957": 1
                    },
                    "msgs": {},
                    "choices": {
                      "fl811502": {
                        "fl": "fl811502",
                        "status": 2
                      },
                      "fl810502": {
                        "fl": "fl810502",
                        "status": 1
                      },
                      "MET_697": {
                        "fl": "MET_697",
                        "status": 1
                      },
                      "MET_798": {
                        "fl": "MET_798",
                        "status": 1
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl241"
                                  },
                                  {
                                    "fl": "fl23"
                                  },
                                  {
                                    "fl": "fl810511"
                                  },
                                  {
                                    "fl": "fl720003"
                                  },
                                  {
                                    "fl": "fl691134"
                                  },
                                  {
                                    "fl": "SEC_4865"
                                  },
                                  {
                                    "fl": "SEC_4852"
                                  },
                                  {
                                    "fl": "SEC_4848"
                                  },
                                  {
                                    "fl": "SEC_4863"
                                  },
                                  {
                                    "fl": "SEC_5668"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève ProfileDTO24, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "Générale",
                    "duree": "long",
                    "apprentissage": "C",
                    "geo_pref": [
                      "Rennes",
                      "Lyon"
                    ],
                    "spe_classes": [
                      "Mathématiques",
                      "Physique-Chimie"
                    ],
                    "scores": {
                      "T_ROME_600174291": 1,
                      "T_IDEO2_4809": 1,
                      "T_ROME_860291826": 1,
                      "T_IDEO2_4827": 1,
                      "T_IDEO2_4817": 1,
                      "T_IDEO2_4812": 1,
                      "T_ITM_936": 1,
                      "T_ITM_794": 1
                    },
                    "moygen": "15.5",
                    "msgs": {},
                    "choices": {
                      "SEC_4859": {
                        "fl": "SEC_4859",
                        "status": 2
                      },
                      "SEC_4857": {
                        "fl": "SEC_4857",
                        "status": 2
                      },
                      "fl210": {
                        "fl": "fl210",
                        "status": 1
                      },
                      "SEC_4856": {
                        "fl": "SEC_4856",
                        "status": 2
                      },
                      "SEC_4855": {
                        "fl": "SEC_4855",
                        "status": 2
                      },
                      "SEC_5668": {
                        "fl": "SEC_5668",
                        "status": 2
                      },
                      "MET_672": {
                        "fl": "MET_672",
                        "status": 1
                      },
                      "SEC_4852": {
                        "fl": "SEC_4852",
                        "status": 2
                      },
                      "MET_356": {
                        "fl": "MET_356",
                        "status": 1
                      },
                      "SEC_4871": {
                        "fl": "SEC_4871",
                        "status": 2
                      },
                      "SEC_4870": {
                        "fl": "SEC_4870",
                        "status": 2
                      },
                      "fl1002020": {
                        "fl": "fl1002020",
                        "status": 1
                      },
                      "SEC_4848": {
                        "fl": "SEC_4848",
                        "status": 2
                      },
                      "SEC_4868": {
                        "fl": "SEC_4868",
                        "status": 2
                      },
                      "MET_481": {
                        "fl": "MET_481",
                        "status": 1
                      },
                      "SEC_4845": {
                        "fl": "SEC_4845",
                        "status": 2
                      },
                      "SEC_4867": {
                        "fl": "SEC_4867",
                        "status": 2
                      },
                      "MET_167": {
                        "fl": "MET_167",
                        "status": 1
                      },
                      "MET_95": {
                        "fl": "MET_95",
                        "status": 1
                      },
                      "SEC_4843": {
                        "fl": "SEC_4843",
                        "status": 2
                      },
                      "SEC_4865": {
                        "fl": "SEC_4865",
                        "status": 2
                      },
                      "MET_221": {
                        "fl": "MET_221",
                        "status": 1
                      },
                      "MET_29": {
                        "fl": "MET_29",
                        "status": 1
                      },
                      "fl2095": {
                        "fl": "fl2095",
                        "status": 1
                      },
                      "SEC_4863": {
                        "fl": "SEC_4863",
                        "status": 2
                      },
                      "SEC_4841": {
                        "fl": "SEC_4841",
                        "status": 2
                      },
                      "SEC_4862": {
                        "fl": "SEC_4862",
                        "status": 1
                      },
                      "SEC_4861": {
                        "fl": "SEC_4861",
                        "status": 2
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fr22"
                                  },
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fr83"
                                  },
                                  {
                                    "fl": "fl2046"
                                  },
                                  {
                                    "fl": "fl13"
                                  },
                                  {
                                    "fl": "SEC_4846"
                                  },
                                  {
                                    "fl": "SEC_4869"
                                  },
                                  {
                                    "fl": "SEC_4835"
                                  },
                                  {
                                    "fl": "SEC_4832"
                                  },
                                  {
                                    "fl": "SEC_4833"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève ProfileDTO25, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "Générale",
                    "duree": "long",
                    "geo_pref": [
                      "Rennes"
                    ],
                    "spe_classes": [
                      "Physique-Chimie",
                      "Mathématiques"
                    ],
                    "scores": {
                      "T_IDEO2_4818": 1,
                      "T_IDEO2_4813": 1,
                      "T_IDEO2_4810": 1,
                      "T_IDEO2_4816": 1,
                      "T_ROME_637471645": 1,
                      "T_IDEO2_4809": 1,
                      "T_ROME_600174291": 1,
                      "T_ROME_58088585": 1,
                      "T_ROME_1959553899": 1,
                      "T_ROME_2027610093": 1,
                      "T_ROME_762517279": 1,
                      "T_IDEO2_4824": 1,
                      "T_ROME_934089965": 1,
                      "T_IDEO2_4826": 1,
                      "T_IDEO2_4825": 1,
                      "T_IDEO2_4827": 1,
                      "T_ITM_PERSO6": 1,
                      "T_ITM_PERSO4": 1,
                      "T_ITM_1112": 1,
                      "T_ITM_821": 1,
                      "T_ITM_636": 1,
                      "T_ITM_611": 1,
                      "T_ITM_918": 1,
                      "T_ITM_936": 1,
                      "T_ITM_1067": 1,
                      "T_ITM_794": 1,
                      "T_ITM_762": 1,
                      "T_ITM_796": 1
                    },
                    "moygen": "15",
                    "msgs": {},
                    "choices": {
                      "MET_760": {
                        "fl": "MET_760",
                        "status": 1
                      },
                      "MET_204": {
                        "fl": "MET_204",
                        "status": 1
                      },
                      "fl41": {
                        "fl": "fl41",
                        "status": 2
                      },
                      "MET_725": {
                        "fl": "MET_725",
                        "status": 1
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fl210"
                                  },
                                  {
                                    "fl": "fl2046"
                                  },
                                  {
                                    "fl": "fl13"
                                  },
                                  {
                                    "fl": "fr83"
                                  },
                                  {
                                    "fl": "SEC_4846"
                                  },
                                  {
                                    "fl": "SEC_4848"
                                  },
                                  {
                                    "fl": "SEC_4845"
                                  },
                                  {
                                    "fl": "SEC_4857"
                                  },
                                  {
                                    "fl": "SEC_4863"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève ST2S santé social mais pas bio moyenne inconnue, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile": {
                    "niveau": "term",
                    "bac": "ST2S",
                    "duree": "court",
                    "apprentissage": "D",
                    "geo_pref": [
                      "Vénissieux"
                    ],
                    "spe_classes": [
                      "Chimie, biologie et physiopathologie humaines",
                      "Sciences et techniques sanitaires et sociales"
                    ],
                    "scores": {
                      "T_IDEO2_4812": 1,
                      "T_IDEO2_4813": 1,
                      "T_ROME_1391567938": 1,
                      "T_ROME_1316643679": 1,
                      "T_IDEO2_4807": 1,
                      "T_ROME_326548351": 1,
                      "T_ROME_58088585": 1,
                      "T_ROME_2027610093": 1,
                      "T_ROME_1046112128": 1,
                      "T_ROME_934089965": 1,
                      "T_ROME_731379930": 1,
                      "T_ITM_1180": 1,
                      "T_ITM_1020": 1,
                      "T_ITM_1491": 1,
                      "T_ITM_1238": 1,
                      "T_ITM_1094": 1
                    },
                    "moygen": "",
                    "msgs": {},
                    "choices": {
                      "MET_38": {
                        "fl": "MET_38",
                        "status": 1
                      },
                      "fl894": {
                        "fl": "fl894",
                        "status": 1
                      },
                      "MET_771": {
                        "fl": "MET_771",
                        "status": 1
                      },
                      "MET_787": {
                        "fl": "MET_787",
                        "status": 1
                      },
                      "fl721006": {
                        "fl": "fl721006",
                        "status": 1
                      }
                    }
                  }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl550001"
                                  },
                                  {
                                    "fl": "fl2008"
                                  },
                                  {
                                    "fl": "fl369"
                                  },
                                  {
                                    "fl": "fl824"
                                  },
                                  {
                                    "fl": "fl367"
                                  },
                                  {
                                    "fl": "SEC_4846"
                                  },
                                  {
                                    "fl": "SEC_4869"
                                  },
                                  {
                                    "fl": "SEC_4856"
                                  },
                                  {
                                    "fl": "SEC_4845"
                                  },
                                  {
                                    "fl": "SEC_4833"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève anonymisé 1, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile":  {
                        "niveau": "term",
                        "bac": "Générale",
                        "duree": "court",
                        "apprentissage": "A",
                        "geo_pref": [
                          "Chambéry"
                        ],
                        "spe_classes": [
                          "Humanités, Littérature et Philosophie",
                          "Langues, littératures et cultures étrangères et régionales"
                        ],
                        "scores": {
                          "T_ROME_58088585": 1,
                          "T_ROME_84652368": 1,
                          "T_IDEO2_4818": 1,
                          "T_ROME_860291826": 1,
                          "T_ROME_313545038": 1,
                          "T_ITM_611": 1,
                          "T_IDEO2_4812": 1,
                          "T_ROME_1391567938": 1,
                          "T_IDEO2_4810": 1,
                          "T_IDEO2_4817": 1,
                          "T_IDEO2_4814": 1,
                          "T_ITM_PERSO1": 1,
                          "T_ITM_1351": 1,
                          "T_ITM_1054": 1,
                          "T_ROME_1959553899": 1,
                          "T_ITM_918": 1,
                          "T_ROME_1814691478": 1,
                          "T_IDEO2_4820": 1,
                          "T_ROME_1547781503": 1,
                          "T_ITM_1039": 1,
                          "T_ITM_917": 1,
                          "T_ROME_803089798": 1,
                          "T_ROME_1046112128": 1,
                          "T_IDEO2_4809": 1,
                          "T_IDEO2_4829": 1,
                          "T_IDEO2_4808": 1,
                          "T_ROME_269720073": 1,
                          "T_ROME_1825212206": 1,
                          "T_IDEO2_4823": 1,
                          "T_IDEO2_4824": 1,
                          "T_IDEO2_4805": 1,
                          "T_IDEO2_4806": 1,
                          "T_IDEO2_4828": 1,
                          "T_ROME_898671777": 1,
                          "T_ITM_1044": 1,
                          "T_ITM_1020": 1,
                          "T_ROME_600174291": 1,
                          "T_ROME_1316643679": 1,
                          "T_ITM_1420": 1
                        },
                        "moygen": "",
                        "choices": {
                          "SEC_4838": {
                            "fl": "SEC_4838",
                            "status": 1
                          },
                          "SEC_4837": {
                            "fl": "SEC_4837",
                            "status": 2
                          },
                          "SEC_4836": {
                            "fl": "SEC_4836",
                            "status": 2
                          },
                          "SEC_4835": {
                            "fl": "SEC_4835",
                            "status": 2
                          },
                          "SEC_4834": {
                            "fl": "SEC_4834",
                            "status": 2
                          },
                          "SEC_4833": {
                            "fl": "SEC_4833",
                            "status": 1
                          },
                          "SEC_4832": {
                            "fl": "SEC_4832",
                            "status": 2
                          },
                          "SEC_4831": {
                            "fl": "SEC_4831",
                            "status": 2
                          },
                          "fl34": {
                            "fl": "fl34",
                            "status": 1
                          },
                          "fl32": {
                            "fl": "fl32",
                            "status": 1
                          },
                          "fl31": {
                            "fl": "fl31",
                            "status": 1
                          },
                          "SEC_4839": {
                            "fl": "SEC_4839",
                            "status": 1
                          },
                          "fl2089": {
                            "fl": "fl2089",
                            "status": 1
                          },
                          "SEC_4830": {
                            "fl": "SEC_4830",
                            "status": 1
                          },
                          "SEC_4871": {
                            "fl": "SEC_4871",
                            "status": 1
                          },
                          "SEC_4870": {
                            "fl": "SEC_4870",
                            "status": 1
                          },
                          "fl680003": {
                            "fl": "fl680003",
                            "status": 1
                          },
                          "SEC_4869": {
                            "fl": "SEC_4869",
                            "status": 1
                          },
                          "SEC_4868": {
                            "fl": "SEC_4868",
                            "status": 2
                          },
                          "fl680006": {
                            "fl": "fl680006",
                            "status": 1
                          },
                          "SEC_4867": {
                            "fl": "SEC_4867",
                            "status": 2
                          },
                          "SEC_4866": {
                            "fl": "SEC_4866",
                            "status": 1
                          },
                          "SEC_4865": {
                            "fl": "SEC_4865",
                            "status": 2
                          },
                          "SEC_5712": {
                            "fl": "SEC_5712",
                            "status": 2
                          },
                          "MET_51": {
                            "fl": "MET_51",
                            "status": 1
                          },
                          "SEC_4864": {
                            "fl": "SEC_4864",
                            "status": 2
                          },
                          "SEC_5711": {
                            "fl": "SEC_5711",
                            "status": 2
                          },
                          "MET_6": {
                            "fl": "MET_6",
                            "status": 1
                          },
                          "SEC_4863": {
                            "fl": "SEC_4863",
                            "status": 2
                          },
                          "SEC_4862": {
                            "fl": "SEC_4862",
                            "status": 1
                          },
                          "SEC_4861": {
                            "fl": "SEC_4861",
                            "status": 2
                          },
                          "SEC_4860": {
                            "fl": "SEC_4860",
                            "status": 2
                          },
                          "SEC_4859": {
                            "fl": "SEC_4859",
                            "status": 2
                          },
                          "SEC_4858": {
                            "fl": "SEC_4858",
                            "status": 1
                          },
                          "SEC_4857": {
                            "fl": "SEC_4857",
                            "status": 1
                          },
                          "SEC_4856": {
                            "fl": "SEC_4856",
                            "status": 1
                          },
                          "SEC_4855": {
                            "fl": "SEC_4855",
                            "status": 2
                          },
                          "SEC_5668": {
                            "fl": "SEC_5668",
                            "status": 2
                          },
                          "SEC_4854": {
                            "fl": "SEC_4854",
                            "status": 2
                          },
                          "SEC_4853": {
                            "fl": "SEC_4853",
                            "status": 2
                          },
                          "SEC_4852": {
                            "fl": "SEC_4852",
                            "status": 1
                          },
                          "SEC_4851": {
                            "fl": "SEC_4851",
                            "status": 2
                          },
                          "SEC_4850": {
                            "fl": "SEC_4850",
                            "status": 1
                          },
                          "fl2029": {
                            "fl": "fl2029",
                            "status": 1
                          },
                          "MET_557": {
                            "fl": "MET_557",
                            "status": 1
                          },
                          "SEC_4849": {
                            "fl": "SEC_4849",
                            "status": 2
                          },
                          "SEC_4848": {
                            "fl": "SEC_4848",
                            "status": 2
                          },
                          "SEC_4847": {
                            "fl": "SEC_4847",
                            "status": 2
                          },
                          "SEC_4846": {
                            "fl": "SEC_4846",
                            "status": 1
                          },
                          "SEC_4845": {
                            "fl": "SEC_4845",
                            "status": 2
                          },
                          "SEC_4844": {
                            "fl": "SEC_4844",
                            "status": 2
                          },
                          "SEC_4843": {
                            "fl": "SEC_4843",
                            "status": 2
                          },
                          "SEC_4842": {
                            "fl": "SEC_4842",
                            "status": 1
                          },
                          "SEC_4841": {
                            "fl": "SEC_4841",
                            "status": 2
                          },
                          "SEC_4840": {
                            "fl": "SEC_4840",
                            "status": 1
                          },
                          "MET_964": {
                            "fl": "MET_964",
                            "status": 1
                          }
                        }
                      }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fl680001"
                                  },
                                  {
                                    "fl": "fl33"
                                  },
                                  {
                                    "fl": "fr90"
                                  },
                                  {
                                    "fl": "fr22"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève anonymisé 2, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile":  {
                        "niveau": "term",
                        "bac": "Générale",
                        "duree": "indiff",
                        "apprentissage": "A",
                        "geo_pref": [
                          "Marseille",
                          "Lyon",
                          "Paris"
                        ],
                        "spe_classes": [
                          "Mathématiques",
                          "Physique-Chimie"
                        ],
                        "scores": {
                          "T_ROME_58088585": 1,
                          "T_ROME_860291826": 1,
                          "T_IDEO2_4812": 1,
                          "T_IDEO2_4813": 1,
                          "T_IDEO2_4810": 1,
                          "T_ROME_1951356737": 1,
                          "T_ITM_PERSO3": 1,
                          "T_IDEO2_4817": 1,
                          "T_IDEO2_4814": 1,
                          "T_ITM_PERSO1": 1,
                          "T_ROME_1959553899": 1,
                          "T_ROME_762517279": 1,
                          "T_ROME_1814691478": 1,
                          "T_IDEO2_4820": 1,
                          "T_ROME_637471645": 1,
                          "T_ROME_1547781503": 1,
                          "T_ROME_2027610093": 1,
                          "T_ROME_1046112128": 1,
                          "T_ROME_749075906": 1,
                          "T_IDEO2_4809": 1,
                          "T_IDEO2_4829": 1,
                          "T_ROME_934089965": 1,
                          "T_IDEO2_4821": 1,
                          "T_IDEO2_4822": 1,
                          "T_IDEO2_4806": 1,
                          "T_IDEO2_4828": 1,
                          "T_ROME_898671777": 1,
                          "T_IDEO2_4826": 1,
                          "T_ITM_1021": 1,
                          "T_ROME_731379930": 1,
                          "T_ITM_723": 1,
                          "T_ROME_1991903888": 1,
                          "T_ROME_1316643679": 1,
                          "T_ROME_1665443017": 1,
                          "T_ITM_1420": 1
                        },
                        "moygen": "13",
                        "choices": {
                          "MET_194": {
                            "fl": "MET_194",
                            "status": 2
                          },
                          "SEC_4838": {
                            "fl": "SEC_4838",
                            "status": 1
                          },
                          "fl455": {
                            "fl": "fl455",
                            "status": 1
                          },
                          "MET_193": {
                            "fl": "MET_193",
                            "status": 1
                          },
                          "fl453": {
                            "fl": "fl453",
                            "status": 1
                          },
                          "MET_580": {
                            "fl": "MET_580",
                            "status": 1
                          },
                          "MET_110": {
                            "fl": "MET_110",
                            "status": 1
                          },
                          "SEC_4865": {
                            "fl": "SEC_4865",
                            "status": 2
                          },
                          "MET_178": {
                            "fl": "MET_178",
                            "status": 1
                          },
                          "fl2060": {
                            "fl": "fl2060",
                            "status": 1
                          },
                          "fl1": {
                            "fl": "fl1",
                            "status": 1
                          },
                          "fl1453": {
                            "fl": "fl1453",
                            "status": 1
                          },
                          "fl1455": {
                            "fl": "fl1455",
                            "status": 1
                          },
                          "MET_537": {
                            "fl": "MET_537",
                            "status": 1
                          }
                        }
                      }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl680003"
                                  },
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fl2024"
                                  },
                                  {
                                    "fl": "fl691134"
                                  },
                                  {
                                    "fl": "fl2050"
                                  },
                                  {
                                    "fl": "SEC_4850"
                                  },
                                  {
                                    "fl": "SEC_4858"
                                  },
                                  {
                                    "fl": "SEC_5712"
                                  },
                                  {
                                    "fl": "SEC_4830"
                                  },
                                  {
                                    "fl": "SEC_4864"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève anonymisé 3, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile":  {
                        "niveau": "term",
                        "bac": "Générale",
                        "duree": "indiff",
                        "apprentissage": "C",
                        "geo_pref": [
                          "Grenoble",
                          "Lyon"
                        ],
                        "spe_classes": [
                          "Humanités, Littérature et Philosophie",
                          "Histoire-Géographie, Géopolitique et Sciences politiques"
                        ],
                        "scores": {
                          "T_ROME_58088585": 1,
                          "T_ROME_84652368": 1,
                          "T_IDEO2_4813": 1,
                          "T_IDEO2_4824": 1,
                          "T_IDEO2_4806": 1,
                          "T_ROME_898671777": 1,
                          "T_IDEO2_4814": 1,
                          "T_ITM_PERSO1": 1,
                          "T_ITM_1021": 1,
                          "T_ITM_1030": 1,
                          "T_ROME_762517279": 1,
                          "T_ITM_917": 1,
                          "T_ROME_1046112128": 1
                        },
                        "moygen": "13",
                        "choices": {
                          "SEC_4838": {
                            "fl": "SEC_4838",
                            "status": 2
                          },
                          "SEC_4837": {
                            "fl": "SEC_4837",
                            "status": 2
                          },
                          "SEC_4836": {
                            "fl": "SEC_4836",
                            "status": 2
                          },
                          "SEC_4835": {
                            "fl": "SEC_4835",
                            "status": 2
                          },
                          "SEC_4834": {
                            "fl": "SEC_4834",
                            "status": 2
                          },
                          "MET_175": {
                            "fl": "MET_175",
                            "status": 2
                          },
                          "SEC_4833": {
                            "fl": "SEC_4833",
                            "status": 2
                          },
                          "SEC_4832": {
                            "fl": "SEC_4832",
                            "status": 2
                          },
                          "SEC_4831": {
                            "fl": "SEC_4831",
                            "status": 2
                          },
                          "SEC_4839": {
                            "fl": "SEC_4839",
                            "status": 2
                          },
                          "fl810514": {
                            "fl": "fl810514",
                            "status": 1
                          },
                          "SEC_4830": {
                            "fl": "SEC_4830",
                            "status": 1
                          },
                          "SEC_4871": {
                            "fl": "SEC_4871",
                            "status": 2
                          },
                          "SEC_4870": {
                            "fl": "SEC_4870",
                            "status": 2
                          },
                          "MET_36": {
                            "fl": "MET_36",
                            "status": 2
                          },
                          "SEC_4869": {
                            "fl": "SEC_4869",
                            "status": 2
                          },
                          "fl680001": {
                            "fl": "fl680001",
                            "status": 1
                          },
                          "SEC_4868": {
                            "fl": "SEC_4868",
                            "status": 2
                          },
                          "SEC_4867": {
                            "fl": "SEC_4867",
                            "status": 2
                          },
                          "SEC_4866": {
                            "fl": "SEC_4866",
                            "status": 2
                          },
                          "SEC_4865": {
                            "fl": "SEC_4865",
                            "status": 2
                          },
                          "SEC_5712": {
                            "fl": "SEC_5712",
                            "status": 2
                          },
                          "SEC_4864": {
                            "fl": "SEC_4864",
                            "status": 2
                          },
                          "SEC_4863": {
                            "fl": "SEC_4863",
                            "status": 2
                          },
                          "SEC_4862": {
                            "fl": "SEC_4862",
                            "status": 2
                          },
                          "SEC_4861": {
                            "fl": "SEC_4861",
                            "status": 2
                          },
                          "MET_44": {
                            "fl": "MET_44",
                            "status": 1
                          },
                          "SEC_4860": {
                            "fl": "SEC_4860",
                            "status": 2
                          },
                          "MET_269": {
                            "fl": "MET_269",
                            "status": 1
                          },
                          "MET_788": {
                            "fl": "MET_788",
                            "status": 2
                          },
                          "SEC_4859": {
                            "fl": "SEC_4859",
                            "status": 2
                          },
                          "SEC_4858": {
                            "fl": "SEC_4858",
                            "status": 2
                          },
                          "SEC_4857": {
                            "fl": "SEC_4857",
                            "status": 2
                          },
                          "SEC_4856": {
                            "fl": "SEC_4856",
                            "status": 2
                          },
                          "SEC_4855": {
                            "fl": "SEC_4855",
                            "status": 2
                          },
                          "SEC_4854": {
                            "fl": "SEC_4854",
                            "status": 2
                          },
                          "SEC_5668": {
                            "fl": "SEC_5668",
                            "status": 2
                          },
                          "SEC_4853": {
                            "fl": "SEC_4853",
                            "status": 2
                          },
                          "SEC_4852": {
                            "fl": "SEC_4852",
                            "status": 1
                          },
                          "MET_433": {
                            "fl": "MET_433",
                            "status": 2
                          },
                          "SEC_4851": {
                            "fl": "SEC_4851",
                            "status": 2
                          },
                          "SEC_4850": {
                            "fl": "SEC_4850",
                            "status": 2
                          },
                          "fl1337": {
                            "fl": "fl1337",
                            "status": 1
                          },
                          "fl2028": {
                            "fl": "fl2028",
                            "status": 1
                          },
                          "MET_878": {
                            "fl": "MET_878",
                            "status": 2
                          },
                          "SEC_4849": {
                            "fl": "SEC_4849",
                            "status": 2
                          },
                          "SEC_4848": {
                            "fl": "SEC_4848",
                            "status": 2
                          },
                          "SEC_4847": {
                            "fl": "SEC_4847",
                            "status": 2
                          },
                          "SEC_4846": {
                            "fl": "SEC_4846",
                            "status": 1
                          },
                          "SEC_4845": {
                            "fl": "SEC_4845",
                            "status": 2
                          },
                          "SEC_4844": {
                            "fl": "SEC_4844",
                            "status": 2
                          },
                          "SEC_4843": {
                            "fl": "SEC_4843",
                            "status": 2
                          },
                          "SEC_4842": {
                            "fl": "SEC_4842",
                            "status": 2
                          },
                          "SEC_4841": {
                            "fl": "SEC_4841",
                            "status": 2
                          },
                          "SEC_4840": {
                            "fl": "SEC_4840",
                            "status": 1
                          }
                        }
                      }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl680003"
                                  },
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fl2029"
                                  },
                                  {
                                    "fl": "fl2023"
                                  },
                                  {
                                    "fl": "fl2010"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève anonymisé 4, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile":  {
                        "niveau": "term",
                        "bac": "Générale",
                        "duree": "long",
                        "apprentissage": "B",
                        "geo_pref": [
                          "Chambéry",
                          "Lyon"
                        ],
                        "spe_classes": [
                          "Mathématiques",
                          "Physique-Chimie"
                        ],
                        "scores": {
                          "T_ROME_58088585": 1,
                          "T_ITM_794": 1,
                          "T_ROME_84652368": 1,
                          "T_IDEO2_4818": 1,
                          "T_IDEO2_4819": 1,
                          "T_ROME_2018646295": 1,
                          "T_ITM_950": 1,
                          "T_IDEO2_4812": 1,
                          "T_ROME_1391567938": 1,
                          "T_ROME_609891024": 1,
                          "T_IDEO2_4813": 1,
                          "T_ROME_1951356737": 1,
                          "T_IDEO2_4817": 1,
                          "T_IDEO2_4814": 1,
                          "T_ITM_1054": 1,
                          "T_ITM_1030": 1,
                          "T_ROME_762517279": 1,
                          "T_IDEO2_4820": 1,
                          "T_ROME_2027610093": 1,
                          "T_ITM_1039": 1,
                          "T_ROME_1046112128": 1,
                          "T_IDEO2_4809": 1,
                          "T_IDEO2_4823": 1,
                          "T_IDEO2_4821": 1,
                          "T_ITM_1044": 1,
                          "T_ROME_600174291": 1,
                          "T_ROME_1573349427": 1,
                          "T_ITM_1248": 1,
                          "T_ITM_1025": 1,
                          "T_ITM_1169": 1
                        },
                        "moygen": "",
                        "choices": {
                          "MET_172": {
                            "fl": "MET_172",
                            "status": 2
                          },
                          "MET_570": {
                            "fl": "MET_570",
                            "status": 2
                          },
                          "MET_692": {
                            "fl": "MET_692",
                            "status": 2
                          },
                          "MET_84": {
                            "fl": "MET_84",
                            "status": 2
                          },
                          "fl251": {
                            "fl": "fl251",
                            "status": 2
                          },
                          "MET_771": {
                            "fl": "MET_771",
                            "status": 2
                          },
                          "fl34": {
                            "fl": "fl34",
                            "status": 2
                          },
                          "fl33": {
                            "fl": "fl33",
                            "status": 2
                          },
                          "fl32": {
                            "fl": "fl32",
                            "status": 2
                          },
                          "fl31": {
                            "fl": "fl31",
                            "status": 2
                          },
                          "fl2090": {
                            "fl": "fl2090",
                            "status": 2
                          },
                          "MET_170": {
                            "fl": "MET_170",
                            "status": 2
                          },
                          "MET_419": {
                            "fl": "MET_419",
                            "status": 2
                          },
                          "MET_738": {
                            "fl": "MET_738",
                            "status": 2
                          },
                          "fl2046": {
                            "fl": "fl2046",
                            "status": 2
                          },
                          "MET_539": {
                            "fl": "MET_539",
                            "status": 2
                          },
                          "fl2089": {
                            "fl": "fl2089",
                            "status": 2
                          },
                          "fl2044": {
                            "fl": "fl2044",
                            "status": 2
                          },
                          "MET_75": {
                            "fl": "MET_75",
                            "status": 2
                          },
                          "MET_179": {
                            "fl": "MET_179",
                            "status": 2
                          },
                          "MET_773": {
                            "fl": "MET_773",
                            "status": 2
                          },
                          "MET_894": {
                            "fl": "MET_894",
                            "status": 2
                          },
                          "fl2009": {
                            "fl": "fl2009",
                            "status": 2
                          },
                          "MET_699": {
                            "fl": "MET_699",
                            "status": 2
                          },
                          "MET_413": {
                            "fl": "MET_413",
                            "status": 2
                          },
                          "fl2008": {
                            "fl": "fl2008",
                            "status": 2
                          },
                          "MET_414": {
                            "fl": "MET_414",
                            "status": 2
                          },
                          "MET_813": {
                            "fl": "MET_813",
                            "status": 2
                          },
                          "MET_36": {
                            "fl": "MET_36",
                            "status": 2
                          },
                          "fl680002": {
                            "fl": "fl680002",
                            "status": 2
                          },
                          "fl680003": {
                            "fl": "fl680003",
                            "status": 2
                          },
                          "fl680001": {
                            "fl": "fl680001",
                            "status": 2
                          },
                          "fl2": {
                            "fl": "fl2",
                            "status": 2
                          },
                          "MET_180": {
                            "fl": "MET_180",
                            "status": 1
                          },
                          "fl2014": {
                            "fl": "fl2014",
                            "status": 1
                          },
                          "MET_829": {
                            "fl": "MET_829",
                            "status": 2
                          },
                          "MET_908": {
                            "fl": "MET_908",
                            "status": 2
                          },
                          "MET_301": {
                            "fl": "MET_301",
                            "status": 2
                          },
                          "MET_103": {
                            "fl": "MET_103",
                            "status": 2
                          },
                          "MET_465": {
                            "fl": "MET_465",
                            "status": 2
                          },
                          "MET_303": {
                            "fl": "MET_303",
                            "status": 2
                          },
                          "MET_589": {
                            "fl": "MET_589",
                            "status": 2
                          },
                          "MET_105": {
                            "fl": "MET_105",
                            "status": 2
                          },
                          "MET_226": {
                            "fl": "MET_226",
                            "status": 2
                          },
                          "MET_820": {
                            "fl": "MET_820",
                            "status": 2
                          },
                          "MET_424": {
                            "fl": "MET_424",
                            "status": 2
                          },
                          "fl2018": {
                            "fl": "fl2018",
                            "status": 2
                          },
                          "fr22": {
                            "fl": "fr22",
                            "status": 2
                          },
                          "MET_107": {
                            "fl": "MET_107",
                            "status": 2
                          },
                          "MET_89": {
                            "fl": "MET_89",
                            "status": 2
                          },
                          "MET_703": {
                            "fl": "MET_703",
                            "status": 2
                          },
                          "MET_109": {
                            "fl": "MET_109",
                            "status": 2
                          },
                          "MET_48": {
                            "fl": "MET_48",
                            "status": 2
                          },
                          "MET_824": {
                            "fl": "MET_824",
                            "status": 2
                          },
                          "MET_590": {
                            "fl": "MET_590",
                            "status": 2
                          },
                          "fl18": {
                            "fl": "fl18",
                            "status": 2
                          },
                          "fl17": {
                            "fl": "fl17",
                            "status": 2
                          },
                          "MET_475": {
                            "fl": "MET_475",
                            "status": 2
                          },
                          "MET_871": {
                            "fl": "MET_871",
                            "status": 1
                          },
                          "MET_61": {
                            "fl": "MET_61",
                            "status": 2
                          },
                          "fl12": {
                            "fl": "fl12",
                            "status": 2
                          },
                          "fl11": {
                            "fl": "fl11",
                            "status": 2
                          },
                          "fl54": {
                            "fl": "fl54",
                            "status": 2
                          },
                          "fl53": {
                            "fl": "fl53",
                            "status": 2
                          },
                          "fl52": {
                            "fl": "fl52",
                            "status": 2
                          },
                          "fl51": {
                            "fl": "fl51",
                            "status": 2
                          },
                          "MET_192": {
                            "fl": "MET_192",
                            "status": 2
                          },
                          "fl2022": {
                            "fl": "fl2022",
                            "status": 2
                          },
                          "MET_279": {
                            "fl": "MET_279",
                            "status": 2
                          },
                          "MET_634": {
                            "fl": "MET_634",
                            "status": 2
                          },
                          "MET_438": {
                            "fl": "MET_438",
                            "status": 1
                          },
                          "MET_361": {
                            "fl": "MET_361",
                            "status": 2
                          },
                          "SEC_4845": {
                            "fl": "SEC_4845",
                            "status": 1
                          },
                          "MET_880": {
                            "fl": "MET_880",
                            "status": 1
                          },
                          "MET_164": {
                            "fl": "MET_164",
                            "status": 2
                          },
                          "MET_244": {
                            "fl": "MET_244",
                            "status": 2
                          },
                          "MET_442": {
                            "fl": "MET_442",
                            "status": 2
                          },
                          "MET_683": {
                            "fl": "MET_683",
                            "status": 2
                          },
                          "MET_166": {
                            "fl": "MET_166",
                            "status": 2
                          },
                          "fl811013": {
                            "fl": "fl811013",
                            "status": 1
                          },
                          "fl2110": {
                            "fl": "fl2110",
                            "status": 2
                          },
                          "fl810003": {
                            "fl": "fl810003",
                            "status": 1
                          },
                          "MET_607": {
                            "fl": "MET_607",
                            "status": 2
                          },
                          "MET_929": {
                            "fl": "MET_929",
                            "status": 2
                          },
                          "fr83": {
                            "fl": "fr83",
                            "status": 2
                          },
                          "MET_565": {
                            "fl": "MET_565",
                            "status": 2
                          },
                          "MET_884": {
                            "fl": "MET_884",
                            "status": 2
                          },
                          "MET_369": {
                            "fl": "MET_369",
                            "status": 2
                          },
                          "MET_567": {
                            "fl": "MET_567",
                            "status": 2
                          },
                          "MET_687": {
                            "fl": "MET_687",
                            "status": 2
                          },
                          "MET_844": {
                            "fl": "MET_844",
                            "status": 2
                          },
                          "MET_800": {
                            "fl": "MET_800",
                            "status": 2
                          },
                          "MET_24": {
                            "fl": "MET_24",
                            "status": 2
                          },
                          "MET_843": {
                            "fl": "MET_843",
                            "status": 2
                          },
                          "MET_26": {
                            "fl": "MET_26",
                            "status": 1
                          }
                        }
                      }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fr90"
                                  },
                                  {
                                    "fl": "fl210"
                                  },
                                  {
                                    "fl": "fl2013"
                                  },
                                  {
                                    "fl": "fl13"
                                  },
                                  {
                                    "fl": "fl2029"
                                  },
                                  {
                                    "fl": "SEC_4846"
                                  },
                                  {
                                    "fl": "SEC_4863"
                                  },
                                  {
                                    "fl": "SEC_4833"
                                  },
                                  {
                                    "fl": "SEC_4869"
                                  },
                                  {
                                    "fl": "SEC_4848"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève anonymisé 5, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile":  {
                        "niveau": "term",
                        "bac": "Générale",
                        "duree": "court",
                        "apprentissage": "B",
                        "geo_pref": [
                          "Grenoble"
                        ],
                        "spe_classes": [
                          "Histoire-Géographie, Géopolitique et Sciences politiques",
                          "Sciences Economiques et Sociales"
                        ],
                        "scores": {
                          "T_ROME_58088585": 1,
                          "T_ITM_1044": 1,
                          "T_ROME_860291826": 1,
                          "T_ITM_1020": 1,
                          "T_ITM_1491": 1,
                          "T_ITM_1180": 1,
                          "T_ROME_731379930": 1,
                          "T_IDEO2_4812": 1,
                          "T_ITM_1025": 1,
                          "T_IDEO2_4817": 1
                        },
                        "moygen": "12",
                        "choices": {
                          "SEC_4838": {
                            "fl": "SEC_4838",
                            "status": 2
                          },
                          "SEC_4859": {
                            "fl": "SEC_4859",
                            "status": 2
                          },
                          "SEC_4858": {
                            "fl": "SEC_4858",
                            "status": 2
                          },
                          "SEC_4835": {
                            "fl": "SEC_4835",
                            "status": 2
                          },
                          "SEC_4857": {
                            "fl": "SEC_4857",
                            "status": 2
                          },
                          "MET_451": {
                            "fl": "MET_451",
                            "status": 2
                          },
                          "SEC_4855": {
                            "fl": "SEC_4855",
                            "status": 2
                          },
                          "SEC_4832": {
                            "fl": "SEC_4832",
                            "status": 2
                          },
                          "SEC_5668": {
                            "fl": "SEC_5668",
                            "status": 2
                          },
                          "MET_871": {
                            "fl": "MET_871",
                            "status": 1
                          },
                          "SEC_4854": {
                            "fl": "SEC_4854",
                            "status": 2
                          },
                          "MET_672": {
                            "fl": "MET_672",
                            "status": 2
                          },
                          "MET_919": {
                            "fl": "MET_919",
                            "status": 2
                          },
                          "fl811506": {
                            "fl": "fl811506",
                            "status": 2
                          },
                          "MET_279": {
                            "fl": "MET_279",
                            "status": 2
                          },
                          "SEC_4852": {
                            "fl": "SEC_4852",
                            "status": 2
                          },
                          "MET_312": {
                            "fl": "MET_312",
                            "status": 2
                          },
                          "SEC_4830": {
                            "fl": "SEC_4830",
                            "status": 2
                          },
                          "MET_311": {
                            "fl": "MET_311",
                            "status": 1
                          },
                          "fl810515": {
                            "fl": "fl810515",
                            "status": 1
                          },
                          "SEC_4851": {
                            "fl": "SEC_4851",
                            "status": 2
                          },
                          "MET_179": {
                            "fl": "MET_179",
                            "status": 2
                          },
                          "SEC_4850": {
                            "fl": "SEC_4850",
                            "status": 2
                          },
                          "SEC_4871": {
                            "fl": "SEC_4871",
                            "status": 2
                          },
                          "SEC_4870": {
                            "fl": "SEC_4870",
                            "status": 2
                          },
                          "fl811509": {
                            "fl": "fl811509",
                            "status": 1
                          },
                          "MET_711": {
                            "fl": "MET_711",
                            "status": 2
                          },
                          "MET_876": {
                            "fl": "MET_876",
                            "status": 2
                          },
                          "MET_219": {
                            "fl": "MET_219",
                            "status": 2
                          },
                          "MET_713": {
                            "fl": "MET_713",
                            "status": 2
                          },
                          "SEC_4848": {
                            "fl": "SEC_4848",
                            "status": 2
                          },
                          "SEC_4868": {
                            "fl": "SEC_4868",
                            "status": 2
                          },
                          "SEC_4846": {
                            "fl": "SEC_4846",
                            "status": 2
                          },
                          "SEC_4845": {
                            "fl": "SEC_4845",
                            "status": 2
                          },
                          "SEC_4866": {
                            "fl": "SEC_4866",
                            "status": 2
                          },
                          "SEC_4844": {
                            "fl": "SEC_4844",
                            "status": 2
                          },
                          "SEC_4865": {
                            "fl": "SEC_4865",
                            "status": 2
                          },
                          "MET_343": {
                            "fl": "MET_343",
                            "status": 2
                          },
                          "SEC_5712": {
                            "fl": "SEC_5712",
                            "status": 2
                          },
                          "SEC_4864": {
                            "fl": "SEC_4864",
                            "status": 2
                          },
                          "SEC_4863": {
                            "fl": "SEC_4863",
                            "status": 2
                          },
                          "MET_64": {
                            "fl": "MET_64",
                            "status": 1
                          },
                          "SEC_4841": {
                            "fl": "SEC_4841",
                            "status": 2
                          },
                          "MET_344": {
                            "fl": "MET_344",
                            "status": 2
                          },
                          "SEC_4862": {
                            "fl": "SEC_4862",
                            "status": 2
                          },
                          "MET_941": {
                            "fl": "MET_941",
                            "status": 2
                          },
                          "SEC_4860": {
                            "fl": "SEC_4860",
                            "status": 2
                          },
                          "MET_668": {
                            "fl": "MET_668",
                            "status": 1
                          }
                        }
                      }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl2008"
                                  },
                                  {
                                    "fl": "fl810509"
                                  },
                                  {
                                    "fl": "fl560002"
                                  },
                                  {
                                    "fl": "fl560001"
                                  },
                                  {
                                    "fl": "fl560003"
                                  },
                                  {
                                    "fl": "SEC_4869"
                                  },
                                  {
                                    "fl": "SEC_4856"
                                  },
                                  {
                                    "fl": "SEC_4833"
                                  },
                                  {
                                    "fl": "SEC_4843"
                                  },
                                  {
                                    "fl": "SEC_4834"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève anonymisé 6, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile":  {
                        "niveau": "term",
                        "bac": "Générale",
                        "duree": "long",
                        "apprentissage": "C",
                        "geo_pref": [
                          "Grenoble",
                          "Poitiers",
                          "Lille",
                          "Lyon"
                        ],
                        "spe_classes": [
                          "Mathématiques",
                          "Physique-Chimie"
                        ],
                        "scores": {
                          "T_ROME_58088585": 1,
                          "T_ITM_794": 1,
                          "T_IDEO2_4819": 1,
                          "T_ITM_611": 1,
                          "T_IDEO2_4812": 1,
                          "T_ITM_PERSO7": 1,
                          "T_IDEO2_4813": 1,
                          "T_ITM_PERSO6": 1,
                          "T_ITM_PERSO4": 1,
                          "T_IDEO2_4816": 1,
                          "T_ITM_1054": 1,
                          "T_ROME_1959553899": 1,
                          "T_ITM_1055": 1,
                          "T_ROME_762517279": 1,
                          "T_ROME_637471645": 1,
                          "T_ROME_1547781503": 1,
                          "T_ITM_636": 1,
                          "T_ROME_2027610093": 1,
                          "T_ITM_1534": 1,
                          "T_ITM_1039": 1,
                          "T_ROME_1046112128": 1,
                          "T_ITM_1112": 1,
                          "T_ROME_326548351": 1,
                          "T_ROME_934089965": 1,
                          "T_IDEO2_4805": 1,
                          "T_ITM_1044": 1,
                          "T_ROME_2092381917": 1,
                          "T_ROME_600174291": 1,
                          "T_ITM_1025": 1,
                          "T_ITM_1067": 1
                        },
                        "moygen": "15",
                        "choices": {
                          "fl252": {
                            "fl": "fl252",
                            "status": 2
                          },
                          "fl251": {
                            "fl": "fl251",
                            "status": 2
                          },
                          "fl250": {
                            "fl": "fl250",
                            "status": 2
                          },
                          "fl691147": {
                            "fl": "fl691147",
                            "status": 2
                          },
                          "fl691029": {
                            "fl": "fl691029",
                            "status": 2
                          },
                          "SEC_4871": {
                            "fl": "SEC_4871",
                            "status": 2
                          },
                          "SEC_4870": {
                            "fl": "SEC_4870",
                            "status": 2
                          },
                          "fl840004": {
                            "fl": "fl840004",
                            "status": 2
                          },
                          "fl680002": {
                            "fl": "fl680002",
                            "status": 2
                          },
                          "fl680003": {
                            "fl": "fl680003",
                            "status": 2
                          },
                          "fl389": {
                            "fl": "fl389",
                            "status": 2
                          },
                          "fl720007": {
                            "fl": "fl720007",
                            "status": 2
                          },
                          "SEC_4869": {
                            "fl": "SEC_4869",
                            "status": 2
                          },
                          "fl680001": {
                            "fl": "fl680001",
                            "status": 2
                          },
                          "SEC_4868": {
                            "fl": "SEC_4868",
                            "status": 2
                          },
                          "SEC_4867": {
                            "fl": "SEC_4867",
                            "status": 2
                          },
                          "fl385": {
                            "fl": "fl385",
                            "status": 2
                          },
                          "SEC_4866": {
                            "fl": "SEC_4866",
                            "status": 2
                          },
                          "fl384": {
                            "fl": "fl384",
                            "status": 2
                          },
                          "SEC_4865": {
                            "fl": "SEC_4865",
                            "status": 2
                          },
                          "SEC_5712": {
                            "fl": "SEC_5712",
                            "status": 2
                          },
                          "SEC_5711": {
                            "fl": "SEC_5711",
                            "status": 2
                          },
                          "SEC_4864": {
                            "fl": "SEC_4864",
                            "status": 2
                          },
                          "fl2": {
                            "fl": "fl2",
                            "status": 2
                          },
                          "fl5": {
                            "fl": "fl5",
                            "status": 2
                          },
                          "fl7": {
                            "fl": "fl7",
                            "status": 2
                          },
                          "fl6": {
                            "fl": "fl6",
                            "status": 2
                          },
                          "SEC_4863": {
                            "fl": "SEC_4863",
                            "status": 2
                          },
                          "fl840011": {
                            "fl": "fl840011",
                            "status": 2
                          },
                          "SEC_4862": {
                            "fl": "SEC_4862",
                            "status": 2
                          },
                          "fl840010": {
                            "fl": "fl840010",
                            "status": 2
                          },
                          "SEC_4861": {
                            "fl": "SEC_4861",
                            "status": 2
                          },
                          "fl824": {
                            "fl": "fl824",
                            "status": 2
                          },
                          "SEC_4860": {
                            "fl": "SEC_4860",
                            "status": 2
                          },
                          "fr22": {
                            "fl": "fr22",
                            "status": 2
                          },
                          "MET_866": {
                            "fl": "MET_866",
                            "status": 2
                          },
                          "fl610002": {
                            "fl": "fl610002",
                            "status": 2
                          },
                          "fl610001": {
                            "fl": "fl610001",
                            "status": 2
                          },
                          "fl352": {
                            "fl": "fl352",
                            "status": 2
                          },
                          "fl230": {
                            "fl": "fl230",
                            "status": 2
                          },
                          "fl470": {
                            "fl": "fl470",
                            "status": 2
                          },
                          "fl2100": {
                            "fl": "fl2100",
                            "status": 2
                          },
                          "fl358": {
                            "fl": "fl358",
                            "status": 2
                          },
                          "fl242": {
                            "fl": "fl242",
                            "status": 2
                          },
                          "fl363": {
                            "fl": "fl363",
                            "status": 2
                          },
                          "fl241": {
                            "fl": "fl241",
                            "status": 2
                          },
                          "fl240": {
                            "fl": "fl240",
                            "status": 2
                          },
                          "fl2112": {
                            "fl": "fl2112",
                            "status": 2
                          },
                          "fl2111": {
                            "fl": "fl2111",
                            "status": 2
                          },
                          "fl2110": {
                            "fl": "fl2110",
                            "status": 2
                          },
                          "fl2118": {
                            "fl": "fl2118",
                            "status": 2
                          },
                          "fl2117": {
                            "fl": "fl2117",
                            "status": 2
                          },
                          "fl691150": {
                            "fl": "fl691150",
                            "status": 2
                          },
                          "fl691151": {
                            "fl": "fl691151",
                            "status": 2
                          },
                          "fl369": {
                            "fl": "fl369",
                            "status": 2
                          },
                          "SEC_4838": {
                            "fl": "SEC_4838",
                            "status": 2
                          },
                          "SEC_4837": {
                            "fl": "SEC_4837",
                            "status": 2
                          },
                          "SEC_4836": {
                            "fl": "SEC_4836",
                            "status": 2
                          },
                          "SEC_4835": {
                            "fl": "SEC_4835",
                            "status": 2
                          },
                          "fl210": {
                            "fl": "fl210",
                            "status": 2
                          },
                          "SEC_4834": {
                            "fl": "SEC_4834",
                            "status": 2
                          },
                          "SEC_4833": {
                            "fl": "SEC_4833",
                            "status": 2
                          },
                          "SEC_4832": {
                            "fl": "SEC_4832",
                            "status": 2
                          },
                          "SEC_4831": {
                            "fl": "SEC_4831",
                            "status": 2
                          },
                          "SEC_4839": {
                            "fl": "SEC_4839",
                            "status": 2
                          },
                          "fl2003": {
                            "fl": "fl2003",
                            "status": 2
                          },
                          "fl2002": {
                            "fl": "fl2002",
                            "status": 2
                          },
                          "fr70": {
                            "fl": "fr70",
                            "status": 2
                          },
                          "SEC_4830": {
                            "fl": "SEC_4830",
                            "status": 2
                          },
                          "fl810515": {
                            "fl": "fl810515",
                            "status": 2
                          },
                          "fl2009": {
                            "fl": "fl2009",
                            "status": 2
                          },
                          "fl2008": {
                            "fl": "fl2008",
                            "status": 2
                          },
                          "fl810519": {
                            "fl": "fl810519",
                            "status": 2
                          },
                          "fl2006": {
                            "fl": "fl2006",
                            "status": 2
                          },
                          "fl458": {
                            "fl": "fl458",
                            "status": 2
                          },
                          "MET_813": {
                            "fl": "MET_813",
                            "status": 2
                          },
                          "fl467": {
                            "fl": "fl467",
                            "status": 2
                          },
                          "fl341": {
                            "fl": "fl341",
                            "status": 2
                          },
                          "fl2014": {
                            "fl": "fl2014",
                            "status": 2
                          },
                          "fl2013": {
                            "fl": "fl2013",
                            "status": 2
                          },
                          "fl2012": {
                            "fl": "fl2012",
                            "status": 2
                          },
                          "MET_949": {
                            "fl": "MET_949",
                            "status": 2
                          },
                          "fl2010": {
                            "fl": "fl2010",
                            "status": 2
                          },
                          "fl2019": {
                            "fl": "fl2019",
                            "status": 2
                          },
                          "fl2018": {
                            "fl": "fl2018",
                            "status": 2
                          },
                          "fl2017": {
                            "fl": "fl2017",
                            "status": 2
                          },
                          "fl810508": {
                            "fl": "fl810508",
                            "status": 2
                          },
                          "fl2016": {
                            "fl": "fl2016",
                            "status": 2
                          },
                          "fl2015": {
                            "fl": "fl2015",
                            "status": 2
                          },
                          "fl10434": {
                            "fl": "fl10434",
                            "status": 2
                          },
                          "fl434": {
                            "fl": "fl434",
                            "status": 2
                          },
                          "SEC_4859": {
                            "fl": "SEC_4859",
                            "status": 2
                          },
                          "fl10436": {
                            "fl": "fl10436",
                            "status": 2
                          },
                          "SEC_4858": {
                            "fl": "SEC_4858",
                            "status": 2
                          },
                          "fl10437": {
                            "fl": "fl10437",
                            "status": 2
                          },
                          "SEC_4857": {
                            "fl": "SEC_4857",
                            "status": 2
                          },
                          "SEC_4856": {
                            "fl": "SEC_4856",
                            "status": 2
                          },
                          "SEC_4855": {
                            "fl": "SEC_4855",
                            "status": 2
                          },
                          "SEC_4854": {
                            "fl": "SEC_4854",
                            "status": 2
                          },
                          "SEC_4853": {
                            "fl": "SEC_4853",
                            "status": 2
                          },
                          "fl2024": {
                            "fl": "fl2024",
                            "status": 2
                          },
                          "fl1176": {
                            "fl": "fl1176",
                            "status": 2
                          },
                          "fl2023": {
                            "fl": "fl2023",
                            "status": 2
                          },
                          "fl2022": {
                            "fl": "fl2022",
                            "status": 2
                          },
                          "fl2021": {
                            "fl": "fl2021",
                            "status": 2
                          },
                          "fl2020": {
                            "fl": "fl2020",
                            "status": 2
                          },
                          "SEC_4852": {
                            "fl": "SEC_4852",
                            "status": 2
                          },
                          "SEC_4851": {
                            "fl": "SEC_4851",
                            "status": 2
                          },
                          "SEC_4850": {
                            "fl": "SEC_4850",
                            "status": 2
                          },
                          "fl2029": {
                            "fl": "fl2029",
                            "status": 2
                          },
                          "fl2028": {
                            "fl": "fl2028",
                            "status": 2
                          },
                          "fl2027": {
                            "fl": "fl2027",
                            "status": 2
                          },
                          "fl324": {
                            "fl": "fl324",
                            "status": 2
                          },
                          "SEC_4849": {
                            "fl": "SEC_4849",
                            "status": 2
                          },
                          "SEC_4848": {
                            "fl": "SEC_4848",
                            "status": 2
                          },
                          "SEC_4847": {
                            "fl": "SEC_4847",
                            "status": 2
                          },
                          "SEC_4846": {
                            "fl": "SEC_4846",
                            "status": 2
                          },
                          "SEC_4845": {
                            "fl": "SEC_4845",
                            "status": 1
                          },
                          "SEC_4844": {
                            "fl": "SEC_4844",
                            "status": 2
                          },
                          "SEC_4843": {
                            "fl": "SEC_4843",
                            "status": 2
                          },
                          "SEC_4842": {
                            "fl": "SEC_4842",
                            "status": 2
                          },
                          "fl2035": {
                            "fl": "fl2035",
                            "status": 2
                          },
                          "fl2034": {
                            "fl": "fl2034",
                            "status": 1
                          },
                          "fl2033": {
                            "fl": "fl2033",
                            "status": 2
                          },
                          "fl2032": {
                            "fl": "fl2032",
                            "status": 2
                          },
                          "fl2030": {
                            "fl": "fl2030",
                            "status": 2
                          },
                          "fr83": {
                            "fl": "fr83",
                            "status": 2
                          },
                          "SEC_4841": {
                            "fl": "SEC_4841",
                            "status": 2
                          },
                          "fr85": {
                            "fl": "fr85",
                            "status": 2
                          },
                          "SEC_4840": {
                            "fl": "SEC_4840",
                            "status": 2
                          },
                          "fr86": {
                            "fl": "fr86",
                            "status": 2
                          },
                          "MET_24": {
                            "fl": "MET_24",
                            "status": 1
                          },
                          "MET_800": {
                            "fl": "MET_800",
                            "status": 1
                          },
                          "fl2038": {
                            "fl": "fl2038",
                            "status": 1
                          },
                          "fl2037": {
                            "fl": "fl2037",
                            "status": 2
                          },
                          "fl688": {
                            "fl": "fl688",
                            "status": 2
                          },
                          "fl660009": {
                            "fl": "fl660009",
                            "status": 2
                          },
                          "MET_570": {
                            "fl": "MET_570",
                            "status": 1
                          },
                          "fl893": {
                            "fl": "fl893",
                            "status": 2
                          },
                          "fl10417": {
                            "fl": "fl10417",
                            "status": 2
                          },
                          "fl10419": {
                            "fl": "fl10419",
                            "status": 2
                          },
                          "fl2050": {
                            "fl": "fl2050",
                            "status": 2
                          },
                          "fl2047": {
                            "fl": "fl2047",
                            "status": 2
                          },
                          "fl2046": {
                            "fl": "fl2046",
                            "status": 2
                          },
                          "fl2044": {
                            "fl": "fl2044",
                            "status": 2
                          },
                          "fl2043": {
                            "fl": "fl2043",
                            "status": 2
                          },
                          "fl2042": {
                            "fl": "fl2042",
                            "status": 2
                          },
                          "fl2041": {
                            "fl": "fl2041",
                            "status": 2
                          },
                          "fl2040": {
                            "fl": "fl2040",
                            "status": 2
                          },
                          "fl659": {
                            "fl": "fl659",
                            "status": 2
                          },
                          "fl10410": {
                            "fl": "fl10410",
                            "status": 2
                          },
                          "fl10411": {
                            "fl": "fl10411",
                            "status": 2
                          },
                          "fl665": {
                            "fl": "fl665",
                            "status": 2
                          },
                          "fl663": {
                            "fl": "fl663",
                            "status": 2
                          },
                          "fl421": {
                            "fl": "fl421",
                            "status": 2
                          },
                          "fl10426": {
                            "fl": "fl10426",
                            "status": 2
                          },
                          "fl420": {
                            "fl": "fl420",
                            "status": 2
                          },
                          "fl10428": {
                            "fl": "fl10428",
                            "status": 2
                          },
                          "fl2061": {
                            "fl": "fl2061",
                            "status": 1
                          },
                          "fl660011": {
                            "fl": "fl660011",
                            "status": 2
                          },
                          "fl2060": {
                            "fl": "fl2060",
                            "status": 2
                          },
                          "fl660010": {
                            "fl": "fl660010",
                            "status": 2
                          },
                          "fl2052": {
                            "fl": "fl2052",
                            "status": 2
                          },
                          "fl2051": {
                            "fl": "fl2051",
                            "status": 2
                          },
                          "MET_85": {
                            "fl": "MET_85",
                            "status": 1
                          },
                          "fl429": {
                            "fl": "fl429",
                            "status": 2
                          },
                          "fl426": {
                            "fl": "fl426",
                            "status": 2
                          },
                          "fl19": {
                            "fl": "fl19",
                            "status": 2
                          },
                          "fl18": {
                            "fl": "fl18",
                            "status": 2
                          },
                          "fl17": {
                            "fl": "fl17",
                            "status": 1
                          },
                          "fl16": {
                            "fl": "fl16",
                            "status": 2
                          },
                          "fl13": {
                            "fl": "fl13",
                            "status": 2
                          },
                          "fl12": {
                            "fl": "fl12",
                            "status": 2
                          },
                          "fl11": {
                            "fl": "fl11",
                            "status": 2
                          },
                          "fl2070": {
                            "fl": "fl2070",
                            "status": 2
                          },
                          "MET_57": {
                            "fl": "MET_57",
                            "status": 2
                          },
                          "fl877": {
                            "fl": "fl877",
                            "status": 2
                          },
                          "fl10401": {
                            "fl": "fl10401",
                            "status": 2
                          },
                          "fl10403": {
                            "fl": "fl10403",
                            "status": 2
                          },
                          "fl10404": {
                            "fl": "fl10404",
                            "status": 2
                          },
                          "fl10409": {
                            "fl": "fl10409",
                            "status": 2
                          },
                          "fr89100": {
                            "fl": "fr89100",
                            "status": 2
                          },
                          "fl2073": {
                            "fl": "fl2073",
                            "status": 2
                          },
                          "fl409": {
                            "fl": "fl409",
                            "status": 2
                          },
                          "fl405": {
                            "fl": "fl405",
                            "status": 2
                          },
                          "MET_206": {
                            "fl": "MET_206",
                            "status": 2
                          },
                          "fl404": {
                            "fl": "fl404",
                            "status": 2
                          },
                          "fl402": {
                            "fl": "fl402",
                            "status": 2
                          },
                          "fl177": {
                            "fl": "fl177",
                            "status": 2
                          },
                          "fl34": {
                            "fl": "fl34",
                            "status": 2
                          },
                          "fl33": {
                            "fl": "fl33",
                            "status": 2
                          },
                          "fl2093": {
                            "fl": "fl2093",
                            "status": 2
                          },
                          "fl32": {
                            "fl": "fl32",
                            "status": 2
                          },
                          "fl31": {
                            "fl": "fl31",
                            "status": 2
                          },
                          "fl2091": {
                            "fl": "fl2091",
                            "status": 2
                          },
                          "fl2090": {
                            "fl": "fl2090",
                            "status": 2
                          },
                          "fl721006": {
                            "fl": "fl721006",
                            "status": 2
                          },
                          "fl2089": {
                            "fl": "fl2089",
                            "status": 2
                          },
                          "MET_773": {
                            "fl": "MET_773",
                            "status": 1
                          },
                          "fl25": {
                            "fl": "fl25",
                            "status": 2
                          },
                          "fl24": {
                            "fl": "fl24",
                            "status": 2
                          },
                          "fl2096": {
                            "fl": "fl2096",
                            "status": 1
                          },
                          "fl2095": {
                            "fl": "fl2095",
                            "status": 2
                          },
                          "SEC_5668": {
                            "fl": "SEC_5668",
                            "status": 2
                          },
                          "fl393": {
                            "fl": "fl393",
                            "status": 2
                          },
                          "fl54": {
                            "fl": "fl54",
                            "status": 2
                          },
                          "fl270": {
                            "fl": "fl270",
                            "status": 2
                          },
                          "fl53": {
                            "fl": "fl53",
                            "status": 2
                          },
                          "fl52": {
                            "fl": "fl52",
                            "status": 2
                          },
                          "fl810020": {
                            "fl": "fl810020",
                            "status": 2
                          },
                          "fl51": {
                            "fl": "fl51",
                            "status": 2
                          },
                          "fl810022": {
                            "fl": "fl810022",
                            "status": 2
                          },
                          "fl810021": {
                            "fl": "fl810021",
                            "status": 2
                          },
                          "fl810013": {
                            "fl": "fl810013",
                            "status": 2
                          },
                          "fl810012": {
                            "fl": "fl810012",
                            "status": 2
                          },
                          "fl810015": {
                            "fl": "fl810015",
                            "status": 2
                          },
                          "fl810017": {
                            "fl": "fl810017",
                            "status": 2
                          },
                          "fl810016": {
                            "fl": "fl810016",
                            "status": 2
                          },
                          "fl810019": {
                            "fl": "fl810019",
                            "status": 2
                          },
                          "fl810018": {
                            "fl": "fl810018",
                            "status": 2
                          },
                          "MET_880": {
                            "fl": "MET_880",
                            "status": 1
                          },
                          "fl41": {
                            "fl": "fl41",
                            "status": 2
                          },
                          "fl810011": {
                            "fl": "fl810011",
                            "status": 2
                          },
                          "fl810010": {
                            "fl": "fl810010",
                            "status": 2
                          },
                          "fl810002": {
                            "fl": "fl810002",
                            "status": 2
                          },
                          "fl810001": {
                            "fl": "fl810001",
                            "status": 2
                          },
                          "fl810004": {
                            "fl": "fl810004",
                            "status": 2
                          },
                          "fl810003": {
                            "fl": "fl810003",
                            "status": 2
                          },
                          "fl810006": {
                            "fl": "fl810006",
                            "status": 2
                          },
                          "fl810008": {
                            "fl": "fl810008",
                            "status": 2
                          },
                          "fl810007": {
                            "fl": "fl810007",
                            "status": 2
                          }
                        }
                      }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl691031"
                                  },
                                  {
                                    "fl": "fl691032"
                                  },
                                  {
                                    "fl": "fl691133"
                                  },
                                  {
                                    "fl": "fl452"
                                  },
                                  {
                                    "fl": "fl550007"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève anonymisé 7, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile":  {
                        "niveau": "term",
                        "bac": "Générale",
                        "duree": "indiff",
                        "apprentissage": "C",
                        "geo_pref": [
                          "Strasbourg"
                        ],
                        "spe_classes": [
                          "Langues, littératures et cultures étrangères et régionales",
                          "Sciences Economiques et Sociales"
                        ],
                        "scores": {
                          "T_ROME_58088585": 1,
                          "T_IDEO2_4818": 1,
                          "T_IDEO2_4813": 1,
                          "T_ITM_PERSO1": 1,
                          "T_ROME_1959553899": 1,
                          "T_ITM_918": 1,
                          "T_ROME_762517279": 1,
                          "T_ROME_637471645": 1,
                          "T_ROME_1547781503": 1,
                          "T_ITM_636": 1,
                          "T_ROME_2027610093": 1,
                          "T_ITM_1039": 1,
                          "T_IDEO2_4829": 1,
                          "T_ROME_269720073": 1,
                          "T_IDEO2_4824": 1,
                          "T_IDEO2_4822": 1,
                          "T_IDEO2_4806": 1,
                          "T_ROME_898671777": 1,
                          "T_IDEO2_4825": 1,
                          "T_ITM_1043": 1,
                          "T_ITM_1044": 1,
                          "T_ITM_1020": 1,
                          "T_ROME_1573349427": 1,
                          "T_ITM_1025": 1,
                          "T_ITM_1067": 1,
                          "T_ROME_1665443017": 1,
                          "T_ITM_1420": 1
                        },
                        "moygen": "",
                        "choices": {
                          "MET_175": {
                            "fl": "MET_175",
                            "status": 2
                          },
                          "fl840009": {
                            "fl": "fl840009",
                            "status": 2
                          },
                          "fl840008": {
                            "fl": "fl840008",
                            "status": 2
                          },
                          "fl840007": {
                            "fl": "fl840007",
                            "status": 2
                          },
                          "fl840006": {
                            "fl": "fl840006",
                            "status": 2
                          },
                          "MET_7814": {
                            "fl": "MET_7814",
                            "status": 2
                          },
                          "fl840001": {
                            "fl": "fl840001",
                            "status": 2
                          },
                          "fl840000": {
                            "fl": "fl840000",
                            "status": 2
                          },
                          "MET_179": {
                            "fl": "MET_179",
                            "status": 2
                          },
                          "SEC_4871": {
                            "fl": "SEC_4871",
                            "status": 2
                          },
                          "SEC_4870": {
                            "fl": "SEC_4870",
                            "status": 2
                          },
                          "fl840004": {
                            "fl": "fl840004",
                            "status": 2
                          },
                          "fl840003": {
                            "fl": "fl840003",
                            "status": 2
                          },
                          "fl840002": {
                            "fl": "fl840002",
                            "status": 2
                          },
                          "fl691142": {
                            "fl": "fl691142",
                            "status": 2
                          },
                          "fl680002": {
                            "fl": "fl680002",
                            "status": 2
                          },
                          "fl680003": {
                            "fl": "fl680003",
                            "status": 2
                          },
                          "fl720007": {
                            "fl": "fl720007",
                            "status": 2
                          },
                          "fl388": {
                            "fl": "fl388",
                            "status": 2
                          },
                          "SEC_4869": {
                            "fl": "SEC_4869",
                            "status": 2
                          },
                          "fl680001": {
                            "fl": "fl680001",
                            "status": 2
                          },
                          "SEC_4868": {
                            "fl": "SEC_4868",
                            "status": 2
                          },
                          "SEC_4867": {
                            "fl": "SEC_4867",
                            "status": 2
                          },
                          "SEC_4866": {
                            "fl": "SEC_4866",
                            "status": 2
                          },
                          "fl250001": {
                            "fl": "fl250001",
                            "status": 2
                          },
                          "SEC_5712": {
                            "fl": "SEC_5712",
                            "status": 2
                          },
                          "SEC_4865": {
                            "fl": "SEC_4865",
                            "status": 2
                          },
                          "SEC_4864": {
                            "fl": "SEC_4864",
                            "status": 2
                          },
                          "fl382": {
                            "fl": "fl382",
                            "status": 2
                          },
                          "fl1": {
                            "fl": "fl1",
                            "status": 2
                          },
                          "fl2": {
                            "fl": "fl2",
                            "status": 2
                          },
                          "fl5": {
                            "fl": "fl5",
                            "status": 2
                          },
                          "fl4": {
                            "fl": "fl4",
                            "status": 2
                          },
                          "MET_180": {
                            "fl": "MET_180",
                            "status": 1
                          },
                          "fl840012": {
                            "fl": "fl840012",
                            "status": 2
                          },
                          "SEC_4863": {
                            "fl": "SEC_4863",
                            "status": 2
                          },
                          "fl840011": {
                            "fl": "fl840011",
                            "status": 2
                          },
                          "SEC_4862": {
                            "fl": "SEC_4862",
                            "status": 2
                          },
                          "fl840010": {
                            "fl": "fl840010",
                            "status": 2
                          },
                          "SEC_4861": {
                            "fl": "SEC_4861",
                            "status": 1
                          },
                          "MET_621": {
                            "fl": "MET_621",
                            "status": 1
                          },
                          "SEC_4860": {
                            "fl": "SEC_4860",
                            "status": 2
                          },
                          "fr22": {
                            "fl": "fr22",
                            "status": 2
                          },
                          "MET_866": {
                            "fl": "MET_866",
                            "status": 2
                          },
                          "fl840013": {
                            "fl": "fl840013",
                            "status": 2
                          },
                          "MET_279": {
                            "fl": "MET_279",
                            "status": 1
                          },
                          "MET_163": {
                            "fl": "MET_163",
                            "status": 2
                          },
                          "fl242": {
                            "fl": "fl242",
                            "status": 2
                          },
                          "fl241": {
                            "fl": "fl241",
                            "status": 2
                          },
                          "fl240": {
                            "fl": "fl240",
                            "status": 2
                          },
                          "fl2112": {
                            "fl": "fl2112",
                            "status": 2
                          },
                          "fl2110": {
                            "fl": "fl2110",
                            "status": 2
                          },
                          "MET_720": {
                            "fl": "MET_720",
                            "status": 2
                          },
                          "fl691032": {
                            "fl": "fl691032",
                            "status": 2
                          },
                          "MET_491": {
                            "fl": "MET_491",
                            "status": 2
                          },
                          "SEC_4838": {
                            "fl": "SEC_4838",
                            "status": 2
                          },
                          "fl455": {
                            "fl": "fl455",
                            "status": 2
                          },
                          "SEC_4837": {
                            "fl": "SEC_4837",
                            "status": 2
                          },
                          "MET_490": {
                            "fl": "MET_490",
                            "status": 1
                          },
                          "fl454": {
                            "fl": "fl454",
                            "status": 2
                          },
                          "SEC_4836": {
                            "fl": "SEC_4836",
                            "status": 2
                          },
                          "fl453": {
                            "fl": "fl453",
                            "status": 2
                          },
                          "SEC_4835": {
                            "fl": "SEC_4835",
                            "status": 1
                          },
                          "fl210": {
                            "fl": "fl210",
                            "status": 2
                          },
                          "fl452": {
                            "fl": "fl452",
                            "status": 2
                          },
                          "SEC_4834": {
                            "fl": "SEC_4834",
                            "status": 2
                          },
                          "fl451": {
                            "fl": "fl451",
                            "status": 2
                          },
                          "SEC_4833": {
                            "fl": "SEC_4833",
                            "status": 1
                          },
                          "SEC_4832": {
                            "fl": "SEC_4832",
                            "status": 2
                          },
                          "SEC_4831": {
                            "fl": "SEC_4831",
                            "status": 2
                          },
                          "SEC_4839": {
                            "fl": "SEC_4839",
                            "status": 2
                          },
                          "fl2003": {
                            "fl": "fl2003",
                            "status": 2
                          },
                          "fl2002": {
                            "fl": "fl2002",
                            "status": 2
                          },
                          "MET_38": {
                            "fl": "MET_38",
                            "status": 2
                          },
                          "MET_939": {
                            "fl": "MET_939",
                            "status": 1
                          },
                          "fl810514": {
                            "fl": "fl810514",
                            "status": 2
                          },
                          "SEC_4830": {
                            "fl": "SEC_4830",
                            "status": 2
                          },
                          "fl2009": {
                            "fl": "fl2009",
                            "status": 1
                          },
                          "fl2008": {
                            "fl": "fl2008",
                            "status": 2
                          },
                          "fl2006": {
                            "fl": "fl2006",
                            "status": 2
                          },
                          "fl810519": {
                            "fl": "fl810519",
                            "status": 2
                          },
                          "MET_931": {
                            "fl": "MET_931",
                            "status": 2
                          },
                          "fl458": {
                            "fl": "fl458",
                            "status": 2
                          },
                          "fl464": {
                            "fl": "fl464",
                            "status": 2
                          },
                          "MET_261": {
                            "fl": "MET_261",
                            "status": 2
                          },
                          "fl2014": {
                            "fl": "fl2014",
                            "status": 2
                          },
                          "fl2013": {
                            "fl": "fl2013",
                            "status": 2
                          },
                          "fl2010": {
                            "fl": "fl2010",
                            "status": 2
                          },
                          "MET_41": {
                            "fl": "MET_41",
                            "status": 1
                          },
                          "MET_149": {
                            "fl": "MET_149",
                            "status": 2
                          },
                          "MET_941": {
                            "fl": "MET_941",
                            "status": 2
                          },
                          "fl2019": {
                            "fl": "fl2019",
                            "status": 1
                          },
                          "fl810506": {
                            "fl": "fl810506",
                            "status": 2
                          },
                          "fl2018": {
                            "fl": "fl2018",
                            "status": 2
                          },
                          "fl847008": {
                            "fl": "fl847008",
                            "status": 2
                          },
                          "fl810508": {
                            "fl": "fl810508",
                            "status": 2
                          },
                          "fl2017": {
                            "fl": "fl2017",
                            "status": 2
                          },
                          "fl2015": {
                            "fl": "fl2015",
                            "status": 2
                          },
                          "SEC_4859": {
                            "fl": "SEC_4859",
                            "status": 2
                          },
                          "fl10436": {
                            "fl": "fl10436",
                            "status": 2
                          },
                          "MET_592": {
                            "fl": "MET_592",
                            "status": 2
                          },
                          "SEC_4858": {
                            "fl": "SEC_4858",
                            "status": 2
                          },
                          "fl10437": {
                            "fl": "fl10437",
                            "status": 2
                          },
                          "SEC_4857": {
                            "fl": "SEC_4857",
                            "status": 2
                          },
                          "SEC_4856": {
                            "fl": "SEC_4856",
                            "status": 2
                          },
                          "SEC_4855": {
                            "fl": "SEC_4855",
                            "status": 2
                          },
                          "SEC_4854": {
                            "fl": "SEC_4854",
                            "status": 2
                          },
                          "MET_354": {
                            "fl": "MET_354",
                            "status": 1
                          },
                          "SEC_4853": {
                            "fl": "SEC_4853",
                            "status": 2
                          },
                          "fl2024": {
                            "fl": "fl2024",
                            "status": 2
                          },
                          "fl620123": {
                            "fl": "fl620123",
                            "status": 2
                          },
                          "fl2023": {
                            "fl": "fl2023",
                            "status": 2
                          },
                          "fr90": {
                            "fl": "fr90",
                            "status": 2
                          },
                          "fl2022": {
                            "fl": "fl2022",
                            "status": 2
                          },
                          "fl2021": {
                            "fl": "fl2021",
                            "status": 2
                          },
                          "MET_919": {
                            "fl": "MET_919",
                            "status": 2
                          },
                          "SEC_4852": {
                            "fl": "SEC_4852",
                            "status": 2
                          },
                          "MET_114": {
                            "fl": "MET_114",
                            "status": 1
                          },
                          "SEC_4851": {
                            "fl": "SEC_4851",
                            "status": 2
                          },
                          "MET_11": {
                            "fl": "MET_11",
                            "status": 2
                          },
                          "SEC_4850": {
                            "fl": "SEC_4850",
                            "status": 2
                          },
                          "fl2029": {
                            "fl": "fl2029",
                            "status": 1
                          },
                          "fl2028": {
                            "fl": "fl2028",
                            "status": 2
                          },
                          "fl2027": {
                            "fl": "fl2027",
                            "status": 1
                          },
                          "fl324": {
                            "fl": "fl324",
                            "status": 2
                          },
                          "SEC_4849": {
                            "fl": "SEC_4849",
                            "status": 2
                          },
                          "SEC_4848": {
                            "fl": "SEC_4848",
                            "status": 2
                          },
                          "SEC_4847": {
                            "fl": "SEC_4847",
                            "status": 2
                          },
                          "SEC_4846": {
                            "fl": "SEC_4846",
                            "status": 1
                          },
                          "SEC_4845": {
                            "fl": "SEC_4845",
                            "status": 2
                          },
                          "SEC_4844": {
                            "fl": "SEC_4844",
                            "status": 2
                          },
                          "SEC_4843": {
                            "fl": "SEC_4843",
                            "status": 2
                          },
                          "SEC_4842": {
                            "fl": "SEC_4842",
                            "status": 2
                          },
                          "fl2033": {
                            "fl": "fl2033",
                            "status": 2
                          },
                          "fl2032": {
                            "fl": "fl2032",
                            "status": 2
                          },
                          "fl2030": {
                            "fl": "fl2030",
                            "status": 1
                          },
                          "fr83": {
                            "fl": "fr83",
                            "status": 2
                          },
                          "SEC_4841": {
                            "fl": "SEC_4841",
                            "status": 2
                          },
                          "fr85": {
                            "fl": "fr85",
                            "status": 2
                          },
                          "SEC_4840": {
                            "fl": "SEC_4840",
                            "status": 2
                          },
                          "fr86": {
                            "fl": "fr86",
                            "status": 2
                          },
                          "MET_451": {
                            "fl": "MET_451",
                            "status": 2
                          },
                          "MET_331": {
                            "fl": "MET_331",
                            "status": 2
                          },
                          "fl2050": {
                            "fl": "fl2050",
                            "status": 2
                          },
                          "fl2046": {
                            "fl": "fl2046",
                            "status": 2
                          },
                          "fl2044": {
                            "fl": "fl2044",
                            "status": 2
                          },
                          "fl2040": {
                            "fl": "fl2040",
                            "status": 2
                          },
                          "MET_699": {
                            "fl": "MET_699",
                            "status": 2
                          },
                          "MET_77": {
                            "fl": "MET_77",
                            "status": 2
                          },
                          "MET_214": {
                            "fl": "MET_214",
                            "status": 2
                          },
                          "MET_583": {
                            "fl": "MET_583",
                            "status": 2
                          },
                          "MET_93": {
                            "fl": "MET_93",
                            "status": 2
                          },
                          "fl10428": {
                            "fl": "fl10428",
                            "status": 2
                          },
                          "MET_585": {
                            "fl": "MET_585",
                            "status": 1
                          },
                          "fl2060": {
                            "fl": "fl2060",
                            "status": 2
                          },
                          "fl2052": {
                            "fl": "fl2052",
                            "status": 2
                          },
                          "MET_89": {
                            "fl": "MET_89",
                            "status": 2
                          },
                          "fl19": {
                            "fl": "fl19",
                            "status": 2
                          },
                          "fl18": {
                            "fl": "fl18",
                            "status": 2
                          },
                          "fl17": {
                            "fl": "fl17",
                            "status": 2
                          },
                          "fl13": {
                            "fl": "fl13",
                            "status": 2
                          },
                          "fl12": {
                            "fl": "fl12",
                            "status": 2
                          },
                          "fl11": {
                            "fl": "fl11",
                            "status": 2
                          },
                          "MET_52": {
                            "fl": "MET_52",
                            "status": 1
                          },
                          "MET_57": {
                            "fl": "MET_57",
                            "status": 2
                          },
                          "fl877": {
                            "fl": "fl877",
                            "status": 2
                          },
                          "MET_7744": {
                            "fl": "MET_7744",
                            "status": 1
                          },
                          "fl405": {
                            "fl": "fl405",
                            "status": 2
                          },
                          "MET_568": {
                            "fl": "MET_568",
                            "status": 2
                          },
                          "MET_326": {
                            "fl": "MET_326",
                            "status": 2
                          },
                          "fl34": {
                            "fl": "fl34",
                            "status": 2
                          },
                          "fl33": {
                            "fl": "fl33",
                            "status": 2
                          },
                          "fl32": {
                            "fl": "fl32",
                            "status": 2
                          },
                          "fl31": {
                            "fl": "fl31",
                            "status": 2
                          },
                          "fl2090": {
                            "fl": "fl2090",
                            "status": 2
                          },
                          "fl2089": {
                            "fl": "fl2089",
                            "status": 2
                          },
                          "MET_778": {
                            "fl": "MET_778",
                            "status": 2
                          },
                          "MET_537": {
                            "fl": "MET_537",
                            "status": 2
                          },
                          "fl25": {
                            "fl": "fl25",
                            "status": 2
                          },
                          "fl24": {
                            "fl": "fl24",
                            "status": 2
                          },
                          "fl2095": {
                            "fl": "fl2095",
                            "status": 2
                          },
                          "MET_871": {
                            "fl": "MET_871",
                            "status": 1
                          },
                          "SEC_5668": {
                            "fl": "SEC_5668",
                            "status": 2
                          },
                          "fl393": {
                            "fl": "fl393",
                            "status": 2
                          },
                          "fl271": {
                            "fl": "fl271",
                            "status": 2
                          },
                          "fl270": {
                            "fl": "fl270",
                            "status": 2
                          },
                          "fl54": {
                            "fl": "fl54",
                            "status": 2
                          },
                          "fl391": {
                            "fl": "fl391",
                            "status": 2
                          },
                          "fl53": {
                            "fl": "fl53",
                            "status": 2
                          },
                          "fl52": {
                            "fl": "fl52",
                            "status": 2
                          },
                          "fl51": {
                            "fl": "fl51",
                            "status": 2
                          },
                          "MET_639": {
                            "fl": "MET_639",
                            "status": 2
                          },
                          "MET_759": {
                            "fl": "MET_759",
                            "status": 2
                          },
                          "fl810014": {
                            "fl": "fl810014",
                            "status": 2
                          },
                          "fl810016": {
                            "fl": "fl810016",
                            "status": 2
                          },
                          "MET_878": {
                            "fl": "MET_878",
                            "status": 2
                          },
                          "fl691118": {
                            "fl": "fl691118",
                            "status": 2
                          },
                          "fl810011": {
                            "fl": "fl810011",
                            "status": 2
                          },
                          "fl810010": {
                            "fl": "fl810010",
                            "status": 2
                          },
                          "fl810001": {
                            "fl": "fl810001",
                            "status": 2
                          },
                          "fl810004": {
                            "fl": "fl810004",
                            "status": 2
                          },
                          "fl810006": {
                            "fl": "fl810006",
                            "status": 2
                          },
                          "MET_885": {
                            "fl": "MET_885",
                            "status": 1
                          },
                          "MET_768": {
                            "fl": "MET_768",
                            "status": 1
                          }
                        }
                      }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl10413"
                                  },
                                  {
                                    "fl": "fl2051"
                                  },
                                  {
                                    "fl": "fl691031"
                                  },
                                  {
                                    "fl": "fr89100"
                                  },
                                  {
                                    "fl": "fl230"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `pour l'élève anonymisé 8, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile":  {
                        "niveau": "term",
                        "bac": "Générale",
                        "duree": "indiff",
                        "apprentissage": "C",
                        "geo_pref": [
                          "Montpellier",
                          "Chambéry",
                          "Rouen"
                        ],
                        "spe_classes": [
                          "Mathématiques",
                          "Physique-Chimie"
                        ],
                        "scores": {
                          "T_IDEO2_4809": 1,
                          "T_IDEO2_4807": 1,
                          "T_ROME_1825212206": 1,
                          "T_IDEO2_4812": 1,
                          "T_ROME_609891024": 1,
                          "T_IDEO2_4824": 1,
                          "T_IDEO2_4817": 1,
                          "T_IDEO2_4806": 1,
                          "T_ROME_1088162470": 1,
                          "T_IDEO2_4815": 1,
                          "T_ROME_1959553899": 1,
                          "T_ITM_1044": 1,
                          "T_ROME_762517279": 1,
                          "T_ROME_600174291": 1,
                          "T_ITM_1180": 1,
                          "T_ROME_731379930": 1,
                          "T_ROME_1547781503": 1,
                          "T_ROME_2027610093": 1,
                          "T_ITM_933": 1,
                          "T_ITM_936": 1
                        },
                        "moygen": "14.5",
                        "choices": {
                          "MET_293": {
                            "fl": "MET_293",
                            "status": 2
                          },
                          "MET_172": {
                            "fl": "MET_172",
                            "status": 2
                          },
                          "MET_451": {
                            "fl": "MET_451",
                            "status": 2
                          },
                          "MET_692": {
                            "fl": "MET_692",
                            "status": 2
                          },
                          "MET_331": {
                            "fl": "MET_331",
                            "status": 2
                          },
                          "MET_7814": {
                            "fl": "MET_7814",
                            "status": 2
                          },
                          "MET_212": {
                            "fl": "MET_212",
                            "status": 2
                          },
                          "MET_699": {
                            "fl": "MET_699",
                            "status": 2
                          },
                          "MET_77": {
                            "fl": "MET_77",
                            "status": 2
                          },
                          "SEC_4871": {
                            "fl": "SEC_4871",
                            "status": 2
                          },
                          "SEC_4870": {
                            "fl": "SEC_4870",
                            "status": 2
                          },
                          "SEC_4869": {
                            "fl": "SEC_4869",
                            "status": 2
                          },
                          "SEC_4868": {
                            "fl": "SEC_4868",
                            "status": 2
                          },
                          "MET_580": {
                            "fl": "MET_580",
                            "status": 2
                          },
                          "SEC_4867": {
                            "fl": "SEC_4867",
                            "status": 2
                          },
                          "MET_93": {
                            "fl": "MET_93",
                            "status": 2
                          },
                          "SEC_4866": {
                            "fl": "SEC_4866",
                            "status": 2
                          },
                          "MET_343": {
                            "fl": "MET_343",
                            "status": 2
                          },
                          "SEC_4865": {
                            "fl": "SEC_4865",
                            "status": 2
                          },
                          "SEC_5712": {
                            "fl": "SEC_5712",
                            "status": 2
                          },
                          "MET_342": {
                            "fl": "MET_342",
                            "status": 2
                          },
                          "MET_100": {
                            "fl": "MET_100",
                            "status": 2
                          },
                          "SEC_4864": {
                            "fl": "SEC_4864",
                            "status": 2
                          },
                          "SEC_5711": {
                            "fl": "SEC_5711",
                            "status": 2
                          },
                          "SEC_4863": {
                            "fl": "SEC_4863",
                            "status": 2
                          },
                          "MET_344": {
                            "fl": "MET_344",
                            "status": 2
                          },
                          "SEC_4862": {
                            "fl": "SEC_4862",
                            "status": 2
                          },
                          "MET_586": {
                            "fl": "MET_586",
                            "status": 2
                          },
                          "MET_622": {
                            "fl": "MET_622",
                            "status": 2
                          },
                          "SEC_4861": {
                            "fl": "SEC_4861",
                            "status": 2
                          },
                          "SEC_4860": {
                            "fl": "SEC_4860",
                            "status": 2
                          },
                          "MET_89": {
                            "fl": "MET_89",
                            "status": 2
                          },
                          "MET_901": {
                            "fl": "MET_901",
                            "status": 2
                          },
                          "MET_108": {
                            "fl": "MET_108",
                            "status": 2
                          },
                          "MET_672": {
                            "fl": "MET_672",
                            "status": 2
                          },
                          "MET_312": {
                            "fl": "MET_312",
                            "status": 2
                          },
                          "MET_311": {
                            "fl": "MET_311",
                            "status": 2
                          },
                          "MET_52": {
                            "fl": "MET_52",
                            "status": 2
                          },
                          "MET_677": {
                            "fl": "MET_677",
                            "status": 2
                          },
                          "MET_434": {
                            "fl": "MET_434",
                            "status": 2
                          },
                          "MET_799": {
                            "fl": "MET_799",
                            "status": 2
                          },
                          "MET_713": {
                            "fl": "MET_713",
                            "status": 2
                          },
                          "MET_163": {
                            "fl": "MET_163",
                            "status": 1
                          },
                          "MET_563": {
                            "fl": "MET_563",
                            "status": 2
                          },
                          "MET_166": {
                            "fl": "MET_166",
                            "status": 2
                          },
                          "MET_727": {
                            "fl": "MET_727",
                            "status": 2
                          },
                          "MET_7744": {
                            "fl": "MET_7744",
                            "status": 1
                          },
                          "MET_64": {
                            "fl": "MET_64",
                            "status": 2
                          },
                          "MET_201": {
                            "fl": "MET_201",
                            "status": 2
                          },
                          "MET_842": {
                            "fl": "MET_842",
                            "status": 2
                          },
                          "MET_204": {
                            "fl": "MET_204",
                            "status": 2
                          },
                          "MET_720": {
                            "fl": "MET_720",
                            "status": 2
                          },
                          "MET_445": {
                            "fl": "MET_445",
                            "status": 2
                          },
                          "MET_844": {
                            "fl": "MET_844",
                            "status": 2
                          },
                          "MET_602": {
                            "fl": "MET_602",
                            "status": 2
                          },
                          "MET_67": {
                            "fl": "MET_67",
                            "status": 2
                          },
                          "MET_604": {
                            "fl": "MET_604",
                            "status": 2
                          },
                          "MET_449": {
                            "fl": "MET_449",
                            "status": 2
                          },
                          "MET_328": {
                            "fl": "MET_328",
                            "status": 2
                          },
                          "SEC_4838": {
                            "fl": "SEC_4838",
                            "status": 2
                          },
                          "SEC_4837": {
                            "fl": "SEC_4837",
                            "status": 2
                          },
                          "SEC_4836": {
                            "fl": "SEC_4836",
                            "status": 2
                          },
                          "SEC_4835": {
                            "fl": "SEC_4835",
                            "status": 2
                          },
                          "SEC_4834": {
                            "fl": "SEC_4834",
                            "status": 2
                          },
                          "SEC_4833": {
                            "fl": "SEC_4833",
                            "status": 1
                          },
                          "SEC_4832": {
                            "fl": "SEC_4832",
                            "status": 2
                          },
                          "MET_497": {
                            "fl": "MET_497",
                            "status": 2
                          },
                          "SEC_4831": {
                            "fl": "SEC_4831",
                            "status": 2
                          },
                          "SEC_4839": {
                            "fl": "SEC_4839",
                            "status": 2
                          },
                          "MET_38": {
                            "fl": "MET_38",
                            "status": 2
                          },
                          "MET_937": {
                            "fl": "MET_937",
                            "status": 1
                          },
                          "MET_939": {
                            "fl": "MET_939",
                            "status": 1
                          },
                          "SEC_4830": {
                            "fl": "SEC_4830",
                            "status": 2
                          },
                          "MET_136": {
                            "fl": "MET_136",
                            "status": 2
                          },
                          "MET_261": {
                            "fl": "MET_261",
                            "status": 1
                          },
                          "MET_707": {
                            "fl": "MET_707",
                            "status": 2
                          },
                          "MET_784": {
                            "fl": "MET_784",
                            "status": 2
                          },
                          "MET_41": {
                            "fl": "MET_41",
                            "status": 2
                          },
                          "MET_820": {
                            "fl": "MET_820",
                            "status": 2
                          },
                          "MET_941": {
                            "fl": "MET_941",
                            "status": 2
                          },
                          "MET_44": {
                            "fl": "MET_44",
                            "status": 2
                          },
                          "MET_43": {
                            "fl": "MET_43",
                            "status": 2
                          },
                          "MET_668": {
                            "fl": "MET_668",
                            "status": 1
                          },
                          "MET_46": {
                            "fl": "MET_46",
                            "status": 2
                          },
                          "MET_45": {
                            "fl": "MET_45",
                            "status": 2
                          },
                          "MET_824": {
                            "fl": "MET_824",
                            "status": 2
                          },
                          "MET_669": {
                            "fl": "MET_669",
                            "status": 2
                          },
                          "MET_590": {
                            "fl": "MET_590",
                            "status": 2
                          },
                          "SEC_4859": {
                            "fl": "SEC_4859",
                            "status": 2
                          },
                          "MET_592": {
                            "fl": "MET_592",
                            "status": 2
                          },
                          "SEC_4858": {
                            "fl": "SEC_4858",
                            "status": 2
                          },
                          "SEC_4857": {
                            "fl": "SEC_4857",
                            "status": 2
                          },
                          "SEC_4856": {
                            "fl": "SEC_4856",
                            "status": 1
                          },
                          "MET_110": {
                            "fl": "MET_110",
                            "status": 2
                          },
                          "SEC_4855": {
                            "fl": "SEC_4855",
                            "status": 2
                          },
                          "MET_593": {
                            "fl": "MET_593",
                            "status": 2
                          },
                          "MET_871": {
                            "fl": "MET_871",
                            "status": 2
                          },
                          "SEC_5668": {
                            "fl": "SEC_5668",
                            "status": 2
                          },
                          "MET_112": {
                            "fl": "MET_112",
                            "status": 2
                          },
                          "SEC_4854": {
                            "fl": "SEC_4854",
                            "status": 2
                          },
                          "SEC_4853": {
                            "fl": "SEC_4853",
                            "status": 2
                          },
                          "MET_916": {
                            "fl": "MET_916",
                            "status": 2
                          },
                          "MET_918": {
                            "fl": "MET_918",
                            "status": 2
                          },
                          "MET_919": {
                            "fl": "MET_919",
                            "status": 2
                          },
                          "SEC_4852": {
                            "fl": "SEC_4852",
                            "status": 2
                          },
                          "SEC_4851": {
                            "fl": "SEC_4851",
                            "status": 2
                          },
                          "SEC_4850": {
                            "fl": "SEC_4850",
                            "status": 2
                          },
                          "MET_753": {
                            "fl": "MET_753",
                            "status": 2
                          },
                          "MET_599": {
                            "fl": "MET_599",
                            "status": 2
                          },
                          "MET_876": {
                            "fl": "MET_876",
                            "status": 1
                          },
                          "MET_634": {
                            "fl": "MET_634",
                            "status": 2
                          },
                          "MET_879": {
                            "fl": "MET_879",
                            "status": 2
                          },
                          "SEC_4849": {
                            "fl": "SEC_4849",
                            "status": 2
                          },
                          "SEC_4848": {
                            "fl": "SEC_4848",
                            "status": 2
                          },
                          "MET_361": {
                            "fl": "MET_361",
                            "status": 2
                          },
                          "SEC_4847": {
                            "fl": "SEC_4847",
                            "status": 2
                          },
                          "SEC_4846": {
                            "fl": "SEC_4846",
                            "status": 1
                          },
                          "SEC_4845": {
                            "fl": "SEC_4845",
                            "status": 2
                          },
                          "SEC_4844": {
                            "fl": "SEC_4844",
                            "status": 2
                          },
                          "SEC_4843": {
                            "fl": "SEC_4843",
                            "status": 2
                          },
                          "SEC_4842": {
                            "fl": "SEC_4842",
                            "status": 2
                          },
                          "SEC_4841": {
                            "fl": "SEC_4841",
                            "status": 2
                          },
                          "SEC_4840": {
                            "fl": "SEC_4840",
                            "status": 2
                          }
                        }
                      }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fl550007"
                                  },
                                  {
                                    "fl": "fr22"
                                  },
                                  {
                                    "fl": "fl1002020"
                                  },
                                  {
                                    "fl": "fl847008"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }
        @Test
        fun `pour l'élève anonymisé 9, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile":  {
                        "niveau": "term",
                        "bac": "Générale",
                        "duree": "long",
                        "apprentissage": "C",
                        "geo_pref": [
                          "Chambéry",
                          "Grenoble",
                          "Valence (26000)"
                        ],
                        "spe_classes": [
                          "Humanités, Littérature et Philosophie",
                          "Histoire-Géographie, Géopolitique et Sciences politiques"
                        ],
                        "scores": {
                          "T_IDEO2_4808": 1,
                          "T_ROME_313545038": 1,
                          "T_ITM_1284": 1,
                          "T_ROME_1814691478": 1,
                          "T_IDEO2_4820": 1,
                          "T_ROME_2018646295": 1,
                          "T_ROME_731379930": 1,
                          "T_IDEO2_4812": 1,
                          "T_ROME_749075906": 1
                        },
                        "moygen": "10",
                        "choices": {
                          "SEC_4838": {
                            "fl": "SEC_4838",
                            "status": 2
                          },
                          "MET_293": {
                            "fl": "MET_293",
                            "status": 2
                          },
                          "SEC_4837": {
                            "fl": "SEC_4837",
                            "status": 2
                          },
                          "SEC_4836": {
                            "fl": "SEC_4836",
                            "status": 2
                          },
                          "SEC_4835": {
                            "fl": "SEC_4835",
                            "status": 2
                          },
                          "SEC_4834": {
                            "fl": "SEC_4834",
                            "status": 2
                          },
                          "SEC_4833": {
                            "fl": "SEC_4833",
                            "status": 1
                          },
                          "MET_890": {
                            "fl": "MET_890",
                            "status": 2
                          },
                          "SEC_4832": {
                            "fl": "SEC_4832",
                            "status": 2
                          },
                          "SEC_4831": {
                            "fl": "SEC_4831",
                            "status": 2
                          },
                          "SEC_4839": {
                            "fl": "SEC_4839",
                            "status": 2
                          },
                          "fl2002": {
                            "fl": "fl2002",
                            "status": 1
                          },
                          "MET_38": {
                            "fl": "MET_38",
                            "status": 2
                          },
                          "SEC_4830": {
                            "fl": "SEC_4830",
                            "status": 2
                          },
                          "MET_33": {
                            "fl": "MET_33",
                            "status": 1
                          },
                          "SEC_4871": {
                            "fl": "SEC_4871",
                            "status": 2
                          },
                          "SEC_4870": {
                            "fl": "SEC_4870",
                            "status": 2
                          },
                          "SEC_4869": {
                            "fl": "SEC_4869",
                            "status": 2
                          },
                          "SEC_4868": {
                            "fl": "SEC_4868",
                            "status": 2
                          },
                          "SEC_4867": {
                            "fl": "SEC_4867",
                            "status": 2
                          },
                          "SEC_4866": {
                            "fl": "SEC_4866",
                            "status": 1
                          },
                          "SEC_4865": {
                            "fl": "SEC_4865",
                            "status": 2
                          },
                          "SEC_5712": {
                            "fl": "SEC_5712",
                            "status": 2
                          },
                          "SEC_5711": {
                            "fl": "SEC_5711",
                            "status": 2
                          },
                          "SEC_4864": {
                            "fl": "SEC_4864",
                            "status": 2
                          },
                          "SEC_4863": {
                            "fl": "SEC_4863",
                            "status": 2
                          },
                          "SEC_4862": {
                            "fl": "SEC_4862",
                            "status": 2
                          },
                          "SEC_4861": {
                            "fl": "SEC_4861",
                            "status": 2
                          },
                          "SEC_4860": {
                            "fl": "SEC_4860",
                            "status": 2
                          },
                          "MET_668": {
                            "fl": "MET_668",
                            "status": 1
                          },
                          "SEC_4859": {
                            "fl": "SEC_4859",
                            "status": 2
                          },
                          "SEC_4858": {
                            "fl": "SEC_4858",
                            "status": 2
                          },
                          "SEC_4857": {
                            "fl": "SEC_4857",
                            "status": 2
                          },
                          "SEC_4856": {
                            "fl": "SEC_4856",
                            "status": 2
                          },
                          "SEC_4855": {
                            "fl": "SEC_4855",
                            "status": 2
                          },
                          "MET_472": {
                            "fl": "MET_472",
                            "status": 1
                          },
                          "SEC_5668": {
                            "fl": "SEC_5668",
                            "status": 2
                          },
                          "SEC_4854": {
                            "fl": "SEC_4854",
                            "status": 2
                          },
                          "SEC_4853": {
                            "fl": "SEC_4853",
                            "status": 2
                          },
                          "SEC_4852": {
                            "fl": "SEC_4852",
                            "status": 2
                          },
                          "SEC_4851": {
                            "fl": "SEC_4851",
                            "status": 2
                          },
                          "MET_311": {
                            "fl": "MET_311",
                            "status": 1
                          },
                          "SEC_4850": {
                            "fl": "SEC_4850",
                            "status": 2
                          },
                          "SEC_4848": {
                            "fl": "SEC_4848",
                            "status": 2
                          },
                          "SEC_4847": {
                            "fl": "SEC_4847",
                            "status": 2
                          },
                          "SEC_4846": {
                            "fl": "SEC_4846",
                            "status": 2
                          },
                          "SEC_4845": {
                            "fl": "SEC_4845",
                            "status": 2
                          },
                          "SEC_4844": {
                            "fl": "SEC_4844",
                            "status": 2
                          },
                          "SEC_4843": {
                            "fl": "SEC_4843",
                            "status": 2
                          },
                          "SEC_4842": {
                            "fl": "SEC_4842",
                            "status": 2
                          },
                          "MET_924": {
                            "fl": "MET_924",
                            "status": 1
                          },
                          "SEC_4841": {
                            "fl": "SEC_4841",
                            "status": 2
                          },
                          "SEC_4840": {
                            "fl": "SEC_4840",
                            "status": 2
                          },
                          "MET_687": {
                            "fl": "MET_687",
                            "status": 2
                          }
                        }
                      }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fl2010"
                                  },
                                  {
                                    "fl": "fr641"
                                  },
                                  {
                                    "fl": "fl2017"
                                  },
                                  {
                                    "fl": "fl810509"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }
        @Test
        fun `pour l'élève anonymisé 10, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile":  {
                        "niveau": "term",
                        "bac": "Générale",
                        "duree": "long",
                        "apprentissage": "B",
                        "geo_pref": [
                          "Chambéry",
                          "Grenoble",
                          "Annecy"
                        ],
                        "spe_classes": [
                          "Humanités, Littérature et Philosophie",
                          "Histoire-Géographie, Géopolitique et Sciences politiques"
                        ],
                        "scores": {
                          "T_IDEO2_4809": 1,
                          "T_IDEO2_4818": 1,
                          "T_ITM_821": 1,
                          "T_ROME_2018646295": 1,
                          "T_IDEO2_4812": 1,
                          "T_ROME_934089965": 1,
                          "T_ROME_898671777": 1,
                          "T_ITM_PERSO1": 1,
                          "T_ITM_1021": 1,
                          "T_ITM_1054": 1,
                          "T_ROME_1959553899": 1,
                          "T_ITM_1044": 1,
                          "T_ITM_918": 1,
                          "T_ROME_1814691478": 1,
                          "T_ROME_731379930": 1,
                          "T_ROME_2027610093": 1,
                          "T_ITM_723": 1,
                          "T_ROME_1991903888": 1,
                          "T_ITM_917": 1
                        },
                        "moygen": "",
                        "choices": {
                          "MET_293": {
                            "fl": "MET_293",
                            "status": 2
                          },
                          "MET_570": {
                            "fl": "MET_570",
                            "status": 2
                          },
                          "MET_693": {
                            "fl": "MET_693",
                            "status": 2
                          },
                          "MET_451": {
                            "fl": "MET_451",
                            "status": 2
                          },
                          "fl680018": {
                            "fl": "fl680018",
                            "status": 2
                          },
                          "MET_692": {
                            "fl": "MET_692",
                            "status": 2
                          },
                          "MET_450": {
                            "fl": "MET_450",
                            "status": 2
                          },
                          "MET_331": {
                            "fl": "MET_331",
                            "status": 2
                          },
                          "MET_452": {
                            "fl": "MET_452",
                            "status": 2
                          },
                          "MET_170": {
                            "fl": "MET_170",
                            "status": 2
                          },
                          "MET_738": {
                            "fl": "MET_738",
                            "status": 2
                          },
                          "fl691147": {
                            "fl": "fl691147",
                            "status": 2
                          },
                          "fl691149": {
                            "fl": "fl691149",
                            "status": 2
                          },
                          "MET_179": {
                            "fl": "MET_179",
                            "status": 2
                          },
                          "MET_77": {
                            "fl": "MET_77",
                            "status": 2
                          },
                          "MET_699": {
                            "fl": "MET_699",
                            "status": 2
                          },
                          "MET_214": {
                            "fl": "MET_214",
                            "status": 2
                          },
                          "SEC_4871": {
                            "fl": "SEC_4871",
                            "status": 1
                          },
                          "MET_7815": {
                            "fl": "MET_7815",
                            "status": 2
                          },
                          "SEC_4870": {
                            "fl": "SEC_4870",
                            "status": 1
                          },
                          "fl691140": {
                            "fl": "fl691140",
                            "status": 2
                          },
                          "fl691142": {
                            "fl": "fl691142",
                            "status": 2
                          },
                          "fl10423": {
                            "fl": "fl10423",
                            "status": 2
                          },
                          "SEC_4869": {
                            "fl": "SEC_4869",
                            "status": 2
                          },
                          "SEC_4868": {
                            "fl": "SEC_4868",
                            "status": 2
                          },
                          "MET_580": {
                            "fl": "MET_580",
                            "status": 2
                          },
                          "SEC_4867": {
                            "fl": "SEC_4867",
                            "status": 2
                          },
                          "SEC_4866": {
                            "fl": "SEC_4866",
                            "status": 1
                          },
                          "SEC_4865": {
                            "fl": "SEC_4865",
                            "status": 2
                          },
                          "MET_343": {
                            "fl": "MET_343",
                            "status": 2
                          },
                          "SEC_5712": {
                            "fl": "SEC_5712",
                            "status": 2
                          },
                          "MET_585": {
                            "fl": "MET_585",
                            "status": 2
                          },
                          "SEC_4864": {
                            "fl": "SEC_4864",
                            "status": 2
                          },
                          "SEC_5711": {
                            "fl": "SEC_5711",
                            "status": 2
                          },
                          "MET_100": {
                            "fl": "MET_100",
                            "status": 2
                          },
                          "MET_342": {
                            "fl": "MET_342",
                            "status": 2
                          },
                          "MET_180": {
                            "fl": "MET_180",
                            "status": 2
                          },
                          "MET_5": {
                            "fl": "MET_5",
                            "status": 2
                          },
                          "fl691132": {
                            "fl": "fl691132",
                            "status": 2
                          },
                          "fl560002": {
                            "fl": "fl560002",
                            "status": 1
                          },
                          "fl691133": {
                            "fl": "fl691133",
                            "status": 2
                          },
                          "fl691134": {
                            "fl": "fl691134",
                            "status": 2
                          },
                          "MET_2": {
                            "fl": "MET_2",
                            "status": 1
                          },
                          "fl691135": {
                            "fl": "fl691135",
                            "status": 2
                          },
                          "fl1002014": {
                            "fl": "fl1002014",
                            "status": 2
                          },
                          "SEC_4863": {
                            "fl": "SEC_4863",
                            "status": 2
                          },
                          "MET_86": {
                            "fl": "MET_86",
                            "status": 2
                          },
                          "MET_85": {
                            "fl": "MET_85",
                            "status": 2
                          },
                          "SEC_4862": {
                            "fl": "SEC_4862",
                            "status": 2
                          },
                          "SEC_4861": {
                            "fl": "SEC_4861",
                            "status": 2
                          },
                          "SEC_4860": {
                            "fl": "SEC_4860",
                            "status": 2
                          },
                          "MET_866": {
                            "fl": "MET_866",
                            "status": 2
                          },
                          "MET_89": {
                            "fl": "MET_89",
                            "status": 2
                          },
                          "MET_469": {
                            "fl": "MET_469",
                            "status": 2
                          },
                          "MET_108": {
                            "fl": "MET_108",
                            "status": 2
                          },
                          "fl872": {
                            "fl": "fl872",
                            "status": 2
                          },
                          "MET_396": {
                            "fl": "MET_396",
                            "status": 2
                          },
                          "MET_431": {
                            "fl": "MET_431",
                            "status": 2
                          },
                          "MET_672": {
                            "fl": "MET_672",
                            "status": 1
                          },
                          "MET_52": {
                            "fl": "MET_52",
                            "status": 2
                          },
                          "MET_311": {
                            "fl": "MET_311",
                            "status": 2
                          },
                          "MET_434": {
                            "fl": "MET_434",
                            "status": 2
                          },
                          "MET_557": {
                            "fl": "MET_557",
                            "status": 1
                          },
                          "MET_58": {
                            "fl": "MET_58",
                            "status": 2
                          },
                          "MET_163": {
                            "fl": "MET_163",
                            "status": 2
                          },
                          "MET_285": {
                            "fl": "MET_285",
                            "status": 1
                          },
                          "MET_166": {
                            "fl": "MET_166",
                            "status": 2
                          },
                          "MET_7744": {
                            "fl": "MET_7744",
                            "status": 2
                          },
                          "MET_64": {
                            "fl": "MET_64",
                            "status": 2
                          },
                          "MET_446": {
                            "fl": "MET_446",
                            "status": 2
                          },
                          "MET_445": {
                            "fl": "MET_445",
                            "status": 1
                          },
                          "MET_720": {
                            "fl": "MET_720",
                            "status": 2
                          },
                          "fl691150": {
                            "fl": "fl691150",
                            "status": 2
                          },
                          "fl691151": {
                            "fl": "fl691151",
                            "status": 2
                          },
                          "MET_568": {
                            "fl": "MET_568",
                            "status": 2
                          },
                          "MET_604": {
                            "fl": "MET_604",
                            "status": 1
                          },
                          "fl691153": {
                            "fl": "fl691153",
                            "status": 2
                          },
                          "MET_491": {
                            "fl": "MET_491",
                            "status": 2
                          },
                          "SEC_4838": {
                            "fl": "SEC_4838",
                            "status": 2
                          },
                          "SEC_4837": {
                            "fl": "SEC_4837",
                            "status": 2
                          },
                          "SEC_4836": {
                            "fl": "SEC_4836",
                            "status": 2
                          },
                          "SEC_4835": {
                            "fl": "SEC_4835",
                            "status": 2
                          },
                          "SEC_4834": {
                            "fl": "SEC_4834",
                            "status": 2
                          },
                          "SEC_4833": {
                            "fl": "SEC_4833",
                            "status": 1
                          },
                          "MET_890": {
                            "fl": "MET_890",
                            "status": 2
                          },
                          "SEC_4832": {
                            "fl": "SEC_4832",
                            "status": 2
                          },
                          "MET_497": {
                            "fl": "MET_497",
                            "status": 1
                          },
                          "SEC_4831": {
                            "fl": "SEC_4831",
                            "status": 2
                          },
                          "fl2093": {
                            "fl": "fl2093",
                            "status": 1
                          },
                          "fl33": {
                            "fl": "fl33",
                            "status": 2
                          },
                          "SEC_4839": {
                            "fl": "SEC_4839",
                            "status": 2
                          },
                          "MET_38": {
                            "fl": "MET_38",
                            "status": 2
                          },
                          "MET_939": {
                            "fl": "MET_939",
                            "status": 1
                          },
                          "SEC_4830": {
                            "fl": "SEC_4830",
                            "status": 2
                          },
                          "MET_778": {
                            "fl": "MET_778",
                            "status": 2
                          },
                          "MET_536": {
                            "fl": "MET_536",
                            "status": 2
                          },
                          "fl699": {
                            "fl": "fl699",
                            "status": 2
                          },
                          "MET_36": {
                            "fl": "MET_36",
                            "status": 2
                          },
                          "fl464": {
                            "fl": "fl464",
                            "status": 1
                          },
                          "MET_261": {
                            "fl": "MET_261",
                            "status": 2
                          },
                          "MET_143": {
                            "fl": "MET_143",
                            "status": 2
                          },
                          "MET_780": {
                            "fl": "MET_780",
                            "status": 2
                          },
                          "MET_51": {
                            "fl": "MET_51",
                            "status": 1
                          },
                          "fl2014": {
                            "fl": "fl2014",
                            "status": 1
                          },
                          "MET_301": {
                            "fl": "MET_301",
                            "status": 2
                          },
                          "MET_784": {
                            "fl": "MET_784",
                            "status": 2
                          },
                          "MET_146": {
                            "fl": "MET_146",
                            "status": 2
                          },
                          "MET_41": {
                            "fl": "MET_41",
                            "status": 1
                          },
                          "MET_941": {
                            "fl": "MET_941",
                            "status": 2
                          },
                          "MET_43": {
                            "fl": "MET_43",
                            "status": 2
                          },
                          "MET_423": {
                            "fl": "MET_423",
                            "status": 2
                          },
                          "MET_668": {
                            "fl": "MET_668",
                            "status": 1
                          },
                          "fl865": {
                            "fl": "fl865",
                            "status": 2
                          },
                          "fl2016": {
                            "fl": "fl2016",
                            "status": 1
                          },
                          "MET_48": {
                            "fl": "MET_48",
                            "status": 2
                          },
                          "MET_307": {
                            "fl": "MET_307",
                            "status": 2
                          },
                          "MET_702": {
                            "fl": "MET_702",
                            "status": 2
                          },
                          "MET_590": {
                            "fl": "MET_590",
                            "status": 1
                          },
                          "SEC_4859": {
                            "fl": "SEC_4859",
                            "status": 2
                          },
                          "SEC_4858": {
                            "fl": "SEC_4858",
                            "status": 2
                          },
                          "SEC_4857": {
                            "fl": "SEC_4857",
                            "status": 2
                          },
                          "MET_591": {
                            "fl": "MET_591",
                            "status": 1
                          },
                          "MET_110": {
                            "fl": "MET_110",
                            "status": 2
                          },
                          "SEC_4856": {
                            "fl": "SEC_4856",
                            "status": 1
                          },
                          "MET_352": {
                            "fl": "MET_352",
                            "status": 2
                          },
                          "SEC_4855": {
                            "fl": "SEC_4855",
                            "status": 2
                          },
                          "MET_112": {
                            "fl": "MET_112",
                            "status": 2
                          },
                          "MET_871": {
                            "fl": "MET_871",
                            "status": 1
                          },
                          "SEC_5668": {
                            "fl": "SEC_5668",
                            "status": 2
                          },
                          "SEC_4854": {
                            "fl": "SEC_4854",
                            "status": 2
                          },
                          "MET_475": {
                            "fl": "MET_475",
                            "status": 2
                          },
                          "SEC_4853": {
                            "fl": "SEC_4853",
                            "status": 2
                          },
                          "fl691129": {
                            "fl": "fl691129",
                            "status": 2
                          },
                          "fl271": {
                            "fl": "fl271",
                            "status": 1
                          },
                          "fl270": {
                            "fl": "fl270",
                            "status": 1
                          },
                          "fl2024": {
                            "fl": "fl2024",
                            "status": 1
                          },
                          "MET_759": {
                            "fl": "MET_759",
                            "status": 2
                          },
                          "fl880001": {
                            "fl": "fl880001",
                            "status": 2
                          },
                          "MET_918": {
                            "fl": "MET_918",
                            "status": 2
                          },
                          "MET_919": {
                            "fl": "MET_919",
                            "status": 2
                          },
                          "SEC_4852": {
                            "fl": "SEC_4852",
                            "status": 2
                          },
                          "MET_97": {
                            "fl": "MET_97",
                            "status": 2
                          },
                          "MET_114": {
                            "fl": "MET_114",
                            "status": 2
                          },
                          "MET_597": {
                            "fl": "MET_597",
                            "status": 2
                          },
                          "SEC_4851": {
                            "fl": "SEC_4851",
                            "status": 2
                          },
                          "fl836": {
                            "fl": "fl836",
                            "status": 2
                          },
                          "SEC_4850": {
                            "fl": "SEC_4850",
                            "status": 1
                          },
                          "fl880002": {
                            "fl": "fl880002",
                            "status": 2
                          },
                          "MET_753": {
                            "fl": "MET_753",
                            "status": 2
                          },
                          "MET_239": {
                            "fl": "MET_239",
                            "status": 2
                          },
                          "MET_876": {
                            "fl": "MET_876",
                            "status": 2
                          },
                          "MET_634": {
                            "fl": "MET_634",
                            "status": 1
                          },
                          "MET_755": {
                            "fl": "MET_755",
                            "status": 2
                          },
                          "MET_879": {
                            "fl": "MET_879",
                            "status": 2
                          },
                          "fl691120": {
                            "fl": "fl691120",
                            "status": 2
                          },
                          "MET_878": {
                            "fl": "MET_878",
                            "status": 2
                          },
                          "SEC_4849": {
                            "fl": "SEC_4849",
                            "status": 2
                          },
                          "MET_480": {
                            "fl": "MET_480",
                            "status": 2
                          },
                          "SEC_4848": {
                            "fl": "SEC_4848",
                            "status": 2
                          },
                          "SEC_4847": {
                            "fl": "SEC_4847",
                            "status": 2
                          },
                          "MET_361": {
                            "fl": "MET_361",
                            "status": 2
                          },
                          "fl442": {
                            "fl": "fl442",
                            "status": 2
                          },
                          "SEC_4846": {
                            "fl": "SEC_4846",
                            "status": 1
                          },
                          "MET_880": {
                            "fl": "MET_880",
                            "status": 1
                          },
                          "SEC_4845": {
                            "fl": "SEC_4845",
                            "status": 1
                          },
                          "SEC_4844": {
                            "fl": "SEC_4844",
                            "status": 2
                          },
                          "SEC_4843": {
                            "fl": "SEC_4843",
                            "status": 2
                          },
                          "SEC_4842": {
                            "fl": "SEC_4842",
                            "status": 2
                          },
                          "MET_803": {
                            "fl": "MET_803",
                            "status": 1
                          },
                          "MET_884": {
                            "fl": "MET_884",
                            "status": 2
                          },
                          "SEC_4841": {
                            "fl": "SEC_4841",
                            "status": 2
                          },
                          "SEC_4840": {
                            "fl": "SEC_4840",
                            "status": 1
                          },
                          "MET_762": {
                            "fl": "MET_762",
                            "status": 2
                          },
                          "MET_883": {
                            "fl": "MET_883",
                            "status": 1
                          },
                          "MET_522": {
                            "fl": "MET_522",
                            "status": 2
                          },
                          "MET_885": {
                            "fl": "MET_885",
                            "status": 2
                          },
                          "MET_25": {
                            "fl": "MET_25",
                            "status": 1
                          }
                        }
                      }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl31"
                                  },
                                  {
                                    "fl": "fl680003"
                                  },
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fl32"
                                  },
                                  {
                                    "fl": "fl680001"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }
        @Test
        fun `pour l'élève anonymisé 11, il faut retourner ses suggestions`() {
            // given
            val requete = """
                {
                  "profile":  {
                        "niveau": "term",
                        "bac": "Générale",
                        "duree": "long",
                        "apprentissage": "B",
                        "geo_pref": [
                          "Grenoble",
                          "Lyon"
                        ],
                        "spe_classes": [
                          "Mathématiques",
                          "Physique-Chimie"
                        ],
                        "scores": {
                          "T_ROME_58088585": 1,
                          "T_ITM_762": 1,
                          "T_ITM_794": 1,
                          "T_ROME_84652368": 1,
                          "T_ITM_611": 1,
                          "T_ITM_PERSO9": 1,
                          "T_IDEO2_4823": 1,
                          "T_IDEO2_4813": 1,
                          "T_IDEO2_4824": 1,
                          "T_ROME_934089965": 1,
                          "T_IDEO2_4816": 1,
                          "T_IDEO2_4805": 1,
                          "T_ITM_PERSO4": 1,
                          "T_ROME_2092381917": 1,
                          "T_ROME_1547781503": 1,
                          "T_ROME_2027610093": 1,
                          "T_ROME_1665443017": 1,
                          "T_ITM_1112": 1
                        },
                        "moygen": "16.5",
                        "choices": {
                          "SEC_4848": {
                            "fl": "SEC_4848",
                            "status": 1
                          },
                          "fl2111": {
                            "fl": "fl2111",
                            "status": 1
                          },
                          "SEC_4857": {
                            "fl": "SEC_4857",
                            "status": 2
                          },
                          "fl810018": {
                            "fl": "fl810018",
                            "status": 1
                          },
                          "MET_433": {
                            "fl": "MET_433",
                            "status": 1
                          },
                          "fl2061": {
                            "fl": "fl2061",
                            "status": 1
                          },
                          "MET_774": {
                            "fl": "MET_774",
                            "status": 1
                          },
                          "MET_589": {
                            "fl": "MET_589",
                            "status": 1
                          },
                          "MET_349": {
                            "fl": "MET_349",
                            "status": 1
                          },
                          "MET_725": {
                            "fl": "MET_725",
                            "status": 1
                          }
                        }
                      }
                }
            """.trimIndent()

            // when-then
            mvc.perform(
                post("/api/1.1/public/suggestions").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete),
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                            {
                              "header": {
                                "status": 0
                              },
                              "suggestions": {
                                "suggestions": [
                                  {
                                    "fl": "fl680003"
                                  },
                                  {
                                    "fl": "fl680002"
                                  },
                                  {
                                    "fl": "fl17"
                                  },
                                  {
                                    "fl": "fr22"
                                  },
                                  {
                                    "fl": "fr83"
                                  },
                                  {
                                    "fl": "SEC_4846"
                                  },
                                  {
                                    "fl": "SEC_4863"
                                  },
                                  {
                                    "fl": "SEC_4852"
                                  },
                                  {
                                    "fl": "SEC_4845"
                                  },
                                  {
                                    "fl": "SEC_5668"
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    )
                )
        }
    }

    @Nested
    inner class `Quand on appelle la route des formations of interest` {
        // TODO
    }

    @Nested
    inner class `Quand on appelle la route des explanations` {
        // TODO
    }
}