package fr.gouv.monprojetsup.metier.application.controller

import fr.gouv.monprojetsup.commun.ConnecteAvecUnEleve
import fr.gouv.monprojetsup.commun.ConnecteAvecUnEnseignant
import fr.gouv.monprojetsup.commun.ConnecteSansId
import fr.gouv.monprojetsup.commun.application.controller.ControllerTest
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupBadRequestException
import fr.gouv.monprojetsup.commun.hateoas.domain.entity.Hateoas
import fr.gouv.monprojetsup.commun.hateoas.usecase.HateoasBuilder
import fr.gouv.monprojetsup.commun.lien.domain.entity.Lien
import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import fr.gouv.monprojetsup.metier.domain.entity.MetierAvecSesFormations
import fr.gouv.monprojetsup.metier.domain.entity.MetierCourt
import fr.gouv.monprojetsup.metier.usecase.RechercherMetiersService
import fr.gouv.monprojetsup.metier.usecase.RecupererMetiersService
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [MetierController::class])
class MetiersControllerTest(
    @Autowired val mvc: MockMvc,
) : ControllerTest() {
    @MockBean
    lateinit var recupererMetiersService: RecupererMetiersService

    @MockBean
    lateinit var rechercherMetiersService: RechercherMetiersService

    @MockBean
    lateinit var hateoasBuilder: HateoasBuilder

    @Nested
    inner class `Quand on appelle la route de récupération de métiers` {
        val metiers =
            listOf(
                MetierAvecSesFormations(
                    id = "MET_356",
                    nom = "ingénieur / ingénieure matériaux",
                    descriptif =
                        "<p>L'ingénieur matériaux intervient de la conception à l'utilisation des matériaux. À la pointe " +
                            "de l'innovation, cet expert met ses compétences au service d'un bureau d'études, d'une entreprise " +
                            "industrielle ou d'un organisme de recherche.</p>",
                    liens =
                        listOf(
                            Lien(
                                nom = "Management et ingénierie études, recherche et développement industriel",
                                url = "https://candidat.pole-emploi.fr/marche-du-travail/fichemetierrome?codeRome=H1206",
                            ),
                            Lien(
                                nom = "ingénieur / ingénieure matériaux",
                                url = "https://explorer-avenirs.onisep.fr/http/redirection/metier/slug/MET.356",
                            ),
                        ),
                    formations =
                        listOf(
                            FormationCourte(id = "fl1", nom = "CPGE MPSI"),
                            FormationCourte(id = "fl7", nom = "BUT Informatique"),
                        ),
                ),
                MetierAvecSesFormations(
                    id = "MET_355",
                    nom = "responsable de laboratoire de contrôle en chimie",
                    descriptif =
                        "<p> Dernière étape avant la mise sur le marché d'un produit issu de l'industrie de la chimie : " +
                            "son contrôle. Sous la direction du responsable de laboratoire de contrôle, des tests sont effectués " +
                            "pour évaluer sa qualité et sa conformité aux normes. </p>",
                    liens = emptyList(),
                    formations = emptyList(),
                ),
                MetierAvecSesFormations(
                    id = "MET_358",
                    nom = "pilote d'hélicoptère",
                    descriptif =
                        "<p>Le pilote d'hélicoptère, militaire ou civil, participe à des missions de combat ou de secours. " +
                            "Il est capable de se poser n'importe où et d'évoluer en montagne. Sang-froid et ténacité sont " +
                            "nécessaires pour exercer ce métier.</p>",
                    liens =
                        listOf(
                            Lien(
                                nom = "pilote d'hélicoptère",
                                url = "https://explorer-avenirs.onisep.fr/http/redirection/metier/slug/MET.358",
                            ),
                            Lien(
                                nom = "Pilotage et navigation technique aérienne",
                                url = "https://candidat.pole-emploi.fr/marche-du-travail/fichemetierrome?codeRome=N2102",
                            ),
                        ),
                    formations =
                        listOf(
                            FormationCourte(id = "fl3", nom = "CAP Pâtisserie"),
                            FormationCourte(id = "fl1000", nom = "BPJEPS"),
                            FormationCourte(id = "fl17", nom = "L1 - Mathématique"),
                        ),
                ),
            )

        val resultatMetiers =
            """
            {
              "metiers": [
                {
                  "id": "MET_356",
                  "nom": "ingénieur / ingénieure matériaux",
                  "descriptif": "<p>L'ingénieur matériaux intervient de la conception à l'utilisation des matériaux. À la pointe de l'innovation, cet expert met ses compétences au service d'un bureau d'études, d'une entreprise industrielle ou d'un organisme de recherche.</p>",
                  "liens": [
                    {
                      "nom": "Management et ingénierie études, recherche et développement industriel",
                      "url": "https://candidat.pole-emploi.fr/marche-du-travail/fichemetierrome?codeRome=H1206"
                    },
                    {
                      "nom": "ingénieur / ingénieure matériaux",
                      "url": "https://explorer-avenirs.onisep.fr/http/redirection/metier/slug/MET.356"
                    }
                  ],
                  "formations": [
                    {
                      "id": "fl1",
                      "nom": "CPGE MPSI"
                    },
                    {
                      "id": "fl7",
                      "nom": "BUT Informatique"
                    }
                  ]
                },
                {
                  "id": "MET_355",
                  "nom": "responsable de laboratoire de contrôle en chimie",
                  "descriptif": "<p> Dernière étape avant la mise sur le marché d'un produit issu de l'industrie de la chimie : son contrôle. Sous la direction du responsable de laboratoire de contrôle, des tests sont effectués pour évaluer sa qualité et sa conformité aux normes. </p>",
                  "liens": [],
                  "formations": []
                },
                {
                  "id": "MET_358",
                  "nom": "pilote d'hélicoptère",
                  "descriptif": "<p>Le pilote d'hélicoptère, militaire ou civil, participe à des missions de combat ou de secours. Il est capable de se poser n'importe où et d'évoluer en montagne. Sang-froid et ténacité sont nécessaires pour exercer ce métier.</p>",
                  "liens": [
                    {
                      "nom": "pilote d'hélicoptère",
                      "url": "https://explorer-avenirs.onisep.fr/http/redirection/metier/slug/MET.358"
                    },
                    {
                      "nom": "Pilotage et navigation technique aérienne",
                      "url": "https://candidat.pole-emploi.fr/marche-du-travail/fichemetierrome?codeRome=N2102"
                    }
                  ],
                  "formations": [
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
                    }
                  ]
                }
              ],
              "liens": [
                {
                  "rel": "premier",
                  "href": "http://localhost/api/v1/metiers?ids=MET_356&ids=MET_355&ids=MET_358&numeroDePage=1"
                },
                {
                  "rel": "dernier",
                  "href": "http://localhost/api/v1/metiers?ids=MET_356&ids=MET_355&ids=MET_358&numeroDePage=1"
                },
                {
                  "rel": "actuel",
                  "href": "http://localhost/api/v1/metiers?ids=MET_356&ids=MET_355&ids=MET_358&numeroDePage=1"
                }
              ]
            }
            """.trimIndent()

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si connecté en élève, doit retourner 200 avec la liste des métiers`() {
            // Given
            val idsMetier = listOf("MET_356", "MET_355", "MET_358")

            `when`(recupererMetiersService.recupererMetiers(idsMetier)).thenReturn(metiers)
            val hateoas =
                Hateoas(
                    pageActuelle = 1,
                    pageSuivante = null,
                    premierePage = 1,
                    dernierePage = 1,
                    listeCoupee = metiers,
                )
            `when`(hateoasBuilder.creerHateoas(liste = metiers, numeroDePageActuelle = 1, tailleLot = 30)).thenReturn(hateoas)

            // When & Then
            mvc.perform(
                get("/api/v1/metiers?ids=MET_356&ids=MET_355&ids=MET_358"),
            ).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(resultatMetiers))
        }

        @ConnecteAvecUnEnseignant(idEnseignant = "cb3d5ec2-8899-42e0-aa8c-e297b2bcb13f")
        @Test
        fun `si connecté en enseignant, doit retourner 200 avec la liste des métiers`() {
            // Given
            val idsMetier = listOf("MET_356", "MET_355", "MET_358")
            `when`(recupererMetiersService.recupererMetiers(idsMetier)).thenReturn(metiers)
            val hateoas =
                Hateoas(
                    pageActuelle = 1,
                    pageSuivante = null,
                    premierePage = 1,
                    dernierePage = 1,
                    listeCoupee = metiers,
                )
            `when`(hateoasBuilder.creerHateoas(liste = metiers, numeroDePageActuelle = 1, tailleLot = 30)).thenReturn(hateoas)

            // When & Then
            mvc.perform(
                get("/api/v1/metiers?ids=MET_356&ids=MET_355&ids=MET_358"),
            ).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(resultatMetiers))
        }

        @ConnecteSansId
        @Test
        fun `si connecté sans profil, doit retourner 200 avec la liste des métiers`() {
            // Given
            val idsMetier = listOf("MET_356", "MET_355", "MET_358")
            `when`(recupererMetiersService.recupererMetiers(idsMetier)).thenReturn(metiers)
            val hateoas =
                Hateoas(
                    pageActuelle = 1,
                    pageSuivante = null,
                    premierePage = 1,
                    dernierePage = 1,
                    listeCoupee = metiers,
                )
            `when`(hateoasBuilder.creerHateoas(liste = metiers, numeroDePageActuelle = 1, tailleLot = 30)).thenReturn(hateoas)

            // When & Then
            mvc.perform(
                get("/api/v1/metiers?ids=MET_356&ids=MET_355&ids=MET_358"),
            ).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(resultatMetiers))
        }

        @Test
        fun `si pas connecté, doit retourner 401`() {
            // When & Then
            mvc.perform(get("/api/v1/metiers?ids=MET_356&ids=MET_355&ids=MET_358")).andExpect(status().isUnauthorized)
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si appel sans ids, doit retourner 400`() {
            // When & Then
            mvc.perform(get("/api/v1/metiers")).andExpect(status().isBadRequest)
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le numéro de page dépasse le nombre de page maximum, doit retourner 400`() {
            // Given
            val idsMetier = listOf("MET_356", "MET_355", "MET_358")
            `when`(recupererMetiersService.recupererMetiers(idsMetier)).thenReturn(metiers)
            val exception =
                MonProjetSupBadRequestException(
                    code = "PAGE_DEMANDEE_INXISTANTE",
                    msg = "La page 10 n'existe pas. Veuillez en donner une entre 1 et 5",
                )
            `when`(hateoasBuilder.creerHateoas(liste = metiers, numeroDePageActuelle = 10, tailleLot = 30)).thenThrow(exception)

            // When & Then
            mvc.perform(
                get("/api/v1/metiers?ids=MET_356&ids=MET_355&ids=MET_358&numeroDePage=10"),
            ).andDo(
                MockMvcResultHandlers.print(),
            ).andExpect(status().isBadRequest).andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "type": "about:blank",
                          "title": "PAGE_DEMANDEE_INXISTANTE",
                          "status": 400,
                          "detail": "La page 10 n'existe pas. Veuillez en donner une entre 1 et 5",
                          "instance": "/api/v1/metiers"
                        }
                        """.trimIndent(),
                    ),
                )
        }
    }

    @Nested
    inner class `Quand on appelle la route de recherche de métiers` {
        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si connecté en élève, doit retourner 200 avec la liste des métiers`() {
            // Given
            val metiers =
                listOf(
                    MetierCourt(id = "MET_454", nom = "garde à cheval"),
                    MetierCourt(id = "MET_471", nom = "entraîneur / entraîneuse de chevaux"),
                    MetierCourt(id = "MET_682", nom = "maréchal-ferrant / maréchale-ferrante"),
                    MetierCourt(id = "MET_155", nom = "lad-jockey, lad-driver"),
                    MetierCourt(id = "MET_345", nom = "moniteur/trice d'activités équestres"),
                    MetierCourt(id = "MET_19", nom = "palefrenier / palefrenière"),
                    MetierCourt(id = "MET_98", nom = "sellier/ère"),
                )
            `when`(
                rechercherMetiersService.rechercherMetiersTriesParScores(
                    recherche = "cheval",
                    tailleMinimumRecherche = 2,
                ),
            ).thenReturn(metiers)
            val hateoas =
                Hateoas(
                    pageActuelle = 1,
                    pageSuivante = null,
                    premierePage = 1,
                    dernierePage = 1,
                    listeCoupee = metiers,
                )
            `when`(hateoasBuilder.creerHateoas(liste = metiers, numeroDePageActuelle = 1, tailleLot = 30)).thenReturn(hateoas)

            // When & Then
            mvc.perform(
                get("/api/v1/metiers/recherche/succincte?recherche=cheval"),
            ).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "metiers": [
                            {
                              "id": "MET_454",
                              "nom": "garde à cheval"
                            },
                            {
                              "id": "MET_471",
                              "nom": "entraîneur / entraîneuse de chevaux"
                            },
                            {
                              "id": "MET_682",
                              "nom": "maréchal-ferrant / maréchale-ferrante"
                            },
                            {
                              "id": "MET_155",
                              "nom": "lad-jockey, lad-driver"
                            },
                            {
                              "id": "MET_345",
                              "nom": "moniteur/trice d'activités équestres"
                            },
                            {
                              "id": "MET_19",
                              "nom": "palefrenier / palefrenière"
                            },
                            {
                              "id": "MET_98",
                              "nom": "sellier/ère"
                            }
                          ],
                          "liens": [
                            {
                              "rel": "premier",
                              "href": "http://localhost/api/v1/metiers/recherche/succincte?recherche=cheval&numeroDePage=1"
                            },
                            {
                              "rel": "dernier",
                              "href": "http://localhost/api/v1/metiers/recherche/succincte?recherche=cheval&numeroDePage=1"
                            },
                            {
                              "rel": "actuel",
                              "href": "http://localhost/api/v1/metiers/recherche/succincte?recherche=cheval&numeroDePage=1"
                            }
                          ]
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEnseignant(idEnseignant = "cb3d5ec2-8899-42e0-aa8c-e297b2bcb13f")
        @Test
        fun `si connecté en enseignant, doit retourner 200 avec la liste des métiers`() {
            // Given
            val metiers =
                listOf(
                    MetierCourt(id = "MET_454", nom = "garde à cheval"),
                    MetierCourt(id = "MET_471", nom = "entraîneur / entraîneuse de chevaux"),
                    MetierCourt(id = "MET_682", nom = "maréchal-ferrant / maréchale-ferrante"),
                    MetierCourt(id = "MET_155", nom = "lad-jockey, lad-driver"),
                    MetierCourt(id = "MET_345", nom = "moniteur/trice d'activités équestres"),
                    MetierCourt(id = "MET_19", nom = "palefrenier / palefrenière"),
                    MetierCourt(id = "MET_98", nom = "sellier/ère"),
                )
            `when`(
                rechercherMetiersService.rechercherMetiersTriesParScores(
                    recherche = "cheval",
                    tailleMinimumRecherche = 2,
                ),
            ).thenReturn(metiers)
            val hateoas =
                Hateoas(
                    pageActuelle = 1,
                    pageSuivante = null,
                    premierePage = 1,
                    dernierePage = 1,
                    listeCoupee = metiers,
                )
            `when`(hateoasBuilder.creerHateoas(liste = metiers, numeroDePageActuelle = 1, tailleLot = 30)).thenReturn(hateoas)

            // When & Then
            mvc.perform(
                get("/api/v1/metiers/recherche/succincte?recherche=cheval"),
            ).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "metiers": [
                            {
                              "id": "MET_454",
                              "nom": "garde à cheval"
                            },
                            {
                              "id": "MET_471",
                              "nom": "entraîneur / entraîneuse de chevaux"
                            },
                            {
                              "id": "MET_682",
                              "nom": "maréchal-ferrant / maréchale-ferrante"
                            },
                            {
                              "id": "MET_155",
                              "nom": "lad-jockey, lad-driver"
                            },
                            {
                              "id": "MET_345",
                              "nom": "moniteur/trice d'activités équestres"
                            },
                            {
                              "id": "MET_19",
                              "nom": "palefrenier / palefrenière"
                            },
                            {
                              "id": "MET_98",
                              "nom": "sellier/ère"
                            }
                          ],
                          "liens": [
                            {
                              "rel": "premier",
                              "href": "http://localhost/api/v1/metiers/recherche/succincte?recherche=cheval&numeroDePage=1"
                            },
                            {
                              "rel": "dernier",
                              "href": "http://localhost/api/v1/metiers/recherche/succincte?recherche=cheval&numeroDePage=1"
                            },
                            {
                              "rel": "actuel",
                              "href": "http://localhost/api/v1/metiers/recherche/succincte?recherche=cheval&numeroDePage=1"
                            }
                          ]
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteSansId
        @Test
        fun `si connecté sans profil, doit retourner 200 avec la liste des métiers`() {
            // Given
            val metiers =
                listOf(
                    MetierCourt(id = "MET_454", nom = "garde à cheval"),
                    MetierCourt(id = "MET_471", nom = "entraîneur / entraîneuse de chevaux"),
                    MetierCourt(id = "MET_682", nom = "maréchal-ferrant / maréchale-ferrante"),
                    MetierCourt(id = "MET_155", nom = "lad-jockey, lad-driver"),
                    MetierCourt(id = "MET_345", nom = "moniteur/trice d'activités équestres"),
                    MetierCourt(id = "MET_19", nom = "palefrenier / palefrenière"),
                    MetierCourt(id = "MET_98", nom = "sellier/ère"),
                )
            `when`(
                rechercherMetiersService.rechercherMetiersTriesParScores(
                    recherche = "cheval",
                    tailleMinimumRecherche = 2,
                ),
            ).thenReturn(metiers)
            val hateoas =
                Hateoas(
                    pageActuelle = 1,
                    pageSuivante = null,
                    premierePage = 1,
                    dernierePage = 1,
                    listeCoupee = metiers,
                )
            `when`(hateoasBuilder.creerHateoas(liste = metiers, numeroDePageActuelle = 1, tailleLot = 30)).thenReturn(hateoas)

            // When & Then
            mvc.perform(
                get("/api/v1/metiers/recherche/succincte?recherche=cheval"),
            ).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "metiers": [
                            {
                              "id": "MET_454",
                              "nom": "garde à cheval"
                            },
                            {
                              "id": "MET_471",
                              "nom": "entraîneur / entraîneuse de chevaux"
                            },
                            {
                              "id": "MET_682",
                              "nom": "maréchal-ferrant / maréchale-ferrante"
                            },
                            {
                              "id": "MET_155",
                              "nom": "lad-jockey, lad-driver"
                            },
                            {
                              "id": "MET_345",
                              "nom": "moniteur/trice d'activités équestres"
                            },
                            {
                              "id": "MET_19",
                              "nom": "palefrenier / palefrenière"
                            },
                            {
                              "id": "MET_98",
                              "nom": "sellier/ère"
                            }
                          ],
                          "liens": [
                            {
                              "rel": "premier",
                              "href": "http://localhost/api/v1/metiers/recherche/succincte?recherche=cheval&numeroDePage=1"
                            },
                            {
                              "rel": "dernier",
                              "href": "http://localhost/api/v1/metiers/recherche/succincte?recherche=cheval&numeroDePage=1"
                            },
                            {
                              "rel": "actuel",
                              "href": "http://localhost/api/v1/metiers/recherche/succincte?recherche=cheval&numeroDePage=1"
                            }
                          ]
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @Test
        fun `si pas connecté, doit retourner 401`() {
            // When & Then
            mvc.perform(get("/api/v1/metiers/recherche/succincte?recherche=cheval")).andExpect(status().isUnauthorized)
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si la recherche est trop courte, doit retourner 400`() {
            // When & Then
            mvc.perform(
                get("/api/v1/metiers/recherche/succincte?recherche=t"),
            ).andDo(MockMvcResultHandlers.print()).andExpect(status().isBadRequest)
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "type": "about:blank",
                          "title": "REQUETE_TROP_COURTE",
                          "status": 400,
                          "detail": "La taille de la requête est trop courte. Elle doit faire au moins 2 caractères",
                          "instance": "/api/v1/metiers/recherche/succincte"
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si la recherche est trop longue, doit retourner 400`() {
            // Given
            val rechercheDe51Caracteres = "Lorem ipsum dolor sit amet, consectetur sodales sed"

            // When & Then
            mvc.perform(
                get("/api/v1/metiers/recherche/succincte?recherche=$rechercheDe51Caracteres"),
            ).andDo(MockMvcResultHandlers.print()).andExpect(status().isBadRequest)
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "type": "about:blank",
                          "title": "REQUETE_TROP_LONGUE",
                          "status": 400,
                          "detail": "La taille de la requête dépasse la taille maximale de 50 caractères",
                          "instance": "/api/v1/metiers/recherche/succincte"
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le numéro de page dépasse le nombre de page maximum, doit retourner 400`() {
            // Given
            val metiers =
                listOf(
                    MetierCourt(id = "MET_454", nom = "garde à cheval"),
                    MetierCourt(id = "MET_471", nom = "entraîneur / entraîneuse de chevaux"),
                    MetierCourt(id = "MET_682", nom = "maréchal-ferrant / maréchale-ferrante"),
                    MetierCourt(id = "MET_155", nom = "lad-jockey, lad-driver"),
                    MetierCourt(id = "MET_345", nom = "moniteur/trice d'activités équestres"),
                    MetierCourt(id = "MET_19", nom = "palefrenier / palefrenière"),
                    MetierCourt(id = "MET_98", nom = "sellier/ère"),
                )
            `when`(
                rechercherMetiersService.rechercherMetiersTriesParScores(
                    recherche = "cheval",
                    tailleMinimumRecherche = 2,
                ),
            ).thenReturn(metiers)
            val exception =
                MonProjetSupBadRequestException(
                    code = "PAGE_DEMANDEE_INXISTANTE",
                    msg = "La page 32 n'existe pas. Veuillez en donner une entre 1 et 6",
                )
            `when`(hateoasBuilder.creerHateoas(liste = metiers, numeroDePageActuelle = 32, tailleLot = 30)).thenThrow(exception)

            // When & Then
            mvc.perform(
                get("/api/v1/metiers/recherche/succincte?recherche=cheval&numeroDePage=32"),
            ).andDo(MockMvcResultHandlers.print()).andExpect(status().isBadRequest)
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "type": "about:blank",
                          "title": "PAGE_DEMANDEE_INXISTANTE",
                          "status": 400,
                          "detail": "La page 32 n'existe pas. Veuillez en donner une entre 1 et 6",
                          "instance": "/api/v1/metiers/recherche/succincte"
                        }
                        """.trimIndent(),
                    ),
                )
        }
    }
}
