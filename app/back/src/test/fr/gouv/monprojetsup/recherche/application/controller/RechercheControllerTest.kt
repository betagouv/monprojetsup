package fr.gouv.monprojetsup.recherche.application.controller

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetIllegalStateErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.recherche.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.recherche.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.recherche.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.recherche.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.recherche.domain.entity.Domaine
import fr.gouv.monprojetsup.recherche.domain.entity.ExplicationAutoEvaluationMoyenne
import fr.gouv.monprojetsup.recherche.domain.entity.ExplicationTypeBaccalaureat
import fr.gouv.monprojetsup.recherche.domain.entity.ExplicationsSuggestion
import fr.gouv.monprojetsup.recherche.domain.entity.FicheFormation
import fr.gouv.monprojetsup.recherche.domain.entity.Formation
import fr.gouv.monprojetsup.recherche.domain.entity.FormationDetaillee
import fr.gouv.monprojetsup.recherche.domain.entity.FormationPourProfil
import fr.gouv.monprojetsup.recherche.domain.entity.Interet
import fr.gouv.monprojetsup.recherche.domain.entity.MetierDetaille
import fr.gouv.monprojetsup.recherche.domain.entity.ProfilEleve
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
                    formation =
                        FormationDetaillee(
                            id = "fl680002",
                            nom = "Cycle pluridisciplinaire d'Études Supérieures - Science",
                            formationsAssociees = listOf("fl0012"),
                            descriptifGeneral =
                                "Les formations CPES recrutent des lycéen.nes de très bon niveau sur sélection et dispensent " +
                                    "des enseignements pluri-disciplinaires (scientifiques, artistiques, de sciences sociales, " +
                                    "de littérature) permettant une poursuite d'études en master ou en grande école. Il s’agit de " +
                                    "formations ouvertes socialement recrutant 40% de boursiers sur critères sociaux. Elles sont " +
                                    "organisées conjointement par un établissement d’enseignement secondaire lycée et un " +
                                    "établissement de l’enseignement supérieur, une université.",
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
                            liens =
                                listOf(
                                    "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/" +
                                        "cycle-pluridisciplinaire-d-etudes-superieures",
                                ),
                            metiers = emptyList(),
                            pointsAttendus = emptyList(),
                        ),
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
                    tauxAffinite = 0.9f,
                    domaines =
                        listOf(
                            Domaine(id = "T_ITM_1356", nom = "soin aux animaux"),
                        ),
                    interets = listOf(Interet(id = "T_ROME_731379930", nom = "je veux aider les autres")),
                    explicationAutoEvaluationMoyenne =
                        ExplicationAutoEvaluationMoyenne(
                            baccalaureat = Baccalaureat("Générale", "Générale", "Série Générale"),
                            moyenneAutoEvalue = 15f,
                            basIntervalleNotes = 14f,
                            hautIntervalleNotes = 16f,
                        ),
                    formationsSimilaires =
                        listOf(
                            Formation("fl1", "CPGE MPSI"),
                            Formation("fl7", "BUT Informatique"),
                        ),
                    explications =
                        ExplicationsSuggestion(
                            geographique =
                                listOf(
                                    ExplicationsSuggestion.ExplicationGeographique(
                                        ville = "Nantes",
                                        distanceKm = 1,
                                    ),
                                    ExplicationsSuggestion.ExplicationGeographique(
                                        ville = "Paris",
                                        distanceKm = 3,
                                    ),
                                ),
                            formationsSimilaires = listOf("fl1", "fl7"),
                            dureeEtudesPrevue = ChoixDureeEtudesPrevue.LONGUE,
                            alternance = ChoixAlternance.TRES_INTERESSE,
                            specialitesChoisies =
                                listOf(
                                    ExplicationsSuggestion.AffiniteSpecialite(nomSpecialite = "specialiteA", pourcentage = 12),
                                    ExplicationsSuggestion.AffiniteSpecialite(nomSpecialite = "specialiteB", pourcentage = 1),
                                    ExplicationsSuggestion.AffiniteSpecialite(nomSpecialite = "specialiteC", pourcentage = 89),
                                ),
                            typeBaccalaureat =
                                ExplicationsSuggestion.TypeBaccalaureat(
                                    nomBaccalaureat = "Général",
                                    pourcentage = 18,
                                ),
                            autoEvaluationMoyenne =
                                ExplicationsSuggestion.AutoEvaluationMoyenne(
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
                                    "T_ITM_1356",
                                ),
                        ),
                    explicationTypeBaccalaureat =
                        ExplicationTypeBaccalaureat(
                            baccalaureat = Baccalaureat("Générale", "Général", "Série Générale"),
                            pourcentage = 18,
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
                            "idsFormationsAssociees": [
                              "fl0012"
                            ],
                            "descriptifFormation": "Les formations CPES recrutent des lycéen.nes de très bon niveau sur sélection et dispensent des enseignements pluri-disciplinaires (scientifiques, artistiques, de sciences sociales, de littérature) permettant une poursuite d'études en master ou en grande école. Il s’agit de formations ouvertes socialement recrutant 40% de boursiers sur critères sociaux. Elles sont organisées conjointement par un établissement d’enseignement secondaire lycée et un établissement de l’enseignement supérieur, une université.",
                            "descriptifDiplome": "Les formations CPES sont des diplômes d’établissement diplômants en trois ans qui conférent le grade de licence.",
                            "descriptifConseils": "Nous vous conseillons de développer une sensibilité artistique et de rester informé des tendances actuelles en matière de design floral pour exceller dans ce domaine.",
                            "descriptifAttendus": "Il est attendu des candidats de démontrer une solide compréhension des techniques de base de la floristerie, y compris la composition florale, la reconnaissance des plantes et des fleurs, ainsi que les soins et l'entretien des végétaux.",
                            "moyenneGeneraleDesAdmis": {
                              "idBaccalaureat": "",
                              "nomBaccalaureat": "",
                              "centilles": []
                            },
                            "criteresAnalyseCandidature": {
                              "nom": "",
                              "pourcentage": 12
                            },
                            "repartitionAdmisAnneePrecedente": {
                              "total": 12,
                              "parBaccalaureat": []
                            },
                            "liens": [],
                            "villes": [
                              "Paris  5e  Arrondissement",
                              "Paris 16e  Arrondissement"
                            ],
                            "metiers": [
                              {
                                "id": "MET001",
                                "nom": "géomaticien/ne",
                                "descriptif": "À la croisée de la géographie et de l'informatique, le géomaticien ou la géomaticienne exploite les données pour modéliser le territoire",
                                "liens": []
                              },
                              {
                                "id": "MET002",
                                "nom": "documentaliste",
                                "descriptif": null,
                                "liens": []
                              }
                            ],
                            "tauxAffinite": 0.9
                          },
                          "explications": {
                            "geographique": [
                              {
                                "nomVille": "Nantes",
                                "distanceKm": 1
                              },
                              {
                                "nomVille": "Paris",
                                "distanceKm": 3
                              }
                            ],
                            "formationsSimilaires": [
                              {
                                "id": "fl1",
                                "nom": "CPGE MPSI"
                              },
                              {
                                "id": "fl7",
                                "nom": "BUT Informatique"
                              }
                            ],
                            "dureeEtudesPrevue": "longue",
                            "alternance": "tres_interesse",
                            "interetsEtDomainesChoisis": {
                              "interets": [
                                {
                                  "id": "T_ROME_731379930",
                                  "nom": "je veux aider les autres"
                                }
                              ],
                              "domaines": [
                                {
                                  "id": "T_ITM_1356",
                                  "nom": "soin aux animaux"
                                }
                              ]
                            },
                            "specialitesChoisies": [
                              {
                                "nomSpecialite": "specialiteA",
                                "pourcentage": 12
                              },
                              {
                                "nomSpecialite": "specialiteB",
                                "pourcentage": 1
                              },
                              {
                                "nomSpecialite": "specialiteC",
                                "pourcentage": 89
                              }
                            ],
                            "typeBaccalaureat": {
                              "nomBaccalaureat": "Série Générale",
                              "pourcentage": 18
                            },
                            "autoEvaluationMoyenne": {
                              "moyenne": 15.0,
                              "basIntervalleNotes": 14.0,
                              "hautIntervalleNotes": 16.0,
                              "idBaccalaureatUtilise": "Générale",
                              "nomBaccalaureatUtilise": "Série Générale"
                            }
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
                    formation =
                        FormationDetaillee(
                            id = "fl680002",
                            nom = "Cycle pluridisciplinaire d'Études Supérieures - Science",
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
                            descriptifGeneral =
                                "Les formations CPES recrutent des lycéen.nes de très bon niveau sur sélection et dispensent " +
                                    "des enseignements pluri-disciplinaires (scientifiques, artistiques, de sciences sociales, " +
                                    "de littérature) permettant une poursuite d'études en master ou en grande école. Il s’agit " +
                                    "de formations ouvertes socialement recrutant 40% de boursiers sur critères sociaux. Elles " +
                                    "sont organisées conjointement par un établissement d’enseignement secondaire lycée et un " +
                                    "établissement de l’enseignement supérieur, une université.",
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
                            liens =
                                listOf(
                                    "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/" +
                                        "cycle-pluridisciplinaire-d-etudes-superieures",
                                ),
                            pointsAttendus =
                                listOf(
                                    "Les résultats scolaires",
                                    "Les compétences, méthodes de travail et savoir-faire",
                                ),
                        ),
                    communes =
                        listOf(
                            "Paris  5e  Arrondissement",
                            "Paris 16e  Arrondissement",
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
                            "idsFormationsAssociees": [
                              "fl0012"
                            ],
                            "descriptifFormation": "Les formations CPES recrutent des lycéen.nes de très bon niveau sur sélection et dispensent des enseignements pluri-disciplinaires (scientifiques, artistiques, de sciences sociales, de littérature) permettant une poursuite d'études en master ou en grande école. Il s’agit de formations ouvertes socialement recrutant 40% de boursiers sur critères sociaux. Elles sont organisées conjointement par un établissement d’enseignement secondaire lycée et un établissement de l’enseignement supérieur, une université.",
                            "descriptifDiplome": "Les formations CPES sont des diplômes d’établissement diplômants en trois ans qui conférent le grade de licence.",
                            "descriptifConseils": "Nous vous conseillons de développer une sensibilité artistique et de rester informé des tendances actuelles en matière de design floral pour exceller dans ce domaine.",
                            "descriptifAttendus": "Il est attendu des candidats de démontrer une solide compréhension des techniques de base de la floristerie, y compris la composition florale, la reconnaissance des plantes et des fleurs, ainsi que les soins et l'entretien des végétaux.",
                            "moyenneGeneraleDesAdmis": {
                              "idBaccalaureat": "",
                              "nomBaccalaureat": "",
                              "centilles": []
                            },
                            "criteresAnalyseCandidature": {
                              "nom": "",
                              "pourcentage": 12
                            },
                            "repartitionAdmisAnneePrecedente": {
                              "total": 12,
                              "parBaccalaureat": []
                            },
                            "liens": [],
                            "villes": [
                              "Paris  5e  Arrondissement",
                              "Paris 16e  Arrondissement"
                            ],
                            "metiers": [
                              {
                                "id": "MET001",
                                "nom": "géomaticien/ne",
                                "descriptif": "À la croisée de la géographie et de l'informatique, le géomaticien ou la géomaticienne exploite les données pour modéliser le territoire",
                                "liens": []
                              },
                              {
                                "id": "MET002",
                                "nom": "documentaliste",
                                "descriptif": null,
                                "liens": []
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
