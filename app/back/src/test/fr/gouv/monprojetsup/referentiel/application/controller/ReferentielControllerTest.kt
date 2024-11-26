package fr.gouv.monprojetsup.referentiel.application.controller

import fr.gouv.monprojetsup.commun.ConnecteAvecUnEleve
import fr.gouv.monprojetsup.commun.ConnecteAvecUnEnseignant
import fr.gouv.monprojetsup.commun.ConnecteSansId
import fr.gouv.monprojetsup.commun.Constantes.ANNEE_DONNEES_PARCOURSUP
import fr.gouv.monprojetsup.commun.application.controller.ControllerTest
import fr.gouv.monprojetsup.referentiel.domain.entity.AdmissionsParcoursup
import fr.gouv.monprojetsup.referentiel.domain.entity.AdmissionsParcoursup.PourcentagesPourChaqueMoyenneParBaccalaureat
import fr.gouv.monprojetsup.referentiel.domain.entity.AdmissionsParcoursup.PourcentagesPourChaqueMoyenneParBaccalaureat.PourcentagesMoyenne
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

    private val contenuReferentiel =
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
            "terminale"
          ],
          "choixAlternance": [
            "pas_interesse",
            "indifferent",
            "interesse",
            "tres_interesse"
          ],
          "choixDureeEtudesPrevue": [
            "indifferent",
            "courte",
            "longue",
            "aucune_idee"
          ],
          "baccalaureatsAvecLeurSpecialites": [
            {
              "baccalaureat": {
                "id": "P",
                "nom": "Bac Professionnel",
                "idCarteParcoursup": "3"
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
                "id": "Générale",
                "nom": "Bac Général",
                "idCarteParcoursup": "1"
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
                  "description": "Pour travailler dans le tourisme, l’hôtellerie, les transports, ou encore pour organiser des voyages et des séjours.",
                  "emoji": "\uD83D\uDE85"
                },
                {
                  "id": "linguistique",
                  "nom": "Apprendre de nouvelles langues",
                  "description": null,
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
                  "description": "Pour travailler dans les élevages ou la pêche, mais aussi apprendre à soigner les animaux, les nourrir et assurer leur bien-être.",
                  "emoji": "\uD83D\uDC2E"
                },
                {
                  "id": "agroequipement",
                  "nom": "Agroéquipement",
                  "description": null,
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
          ],
          "admissionsParcoursup": {
            "annee": "2024",
            "parBaccalaureat": [
              {
                "baccalaureat": {
                  "id": "Générale",
                  "nom": "Bac Général",
                  "idCarteParcoursup": "1"
                },
                "pourcentages": [
                  {
                    "note": 0.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 0.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 1.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 1.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 2.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 2.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 3.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 3.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 4.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 4.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 5.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 5.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 6.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 6.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 7.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 7.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 8.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 8.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 9.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 9.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 10.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 2
                  },
                  {
                    "note": 10.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 4
                  },
                  {
                    "note": 11.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 7
                  },
                  {
                    "note": 11.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 12
                  },
                  {
                    "note": 12.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 19
                  },
                  {
                    "note": 12.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 28
                  },
                  {
                    "note": 13.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 37
                  },
                  {
                    "note": 13.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 47
                  },
                  {
                    "note": 14.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 57
                  },
                  {
                    "note": 14.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 66
                  },
                  {
                    "note": 15.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 75
                  },
                  {
                    "note": 15.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 82
                  },
                  {
                    "note": 16.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 88
                  },
                  {
                    "note": 16.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 93
                  },
                  {
                    "note": 17.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 96
                  },
                  {
                    "note": 17.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 98
                  },
                  {
                    "note": 18.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 99
                  },
                  {
                    "note": 18.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 99
                  },
                  {
                    "note": 19.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 99
                  },
                  {
                    "note": 19.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 100
                  }
                ]
              },
              {
                "baccalaureat": {
                  "id": "NC",
                  "nom": "Non-communiqué",
                  "idCarteParcoursup": "0"
                },
                "pourcentages": [
                  {
                    "note": 0.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 0.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 1.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 1.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 2.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 2.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 3.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 3.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 4.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 4.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 5.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 5.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 6.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 6.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 7.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 7.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 8.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 8.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 9.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 9.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 1
                  },
                  {
                    "note": 10.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 3
                  },
                  {
                    "note": 10.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 6
                  },
                  {
                    "note": 11.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 10
                  },
                  {
                    "note": 11.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 17
                  },
                  {
                    "note": 12.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 25
                  },
                  {
                    "note": 12.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 34
                  },
                  {
                    "note": 13.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 44
                  },
                  {
                    "note": 13.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 55
                  },
                  {
                    "note": 14.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 64
                  },
                  {
                    "note": 14.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 73
                  },
                  {
                    "note": 15.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 80
                  },
                  {
                    "note": 15.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 86
                  },
                  {
                    "note": 16.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 91
                  },
                  {
                    "note": 16.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 95
                  },
                  {
                    "note": 17.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 97
                  },
                  {
                    "note": 17.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 98
                  },
                  {
                    "note": 18.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 99
                  },
                  {
                    "note": 18.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 99
                  },
                  {
                    "note": 19.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 99
                  },
                  {
                    "note": 19.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 100
                  }
                ]
              },
              {
                "baccalaureat": {
                  "id": "P",
                  "nom": "Bac Professionnel",
                  "idCarteParcoursup": "3"
                },
                "pourcentages": [
                  {
                    "note": 0.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 0.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 1.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 1.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 2.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 2.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 3.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 3.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 4.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 4.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 5.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 5.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 6.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 6.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 7.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 7.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 8.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 8.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 9.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 1
                  },
                  {
                    "note": 9.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 2
                  },
                  {
                    "note": 10.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 4
                  },
                  {
                    "note": 10.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 8
                  },
                  {
                    "note": 11.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 13
                  },
                  {
                    "note": 11.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 21
                  },
                  {
                    "note": 12.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 31
                  },
                  {
                    "note": 12.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 42
                  },
                  {
                    "note": 13.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 53
                  },
                  {
                    "note": 13.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 65
                  },
                  {
                    "note": 14.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 75
                  },
                  {
                    "note": 14.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 83
                  },
                  {
                    "note": 15.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 90
                  },
                  {
                    "note": 15.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 94
                  },
                  {
                    "note": 16.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 97
                  },
                  {
                    "note": 16.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 99
                  },
                  {
                    "note": 17.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 99
                  },
                  {
                    "note": 17.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 99
                  },
                  {
                    "note": 18.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 99
                  },
                  {
                    "note": 18.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 100
                  },
                  {
                    "note": 19.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 100
                  },
                  {
                    "note": 19.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 100
                  }
                ]
              },
              {
                "baccalaureat": {
                  "id": "STL",
                  "nom": "Bac STL",
                  "idCarteParcoursup": "2"
                },
                "pourcentages": [
                  {
                    "note": 0.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 0.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 1.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 1.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 2.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 2.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 3.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 3.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 4.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 4.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 5.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 5.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 6.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 6.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 7.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 7.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 8.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 8.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 0
                  },
                  {
                    "note": 9.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 1
                  },
                  {
                    "note": 9.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 3
                  },
                  {
                    "note": 10.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 7
                  },
                  {
                    "note": 10.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 12
                  },
                  {
                    "note": 11.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 19
                  },
                  {
                    "note": 11.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 28
                  },
                  {
                    "note": 12.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 38
                  },
                  {
                    "note": 12.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 48
                  },
                  {
                    "note": 13.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 59
                  },
                  {
                    "note": 13.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 69
                  },
                  {
                    "note": 14.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 78
                  },
                  {
                    "note": 14.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 85
                  },
                  {
                    "note": 15.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 90
                  },
                  {
                    "note": 15.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 94
                  },
                  {
                    "note": 16.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 97
                  },
                  {
                    "note": 16.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 98
                  },
                  {
                    "note": 17.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 99
                  },
                  {
                    "note": 17.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 99
                  },
                  {
                    "note": 18.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 99
                  },
                  {
                    "note": 18.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 100
                  },
                  {
                    "note": 19.0,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 100
                  },
                  {
                    "note": 19.5,
                    "pourcentageAdmisAyantCetteMoyenneOuMoins": 100
                  }
                ]
              }
            ]
          }
        }
        """.trimIndent()

    @BeforeEach
    fun setupGiven() {
        // Given
        val baccalaureatsAvecSpecialites =
            mapOf(
                baccalaureatPro to
                    listOf(
                        Specialite(id = "4", label = "Sciences de l'ingénieur"),
                        Specialite(id = "1006", label = "Economie et gestion hôtelière"),
                    ),
                baccalaureatGeneral to
                    listOf(
                        Specialite(id = "4", label = "Sciences de l'ingénieur"),
                        Specialite(id = "1040", label = "Physique-Chimie et Mathématiques"),
                    ),
            )
        val toutesLesCategoriesEtSousCategoriesDInteret =
            mapOf(
                InteretCategorie(
                    id = "decouvrir_monde",
                    nom = "Découvrir le monde",
                    emoji = "🌎",
                ) to
                    listOf(
                        InteretSousCategorie(
                            id = "voyage",
                            nom = "Voyager",
                            emoji = "🚅",
                            description =
                                "Pour travailler dans le tourisme, l’hôtellerie, les transports, ou encore pour " +
                                    "organiser des voyages et des séjours.",
                        ),
                        InteretSousCategorie(
                            id = "linguistique",
                            nom = "Apprendre de nouvelles langues",
                            emoji = "🇬🇧",
                            description = null,
                        ),
                    ),
                InteretCategorie(
                    id = "rechercher",
                    nom = "Découvrir, enquêter et rechercher",
                    emoji = "\uD83E\uDDD0",
                ) to emptyList(),
            )
        val categorieDomaineAvecLeursDomaines =
            mapOf(
                CategorieDomaine(
                    id = "agriculture_alimentaire",
                    nom = "Agriculture et Alimentation",
                    emoji = "🥕",
                ) to
                    listOf(
                        Domaine(
                            id = "animaux",
                            nom = "Soins aux animaux",
                            emoji = "\uD83D\uDC2E",
                            description =
                                "Pour travailler dans les élevages ou la pêche, mais aussi apprendre à soigner les " +
                                    "animaux, les nourrir et assurer leur bien-être.",
                        ),
                        Domaine(id = "agroequipement", nom = "Agroéquipement", emoji = "\uD83D\uDE9C", description = null),
                    ),
                CategorieDomaine(
                    id = "commerce",
                    nom = "Commerce",
                    emoji = "\uD83C\uDFE2",
                ) to emptyList(),
            )
        val admissionsParcoursup =
            AdmissionsParcoursup(
                annee = ANNEE_DONNEES_PARCOURSUP,
                parBaccalaureat =
                    listOf(
                        PourcentagesPourChaqueMoyenneParBaccalaureat(
                            baccalaureat = baccalaureatGeneral,
                            pourcentages =
                                listOf(
                                    PourcentagesMoyenne(note = 0.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 0.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 1.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 1.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 2.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 2.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 3.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 3.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 4.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 4.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 5.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 5.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 6.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 6.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 7.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 7.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 8.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 8.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 9.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 9.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 10.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 2),
                                    PourcentagesMoyenne(note = 10.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 4),
                                    PourcentagesMoyenne(note = 11.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 7),
                                    PourcentagesMoyenne(note = 11.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 12),
                                    PourcentagesMoyenne(note = 12.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 19),
                                    PourcentagesMoyenne(note = 12.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 28),
                                    PourcentagesMoyenne(note = 13.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 37),
                                    PourcentagesMoyenne(note = 13.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 47),
                                    PourcentagesMoyenne(note = 14.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 57),
                                    PourcentagesMoyenne(note = 14.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 66),
                                    PourcentagesMoyenne(note = 15.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 75),
                                    PourcentagesMoyenne(note = 15.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 82),
                                    PourcentagesMoyenne(note = 16.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 88),
                                    PourcentagesMoyenne(note = 16.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 93),
                                    PourcentagesMoyenne(note = 17.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 96),
                                    PourcentagesMoyenne(note = 17.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 98),
                                    PourcentagesMoyenne(note = 18.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 99),
                                    PourcentagesMoyenne(note = 18.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 99),
                                    PourcentagesMoyenne(note = 19.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 99),
                                    PourcentagesMoyenne(note = 19.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 100),
                                ),
                        ),
                        PourcentagesPourChaqueMoyenneParBaccalaureat(
                            baccalaureat = baccalaureatNC,
                            pourcentages =
                                listOf(
                                    PourcentagesMoyenne(note = 0.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 0.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 1.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 1.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 2.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 2.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 3.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 3.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 4.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 4.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 5.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 5.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 6.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 6.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 7.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 7.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 8.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 8.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 9.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 9.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 1),
                                    PourcentagesMoyenne(note = 10.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 3),
                                    PourcentagesMoyenne(note = 10.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 6),
                                    PourcentagesMoyenne(note = 11.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 10),
                                    PourcentagesMoyenne(note = 11.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 17),
                                    PourcentagesMoyenne(note = 12.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 25),
                                    PourcentagesMoyenne(note = 12.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 34),
                                    PourcentagesMoyenne(note = 13.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 44),
                                    PourcentagesMoyenne(note = 13.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 55),
                                    PourcentagesMoyenne(note = 14.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 64),
                                    PourcentagesMoyenne(note = 14.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 73),
                                    PourcentagesMoyenne(note = 15.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 80),
                                    PourcentagesMoyenne(note = 15.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 86),
                                    PourcentagesMoyenne(note = 16.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 91),
                                    PourcentagesMoyenne(note = 16.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 95),
                                    PourcentagesMoyenne(note = 17.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 97),
                                    PourcentagesMoyenne(note = 17.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 98),
                                    PourcentagesMoyenne(note = 18.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 99),
                                    PourcentagesMoyenne(note = 18.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 99),
                                    PourcentagesMoyenne(note = 19.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 99),
                                    PourcentagesMoyenne(note = 19.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 100),
                                ),
                        ),
                        PourcentagesPourChaqueMoyenneParBaccalaureat(
                            baccalaureat = baccalaureatPro,
                            pourcentages =
                                listOf(
                                    PourcentagesMoyenne(note = 0.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 0.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 1.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 1.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 2.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 2.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 3.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 3.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 4.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 4.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 5.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 5.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 6.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 6.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 7.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 7.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 8.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 8.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 9.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 1),
                                    PourcentagesMoyenne(note = 9.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 2),
                                    PourcentagesMoyenne(note = 10.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 4),
                                    PourcentagesMoyenne(note = 10.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 8),
                                    PourcentagesMoyenne(note = 11.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 13),
                                    PourcentagesMoyenne(note = 11.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 21),
                                    PourcentagesMoyenne(note = 12.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 31),
                                    PourcentagesMoyenne(note = 12.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 42),
                                    PourcentagesMoyenne(note = 13.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 53),
                                    PourcentagesMoyenne(note = 13.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 65),
                                    PourcentagesMoyenne(note = 14.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 75),
                                    PourcentagesMoyenne(note = 14.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 83),
                                    PourcentagesMoyenne(note = 15.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 90),
                                    PourcentagesMoyenne(note = 15.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 94),
                                    PourcentagesMoyenne(note = 16.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 97),
                                    PourcentagesMoyenne(note = 16.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 99),
                                    PourcentagesMoyenne(note = 17.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 99),
                                    PourcentagesMoyenne(note = 17.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 99),
                                    PourcentagesMoyenne(note = 18.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 99),
                                    PourcentagesMoyenne(note = 18.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 100),
                                    PourcentagesMoyenne(note = 19.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 100),
                                    PourcentagesMoyenne(note = 19.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 100),
                                ),
                        ),
                        PourcentagesPourChaqueMoyenneParBaccalaureat(
                            baccalaureat = baccalaureatSTL,
                            pourcentages =
                                listOf(
                                    PourcentagesMoyenne(note = 0.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 0.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 1.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 1.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 2.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 2.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 3.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 3.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 4.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 4.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 5.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 5.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 6.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 6.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 7.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 7.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 8.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 8.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                    PourcentagesMoyenne(note = 9.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 1),
                                    PourcentagesMoyenne(note = 9.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 3),
                                    PourcentagesMoyenne(note = 10.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 7),
                                    PourcentagesMoyenne(note = 10.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 12),
                                    PourcentagesMoyenne(note = 11.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 19),
                                    PourcentagesMoyenne(note = 11.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 28),
                                    PourcentagesMoyenne(note = 12.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 38),
                                    PourcentagesMoyenne(note = 12.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 48),
                                    PourcentagesMoyenne(note = 13.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 59),
                                    PourcentagesMoyenne(note = 13.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 69),
                                    PourcentagesMoyenne(note = 14.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 78),
                                    PourcentagesMoyenne(note = 14.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 85),
                                    PourcentagesMoyenne(note = 15.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 90),
                                    PourcentagesMoyenne(note = 15.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 94),
                                    PourcentagesMoyenne(note = 16.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 97),
                                    PourcentagesMoyenne(note = 16.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 98),
                                    PourcentagesMoyenne(note = 17.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 99),
                                    PourcentagesMoyenne(note = 17.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 99),
                                    PourcentagesMoyenne(note = 18.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 99),
                                    PourcentagesMoyenne(note = 18.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 100),
                                    PourcentagesMoyenne(note = 19.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 100),
                                    PourcentagesMoyenne(note = 19.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 100),
                                ),
                        ),
                    ),
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
                    ),
                choixAlternance =
                    listOf(
                        ChoixAlternance.PAS_INTERESSE,
                        ChoixAlternance.INDIFFERENT,
                        ChoixAlternance.INTERESSE,
                        ChoixAlternance.TRES_INTERESSE,
                    ),
                choixDureeEtudesPrevue =
                    listOf(
                        ChoixDureeEtudesPrevue.INDIFFERENT,
                        ChoixDureeEtudesPrevue.COURTE,
                        ChoixDureeEtudesPrevue.LONGUE,
                        ChoixDureeEtudesPrevue.AUCUNE_IDEE,
                    ),
                categoriesDInteretsAvecLeursSousCategories = toutesLesCategoriesEtSousCategoriesDInteret,
                categoriesDomaineAvecLeursDomaines = categorieDomaineAvecLeursDomaines,
                admissionsParcoursup = admissionsParcoursup,
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
            .andExpect(content().json(contenuReferentiel))
    }

    @ConnecteAvecUnEnseignant(idEnseignant = "adcf627c-36dd-4df5-897b-159443a6d49c")
    @Test
    fun `si connecté avec un enseignant, doit retourner 200 avec le referentiel du parcours d'inscription`() {
        // When & Then
        mvc.perform(
            get("/api/v1/referentiel").contentType(MediaType.APPLICATION_JSON),
        ).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(contenuReferentiel))
    }

    @ConnecteSansId
    @Test
    fun `si connecté avec un token, doit retourner 200 avec le referentiel du parcours d'inscription`() {
        // When & Then
        mvc.perform(
            get("/api/v1/referentiel").contentType(MediaType.APPLICATION_JSON),
        ).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(contenuReferentiel))
    }

    companion object {
        private val baccalaureatGeneral =
            Baccalaureat(id = "Générale", nom = "Bac Général", idExterne = "Générale", idCarteParcoursup = "1")
        private val baccalaureatNC = Baccalaureat(id = "NC", nom = "Non-communiqué", idExterne = "NC", idCarteParcoursup = "0")
        private val baccalaureatPro = Baccalaureat(id = "P", nom = "Bac Professionnel", idExterne = "P", idCarteParcoursup = "3")
        private val baccalaureatSTL = Baccalaureat(id = "STL", nom = "Bac STL", idExterne = "STL", idCarteParcoursup = "2")
    }
}
