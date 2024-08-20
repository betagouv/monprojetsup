package fr.gouv.monprojetsup.eleve.application.controller

import fr.gouv.monprojetsup.authentification.domain.entity.ModificationProfilEleve
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.ConnecteAvecUnEleve
import fr.gouv.monprojetsup.commun.ConnecteAvecUnEnseignant
import fr.gouv.monprojetsup.commun.ConnecteSansId
import fr.gouv.monprojetsup.commun.application.controller.ControllerTest
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupBadRequestException
import fr.gouv.monprojetsup.eleve.usecase.MiseAJourEleveService
import fr.gouv.monprojetsup.formation.entity.Communes
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.referentiel.domain.entity.SituationAvanceeProjetSup
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [ProfilEleveController::class])
class ProfilEleveControllerTest(
    @Autowired val mvc: MockMvc,
) : ControllerTest() {
    @MockBean
    lateinit var miseAJourEleveService: MiseAJourEleveService

    @Nested
    inner class `Quand on appelle la route POST profil` {
        private val requeteNouveauProfil =
            """
            {
              "situation": "projet_precis",
              "classe": "terminale",
              "baccalaureat": "Générale",
              "specialites": [
                "1054"
              ],
              "domaines": [
                "T_ITM_1054",
                "T_ITM_1351"
              ],
              "centresInterets": [
                "T_IDEO2_4812"
              ],
              "metiersFavoris": [
                "MET_456"
              ],
              "dureeEtudesPrevue": "longue",
              "alternance": "interesse",
              "communesFavorites": [
                {
                  "codeInsee": "75015",
                  "nom": "Paris",
                  "latitude": 48.851227,
                  "longitude": 2.2885659
                }
              ],
              "moyenneGenerale": 14,
              "formationsFavorites": [
                "fl1234",
                "fl5678"
              ],
              "corbeilleFormations": [
                "fl0001",
                "fl0002"
              ]
            }
            """.trimIndent()

        private val modificationProfilEleve =
            ModificationProfilEleve(
                situation = SituationAvanceeProjetSup.PROJET_PRECIS,
                classe = ChoixNiveau.TERMINALE,
                baccalaureat = "Générale",
                dureeEtudesPrevue = ChoixDureeEtudesPrevue.LONGUE,
                alternance = ChoixAlternance.INTERESSE,
                communesFavorites = listOf(Communes.PARIS15EME),
                specialites = listOf("1054"),
                centresInterets = listOf("T_IDEO2_4812"),
                moyenneGenerale = 14f,
                metiersFavoris = listOf("MET_456"),
                formationsFavorites = listOf("fl1234", "fl5678"),
                domainesInterets = listOf("T_ITM_1054", "T_ITM_1351"),
                corbeilleFormations = listOf("fl0001", "fl0002"),
            )

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si élève existe et que le service réussi, doit retourner 204`() {
            // When & Then
            mvc.perform(
                post("/api/v1/profil").contentType(MediaType.APPLICATION_JSON).content(requeteNouveauProfil)
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(status().isNoContent)
            then(miseAJourEleveService).should().mettreAJourUnProfilEleve(modificationProfilEleve, unProfil)
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si élève existe et que le service réussi pour une modification vide, doit retourner 204`() {
            // Given
            val modificationProfilEleveVide = ModificationProfilEleve()

            // When & Then
            mvc.perform(
                post("/api/v1/profil").contentType(MediaType.APPLICATION_JSON).content("{}")
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(status().isNoContent)
            then(miseAJourEleveService).should().mettreAJourUnProfilEleve(
                miseAJourDuProfil = modificationProfilEleveVide,
                profilActuel = unProfil,
            )
        }

        @ConnecteAvecUnEleve(idEleve = "efgf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si élève n'existe pas et que le service réussi, doit retourner 204`() {
            // Given
            val profilInconnu = ProfilEleve.Inconnu(id = "efgf627c-36dd-4df5-897b-159443a6d49c")
            given(eleveRepository.recupererUnEleve(id = "efgf627c-36dd-4df5-897b-159443a6d49c")).willReturn(profilInconnu)

            // When & Then
            mvc.perform(
                post("/api/v1/profil").contentType(MediaType.APPLICATION_JSON).content(requeteNouveauProfil)
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(status().isNoContent)
            then(miseAJourEleveService).should().mettreAJourUnProfilEleve(
                miseAJourDuProfil = modificationProfilEleve,
                profilActuel = profilInconnu,
            )
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si l'élève existe mais que le service échoue, doit retourner 400`() {
            // Given
            val exception = MonProjetSupBadRequestException("FORMATIONS_NON_RECONNUES", "Une ou plusieurs des formations n'existent pas")
            given(
                miseAJourEleveService.mettreAJourUnProfilEleve(miseAJourDuProfil = modificationProfilEleve, profilActuel = unProfil),
            ).willThrow(exception)

            // When & Then
            mvc.perform(
                post("/api/v1/profil").contentType(MediaType.APPLICATION_JSON).content(requeteNouveauProfil)
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(status().isBadRequest)
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si l'élève existe mais que la situation n'est pas dans l'enum, doit retourner 400`() {
            // Given
            val requeteNouveauProfil =
                """
                {
                  "situation": "mon_projet",
                  "classe": "terminale",
                  "baccalaureat": "Générale",
                  "specialites": [
                    "1054"
                  ],
                  "domaines": [
                    "T_ITM_1054",
                    "T_ITM_1351"
                  ],
                  "centresInterets": [
                    "T_IDEO2_4812"
                  ],
                  "metiersFavoris": [
                    "MET_456"
                  ],
                  "dureeEtudesPrevue": "longue",
                  "alternance": "interesse",
                  "communesFavorites": [
                    {
                      "codeInsee": "75015",
                      "nom": "Paris",
                      "latitude": 48.8512252,
                      "longitude": 2.2885659
                    }
                  ],
                  "moyenneGenerale": 14,
                  "formationsFavorites": [
                    "fl1234",
                    "fl5678"
                  ]
                }
                """.trimIndent()

            // When & Then
            mvc.perform(
                post("/api/v1/profil").contentType(MediaType.APPLICATION_JSON).content(requeteNouveauProfil)
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(status().isBadRequest)
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si l'élève existe mais que la classe n'est pas dans l'enum, doit retourner 400`() {
            // Given
            val requeteNouveauProfil =
                """
                {
                  "situation": "projet_precis",
                  "classe": "classe_inconnue",
                  "baccalaureat": "Générale",
                  "specialites": [
                    "1054"
                  ],
                  "domaines": [
                    "T_ITM_1054",
                    "T_ITM_1351"
                  ],
                  "centresInterets": [
                    "T_IDEO2_4812"
                  ],
                  "metiersFavoris": [
                    "MET_456"
                  ],
                  "dureeEtudesPrevue": "longue",
                  "alternance": "interesse",
                  "communesFavorites": [
                    {
                      "codeInsee": "75015",
                      "nom": "Paris",
                      "latitude": 48.8512252,
                      "longitude": 2.2885659
                    }
                  ],
                  "moyenneGenerale": 14,
                  "formationsFavorites": [
                    "fl1234",
                    "fl5678"
                  ]
                }
                """.trimIndent()

            // When & Then
            mvc.perform(
                post("/api/v1/profil").contentType(MediaType.APPLICATION_JSON).content(requeteNouveauProfil)
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(status().isBadRequest)
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si l'élève existe mais que la durées des études n'est pas dans l'enum, doit retourner 400`() {
            // Given
            val requeteNouveauProfil =
                """
                {
                  "situation": "projet_precis",
                  "classe": "terminale",
                  "baccalaureat": "Générale",
                  "specialites": [
                    "1054"
                  ],
                  "domaines": [
                    "T_ITM_1054",
                    "T_ITM_1351"
                  ],
                  "centresInterets": [
                    "T_IDEO2_4812"
                  ],
                  "metiersFavoris": [
                    "MET_456"
                  ],
                  "dureeEtudesPrevue": "inconnue_au_bataillon",
                  "alternance": "interesse",
                  "communesFavorites": [
                    {
                      "codeInsee": "75015",
                      "nom": "Paris",
                      "latitude": 48.8512252,
                      "longitude": 2.2885659
                    }
                  ],
                  "moyenneGenerale": 14,
                  "formationsFavorites": [
                    "fl1234",
                    "fl5678"
                  ]
                }
                """.trimIndent()

            // When & Then
            mvc.perform(
                post("/api/v1/profil").contentType(MediaType.APPLICATION_JSON).content(requeteNouveauProfil)
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(status().isBadRequest)
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si l'élève existe mais que le choix de l'alternance n'est pas dans l'enum, doit retourner 400`() {
            // Given
            val requeteNouveauProfil =
                """
                {
                  "situation": "projet_precis",
                  "classe": "terminale",
                  "baccalaureat": "Générale",
                  "specialites": [
                    "1054"
                  ],
                  "domaines": [
                    "T_ITM_1054",
                    "T_ITM_1351"
                  ],
                  "centresInterets": [
                    "T_IDEO2_4812"
                  ],
                  "metiersFavoris": [
                    "MET_456"
                  ],
                  "dureeEtudesPrevue": "longue",
                  "alternance": "inconnue_au_bataillon",
                  "communesFavorites": [
                    {
                      "codeInsee": "75015",
                      "nom": "Paris",
                      "latitude": 48.8512252,
                      "longitude": 2.2885659
                    }
                  ],
                  "moyenneGenerale": 14,
                  "formationsFavorites": [
                    "fl1234",
                    "fl5678"
                  ]
                }
                """.trimIndent()

            // When & Then
            mvc.perform(
                post("/api/v1/profil").contentType(MediaType.APPLICATION_JSON).content(requeteNouveauProfil)
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(status().isBadRequest)
        }

        @ConnecteAvecUnEnseignant(idEnseignant = "egff627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si enseignant, doit retourner 403`() {
            // When & Then
            mvc.perform(
                post("/api/v1/profil").contentType(MediaType.APPLICATION_JSON).content(requeteNouveauProfil)
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(status().isForbidden)
        }

        @ConnecteSansId
        @Test
        fun `si token, doit retourner 403`() {
            // When & Then
            mvc.perform(
                post("/api/v1/profil").contentType(MediaType.APPLICATION_JSON).content(requeteNouveauProfil)
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(status().isForbidden)
        }
    }

    @Nested
    inner class `Quand on appelle la route GET profil` {
        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si l'élève existe, doit retourner le profil de l'élève`() {
            // When & Then
            mvc.perform(get("/api/v1/profil")).andDo(print())
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "situation": "aucune_idee",
                          "classe": "terminale",
                          "baccalaureat": "Générale",
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
                          "metiersFavoris": [
                            "MET_123",
                            "MET_456"
                          ],
                          "dureeEtudesPrevue": "indifferent",
                          "alternance": "pas_interesse",
                          "communesFavorites": [
                            {
                              "codeInsee": "75015",
                              "nom": "Paris",
                              "latitude": 48.851227,
                              "longitude": 2.2885659
                            }
                          ],
                          "moyenneGenerale": 14.0,
                          "formationsFavorites": [
                            "fl1234",
                            "fl5678"
                          ]
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEleve(idEleve = "123f627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si l'élève n'existe pas, doit renvoyer 403`() {
            // Given
            val profilInconnu = ProfilEleve.Inconnu(id = "123f627c-36dd-4df5-897b-159443a6d49c")
            given(eleveRepository.recupererUnEleve(id = "123f627c-36dd-4df5-897b-159443a6d49c")).willReturn(profilInconnu)

            // When & Then
            mvc.perform(get("/api/v1/profil")).andDo(print())
                .andExpect(status().isForbidden)
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "type": "about:blank",
                          "title": "ELEVE_SANS_COMPTE",
                          "status": 403,
                          "detail": "L'élève connecté n'a pas encore crée son compte",
                          "instance": "/api/v1/profil"
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEnseignant(idEnseignant = "egff627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si enseignant, doit retourner 403`() {
            // When & Then
            mvc.perform(get("/api/v1/profil")).andExpect(status().isForbidden)
        }

        @ConnecteSansId
        @Test
        fun `si token, doit retourner 403`() {
            // When & Then
            mvc.perform(get("/api/v1/profil")).andExpect(status().isForbidden)
        }
    }
}
