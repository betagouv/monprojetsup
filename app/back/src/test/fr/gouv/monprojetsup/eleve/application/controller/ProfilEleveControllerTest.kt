package fr.gouv.monprojetsup.eleve.application.controller

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.ConnecteAvecUnEleve
import fr.gouv.monprojetsup.commun.ConnecteAvecUnEnseignant
import fr.gouv.monprojetsup.commun.ConnecteSansId
import fr.gouv.monprojetsup.commun.application.controller.ControllerTest
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupBadRequestException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.eleve.domain.entity.FormationFavorite
import fr.gouv.monprojetsup.eleve.domain.entity.ModificationProfilEleve
import fr.gouv.monprojetsup.eleve.domain.entity.ParametresPourRecupererToken
import fr.gouv.monprojetsup.eleve.domain.entity.VoeuFavori
import fr.gouv.monprojetsup.eleve.entity.CommunesFavorites
import fr.gouv.monprojetsup.eleve.usecase.MiseAJourEleveService
import fr.gouv.monprojetsup.eleve.usecase.MiseAJourIdParcoursupService
import fr.gouv.monprojetsup.eleve.usecase.RecupererAssociationFormationsVoeuxService
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.referentiel.domain.entity.SituationAvanceeProjetSup
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
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

    @MockBean
    lateinit var recupererAssociationFormationsVoeuxService:
        RecupererAssociationFormationsVoeuxService

    @MockBean
    lateinit var miseAJourIdParcoursupService: MiseAJourIdParcoursupService

    @Nested
    inner class `Quand on appelle la route POST profil` {
        private val modificationProfilEleve =
            ModificationProfilEleve(
                situation = SituationAvanceeProjetSup.PROJET_PRECIS,
                classe = ChoixNiveau.TERMINALE,
                baccalaureat = "Générale",
                dureeEtudesPrevue = ChoixDureeEtudesPrevue.LONGUE,
                alternance = ChoixAlternance.INTERESSE,
                communesFavorites = listOf(CommunesFavorites.PARIS15EME),
                specialites = listOf("1054"),
                centresInterets = listOf("T_IDEO2_4812"),
                moyenneGenerale = 14f,
                metiersFavoris = listOf("MET_456"),
                formationsFavorites =
                    listOf(
                        FormationFavorite(
                            idFormation = "fl1234",
                            niveauAmbition = 1,
                            priseDeNote = null,
                        ),
                        FormationFavorite(
                            idFormation = "fl5678",
                            niveauAmbition = 3,
                            priseDeNote = "Ma formation préférée",
                        ),
                    ),
                domainesInterets = listOf("T_ITM_1054", "T_ITM_1351"),
                corbeilleFormations = listOf("fl0001", "fl0002"),
                voeuxFavoris =
                    listOf(
                        VoeuFavori("ta1", false),
                        VoeuFavori("ta2", false),
                    ),
            )

        private val nouveauProfil =
            ProfilEleve.AvecProfilExistant(
                id = ID_ELEVE,
                situation = SituationAvanceeProjetSup.PROJET_PRECIS,
                classe = ChoixNiveau.TERMINALE,
                baccalaureat = "Générale",
                dureeEtudesPrevue = ChoixDureeEtudesPrevue.LONGUE,
                alternance = ChoixAlternance.INTERESSE,
                communesFavorites = listOf(CommunesFavorites.PARIS15EME),
                specialites = listOf("1054"),
                centresInterets = listOf("T_IDEO2_4812"),
                moyenneGenerale = 14f,
                metiersFavoris = listOf("MET_456"),
                formationsFavorites =
                    listOf(
                        FormationFavorite(
                            idFormation = "fl1234",
                            niveauAmbition = 1,
                            priseDeNote = null,
                        ),
                        FormationFavorite(
                            idFormation = "fl5678",
                            niveauAmbition = 3,
                            priseDeNote = "Ma formation préférée",
                        ),
                    ),
                domainesInterets = listOf("T_ITM_1054", "T_ITM_1351"),
                corbeilleFormations = listOf("fl0001", "fl0002"),
                voeuxFavoris =
                    listOf(
                        VoeuFavori("ta1", false),
                        VoeuFavori("ta2", false),
                    ),
                compteParcoursupLie = false,
            )

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si élève existe et que le service réussi, doit retourner 200`() {
            // Given
            given(miseAJourEleveService.mettreAJourUnProfilEleve(modificationProfilEleve, unProfilEleve)).willReturn(nouveauProfil)

            // When & Then
            mvc.perform(
                post("/api/v1/profil").contentType(MediaType.APPLICATION_JSON).content(creerRequeteNouveauProfilJson())
                    .accept(MediaType.APPLICATION_JSON),
            ).andDo(print()).andExpect(status().isOk)
                .andExpect(content().json(creerReponseNouveauProfilJson()))
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si élève existe et que le service réussi pour une modification vide, doit retourner 200`() {
            // Given
            val modificationProfilEleveVide = ModificationProfilEleve()
            given(miseAJourEleveService.mettreAJourUnProfilEleve(modificationProfilEleveVide, unProfilEleve)).willReturn(unProfilEleve)

            // When & Then
            mvc.perform(
                post("/api/v1/profil").contentType(MediaType.APPLICATION_JSON).content("{}")
                    .accept(MediaType.APPLICATION_JSON),
            ).andDo(print()).andExpect(status().isOk)
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
                              "priseDeNote": null
                            },
                            {
                              "idFormation": "fl5678",
                              "niveauAmbition": 3,
                              "priseDeNote": "Ma formation préférée"
                            }
                          ],
                          "corbeilleFormations": [
                            "fl0010",
                            "fl0012"
                          ],
                          "compteParcoursupAssocie": true,
                          "voeuxFavoris": [
                            {
                              "idVoeu": "ta1",
                              "estFavoriParcoursup": true
                            },
                            {
                              "idVoeu": "ta77",
                              "estFavoriParcoursup": false
                            }
                          ]
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEleve(idEleve = "61a3b1a9-03dd-4fc6-9549-c0e5d1403214")
        @Test
        fun `si élève n'existe pas et que le service réussi, doit retourner 200`() {
            // Given
            val idProfilInconnu = "61a3b1a9-03dd-4fc6-9549-c0e5d1403214"
            val profilSansCompte = ProfilEleve.SansCompte(id = idProfilInconnu)
            given(recupererEleveService.recupererEleve(id = idProfilInconnu)).willReturn(profilSansCompte)
            given(miseAJourEleveService.mettreAJourUnProfilEleve(modificationProfilEleve, profilSansCompte)).willReturn(nouveauProfil)

            // When & Then
            mvc.perform(
                post("/api/v1/profil").contentType(MediaType.APPLICATION_JSON).content(creerRequeteNouveauProfilJson())
                    .accept(MediaType.APPLICATION_JSON),
            ).andDo(print()).andExpect(status().isOk)
                .andExpect(content().json(creerReponseNouveauProfilJson()))
        }

        @ConnecteAvecUnEnseignant(idEnseignant = "49e8e8c2-5eec-4eae-a90d-992225bbea1b")
        @Test
        fun `si enseignant existe, doit retourner 200`() {
            // Given
            val unProfilEnseignant =
                ProfilEleve.AvecProfilExistant(
                    id = ID_ENSEIGNANT,
                    situation = SituationAvanceeProjetSup.PROJET_PRECIS,
                    classe = ChoixNiveau.TERMINALE,
                    baccalaureat = "NC",
                    dureeEtudesPrevue = ChoixDureeEtudesPrevue.INDIFFERENT,
                    alternance = ChoixAlternance.PAS_INTERESSE,
                    communesFavorites = listOf(CommunesFavorites.PARIS15EME, CommunesFavorites.MARSEILLE),
                    specialites = listOf("mat1001", "mat1049"),
                    centresInterets = null,
                    moyenneGenerale = 4.9f,
                    metiersFavoris = null,
                    formationsFavorites =
                        listOf(
                            FormationFavorite(
                                idFormation = "fl1234",
                                niveauAmbition = 1,
                                priseDeNote = null,
                            ),
                            FormationFavorite(
                                idFormation = "fl5678",
                                niveauAmbition = 3,
                                priseDeNote = "Ma formation préférée",
                            ),
                        ),
                    domainesInterets = listOf("T_ITM_1054", "T_ITM_1534", "T_ITM_1248", "T_ITM_1351"),
                    corbeilleFormations = listOf("fl0012"),
                    compteParcoursupLie = false,
                    voeuxFavoris =
                        listOf(
                            VoeuFavori("ta1", true),
                            VoeuFavori("ta77", false),
                        ),
                )
            given(recupererEleveService.recupererEleve(id = ID_ENSEIGNANT)).willReturn(unProfilEnseignant)
            given(miseAJourEleveService.mettreAJourUnProfilEleve(modificationProfilEleve, unProfilEnseignant)).willReturn(nouveauProfil)

            // When & Then
            mvc.perform(
                post("/api/v1/profil").contentType(MediaType.APPLICATION_JSON).content(creerRequeteNouveauProfilJson())
                    .accept(MediaType.APPLICATION_JSON),
            ).andDo(print()).andExpect(status().isOk)
                .andExpect(content().json(creerReponseNouveauProfilJson()))
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si l'élève existe mais que le service échoue, doit retourner 400`() {
            // Given
            val exception = MonProjetSupBadRequestException("FORMATIONS_NON_RECONNUES", "Une ou plusieurs des formations n'existent pas")
            given(
                miseAJourEleveService.mettreAJourUnProfilEleve(miseAJourDuProfil = modificationProfilEleve, profilActuel = unProfilEleve),
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
                  "priseDeNote": null
                },
                {
                  "idFormation": "fl5678",
                  "niveauAmbition": 3,
                  "priseDeNote": "Ma formation préférée"
                }
              ],
              "voeuxFavoris": [
                {
                    "idVoeu": "ta1",
                    "estFavoriParcoursup": false
                },
                {
                    "idVoeu": "ta2",
                    "estFavoriParcoursup": false
                }
              ],
              "corbeilleFormations": [
                "fl0001",
                "fl0002"
              ]
            }
            """.trimIndent()

        private fun creerReponseNouveauProfilJson(
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
              "moyenneGenerale": 14.0,
              "formationsFavorites": [
                {
                  "idFormation": "fl1234",
                  "niveauAmbition": 1,
                  "priseDeNote": null
                },
                {
                  "idFormation": "fl5678",
                  "niveauAmbition": 3,
                  "priseDeNote": "Ma formation préférée"
                }
              ],
              "corbeilleFormations": [
                "fl0001",
                "fl0002"
              ],
              "compteParcoursupAssocie": false,
              "voeuxFavoris": [
                {
                  "idVoeu": "ta1",
                  "estFavoriParcoursup": false
                },
                {
                  "idVoeu": "ta2",
                  "estFavoriParcoursup": false
                }
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
            given(
                recupererAssociationFormationsVoeuxService.recupererVoeuxFavoris(unProfilEleve),
            ).willReturn(
                listOf(
                    VoeuFavori("ta1", true),
                    VoeuFavori("ta2", false),
                ),
            )

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
                              "priseDeNote": null
                            },
                            {
                              "idFormation": "fl5678",
                              "niveauAmbition": 3,
                              "priseDeNote": "Ma formation préférée"
                            }
                          ],
                          "corbeilleFormations": [
                            "fl0010",
                            "fl0012"
                          ],
                          "voeuxFavoris": [
                              { 
                                "idVoeu": "ta1",
                                "estFavoriParcoursup": true
                              },
                              { 
                                "idVoeu": "ta2",
                                "estFavoriParcoursup": false
                              }
                          ]
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEleve(idEleve = "123f627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si l'élève n'existe pas, doit renvoyer 404`() {
            // Given
            val idProfilInconnu = "123f627c-36dd-4df5-897b-159443a6d49c"
            val profilSansCompte = ProfilEleve.SansCompte(id = idProfilInconnu)
            given(recupererEleveService.recupererEleve(id = idProfilInconnu)).willReturn(profilSansCompte)

            // When & Then
            mvc.perform(get("/api/v1/profil")).andDo(print())
                .andExpect(status().isNotFound)
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "type": "about:blank",
                          "title": "ELEVE_SANS_COMPTE",
                          "status": 404,
                          "detail": "L'élève connecté n'a pas encore crée son compte",
                          "instance": "/api/v1/profil"
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEnseignant(idEnseignant = "49e8e8c2-5eec-4eae-a90d-992225bbea1b")
        @Test
        fun `si enseignant, doit retourner 200`() {
            val voeuxFavoris = listOf(VoeuFavori("ta1", true), VoeuFavori("ta2", false))
            // Given
            val unProfilEnseignant =
                ProfilEleve.AvecProfilExistant(
                    id = ID_ENSEIGNANT,
                    situation = SituationAvanceeProjetSup.PROJET_PRECIS,
                    classe = ChoixNiveau.TERMINALE,
                    baccalaureat = "NC",
                    dureeEtudesPrevue = ChoixDureeEtudesPrevue.INDIFFERENT,
                    alternance = ChoixAlternance.PAS_INTERESSE,
                    communesFavorites = listOf(CommunesFavorites.PARIS15EME, CommunesFavorites.MARSEILLE),
                    specialites = listOf("mat1001", "mat1049"),
                    centresInterets = null,
                    moyenneGenerale = 4.9f,
                    metiersFavoris = null,
                    formationsFavorites =
                        listOf(
                            FormationFavorite(
                                idFormation = "fl1234",
                                niveauAmbition = 1,
                                priseDeNote = null,
                            ),
                            FormationFavorite(
                                idFormation = "fl5678",
                                niveauAmbition = 3,
                                priseDeNote = "Ma formation préférée",
                            ),
                        ),
                    domainesInterets = listOf("T_ITM_1054", "T_ITM_1534", "T_ITM_1248", "T_ITM_1351"),
                    corbeilleFormations = listOf("fl0012"),
                    compteParcoursupLie = true,
                    voeuxFavoris = voeuxFavoris,
                )
            given(recupererEleveService.recupererEleve(id = ID_ENSEIGNANT)).willReturn(unProfilEnseignant)
            given(
                recupererAssociationFormationsVoeuxService.recupererVoeuxFavoris(unProfilEnseignant),
            ).willReturn(voeuxFavoris)

            // When & Then
            mvc.perform(get("/api/v1/profil")).andDo(print())
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "situation": "projet_precis",
                          "classe": "terminale",
                          "baccalaureat": "NC",
                          "specialites": [
                            "mat1001",
                            "mat1049"
                          ],
                          "domaines": [
                            "T_ITM_1054",
                            "T_ITM_1534",
                            "T_ITM_1248",
                            "T_ITM_1351"
                          ],
                          "centresInterets": null,
                          "metiersFavoris": null,
                          "dureeEtudesPrevue": "indifferent",
                          "alternance": "pas_interesse",
                          "communesFavorites": [
                            {
                              "codeInsee": "75115",
                              "nom": "Paris",
                              "latitude": 48.851227,
                              "longitude": 2.2885659
                            },
                            {
                              "codeInsee": "13055",
                              "nom": "Marseille",
                              "latitude": 43.3,
                              "longitude": 5.4
                            }
                          ],
                          "moyenneGenerale": 4.9,
                          "formationsFavorites": [
                            {
                              "idFormation": "fl1234",
                              "niveauAmbition": 1,
                              "priseDeNote": null
                            },
                            {
                              "idFormation": "fl5678",
                              "niveauAmbition": 3,
                              "priseDeNote": "Ma formation préférée"
                            }
                          ],
                          "voeuxFavoris": [
                              { 
                                "idVoeu": "ta1",
                                "estFavoriParcoursup": true
                              },
                              { 
                                "idVoeu": "ta2",
                                "estFavoriParcoursup": false
                              }
                          ],
                          "corbeilleFormations": [
                            "fl0012"
                          ]
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteSansId
        @Test
        fun `si token, doit retourner 403`() {
            // When & Then
            mvc.perform(get("/api/v1/profil")).andExpect(status().isForbidden)
        }
    }

    @Nested
    inner class `Quand on appelle la route POST profil parcoursup` {
        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si l'élève existe et le service réussi, doit retourner 204`() {
            // When & Then
            val bodyEntree =
                """
                {
                  "codeVerifier": "code_verifier",
                  "code": "code",
                  "redirectUri": "redirect_uri"
                }
                """.trimIndent()
            mvc.perform(post("/api/v1/profil/parcoursup").contentType(MediaType.APPLICATION_JSON).content(bodyEntree))
                .andExpect(status().isNoContent)
        }

        @ConnecteAvecUnEnseignant(idEnseignant = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si l'enseignant existe et le service réussi, doit retourner 204`() {
            // When & Then
            val bodyEntree =
                """
                {
                  "codeVerifier": "code_verifier",
                  "code": "code",
                  "redirectUri": "redirect_uri"
                }
                """.trimIndent()
            mvc.perform(post("/api/v1/profil/parcoursup").contentType(MediaType.APPLICATION_JSON).content(bodyEntree))
                .andExpect(status().isNoContent)
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si l'élève existe mais que le service renvoie MonProjetSupNotFoundException, doit retourner 404`() {
            // Given
            val exception =
                MonProjetSupNotFoundException(
                    code = "ELEVE_NOT_FOUND",
                    msg =
                        "L'élève avec l'id adcf627c-36dd-4df5-897b-159443a6d49c tente de sauvegarder son id parcoursup " +
                            "mais il n'est pas encore sauvegardé en BDD",
                )
            val parametresPourRecupererToken =
                ParametresPourRecupererToken(
                    codeVerifier = "code_verifier",
                    code = "code",
                    redirectUri = "redirect_uri",
                )
            given(miseAJourIdParcoursupService.mettreAJourIdParcoursup(unProfilEleve, parametresPourRecupererToken)).willThrow(exception)

            // When & Then
            val bodyEntree =
                """
                {
                  "codeVerifier": "code_verifier",
                  "code": "code",
                  "redirectUri": "redirect_uri"
                }
                """.trimIndent()
            mvc.perform(post("/api/v1/profil/parcoursup").contentType(MediaType.APPLICATION_JSON).content(bodyEntree))
                .andExpect(status().isNotFound)
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "type": "about:blank",
                          "title": "ELEVE_NOT_FOUND",
                          "status": 404,
                          "detail": "L'élève avec l'id adcf627c-36dd-4df5-897b-159443a6d49c tente de sauvegarder son id parcoursup mais il n'est pas encore sauvegardé en BDD",
                          "instance": "/api/v1/profil/parcoursup"
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si l'élève existe mais que le service renvoie MonProjetSupBadRequestException, doit retourner 400`() {
            // Given
            val exception = MonProjetSupBadRequestException(code = "JWT_NON_VALIDE", msg = "JWT envoyé invalide")
            val parametresPourRecupererToken =
                ParametresPourRecupererToken(
                    codeVerifier = "code_verifier",
                    code = "code",
                    redirectUri = "redirect_uri",
                )
            given(miseAJourIdParcoursupService.mettreAJourIdParcoursup(unProfilEleve, parametresPourRecupererToken)).willThrow(exception)

            // When & Then
            val bodyEntree =
                """
                {
                  "codeVerifier": "code_verifier",
                  "code": "code",
                  "redirectUri": "redirect_uri"
                }
                """.trimIndent()
            mvc.perform(post("/api/v1/profil/parcoursup").contentType(MediaType.APPLICATION_JSON).content(bodyEntree))
                .andExpect(status().isBadRequest)
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "type": "about:blank",
                          "title": "JWT_NON_VALIDE",
                          "status": 400,
                          "detail": "JWT envoyé invalide",
                          "instance": "/api/v1/profil/parcoursup"
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEleve(idEleve = "d26da5c2-c38d-4c07-9ef3-9da2443846df")
        @Test
        fun `si l'élève n'a pas encore crée de profil, doit retourner 404`() {
            // Given
            val id = "d26da5c2-c38d-4c07-9ef3-9da2443846df"
            given(recupererEleveService.recupererEleve(id)).willReturn(ProfilEleve.SansCompte(id))

            // When & Then
            val bodyEntree =
                """
                {
                  "codeVerifier": "code_verifier",
                  "code": "code",
                  "redirectUri": "redirect_uri"
                }
                """.trimIndent()
            mvc.perform(post("/api/v1/profil/parcoursup").contentType(MediaType.APPLICATION_JSON).content(bodyEntree))
                .andExpect(status().isNotFound)
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "type": "about:blank",
                          "title": "ELEVE_SANS_COMPTE",
                          "status": 404,
                          "detail": "L'élève connecté n'a pas encore crée son compte",
                          "instance": "/api/v1/profil/parcoursup"
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteSansId
        @Test
        fun `si utilisateur sans id, doit retourner 404`() {
            // When & Then
            val bodyEntree =
                """
                {
                  "codeVerifier": "code_verifier",
                  "code": "code",
                  "redirectUri": "redirect_uri"
                }
                """.trimIndent()
            mvc.perform(post("/api/v1/profil/parcoursup").contentType(MediaType.APPLICATION_JSON).content(bodyEntree))
                .andExpect(status().isForbidden)
        }
    }
}
