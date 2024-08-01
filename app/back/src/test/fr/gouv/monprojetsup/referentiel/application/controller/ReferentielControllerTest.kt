package fr.gouv.monprojetsup.referentiel.application.controller

import fr.gouv.monprojetsup.commun.ConnecteAvecUnEleve
import fr.gouv.monprojetsup.commun.ConnecteAvecUnEnseignant
import fr.gouv.monprojetsup.commun.ConnecteSansId
import fr.gouv.monprojetsup.commun.application.controller.ControllerTest
import fr.gouv.monprojetsup.referentiel.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.referentiel.domain.entity.CategorieDomaine
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.referentiel.domain.entity.Domaine
import fr.gouv.monprojetsup.referentiel.domain.entity.InteretCategorie
import fr.gouv.monprojetsup.referentiel.domain.entity.InteretSousCategorie
import fr.gouv.monprojetsup.referentiel.domain.entity.Referentiel
import fr.gouv.monprojetsup.referentiel.domain.entity.SituationAvanceeProjetSup
import fr.gouv.monprojetsup.referentiel.domain.entity.Specialite
import fr.gouv.monprojetsup.referentiel.usecase.ReferentielService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [ReferentielController::class])
class ReferentielControllerTest(
    @Autowired val mvc: MockMvc,
) : ControllerTest() {
    @MockBean
    lateinit var referentielService: ReferentielService

    private val contentReferentiel =
        """
        {
          "situations": [
            "aucune_idee",
            "quelques_pistes",
            "projet_precis"
          ],
          "choixNiveau": [
            "seconde",
            "premiere",
            "terminale",
            "NC"
          ],
          "choixAlternance": [
            "pas_interesse",
            "indifferent",
            "interesse",
            "tres_interesse",
            "NC"
          ],
          "choixDureeEtudesPrevue": [
            "indifferent",
            "courte",
            "longue",
            "aucune_idee",
            "NC"
          ],
          "baccalaureatsAvecLeurSpecialites": [
            {
              "baccalaureat": {
                "id": "Professionnel",
                "nom": "Série Pro"
              },
              "specialites": [
                {
                  "id": "4",
                  "nom": "Sciences de l'ingénieur"
                },
                {
                  "id": "1006",
                  "nom": "Economie et gestion hôtelière"
                }
              ]
            },
            {
              "baccalaureat": {
                "id": "Général",
                "nom": "Série Générale"
              },
              "specialites": [
                {
                  "id": "4",
                  "nom": "Sciences de l'ingénieur"
                },
                {
                  "id": "1040",
                  "nom": "Physique-Chimie et Mathématiques"
                }
              ]
            }
          ],
          "categoriesDInteretsAvecLeursSousCategories": [
            {
              "categorieInteret": {
                "id": "decouvrir_monde",
                "nom": "Découvrir le monde",
                "emoji": "\uD83C\uDF0E"
              },
              "sousCategoriesInterets": [
                {
                  "id": "voyage",
                  "nom": "Voyager",
                  "emoji": "\uD83D\uDE85"
                },
                {
                  "id": "linguistique",
                  "nom": "Apprendre de nouvelles langues",
                  "emoji": "\uD83C\uDDEC\uD83C\uDDE7"
                }
              ]
            },
            {
              "categorieInteret": {
                "id": "rechercher",
                "nom": "Découvrir, enquêter et rechercher",
                "emoji": "\uD83E\uDDD0"
              },
              "sousCategoriesInterets": []
            }
          ],
          "categoriesDomaineAvecLeursDomaines": [
            {
              "categorieDomaine": {
                "id": "agriculture_alimentaire",
                "nom": "Agriculture et Alimentation",
                "emoji": "\uD83E\uDD55"
              },
              "domaines": [
                {
                  "id": "animaux",
                  "nom": "Soins aux animaux",
                  "emoji": "\uD83D\uDC2E"
                },
                {
                  "id": "agroequipement",
                  "nom": "Agroéquipement",
                  "emoji": "\uD83D\uDE9C"
                }
              ]
            },
            {
              "categorieDomaine": {
                "id": "commerce",
                "nom": "Commerce",
                "emoji": "\uD83C\uDFE2"
              },
              "domaines": []
            }
          ]
        }
        """.trimIndent()

    @BeforeEach
    fun setupGiven() {
        // Given
        val baccalaureatsAvecSpecialites =
            mapOf(
                Baccalaureat(
                    id = "Professionnel",
                    nom = "Série Pro",
                    idExterne = "P",
                ) to
                    listOf(
                        Specialite(id = "4", label = "Sciences de l'ingénieur"),
                        Specialite(id = "1006", label = "Economie et gestion hôtelière"),
                    ),
                Baccalaureat(
                    id = "Général",
                    nom = "Série Générale",
                    idExterne = "Générale",
                ) to
                    listOf(
                        Specialite(id = "4", label = "Sciences de l'ingénieur"),
                        Specialite(id = "1040", label = "Physique-Chimie et Mathématiques"),
                    ),
            )
        val toutesLesCategoriesEtSousCategoriesDInteret =
            mapOf(
                InteretCategorie(id = "decouvrir_monde", nom = "Découvrir le monde", emoji = "🌎") to
                    listOf(
                        InteretSousCategorie(id = "voyage", nom = "Voyager", emoji = "🚅"),
                        InteretSousCategorie(id = "linguistique", nom = "Apprendre de nouvelles langues", emoji = "🇬🇧"),
                    ),
                InteretCategorie(id = "rechercher", nom = "Découvrir, enquêter et rechercher", emoji = "\uD83E\uDDD0") to emptyList(),
            )
        val categorieDomaineAvecLeursDomaines =
            mapOf(
                CategorieDomaine(
                    id = "agriculture_alimentaire",
                    nom = "Agriculture et Alimentation",
                    emoji = "🥕",
                ) to
                    listOf(
                        Domaine(id = "animaux", nom = "Soins aux animaux", emoji = "\uD83D\uDC2E"),
                        Domaine(id = "agroequipement", nom = "Agroéquipement", emoji = "\uD83D\uDE9C"),
                    ),
                CategorieDomaine(
                    id = "commerce",
                    nom = "Commerce",
                    emoji = "\uD83C\uDFE2",
                ) to emptyList(),
            )
        val referentiel =
            Referentiel(
                situations =
                    listOf(
                        SituationAvanceeProjetSup.AUCUNE_IDEE,
                        SituationAvanceeProjetSup.QUELQUES_PISTES,
                        SituationAvanceeProjetSup.PROJET_PRECIS,
                    ),
                baccalaureatsAvecLeursSpecialites = baccalaureatsAvecSpecialites,
                choixNiveau =
                    listOf(
                        ChoixNiveau.SECONDE,
                        ChoixNiveau.PREMIERE,
                        ChoixNiveau.TERMINALE,
                        ChoixNiveau.NON_RENSEIGNE,
                    ),
                choixAlternance =
                    listOf(
                        ChoixAlternance.PAS_INTERESSE,
                        ChoixAlternance.INDIFFERENT,
                        ChoixAlternance.INTERESSE,
                        ChoixAlternance.TRES_INTERESSE,
                        ChoixAlternance.NON_RENSEIGNE,
                    ),
                choixDureeEtudesPrevue =
                    listOf(
                        ChoixDureeEtudesPrevue.INDIFFERENT,
                        ChoixDureeEtudesPrevue.COURTE,
                        ChoixDureeEtudesPrevue.LONGUE,
                        ChoixDureeEtudesPrevue.AUCUNE_IDEE,
                        ChoixDureeEtudesPrevue.NON_RENSEIGNE,
                    ),
                categoriesDInteretsAvecLeursSousCategories = toutesLesCategoriesEtSousCategoriesDInteret,
                categoriesDomaineAvecLeursDomaines = categorieDomaineAvecLeursDomaines,
            )
        `when`(referentielService.recupererReferentiel()).thenReturn(referentiel)
    }

    @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
    @Test
    fun `si connecté avec un élève, doit retourner 200 avec le referentiel du parcours d'inscription`() {
        // When & Then
        mvc.perform(
            get("/api/v1/referentiel").contentType(MediaType.APPLICATION_JSON),
        ).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(contentReferentiel))
    }

    @ConnecteAvecUnEnseignant(idEnseignant = "adcf627c-36dd-4df5-897b-159443a6d49c")
    @Test
    fun `si connecté avec un enseignant, doit retourner 200 avec le referentiel du parcours d'inscription`() {
        // When & Then
        mvc.perform(
            get("/api/v1/referentiel").contentType(MediaType.APPLICATION_JSON),
        ).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(contentReferentiel))
    }

    @ConnecteSansId
    @Test
    fun `si connecté avec un token, doit retourner 200 avec le referentiel du parcours d'inscription`() {
        // When & Then
        mvc.perform(
            get("/api/v1/referentiel").contentType(MediaType.APPLICATION_JSON),
        ).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(contentReferentiel))
    }
}
