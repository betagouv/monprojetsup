package fr.gouv.monprojetsup.authentification.filter

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.ConnecteAvecUnEleve
import fr.gouv.monprojetsup.commun.ConnecteAvecUnEnseignant
import fr.gouv.monprojetsup.commun.ConnecteSansId
import fr.gouv.monprojetsup.commun.application.controller.ControllerTest
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupServiceUnavailableException
import fr.gouv.monprojetsup.parametre.domain.entity.Parametre
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("test")
@RestController
class IdentificationMockController {
    @GetMapping
    fun getTest(): ResponseEntity<IdentificationMockDTO> {
        val authentification = SecurityContextHolder.getContext().authentication
        val dto =
            IdentificationMockDTO(
                principal = authentification.principal,
                authorities = authentification.authorities,
                isAuthenticated = authentification.isAuthenticated,
            )
        return ResponseEntity<IdentificationMockDTO>(dto, HttpStatus.OK)
    }
}

data class IdentificationMockDTO(
    val principal: Any,
    val authorities: MutableCollection<out GrantedAuthority>,
    val isAuthenticated: Boolean,
)

@WebMvcTest(controllers = [IdentificationMockController::class])
class FilterTest(
    @Autowired val mvc: MockMvc,
) : ControllerTest() {
    @ConnecteAvecUnEleve("adcf627c-36dd-4df5-897b-159443a6d49c")
    @Test
    fun `si connecté avec un élève, doit retourner 200 avec le détail des infos de l'élève et son authorité`() {
        // When & Then
        mvc.perform(get("/test")).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                content().json(
                    """
                    {
                      "principal": {
                        "id": "adcf627c-36dd-4df5-897b-159443a6d49c",
                        "situation": "aucune_idee",
                        "classe": "terminale",
                        "baccalaureat": "Générale",
                        "specialites": [
                          "1056",
                          "1054"
                        ],
                        "domainesInterets": [
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
                        "moyenneGenerale": 14.0,
                        "corbeilleFormations": [
                          "fl0010",
                          "fl0012"
                        ],
                        "voeuxFavoris":[
                            {"idVoeu":"ta1","estFavoriParcoursup":true},
                            {"idVoeu":"ta77","estFavoriParcoursup":false}
                        ]
                      },
                      "authorities": [
                        {
                          "authority": "UTILISATEUR_AUTHENTIFIE"
                        }
                      ],
                      "isAuthenticated": true
                    }
                    """.trimIndent(),
                ),
            )
    }

    @ConnecteAvecUnEleve(idEleve = "40422ae5-f535-4f9a-8a1f-9e24978c2b14")
    @Test
    fun `si connecté avec un élève mais ne le reconnait pas, doit retourner 200 avec le détail des infos de l'élève et son authorité`() {
        // Given
        val id = "40422ae5-f535-4f9a-8a1f-9e24978c2b14"
        given(recupererEleveService.recupererEleve(id)).willReturn(ProfilEleve.SansCompte(id))

        // When & Then
        mvc.perform(get("/test")).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                content().json(
                    """
                    {
                      "principal": {
                        "id": "40422ae5-f535-4f9a-8a1f-9e24978c2b14"
                      },
                      "authorities": [
                        {
                          "authority": "UTILISATEUR_AUTHENTIFIE"
                        }
                      ],
                      "isAuthenticated": true
                    }
                    """.trimIndent(),
                ),
            )
    }

    @ConnecteAvecUnEnseignant(idEnseignant = "49e8e8c2-5eec-4eae-a90d-992225bbea1b")
    @Test
    fun `si connecté avec un professeur, doit retourner 200 avec le détail de ses infos`() {
        // When & Then
        mvc.perform(get("/test")).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                content().json(
                    """
                    {
                      "principal": {},
                      "authorities": [
                        {
                          "authority": "UTILISATEUR_AUTHENTIFIE"
                        }
                      ],
                      "isAuthenticated": true
                    }
                    """.trimIndent(),
                ),
            )
    }

    @ConnecteSansId
    @Test
    fun `si connecté sans id dans le JWT, doit retourner 403 et ne pas appeler le repo eleve`() {
        // When & Then
        mvc.perform(get("/test")).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                content().json(
                    """
                    {
                      "principal": {},
                      "authorities": [],
                      "isAuthenticated": true
                    }
                    """.trimIndent(),
                ),
            )
        then(recupererEleveService).shouldHaveNoInteractions()
    }

    @Test
    fun `si pas connecté, doit retourner 401 avec body vide`() {
        // When & Then
        mvc.perform(get("/test")).andExpect(status().isUnauthorized)
        then(recupererEleveService).shouldHaveNoInteractions()
    }

    @ConnecteAvecUnEleve("adcf627c-36dd-4df5-897b-159443a6d49c")
    @Test
    fun `si connecté avec un élève reconnu mais que ETL en cours, doit retourner 503`() {
        // Given
        given(parametreRepository.estActif(Parametre.ETL_EN_COURS)).willReturn(true)

        // When & Then
        mvc.perform(get("/test")).andDo(MockMvcResultHandlers.print()).andExpect(status().isServiceUnavailable)
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(
                content().json(
                    """
                    {
                      "type": "about:blank",
                      "title": "ETL_EN_COURS",
                      "status": 503,
                      "detail": "Le service est indisponible pour cause de mise a jour de la BDD"
                    }
                    """.trimIndent(),
                ),
            )
        val exception =
            MonProjetSupServiceUnavailableException(
                code = "ETL_EN_COURS",
                msg = "Le service est indisponible pour cause de mise a jour de la BDD",
            )
        then(this.logger).should().logException(exception, 503)
    }
}
