package fr.gouv.monprojetsup.recherche.application.controller

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.recherche.domain.entity.FormationPourProfil
import fr.gouv.monprojetsup.recherche.domain.entity.Profile
import fr.gouv.monprojetsup.recherche.domain.entity.Suggestion
import fr.gouv.monprojetsup.recherche.usecase.SuggestionsFormationsService
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
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

    @Nested
    inner class `Quand on appelle la route de recherche de formations` {
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
        private val requete =
            """
            {
              "profile": {
                "niveau": "term",
                "bac": "Générale",
                "duree": "long",
                "apprentissage": "C",
                "preferencesGeographique": [
                  "Soulac-sur-Mer",
                  "Nantes"
                ],
                "specialites": [
                  "Sciences de la vie et de la Terre",
                  "Mathématiques"
                ],
                "interets": [
                  "T_ITM_1054",
                  "T_ITM_1534",
                  "T_ITM_1248",
                  "T_ITM_1351",
                  "T_ROME_2092381917",
                  "T_IDEO2_4812"
                ],
                "moyenneGenerale": "14",
                "choix": [
                  {
                    "fl": "fl2014",
                    "status": 1,
                    "date": "string"
                  }
                ]
              }
            }
            """.trimIndent()

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
            Mockito.`when`(suggestionsFormationsService.suggererFormations(unProfil, 0, 50)).thenReturn(listOf(formationPourProfil))

            // when-then
            mvc.perform(
                post("/api/v1/formations/recherche")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete)
                    .accept(MediaType.APPLICATION_JSON),
            )
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
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
        fun `si le service échoue, doit retourner 500`() {
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
                post("/api/v1/formations/recherche")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requete)
                    .accept(MediaType.APPLICATION_JSON),
            )
                .andDo(print())
                .andExpect(status().isInternalServerError)
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
        }
    }
}
