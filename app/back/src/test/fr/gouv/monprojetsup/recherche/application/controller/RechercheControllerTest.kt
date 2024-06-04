package fr.gouv.monprojetsup.recherche.application.controller

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetIllegalStateErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.recherche.domain.entity.AutoEvaluationMoyenne
import fr.gouv.monprojetsup.recherche.domain.entity.Centilles
import fr.gouv.monprojetsup.recherche.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.recherche.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.recherche.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.recherche.domain.entity.CriteresAdmission
import fr.gouv.monprojetsup.recherche.domain.entity.ExplicationGeographique
import fr.gouv.monprojetsup.recherche.domain.entity.ExplicationsSuggestion
import fr.gouv.monprojetsup.recherche.domain.entity.FicheFormation
import fr.gouv.monprojetsup.recherche.domain.entity.FormationPourProfil
import fr.gouv.monprojetsup.recherche.domain.entity.MetierDetaille
import fr.gouv.monprojetsup.recherche.domain.entity.MoyenneGenerale
import fr.gouv.monprojetsup.recherche.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.recherche.domain.entity.Tag
import fr.gouv.monprojetsup.recherche.domain.entity.TypeBaccalaureat
import fr.gouv.monprojetsup.recherche.usecase.RecupererFormationService
import fr.gouv.monprojetsup.recherche.usecase.SuggestionsFormationsService
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.net.ConnectException

@WebMvcTest(controllers = [RechercheController::class])
class RechercheControllerTest(
    @Autowired val mvc: MockMvc,
) {
    @MockBean
    lateinit var suggestionsFormationsService: SuggestionsFormationsService

    @MockBean
    lateinit var recupererFormationService: RecupererFormationService

    private val unProfil =
        ProfilEleve(
            id = "adcf627c-36dd-4df5-897b-159443a6d49c",
            classe = ChoixNiveau.TERMINALE,
            bac = "Générale",
            dureeEtudesPrevue = ChoixDureeEtudesPrevue.OPTIONS_OUVERTES,
            alternance = ChoixAlternance.PAS_INTERESSE,
            villesPreferees = listOf("Paris"),
            specialites = listOf("1056", "1054"),
            centresInterets = listOf("T_ROME_2092381917", "T_IDEO2_4812"),
            moyenneGenerale = 14f,
            metiersChoisis = listOf("MET_123", "MET_456"),
            formationsChoisies = listOf("fl1234", "fl5678"),
            domainesInterets = listOf("T_ITM_1054", "T_ITM_1534", "T_ITM_1248", "T_ITM_1351"),
        )
    private val requete =
        """
        {
          "profil": {
            "id": "adcf627c-36dd-4df5-897b-159443a6d49c",
            "situation": "aucune_idee",
            "classe": "terminale",
            "bac": "Générale",
            "specialites": [
              "1056",
              "1054"
            ],
            "domaines": [
              "T_ITM_1054",
              "T_ITM_1534",
              "T_ITM_1248",
              "T_ITM_1351"
            ],
            "centresInterets": [
              "T_ROME_2092381917",
              "T_IDEO2_4812"
            ],
            "situationMetiers": "quelques_pistes",
            "metiers": [
              "MET_123",
              "MET_456"
            ],
            "dureeEtudesPrevue": "options_ouvertes",
            "alternance": "pas_interesse",
            "situationVilles": "quelques_pistes",
            "villes": [
              {
                "codeInsee": "75015",
                "nom": "Paris",
                "latitude": 2.2885659,
                "longitude": 48.8512252
              }
            ],
            "moyenneGenerale": 14,
            "situationFormations": "quelques_pistes",
            "formations": [
              "fl1234",
              "fl5678"
            ]
          }
        }
        """.trimIndent()

    @Nested
    inner class `Quand on appelle la route de recherche de formations` {
        @Test
        fun `si le service réussi, doit retourner 200 avec une liste des FormationsDTO`() {
            // Given
            val formationPourProfil =
                FormationPourProfil(
                    id = "fl680002",
                    nom = "Cycle pluridisciplinaire d'Études Supérieures - Science",
                    tauxAffinite = 0.9f,
                    communesTrieesParAffinites =
                        listOf(
                            "Paris  5e  Arrondissement",
                            "Paris 16e  Arrondissement",
                        ),
                    metiersTriesParAffinites =
                        listOf(
                            "géomaticien/ne",
                            "documentaliste",
                            "vétérinaire",
                        ),
                )
            given(suggestionsFormationsService.suggererFormations(unProfil, 0, 50)).willReturn(
                listOf(
                    formationPourProfil,
                ),
            )

            // when-then
            mvc.perform(
                post("/api/v1/formations/recherche").contentType(MediaType.APPLICATION_JSON).content(requete)
                    .accept(MediaType.APPLICATION_JSON),
            ).andDo(print()).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "formations": [
                            {
                              "id": "fl680002",
                              "nom": "Cycle pluridisciplinaire d'Études Supérieures - Science",
                              "tauxAffinite": 0.9,
                              "villes": [
                                "Paris  5e  Arrondissement",
                                "Paris 16e  Arrondissement"
                              ],
                              "metiers": [
                                "géomaticien/ne",
                                "documentaliste",
                                "vétérinaire"
                              ]
                            }
                          ]
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @Test
        fun `si le service échoue avec une erreur interne, alors doit retourner 500`() {
            // Given
            val uneException =
                MonProjetSupInternalErrorException(
                    code = "ERREUR_API_SUGGESTIONS_CONNEXION",
                    msg = "Erreur lors de la connexion à l'API de suggestions",
                    origine = ConnectException("Connection refused"),
                )
            Mockito.`when`(suggestionsFormationsService.suggererFormations(unProfil, 0, 50)).thenThrow(uneException)

            // when-then
            mvc.perform(
                post("/api/v1/formations/recherche").contentType(MediaType.APPLICATION_JSON).content(requete)
                    .accept(MediaType.APPLICATION_JSON),
            ).andDo(print()).andExpect(status().isInternalServerError)
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
        }
    }

    @Nested
    inner class `Quand on appelle la route de récupération d'une formation` {
        @Test
        fun `si le service réussi pour un appel avec un profil, doit retourner 200 avec le détail de la formation`() {
            // Given
            val ficheFormation =
                FicheFormation.FicheFormationPourProfil(
                    id = "fl680002",
                    nom = "Cycle pluridisciplinaire d'Études Supérieures - Science",
                    tauxAffinite = 0.9f,
                    communesTrieesParAffinites =
                        listOf(
                            "Paris  5e  Arrondissement",
                            "Paris 16e  Arrondissement",
                        ),
                    metiersTriesParAffinites =
                        listOf(
                            MetierDetaille(
                                id = "MET001",
                                nom = "géomaticien/ne",
                                descriptif =
                                    "À la croisée de la géographie et de l'informatique, le géomaticien ou la géomaticienne " +
                                        "exploite les données pour modéliser le territoire",
                                liens = listOf("https://www.onisep.fr/ressources/univers-metier/metiers/geomaticien-geomaticienne"),
                            ),
                            MetierDetaille(
                                id = "MET002",
                                nom = "documentaliste",
                                descriptif = null,
                                liens = emptyList(),
                            ),
                        ),
                    formationsAssociees = listOf("fl0012"),
                    descriptifFormation =
                        "Les formations CPES recrutent des lycéen.nes de très bon niveau sur sélection et dispensent " +
                            "des enseignements pluri-disciplinaires (scientifiques, artistiques, de sciences sociales, de littérature) " +
                            "permettant une poursuite d'études en master ou en grande école. Il s’agit de formations ouvertes " +
                            "socialement recrutant 40% de boursiers sur critères sociaux. Elles sont organisées conjointement " +
                            "par un établissement d’enseignement secondaire lycée et un établissement de l’enseignement supérieur, " +
                            "une université.",
                    descriptifDiplome =
                        "Les formations CPES sont des diplômes d’établissement diplômants en trois ans qui " +
                            "conférent le grade de licence.",
                    descriptifAttendus =
                        "Il est attendu des candidats de démontrer une solide compréhension des techniques de base " +
                            "de la floristerie, y compris la composition florale, la reconnaissance des plantes et " +
                            "des fleurs, ainsi que les soins et l'entretien des végétaux.",
                    descriptifConseils =
                        "Nous vous conseillons de développer une sensibilité artistique et de rester informé des tendances " +
                            "actuelles en matière de design floral pour exceller dans ce domaine.",
                    criteresAdmission =
                        CriteresAdmission(
                            principauxPoints =
                                listOf(
                                    "Les résultats scolaires",
                                    "Les compétences, méthodes de travail et savoir-faire",
                                ),
                            moyenneGenerale =
                                MoyenneGenerale(
                                    centille5eme = 10f,
                                    centille25eme = 14f,
                                    centille75eme = 17f,
                                    centille95eme = 19.5f,
                                ),
                        ),
                    liens =
                        listOf(
                            "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/" +
                                "cycle-pluridisciplinaire-d-etudes-superieures",
                        ),
                    explications =
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
                            formationsSimilaires = listOf("fl1", "fl7"),
                            dureeEtudesPrevue = ChoixDureeEtudesPrevue.LONGUE,
                            alternance = ChoixAlternance.TRES_INTERESSE,
                            interets = listOf("chat", "medecin"),
                            specialitesChoisies =
                                mapOf(
                                    "specialiteA" to 0.12,
                                    "specialiteB" to 0.1,
                                    "specialiteC" to 0.89,
                                ),
                            typeBaccalaureat =
                                TypeBaccalaureat(
                                    nomBaccalaureat = "Général",
                                    pourcentage = 18,
                                ),
                            autoEvaluationMoyenne =
                                AutoEvaluationMoyenne(
                                    moyenne = 14.5,
                                    mediane =
                                        Centilles(
                                            rangEch25 = 12,
                                            rangEch50 = 14,
                                            rangEch75 = 16,
                                            rangEch10 = 10,
                                            rangEch90 = 17,
                                        ),
                                    bacUtilise = "Général",
                                ),
                            tags =
                                listOf(
                                    Tag(
                                        noeuds = listOf("noeud1", "noeud2", "noeud3"),
                                        poid = 12.4,
                                    ),
                                    Tag(
                                        noeuds = listOf("noeud4"),
                                        poid = 18.0,
                                    ),
                                ),
                            tagsCourts =
                                listOf(
                                    "T_ROME_731379930",
                                    "T_IDEO2_4812",
                                    "T_ROME_803089798",
                                ),
                        ),
                )
            given(recupererFormationService.recupererFormation(unProfil, "fl680002")).willReturn(ficheFormation)

            // when-then
            mvc.perform(
                post("/api/v1/formations/fl680002").contentType(MediaType.APPLICATION_JSON).content(requete)
                    .accept(MediaType.APPLICATION_JSON),
            ).andDo(print()).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "formation": {
                            "id": "fl680002",
                            "nom": "Cycle pluridisciplinaire d'Études Supérieures - Science",
                            "formationsAssociees": [
                              "fl0012"
                            ],
                            "descriptifFormation": "Les formations CPES recrutent des lycéen.nes de très bon niveau sur sélection et dispensent des enseignements pluri-disciplinaires (scientifiques, artistiques, de sciences sociales, de littérature) permettant une poursuite d'études en master ou en grande école. Il s’agit de formations ouvertes socialement recrutant 40% de boursiers sur critères sociaux. Elles sont organisées conjointement par un établissement d’enseignement secondaire lycée et un établissement de l’enseignement supérieur, une université.",
                            "descriptifDiplome": "Les formations CPES sont des diplômes d’établissement diplômants en trois ans qui conférent le grade de licence.",
                            "descriptifConseils": "Nous vous conseillons de développer une sensibilité artistique et de rester informé des tendances actuelles en matière de design floral pour exceller dans ce domaine.",
                            "descriptifAttendus": "Il est attendu des candidats de démontrer une solide compréhension des techniques de base de la floristerie, y compris la composition florale, la reconnaissance des plantes et des fleurs, ainsi que les soins et l'entretien des végétaux.",
                            "criteresAdmission": {
                              "principauxPoints": [
                                "Les résultats scolaires",
                                "Les compétences, méthodes de travail et savoir-faire"
                              ],
                              "moyenneGenerale": {
                                "centille5eme": 10.0,
                                "centille25eme": 14.0,
                                "centille75eme": 17.0,
                                "centille95eme": 19.5
                              }
                            },
                            "liens": [
                              "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/cycle-pluridisciplinaire-d-etudes-superieures"
                            ],
                            "villes": [
                              "Paris  5e  Arrondissement",
                              "Paris 16e  Arrondissement"
                            ],
                            "metiers": [
                              {
                                "id": "MET001",
                                "nom": "géomaticien/ne",
                                "descriptif": "À la croisée de la géographie et de l'informatique, le géomaticien ou la géomaticienne exploite les données pour modéliser le territoire",
                                "liens": [
                                  "https://www.onisep.fr/ressources/univers-metier/metiers/geomaticien-geomaticienne"
                                ]
                              },
                              {
                                "id": "MET002",
                                "nom": "documentaliste",
                                "descriptif": null,
                                "liens": null
                              }
                            ],
                            "tauxAffinite": 0.9
                          },
                          "explications": {
                            "geographique": [
                              {
                                "nom": "Nantes",
                                "distanceKm": 1
                              },
                              {
                                "nom": "Nantes",
                                "distanceKm": 85
                              }
                            ],
                            "formationsSimilaires": [
                              "fl1",
                              "fl7"
                            ],
                            "dureeEtudesPrevue": "longue",
                            "alternance": "tres_interesse",
                            "interets": [
                              "chat",
                              "medecin"
                            ],
                            "specialitesChoisies": {
                              "specialiteA": 0.12,
                              "specialiteB": 0.1,
                              "specialiteC": 0.89
                            },
                            "typeBaccalaureat": {
                              "nomBaccalaureat": "Général",
                              "pourcentage": 18
                            },
                            "autoEvaluationMoyenne": {
                              "moyenne": 14.5,
                              "mediane": {
                                "rangEch25": 12,
                                "rangEch50": 14,
                                "rangEch75": 16,
                                "rangEch10": 10,
                                "rangEch90": 17
                              },
                              "bacUtilise": "Général"
                            },
                            "tags": [
                              {
                                "noeuds": [
                                  "noeud1",
                                  "noeud2",
                                  "noeud3"
                                ],
                                "poid": 12.4
                              },
                              {
                                "noeuds": [
                                  "noeud4"
                                ],
                                "poid": 18.0
                              }
                            ],
                            "tagsCourts": [
                              "T_ROME_731379930",
                              "T_IDEO2_4812",
                              "T_ROME_803089798"
                            ]
                          }
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @Test
        fun `si le service réussi pour un appel sans profil, doit retourner 200 avec le détail de la formation`() {
            // Given
            val ficheFormation =
                FicheFormation.FicheFormationSansProfil(
                    id = "fl680002",
                    nom = "Cycle pluridisciplinaire d'Études Supérieures - Science",
                    communes =
                        listOf(
                            "Paris  5e  Arrondissement",
                            "Paris 16e  Arrondissement",
                        ),
                    metiers =
                        listOf(
                            MetierDetaille(
                                id = "MET001",
                                nom = "géomaticien/ne",
                                descriptif =
                                    "À la croisée de la géographie et de l'informatique, le géomaticien ou la géomaticienne " +
                                        "exploite les données pour modéliser le territoire",
                                liens = listOf("https://www.onisep.fr/ressources/univers-metier/metiers/geomaticien-geomaticienne"),
                            ),
                            MetierDetaille(
                                id = "MET002",
                                nom = "documentaliste",
                                descriptif = null,
                                liens = emptyList(),
                            ),
                        ),
                    formationsAssociees = listOf("fl0012"),
                    descriptifFormation =
                        "Les formations CPES recrutent des lycéen.nes de très bon niveau sur sélection et dispensent " +
                            "des enseignements pluri-disciplinaires (scientifiques, artistiques, de sciences sociales, de littérature) " +
                            "permettant une poursuite d'études en master ou en grande école. Il s’agit de formations ouvertes " +
                            "socialement recrutant 40% de boursiers sur critères sociaux. Elles sont organisées conjointement " +
                            "par un établissement d’enseignement secondaire lycée et un établissement de l’enseignement supérieur, " +
                            "une université.",
                    descriptifDiplome =
                        "Les formations CPES sont des diplômes d’établissement diplômants en trois ans qui " +
                            "conférent le grade de licence.",
                    descriptifAttendus =
                        "Il est attendu des candidats de démontrer une solide compréhension des techniques de base " +
                            "de la floristerie, y compris la composition florale, la reconnaissance des plantes et " +
                            "des fleurs, ainsi que les soins et l'entretien des végétaux.",
                    descriptifConseils =
                        "Nous vous conseillons de développer une sensibilité artistique et de rester informé des tendances " +
                            "actuelles en matière de design floral pour exceller dans ce domaine.",
                    criteresAdmission =
                        CriteresAdmission(
                            principauxPoints =
                                listOf(
                                    "Les résultats scolaires",
                                    "Les compétences, méthodes de travail et savoir-faire",
                                ),
                            moyenneGenerale =
                                MoyenneGenerale(
                                    centille5eme = 10f,
                                    centille25eme = 14f,
                                    centille75eme = 17f,
                                    centille95eme = 19.5f,
                                ),
                        ),
                    liens =
                        listOf(
                            "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/" +
                                "cycle-pluridisciplinaire-d-etudes-superieures",
                        ),
                )
            given(recupererFormationService.recupererFormation(null, "fl680002")).willReturn(ficheFormation)

            // when-then
            mvc.perform(
                post("/api/v1/formations/fl680002").contentType(MediaType.APPLICATION_JSON).content(
                    """
                    {
                      "profil": null
                    }
                    """.trimIndent(),
                ).accept(MediaType.APPLICATION_JSON),
            ).andDo(print()).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "formation": {
                            "id": "fl680002",
                            "nom": "Cycle pluridisciplinaire d'Études Supérieures - Science",
                            "formationsAssociees": [
                              "fl0012"
                            ],
                            "descriptifFormation": "Les formations CPES recrutent des lycéen.nes de très bon niveau sur sélection et dispensent des enseignements pluri-disciplinaires (scientifiques, artistiques, de sciences sociales, de littérature) permettant une poursuite d'études en master ou en grande école. Il s’agit de formations ouvertes socialement recrutant 40% de boursiers sur critères sociaux. Elles sont organisées conjointement par un établissement d’enseignement secondaire lycée et un établissement de l’enseignement supérieur, une université.",
                            "descriptifDiplome": "Les formations CPES sont des diplômes d’établissement diplômants en trois ans qui conférent le grade de licence.",
                            "descriptifAttendus": "Il est attendu des candidats de démontrer une solide compréhension des techniques de base de la floristerie, y compris la composition florale, la reconnaissance des plantes et des fleurs, ainsi que les soins et l'entretien des végétaux.",
                            "criteresAdmission": {
                              "principauxPoints": [
                                "Les résultats scolaires",
                                "Les compétences, méthodes de travail et savoir-faire"
                              ],
                              "moyenneGenerale": {
                                "centille5eme": 10.0,
                                "centille25eme": 14.0,
                                "centille75eme": 17.0,
                                "centille95eme": 19.5
                              }
                            },
                            "descriptifConseils": "Nous vous conseillons de développer une sensibilité artistique et de rester informé des tendances actuelles en matière de design floral pour exceller dans ce domaine.",
                            "liens": [
                              "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/cycle-pluridisciplinaire-d-etudes-superieures"
                            ],
                            "villes": [
                              "Paris  5e  Arrondissement",
                              "Paris 16e  Arrondissement"
                            ],
                            "metiers": [
                              {
                                "id": "MET001",
                                "nom": "géomaticien/ne",
                                "descriptif": "À la croisée de la géographie et de l'informatique, le géomaticien ou la géomaticienne exploite les données pour modéliser le territoire",
                                "liens": ["https://www.onisep.fr/ressources/univers-metier/metiers/geomaticien-geomaticienne"]
                              },
                              {
                                "id": "MET002",
                                "nom": "documentaliste",
                                "descriptif": null,
                                "liens": null
                              }
                            ],
                            "tauxAffinite": null
                          },
                          "explications": null
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @Test
        fun `si le service échoue avec une erreur interne, alors doit retourner 500`() {
            // Given
            val uneException =
                MonProjetIllegalStateErrorException(
                    code = "RECHERCHE_FORMATION",
                    msg = "La formation fl00010 existe plusieurs fois entre id et dans les formations équivalentes",
                )
            Mockito.`when`(recupererFormationService.recupererFormation(unProfil, "fl00010")).thenThrow(uneException)

            // when-then
            mvc.perform(
                post("/api/v1/formations/fl00010").contentType(MediaType.APPLICATION_JSON).content(requete)
                    .accept(MediaType.APPLICATION_JSON),
            ).andDo(print()).andExpect(status().isInternalServerError)
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
        }

        @Test
        fun `si le service échoue avec une erreur not found, alors doit retourner 404`() {
            // Given
            val uneException =
                MonProjetSupNotFoundException(
                    code = "RECHERCHE_FORMATION",
                    msg = "La formation inconnu n'existe pas",
                )
            Mockito.`when`(recupererFormationService.recupererFormation(unProfil, "inconnu")).thenThrow(uneException)

            // when-then
            mvc.perform(
                post("/api/v1/formations/inconnu").contentType(MediaType.APPLICATION_JSON).content(requete)
                    .accept(MediaType.APPLICATION_JSON),
            ).andDo(print()).andExpect(status().isNotFound)
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
        }
    }
}
