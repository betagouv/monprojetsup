package fr.gouv.monprojetsup.metier.application.controller

import fr.gouv.monprojetsup.commun.ConnecteAvecUnEleve
import fr.gouv.monprojetsup.commun.ConnecteAvecUnEnseignant
import fr.gouv.monprojetsup.commun.ConnecteSansId
import fr.gouv.monprojetsup.commun.application.controller.ControllerTest
import fr.gouv.monprojetsup.commun.lien.domain.entity.Lien
import fr.gouv.monprojetsup.metier.domain.entity.Metier
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
class MetierControllerTest(
    @Autowired val mvc: MockMvc,
) : ControllerTest() {
    @MockBean
    lateinit var recupererMetiersService: RecupererMetiersService

    @Nested
    inner class `Quand on appelle la route de suggestions de formations` {
        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si connecté en élève, doit retourner 200 avec la liste des métiers`() {
            // Given
            val idsMetier = listOf("MET_356", "MET_355", "MET_358")
            val metiers =
                listOf(
                    Metier(
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
                    ),
                    Metier(
                        id = "MET_355",
                        nom = "responsable de laboratoire de contrôle en chimie",
                        descriptif =
                            "<p> Dernière étape avant la mise sur le marché d'un produit issu de l'industrie de la chimie : " +
                                "son contrôle. Sous la direction du responsable de laboratoire de contrôle, des tests sont effectués " +
                                "pour évaluer sa qualité et sa conformité aux normes. </p>",
                        liens = emptyList(),
                    ),
                    Metier(
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
                    ),
                )
            `when`(recupererMetiersService.recupererMetiers(idsMetier)).thenReturn(metiers)

            // When & Then
            mvc.perform(
                get("/api/v1/metiers?ids=MET_356&ids=MET_355&ids=MET_358"),
            ).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
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
                              ]
                            },
                            {
                              "id": "MET_355",
                              "nom": "responsable de laboratoire de contrôle en chimie",
                              "descriptif": "<p> Dernière étape avant la mise sur le marché d'un produit issu de l'industrie de la chimie : son contrôle. Sous la direction du responsable de laboratoire de contrôle, des tests sont effectués pour évaluer sa qualité et sa conformité aux normes. </p>",
                              "liens": []
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
                              ]
                            }
                          ]
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEnseignant(idEnseignant = "egff627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si connecté en enseignant, doit retourner 200 avec la liste des métiers`() {
            // Given
            val idsMetier = listOf("MET_356", "MET_355", "MET_358")
            val metiers =
                listOf(
                    Metier(
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
                    ),
                    Metier(
                        id = "MET_355",
                        nom = "responsable de laboratoire de contrôle en chimie",
                        descriptif =
                            "<p> Dernière étape avant la mise sur le marché d'un produit issu de l'industrie de la chimie : " +
                                "son contrôle. Sous la direction du responsable de laboratoire de contrôle, des tests sont effectués " +
                                "pour évaluer sa qualité et sa conformité aux normes. </p>",
                        liens = emptyList(),
                    ),
                    Metier(
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
                    ),
                )
            `when`(recupererMetiersService.recupererMetiers(idsMetier)).thenReturn(metiers)

            // When & Then
            mvc.perform(
                get("/api/v1/metiers?ids=MET_356&ids=MET_355&ids=MET_358"),
            ).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
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
                              ]
                            },
                            {
                              "id": "MET_355",
                              "nom": "responsable de laboratoire de contrôle en chimie",
                              "descriptif": "<p> Dernière étape avant la mise sur le marché d'un produit issu de l'industrie de la chimie : son contrôle. Sous la direction du responsable de laboratoire de contrôle, des tests sont effectués pour évaluer sa qualité et sa conformité aux normes. </p>",
                              "liens": []
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
                              ]
                            }
                          ]
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
                get("/api/v1/metiers?ids=MET_356&ids=MET_355&ids=MET_358"),
            ).andDo(MockMvcResultHandlers.print()).andExpect(status().isForbidden)
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si appel sans ids, doit retourner 400`() {
            // When & Then
            mvc.perform(
                get("/api/v1/metiers"),
            ).andDo(MockMvcResultHandlers.print()).andExpect(status().isBadRequest)
        }
    }
}
