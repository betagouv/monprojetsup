package fr.gouv.monprojetsup.authentification.filter

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.ConnecteAvecUnEleve
import fr.gouv.monprojetsup.commun.ConnecteAvecUnEnseignant
import fr.gouv.monprojetsup.commun.ConnecteSansId
import fr.gouv.monprojetsup.commun.application.controller.ControllerTest
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

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
class IdentificationFilterTest(
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
                            "voeuxChoisis": [],
                            "priseDeNote": null
                          },
                          {
                            "idFormation": "fl5678",
                            "niveauAmbition": 3,
                            "voeuxChoisis": [
                              "ta1",
                              "ta2"
                            ],
                            "priseDeNote": "Mon voeu préféré"
                          }
                        ],
                        "moyenneGenerale": 14.0,
                        "corbeilleFormations": [
                          "fl0010",
                          "fl0012"
                        ]
                      },
                      "authorities": [
                        {
                          "authority": "ELEVE_AUTHENTIFIE"
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
        val uuid = UUID.fromString("40422ae5-f535-4f9a-8a1f-9e24978c2b14")
        given(eleveRepository.recupererUnEleve(uuid)).willReturn(ProfilEleve.Inconnu(uuid))

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
                          "authority": "ELEVE_AUTHENTIFIE"
                        }
                      ],
                      "isAuthenticated": true
                    }
                    """.trimIndent(),
                ),
            )
    }

    @ConnecteAvecUnEnseignant(idEnseignant = "590a6ada-3134-49ce-88b2-f894fc485670")
    @Test
    fun `si connecté avec un professeur, doit retourner 200 avec le détail de ses infos et ne pas appeler le repo eleve`() {
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
                          "authority": "ENSEIGNANT_AUTHENTIFIE"
                        }
                      ],
                      "isAuthenticated": true
                    }
                    """.trimIndent(),
                ),
            )

        then(eleveRepository).shouldHaveNoInteractions()
    }

    @ConnecteSansId
    @Test
    fun `si connecté sans id dans le JWT, doit retourner 200 et ne pas appeler le repo eleve`() {
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
        then(eleveRepository).shouldHaveNoInteractions()
    }

    @Test
    fun `si pas connecté, doit retourner 401 avec body vide`() {
        // When & Then
        mvc.perform(get("/test")).andExpect(status().isUnauthorized)
    }
}
