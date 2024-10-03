package fr.gouv.monprojetsup.eleve.application.controller

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.ConnecteAvecUnEleve
import fr.gouv.monprojetsup.commun.ConnecteAvecUnEnseignant
import fr.gouv.monprojetsup.commun.ConnecteSansId
import fr.gouv.monprojetsup.commun.application.controller.ControllerTest
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupBadRequestException
import fr.gouv.monprojetsup.eleve.domain.entity.ModificationProfilEleve
import fr.gouv.monprojetsup.eleve.domain.entity.VoeuFormation
import fr.gouv.monprojetsup.eleve.usecase.MiseAJourEleveService
import fr.gouv.monprojetsup.eleve.usecase.MiseAJourFavorisParcoursupService
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
import java.util.UUID

@WebMvcTest(controllers = [ProfilEleveController::class])
class ProfilEleveControllerTest(
    @Autowired val mvc: MockMvc,
) : ControllerTest() {
    @MockBean
    lateinit var miseAJourEleveService: MiseAJourEleveService

    @MockBean
    lateinit var miseAJourFavorisParcoursupService: MiseAJourFavorisParcoursupService

    @Nested
    inner class `Quand on appelle la route POST profil` {
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
                formationsFavorites =
                    listOf(
                        VoeuFormation(
                            idFormation = "fl1234",
                            niveauAmbition = 1,
                            tripletsAffectationsChoisis = emptyList(),
                            priseDeNote = null,
                        ),
                        VoeuFormation(
                            idFormation = "fl5678",
                            niveauAmbition = 3,
                            tripletsAffectationsChoisis = listOf("ta1", "ta2"),
                            priseDeNote = "Mon voeu préféré",
                        ),
                    ),
                domainesInterets = listOf("T_ITM_1054", "T_ITM_1351"),
                corbeilleFormations = listOf("fl0001", "fl0002"),
            )

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si élève existe et que le service réussi, doit retourner 204`() {
            // When & Then
            mvc.perform(
                post("/api/v1/profil").contentType(MediaType.APPLICATION_JSON).content(creerRequeteNouveauProfilJson())
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

        @ConnecteAvecUnEleve(idEleve = "61a3b1a9-03dd-4fc6-9549-c0e5d1403214")
        @Test
        fun `si élève n'existe pas et que le service réussi, doit retourner 204`() {
            // Given
            val uuidProfilInconnu = UUID.fromString("61a3b1a9-03dd-4fc6-9549-c0e5d1403214")
            val profilInconnu = ProfilEleve.Inconnu(id = uuidProfilInconnu)
            given(eleveRepository.recupererUnEleve(id = uuidProfilInconnu)).willReturn(profilInconnu)

            // When & Then
            mvc.perform(
                post("/api/v1/profil").contentType(MediaType.APPLICATION_JSON).content(creerRequeteNouveauProfilJson())
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
                post("/api/v1/profil").contentType(MediaType.APPLICATION_JSON).content(creerRequeteNouveauProfilJson())
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(status().isBadRequest)
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si l'élève existe mais que la situation n'est pas dans l'enum, doit retourner 400`() {
            // Given
            val requeteNouveauProfil = creerRequeteNouveauProfilJson(situation = "mon_projet")

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
            val requeteNouveauProfil = creerRequeteNouveauProfilJson(classe = "classe_inconnue")

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
            val requeteNouveauProfil = creerRequeteNouveauProfilJson(dureeEtudesPrevue = "inconnue_au_bataillon")

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
            val requeteNouveauProfil = creerRequeteNouveauProfilJson(alternance = "inconnue_au_bataillon")

            // When & Then
            mvc.perform(
                post("/api/v1/profil").contentType(MediaType.APPLICATION_JSON).content(requeteNouveauProfil)
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(status().isBadRequest)
        }

        @ConnecteAvecUnEnseignant(idEnseignant = "cb3d5ec2-8899-42e0-aa8c-e297b2bcb13f")
        @Test
        fun `si enseignant, doit retourner 403`() {
            // When & Then
            mvc.perform(
                post("/api/v1/profil").contentType(MediaType.APPLICATION_JSON).content(creerRequeteNouveauProfilJson())
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(status().isForbidden)
        }

        @ConnecteSansId
        @Test
        fun `si token, doit retourner 403`() {
            // When & Then
            mvc.perform(
                post("/api/v1/profil").contentType(MediaType.APPLICATION_JSON).content(creerRequeteNouveauProfilJson())
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(status().isForbidden)
        }

        private fun creerRequeteNouveauProfilJson(
            situation: String = "projet_precis",
            classe: String = "terminale",
            dureeEtudesPrevue: String = "longue",
            alternance: String = "interesse",
        ) = """
            {
              "situation": "$situation",
              "classe": "$classe",
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
              "dureeEtudesPrevue": "$dureeEtudesPrevue",
              "alternance": "$alternance",
              "communesFavorites": [
                {
                  "codeInsee": "75115",
                  "nom": "Paris",
                  "latitude": 48.851227,
                  "longitude": 2.2885659
                }
              ],
              "moyenneGenerale": 14,
              "formationsFavorites": [
                {
                  "idFormation": "fl1234",
                  "niveauAmbition": 1,
                  "tripletsAffectationsChoisis": [],
                  "priseDeNote": null
                },
                {
                  "idFormation": "fl5678",
                  "niveauAmbition": 3,
                  "tripletsAffectationsChoisis": [
                    "ta1",
                    "ta2"
                  ],
                  "priseDeNote": "Mon voeu préféré"
                }
              ],
              "corbeilleFormations": [
                "fl0001",
                "fl0002"
              ]
            }
            """.trimIndent()
    }

    @Nested
    inner class `Quand on appelle la route GET profil` {
        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si l'élève existe, doit retourner le profil de l'élève`() {
            // Given
            given(miseAJourFavorisParcoursupService.mettreAJourFavorisParcoursup(unProfil)).willReturn(unProfil)

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
                              "codeInsee": "75115",
                              "nom": "Paris",
                              "latitude": 48.851227,
                              "longitude": 2.2885659
                            }
                          ],
                          "moyenneGenerale": 14.0,
                          "formationsFavorites": [
                            {
                              "idFormation": "fl1234",
                              "niveauAmbition": 1,
                              "tripletsAffectationsChoisis": [],
                              "priseDeNote": null
                            },
                            {
                              "idFormation": "fl5678",
                              "niveauAmbition": 3,
                              "tripletsAffectationsChoisis": [
                                "ta1",
                                "ta2"
                              ],
                              "priseDeNote": "Mon voeu préféré"
                            }
                          ],
                          "corbeilleFormations": [
                            "fl0010",
                            "fl0012"
                          ]
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si les favoris de l'élève sont mis à jours, doit retourner le profil de l'élève avec ses favoris mis à jours`() {
            // Given
            val nouvellesFormationsFavortites =
                listOf(
                    VoeuFormation(
                        idFormation = "fl5678",
                        niveauAmbition = 3,
                        tripletsAffectationsChoisis = listOf("ta1", "ta2"),
                        priseDeNote = "Mon voeu préféré",
                    ),
                    VoeuFormation(
                        idFormation = "fl1234",
                        niveauAmbition = 1,
                        tripletsAffectationsChoisis = emptyList(),
                        priseDeNote = null,
                    ),
                    VoeuFormation(
                        idFormation = "fl2341",
                        niveauAmbition = 0,
                        tripletsAffectationsChoisis = listOf("ta6", "ta22"),
                        priseDeNote = null,
                    ),
                )
            val nouveauProfil = unProfil.copy(formationsFavorites = nouvellesFormationsFavortites)
            given(miseAJourFavorisParcoursupService.mettreAJourFavorisParcoursup(unProfil)).willReturn(nouveauProfil)

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
                              "codeInsee": "75115",
                              "nom": "Paris",
                              "latitude": 48.851227,
                              "longitude": 2.2885659
                            }
                          ],
                          "moyenneGenerale": 14.0,
                          "formationsFavorites": [
                            {
                              "idFormation": "fl5678",
                              "niveauAmbition": 3,
                              "tripletsAffectationsChoisis": [
                                "ta1",
                                "ta2"
                              ],
                              "priseDeNote": "Mon voeu préféré"
                            },
                            {
                              "idFormation": "fl1234",
                              "niveauAmbition": 1,
                              "tripletsAffectationsChoisis": [],
                              "priseDeNote": null
                            },
                            {
                              "idFormation": "fl2341",
                              "niveauAmbition": 0,
                              "tripletsAffectationsChoisis": [
                                "ta6",
                                "ta22"
                              ],
                              "priseDeNote": null
                            }
                          ],
                          "corbeilleFormations": [
                            "fl0010",
                            "fl0012"
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
            val uuidProfilInconnu = UUID.fromString("123f627c-36dd-4df5-897b-159443a6d49c")
            val profilInconnu = ProfilEleve.Inconnu(id = uuidProfilInconnu)
            given(eleveRepository.recupererUnEleve(id = uuidProfilInconnu)).willReturn(profilInconnu)

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

        @ConnecteAvecUnEnseignant(idEnseignant = "cb3d5ec2-8899-42e0-aa8c-e297b2bcb13f")
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
