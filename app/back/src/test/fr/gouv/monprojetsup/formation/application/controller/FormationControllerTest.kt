package fr.gouv.monprojetsup.formation.application.controller

import fr.gouv.monprojetsup.commun.ConnecteAvecUnEleve
import fr.gouv.monprojetsup.commun.ConnecteAvecUnEnseignant
import fr.gouv.monprojetsup.commun.ConnecteSansId
import fr.gouv.monprojetsup.commun.application.controller.ControllerTest
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetIllegalStateErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.commun.lien.domain.entity.Lien
import fr.gouv.monprojetsup.formation.domain.entity.AffiniteSpecialite
import fr.gouv.monprojetsup.formation.domain.entity.CritereAnalyseCandidature
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationGeographique
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionDetaillees
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation.FicheFormationPourProfil.ExplicationAutoEvaluationMoyenne
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation.FicheFormationPourProfil.ExplicationTypeBaccalaureat
import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis.MoyenneGeneraleDesAdmis
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis.MoyenneGeneraleDesAdmis.Centile
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis.RepartitionAdmis
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis.RepartitionAdmis.TotalAdmisPourUnBaccalaureat
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil
import fr.gouv.monprojetsup.formation.usecase.RechercherFormationsService
import fr.gouv.monprojetsup.formation.usecase.RecupererFormationService
import fr.gouv.monprojetsup.formation.usecase.RecupererFormationsService
import fr.gouv.monprojetsup.formation.usecase.SuggestionsFormationsService
import fr.gouv.monprojetsup.metier.domain.entity.Metier
import fr.gouv.monprojetsup.referentiel.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.referentiel.domain.entity.Domaine
import fr.gouv.monprojetsup.referentiel.domain.entity.InteretSousCategorie
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.`when`
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.net.ConnectException

@WebMvcTest(controllers = [FormationController::class])
class FormationControllerTest(
    @Autowired val mvc: MockMvc,
) : ControllerTest() {
    @MockBean
    lateinit var suggestionsFormationsService: SuggestionsFormationsService

    @MockBean
    lateinit var recupererFormationService: RecupererFormationService

    @MockBean
    lateinit var recupererFormationsService: RecupererFormationsService

    @MockBean
    lateinit var rechercherFormation: RechercherFormationsService

    private val explications =
        ExplicationsSuggestionDetaillees(
            geographique =
                listOf(
                    ExplicationGeographique(
                        ville = "Nantes",
                        distanceKm = 1,
                    ),
                    ExplicationGeographique(
                        ville = "Paris",
                        distanceKm = 3,
                    ),
                ),
            dureeEtudesPrevue = ChoixDureeEtudesPrevue.LONGUE,
            alternance = ChoixAlternance.TRES_INTERESSE,
            specialitesChoisies =
                listOf(
                    AffiniteSpecialite(nomSpecialite = "specialiteA", pourcentage = 12),
                    AffiniteSpecialite(nomSpecialite = "specialiteB", pourcentage = 1),
                    AffiniteSpecialite(nomSpecialite = "specialiteC", pourcentage = 89),
                ),
            domaines =
                listOf(
                    Domaine(id = "T_ITM_1356", nom = "soin aux animaux", emoji = "\uD83D\uDC2E"),
                ),
            interets = listOf(InteretSousCategorie(id = "aider_autres", nom = "Aider les autres", emoji = "\uD83E\uDEC2")),
            explicationAutoEvaluationMoyenne =
                ExplicationAutoEvaluationMoyenne(
                    baccalaureatUtilise = Baccalaureat(id = "Générale", idExterne = "Générale", nom = "Série Générale"),
                    moyenneAutoEvalue = 15f,
                    basIntervalleNotes = 14f,
                    hautIntervalleNotes = 16f,
                ),
            formationsSimilaires =
                listOf(
                    FormationCourte(id = "fl1", nom = "CPGE MPSI"),
                    FormationCourte(id = "fl7", nom = "BUT Informatique"),
                ),
            explicationTypeBaccalaureat =
                ExplicationTypeBaccalaureat(
                    baccalaureat = Baccalaureat(id = "Générale", idExterne = "Général", nom = "Série Générale"),
                    pourcentage = 18,
                ),
        )
    val ficheFormation =
        FicheFormation.FicheFormationPourProfil(
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
                    Lien(
                        nom = "Voir sur l'ONISEP",
                        url =
                            "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/" +
                                "cycle-pluridisciplinaire-d-etudes-superieures",
                    ),
                ),
            communesTrieesParAffinites =
                listOf(
                    "Paris  5e  Arrondissement",
                    "Paris 16e  Arrondissement",
                ),
            metiersTriesParAffinites =
                listOf(
                    Metier(
                        id = "MET001",
                        nom = "géomaticien/ne",
                        descriptif =
                            "À la croisée de la géographie et de l'informatique, le géomaticien ou la géomaticienne " +
                                "exploite les données pour modéliser le territoire",
                        liens =
                            listOf(
                                Lien(
                                    nom = "Voir sur l'ONISEP",
                                    url = "https://www.onisep.fr/ressources/univers-metier/metiers/geomaticien-geomaticienne",
                                ),
                            ),
                    ),
                    Metier(
                        id = "MET002",
                        nom = "documentaliste",
                        descriptif = null,
                        liens = emptyList(),
                    ),
                ),
            tauxAffinite = 90,
            explications = explications,
            criteresAnalyseCandidature =
                listOf(
                    CritereAnalyseCandidature(nom = "Compétences académiques", pourcentage = 10),
                    CritereAnalyseCandidature(
                        nom = "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                        pourcentage = 0,
                    ),
                    CritereAnalyseCandidature(nom = "Résultats académiques", pourcentage = 18),
                    CritereAnalyseCandidature(nom = "Savoir-être", pourcentage = 42),
                    CritereAnalyseCandidature(nom = "Motivation, connaissance", pourcentage = 30),
                ),
            statistiquesDesAdmis =
                StatistiquesDesAdmis(
                    moyenneGeneraleDesAdmis =
                        MoyenneGeneraleDesAdmis(
                            baccalaureat = Baccalaureat("Générale", "Général", "Série Générale"),
                            centiles =
                                listOf(
                                    Centile(centile = 5, note = 13f),
                                    Centile(centile = 25, note = 14.5f),
                                    Centile(centile = 75, note = 17f),
                                    Centile(centile = 95, note = 18f),
                                ),
                        ),
                    repartitionAdmis =
                        RepartitionAdmis(
                            total = 6915,
                            parBaccalaureat =
                                listOf(
                                    TotalAdmisPourUnBaccalaureat(
                                        baccalaureat = Baccalaureat(id = "Générale", idExterne = "Général", nom = "Série Générale"),
                                        nombreAdmis = 6677,
                                    ),
                                    TotalAdmisPourUnBaccalaureat(
                                        baccalaureat = Baccalaureat(id = "STMG", idExterne = "STMG", nom = "Série STMG"),
                                        nombreAdmis = 15,
                                    ),
                                    TotalAdmisPourUnBaccalaureat(
                                        baccalaureat = Baccalaureat(id = "STI2D", idExterne = "STI2D", nom = "Série STI2D"),
                                        nombreAdmis = 223,
                                    ),
                                ),
                        ),
                ),
        )

    @Nested
    inner class `Quand on appelle la route de suggestions de formations` {
        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le service réussi, doit retourner 200 avec une liste des fiches formations suggérées`() {
            // Given
            `when`(
                suggestionsFormationsService.suggererFormations(profilEleve = unProfil, deLIndex = 0, aLIndex = 30),
            ).thenReturn(
                listOf(
                    ficheFormation,
                    ficheFormation.copy(
                        id = "fl2",
                        nom = "2eme formation",
                        descriptifGeneral = null,
                        descriptifAttendus = null,
                        descriptifDiplome = null,
                        descriptifConseils = null,
                        formationsAssociees = listOf("fl3"),
                        liens = emptyList(),
                        criteresAnalyseCandidature = emptyList(),
                        statistiquesDesAdmis = null,
                        tauxAffinite = 17,
                        metiersTriesParAffinites = emptyList(),
                        communesTrieesParAffinites = emptyList(),
                        explications = null,
                    ),
                ),
            )

            // When & Then
            mvc.perform(
                get("/api/v1/formations/suggestions"),
            ).andDo(print()).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "formations": [
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
                                  "baccalaureat": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  },
                                  "centiles": [
                                    {
                                      "centile": 5,
                                      "note": 13.0
                                    },
                                    {
                                      "centile": 25,
                                      "note": 14.5
                                    },
                                    {
                                      "centile": 75,
                                      "note": 17.0
                                    },
                                    {
                                      "centile": 95,
                                      "note": 18.0
                                    }
                                  ]
                                },
                                "criteresAnalyseCandidature": [
                                  {
                                    "nom": "Compétences académiques",
                                    "pourcentage": 10
                                  },
                                  {
                                    "nom": "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                                    "pourcentage": 0
                                  },
                                  {
                                    "nom": "Résultats académiques",
                                    "pourcentage": 18
                                  },
                                  {
                                    "nom": "Savoir-être",
                                    "pourcentage": 42
                                  },
                                  {
                                    "nom": "Motivation, connaissance",
                                    "pourcentage": 30
                                  }
                                ],
                                "repartitionAdmisAnneePrecedente": {
                                  "total": 6915,
                                  "parBaccalaureat": [
                                    {
                                      "baccalaureat": {
                                        "id": "Générale",
                                        "nom": "Série Générale"
                                      },
                                      "nombreAdmis": 6677
                                    },
                                    {
                                      "baccalaureat": {
                                        "id": "STMG",
                                        "nom": "Série STMG"
                                      },
                                      "nombreAdmis": 15
                                    },
                                    {
                                      "baccalaureat": {
                                        "id": "STI2D",
                                        "nom": "Série STI2D"
                                      },
                                      "nombreAdmis": 223
                                    }
                                  ]
                                },
                                "liens": [
                                  {
                                    "nom": "Voir sur l'ONISEP",
                                    "url": "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/cycle-pluridisciplinaire-d-etudes-superieures"
                                  }
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
                                      {
                                        "nom": "Voir sur l'ONISEP",
                                        "url": "https://www.onisep.fr/ressources/univers-metier/metiers/geomaticien-geomaticienne"
                                      }
                                    ]
                                  },
                                  {
                                    "id": "MET002",
                                    "nom": "documentaliste",
                                    "descriptif": null,
                                    "liens": []
                                  }
                                ],
                                "tauxAffinite": 90
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
                                      "id": "aider_autres",
                                      "nom": "Aider les autres"
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
                                  "baccalaureat": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  },
                                  "pourcentage": 18
                                },
                                "autoEvaluationMoyenne": {
                                  "moyenne": 15.0,
                                  "basIntervalleNotes": 14.0,
                                  "hautIntervalleNotes": 16.0,
                                  "baccalaureatUtilise": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  }
                                }
                              }
                            },
                            {
                              "formation": {
                                "id": "fl2",
                                "nom": "2eme formation",
                                "idsFormationsAssociees": [
                                  "fl3"
                                ],
                                "descriptifFormation": null,
                                "descriptifDiplome": null,
                                "descriptifConseils": null,
                                "descriptifAttendus": null,
                                "moyenneGeneraleDesAdmis": null,
                                "criteresAnalyseCandidature": [],
                                "repartitionAdmisAnneePrecedente": null,
                                "liens": [],
                                "villes": [],
                                "metiers": [],
                                "tauxAffinite": 17
                              },
                              "explications": null
                            }
                          ]
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEnseignant(idEnseignant = "egff627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si connecté en tant qu'enseignant, doit retourner 403`() {
            // When & Then
            mvc.perform(
                get("/api/v1/formations/suggestions"),
            ).andDo(print()).andExpect(status().isForbidden).andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "type": "about:blank",
                          "title": "UTILISATEUR_PAS_ELEVE",
                          "status": 403,
                          "detail": "L'utilisateur connecté n'est pas un élève",
                          "instance": "/api/v1/formations/suggestions"
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteSansId
        @Test
        fun `si connecté sans profil, doit retourner 403`() {
            // When & Then
            mvc.perform(
                get("/api/v1/formations/suggestions"),
            ).andDo(print()).andExpect(status().isForbidden)
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le service échoue avec une erreur interne, alors doit retourner 500`() {
            // Given
            val uneException =
                MonProjetSupInternalErrorException(
                    code = "ERREUR_API_SUGGESTIONS_CONNEXION",
                    msg = "Erreur lors de la connexion à l'API de suggestions",
                    origine = ConnectException("Connection refused"),
                )
            `when`(suggestionsFormationsService.suggererFormations(unProfil, 0, 30)).thenThrow(uneException)

            // When & Then
            mvc.perform(
                get("/api/v1/formations/suggestions"),
            ).andDo(print()).andExpect(status().isInternalServerError)
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
        }
    }

    @Nested
    inner class `Quand on appelle la route de récupération d'une formation` {
        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le service réussi pour un appel avec un profil, doit retourner 200 avec le détail de la formation`() {
            // Given
            `when`(recupererFormationService.recupererFormation(unProfil, "fl680002")).thenReturn(ficheFormation)

            // When & Then
            mvc.perform(
                get("/api/v1/formations/fl680002"),
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
                              "baccalaureat": {
                                "id": "Générale",
                                "nom": "Série Générale"
                              },
                              "centiles": [
                                {
                                  "centile": 5,
                                  "note": 13.0
                                },
                                {
                                  "centile": 25,
                                  "note": 14.5
                                },
                                {
                                  "centile": 75,
                                  "note": 17.0
                                },
                                {
                                  "centile": 95,
                                  "note": 18.0
                                }
                              ]
                            },
                            "criteresAnalyseCandidature": [
                              {
                                "nom": "Compétences académiques",
                                "pourcentage": 10
                              },
                              {
                                "nom": "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                                "pourcentage": 0
                              },
                              {
                                "nom": "Résultats académiques",
                                "pourcentage": 18
                              },
                              {
                                "nom": "Savoir-être",
                                "pourcentage": 42
                              },
                              {
                                "nom": "Motivation, connaissance",
                                "pourcentage": 30
                              }
                            ],
                            "repartitionAdmisAnneePrecedente": {
                              "total": 6915,
                              "parBaccalaureat": [
                                {
                                  "baccalaureat": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  },
                                  "nombreAdmis": 6677
                                },
                                {
                                  "baccalaureat": {
                                    "id": "STMG",
                                    "nom": "Série STMG"
                                  },
                                  "nombreAdmis": 15
                                },
                                {
                                  "baccalaureat": {
                                    "id": "STI2D",
                                    "nom": "Série STI2D"
                                  },
                                  "nombreAdmis": 223
                                }
                              ]
                            },
                            "liens": [
                              {
                                "nom": "Voir sur l'ONISEP",
                                "url": "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/cycle-pluridisciplinaire-d-etudes-superieures"
                              }
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
                                  {
                                    "nom": "Voir sur l'ONISEP",
                                    "url": "https://www.onisep.fr/ressources/univers-metier/metiers/geomaticien-geomaticienne"
                                  }
                                ]
                              },
                              {
                                "id": "MET002",
                                "nom": "documentaliste",
                                "descriptif": null,
                                "liens": []
                              }
                            ],
                            "tauxAffinite": 90
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
                                  "id": "aider_autres",
                                  "nom": "Aider les autres"
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
                              "baccalaureat": {
                                "id": "Générale",
                                "nom": "Série Générale"
                              },
                              "pourcentage": 18
                            },
                            "autoEvaluationMoyenne": {
                              "moyenne": 15.0,
                              "basIntervalleNotes": 14.0,
                              "hautIntervalleNotes": 16.0,
                              "baccalaureatUtilise": {
                                "id": "Générale",
                                "nom": "Série Générale"
                              }
                            }
                          }
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEnseignant(idEnseignant = "bfef627c-36dd-4df5-897b-159443a6d49d")
        @Test
        fun `si le service réussi pour un enseignant, doit retourner 200 avec le détail de la formation`() {
            // Given
            val ficheFormation =
                FicheFormation.FicheFormationSansProfil(
                    id = "fl680002",
                    nom = "Cycle pluridisciplinaire d'Études Supérieures - Science",
                    metiers =
                        listOf(
                            Metier(
                                id = "MET001",
                                nom = "géomaticien/ne",
                                descriptif =
                                    "À la croisée de la géographie et de l'informatique, le géomaticien ou la géomaticienne " +
                                        "exploite les données pour modéliser le territoire",
                                liens =
                                    listOf(
                                        Lien(
                                            nom = "Voir sur l'ONISEP",
                                            url = "https://www.onisep.fr/ressources/univers-metier/metiers/geomaticien-geomaticienne",
                                        ),
                                    ),
                            ),
                            Metier(
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
                            Lien(
                                nom = "Voir sur l'ONISEP",
                                url =
                                    "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/" +
                                        "cycle-pluridisciplinaire-d-etudes-superieures",
                            ),
                        ),
                    communes =
                        listOf(
                            "Paris  5e  Arrondissement",
                            "Paris 16e  Arrondissement",
                        ),
                    criteresAnalyseCandidature =
                        listOf(
                            CritereAnalyseCandidature(nom = "Compétences académiques", pourcentage = 10),
                            CritereAnalyseCandidature(
                                nom = "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                                pourcentage = 0,
                            ),
                            CritereAnalyseCandidature(nom = "Résultats académiques", pourcentage = 18),
                            CritereAnalyseCandidature(nom = "Savoir-être", pourcentage = 42),
                            CritereAnalyseCandidature(nom = "Motivation, connaissance", pourcentage = 30),
                        ),
                    statistiquesDesAdmis =
                        StatistiquesDesAdmis(
                            repartitionAdmis =
                                RepartitionAdmis(
                                    total = 12,
                                    parBaccalaureat = listOf(),
                                ),
                            moyenneGeneraleDesAdmis = null,
                        ),
                )
            `when`(recupererFormationService.recupererFormation(profilEleve = null, idFormation = "fl680002")).thenReturn(ficheFormation)

            // When & Then
            mvc.perform(
                get("/api/v1/formations/fl680002"),
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
                            "moyenneGeneraleDesAdmis": null,
                            "criteresAnalyseCandidature": [
                              {
                                "nom": "Compétences académiques",
                                "pourcentage": 10
                              },
                              {
                                "nom": "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                                "pourcentage": 0
                              },
                              {
                                "nom": "Résultats académiques",
                                "pourcentage": 18
                              },
                              {
                                "nom": "Savoir-être",
                                "pourcentage": 42
                              },
                              {
                                "nom": "Motivation, connaissance",
                                "pourcentage": 30
                              }
                            ],
                            "repartitionAdmisAnneePrecedente": {
                              "total": 12,
                              "parBaccalaureat": []
                            },
                            "liens": [
                              {
                                "nom": "Voir sur l'ONISEP",
                                "url": "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/cycle-pluridisciplinaire-d-etudes-superieures"
                              }
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
                                  {
                                    "nom": "Voir sur l'ONISEP",
                                    "url": "https://www.onisep.fr/ressources/univers-metier/metiers/geomaticien-geomaticienne"
                                  }
                                ]
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

        @ConnecteSansId
        @Test
        fun `si connecté sans profil, doit retourner 403`() {
            // When & Then
            mvc.perform(
                get("/api/v1/formations/fl68000"),
            ).andDo(print()).andExpect(status().isForbidden)
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le service échoue avec une erreur interne, alors doit retourner 500`() {
            // Given
            val uneException =
                MonProjetIllegalStateErrorException(
                    code = "RECHERCHE_FORMATION",
                    msg = "La formation fl00010 existe plusieurs fois entre id et dans les formations équivalentes",
                )
            `when`(recupererFormationService.recupererFormation(unProfil, "fl00010")).thenThrow(uneException)

            // When & Then
            mvc.perform(
                get("/api/v1/formations/fl00010"),
            ).andDo(print()).andExpect(status().isInternalServerError)
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le service échoue avec une erreur not found, alors doit retourner 404`() {
            // Given
            val uneException =
                MonProjetSupNotFoundException(
                    code = "RECHERCHE_FORMATION",
                    msg = "La formation inconnu n'existe pas",
                )
            `when`(recupererFormationService.recupererFormation(unProfil, "inconnu")).thenThrow(uneException)

            // When & Then
            mvc.perform(
                get("/api/v1/formations/inconnu"),
            ).andDo(print()).andExpect(status().isNotFound)
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
        }
    }

    @Nested
    inner class `Quand on appelle la route de recherche succincte de formations` {
        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le service réussi pour un appel avec un profil, doit retourner 200 avec la listes de formations`() {
            // Given
            val rechercheL1 =
                listOf(
                    FormationCourte(id = "fl1", nom = "L1 - Psychologie"),
                    FormationCourte(id = "fl7", nom = "L1 - Philosophie"),
                )
            `when`(
                rechercherFormation.rechercheLesFormationsCorrespondantes(recherche = "L1", nombreMaximaleDeFormation = 30),
            ).thenReturn(rechercheL1)

            // When & Then
            mvc.perform(
                get("/api/v1/formations/recherche/succincte?recherche=L1"),
            ).andDo(print()).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "formations": [
                            {
                              "id": "fl1",
                              "nom": "L1 - Psychologie"
                            },
                            {
                              "id": "fl7",
                              "nom": "L1 - Philosophie"
                            }
                          ]
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEnseignant(idEnseignant = "bfef627c-36dd-4df5-897b-159443a6d49d")
        @Test
        fun `si le service réussi pour un enseignant, doit retourner 200 avec le détail de la formation`() {
            // Given
            val rechercheDe50Caracteres = "Lorem ipsum dolor sit amet, consectetur porta ante"
            val rechercheLongue =
                listOf(
                    FormationCourte(id = "fl1", nom = "L1 - Psychologie"),
                    FormationCourte(id = "fl7", nom = "L1 - Philosophie"),
                    FormationCourte(id = "fl3", nom = "CAP Pâtisserie"),
                    FormationCourte(id = "fl1000", nom = "BPJEPS"),
                    FormationCourte(id = "fl17", nom = "L1 - Mathématique"),
                    FormationCourte(id = "fl20", nom = "CAP Boulangerie"),
                    FormationCourte(id = "fl10", nom = "DUT Informatique"),
                    FormationCourte(id = "fl18", nom = "L1 - Littérature"),
                )
            `when`(
                rechercherFormation.rechercheLesFormationsCorrespondantes(
                    recherche = rechercheDe50Caracteres,
                    nombreMaximaleDeFormation = 30,
                ),
            ).thenReturn(rechercheLongue)

            // When & Then
            mvc.perform(
                get("/api/v1/formations/recherche/succincte?recherche=$rechercheDe50Caracteres"),
            ).andDo(print()).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "formations": [
                            {
                              "id": "fl1",
                              "nom": "L1 - Psychologie"
                            },
                            {
                              "id": "fl7",
                              "nom": "L1 - Philosophie"
                            },
                            {
                              "id": "fl3",
                              "nom": "CAP Pâtisserie"
                            },
                            {
                              "id": "fl1000",
                              "nom": "BPJEPS"
                            },
                            {
                              "id": "fl17",
                              "nom": "L1 - Mathématique"
                            },
                            {
                              "id": "fl20",
                              "nom": "CAP Boulangerie"
                            },
                            {
                              "id": "fl10",
                              "nom": "DUT Informatique"
                            },
                            {
                              "id": "fl18",
                              "nom": "L1 - Littérature"
                            }
                          ]
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le mot recherché fait strictement moins de 2 caractère, doit retourner 400`() {
            // When & Then
            mvc.perform(
                get("/api/v1/formations/recherche/succincte?recherche=L"),
            ).andDo(print()).andExpect(status().isBadRequest).andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "type": "about:blank",
                          "title": "REQUETE_TROP_COURTE",
                          "status": 400,
                          "detail": "La taille de la requête est trop courte. Elle doit faire au moins 2 caractères",
                          "instance": "/api/v1/formations/recherche/succincte"
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le mot recherché fait strictement plus de 50 caractère, doit retourner 400`() {
            // When & Then
            val rechercheDe51Caracteres = "Lorem ipsum dolor sit amet, consectetur sodales sed"
            mvc.perform(
                get("/api/v1/formations/recherche/succincte?recherche=$rechercheDe51Caracteres"),
            ).andDo(print()).andExpect(status().isBadRequest).andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "type": "about:blank",
                          "title": "REQUETE_TROP_LONGUE",
                          "status": 400,
                          "detail": "La taille de la requête dépasse la taille maximale de 50 caractères",
                          "instance": "/api/v1/formations/recherche/succincte"
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteSansId
        @Test
        fun `si connecté sans profil, doit retourner 403`() {
            // When & Then
            mvc.perform(
                get("/api/v1/formations/recherche/succincte?recherche=test"),
            ).andDo(print()).andExpect(status().isForbidden)
        }
    }

    @Nested
    inner class `Quand on appelle la route de recherche détaillée de formations` {
        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le service réussi pour un appel avec un profil, doit retourner 200 avec la listes de formations`() {
            // Given
            val rechercheL1 =
                listOf(
                    FormationCourte(id = "fl1", nom = "L1 - Psychologie"),
                    FormationCourte(id = "fl7", nom = "L1 - Philosophie"),
                )
            `when`(
                rechercherFormation.rechercheLesFormationsCorrespondantes(recherche = "L1", nombreMaximaleDeFormation = 30),
            ).thenReturn(rechercheL1)
            val toutesLesSuggestions = mock(SuggestionsPourUnProfil::class.java)
            `when`(suggestionsFormationsService.recupererToutesLesSuggestionsPourUnProfil(unProfil)).thenReturn(toutesLesSuggestions)
            val fichesFormations =
                listOf(
                    ficheFormation.copy(id = "fl1"),
                    ficheFormation.copy(
                        id = "fl7",
                        nom = "2eme formation",
                        descriptifGeneral = null,
                        descriptifAttendus = null,
                        descriptifDiplome = null,
                        descriptifConseils = null,
                        formationsAssociees = listOf("fl3"),
                        liens = emptyList(),
                        criteresAnalyseCandidature = emptyList(),
                        statistiquesDesAdmis = null,
                        tauxAffinite = 17,
                        metiersTriesParAffinites = emptyList(),
                        communesTrieesParAffinites = emptyList(),
                        explications = null,
                    ),
                )
            `when`(
                recupererFormationsService.recupererFichesFormationPourProfil(unProfil, toutesLesSuggestions, listOf("fl1", "fl7")),
            ).thenReturn(fichesFormations)

            // When & Then
            mvc.perform(
                get("/api/v1/formations/recherche/detaillee?recherche=L1"),
            ).andDo(print()).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "formations": [
                            {
                              "formation": {
                                "id": "fl1",
                                "nom": "Cycle pluridisciplinaire d'Études Supérieures - Science",
                                "idsFormationsAssociees": [
                                  "fl0012"
                                ],
                                "descriptifFormation": "Les formations CPES recrutent des lycéen.nes de très bon niveau sur sélection et dispensent des enseignements pluri-disciplinaires (scientifiques, artistiques, de sciences sociales, de littérature) permettant une poursuite d'études en master ou en grande école. Il s’agit de formations ouvertes socialement recrutant 40% de boursiers sur critères sociaux. Elles sont organisées conjointement par un établissement d’enseignement secondaire lycée et un établissement de l’enseignement supérieur, une université.",
                                "descriptifDiplome": "Les formations CPES sont des diplômes d’établissement diplômants en trois ans qui conférent le grade de licence.",
                                "descriptifConseils": "Nous vous conseillons de développer une sensibilité artistique et de rester informé des tendances actuelles en matière de design floral pour exceller dans ce domaine.",
                                "descriptifAttendus": "Il est attendu des candidats de démontrer une solide compréhension des techniques de base de la floristerie, y compris la composition florale, la reconnaissance des plantes et des fleurs, ainsi que les soins et l'entretien des végétaux.",
                                "moyenneGeneraleDesAdmis": {
                                  "baccalaureat": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  },
                                  "centiles": [
                                    {
                                      "centile": 5,
                                      "note": 13.0
                                    },
                                    {
                                      "centile": 25,
                                      "note": 14.5
                                    },
                                    {
                                      "centile": 75,
                                      "note": 17.0
                                    },
                                    {
                                      "centile": 95,
                                      "note": 18.0
                                    }
                                  ]
                                },
                                "criteresAnalyseCandidature": [
                                  {
                                    "nom": "Compétences académiques",
                                    "pourcentage": 10
                                  },
                                  {
                                    "nom": "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                                    "pourcentage": 0
                                  },
                                  {
                                    "nom": "Résultats académiques",
                                    "pourcentage": 18
                                  },
                                  {
                                    "nom": "Savoir-être",
                                    "pourcentage": 42
                                  },
                                  {
                                    "nom": "Motivation, connaissance",
                                    "pourcentage": 30
                                  }
                                ],
                                "repartitionAdmisAnneePrecedente": {
                                  "total": 6915,
                                  "parBaccalaureat": [
                                    {
                                      "baccalaureat": {
                                        "id": "Générale",
                                        "nom": "Série Générale"
                                      },
                                      "nombreAdmis": 6677
                                    },
                                    {
                                      "baccalaureat": {
                                        "id": "STMG",
                                        "nom": "Série STMG"
                                      },
                                      "nombreAdmis": 15
                                    },
                                    {
                                      "baccalaureat": {
                                        "id": "STI2D",
                                        "nom": "Série STI2D"
                                      },
                                      "nombreAdmis": 223
                                    }
                                  ]
                                },
                                "liens": [
                                  {
                                    "nom": "Voir sur l'ONISEP",
                                    "url": "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/cycle-pluridisciplinaire-d-etudes-superieures"
                                  }
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
                                      {
                                        "nom": "Voir sur l'ONISEP",
                                        "url": "https://www.onisep.fr/ressources/univers-metier/metiers/geomaticien-geomaticienne"
                                      }
                                    ]
                                  },
                                  {
                                    "id": "MET002",
                                    "nom": "documentaliste",
                                    "descriptif": null,
                                    "liens": []
                                  }
                                ],
                                "tauxAffinite": 90
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
                                      "id": "aider_autres",
                                      "nom": "Aider les autres"
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
                                  "baccalaureat": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  },
                                  "pourcentage": 18
                                },
                                "autoEvaluationMoyenne": {
                                  "moyenne": 15.0,
                                  "basIntervalleNotes": 14.0,
                                  "hautIntervalleNotes": 16.0,
                                  "baccalaureatUtilise": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  }
                                }
                              }
                            },
                            {
                              "formation": {
                                "id": "fl7",
                                "nom": "2eme formation",
                                "idsFormationsAssociees": [
                                  "fl3"
                                ],
                                "descriptifFormation": null,
                                "descriptifDiplome": null,
                                "descriptifConseils": null,
                                "descriptifAttendus": null,
                                "moyenneGeneraleDesAdmis": null,
                                "criteresAnalyseCandidature": [],
                                "repartitionAdmisAnneePrecedente": null,
                                "liens": [],
                                "villes": [],
                                "metiers": [],
                                "tauxAffinite": 17
                              },
                              "explications": null
                            }
                          ]
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEnseignant(idEnseignant = "bfef627c-36dd-4df5-897b-159443a6d49d")
        @Test
        fun `pour un enseignant, doit retourner 401 avec le détail de la formation`() {
            // Given
            val recherche = "Lorem ipsum"

            // When & Then
            mvc.perform(
                get("/api/v1/formations/recherche/detaillee?recherche=$recherche"),
            ).andDo(print()).andExpect(status().isForbidden).andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "type": "about:blank",
                          "title": "UTILISATEUR_PAS_ELEVE",
                          "status": 403,
                          "detail": "L'utilisateur connecté n'est pas un élève",
                          "instance": "/api/v1/formations/recherche/detaillee"
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le mot recherché fait strictement moins de 2 caractère, doit retourner 400`() {
            // When & Then
            mvc.perform(
                get("/api/v1/formations/recherche/detaillee?recherche=L"),
            ).andDo(print()).andExpect(status().isBadRequest).andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "type": "about:blank",
                          "title": "REQUETE_TROP_COURTE",
                          "status": 400,
                          "detail": "La taille de la requête est trop courte. Elle doit faire au moins 2 caractères",
                          "instance": "/api/v1/formations/recherche/detaillee"
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le mot recherché fait strictement plus de 50 caractère, doit retourner 400`() {
            // When & Then
            val rechercheDe51Caracteres = "Lorem ipsum dolor sit amet, consectetur sodales sed"
            mvc.perform(
                get("/api/v1/formations/recherche/detaillee?recherche=$rechercheDe51Caracteres"),
            ).andDo(print()).andExpect(status().isBadRequest).andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "type": "about:blank",
                          "title": "REQUETE_TROP_LONGUE",
                          "status": 400,
                          "detail": "La taille de la requête dépasse la taille maximale de 50 caractères",
                          "instance": "/api/v1/formations/recherche/detaillee"
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteSansId
        @Test
        fun `si connecté sans profil, doit retourner 403`() {
            // When & Then
            mvc.perform(
                get("/api/v1/formations/recherche/detaillee?recherche=test"),
            ).andDo(print()).andExpect(status().isForbidden)
        }
    }

    @Nested
    inner class `Quand on appelle la route de récupération de formations` {
        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le service réussi pour un appel avec un profil, doit retourner 200 avec le détail de la formation`() {
            // Given
            val toutesLesSuggestions = mock(SuggestionsPourUnProfil::class.java)
            `when`(suggestionsFormationsService.recupererToutesLesSuggestionsPourUnProfil(unProfil)).thenReturn(toutesLesSuggestions)
            val fichesFormations =
                listOf(
                    ficheFormation.copy(id = "fl1"),
                    ficheFormation.copy(
                        id = "fl2",
                        nom = "2eme formation",
                        descriptifGeneral = null,
                        descriptifAttendus = null,
                        descriptifDiplome = null,
                        descriptifConseils = null,
                        formationsAssociees = listOf("fl3"),
                        liens = emptyList(),
                        criteresAnalyseCandidature = emptyList(),
                        statistiquesDesAdmis = null,
                        tauxAffinite = 17,
                        metiersTriesParAffinites = emptyList(),
                        communesTrieesParAffinites = emptyList(),
                        explications = null,
                    ),
                )
            `when`(
                recupererFormationsService.recupererFichesFormationPourProfil(unProfil, toutesLesSuggestions, listOf("fl1", "fl2")),
            ).thenReturn(fichesFormations)

            // When & Then
            mvc.perform(
                get("/api/v1/formations?ids=fl1&ids=fl2"),
            ).andDo(print()).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "formations": [
                            {
                              "formation": {
                                "id": "fl1",
                                "nom": "Cycle pluridisciplinaire d'Études Supérieures - Science",
                                "idsFormationsAssociees": [
                                  "fl0012"
                                ],
                                "descriptifFormation": "Les formations CPES recrutent des lycéen.nes de très bon niveau sur sélection et dispensent des enseignements pluri-disciplinaires (scientifiques, artistiques, de sciences sociales, de littérature) permettant une poursuite d'études en master ou en grande école. Il s’agit de formations ouvertes socialement recrutant 40% de boursiers sur critères sociaux. Elles sont organisées conjointement par un établissement d’enseignement secondaire lycée et un établissement de l’enseignement supérieur, une université.",
                                "descriptifDiplome": "Les formations CPES sont des diplômes d’établissement diplômants en trois ans qui conférent le grade de licence.",
                                "descriptifConseils": "Nous vous conseillons de développer une sensibilité artistique et de rester informé des tendances actuelles en matière de design floral pour exceller dans ce domaine.",
                                "descriptifAttendus": "Il est attendu des candidats de démontrer une solide compréhension des techniques de base de la floristerie, y compris la composition florale, la reconnaissance des plantes et des fleurs, ainsi que les soins et l'entretien des végétaux.",
                                "moyenneGeneraleDesAdmis": {
                                  "baccalaureat": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  },
                                  "centiles": [
                                    {
                                      "centile": 5,
                                      "note": 13.0
                                    },
                                    {
                                      "centile": 25,
                                      "note": 14.5
                                    },
                                    {
                                      "centile": 75,
                                      "note": 17.0
                                    },
                                    {
                                      "centile": 95,
                                      "note": 18.0
                                    }
                                  ]
                                },
                                "criteresAnalyseCandidature": [
                                  {
                                    "nom": "Compétences académiques",
                                    "pourcentage": 10
                                  },
                                  {
                                    "nom": "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                                    "pourcentage": 0
                                  },
                                  {
                                    "nom": "Résultats académiques",
                                    "pourcentage": 18
                                  },
                                  {
                                    "nom": "Savoir-être",
                                    "pourcentage": 42
                                  },
                                  {
                                    "nom": "Motivation, connaissance",
                                    "pourcentage": 30
                                  }
                                ],
                                "repartitionAdmisAnneePrecedente": {
                                  "total": 6915,
                                  "parBaccalaureat": [
                                    {
                                      "baccalaureat": {
                                        "id": "Générale",
                                        "nom": "Série Générale"
                                      },
                                      "nombreAdmis": 6677
                                    },
                                    {
                                      "baccalaureat": {
                                        "id": "STMG",
                                        "nom": "Série STMG"
                                      },
                                      "nombreAdmis": 15
                                    },
                                    {
                                      "baccalaureat": {
                                        "id": "STI2D",
                                        "nom": "Série STI2D"
                                      },
                                      "nombreAdmis": 223
                                    }
                                  ]
                                },
                                "liens": [
                                  {
                                    "nom": "Voir sur l'ONISEP",
                                    "url": "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/cycle-pluridisciplinaire-d-etudes-superieures"
                                  }
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
                                      {
                                        "nom": "Voir sur l'ONISEP",
                                        "url": "https://www.onisep.fr/ressources/univers-metier/metiers/geomaticien-geomaticienne"
                                      }
                                    ]
                                  },
                                  {
                                    "id": "MET002",
                                    "nom": "documentaliste",
                                    "descriptif": null,
                                    "liens": []
                                  }
                                ],
                                "tauxAffinite": 90
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
                                      "id": "aider_autres",
                                      "nom": "Aider les autres"
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
                                  "baccalaureat": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  },
                                  "pourcentage": 18
                                },
                                "autoEvaluationMoyenne": {
                                  "moyenne": 15.0,
                                  "basIntervalleNotes": 14.0,
                                  "hautIntervalleNotes": 16.0,
                                  "baccalaureatUtilise": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  }
                                }
                              }
                            },
                            {
                              "formation": {
                                "id": "fl2",
                                "nom": "2eme formation",
                                "idsFormationsAssociees": [
                                  "fl3"
                                ],
                                "descriptifFormation": null,
                                "descriptifDiplome": null,
                                "descriptifConseils": null,
                                "descriptifAttendus": null,
                                "moyenneGeneraleDesAdmis": null,
                                "criteresAnalyseCandidature": [],
                                "repartitionAdmisAnneePrecedente": null,
                                "liens": [],
                                "villes": [],
                                "metiers": [],
                                "tauxAffinite": 17
                              },
                              "explications": null
                            }
                          ]
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEnseignant(idEnseignant = "egff627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si enseignant, doit retourner 403`() {
            // When & Then
            mvc.perform(
                get("/api/v1/formations?ids=fl1&ids=fl2"),
            ).andDo(print()).andExpect(status().isForbidden).andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "type": "about:blank",
                          "title": "UTILISATEUR_PAS_ELEVE",
                          "status": 403,
                          "detail": "L'utilisateur connecté n'est pas un élève",
                          "instance": "/api/v1/formations"
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteSansId
        @Test
        fun `si connecté sans profil, doit retourner 403`() {
            // When & Then
            mvc.perform(
                get("/api/v1/formations?ids=fl1&ids=fl2"),
            ).andDo(print()).andExpect(status().isForbidden)
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le service échoue avec une erreur interne, alors doit retourner 500`() {
            // Given
            val uneException =
                MonProjetSupInternalErrorException(
                    "ERREUR_API_SUGGESTIONS_CONNEXION",
                    "Erreur lors de la connexion à l'API de suggestions à l'url /api/v1/formations?ids=fl1&ids=fl2",
                    null,
                )
            `when`(suggestionsFormationsService.recupererToutesLesSuggestionsPourUnProfil(unProfil)).thenThrow(uneException)

            // When & Then
            mvc.perform(
                get("/api/v1/formations?ids=fl1&ids=fl2"),
            ).andDo(print()).andExpect(status().isInternalServerError)
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
        }
    }
}
