package fr.gouv.monprojetsup.formation.application.controller

import fr.gouv.monprojetsup.commun.ConnecteAvecUnEleve
import fr.gouv.monprojetsup.commun.ConnecteAvecUnEnseignant
import fr.gouv.monprojetsup.commun.ConnecteSansId
import fr.gouv.monprojetsup.commun.application.controller.ControllerTest
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetIllegalStateErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupBadRequestException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.commun.hateoas.domain.entity.Hateoas
import fr.gouv.monprojetsup.commun.hateoas.usecase.HateoasBuilder
import fr.gouv.monprojetsup.commun.lien.domain.entity.Lien
import fr.gouv.monprojetsup.formation.domain.entity.CommuneAvecVoeuxAuxAlentours
import fr.gouv.monprojetsup.formation.domain.entity.CommuneAvecVoeuxAuxAlentours.VoeuAvecDistance
import fr.gouv.monprojetsup.formation.domain.entity.CritereAnalyseCandidature
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationGeographique
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionDetaillees
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionDetaillees.AffiniteSpecialite
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation.FicheFormationPourProfil.ExplicationAutoEvaluationMoyenne
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation.FicheFormationPourProfil.ExplicationTypeBaccalaureat
import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis.MoyenneGeneraleDesAdmis
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis.MoyenneGeneraleDesAdmis.Centile
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis.RepartitionAdmis
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis.RepartitionAdmis.TotalAdmisPourUnBaccalaureat
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil.FormationAvecSonAffinite
import fr.gouv.monprojetsup.formation.domain.entity.Voeu
import fr.gouv.monprojetsup.formation.entity.Communes.LYON
import fr.gouv.monprojetsup.formation.entity.Communes.MARSEILLE
import fr.gouv.monprojetsup.formation.entity.Communes.PARIS15EME
import fr.gouv.monprojetsup.formation.entity.Communes.PARIS5EME
import fr.gouv.monprojetsup.formation.entity.Communes.STRASBOURG
import fr.gouv.monprojetsup.formation.usecase.OrdonnerRechercheFormationsBuilder
import fr.gouv.monprojetsup.formation.usecase.RechercherFormationsService
import fr.gouv.monprojetsup.formation.usecase.RecupererFormationService
import fr.gouv.monprojetsup.formation.usecase.RecupererFormationsService
import fr.gouv.monprojetsup.formation.usecase.SuggestionsFormationsService
import fr.gouv.monprojetsup.metier.domain.entity.Metier
import fr.gouv.monprojetsup.referentiel.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.referentiel.domain.entity.Domaine
import fr.gouv.monprojetsup.referentiel.domain.entity.InteretSousCategorie
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.net.ConnectException

@WebMvcTest(controllers = [FormationController::class])
class FormationControllerTest(
    @Autowired val mvc: MockMvc,
) : ControllerTest() {
    @MockBean
    lateinit var suggestionsFormationsService: SuggestionsFormationsService

    @MockBean
    lateinit var recupererFormationService: RecupererFormationService

    @MockBean
    lateinit var recupererFormationsService: RecupererFormationsService

    @MockBean
    lateinit var rechercherFormation: RechercherFormationsService

    @MockBean
    lateinit var ordonnerRechercheFormationsBuilder: OrdonnerRechercheFormationsBuilder

    @MockBean
    lateinit var hateoasBuilder: HateoasBuilder

    private val explications =
        ExplicationsSuggestionDetaillees(
            geographique =
                listOf(
                    ExplicationGeographique(
                        ville = "Nantes",
                        distanceKm = 1,
                    ),
                    ExplicationGeographique(
                        ville = "Paris",
                        distanceKm = 3,
                    ),
                ),
            dureeEtudesPrevue = ChoixDureeEtudesPrevue.LONGUE,
            alternance = ChoixAlternance.TRES_INTERESSE,
            specialitesChoisies =
                listOf(
                    AffiniteSpecialite(idSpecialite = "mat001", nomSpecialite = "specialiteA", pourcentage = 12),
                    AffiniteSpecialite(idSpecialite = "mat002", nomSpecialite = "specialiteB", pourcentage = 1),
                    AffiniteSpecialite(idSpecialite = "mat003", nomSpecialite = "specialiteC", pourcentage = 89),
                ),
            domaines =
                listOf(
                    Domaine(id = "T_ITM_1356", nom = "soin aux animaux", emoji = "\uD83D\uDC2E"),
                ),
            interets = listOf(InteretSousCategorie(id = "aider_autres", nom = "Aider les autres", emoji = "\uD83E\uDEC2")),
            explicationAutoEvaluationMoyenne =
                ExplicationAutoEvaluationMoyenne(
                    baccalaureatUtilise = Baccalaureat(id = "Générale", idExterne = "Générale", nom = "Série Générale"),
                    moyenneAutoEvalue = 15f,
                    basIntervalleNotes = 14f,
                    hautIntervalleNotes = 16f,
                ),
            formationsSimilaires =
                listOf(
                    FormationCourte(id = "fl1", nom = "CPGE MPSI"),
                    FormationCourte(id = "fl7", nom = "BUT Informatique"),
                ),
            explicationTypeBaccalaureat =
                ExplicationTypeBaccalaureat(
                    baccalaureat = Baccalaureat(id = "Générale", idExterne = "Général", nom = "Série Générale"),
                    pourcentage = 18,
                ),
            detailsCalculScore = emptyList(),
        )
    val ficheFormation =
        FicheFormation.FicheFormationPourProfil(
            id = "fl680002",
            nom = "Cycle pluridisciplinaire d'Études Supérieures - Science",
            formationsAssociees = listOf("fl0012"),
            descriptifGeneral =
                "Les formations CPES recrutent des lycéen.nes de très bon niveau sur sélection et dispensent " +
                    "des enseignements pluri-disciplinaires (scientifiques, artistiques, de sciences sociales, " +
                    "de littérature) permettant une poursuite d'études en master ou en grande école. Il s’agit de " +
                    "formations ouvertes socialement recrutant 40% de boursiers sur critères sociaux. Elles sont " +
                    "organisées conjointement par un établissement d’enseignement secondaire lycée et un " +
                    "établissement de l’enseignement supérieur, une université.",
            descriptifDiplome =
                "Les formations CPES sont des diplômes d’établissement diplômants en trois ans qui " +
                    "conférent le grade de licence.",
            descriptifAttendus =
                "Il est attendu des candidats de démontrer une solide compréhension des techniques de base " +
                    "de la floristerie, y compris la composition florale, la reconnaissance des plantes et " +
                    "des fleurs, ainsi que les soins et l'entretien des végétaux.",
            descriptifConseils =
                "Nous vous conseillons de développer une sensibilité artistique et de rester informé des tendances " +
                    "actuelles en matière de design floral pour exceller dans ce domaine.",
            liens =
                listOf(
                    Lien(
                        nom = "Voir sur l'ONISEP",
                        url =
                            "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/" +
                                "cycle-pluridisciplinaire-d-etudes-superieures",
                    ),
                ),
            voeux =
                listOf(
                    Voeu(id = "ta10", nom = "Nom du ta10", commune = LYON),
                    Voeu(id = "ta3", nom = "Nom du ta3", commune = PARIS5EME),
                    Voeu(id = "ta11", nom = "Nom du ta11", commune = LYON),
                    Voeu(id = "ta32", nom = "Nom du ta32", commune = PARIS15EME),
                    Voeu(id = "ta17", nom = "Nom du ta17", commune = STRASBOURG),
                    Voeu(id = "ta7", nom = "Nom du ta7", commune = MARSEILLE),
                ),
            voeuxParCommunesFavorites =
                listOf(
                    CommuneAvecVoeuxAuxAlentours(
                        commune = PARIS15EME,
                        distances =
                            listOf(
                                VoeuAvecDistance(
                                    Voeu(id = "ta3", nom = "Nom du ta3", commune = PARIS5EME),
                                    km = 3,
                                ),
                                VoeuAvecDistance(
                                    Voeu(id = "ta32", nom = "Nom du ta32", commune = PARIS15EME),
                                    km = 1,
                                ),
                            ),
                    ),
                ),
            metiersTriesParAffinites =
                listOf(
                    Metier(
                        id = "MET001",
                        nom = "géomaticien/ne",
                        descriptif =
                            "À la croisée de la géographie et de l'informatique, le géomaticien ou la géomaticienne " +
                                "exploite les données pour modéliser le territoire",
                        liens =
                            listOf(
                                Lien(
                                    nom = "Voir sur l'ONISEP",
                                    url = "https://www.onisep.fr/ressources/univers-metier/metiers/geomaticien-geomaticienne",
                                ),
                            ),
                    ),
                    Metier(
                        id = "MET002",
                        nom = "documentaliste",
                        descriptif = null,
                        liens = emptyList(),
                    ),
                ),
            tauxAffinite = 90,
            explications = explications,
            criteresAnalyseCandidature =
                listOf(
                    CritereAnalyseCandidature(nom = "Compétences académiques", pourcentage = 10),
                    CritereAnalyseCandidature(
                        nom = "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                        pourcentage = 0,
                    ),
                    CritereAnalyseCandidature(nom = "Résultats académiques", pourcentage = 18),
                    CritereAnalyseCandidature(nom = "Savoir-être", pourcentage = 42),
                    CritereAnalyseCandidature(nom = "Motivation, connaissance", pourcentage = 30),
                ),
            statistiquesDesAdmis =
                StatistiquesDesAdmis(
                    moyenneGeneraleDesAdmis =
                        MoyenneGeneraleDesAdmis(
                            baccalaureat = Baccalaureat(id = "Générale", idExterne = "Général", nom = "Série Générale"),
                            centiles =
                                listOf(
                                    Centile(centile = 5, note = 13f),
                                    Centile(centile = 25, note = 14.5f),
                                    Centile(centile = 75, note = 17f),
                                    Centile(centile = 95, note = 18f),
                                ),
                        ),
                    repartitionAdmis =
                        RepartitionAdmis(
                            total = 6915,
                            parBaccalaureat =
                                listOf(
                                    TotalAdmisPourUnBaccalaureat(
                                        baccalaureat = Baccalaureat(id = "Générale", idExterne = "Général", nom = "Série Générale"),
                                        nombreAdmis = 6677,
                                    ),
                                    TotalAdmisPourUnBaccalaureat(
                                        baccalaureat = Baccalaureat(id = "STMG", idExterne = "STMG", nom = "Série STMG"),
                                        nombreAdmis = 15,
                                    ),
                                    TotalAdmisPourUnBaccalaureat(
                                        baccalaureat = Baccalaureat(id = "STI2D", idExterne = "STI2D", nom = "Série STI2D"),
                                        nombreAdmis = 223,
                                    ),
                                ),
                        ),
                ),
            apprentissage = true,
        )

    val ficheFormationSansProfil =
        FicheFormation.FicheFormationSansProfil(
            id = "fl680002",
            nom = "Cycle pluridisciplinaire d'Études Supérieures - Science",
            metiers =
                listOf(
                    Metier(
                        id = "MET001",
                        nom = "géomaticien/ne",
                        descriptif =
                            "À la croisée de la géographie et de l'informatique, le géomaticien ou la géomaticienne " +
                                "exploite les données pour modéliser le territoire",
                        liens =
                            listOf(
                                Lien(
                                    nom = "Voir sur l'ONISEP",
                                    url = "https://www.onisep.fr/ressources/univers-metier/metiers/geomaticien-geomaticienne",
                                ),
                            ),
                    ),
                    Metier(
                        id = "MET002",
                        nom = "documentaliste",
                        descriptif = null,
                        liens = emptyList(),
                    ),
                ),
            formationsAssociees = listOf("fl0012"),
            descriptifGeneral =
                "Les formations CPES recrutent des lycéen.nes de très bon niveau sur sélection et dispensent " +
                    "des enseignements pluri-disciplinaires (scientifiques, artistiques, de sciences sociales, " +
                    "de littérature) permettant une poursuite d'études en master ou en grande école. Il s’agit " +
                    "de formations ouvertes socialement recrutant 40% de boursiers sur critères sociaux. Elles " +
                    "sont organisées conjointement par un établissement d’enseignement secondaire lycée et un " +
                    "établissement de l’enseignement supérieur, une université.",
            descriptifDiplome =
                "Les formations CPES sont des diplômes d’établissement diplômants en trois ans qui " +
                    "conférent le grade de licence.",
            descriptifAttendus =
                "Il est attendu des candidats de démontrer une solide compréhension des techniques de base " +
                    "de la floristerie, y compris la composition florale, la reconnaissance des plantes et " +
                    "des fleurs, ainsi que les soins et l'entretien des végétaux.",
            descriptifConseils =
                "Nous vous conseillons de développer une sensibilité artistique et de rester informé des tendances " +
                    "actuelles en matière de design floral pour exceller dans ce domaine.",
            liens =
                listOf(
                    Lien(
                        nom = "Voir sur l'ONISEP",
                        url =
                            "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/" +
                                "cycle-pluridisciplinaire-d-etudes-superieures",
                    ),
                ),
            voeux =
                listOf(
                    Voeu(id = "ta10", nom = "Nom du ta10", commune = LYON),
                    Voeu(id = "ta3", nom = "Nom du ta3", commune = PARIS5EME),
                    Voeu(id = "ta11", nom = "Nom du ta11", commune = LYON),
                    Voeu(id = "ta32", nom = "Nom du ta32", commune = PARIS15EME),
                    Voeu(id = "ta17", nom = "Nom du ta17", commune = STRASBOURG),
                    Voeu(id = "ta7", nom = "Nom du ta7", commune = MARSEILLE),
                ),
            criteresAnalyseCandidature =
                listOf(
                    CritereAnalyseCandidature(nom = "Compétences académiques", pourcentage = 10),
                    CritereAnalyseCandidature(
                        nom = "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                        pourcentage = 0,
                    ),
                    CritereAnalyseCandidature(nom = "Résultats académiques", pourcentage = 18),
                    CritereAnalyseCandidature(nom = "Savoir-être", pourcentage = 42),
                    CritereAnalyseCandidature(nom = "Motivation, connaissance", pourcentage = 30),
                ),
            statistiquesDesAdmis =
                StatistiquesDesAdmis(
                    repartitionAdmis =
                        RepartitionAdmis(
                            total = 12,
                            parBaccalaureat = listOf(),
                        ),
                    moyenneGeneraleDesAdmis = null,
                ),
            apprentissage = false,
        )

    private val metiersTriesParAffinites =
        listOf(
            "MET_611",
            "MET_610",
            "MET_613",
            "MET_211",
        )

    private val formations =
        listOf(
            FormationAvecSonAffinite(idFormation = "fl240", tauxAffinite = 0.5448393f),
            FormationAvecSonAffinite(idFormation = "fr22", tauxAffinite = 0.7782054f),
            FormationAvecSonAffinite(idFormation = "fl2110", tauxAffinite = 0.3333385f),
            FormationAvecSonAffinite(idFormation = "fl2016", tauxAffinite = 0.7217561f),
            FormationAvecSonAffinite(idFormation = "fl252", tauxAffinite = 0.8125898f),
            FormationAvecSonAffinite(idFormation = "fl2118", tauxAffinite = 0.7103791f),
            FormationAvecSonAffinite(idFormation = "fl680003", tauxAffinite = 0.6735823f),
            FormationAvecSonAffinite(idFormation = "fl2009", tauxAffinite = 0.2486587f),
            FormationAvecSonAffinite(idFormation = "fl2046", tauxAffinite = 0.1638471f),
            FormationAvecSonAffinite(idFormation = "fl2022", tauxAffinite = 0.6206682f),
            FormationAvecSonAffinite(idFormation = "fr83", tauxAffinite = 0.8900792f),
            FormationAvecSonAffinite(idFormation = "fl2044", tauxAffinite = 0.2842652f),
            FormationAvecSonAffinite(idFormation = "fl2090", tauxAffinite = 0.1719057f),
            FormationAvecSonAffinite(idFormation = "fl840010", tauxAffinite = 0.5644857f),
            FormationAvecSonAffinite(idFormation = "fl2034", tauxAffinite = 0.538966f),
            FormationAvecSonAffinite(idFormation = "fl2073", tauxAffinite = 0.8769478f),
            FormationAvecSonAffinite(idFormation = "fl2018", tauxAffinite = 0.5652516f),
            FormationAvecSonAffinite(idFormation = "fl2010", tauxAffinite = 0.6200685f),
            FormationAvecSonAffinite(idFormation = "fl2019", tauxAffinite = 0.5145695f),
            FormationAvecSonAffinite(idFormation = "fl52", tauxAffinite = 0.983774f),
            FormationAvecSonAffinite(idFormation = "fl41", tauxAffinite = 0.9f),
            FormationAvecSonAffinite(idFormation = "fl2051", tauxAffinite = 0.4817011f),
        )

    private val affinitesFormationEtMetier =
        SuggestionsPourUnProfil(
            metiersTriesParAffinites = metiersTriesParAffinites,
            formations = formations,
        )

    @Nested
    inner class `Quand on appelle la route de suggestions de formations` {
        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le service réussi sans page indiquée, doit retourner 200 avec la premiere page des fiches formations suggérées`() {
            // Given
            given(suggestionsFormationsService.recupererLesSuggestionsPourUnProfil(unProfilEleve)).willReturn(
                affinitesFormationEtMetier,
            )
            val hateoas =
                Hateoas(
                    pageActuelle = 1,
                    pageSuivante = null,
                    premierePage = 1,
                    dernierePage = 1,
                    listeCoupee = formations,
                )
            given(hateoasBuilder.creerHateoas(liste = formations, numeroDePageActuelle = 1, tailleLot = 30)).willReturn(
                hateoas,
            )
            val idsFormations =
                listOf(
                    "fl240",
                    "fr22",
                    "fl2110",
                    "fl2016",
                    "fl252",
                    "fl2118",
                    "fl680003",
                    "fl2009",
                    "fl2046",
                    "fl2022",
                    "fr83",
                    "fl2044",
                    "fl2090",
                    "fl840010",
                    "fl2034",
                    "fl2073",
                    "fl2018",
                    "fl2010",
                    "fl2019",
                    "fl52",
                    "fl41",
                    "fl2051",
                )
            given(
                recupererFormationsService.recupererFichesFormationPourProfil(
                    unProfilEleve,
                    affinitesFormationEtMetier,
                    idsFormations,
                    false,
                ),
            ).willReturn(
                listOf(
                    ficheFormation,
                    ficheFormation.copy(
                        id = "fl2",
                        nom = "2eme formation",
                        descriptifGeneral = null,
                        descriptifAttendus = null,
                        descriptifDiplome = null,
                        descriptifConseils = null,
                        formationsAssociees = listOf("fl3"),
                        liens = emptyList(),
                        criteresAnalyseCandidature = emptyList(),
                        statistiquesDesAdmis = null,
                        tauxAffinite = 17,
                        metiersTriesParAffinites = emptyList(),
                        voeux = emptyList(),
                        apprentissage = false,
                        explications = null,
                    ),
                ),
            )

            // When & Then
            mvc.perform(
                get("/api/v1/formations/suggestions"),
            ).andDo(print()).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "formations": [
                            {
                              "formation": {
                                "id": "fl680002",
                                "nom": "Cycle pluridisciplinaire d'Études Supérieures - Science",
                                "idsFormationsAssociees": [
                                  "fl0012"
                                ],
                                "descriptifFormation": "Les formations CPES recrutent des lycéen.nes de très bon niveau sur sélection et dispensent des enseignements pluri-disciplinaires (scientifiques, artistiques, de sciences sociales, de littérature) permettant une poursuite d'études en master ou en grande école. Il s’agit de formations ouvertes socialement recrutant 40% de boursiers sur critères sociaux. Elles sont organisées conjointement par un établissement d’enseignement secondaire lycée et un établissement de l’enseignement supérieur, une université.",
                                "descriptifDiplome": "Les formations CPES sont des diplômes d’établissement diplômants en trois ans qui conférent le grade de licence.",
                                "descriptifConseils": "Nous vous conseillons de développer une sensibilité artistique et de rester informé des tendances actuelles en matière de design floral pour exceller dans ce domaine.",
                                "descriptifAttendus": "Il est attendu des candidats de démontrer une solide compréhension des techniques de base de la floristerie, y compris la composition florale, la reconnaissance des plantes et des fleurs, ainsi que les soins et l'entretien des végétaux.",
                                "moyenneGeneraleDesAdmis": {
                                  "baccalaureat": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  },
                                  "centiles": [
                                    {
                                      "centile": 5,
                                      "note": 13.0
                                    },
                                    {
                                      "centile": 25,
                                      "note": 14.5
                                    },
                                    {
                                      "centile": 75,
                                      "note": 17.0
                                    },
                                    {
                                      "centile": 95,
                                      "note": 18.0
                                    }
                                  ]
                                },
                                "criteresAnalyseCandidature": [
                                  {
                                    "nom": "Compétences académiques",
                                    "pourcentage": 10
                                  },
                                  {
                                    "nom": "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                                    "pourcentage": 0
                                  },
                                  {
                                    "nom": "Résultats académiques",
                                    "pourcentage": 18
                                  },
                                  {
                                    "nom": "Savoir-être",
                                    "pourcentage": 42
                                  },
                                  {
                                    "nom": "Motivation, connaissance",
                                    "pourcentage": 30
                                  }
                                ],
                                "repartitionAdmisAnneePrecedente": {
                                  "total": 6915,
                                  "parBaccalaureat": [
                                    {
                                      "baccalaureat": {
                                        "id": "Générale",
                                        "nom": "Série Générale"
                                      },
                                      "nombreAdmis": 6677
                                    },
                                    {
                                      "baccalaureat": {
                                        "id": "STMG",
                                        "nom": "Série STMG"
                                      },
                                      "nombreAdmis": 15
                                    },
                                    {
                                      "baccalaureat": {
                                        "id": "STI2D",
                                        "nom": "Série STI2D"
                                      },
                                      "nombreAdmis": 223
                                    }
                                  ]
                                },
                                "liens": [
                                  {
                                    "nom": "Voir sur l'ONISEP",
                                    "url": "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/cycle-pluridisciplinaire-d-etudes-superieures"
                                  }
                                ],
                                "voeux": [
                                  {
                                    "id": "ta10",
                                    "nom": "Nom du ta10",
                                    "commune": {
                                      "nom": "Lyon",
                                      "codeInsee": "69123"
                                    }
                                  },
                                  {
                                    "id": "ta3",
                                    "nom": "Nom du ta3",
                                    "commune": {
                                      "nom": "Paris",
                                      "codeInsee": "75105"
                                    }
                                  },
                                  {
                                    "id": "ta11",
                                    "nom": "Nom du ta11",
                                    "commune": {
                                      "nom": "Lyon",
                                      "codeInsee": "69123"
                                    }
                                  },
                                  {
                                    "id": "ta32",
                                    "nom": "Nom du ta32",
                                    "commune": {
                                      "nom": "Paris",
                                      "codeInsee": "75115"
                                    }
                                  },
                                  {
                                    "id": "ta17",
                                    "nom": "Nom du ta17",
                                    "commune": {
                                      "nom": "Strasbourg",
                                      "codeInsee": "67482"
                                    }
                                  },
                                  {
                                    "id": "ta7",
                                    "nom": "Nom du ta7",
                                    "commune": {
                                      "nom": "Marseille",
                                      "codeInsee": "13055"
                                    }
                                  }
                                ],
                                "communesFavoritesAvecLeursVoeux": [
                                  {
                                    "commune": {
                                      "codeInsee": "75115",
                                      "nom": "Paris",
                                      "latitude": 48.851227,
                                      "longitude": 2.2885659
                                    },
                                    "voeuxAvecDistance": [
                                      {
                                        "voeu": {
                                          "id": "ta3",
                                          "nom": "Nom du ta3",
                                          "commune": {
                                            "nom": "Paris",
                                            "codeInsee": "75105"
                                          }
                                        },
                                        "distanceKm": 3
                                      },
                                      {
                                        "voeu": {
                                          "id": "ta32",
                                          "nom": "Nom du ta32",
                                          "commune": {
                                            "nom": "Paris",
                                            "codeInsee": "75115"
                                          }
                                        },
                                        "distanceKm": 1
                                      }
                                    ]
                                  }
                                ],
                                "metiers": [
                                  {
                                    "id": "MET001",
                                    "nom": "géomaticien/ne",
                                    "descriptif": "À la croisée de la géographie et de l'informatique, le géomaticien ou la géomaticienne exploite les données pour modéliser le territoire",
                                    "liens": [
                                      {
                                        "nom": "Voir sur l'ONISEP",
                                        "url": "https://www.onisep.fr/ressources/univers-metier/metiers/geomaticien-geomaticienne"
                                      }
                                    ]
                                  },
                                  {
                                    "id": "MET002",
                                    "nom": "documentaliste",
                                    "descriptif": null,
                                    "liens": []
                                  }
                                ],
                                "tauxAffinite": 90,
                                "apprentissage": true
                              },
                              "explications": {
                                "geographique": [
                                  {
                                    "nomVille": "Nantes",
                                    "distanceKm": 1
                                  },
                                  {
                                    "nomVille": "Paris",
                                    "distanceKm": 3
                                  }
                                ],
                                "formationsSimilaires": [
                                  {
                                    "id": "fl1",
                                    "nom": "CPGE MPSI"
                                  },
                                  {
                                    "id": "fl7",
                                    "nom": "BUT Informatique"
                                  }
                                ],
                                "dureeEtudesPrevue": "longue",
                                "alternance": "tres_interesse",
                                "interetsEtDomainesChoisis": {
                                  "interets": [
                                    {
                                      "id": "aider_autres",
                                      "nom": "Aider les autres"
                                    }
                                  ],
                                  "domaines": [
                                    {
                                      "id": "T_ITM_1356",
                                      "nom": "soin aux animaux",
                                      "emoji": "\uD83D\uDC2E"
                                    }
                                  ]
                                },
                                "specialitesChoisies": [
                                  {
                                    "nomSpecialite": "specialiteA",
                                    "pourcentage": 12
                                  },
                                  {
                                    "nomSpecialite": "specialiteB",
                                    "pourcentage": 1
                                  },
                                  {
                                    "nomSpecialite": "specialiteC",
                                    "pourcentage": 89
                                  }
                                ],
                                "typeBaccalaureat": {
                                  "baccalaureat": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  },
                                  "pourcentage": 18
                                },
                                "autoEvaluationMoyenne": {
                                  "moyenne": 15.0,
                                  "basIntervalleNotes": 14.0,
                                  "hautIntervalleNotes": 16.0,
                                  "baccalaureatUtilise": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  }
                                },
                                "detailsCalculScore": {
                                  "details": []
                                }
                              }
                            },
                            {
                              "formation": {
                                "id": "fl2",
                                "nom": "2eme formation",
                                "idsFormationsAssociees": [
                                  "fl3"
                                ],
                                "descriptifFormation": null,
                                "descriptifDiplome": null,
                                "descriptifConseils": null,
                                "descriptifAttendus": null,
                                "moyenneGeneraleDesAdmis": null,
                                "criteresAnalyseCandidature": [],
                                "repartitionAdmisAnneePrecedente": null,
                                "liens": [],
                                "voeux": [],
                                "communesFavoritesAvecLeursVoeux": [
                                  {
                                    "commune": {
                                      "codeInsee": "75115",
                                      "nom": "Paris",
                                      "latitude": 48.851227,
                                      "longitude": 2.2885659
                                    },
                                    "voeuxAvecDistance": [
                                      {
                                        "voeu": {
                                          "id": "ta3",
                                          "nom": "Nom du ta3",
                                          "commune": {
                                            "nom": "Paris",
                                            "codeInsee": "75105"
                                          }
                                        },
                                        "distanceKm": 3
                                      },
                                      {
                                        "voeu": {
                                          "id": "ta32",
                                          "nom": "Nom du ta32",
                                          "commune": {
                                            "nom": "Paris",
                                            "codeInsee": "75115"
                                          }
                                        },
                                        "distanceKm": 1
                                      }
                                    ]
                                  }
                                ],
                                "metiers": [],
                                "tauxAffinite": 17,
                                "apprentissage": false
                              },
                              "explications": null
                            }
                          ],
                          "liens": [
                            {
                              "rel": "premier",
                              "href": "http://localhost/api/v1/formations/suggestions?numeroDePage=1"
                            },
                            {
                              "rel": "dernier",
                              "href": "http://localhost/api/v1/formations/suggestions?numeroDePage=1"
                            },
                            {
                              "rel": "actuel",
                              "href": "http://localhost/api/v1/formations/suggestions?numeroDePage=1"
                            }
                          ]
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le service réussi pour la page 2, doit retourner 200 avec une liste des fiches formations suggérées`() {
            // Given
            given(suggestionsFormationsService.recupererLesSuggestionsPourUnProfil(unProfilEleve)).willReturn(
                affinitesFormationEtMetier,
            )
            val listeCoupee =
                listOf(
                    FormationAvecSonAffinite(idFormation = "fl2016", tauxAffinite = 0.7217561f),
                    FormationAvecSonAffinite(idFormation = "fl2118", tauxAffinite = 0.7103791f),
                    FormationAvecSonAffinite(idFormation = "fl680003", tauxAffinite = 0.6735823f),
                )
            val hateoas = Hateoas(pageActuelle = 2, pageSuivante = 3, premierePage = 1, dernierePage = 4, listeCoupee = listeCoupee)
            given(hateoasBuilder.creerHateoas(liste = formations, numeroDePageActuelle = 2, tailleLot = 30)).willReturn(
                hateoas,
            )
            val idsFormations = listOf("fl2016", "fl2118", "fl680003")
            given(
                recupererFormationsService.recupererFichesFormationPourProfil(
                    unProfilEleve,
                    affinitesFormationEtMetier,
                    idsFormations,
                    false,
                ),
            ).willReturn(listOf(ficheFormation.copy(id = "fl2016")))

            // When & Then
            mvc.perform(
                get("/api/v1/formations/suggestions?numeroDePage=2"),
            ).andDo(print()).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "formations": [
                            {
                              "formation": {
                                "id": "fl2016",
                                "nom": "Cycle pluridisciplinaire d'Études Supérieures - Science",
                                "idsFormationsAssociees": [
                                  "fl0012"
                                ],
                                "descriptifFormation": "Les formations CPES recrutent des lycéen.nes de très bon niveau sur sélection et dispensent des enseignements pluri-disciplinaires (scientifiques, artistiques, de sciences sociales, de littérature) permettant une poursuite d'études en master ou en grande école. Il s’agit de formations ouvertes socialement recrutant 40% de boursiers sur critères sociaux. Elles sont organisées conjointement par un établissement d’enseignement secondaire lycée et un établissement de l’enseignement supérieur, une université.",
                                "descriptifDiplome": "Les formations CPES sont des diplômes d’établissement diplômants en trois ans qui conférent le grade de licence.",
                                "descriptifConseils": "Nous vous conseillons de développer une sensibilité artistique et de rester informé des tendances actuelles en matière de design floral pour exceller dans ce domaine.",
                                "descriptifAttendus": "Il est attendu des candidats de démontrer une solide compréhension des techniques de base de la floristerie, y compris la composition florale, la reconnaissance des plantes et des fleurs, ainsi que les soins et l'entretien des végétaux.",
                                "moyenneGeneraleDesAdmis": {
                                  "baccalaureat": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  },
                                  "centiles": [
                                    {
                                      "centile": 5,
                                      "note": 13.0
                                    },
                                    {
                                      "centile": 25,
                                      "note": 14.5
                                    },
                                    {
                                      "centile": 75,
                                      "note": 17.0
                                    },
                                    {
                                      "centile": 95,
                                      "note": 18.0
                                    }
                                  ]
                                },
                                "criteresAnalyseCandidature": [
                                  {
                                    "nom": "Compétences académiques",
                                    "pourcentage": 10
                                  },
                                  {
                                    "nom": "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                                    "pourcentage": 0
                                  },
                                  {
                                    "nom": "Résultats académiques",
                                    "pourcentage": 18
                                  },
                                  {
                                    "nom": "Savoir-être",
                                    "pourcentage": 42
                                  },
                                  {
                                    "nom": "Motivation, connaissance",
                                    "pourcentage": 30
                                  }
                                ],
                                "repartitionAdmisAnneePrecedente": {
                                  "total": 6915,
                                  "parBaccalaureat": [
                                    {
                                      "baccalaureat": {
                                        "id": "Générale",
                                        "nom": "Série Générale"
                                      },
                                      "nombreAdmis": 6677
                                    },
                                    {
                                      "baccalaureat": {
                                        "id": "STMG",
                                        "nom": "Série STMG"
                                      },
                                      "nombreAdmis": 15
                                    },
                                    {
                                      "baccalaureat": {
                                        "id": "STI2D",
                                        "nom": "Série STI2D"
                                      },
                                      "nombreAdmis": 223
                                    }
                                  ]
                                },
                                "liens": [
                                  {
                                    "nom": "Voir sur l'ONISEP",
                                    "url": "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/cycle-pluridisciplinaire-d-etudes-superieures"
                                  }
                                ],
                                "voeux": [
                                  {
                                    "id": "ta10",
                                    "nom": "Nom du ta10",
                                    "commune": {
                                      "nom": "Lyon",
                                      "codeInsee": "69123"
                                    }
                                  },
                                  {
                                    "id": "ta3",
                                    "nom": "Nom du ta3",
                                    "commune": {
                                      "nom": "Paris",
                                      "codeInsee": "75105"
                                    }
                                  },
                                  {
                                    "id": "ta11",
                                    "nom": "Nom du ta11",
                                    "commune": {
                                      "nom": "Lyon",
                                      "codeInsee": "69123"
                                    }
                                  },
                                  {
                                    "id": "ta32",
                                    "nom": "Nom du ta32",
                                    "commune": {
                                      "nom": "Paris",
                                      "codeInsee": "75115"
                                    }
                                  },
                                  {
                                    "id": "ta17",
                                    "nom": "Nom du ta17",
                                    "commune": {
                                      "nom": "Strasbourg",
                                      "codeInsee": "67482"
                                    }
                                  },
                                  {
                                    "id": "ta7",
                                    "nom": "Nom du ta7",
                                    "commune": {
                                      "nom": "Marseille",
                                      "codeInsee": "13055"
                                    }
                                  }
                                ],
                                "communesFavoritesAvecLeursVoeux": [
                                  {
                                    "commune": {
                                      "codeInsee": "75115",
                                      "nom": "Paris",
                                      "latitude": 48.851227,
                                      "longitude": 2.2885659
                                    },
                                    "voeuxAvecDistance": [
                                      {
                                        "voeu": {
                                          "id": "ta3",
                                          "nom": "Nom du ta3",
                                          "commune": {
                                            "nom": "Paris",
                                            "codeInsee": "75105"
                                          }
                                        },
                                        "distanceKm": 3
                                      },
                                      {
                                        "voeu": {
                                          "id": "ta32",
                                          "nom": "Nom du ta32",
                                          "commune": {
                                            "nom": "Paris",
                                            "codeInsee": "75115"
                                          }
                                        },
                                        "distanceKm": 1
                                      }
                                    ]
                                  }
                                ],
                                "metiers": [
                                  {
                                    "id": "MET001",
                                    "nom": "géomaticien/ne",
                                    "descriptif": "À la croisée de la géographie et de l'informatique, le géomaticien ou la géomaticienne exploite les données pour modéliser le territoire",
                                    "liens": [
                                      {
                                        "nom": "Voir sur l'ONISEP",
                                        "url": "https://www.onisep.fr/ressources/univers-metier/metiers/geomaticien-geomaticienne"
                                      }
                                    ]
                                  },
                                  {
                                    "id": "MET002",
                                    "nom": "documentaliste",
                                    "descriptif": null,
                                    "liens": []
                                  }
                                ],
                                "tauxAffinite": 90,
                                "apprentissage": true
                              },
                              "explications": {
                                "geographique": [
                                  {
                                    "nomVille": "Nantes",
                                    "distanceKm": 1
                                  },
                                  {
                                    "nomVille": "Paris",
                                    "distanceKm": 3
                                  }
                                ],
                                "formationsSimilaires": [
                                  {
                                    "id": "fl1",
                                    "nom": "CPGE MPSI"
                                  },
                                  {
                                    "id": "fl7",
                                    "nom": "BUT Informatique"
                                  }
                                ],
                                "dureeEtudesPrevue": "longue",
                                "alternance": "tres_interesse",
                                "interetsEtDomainesChoisis": {
                                  "interets": [
                                    {
                                      "id": "aider_autres",
                                      "nom": "Aider les autres"
                                    }
                                  ],
                                  "domaines": [
                                    {
                                      "id": "T_ITM_1356",
                                      "nom": "soin aux animaux",
                                      "emoji": "\uD83D\uDC2E"
                                    }
                                  ]
                                },
                                "specialitesChoisies": [
                                  {
                                    "nomSpecialite": "specialiteA",
                                    "pourcentage": 12
                                  },
                                  {
                                    "nomSpecialite": "specialiteB",
                                    "pourcentage": 1
                                  },
                                  {
                                    "nomSpecialite": "specialiteC",
                                    "pourcentage": 89
                                  }
                                ],
                                "typeBaccalaureat": {
                                  "baccalaureat": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  },
                                  "pourcentage": 18
                                },
                                "autoEvaluationMoyenne": {
                                  "moyenne": 15.0,
                                  "basIntervalleNotes": 14.0,
                                  "hautIntervalleNotes": 16.0,
                                  "baccalaureatUtilise": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  }
                                },
                                "detailsCalculScore": {
                                  "details": []
                                }
                              }
                            }
                          ],
                          "liens": [
                            {
                              "rel": "premier",
                              "href": "http://localhost/api/v1/formations/suggestions?numeroDePage=1"
                            },
                            {
                              "rel": "dernier",
                              "href": "http://localhost/api/v1/formations/suggestions?numeroDePage=4"
                            },
                            {
                              "rel": "actuel",
                              "href": "http://localhost/api/v1/formations/suggestions?numeroDePage=2"
                            },
                            {
                              "rel": "suivant",
                              "href": "http://localhost/api/v1/formations/suggestions?numeroDePage=3"
                            }
                          ]
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEnseignant(idEnseignant = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si connecté en tant qu'enseignant, doit retourner 200 avec les formations suggérées`() {
            // Given
            given(suggestionsFormationsService.recupererLesSuggestionsPourUnProfil(unProfilEleve)).willReturn(
                affinitesFormationEtMetier,
            )
            val hateoas =
                Hateoas(
                    pageActuelle = 1,
                    pageSuivante = null,
                    premierePage = 1,
                    dernierePage = 1,
                    listeCoupee = formations,
                )
            given(hateoasBuilder.creerHateoas(liste = formations, numeroDePageActuelle = 1, tailleLot = 30)).willReturn(
                hateoas,
            )
            val idsFormations =
                listOf(
                    "fl240",
                    "fr22",
                    "fl2110",
                    "fl2016",
                    "fl252",
                    "fl2118",
                    "fl680003",
                    "fl2009",
                    "fl2046",
                    "fl2022",
                    "fr83",
                    "fl2044",
                    "fl2090",
                    "fl840010",
                    "fl2034",
                    "fl2073",
                    "fl2018",
                    "fl2010",
                    "fl2019",
                    "fl52",
                    "fl41",
                    "fl2051",
                )
            given(
                recupererFormationsService.recupererFichesFormationPourProfil(
                    unProfilEleve,
                    affinitesFormationEtMetier,
                    idsFormations,
                    false,
                ),
            ).willReturn(
                listOf(
                    ficheFormation,
                    ficheFormation.copy(
                        id = "fl2",
                        nom = "2eme formation",
                        descriptifGeneral = null,
                        descriptifAttendus = null,
                        descriptifDiplome = null,
                        descriptifConseils = null,
                        formationsAssociees = listOf("fl3"),
                        liens = emptyList(),
                        criteresAnalyseCandidature = emptyList(),
                        statistiquesDesAdmis = null,
                        tauxAffinite = 17,
                        metiersTriesParAffinites = emptyList(),
                        voeux = emptyList(),
                        apprentissage = false,
                        explications = null,
                    ),
                ),
            )

            // When & Then
            mvc.perform(
                get("/api/v1/formations/suggestions"),
            ).andDo(print()).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "formations": [
                            {
                              "formation": {
                                "id": "fl680002",
                                "nom": "Cycle pluridisciplinaire d'Études Supérieures - Science",
                                "idsFormationsAssociees": [
                                  "fl0012"
                                ],
                                "descriptifFormation": "Les formations CPES recrutent des lycéen.nes de très bon niveau sur sélection et dispensent des enseignements pluri-disciplinaires (scientifiques, artistiques, de sciences sociales, de littérature) permettant une poursuite d'études en master ou en grande école. Il s’agit de formations ouvertes socialement recrutant 40% de boursiers sur critères sociaux. Elles sont organisées conjointement par un établissement d’enseignement secondaire lycée et un établissement de l’enseignement supérieur, une université.",
                                "descriptifDiplome": "Les formations CPES sont des diplômes d’établissement diplômants en trois ans qui conférent le grade de licence.",
                                "descriptifConseils": "Nous vous conseillons de développer une sensibilité artistique et de rester informé des tendances actuelles en matière de design floral pour exceller dans ce domaine.",
                                "descriptifAttendus": "Il est attendu des candidats de démontrer une solide compréhension des techniques de base de la floristerie, y compris la composition florale, la reconnaissance des plantes et des fleurs, ainsi que les soins et l'entretien des végétaux.",
                                "moyenneGeneraleDesAdmis": {
                                  "baccalaureat": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  },
                                  "centiles": [
                                    {
                                      "centile": 5,
                                      "note": 13.0
                                    },
                                    {
                                      "centile": 25,
                                      "note": 14.5
                                    },
                                    {
                                      "centile": 75,
                                      "note": 17.0
                                    },
                                    {
                                      "centile": 95,
                                      "note": 18.0
                                    }
                                  ]
                                },
                                "criteresAnalyseCandidature": [
                                  {
                                    "nom": "Compétences académiques",
                                    "pourcentage": 10
                                  },
                                  {
                                    "nom": "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                                    "pourcentage": 0
                                  },
                                  {
                                    "nom": "Résultats académiques",
                                    "pourcentage": 18
                                  },
                                  {
                                    "nom": "Savoir-être",
                                    "pourcentage": 42
                                  },
                                  {
                                    "nom": "Motivation, connaissance",
                                    "pourcentage": 30
                                  }
                                ],
                                "repartitionAdmisAnneePrecedente": {
                                  "total": 6915,
                                  "parBaccalaureat": [
                                    {
                                      "baccalaureat": {
                                        "id": "Générale",
                                        "nom": "Série Générale"
                                      },
                                      "nombreAdmis": 6677
                                    },
                                    {
                                      "baccalaureat": {
                                        "id": "STMG",
                                        "nom": "Série STMG"
                                      },
                                      "nombreAdmis": 15
                                    },
                                    {
                                      "baccalaureat": {
                                        "id": "STI2D",
                                        "nom": "Série STI2D"
                                      },
                                      "nombreAdmis": 223
                                    }
                                  ]
                                },
                                "liens": [
                                  {
                                    "nom": "Voir sur l'ONISEP",
                                    "url": "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/cycle-pluridisciplinaire-d-etudes-superieures"
                                  }
                                ],
                                "voeux": [
                                  {
                                    "id": "ta10",
                                    "nom": "Nom du ta10",
                                    "commune": {
                                      "nom": "Lyon",
                                      "codeInsee": "69123"
                                    }
                                  },
                                  {
                                    "id": "ta3",
                                    "nom": "Nom du ta3",
                                    "commune": {
                                      "nom": "Paris",
                                      "codeInsee": "75105"
                                    }
                                  },
                                  {
                                    "id": "ta11",
                                    "nom": "Nom du ta11",
                                    "commune": {
                                      "nom": "Lyon",
                                      "codeInsee": "69123"
                                    }
                                  },
                                  {
                                    "id": "ta32",
                                    "nom": "Nom du ta32",
                                    "commune": {
                                      "nom": "Paris",
                                      "codeInsee": "75115"
                                    }
                                  },
                                  {
                                    "id": "ta17",
                                    "nom": "Nom du ta17",
                                    "commune": {
                                      "nom": "Strasbourg",
                                      "codeInsee": "67482"
                                    }
                                  },
                                  {
                                    "id": "ta7",
                                    "nom": "Nom du ta7",
                                    "commune": {
                                      "nom": "Marseille",
                                      "codeInsee": "13055"
                                    }
                                  }
                                ],
                                "communesFavoritesAvecLeursVoeux": [
                                  {
                                    "commune": {
                                      "codeInsee": "75115",
                                      "nom": "Paris",
                                      "latitude": 48.851227,
                                      "longitude": 2.2885659
                                    },
                                    "voeuxAvecDistance": [
                                      {
                                        "voeu": {
                                          "id": "ta3",
                                          "nom": "Nom du ta3",
                                          "commune": {
                                            "nom": "Paris",
                                            "codeInsee": "75105"
                                          }
                                        },
                                        "distanceKm": 3
                                      },
                                      {
                                        "voeu": {
                                          "id": "ta32",
                                          "nom": "Nom du ta32",
                                          "commune": {
                                            "nom": "Paris",
                                            "codeInsee": "75115"
                                          }
                                        },
                                        "distanceKm": 1
                                      }
                                    ]
                                  }
                                ],
                                "metiers": [
                                  {
                                    "id": "MET001",
                                    "nom": "géomaticien/ne",
                                    "descriptif": "À la croisée de la géographie et de l'informatique, le géomaticien ou la géomaticienne exploite les données pour modéliser le territoire",
                                    "liens": [
                                      {
                                        "nom": "Voir sur l'ONISEP",
                                        "url": "https://www.onisep.fr/ressources/univers-metier/metiers/geomaticien-geomaticienne"
                                      }
                                    ]
                                  },
                                  {
                                    "id": "MET002",
                                    "nom": "documentaliste",
                                    "descriptif": null,
                                    "liens": []
                                  }
                                ],
                                "tauxAffinite": 90,
                                "apprentissage": true
                              },
                              "explications": {
                                "geographique": [
                                  {
                                    "nomVille": "Nantes",
                                    "distanceKm": 1
                                  },
                                  {
                                    "nomVille": "Paris",
                                    "distanceKm": 3
                                  }
                                ],
                                "formationsSimilaires": [
                                  {
                                    "id": "fl1",
                                    "nom": "CPGE MPSI"
                                  },
                                  {
                                    "id": "fl7",
                                    "nom": "BUT Informatique"
                                  }
                                ],
                                "dureeEtudesPrevue": "longue",
                                "alternance": "tres_interesse",
                                "interetsEtDomainesChoisis": {
                                  "interets": [
                                    {
                                      "id": "aider_autres",
                                      "nom": "Aider les autres"
                                    }
                                  ],
                                  "domaines": [
                                    {
                                      "id": "T_ITM_1356",
                                      "nom": "soin aux animaux",
                                      "emoji": "\uD83D\uDC2E"
                                    }
                                  ]
                                },
                                "specialitesChoisies": [
                                  {
                                    "nomSpecialite": "specialiteA",
                                    "pourcentage": 12
                                  },
                                  {
                                    "nomSpecialite": "specialiteB",
                                    "pourcentage": 1
                                  },
                                  {
                                    "nomSpecialite": "specialiteC",
                                    "pourcentage": 89
                                  }
                                ],
                                "typeBaccalaureat": {
                                  "baccalaureat": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  },
                                  "pourcentage": 18
                                },
                                "autoEvaluationMoyenne": {
                                  "moyenne": 15.0,
                                  "basIntervalleNotes": 14.0,
                                  "hautIntervalleNotes": 16.0,
                                  "baccalaureatUtilise": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  }
                                },
                                "detailsCalculScore": {
                                  "details": []
                                }
                              }
                            },
                            {
                              "formation": {
                                "id": "fl2",
                                "nom": "2eme formation",
                                "idsFormationsAssociees": [
                                  "fl3"
                                ],
                                "descriptifFormation": null,
                                "descriptifDiplome": null,
                                "descriptifConseils": null,
                                "descriptifAttendus": null,
                                "moyenneGeneraleDesAdmis": null,
                                "criteresAnalyseCandidature": [],
                                "repartitionAdmisAnneePrecedente": null,
                                "liens": [],
                                "voeux": [],
                                "communesFavoritesAvecLeursVoeux": [
                                  {
                                    "commune": {
                                      "codeInsee": "75115",
                                      "nom": "Paris",
                                      "latitude": 48.851227,
                                      "longitude": 2.2885659
                                    },
                                    "voeuxAvecDistance": [
                                      {
                                        "voeu": {
                                          "id": "ta3",
                                          "nom": "Nom du ta3",
                                          "commune": {
                                            "nom": "Paris",
                                            "codeInsee": "75105"
                                          }
                                        },
                                        "distanceKm": 3
                                      },
                                      {
                                        "voeu": {
                                          "id": "ta32",
                                          "nom": "Nom du ta32",
                                          "commune": {
                                            "nom": "Paris",
                                            "codeInsee": "75115"
                                          }
                                        },
                                        "distanceKm": 1
                                      }
                                    ]
                                  }
                                ],
                                "metiers": [],
                                "tauxAffinite": 17,
                                "apprentissage": false
                              },
                              "explications": null
                            }
                          ],
                          "liens": [
                            {
                              "rel": "premier",
                              "href": "http://localhost/api/v1/formations/suggestions?numeroDePage=1"
                            },
                            {
                              "rel": "dernier",
                              "href": "http://localhost/api/v1/formations/suggestions?numeroDePage=1"
                            },
                            {
                              "rel": "actuel",
                              "href": "http://localhost/api/v1/formations/suggestions?numeroDePage=1"
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
            mvc.perform(get("/api/v1/formations/suggestions")).andDo(print()).andExpect(status().isForbidden)
        }

        @Test
        fun `si pas connecté, doit retourner 401`() {
            // When & Then
            mvc.perform(get("/api/v1/formations/suggestions")).andExpect(status().isUnauthorized)
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le numéro de page envoyé est inférieur ou égale à 0, alors doit retourner 400`() {
            // When & Then
            mvc.perform(
                get("/api/v1/formations/suggestions?numeroDePage=0"),
            ).andDo(print()).andExpect(status().isBadRequest)
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                .andExpect(
                    content().json(
                        """
                        {
                          "type": "about:blank",
                          "title": "PAGINATION_COMMENCE_A_1",
                          "status": 400,
                          "detail": "La pagination commence à 1",
                          "instance": "/api/v1/formations/suggestions"
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le numéro de page envoyé est dépasse, alors doit retourner 400`() {
            //
            val uneException =
                MonProjetSupBadRequestException(
                    code = "PAGE_DEMANDEE_INXISTANTE",
                    msg = "La page 100 n'existe pas. Veuillez en donner une entre 1 et 8",
                )
            given(suggestionsFormationsService.recupererLesSuggestionsPourUnProfil(unProfilEleve)).willReturn(
                affinitesFormationEtMetier,
            )
            given(
                hateoasBuilder.creerHateoas(liste = formations, numeroDePageActuelle = 100, tailleLot = 30),
            ).willThrow(uneException)

            // When & Then
            mvc.perform(
                get("/api/v1/formations/suggestions?numeroDePage=100"),
            ).andDo(print()).andExpect(status().isBadRequest)
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                .andExpect(
                    content().json(
                        """
                        {
                          "type": "about:blank",
                          "title": "PAGE_DEMANDEE_INXISTANTE",
                          "status": 400,
                          "detail": "La page 100 n'existe pas. Veuillez en donner une entre 1 et 8",
                          "instance": "/api/v1/formations/suggestions"
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le service suggestion échoue avec une erreur interne, alors doit retourner 500`() {
            // Given
            val uneException =
                MonProjetSupInternalErrorException(
                    code = "ERREUR_API_SUGGESTIONS_CONNEXION",
                    msg = "Erreur lors de la connexion à l'API de suggestions",
                    origine = ConnectException("Connection refused"),
                )
            given(suggestionsFormationsService.recupererLesSuggestionsPourUnProfil(unProfilEleve)).willThrow(uneException)

            // When & Then
            mvc.perform(
                get("/api/v1/formations/suggestions"),
            ).andDo(print()).andExpect(status().isInternalServerError)
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
                .andExpect(
                    content().json(
                        """
                        {
                          "type": "about:blank",
                          "title": "ERREUR_API_SUGGESTIONS_CONNEXION",
                          "status": 500,
                          "detail": "Erreur lors de la connexion à l'API de suggestions",
                          "instance": "/api/v1/formations/suggestions"
                        }
                        """.trimIndent(),
                    ),
                )
        }
    }

    @Nested
    inner class `Quand on appelle la route de récupération d'une formation` {
        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le service réussi pour un appel avec un profil, doit retourner 200 avec le détail de la formation`() {
            // Given
            given(recupererFormationService.recupererFormation(unProfilEleve, "fl680002")).willReturn(ficheFormation)

            // When & Then
            mvc.perform(
                get("/api/v1/formations/fl680002"),
            ).andDo(print()).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "formation": {
                            "id": "fl680002",
                            "nom": "Cycle pluridisciplinaire d'Études Supérieures - Science",
                            "idsFormationsAssociees": [
                              "fl0012"
                            ],
                            "descriptifFormation": "Les formations CPES recrutent des lycéen.nes de très bon niveau sur sélection et dispensent des enseignements pluri-disciplinaires (scientifiques, artistiques, de sciences sociales, de littérature) permettant une poursuite d'études en master ou en grande école. Il s’agit de formations ouvertes socialement recrutant 40% de boursiers sur critères sociaux. Elles sont organisées conjointement par un établissement d’enseignement secondaire lycée et un établissement de l’enseignement supérieur, une université.",
                            "descriptifDiplome": "Les formations CPES sont des diplômes d’établissement diplômants en trois ans qui conférent le grade de licence.",
                            "descriptifConseils": "Nous vous conseillons de développer une sensibilité artistique et de rester informé des tendances actuelles en matière de design floral pour exceller dans ce domaine.",
                            "descriptifAttendus": "Il est attendu des candidats de démontrer une solide compréhension des techniques de base de la floristerie, y compris la composition florale, la reconnaissance des plantes et des fleurs, ainsi que les soins et l'entretien des végétaux.",
                            "moyenneGeneraleDesAdmis": {
                              "baccalaureat": {
                                "id": "Générale",
                                "nom": "Série Générale"
                              },
                              "centiles": [
                                {
                                  "centile": 5,
                                  "note": 13.0
                                },
                                {
                                  "centile": 25,
                                  "note": 14.5
                                },
                                {
                                  "centile": 75,
                                  "note": 17.0
                                },
                                {
                                  "centile": 95,
                                  "note": 18.0
                                }
                              ]
                            },
                            "criteresAnalyseCandidature": [
                              {
                                "nom": "Compétences académiques",
                                "pourcentage": 10
                              },
                              {
                                "nom": "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                                "pourcentage": 0
                              },
                              {
                                "nom": "Résultats académiques",
                                "pourcentage": 18
                              },
                              {
                                "nom": "Savoir-être",
                                "pourcentage": 42
                              },
                              {
                                "nom": "Motivation, connaissance",
                                "pourcentage": 30
                              }
                            ],
                            "repartitionAdmisAnneePrecedente": {
                              "total": 6915,
                              "parBaccalaureat": [
                                {
                                  "baccalaureat": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  },
                                  "nombreAdmis": 6677
                                },
                                {
                                  "baccalaureat": {
                                    "id": "STMG",
                                    "nom": "Série STMG"
                                  },
                                  "nombreAdmis": 15
                                },
                                {
                                  "baccalaureat": {
                                    "id": "STI2D",
                                    "nom": "Série STI2D"
                                  },
                                  "nombreAdmis": 223
                                }
                              ]
                            },
                            "liens": [
                              {
                                "nom": "Voir sur l'ONISEP",
                                "url": "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/cycle-pluridisciplinaire-d-etudes-superieures"
                              }
                            ],
                            "voeux": [
                              {
                                "id": "ta10",
                                "nom": "Nom du ta10",
                                "commune": {
                                  "nom": "Lyon",
                                  "codeInsee": "69123"
                                }
                              },
                              {
                                "id": "ta3",
                                "nom": "Nom du ta3",
                                "commune": {
                                  "nom": "Paris",
                                  "codeInsee": "75105"
                                }
                              },
                              {
                                "id": "ta11",
                                "nom": "Nom du ta11",
                                "commune": {
                                  "nom": "Lyon",
                                  "codeInsee": "69123"
                                }
                              },
                              {
                                "id": "ta32",
                                "nom": "Nom du ta32",
                                "commune": {
                                  "nom": "Paris",
                                  "codeInsee": "75115"
                                }
                              },
                              {
                                "id": "ta17",
                                "nom": "Nom du ta17",
                                "commune": {
                                  "nom": "Strasbourg",
                                  "codeInsee": "67482"
                                }
                              },
                              {
                                "id": "ta7",
                                "nom": "Nom du ta7",
                                "commune": {
                                  "nom": "Marseille",
                                  "codeInsee": "13055"
                                }
                              }
                            ],
                            "communesFavoritesAvecLeursVoeux": [
                              {
                                "commune": {
                                  "codeInsee": "75115",
                                  "nom": "Paris",
                                  "latitude": 48.851227,
                                  "longitude": 2.2885659
                                },
                                "voeuxAvecDistance": [
                                  {
                                    "voeu": {
                                      "id": "ta3",
                                      "nom": "Nom du ta3",
                                      "commune": {
                                        "nom": "Paris",
                                        "codeInsee": "75105"
                                      }
                                    },
                                    "distanceKm": 3
                                  },
                                  {
                                    "voeu": {
                                      "id": "ta32",
                                      "nom": "Nom du ta32",
                                      "commune": {
                                        "nom": "Paris",
                                        "codeInsee": "75115"
                                      }
                                    },
                                    "distanceKm": 1
                                  }
                                ]
                              }
                            ],
                            "metiers": [
                              {
                                "id": "MET001",
                                "nom": "géomaticien/ne",
                                "descriptif": "À la croisée de la géographie et de l'informatique, le géomaticien ou la géomaticienne exploite les données pour modéliser le territoire",
                                "liens": [
                                  {
                                    "nom": "Voir sur l'ONISEP",
                                    "url": "https://www.onisep.fr/ressources/univers-metier/metiers/geomaticien-geomaticienne"
                                  }
                                ]
                              },
                              {
                                "id": "MET002",
                                "nom": "documentaliste",
                                "descriptif": null,
                                "liens": []
                              }
                            ],
                            "tauxAffinite": 90,
                            "apprentissage": true
                          },
                          "explications": {
                            "geographique": [
                              {
                                "nomVille": "Nantes",
                                "distanceKm": 1
                              },
                              {
                                "nomVille": "Paris",
                                "distanceKm": 3
                              }
                            ],
                            "formationsSimilaires": [
                              {
                                "id": "fl1",
                                "nom": "CPGE MPSI"
                              },
                              {
                                "id": "fl7",
                                "nom": "BUT Informatique"
                              }
                            ],
                            "dureeEtudesPrevue": "longue",
                            "alternance": "tres_interesse",
                            "interetsEtDomainesChoisis": {
                              "interets": [
                                {
                                  "id": "aider_autres",
                                  "nom": "Aider les autres"
                                }
                              ],
                              "domaines": [
                                {
                                  "id": "T_ITM_1356",
                                  "nom": "soin aux animaux",
                                  "emoji": "\uD83D\uDC2E"
                                }
                              ]
                            },
                            "specialitesChoisies": [
                              {
                                "nomSpecialite": "specialiteA",
                                "pourcentage": 12
                              },
                              {
                                "nomSpecialite": "specialiteB",
                                "pourcentage": 1
                              },
                              {
                                "nomSpecialite": "specialiteC",
                                "pourcentage": 89
                              }
                            ],
                            "typeBaccalaureat": {
                              "baccalaureat": {
                                "id": "Générale",
                                "nom": "Série Générale"
                              },
                              "pourcentage": 18
                            },
                            "autoEvaluationMoyenne": {
                              "moyenne": 15.0,
                              "basIntervalleNotes": 14.0,
                              "hautIntervalleNotes": 16.0,
                              "baccalaureatUtilise": {
                                "id": "Générale",
                                "nom": "Série Générale"
                              }
                            },
                            "detailsCalculScore": {
                              "details": []
                            }
                          }
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEnseignant(idEnseignant = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le service réussi pour un enseignant, doit retourner 200 avec le détail de la formation`() {
            // Given
            given(recupererFormationService.recupererFormation(unProfilEleve, "fl680002")).willReturn(ficheFormation)

            // When & Then
            mvc.perform(
                get("/api/v1/formations/fl680002"),
            ).andDo(print()).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "formation": {
                            "id": "fl680002",
                            "nom": "Cycle pluridisciplinaire d'Études Supérieures - Science",
                            "idsFormationsAssociees": [
                              "fl0012"
                            ],
                            "descriptifFormation": "Les formations CPES recrutent des lycéen.nes de très bon niveau sur sélection et dispensent des enseignements pluri-disciplinaires (scientifiques, artistiques, de sciences sociales, de littérature) permettant une poursuite d'études en master ou en grande école. Il s’agit de formations ouvertes socialement recrutant 40% de boursiers sur critères sociaux. Elles sont organisées conjointement par un établissement d’enseignement secondaire lycée et un établissement de l’enseignement supérieur, une université.",
                            "descriptifDiplome": "Les formations CPES sont des diplômes d’établissement diplômants en trois ans qui conférent le grade de licence.",
                            "descriptifConseils": "Nous vous conseillons de développer une sensibilité artistique et de rester informé des tendances actuelles en matière de design floral pour exceller dans ce domaine.",
                            "descriptifAttendus": "Il est attendu des candidats de démontrer une solide compréhension des techniques de base de la floristerie, y compris la composition florale, la reconnaissance des plantes et des fleurs, ainsi que les soins et l'entretien des végétaux.",
                            "moyenneGeneraleDesAdmis": {
                              "baccalaureat": {
                                "id": "Générale",
                                "nom": "Série Générale"
                              },
                              "centiles": [
                                {
                                  "centile": 5,
                                  "note": 13.0
                                },
                                {
                                  "centile": 25,
                                  "note": 14.5
                                },
                                {
                                  "centile": 75,
                                  "note": 17.0
                                },
                                {
                                  "centile": 95,
                                  "note": 18.0
                                }
                              ]
                            },
                            "criteresAnalyseCandidature": [
                              {
                                "nom": "Compétences académiques",
                                "pourcentage": 10
                              },
                              {
                                "nom": "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                                "pourcentage": 0
                              },
                              {
                                "nom": "Résultats académiques",
                                "pourcentage": 18
                              },
                              {
                                "nom": "Savoir-être",
                                "pourcentage": 42
                              },
                              {
                                "nom": "Motivation, connaissance",
                                "pourcentage": 30
                              }
                            ],
                            "repartitionAdmisAnneePrecedente": {
                              "total": 6915,
                              "parBaccalaureat": [
                                {
                                  "baccalaureat": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  },
                                  "nombreAdmis": 6677
                                },
                                {
                                  "baccalaureat": {
                                    "id": "STMG",
                                    "nom": "Série STMG"
                                  },
                                  "nombreAdmis": 15
                                },
                                {
                                  "baccalaureat": {
                                    "id": "STI2D",
                                    "nom": "Série STI2D"
                                  },
                                  "nombreAdmis": 223
                                }
                              ]
                            },
                            "liens": [
                              {
                                "nom": "Voir sur l'ONISEP",
                                "url": "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/cycle-pluridisciplinaire-d-etudes-superieures"
                              }
                            ],
                            "voeux": [
                              {
                                "id": "ta10",
                                "nom": "Nom du ta10",
                                "commune": {
                                  "nom": "Lyon",
                                  "codeInsee": "69123"
                                }
                              },
                              {
                                "id": "ta3",
                                "nom": "Nom du ta3",
                                "commune": {
                                  "nom": "Paris",
                                  "codeInsee": "75105"
                                }
                              },
                              {
                                "id": "ta11",
                                "nom": "Nom du ta11",
                                "commune": {
                                  "nom": "Lyon",
                                  "codeInsee": "69123"
                                }
                              },
                              {
                                "id": "ta32",
                                "nom": "Nom du ta32",
                                "commune": {
                                  "nom": "Paris",
                                  "codeInsee": "75115"
                                }
                              },
                              {
                                "id": "ta17",
                                "nom": "Nom du ta17",
                                "commune": {
                                  "nom": "Strasbourg",
                                  "codeInsee": "67482"
                                }
                              },
                              {
                                "id": "ta7",
                                "nom": "Nom du ta7",
                                "commune": {
                                  "nom": "Marseille",
                                  "codeInsee": "13055"
                                }
                              }
                            ],
                            "communesFavoritesAvecLeursVoeux": [
                              {
                                "commune": {
                                  "codeInsee": "75115",
                                  "nom": "Paris",
                                  "latitude": 48.851227,
                                  "longitude": 2.2885659
                                },
                                "voeuxAvecDistance": [
                                  {
                                    "voeu": {
                                      "id": "ta3",
                                      "nom": "Nom du ta3",
                                      "commune": {
                                        "nom": "Paris",
                                        "codeInsee": "75105"
                                      }
                                    },
                                    "distanceKm": 3
                                  },
                                  {
                                    "voeu": {
                                      "id": "ta32",
                                      "nom": "Nom du ta32",
                                      "commune": {
                                        "nom": "Paris",
                                        "codeInsee": "75115"
                                      }
                                    },
                                    "distanceKm": 1
                                  }
                                ]
                              }
                            ],
                            "metiers": [
                              {
                                "id": "MET001",
                                "nom": "géomaticien/ne",
                                "descriptif": "À la croisée de la géographie et de l'informatique, le géomaticien ou la géomaticienne exploite les données pour modéliser le territoire",
                                "liens": [
                                  {
                                    "nom": "Voir sur l'ONISEP",
                                    "url": "https://www.onisep.fr/ressources/univers-metier/metiers/geomaticien-geomaticienne"
                                  }
                                ]
                              },
                              {
                                "id": "MET002",
                                "nom": "documentaliste",
                                "descriptif": null,
                                "liens": []
                              }
                            ],
                            "tauxAffinite": 90,
                            "apprentissage": true
                          },
                          "explications": {
                            "geographique": [
                              {
                                "nomVille": "Nantes",
                                "distanceKm": 1
                              },
                              {
                                "nomVille": "Paris",
                                "distanceKm": 3
                              }
                            ],
                            "formationsSimilaires": [
                              {
                                "id": "fl1",
                                "nom": "CPGE MPSI"
                              },
                              {
                                "id": "fl7",
                                "nom": "BUT Informatique"
                              }
                            ],
                            "dureeEtudesPrevue": "longue",
                            "alternance": "tres_interesse",
                            "interetsEtDomainesChoisis": {
                              "interets": [
                                {
                                  "id": "aider_autres",
                                  "nom": "Aider les autres"
                                }
                              ],
                              "domaines": [
                                {
                                  "id": "T_ITM_1356",
                                  "nom": "soin aux animaux",
                                  "emoji": "\uD83D\uDC2E"
                                }
                              ]
                            },
                            "specialitesChoisies": [
                              {
                                "nomSpecialite": "specialiteA",
                                "pourcentage": 12
                              },
                              {
                                "nomSpecialite": "specialiteB",
                                "pourcentage": 1
                              },
                              {
                                "nomSpecialite": "specialiteC",
                                "pourcentage": 89
                              }
                            ],
                            "typeBaccalaureat": {
                              "baccalaureat": {
                                "id": "Générale",
                                "nom": "Série Générale"
                              },
                              "pourcentage": 18
                            },
                            "autoEvaluationMoyenne": {
                              "moyenne": 15.0,
                              "basIntervalleNotes": 14.0,
                              "hautIntervalleNotes": 16.0,
                              "baccalaureatUtilise": {
                                "id": "Générale",
                                "nom": "Série Générale"
                              }
                            },
                            "detailsCalculScore": {
                              "details": []
                            }
                          }
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteSansId
        @Test
        fun `si connecté sans profil, doit retourner 200 avec la formation sans explications`() {
            given(
                recupererFormationService.recupererFormation(profilEleve = null, idFormation = "fl680002"),
            ).willReturn(ficheFormationSansProfil)

            // When & Then
            mvc.perform(
                get("/api/v1/formations/fl680002"),
            ).andDo(print()).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "formation": {
                            "id": "fl680002",
                            "nom": "Cycle pluridisciplinaire d'Études Supérieures - Science",
                            "idsFormationsAssociees": [
                              "fl0012"
                            ],
                            "descriptifFormation": "Les formations CPES recrutent des lycéen.nes de très bon niveau sur sélection et dispensent des enseignements pluri-disciplinaires (scientifiques, artistiques, de sciences sociales, de littérature) permettant une poursuite d'études en master ou en grande école. Il s’agit de formations ouvertes socialement recrutant 40% de boursiers sur critères sociaux. Elles sont organisées conjointement par un établissement d’enseignement secondaire lycée et un établissement de l’enseignement supérieur, une université.",
                            "descriptifDiplome": "Les formations CPES sont des diplômes d’établissement diplômants en trois ans qui conférent le grade de licence.",
                            "descriptifConseils": "Nous vous conseillons de développer une sensibilité artistique et de rester informé des tendances actuelles en matière de design floral pour exceller dans ce domaine.",
                            "descriptifAttendus": "Il est attendu des candidats de démontrer une solide compréhension des techniques de base de la floristerie, y compris la composition florale, la reconnaissance des plantes et des fleurs, ainsi que les soins et l'entretien des végétaux.",
                            "moyenneGeneraleDesAdmis": null,
                            "criteresAnalyseCandidature": [
                              {
                                "nom": "Compétences académiques",
                                "pourcentage": 10
                              },
                              {
                                "nom": "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                                "pourcentage": 0
                              },
                              {
                                "nom": "Résultats académiques",
                                "pourcentage": 18
                              },
                              {
                                "nom": "Savoir-être",
                                "pourcentage": 42
                              },
                              {
                                "nom": "Motivation, connaissance",
                                "pourcentage": 30
                              }
                            ],
                            "repartitionAdmisAnneePrecedente": {
                              "total": 12,
                              "parBaccalaureat": []
                            },
                            "liens": [
                              {
                                "nom": "Voir sur l'ONISEP",
                                "url": "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/cycle-pluridisciplinaire-d-etudes-superieures"
                              }
                            ],
                            "voeux": [
                              {
                                "id": "ta10",
                                "nom": "Nom du ta10",
                                "commune": {
                                  "nom": "Lyon",
                                  "codeInsee": "69123"
                                }
                              },
                              {
                                "id": "ta3",
                                "nom": "Nom du ta3",
                                "commune": {
                                  "nom": "Paris",
                                  "codeInsee": "75105"
                                }
                              },
                              {
                                "id": "ta11",
                                "nom": "Nom du ta11",
                                "commune": {
                                  "nom": "Lyon",
                                  "codeInsee": "69123"
                                }
                              },
                              {
                                "id": "ta32",
                                "nom": "Nom du ta32",
                                "commune": {
                                  "nom": "Paris",
                                  "codeInsee": "75115"
                                }
                              },
                              {
                                "id": "ta17",
                                "nom": "Nom du ta17",
                                "commune": {
                                  "nom": "Strasbourg",
                                  "codeInsee": "67482"
                                }
                              },
                              {
                                "id": "ta7",
                                "nom": "Nom du ta7",
                                "commune": {
                                  "nom": "Marseille",
                                  "codeInsee": "13055"
                                }
                              }
                            ],
                            "communesFavoritesAvecLeursVoeux": [],
                            "metiers": [
                              {
                                "id": "MET001",
                                "nom": "géomaticien/ne",
                                "descriptif": "À la croisée de la géographie et de l'informatique, le géomaticien ou la géomaticienne exploite les données pour modéliser le territoire",
                                "liens": [
                                  {
                                    "nom": "Voir sur l'ONISEP",
                                    "url": "https://www.onisep.fr/ressources/univers-metier/metiers/geomaticien-geomaticienne"
                                  }
                                ]
                              },
                              {
                                "id": "MET002",
                                "nom": "documentaliste",
                                "descriptif": null,
                                "liens": []
                              }
                            ],
                            "tauxAffinite": null,
                            "apprentissage": false
                          },
                          "explications": null
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @Test
        fun `si pas connecté, doit retourner 401`() {
            // When & Then
            mvc.perform(get("/api/v1/formations/fl68000")).andDo(print()).andExpect(status().isUnauthorized)
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le service échoue avec une erreur interne, alors doit retourner 500`() {
            // Given
            val uneException =
                MonProjetIllegalStateErrorException(
                    code = "RECHERCHE_FORMATION",
                    msg = "La formation fl00010 existe plusieurs fois entre id et dans les formations équivalentes",
                )
            given(recupererFormationService.recupererFormation(unProfilEleve, "fl00010")).willThrow(uneException)

            // When & Then
            mvc.perform(
                get("/api/v1/formations/fl00010"),
            ).andDo(print()).andExpect(status().isInternalServerError)
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le service échoue avec une erreur not found, alors doit retourner 404`() {
            // Given
            val uneException =
                MonProjetSupNotFoundException(
                    code = "RECHERCHE_FORMATION",
                    msg = "La formation inconnu n'existe pas",
                )
            given(recupererFormationService.recupererFormation(unProfilEleve, "inconnu")).willThrow(uneException)

            // When & Then
            mvc.perform(
                get("/api/v1/formations/inconnu"),
            ).andDo(print()).andExpect(status().isNotFound)
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
        }
    }

    @Nested
    inner class `Quand on appelle la route de recherche succincte de formations` {
        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le service réussi pour un appel avec un profil, doit retourner 200 avec la listes de formations triées selon le profil`() {
            // Given
            val mapRechercheL1 =
                mapOf(
                    FormationCourte(id = "fl1", nom = "L1 - Psychologie") to 100,
                    FormationCourte(id = "fl3", nom = "L1 - Philosophie") to 100,
                    FormationCourte(id = "fl7", nom = "L1 - Mathématique") to 100,
                )
            given(
                rechercherFormation.rechercheLesFormationsAvecLeurScoreCorrespondantes(
                    recherche = "L1",
                    tailleMinimumRecherche = 2,
                ),
            ).willReturn(mapRechercheL1)
            val suggestionsPourUnProfil = mock(SuggestionsPourUnProfil::class.java)
            val formationsOrdonnees =
                listOf(
                    FormationAvecSonAffinite("fl7", 0.56f),
                    FormationAvecSonAffinite("fl3", 0.36f),
                    FormationAvecSonAffinite("fl1", 1f),
                )
            given(suggestionsPourUnProfil.formations).willReturn(formationsOrdonnees)
            given(suggestionsFormationsService.recupererLesSuggestionsPourUnProfil(unProfilEleve)).willReturn(suggestionsPourUnProfil)
            val rechercheTriee =
                listOf(
                    FormationCourte(id = "fl1", nom = "L1 - Psychologie"),
                    FormationCourte(id = "fl7", nom = "L1 - Mathématique"),
                    FormationCourte(id = "fl3", nom = "L1 - Philosophie"),
                )
            given(ordonnerRechercheFormationsBuilder.trierParScoreEtSelonSuggestionsProfil(mapRechercheL1, formationsOrdonnees))
                .willReturn(rechercheTriee)

            val hateoas =
                Hateoas(
                    pageActuelle = 1,
                    pageSuivante = null,
                    premierePage = 1,
                    dernierePage = 1,
                    listeCoupee = rechercheTriee,
                )
            given(hateoasBuilder.creerHateoas(liste = rechercheTriee, numeroDePageActuelle = 1, tailleLot = 30)).willReturn(
                hateoas,
            )

            // When & Then
            mvc.perform(
                get("/api/v1/formations/recherche/succincte?recherche=L1"),
            ).andDo(print()).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "formations": [
                            {
                              "id": "fl1",
                              "nom": "L1 - Psychologie"
                            },
                            {
                              "id": "fl7",
                              "nom": "L1 - Mathématique"
                            },
                            {
                              "id": "fl3",
                              "nom": "L1 - Philosophie"
                            }
                          ],
                          "liens": [
                            {
                              "rel": "premier",
                              "href": "http://localhost/api/v1/formations/recherche/succincte?recherche=L1&numeroDePage=1"
                            },
                            {
                              "rel": "dernier",
                              "href": "http://localhost/api/v1/formations/recherche/succincte?recherche=L1&numeroDePage=1"
                            },
                            {
                              "rel": "actuel",
                              "href": "http://localhost/api/v1/formations/recherche/succincte?recherche=L1&numeroDePage=1"
                            }
                          ]
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEnseignant(idEnseignant = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le service réussi pour un enseignant, doit retourner 200 avec le détail de la formation`() {
            // Given
            val rechercheDe50Caracteres = "Lorem ipsum dolor sit amet, consectetur porta ante"
            val rechercheLongueMap =
                mapOf(
                    FormationCourte(id = "fl1", nom = "L1 - Psychologie") to 100,
                    FormationCourte(id = "fl7", nom = "L1 - Philosophie") to 110,
                    FormationCourte(id = "fl3", nom = "CAP Pâtisserie") to 75,
                    FormationCourte(id = "fl1000", nom = "BPJEPS") to 150,
                    FormationCourte(id = "fl17", nom = "L1 - Mathématique") to 10,
                    FormationCourte(id = "fl20", nom = "CAP Boulangerie") to 0,
                    FormationCourte(id = "fl10", nom = "DUT Informatique") to 150,
                    FormationCourte(id = "fl18", nom = "L1 - Littérature") to 110,
                )
            val suggestionsPourUnProfil = mock(SuggestionsPourUnProfil::class.java)
            val formationsOrdonnees =
                listOf(
                    FormationAvecSonAffinite("fl1", 0.56f),
                    FormationAvecSonAffinite("fl7", 0.36f),
                    FormationAvecSonAffinite("fl3", 1f),
                    FormationAvecSonAffinite("fl1000", 0f),
                    FormationAvecSonAffinite("fl17", 0.24f),
                    FormationAvecSonAffinite("fl20", 0.98f),
                    FormationAvecSonAffinite("fl10", 0.24f),
                    FormationAvecSonAffinite("fl18", 0.45f),
                )
            given(suggestionsPourUnProfil.formations).willReturn(formationsOrdonnees)
            given(suggestionsFormationsService.recupererLesSuggestionsPourUnProfil(unProfilEleve)).willReturn(suggestionsPourUnProfil)
            val rechercheTriee =
                listOf(
                    FormationCourte(id = "fl10", nom = "DUT Informatique"),
                    FormationCourte(id = "fl1000", nom = "BPJEPS"),
                    FormationCourte(id = "fl18", nom = "L1 - Littérature"),
                    FormationCourte(id = "fl7", nom = "L1 - Philosophie"),
                    FormationCourte(id = "fl1", nom = "L1 - Psychologie"),
                    FormationCourte(id = "fl3", nom = "CAP Pâtisserie"),
                    FormationCourte(id = "fl17", nom = "L1 - Mathématique"),
                    FormationCourte(id = "fl20", nom = "CAP Boulangerie"),
                )
            given(ordonnerRechercheFormationsBuilder.trierParScoreEtSelonSuggestionsProfil(rechercheLongueMap, formationsOrdonnees))
                .willReturn(rechercheTriee)

            given(
                rechercherFormation.rechercheLesFormationsAvecLeurScoreCorrespondantes(
                    recherche = rechercheDe50Caracteres,
                    tailleMinimumRecherche = 2,
                ),
            ).willReturn(rechercheLongueMap)
            val hateoas =
                Hateoas(
                    pageActuelle = 1,
                    pageSuivante = null,
                    premierePage = 1,
                    dernierePage = 1,
                    listeCoupee = rechercheTriee,
                )
            given(hateoasBuilder.creerHateoas(liste = rechercheTriee, numeroDePageActuelle = 1, tailleLot = 30)).willReturn(
                hateoas,
            )

            // When & Then
            mvc.perform(
                get("/api/v1/formations/recherche/succincte?recherche=$rechercheDe50Caracteres"),
            ).andDo(print()).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "formations": [
                            {
                              "id": "fl10",
                              "nom": "DUT Informatique"
                            },
                            {
                              "id": "fl1000",
                              "nom": "BPJEPS"
                            },
                            {
                              "id": "fl18",
                              "nom": "L1 - Littérature"
                            },
                            {
                              "id": "fl7",
                              "nom": "L1 - Philosophie"
                            },
                            {
                              "id": "fl1",
                              "nom": "L1 - Psychologie"
                            },
                            {
                              "id": "fl3",
                              "nom": "CAP Pâtisserie"
                            },
                            {
                              "id": "fl17",
                              "nom": "L1 - Mathématique"
                            },
                            {
                              "id": "fl20",
                              "nom": "CAP Boulangerie"
                            }
                          ],
                          "liens": [
                            {
                              "rel": "premier",
                              "href": "http://localhost/api/v1/formations/recherche/succincte?recherche=Lorem%20ipsum%20dolor%20sit%20amet,%20consectetur%20porta%20ante&numeroDePage=1"
                            },
                            {
                              "rel": "dernier",
                              "href": "http://localhost/api/v1/formations/recherche/succincte?recherche=Lorem%20ipsum%20dolor%20sit%20amet,%20consectetur%20porta%20ante&numeroDePage=1"
                            },
                            {
                              "rel": "actuel",
                              "href": "http://localhost/api/v1/formations/recherche/succincte?recherche=Lorem%20ipsum%20dolor%20sit%20amet,%20consectetur%20porta%20ante&numeroDePage=1"
                            }
                          ]
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le mot recherché fait strictement moins de 2 caractère, doit retourner 400`() {
            // When & Then
            mvc.perform(
                get("/api/v1/formations/recherche/succincte?recherche=L"),
            ).andDo(print()).andExpect(status().isBadRequest).andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "type": "about:blank",
                          "title": "REQUETE_TROP_COURTE",
                          "status": 400,
                          "detail": "La taille de la requête est trop courte. Elle doit faire au moins 2 caractères",
                          "instance": "/api/v1/formations/recherche/succincte"
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le mot recherché fait strictement plus de 50 caractère, doit retourner 400`() {
            // When & Then
            val rechercheDe51Caracteres = "Lorem ipsum dolor sit amet, consectetur sodales sed"
            mvc.perform(
                get("/api/v1/formations/recherche/succincte?recherche=$rechercheDe51Caracteres"),
            ).andDo(print()).andExpect(status().isBadRequest).andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "type": "about:blank",
                          "title": "REQUETE_TROP_LONGUE",
                          "status": 400,
                          "detail": "La taille de la requête dépasse la taille maximale de 50 caractères",
                          "instance": "/api/v1/formations/recherche/succincte"
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteSansId
        @Test
        fun `si connecté sans profil, doit retourner 200 avec la liste des formations`() {
            // Given
            val rechercheDe50Caracteres = "Lorem ipsum dolor sit amet, consectetur porta ante"
            val mapRechercheLongue =
                mapOf(
                    FormationCourte(id = "fl1", nom = "L1 - Psychologie") to 1,
                    FormationCourte(id = "fl7", nom = "L1 - Philosophie") to 1,
                    FormationCourte(id = "fl3", nom = "CAP Pâtisserie") to 1,
                    FormationCourte(id = "fl1000", nom = "BPJEPS") to 1,
                    FormationCourte(id = "fl17", nom = "L1 - Mathématique") to 1,
                    FormationCourte(id = "fl20", nom = "CAP Boulangerie") to 1,
                    FormationCourte(id = "fl10", nom = "DUT Informatique") to 1,
                    FormationCourte(id = "fl18", nom = "L1 - Littérature") to 1,
                )
            given(
                rechercherFormation.rechercheLesFormationsAvecLeurScoreCorrespondantes(
                    recherche = rechercheDe50Caracteres,
                    tailleMinimumRecherche = 2,
                ),
            ).willReturn(mapRechercheLongue)
            val rechercheLongue =
                listOf(
                    FormationCourte(id = "fl1", nom = "L1 - Psychologie"),
                    FormationCourte(id = "fl7", nom = "L1 - Philosophie"),
                    FormationCourte(id = "fl3", nom = "CAP Pâtisserie"),
                    FormationCourte(id = "fl1000", nom = "BPJEPS"),
                    FormationCourte(id = "fl17", nom = "L1 - Mathématique"),
                    FormationCourte(id = "fl20", nom = "CAP Boulangerie"),
                    FormationCourte(id = "fl10", nom = "DUT Informatique"),
                    FormationCourte(id = "fl18", nom = "L1 - Littérature"),
                )
            given(ordonnerRechercheFormationsBuilder.trierParScore(mapRechercheLongue)).willReturn(rechercheLongue)
            val hateoas =
                Hateoas(
                    pageActuelle = 1,
                    pageSuivante = null,
                    premierePage = 1,
                    dernierePage = 1,
                    listeCoupee = rechercheLongue,
                )
            given(hateoasBuilder.creerHateoas(liste = rechercheLongue, numeroDePageActuelle = 1, tailleLot = 30)).willReturn(
                hateoas,
            )

            // When & Then
            mvc.perform(
                get("/api/v1/formations/recherche/succincte?recherche=$rechercheDe50Caracteres"),
            ).andDo(print()).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "formations": [
                            {
                              "id": "fl1",
                              "nom": "L1 - Psychologie"
                            },
                            {
                              "id": "fl7",
                              "nom": "L1 - Philosophie"
                            },
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
                            },
                            {
                              "id": "fl20",
                              "nom": "CAP Boulangerie"
                            },
                            {
                              "id": "fl10",
                              "nom": "DUT Informatique"
                            },
                            {
                              "id": "fl18",
                              "nom": "L1 - Littérature"
                            }
                          ],
                          "liens": [
                            {
                              "rel": "premier",
                              "href": "http://localhost/api/v1/formations/recherche/succincte?recherche=Lorem%20ipsum%20dolor%20sit%20amet,%20consectetur%20porta%20ante&numeroDePage=1"
                            },
                            {
                              "rel": "dernier",
                              "href": "http://localhost/api/v1/formations/recherche/succincte?recherche=Lorem%20ipsum%20dolor%20sit%20amet,%20consectetur%20porta%20ante&numeroDePage=1"
                            },
                            {
                              "rel": "actuel",
                              "href": "http://localhost/api/v1/formations/recherche/succincte?recherche=Lorem%20ipsum%20dolor%20sit%20amet,%20consectetur%20porta%20ante&numeroDePage=1"
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
            mvc.perform(
                get("/api/v1/formations/recherche/succincte?recherche=test"),
            ).andDo(print()).andExpect(status().isUnauthorized)
        }
    }

    @Nested
    inner class `Quand on appelle la route de recherche détaillée de formations` {
        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le service réussi pour un appel avec un profil, doit retourner 200 avec la listes de formations`() {
            // Given
            val mapRechercheL1 =
                mapOf(
                    FormationCourte(id = "fl1", nom = "L1 - Psychologie") to 1,
                    FormationCourte(id = "fl7", nom = "L1 - Philosophie") to 1,
                )
            val rechercheL1 =
                listOf(
                    FormationCourte(id = "fl1", nom = "L1 - Psychologie"),
                    FormationCourte(id = "fl7", nom = "L1 - Philosophie"),
                )
            given(
                rechercherFormation.rechercheLesFormationsAvecLeurScoreCorrespondantes(
                    recherche = "L1",
                    tailleMinimumRecherche = 2,
                ),
            ).willReturn(mapRechercheL1)
            val toutesLesSuggestions = mock(SuggestionsPourUnProfil::class.java)
            val formationsTriees =
                listOf(
                    FormationAvecSonAffinite("fl7", 0.87f),
                    FormationAvecSonAffinite("fl1", 0.65f),
                )
            given(toutesLesSuggestions.formations).willReturn(formationsTriees)
            given(suggestionsFormationsService.recupererLesSuggestionsPourUnProfil(unProfilEleve)).willReturn(toutesLesSuggestions)
            given(ordonnerRechercheFormationsBuilder.trierParScoreEtSelonSuggestionsProfil(mapRechercheL1, formationsTriees)).willReturn(
                rechercheL1,
            )
            given(suggestionsFormationsService.recupererLesSuggestionsPourUnProfil(unProfilEleve)).willReturn(toutesLesSuggestions)
            val fichesFormations =
                listOf(
                    ficheFormation.copy(id = "fl1"),
                    ficheFormation.copy(
                        id = "fl7",
                        nom = "2eme formation",
                        descriptifGeneral = null,
                        descriptifAttendus = null,
                        descriptifDiplome = null,
                        descriptifConseils = null,
                        formationsAssociees = listOf("fl3"),
                        liens = emptyList(),
                        criteresAnalyseCandidature = emptyList(),
                        statistiquesDesAdmis = null,
                        tauxAffinite = 17,
                        metiersTriesParAffinites = emptyList(),
                        apprentissage = false,
                        explications = null,
                    ),
                )
            given(
                recupererFormationsService.recupererFichesFormationPourProfil(
                    unProfilEleve,
                    toutesLesSuggestions,
                    listOf("fl1", "fl7"),
                    false,
                ),
            ).willReturn(fichesFormations)
            val hateoas =
                Hateoas(
                    pageActuelle = 1,
                    pageSuivante = null,
                    premierePage = 1,
                    dernierePage = 1,
                    listeCoupee = listOf("fl1", "fl7"),
                )
            given(hateoasBuilder.creerHateoas(liste = listOf("fl1", "fl7"), numeroDePageActuelle = 1, tailleLot = 30)).willReturn(
                hateoas,
            )

            // When & Then
            mvc.perform(
                get("/api/v1/formations/recherche/detaillee?recherche=L1"),
            ).andDo(print()).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "formations": [
                            {
                              "formation": {
                                "id": "fl1",
                                "nom": "Cycle pluridisciplinaire d'Études Supérieures - Science",
                                "idsFormationsAssociees": [
                                  "fl0012"
                                ],
                                "descriptifFormation": "Les formations CPES recrutent des lycéen.nes de très bon niveau sur sélection et dispensent des enseignements pluri-disciplinaires (scientifiques, artistiques, de sciences sociales, de littérature) permettant une poursuite d'études en master ou en grande école. Il s’agit de formations ouvertes socialement recrutant 40% de boursiers sur critères sociaux. Elles sont organisées conjointement par un établissement d’enseignement secondaire lycée et un établissement de l’enseignement supérieur, une université.",
                                "descriptifDiplome": "Les formations CPES sont des diplômes d’établissement diplômants en trois ans qui conférent le grade de licence.",
                                "descriptifConseils": "Nous vous conseillons de développer une sensibilité artistique et de rester informé des tendances actuelles en matière de design floral pour exceller dans ce domaine.",
                                "descriptifAttendus": "Il est attendu des candidats de démontrer une solide compréhension des techniques de base de la floristerie, y compris la composition florale, la reconnaissance des plantes et des fleurs, ainsi que les soins et l'entretien des végétaux.",
                                "moyenneGeneraleDesAdmis": {
                                  "baccalaureat": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  },
                                  "centiles": [
                                    {
                                      "centile": 5,
                                      "note": 13.0
                                    },
                                    {
                                      "centile": 25,
                                      "note": 14.5
                                    },
                                    {
                                      "centile": 75,
                                      "note": 17.0
                                    },
                                    {
                                      "centile": 95,
                                      "note": 18.0
                                    }
                                  ]
                                },
                                "criteresAnalyseCandidature": [
                                  {
                                    "nom": "Compétences académiques",
                                    "pourcentage": 10
                                  },
                                  {
                                    "nom": "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                                    "pourcentage": 0
                                  },
                                  {
                                    "nom": "Résultats académiques",
                                    "pourcentage": 18
                                  },
                                  {
                                    "nom": "Savoir-être",
                                    "pourcentage": 42
                                  },
                                  {
                                    "nom": "Motivation, connaissance",
                                    "pourcentage": 30
                                  }
                                ],
                                "repartitionAdmisAnneePrecedente": {
                                  "total": 6915,
                                  "parBaccalaureat": [
                                    {
                                      "baccalaureat": {
                                        "id": "Générale",
                                        "nom": "Série Générale"
                                      },
                                      "nombreAdmis": 6677
                                    },
                                    {
                                      "baccalaureat": {
                                        "id": "STMG",
                                        "nom": "Série STMG"
                                      },
                                      "nombreAdmis": 15
                                    },
                                    {
                                      "baccalaureat": {
                                        "id": "STI2D",
                                        "nom": "Série STI2D"
                                      },
                                      "nombreAdmis": 223
                                    }
                                  ]
                                },
                                "liens": [
                                  {
                                    "nom": "Voir sur l'ONISEP",
                                    "url": "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/cycle-pluridisciplinaire-d-etudes-superieures"
                                  }
                                ],
                                "voeux": [
                                  {
                                    "id": "ta10",
                                    "nom": "Nom du ta10",
                                    "commune": {
                                      "nom": "Lyon",
                                      "codeInsee": "69123"
                                    }
                                  },
                                  {
                                    "id": "ta3",
                                    "nom": "Nom du ta3",
                                    "commune": {
                                      "nom": "Paris",
                                      "codeInsee": "75105"
                                    }
                                  },
                                  {
                                    "id": "ta11",
                                    "nom": "Nom du ta11",
                                    "commune": {
                                      "nom": "Lyon",
                                      "codeInsee": "69123"
                                    }
                                  },
                                  {
                                    "id": "ta32",
                                    "nom": "Nom du ta32",
                                    "commune": {
                                      "nom": "Paris",
                                      "codeInsee": "75115"
                                    }
                                  },
                                  {
                                    "id": "ta17",
                                    "nom": "Nom du ta17",
                                    "commune": {
                                      "nom": "Strasbourg",
                                      "codeInsee": "67482"
                                    }
                                  },
                                  {
                                    "id": "ta7",
                                    "nom": "Nom du ta7",
                                    "commune": {
                                      "nom": "Marseille",
                                      "codeInsee": "13055"
                                    }
                                  }
                                ],
                                "communesFavoritesAvecLeursVoeux": [
                                  {
                                    "commune": {
                                      "codeInsee": "75115",
                                      "nom": "Paris",
                                      "latitude": 48.851227,
                                      "longitude": 2.2885659
                                    },
                                    "voeuxAvecDistance": [
                                      {
                                        "voeu": {
                                          "id": "ta3",
                                          "nom": "Nom du ta3",
                                          "commune": {
                                            "nom": "Paris",
                                            "codeInsee": "75105"
                                          }
                                        },
                                        "distanceKm": 3
                                      },
                                      {
                                        "voeu": {
                                          "id": "ta32",
                                          "nom": "Nom du ta32",
                                          "commune": {
                                            "nom": "Paris",
                                            "codeInsee": "75115"
                                          }
                                        },
                                        "distanceKm": 1
                                      }
                                    ]
                                  }
                                ],
                                "metiers": [
                                  {
                                    "id": "MET001",
                                    "nom": "géomaticien/ne",
                                    "descriptif": "À la croisée de la géographie et de l'informatique, le géomaticien ou la géomaticienne exploite les données pour modéliser le territoire",
                                    "liens": [
                                      {
                                        "nom": "Voir sur l'ONISEP",
                                        "url": "https://www.onisep.fr/ressources/univers-metier/metiers/geomaticien-geomaticienne"
                                      }
                                    ]
                                  },
                                  {
                                    "id": "MET002",
                                    "nom": "documentaliste",
                                    "descriptif": null,
                                    "liens": []
                                  }
                                ],
                                "tauxAffinite": 90,
                                "apprentissage": true
                              },
                              "explications": {
                                "geographique": [
                                  {
                                    "nomVille": "Nantes",
                                    "distanceKm": 1
                                  },
                                  {
                                    "nomVille": "Paris",
                                    "distanceKm": 3
                                  }
                                ],
                                "formationsSimilaires": [
                                  {
                                    "id": "fl1",
                                    "nom": "CPGE MPSI"
                                  },
                                  {
                                    "id": "fl7",
                                    "nom": "BUT Informatique"
                                  }
                                ],
                                "dureeEtudesPrevue": "longue",
                                "alternance": "tres_interesse",
                                "interetsEtDomainesChoisis": {
                                  "interets": [
                                    {
                                      "id": "aider_autres",
                                      "nom": "Aider les autres"
                                    }
                                  ],
                                  "domaines": [
                                    {
                                      "id": "T_ITM_1356",
                                      "nom": "soin aux animaux",
                                      "emoji": "\uD83D\uDC2E"
                                    }
                                  ]
                                },
                                "specialitesChoisies": [
                                  {
                                    "nomSpecialite": "specialiteA",
                                    "pourcentage": 12
                                  },
                                  {
                                    "nomSpecialite": "specialiteB",
                                    "pourcentage": 1
                                  },
                                  {
                                    "nomSpecialite": "specialiteC",
                                    "pourcentage": 89
                                  }
                                ],
                                "typeBaccalaureat": {
                                  "baccalaureat": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  },
                                  "pourcentage": 18
                                },
                                "autoEvaluationMoyenne": {
                                  "moyenne": 15.0,
                                  "basIntervalleNotes": 14.0,
                                  "hautIntervalleNotes": 16.0,
                                  "baccalaureatUtilise": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  }
                                },
                                "detailsCalculScore": {
                                  "details": []
                                }
                              }
                            },
                            {
                              "formation": {
                                "id": "fl7",
                                "nom": "2eme formation",
                                "idsFormationsAssociees": [
                                  "fl3"
                                ],
                                "descriptifFormation": null,
                                "descriptifDiplome": null,
                                "descriptifConseils": null,
                                "descriptifAttendus": null,
                                "moyenneGeneraleDesAdmis": null,
                                "criteresAnalyseCandidature": [],
                                "repartitionAdmisAnneePrecedente": null,
                                "liens": [],
                                "voeux": [
                                  {
                                    "id": "ta10",
                                    "nom": "Nom du ta10",
                                    "commune": {
                                      "nom": "Lyon",
                                      "codeInsee": "69123"
                                    }
                                  },
                                  {
                                    "id": "ta3",
                                    "nom": "Nom du ta3",
                                    "commune": {
                                      "nom": "Paris",
                                      "codeInsee": "75105"
                                    }
                                  },
                                  {
                                    "id": "ta11",
                                    "nom": "Nom du ta11",
                                    "commune": {
                                      "nom": "Lyon",
                                      "codeInsee": "69123"
                                    }
                                  },
                                  {
                                    "id": "ta32",
                                    "nom": "Nom du ta32",
                                    "commune": {
                                      "nom": "Paris",
                                      "codeInsee": "75115"
                                    }
                                  },
                                  {
                                    "id": "ta17",
                                    "nom": "Nom du ta17",
                                    "commune": {
                                      "nom": "Strasbourg",
                                      "codeInsee": "67482"
                                    }
                                  },
                                  {
                                    "id": "ta7",
                                    "nom": "Nom du ta7",
                                    "commune": {
                                      "nom": "Marseille",
                                      "codeInsee": "13055"
                                    }
                                  }
                                ],
                                "communesFavoritesAvecLeursVoeux": [
                                  {
                                    "commune": {
                                      "codeInsee": "75115",
                                      "nom": "Paris",
                                      "latitude": 48.851227,
                                      "longitude": 2.2885659
                                    },
                                    "voeuxAvecDistance": [
                                      {
                                        "voeu": {
                                          "id": "ta3",
                                          "nom": "Nom du ta3",
                                          "commune": {
                                            "nom": "Paris",
                                            "codeInsee": "75105"
                                          }
                                        },
                                        "distanceKm": 3
                                      },
                                      {
                                        "voeu": {
                                          "id": "ta32",
                                          "nom": "Nom du ta32",
                                          "commune": {
                                            "nom": "Paris",
                                            "codeInsee": "75115"
                                          }
                                        },
                                        "distanceKm": 1
                                      }
                                    ]
                                  }
                                ],
                                "metiers": [],
                                "tauxAffinite": 17,
                                "apprentissage": false
                              },
                              "explications": null
                            }
                          ],
                          "liens": [
                            {
                              "rel": "premier",
                              "href": "http://localhost/api/v1/formations/recherche/detaillee?recherche=L1&numeroDePage=1"
                            },
                            {
                              "rel": "dernier",
                              "href": "http://localhost/api/v1/formations/recherche/detaillee?recherche=L1&numeroDePage=1"
                            },
                            {
                              "rel": "actuel",
                              "href": "http://localhost/api/v1/formations/recherche/detaillee?recherche=L1&numeroDePage=1"
                            }
                          ]
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEnseignant(idEnseignant = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `pour un enseignant, doit retourner 200 avec le détail des formations associées à la recherche`() {
            // Given
            val rechercheL1 =
                mapOf(
                    FormationCourte(id = "fl1", nom = "L1 - Psychologie") to 1,
                    FormationCourte(id = "fl7", nom = "L1 - Philosophie") to 1,
                )
            given(
                rechercherFormation.rechercheLesFormationsAvecLeurScoreCorrespondantes(
                    recherche = "L1",
                    tailleMinimumRecherche = 2,
                ),
            ).willReturn(rechercheL1)
            val toutesLesSuggestions = mock(SuggestionsPourUnProfil::class.java)
            val formationsTriees =
                listOf(
                    FormationAvecSonAffinite("fl7", 0.87f),
                    FormationAvecSonAffinite("fl1", 0.65f),
                )
            given(toutesLesSuggestions.formations).willReturn(formationsTriees)
            given(suggestionsFormationsService.recupererLesSuggestionsPourUnProfil(unProfilEleve)).willReturn(toutesLesSuggestions)
            given(ordonnerRechercheFormationsBuilder.trierParScoreEtSelonSuggestionsProfil(rechercheL1, formationsTriees)).willReturn(
                listOf(
                    FormationCourte(id = "fl7", nom = "L1 - Philosophie"),
                    FormationCourte(id = "fl1", nom = "L1 - Psychologie"),
                ),
            )
            val fichesFormations =
                listOf(
                    ficheFormation.copy(
                        id = "fl7",
                        nom = "2eme formation",
                        descriptifGeneral = null,
                        descriptifAttendus = null,
                        descriptifDiplome = null,
                        descriptifConseils = null,
                        formationsAssociees = listOf("fl3"),
                        liens = emptyList(),
                        criteresAnalyseCandidature = emptyList(),
                        statistiquesDesAdmis = null,
                        tauxAffinite = 17,
                        metiersTriesParAffinites = emptyList(),
                        apprentissage = false,
                        explications = null,
                    ),
                    ficheFormation.copy(id = "fl1"),
                )
            given(
                recupererFormationsService.recupererFichesFormationPourProfil(
                    unProfilEleve,
                    toutesLesSuggestions,
                    listOf("fl7", "fl1"),
                    false,
                ),
            ).willReturn(fichesFormations)
            val hateoas =
                Hateoas(
                    pageActuelle = 1,
                    pageSuivante = null,
                    premierePage = 1,
                    dernierePage = 1,
                    listeCoupee = listOf("fl7", "fl1"),
                )
            given(hateoasBuilder.creerHateoas(liste = listOf("fl7", "fl1"), numeroDePageActuelle = 1, tailleLot = 30)).willReturn(
                hateoas,
            )

            // When & Then
            mvc.perform(
                get("/api/v1/formations/recherche/detaillee?recherche=L1"),
            ).andDo(print()).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "formations": [
                            {
                              "formation": {
                                "id": "fl7",
                                "nom": "2eme formation",
                                "idsFormationsAssociees": [
                                  "fl3"
                                ],
                                "descriptifFormation": null,
                                "descriptifDiplome": null,
                                "descriptifConseils": null,
                                "descriptifAttendus": null,
                                "moyenneGeneraleDesAdmis": null,
                                "criteresAnalyseCandidature": [],
                                "repartitionAdmisAnneePrecedente": null,
                                "liens": [],
                                "voeux": [
                                  {
                                    "id": "ta10",
                                    "nom": "Nom du ta10",
                                    "commune": {
                                      "nom": "Lyon",
                                      "codeInsee": "69123"
                                    }
                                  },
                                  {
                                    "id": "ta3",
                                    "nom": "Nom du ta3",
                                    "commune": {
                                      "nom": "Paris",
                                      "codeInsee": "75105"
                                    }
                                  },
                                  {
                                    "id": "ta11",
                                    "nom": "Nom du ta11",
                                    "commune": {
                                      "nom": "Lyon",
                                      "codeInsee": "69123"
                                    }
                                  },
                                  {
                                    "id": "ta32",
                                    "nom": "Nom du ta32",
                                    "commune": {
                                      "nom": "Paris",
                                      "codeInsee": "75115"
                                    }
                                  },
                                  {
                                    "id": "ta17",
                                    "nom": "Nom du ta17",
                                    "commune": {
                                      "nom": "Strasbourg",
                                      "codeInsee": "67482"
                                    }
                                  },
                                  {
                                    "id": "ta7",
                                    "nom": "Nom du ta7",
                                    "commune": {
                                      "nom": "Marseille",
                                      "codeInsee": "13055"
                                    }
                                  }
                                ],
                                "communesFavoritesAvecLeursVoeux": [
                                  {
                                    "commune": {
                                      "codeInsee": "75115",
                                      "nom": "Paris",
                                      "latitude": 48.851227,
                                      "longitude": 2.2885659
                                    },
                                    "voeuxAvecDistance": [
                                      {
                                        "voeu": {
                                          "id": "ta3",
                                          "nom": "Nom du ta3",
                                          "commune": {
                                            "nom": "Paris",
                                            "codeInsee": "75105"
                                          }
                                        },
                                        "distanceKm": 3
                                      },
                                      {
                                        "voeu": {
                                          "id": "ta32",
                                          "nom": "Nom du ta32",
                                          "commune": {
                                            "nom": "Paris",
                                            "codeInsee": "75115"
                                          }
                                        },
                                        "distanceKm": 1
                                      }
                                    ]
                                  }
                                ],
                                "metiers": [],
                                "tauxAffinite": 17,
                                "apprentissage": false
                              },
                              "explications": null
                            },
                            {
                              "formation": {
                                "id": "fl1",
                                "nom": "Cycle pluridisciplinaire d'Études Supérieures - Science",
                                "idsFormationsAssociees": [
                                  "fl0012"
                                ],
                                "descriptifFormation": "Les formations CPES recrutent des lycéen.nes de très bon niveau sur sélection et dispensent des enseignements pluri-disciplinaires (scientifiques, artistiques, de sciences sociales, de littérature) permettant une poursuite d'études en master ou en grande école. Il s’agit de formations ouvertes socialement recrutant 40% de boursiers sur critères sociaux. Elles sont organisées conjointement par un établissement d’enseignement secondaire lycée et un établissement de l’enseignement supérieur, une université.",
                                "descriptifDiplome": "Les formations CPES sont des diplômes d’établissement diplômants en trois ans qui conférent le grade de licence.",
                                "descriptifConseils": "Nous vous conseillons de développer une sensibilité artistique et de rester informé des tendances actuelles en matière de design floral pour exceller dans ce domaine.",
                                "descriptifAttendus": "Il est attendu des candidats de démontrer une solide compréhension des techniques de base de la floristerie, y compris la composition florale, la reconnaissance des plantes et des fleurs, ainsi que les soins et l'entretien des végétaux.",
                                "moyenneGeneraleDesAdmis": {
                                  "baccalaureat": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  },
                                  "centiles": [
                                    {
                                      "centile": 5,
                                      "note": 13.0
                                    },
                                    {
                                      "centile": 25,
                                      "note": 14.5
                                    },
                                    {
                                      "centile": 75,
                                      "note": 17.0
                                    },
                                    {
                                      "centile": 95,
                                      "note": 18.0
                                    }
                                  ]
                                },
                                "criteresAnalyseCandidature": [
                                  {
                                    "nom": "Compétences académiques",
                                    "pourcentage": 10
                                  },
                                  {
                                    "nom": "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                                    "pourcentage": 0
                                  },
                                  {
                                    "nom": "Résultats académiques",
                                    "pourcentage": 18
                                  },
                                  {
                                    "nom": "Savoir-être",
                                    "pourcentage": 42
                                  },
                                  {
                                    "nom": "Motivation, connaissance",
                                    "pourcentage": 30
                                  }
                                ],
                                "repartitionAdmisAnneePrecedente": {
                                  "total": 6915,
                                  "parBaccalaureat": [
                                    {
                                      "baccalaureat": {
                                        "id": "Générale",
                                        "nom": "Série Générale"
                                      },
                                      "nombreAdmis": 6677
                                    },
                                    {
                                      "baccalaureat": {
                                        "id": "STMG",
                                        "nom": "Série STMG"
                                      },
                                      "nombreAdmis": 15
                                    },
                                    {
                                      "baccalaureat": {
                                        "id": "STI2D",
                                        "nom": "Série STI2D"
                                      },
                                      "nombreAdmis": 223
                                    }
                                  ]
                                },
                                "liens": [
                                  {
                                    "nom": "Voir sur l'ONISEP",
                                    "url": "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/cycle-pluridisciplinaire-d-etudes-superieures"
                                  }
                                ],
                                "voeux": [
                                  {
                                    "id": "ta10",
                                    "nom": "Nom du ta10",
                                    "commune": {
                                      "nom": "Lyon",
                                      "codeInsee": "69123"
                                    }
                                  },
                                  {
                                    "id": "ta3",
                                    "nom": "Nom du ta3",
                                    "commune": {
                                      "nom": "Paris",
                                      "codeInsee": "75105"
                                    }
                                  },
                                  {
                                    "id": "ta11",
                                    "nom": "Nom du ta11",
                                    "commune": {
                                      "nom": "Lyon",
                                      "codeInsee": "69123"
                                    }
                                  },
                                  {
                                    "id": "ta32",
                                    "nom": "Nom du ta32",
                                    "commune": {
                                      "nom": "Paris",
                                      "codeInsee": "75115"
                                    }
                                  },
                                  {
                                    "id": "ta17",
                                    "nom": "Nom du ta17",
                                    "commune": {
                                      "nom": "Strasbourg",
                                      "codeInsee": "67482"
                                    }
                                  },
                                  {
                                    "id": "ta7",
                                    "nom": "Nom du ta7",
                                    "commune": {
                                      "nom": "Marseille",
                                      "codeInsee": "13055"
                                    }
                                  }
                                ],
                                "communesFavoritesAvecLeursVoeux": [
                                  {
                                    "commune": {
                                      "codeInsee": "75115",
                                      "nom": "Paris",
                                      "latitude": 48.851227,
                                      "longitude": 2.2885659
                                    },
                                    "voeuxAvecDistance": [
                                      {
                                        "voeu": {
                                          "id": "ta3",
                                          "nom": "Nom du ta3",
                                          "commune": {
                                            "nom": "Paris",
                                            "codeInsee": "75105"
                                          }
                                        },
                                        "distanceKm": 3
                                      },
                                      {
                                        "voeu": {
                                          "id": "ta32",
                                          "nom": "Nom du ta32",
                                          "commune": {
                                            "nom": "Paris",
                                            "codeInsee": "75115"
                                          }
                                        },
                                        "distanceKm": 1
                                      }
                                    ]
                                  }
                                ],
                                "metiers": [
                                  {
                                    "id": "MET001",
                                    "nom": "géomaticien/ne",
                                    "descriptif": "À la croisée de la géographie et de l'informatique, le géomaticien ou la géomaticienne exploite les données pour modéliser le territoire",
                                    "liens": [
                                      {
                                        "nom": "Voir sur l'ONISEP",
                                        "url": "https://www.onisep.fr/ressources/univers-metier/metiers/geomaticien-geomaticienne"
                                      }
                                    ]
                                  },
                                  {
                                    "id": "MET002",
                                    "nom": "documentaliste",
                                    "descriptif": null,
                                    "liens": []
                                  }
                                ],
                                "tauxAffinite": 90,
                                "apprentissage": true
                              },
                              "explications": {
                                "geographique": [
                                  {
                                    "nomVille": "Nantes",
                                    "distanceKm": 1
                                  },
                                  {
                                    "nomVille": "Paris",
                                    "distanceKm": 3
                                  }
                                ],
                                "formationsSimilaires": [
                                  {
                                    "id": "fl1",
                                    "nom": "CPGE MPSI"
                                  },
                                  {
                                    "id": "fl7",
                                    "nom": "BUT Informatique"
                                  }
                                ],
                                "dureeEtudesPrevue": "longue",
                                "alternance": "tres_interesse",
                                "interetsEtDomainesChoisis": {
                                  "interets": [
                                    {
                                      "id": "aider_autres",
                                      "nom": "Aider les autres"
                                    }
                                  ],
                                  "domaines": [
                                    {
                                      "id": "T_ITM_1356",
                                      "nom": "soin aux animaux",
                                      "emoji": "\uD83D\uDC2E"
                                    }
                                  ]
                                },
                                "specialitesChoisies": [
                                  {
                                    "nomSpecialite": "specialiteA",
                                    "pourcentage": 12
                                  },
                                  {
                                    "nomSpecialite": "specialiteB",
                                    "pourcentage": 1
                                  },
                                  {
                                    "nomSpecialite": "specialiteC",
                                    "pourcentage": 89
                                  }
                                ],
                                "typeBaccalaureat": {
                                  "baccalaureat": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  },
                                  "pourcentage": 18
                                },
                                "autoEvaluationMoyenne": {
                                  "moyenne": 15.0,
                                  "basIntervalleNotes": 14.0,
                                  "hautIntervalleNotes": 16.0,
                                  "baccalaureatUtilise": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  }
                                },
                                "detailsCalculScore": {
                                  "details": []
                                }
                              }
                            }
                          ],
                          "liens": [
                            {
                              "rel": "premier",
                              "href": "http://localhost/api/v1/formations/recherche/detaillee?recherche=L1&numeroDePage=1"
                            },
                            {
                              "rel": "dernier",
                              "href": "http://localhost/api/v1/formations/recherche/detaillee?recherche=L1&numeroDePage=1"
                            },
                            {
                              "rel": "actuel",
                              "href": "http://localhost/api/v1/formations/recherche/detaillee?recherche=L1&numeroDePage=1"
                            }
                          ]
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le mot recherché fait strictement moins de 2 caractère, doit retourner 400`() {
            // When & Then
            mvc.perform(
                get("/api/v1/formations/recherche/detaillee?recherche=L"),
            ).andDo(print()).andExpect(status().isBadRequest).andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "type": "about:blank",
                          "title": "REQUETE_TROP_COURTE",
                          "status": 400,
                          "detail": "La taille de la requête est trop courte. Elle doit faire au moins 2 caractères",
                          "instance": "/api/v1/formations/recherche/detaillee"
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le mot recherché fait strictement plus de 50 caractère, doit retourner 400`() {
            // When & Then
            val rechercheDe51Caracteres = "Lorem ipsum dolor sit amet, consectetur sodales sed"
            mvc.perform(
                get("/api/v1/formations/recherche/detaillee?recherche=$rechercheDe51Caracteres"),
            ).andDo(print()).andExpect(status().isBadRequest).andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "type": "about:blank",
                          "title": "REQUETE_TROP_LONGUE",
                          "status": 400,
                          "detail": "La taille de la requête dépasse la taille maximale de 50 caractères",
                          "instance": "/api/v1/formations/recherche/detaillee"
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteSansId
        @Test
        fun `si connecté sans profil, doit retourner 200 avec les formations sans explications`() {
            // Given
            val rechercheL1 =
                mapOf(
                    FormationCourte(id = "fl1", nom = "L1 - Psychologie") to 1,
                    FormationCourte(id = "fl7", nom = "L1 - Philosophie") to 1,
                )
            given(
                rechercherFormation.rechercheLesFormationsAvecLeurScoreCorrespondantes(
                    recherche = "L1",
                    tailleMinimumRecherche = 2,
                ),
            ).willReturn(rechercheL1)
            val fichesFormations =
                listOf(
                    ficheFormationSansProfil.copy(id = "fl1"),
                    ficheFormationSansProfil.copy(
                        id = "fl7",
                        nom = "2eme formation",
                        descriptifGeneral = null,
                        descriptifAttendus = null,
                        descriptifDiplome = null,
                        descriptifConseils = null,
                        formationsAssociees = listOf("fl3"),
                        liens = emptyList(),
                        criteresAnalyseCandidature = emptyList(),
                        statistiquesDesAdmis = null,
                        metiers = emptyList(),
                    ),
                )
            given(recupererFormationsService.recupererFichesFormation(listOf("fl1", "fl7"), false)).willReturn(fichesFormations)
            given(ordonnerRechercheFormationsBuilder.trierParScore(rechercheL1)).willReturn(
                listOf(
                    FormationCourte(id = "fl1", nom = "L1 - Psychologie"),
                    FormationCourte(id = "fl7", nom = "L1 - Philosophie"),
                ),
            )

            val hateoas =
                Hateoas(
                    pageActuelle = 1,
                    pageSuivante = null,
                    premierePage = 1,
                    dernierePage = 1,
                    listeCoupee = listOf("fl1", "fl7"),
                )
            given(hateoasBuilder.creerHateoas(liste = listOf("fl1", "fl7"), numeroDePageActuelle = 1, tailleLot = 30)).willReturn(
                hateoas,
            )

            // When & Then
            mvc.perform(
                get("/api/v1/formations/recherche/detaillee?recherche=L1"),
            ).andDo(print()).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "formations": [
                            {
                              "formation": {
                                "id": "fl1",
                                "nom": "Cycle pluridisciplinaire d'Études Supérieures - Science",
                                "idsFormationsAssociees": [
                                  "fl0012"
                                ],
                                "descriptifFormation": "Les formations CPES recrutent des lycéen.nes de très bon niveau sur sélection et dispensent des enseignements pluri-disciplinaires (scientifiques, artistiques, de sciences sociales, de littérature) permettant une poursuite d'études en master ou en grande école. Il s’agit de formations ouvertes socialement recrutant 40% de boursiers sur critères sociaux. Elles sont organisées conjointement par un établissement d’enseignement secondaire lycée et un établissement de l’enseignement supérieur, une université.",
                                "descriptifDiplome": "Les formations CPES sont des diplômes d’établissement diplômants en trois ans qui conférent le grade de licence.",
                                "descriptifConseils": "Nous vous conseillons de développer une sensibilité artistique et de rester informé des tendances actuelles en matière de design floral pour exceller dans ce domaine.",
                                "descriptifAttendus": "Il est attendu des candidats de démontrer une solide compréhension des techniques de base de la floristerie, y compris la composition florale, la reconnaissance des plantes et des fleurs, ainsi que les soins et l'entretien des végétaux.",
                                "moyenneGeneraleDesAdmis": null,
                                "criteresAnalyseCandidature": [
                                  {
                                    "nom": "Compétences académiques",
                                    "pourcentage": 10
                                  },
                                  {
                                    "nom": "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                                    "pourcentage": 0
                                  },
                                  {
                                    "nom": "Résultats académiques",
                                    "pourcentage": 18
                                  },
                                  {
                                    "nom": "Savoir-être",
                                    "pourcentage": 42
                                  },
                                  {
                                    "nom": "Motivation, connaissance",
                                    "pourcentage": 30
                                  }
                                ],
                                "repartitionAdmisAnneePrecedente": {
                                  "total": 12,
                                  "parBaccalaureat": []
                                },
                                "liens": [
                                  {
                                    "nom": "Voir sur l'ONISEP",
                                    "url": "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/cycle-pluridisciplinaire-d-etudes-superieures"
                                  }
                                ],
                                "voeux": [
                                  {
                                    "id": "ta10",
                                    "nom": "Nom du ta10",
                                    "commune": {
                                      "nom": "Lyon",
                                      "codeInsee": "69123"
                                    }
                                  },
                                  {
                                    "id": "ta3",
                                    "nom": "Nom du ta3",
                                    "commune": {
                                      "nom": "Paris",
                                      "codeInsee": "75105"
                                    }
                                  },
                                  {
                                    "id": "ta11",
                                    "nom": "Nom du ta11",
                                    "commune": {
                                      "nom": "Lyon",
                                      "codeInsee": "69123"
                                    }
                                  },
                                  {
                                    "id": "ta32",
                                    "nom": "Nom du ta32",
                                    "commune": {
                                      "nom": "Paris",
                                      "codeInsee": "75115"
                                    }
                                  },
                                  {
                                    "id": "ta17",
                                    "nom": "Nom du ta17",
                                    "commune": {
                                      "nom": "Strasbourg",
                                      "codeInsee": "67482"
                                    }
                                  },
                                  {
                                    "id": "ta7",
                                    "nom": "Nom du ta7",
                                    "commune": {
                                      "nom": "Marseille",
                                      "codeInsee": "13055"
                                    }
                                  }
                                ],
                                "communesFavoritesAvecLeursVoeux": [],
                                "metiers": [
                                  {
                                    "id": "MET001",
                                    "nom": "géomaticien/ne",
                                    "descriptif": "À la croisée de la géographie et de l'informatique, le géomaticien ou la géomaticienne exploite les données pour modéliser le territoire",
                                    "liens": [
                                      {
                                        "nom": "Voir sur l'ONISEP",
                                        "url": "https://www.onisep.fr/ressources/univers-metier/metiers/geomaticien-geomaticienne"
                                      }
                                    ]
                                  },
                                  {
                                    "id": "MET002",
                                    "nom": "documentaliste",
                                    "descriptif": null,
                                    "liens": []
                                  }
                                ],
                                "tauxAffinite": null,
                                "apprentissage": false
                              },
                              "explications": null
                            },
                            {
                              "formation": {
                                "id": "fl7",
                                "nom": "2eme formation",
                                "idsFormationsAssociees": [
                                  "fl3"
                                ],
                                "descriptifFormation": null,
                                "descriptifDiplome": null,
                                "descriptifConseils": null,
                                "descriptifAttendus": null,
                                "moyenneGeneraleDesAdmis": null,
                                "criteresAnalyseCandidature": [],
                                "repartitionAdmisAnneePrecedente": null,
                                "liens": [],
                                "voeux": [
                                  {
                                    "id": "ta10",
                                    "nom": "Nom du ta10",
                                    "commune": {
                                      "nom": "Lyon",
                                      "codeInsee": "69123"
                                    }
                                  },
                                  {
                                    "id": "ta3",
                                    "nom": "Nom du ta3",
                                    "commune": {
                                      "nom": "Paris",
                                      "codeInsee": "75105"
                                    }
                                  },
                                  {
                                    "id": "ta11",
                                    "nom": "Nom du ta11",
                                    "commune": {
                                      "nom": "Lyon",
                                      "codeInsee": "69123"
                                    }
                                  },
                                  {
                                    "id": "ta32",
                                    "nom": "Nom du ta32",
                                    "commune": {
                                      "nom": "Paris",
                                      "codeInsee": "75115"
                                    }
                                  },
                                  {
                                    "id": "ta17",
                                    "nom": "Nom du ta17",
                                    "commune": {
                                      "nom": "Strasbourg",
                                      "codeInsee": "67482"
                                    }
                                  },
                                  {
                                    "id": "ta7",
                                    "nom": "Nom du ta7",
                                    "commune": {
                                      "nom": "Marseille",
                                      "codeInsee": "13055"
                                    }
                                  }
                                ],
                                "communesFavoritesAvecLeursVoeux": [],
                                "metiers": [],
                                "tauxAffinite": null,
                                "apprentissage": false
                              },
                              "explications": null
                            }
                          ],
                          "liens": [
                            {
                              "rel": "premier",
                              "href": "http://localhost/api/v1/formations/recherche/detaillee?recherche=L1&numeroDePage=1"
                            },
                            {
                              "rel": "dernier",
                              "href": "http://localhost/api/v1/formations/recherche/detaillee?recherche=L1&numeroDePage=1"
                            },
                            {
                              "rel": "actuel",
                              "href": "http://localhost/api/v1/formations/recherche/detaillee?recherche=L1&numeroDePage=1"
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
            mvc.perform(
                get("/api/v1/formations/recherche/detaillee?recherche=test"),
            ).andDo(print()).andExpect(status().isUnauthorized)
        }
    }

    @Nested
    inner class `Quand on appelle la route de récupération de formations` {
        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le service réussi pour un appel avec un profil, doit retourner 200 avec le détail des formations`() {
            // Given
            val toutesLesSuggestions = mock(SuggestionsPourUnProfil::class.java)
            given(suggestionsFormationsService.recupererLesSuggestionsPourUnProfil(unProfilEleve)).willReturn(toutesLesSuggestions)
            val fichesFormations =
                listOf(
                    ficheFormation.copy(id = "fl1"),
                    ficheFormation.copy(
                        id = "fl2",
                        nom = "2eme formation",
                        descriptifGeneral = null,
                        descriptifAttendus = null,
                        descriptifDiplome = null,
                        descriptifConseils = null,
                        formationsAssociees = listOf("fl3"),
                        liens = emptyList(),
                        criteresAnalyseCandidature = emptyList(),
                        statistiquesDesAdmis = null,
                        tauxAffinite = 17,
                        metiersTriesParAffinites = emptyList(),
                        voeux = emptyList(),
                        apprentissage = false,
                        explications = null,
                    ),
                )
            given(
                recupererFormationsService.recupererFichesFormationPourProfil(
                    unProfilEleve,
                    toutesLesSuggestions,
                    listOf("fl1", "fl2"),
                    true,
                ),
            ).willReturn(fichesFormations)
            val hateoas =
                Hateoas(
                    pageActuelle = 1,
                    pageSuivante = null,
                    premierePage = 1,
                    dernierePage = 1,
                    listeCoupee = listOf("fl1", "fl2"),
                )
            given(hateoasBuilder.creerHateoas(liste = listOf("fl1", "fl2"), numeroDePageActuelle = 1, tailleLot = 30)).willReturn(
                hateoas,
            )

            // When & Then
            mvc.perform(
                get("/api/v1/formations?ids=fl1&ids=fl2"),
            ).andDo(print()).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "formations": [
                            {
                              "formation": {
                                "id": "fl1",
                                "nom": "Cycle pluridisciplinaire d'Études Supérieures - Science",
                                "idsFormationsAssociees": [
                                  "fl0012"
                                ],
                                "descriptifFormation": "Les formations CPES recrutent des lycéen.nes de très bon niveau sur sélection et dispensent des enseignements pluri-disciplinaires (scientifiques, artistiques, de sciences sociales, de littérature) permettant une poursuite d'études en master ou en grande école. Il s’agit de formations ouvertes socialement recrutant 40% de boursiers sur critères sociaux. Elles sont organisées conjointement par un établissement d’enseignement secondaire lycée et un établissement de l’enseignement supérieur, une université.",
                                "descriptifDiplome": "Les formations CPES sont des diplômes d’établissement diplômants en trois ans qui conférent le grade de licence.",
                                "descriptifConseils": "Nous vous conseillons de développer une sensibilité artistique et de rester informé des tendances actuelles en matière de design floral pour exceller dans ce domaine.",
                                "descriptifAttendus": "Il est attendu des candidats de démontrer une solide compréhension des techniques de base de la floristerie, y compris la composition florale, la reconnaissance des plantes et des fleurs, ainsi que les soins et l'entretien des végétaux.",
                                "moyenneGeneraleDesAdmis": {
                                  "baccalaureat": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  },
                                  "centiles": [
                                    {
                                      "centile": 5,
                                      "note": 13.0
                                    },
                                    {
                                      "centile": 25,
                                      "note": 14.5
                                    },
                                    {
                                      "centile": 75,
                                      "note": 17.0
                                    },
                                    {
                                      "centile": 95,
                                      "note": 18.0
                                    }
                                  ]
                                },
                                "criteresAnalyseCandidature": [
                                  {
                                    "nom": "Compétences académiques",
                                    "pourcentage": 10
                                  },
                                  {
                                    "nom": "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                                    "pourcentage": 0
                                  },
                                  {
                                    "nom": "Résultats académiques",
                                    "pourcentage": 18
                                  },
                                  {
                                    "nom": "Savoir-être",
                                    "pourcentage": 42
                                  },
                                  {
                                    "nom": "Motivation, connaissance",
                                    "pourcentage": 30
                                  }
                                ],
                                "repartitionAdmisAnneePrecedente": {
                                  "total": 6915,
                                  "parBaccalaureat": [
                                    {
                                      "baccalaureat": {
                                        "id": "Générale",
                                        "nom": "Série Générale"
                                      },
                                      "nombreAdmis": 6677
                                    },
                                    {
                                      "baccalaureat": {
                                        "id": "STMG",
                                        "nom": "Série STMG"
                                      },
                                      "nombreAdmis": 15
                                    },
                                    {
                                      "baccalaureat": {
                                        "id": "STI2D",
                                        "nom": "Série STI2D"
                                      },
                                      "nombreAdmis": 223
                                    }
                                  ]
                                },
                                "liens": [
                                  {
                                    "nom": "Voir sur l'ONISEP",
                                    "url": "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/cycle-pluridisciplinaire-d-etudes-superieures"
                                  }
                                ],
                                "voeux": [
                                  {
                                    "id": "ta10",
                                    "nom": "Nom du ta10",
                                    "commune": {
                                      "nom": "Lyon",
                                      "codeInsee": "69123"
                                    }
                                  },
                                  {
                                    "id": "ta3",
                                    "nom": "Nom du ta3",
                                    "commune": {
                                      "nom": "Paris",
                                      "codeInsee": "75105"
                                    }
                                  },
                                  {
                                    "id": "ta11",
                                    "nom": "Nom du ta11",
                                    "commune": {
                                      "nom": "Lyon",
                                      "codeInsee": "69123"
                                    }
                                  },
                                  {
                                    "id": "ta32",
                                    "nom": "Nom du ta32",
                                    "commune": {
                                      "nom": "Paris",
                                      "codeInsee": "75115"
                                    }
                                  },
                                  {
                                    "id": "ta17",
                                    "nom": "Nom du ta17",
                                    "commune": {
                                      "nom": "Strasbourg",
                                      "codeInsee": "67482"
                                    }
                                  },
                                  {
                                    "id": "ta7",
                                    "nom": "Nom du ta7",
                                    "commune": {
                                      "nom": "Marseille",
                                      "codeInsee": "13055"
                                    }
                                  }
                                ],
                                "communesFavoritesAvecLeursVoeux": [
                                  {
                                    "commune": {
                                      "codeInsee": "75115",
                                      "nom": "Paris",
                                      "latitude": 48.851227,
                                      "longitude": 2.2885659
                                    },
                                    "voeuxAvecDistance": [
                                      {
                                        "voeu": {
                                          "id": "ta3",
                                          "nom": "Nom du ta3",
                                          "commune": {
                                            "nom": "Paris",
                                            "codeInsee": "75105"
                                          }
                                        },
                                        "distanceKm": 3
                                      },
                                      {
                                        "voeu": {
                                          "id": "ta32",
                                          "nom": "Nom du ta32",
                                          "commune": {
                                            "nom": "Paris",
                                            "codeInsee": "75115"
                                          }
                                        },
                                        "distanceKm": 1
                                      }
                                    ]
                                  }
                                ],
                                "metiers": [
                                  {
                                    "id": "MET001",
                                    "nom": "géomaticien/ne",
                                    "descriptif": "À la croisée de la géographie et de l'informatique, le géomaticien ou la géomaticienne exploite les données pour modéliser le territoire",
                                    "liens": [
                                      {
                                        "nom": "Voir sur l'ONISEP",
                                        "url": "https://www.onisep.fr/ressources/univers-metier/metiers/geomaticien-geomaticienne"
                                      }
                                    ]
                                  },
                                  {
                                    "id": "MET002",
                                    "nom": "documentaliste",
                                    "descriptif": null,
                                    "liens": []
                                  }
                                ],
                                "tauxAffinite": 90,
                                "apprentissage": true
                              },
                              "explications": {
                                "geographique": [
                                  {
                                    "nomVille": "Nantes",
                                    "distanceKm": 1
                                  },
                                  {
                                    "nomVille": "Paris",
                                    "distanceKm": 3
                                  }
                                ],
                                "formationsSimilaires": [
                                  {
                                    "id": "fl1",
                                    "nom": "CPGE MPSI"
                                  },
                                  {
                                    "id": "fl7",
                                    "nom": "BUT Informatique"
                                  }
                                ],
                                "dureeEtudesPrevue": "longue",
                                "alternance": "tres_interesse",
                                "interetsEtDomainesChoisis": {
                                  "interets": [
                                    {
                                      "id": "aider_autres",
                                      "nom": "Aider les autres"
                                    }
                                  ],
                                  "domaines": [
                                    {
                                      "id": "T_ITM_1356",
                                      "nom": "soin aux animaux",
                                      "emoji": "\uD83D\uDC2E"
                                    }
                                  ]
                                },
                                "specialitesChoisies": [
                                  {
                                    "nomSpecialite": "specialiteA",
                                    "pourcentage": 12
                                  },
                                  {
                                    "nomSpecialite": "specialiteB",
                                    "pourcentage": 1
                                  },
                                  {
                                    "nomSpecialite": "specialiteC",
                                    "pourcentage": 89
                                  }
                                ],
                                "typeBaccalaureat": {
                                  "baccalaureat": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  },
                                  "pourcentage": 18
                                },
                                "autoEvaluationMoyenne": {
                                  "moyenne": 15.0,
                                  "basIntervalleNotes": 14.0,
                                  "hautIntervalleNotes": 16.0,
                                  "baccalaureatUtilise": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  }
                                },
                                "detailsCalculScore": {
                                  "details": []
                                }
                              }
                            },
                            {
                              "formation": {
                                "id": "fl2",
                                "nom": "2eme formation",
                                "idsFormationsAssociees": [
                                  "fl3"
                                ],
                                "descriptifFormation": null,
                                "descriptifDiplome": null,
                                "descriptifConseils": null,
                                "descriptifAttendus": null,
                                "moyenneGeneraleDesAdmis": null,
                                "criteresAnalyseCandidature": [],
                                "repartitionAdmisAnneePrecedente": null,
                                "liens": [],
                                "voeux": [],
                                "communesFavoritesAvecLeursVoeux": [
                                  {
                                    "commune": {
                                      "codeInsee": "75115",
                                      "nom": "Paris",
                                      "latitude": 48.851227,
                                      "longitude": 2.2885659
                                    },
                                    "voeuxAvecDistance": [
                                      {
                                        "voeu": {
                                          "id": "ta3",
                                          "nom": "Nom du ta3",
                                          "commune": {
                                            "nom": "Paris",
                                            "codeInsee": "75105"
                                          }
                                        },
                                        "distanceKm": 3
                                      },
                                      {
                                        "voeu": {
                                          "id": "ta32",
                                          "nom": "Nom du ta32",
                                          "commune": {
                                            "nom": "Paris",
                                            "codeInsee": "75115"
                                          }
                                        },
                                        "distanceKm": 1
                                      }
                                    ]
                                  }
                                ],
                                "metiers": [],
                                "tauxAffinite": 17,
                                "apprentissage": false
                              },
                              "explications": null
                            }
                          ],
                          "liens": [
                            {
                              "rel": "premier",
                              "href": "http://localhost/api/v1/formations?ids=fl1&ids=fl2&numeroDePage=1"
                            },
                            {
                              "rel": "dernier",
                              "href": "http://localhost/api/v1/formations?ids=fl1&ids=fl2&numeroDePage=1"
                            },
                            {
                              "rel": "actuel",
                              "href": "http://localhost/api/v1/formations?ids=fl1&ids=fl2&numeroDePage=1"
                            }
                          ]
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteAvecUnEnseignant(idEnseignant = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si enseignant, doit retourner 200 avec le detail des formations`() {
            // Given
            val toutesLesSuggestions = mock(SuggestionsPourUnProfil::class.java)
            given(suggestionsFormationsService.recupererLesSuggestionsPourUnProfil(unProfilEleve)).willReturn(toutesLesSuggestions)
            val fichesFormations =
                listOf(
                    ficheFormation.copy(id = "fl1"),
                    ficheFormation.copy(
                        id = "fl2",
                        nom = "2eme formation",
                        descriptifGeneral = null,
                        descriptifAttendus = null,
                        descriptifDiplome = null,
                        descriptifConseils = null,
                        formationsAssociees = listOf("fl3"),
                        liens = emptyList(),
                        criteresAnalyseCandidature = emptyList(),
                        statistiquesDesAdmis = null,
                        tauxAffinite = 17,
                        metiersTriesParAffinites = emptyList(),
                        voeux = emptyList(),
                        apprentissage = false,
                        explications = null,
                    ),
                )
            given(
                recupererFormationsService.recupererFichesFormationPourProfil(
                    unProfilEleve,
                    toutesLesSuggestions,
                    listOf("fl1", "fl2"),
                    true,
                ),
            ).willReturn(fichesFormations)
            val hateoas =
                Hateoas(
                    pageActuelle = 1,
                    pageSuivante = null,
                    premierePage = 1,
                    dernierePage = 1,
                    listeCoupee = listOf("fl1", "fl2"),
                )
            given(hateoasBuilder.creerHateoas(liste = listOf("fl1", "fl2"), numeroDePageActuelle = 1, tailleLot = 30)).willReturn(
                hateoas,
            )

            // When & Then
            mvc.perform(
                get("/api/v1/formations?ids=fl1&ids=fl2"),
            ).andDo(print()).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "formations": [
                            {
                              "formation": {
                                "id": "fl1",
                                "nom": "Cycle pluridisciplinaire d'Études Supérieures - Science",
                                "idsFormationsAssociees": [
                                  "fl0012"
                                ],
                                "descriptifFormation": "Les formations CPES recrutent des lycéen.nes de très bon niveau sur sélection et dispensent des enseignements pluri-disciplinaires (scientifiques, artistiques, de sciences sociales, de littérature) permettant une poursuite d'études en master ou en grande école. Il s’agit de formations ouvertes socialement recrutant 40% de boursiers sur critères sociaux. Elles sont organisées conjointement par un établissement d’enseignement secondaire lycée et un établissement de l’enseignement supérieur, une université.",
                                "descriptifDiplome": "Les formations CPES sont des diplômes d’établissement diplômants en trois ans qui conférent le grade de licence.",
                                "descriptifConseils": "Nous vous conseillons de développer une sensibilité artistique et de rester informé des tendances actuelles en matière de design floral pour exceller dans ce domaine.",
                                "descriptifAttendus": "Il est attendu des candidats de démontrer une solide compréhension des techniques de base de la floristerie, y compris la composition florale, la reconnaissance des plantes et des fleurs, ainsi que les soins et l'entretien des végétaux.",
                                "moyenneGeneraleDesAdmis": {
                                  "baccalaureat": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  },
                                  "centiles": [
                                    {
                                      "centile": 5,
                                      "note": 13.0
                                    },
                                    {
                                      "centile": 25,
                                      "note": 14.5
                                    },
                                    {
                                      "centile": 75,
                                      "note": 17.0
                                    },
                                    {
                                      "centile": 95,
                                      "note": 18.0
                                    }
                                  ]
                                },
                                "criteresAnalyseCandidature": [
                                  {
                                    "nom": "Compétences académiques",
                                    "pourcentage": 10
                                  },
                                  {
                                    "nom": "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                                    "pourcentage": 0
                                  },
                                  {
                                    "nom": "Résultats académiques",
                                    "pourcentage": 18
                                  },
                                  {
                                    "nom": "Savoir-être",
                                    "pourcentage": 42
                                  },
                                  {
                                    "nom": "Motivation, connaissance",
                                    "pourcentage": 30
                                  }
                                ],
                                "repartitionAdmisAnneePrecedente": {
                                  "total": 6915,
                                  "parBaccalaureat": [
                                    {
                                      "baccalaureat": {
                                        "id": "Générale",
                                        "nom": "Série Générale"
                                      },
                                      "nombreAdmis": 6677
                                    },
                                    {
                                      "baccalaureat": {
                                        "id": "STMG",
                                        "nom": "Série STMG"
                                      },
                                      "nombreAdmis": 15
                                    },
                                    {
                                      "baccalaureat": {
                                        "id": "STI2D",
                                        "nom": "Série STI2D"
                                      },
                                      "nombreAdmis": 223
                                    }
                                  ]
                                },
                                "liens": [
                                  {
                                    "nom": "Voir sur l'ONISEP",
                                    "url": "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/cycle-pluridisciplinaire-d-etudes-superieures"
                                  }
                                ],
                                "voeux": [
                                  {
                                    "id": "ta10",
                                    "nom": "Nom du ta10",
                                    "commune": {
                                      "nom": "Lyon",
                                      "codeInsee": "69123"
                                    }
                                  },
                                  {
                                    "id": "ta3",
                                    "nom": "Nom du ta3",
                                    "commune": {
                                      "nom": "Paris",
                                      "codeInsee": "75105"
                                    }
                                  },
                                  {
                                    "id": "ta11",
                                    "nom": "Nom du ta11",
                                    "commune": {
                                      "nom": "Lyon",
                                      "codeInsee": "69123"
                                    }
                                  },
                                  {
                                    "id": "ta32",
                                    "nom": "Nom du ta32",
                                    "commune": {
                                      "nom": "Paris",
                                      "codeInsee": "75115"
                                    }
                                  },
                                  {
                                    "id": "ta17",
                                    "nom": "Nom du ta17",
                                    "commune": {
                                      "nom": "Strasbourg",
                                      "codeInsee": "67482"
                                    }
                                  },
                                  {
                                    "id": "ta7",
                                    "nom": "Nom du ta7",
                                    "commune": {
                                      "nom": "Marseille",
                                      "codeInsee": "13055"
                                    }
                                  }
                                ],
                                "communesFavoritesAvecLeursVoeux": [
                                  {
                                    "commune": {
                                      "codeInsee": "75115",
                                      "nom": "Paris",
                                      "latitude": 48.851227,
                                      "longitude": 2.2885659
                                    },
                                    "voeuxAvecDistance": [
                                      {
                                        "voeu": {
                                          "id": "ta3",
                                          "nom": "Nom du ta3",
                                          "commune": {
                                            "nom": "Paris",
                                            "codeInsee": "75105"
                                          }
                                        },
                                        "distanceKm": 3
                                      },
                                      {
                                        "voeu": {
                                          "id": "ta32",
                                          "nom": "Nom du ta32",
                                          "commune": {
                                            "nom": "Paris",
                                            "codeInsee": "75115"
                                          }
                                        },
                                        "distanceKm": 1
                                      }
                                    ]
                                  }
                                ],
                                "metiers": [
                                  {
                                    "id": "MET001",
                                    "nom": "géomaticien/ne",
                                    "descriptif": "À la croisée de la géographie et de l'informatique, le géomaticien ou la géomaticienne exploite les données pour modéliser le territoire",
                                    "liens": [
                                      {
                                        "nom": "Voir sur l'ONISEP",
                                        "url": "https://www.onisep.fr/ressources/univers-metier/metiers/geomaticien-geomaticienne"
                                      }
                                    ]
                                  },
                                  {
                                    "id": "MET002",
                                    "nom": "documentaliste",
                                    "descriptif": null,
                                    "liens": []
                                  }
                                ],
                                "tauxAffinite": 90,
                                "apprentissage": true
                              },
                              "explications": {
                                "geographique": [
                                  {
                                    "nomVille": "Nantes",
                                    "distanceKm": 1
                                  },
                                  {
                                    "nomVille": "Paris",
                                    "distanceKm": 3
                                  }
                                ],
                                "formationsSimilaires": [
                                  {
                                    "id": "fl1",
                                    "nom": "CPGE MPSI"
                                  },
                                  {
                                    "id": "fl7",
                                    "nom": "BUT Informatique"
                                  }
                                ],
                                "dureeEtudesPrevue": "longue",
                                "alternance": "tres_interesse",
                                "interetsEtDomainesChoisis": {
                                  "interets": [
                                    {
                                      "id": "aider_autres",
                                      "nom": "Aider les autres"
                                    }
                                  ],
                                  "domaines": [
                                    {
                                      "id": "T_ITM_1356",
                                      "nom": "soin aux animaux",
                                      "emoji": "\uD83D\uDC2E"
                                    }
                                  ]
                                },
                                "specialitesChoisies": [
                                  {
                                    "nomSpecialite": "specialiteA",
                                    "pourcentage": 12
                                  },
                                  {
                                    "nomSpecialite": "specialiteB",
                                    "pourcentage": 1
                                  },
                                  {
                                    "nomSpecialite": "specialiteC",
                                    "pourcentage": 89
                                  }
                                ],
                                "typeBaccalaureat": {
                                  "baccalaureat": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  },
                                  "pourcentage": 18
                                },
                                "autoEvaluationMoyenne": {
                                  "moyenne": 15.0,
                                  "basIntervalleNotes": 14.0,
                                  "hautIntervalleNotes": 16.0,
                                  "baccalaureatUtilise": {
                                    "id": "Générale",
                                    "nom": "Série Générale"
                                  }
                                },
                                "detailsCalculScore": {
                                  "details": []
                                }
                              }
                            },
                            {
                              "formation": {
                                "id": "fl2",
                                "nom": "2eme formation",
                                "idsFormationsAssociees": [
                                  "fl3"
                                ],
                                "descriptifFormation": null,
                                "descriptifDiplome": null,
                                "descriptifConseils": null,
                                "descriptifAttendus": null,
                                "moyenneGeneraleDesAdmis": null,
                                "criteresAnalyseCandidature": [],
                                "repartitionAdmisAnneePrecedente": null,
                                "liens": [],
                                "voeux": [],
                                "communesFavoritesAvecLeursVoeux": [
                                  {
                                    "commune": {
                                      "codeInsee": "75115",
                                      "nom": "Paris",
                                      "latitude": 48.851227,
                                      "longitude": 2.2885659
                                    },
                                    "voeuxAvecDistance": [
                                      {
                                        "voeu": {
                                          "id": "ta3",
                                          "nom": "Nom du ta3",
                                          "commune": {
                                            "nom": "Paris",
                                            "codeInsee": "75105"
                                          }
                                        },
                                        "distanceKm": 3
                                      },
                                      {
                                        "voeu": {
                                          "id": "ta32",
                                          "nom": "Nom du ta32",
                                          "commune": {
                                            "nom": "Paris",
                                            "codeInsee": "75115"
                                          }
                                        },
                                        "distanceKm": 1
                                      }
                                    ]
                                  }
                                ],
                                "metiers": [],
                                "tauxAffinite": 17,
                                "apprentissage": false
                              },
                              "explications": null
                            }
                          ],
                          "liens": [
                            {
                              "rel": "premier",
                              "href": "http://localhost/api/v1/formations?ids=fl1&ids=fl2&numeroDePage=1"
                            },
                            {
                              "rel": "dernier",
                              "href": "http://localhost/api/v1/formations?ids=fl1&ids=fl2&numeroDePage=1"
                            },
                            {
                              "rel": "actuel",
                              "href": "http://localhost/api/v1/formations?ids=fl1&ids=fl2&numeroDePage=1"
                            }
                          ]
                        }
                        """.trimIndent(),
                    ),
                )
        }

        @ConnecteSansId
        @Test
        fun `si connecté sans profil, doit retourner 200 avec le detail des formations`() {
            // Given
            val fichesFormations =
                listOf(
                    ficheFormationSansProfil.copy(id = "fl1"),
                    ficheFormationSansProfil.copy(
                        id = "fl2",
                        nom = "2eme formation",
                        descriptifGeneral = null,
                        descriptifAttendus = null,
                        descriptifDiplome = null,
                        descriptifConseils = null,
                        formationsAssociees = listOf("fl3"),
                        liens = emptyList(),
                        criteresAnalyseCandidature = emptyList(),
                        statistiquesDesAdmis = null,
                        metiers = emptyList(),
                        voeux = emptyList(),
                    ),
                )
            given(recupererFormationsService.recupererFichesFormation(listOf("fl1", "fl2"), true)).willReturn(fichesFormations)
            val hateoas =
                Hateoas(
                    pageActuelle = 1,
                    pageSuivante = null,
                    premierePage = 1,
                    dernierePage = 1,
                    listeCoupee = listOf("fl1", "fl2"),
                )
            given(hateoasBuilder.creerHateoas(liste = listOf("fl1", "fl2"), numeroDePageActuelle = 1, tailleLot = 30)).willReturn(
                hateoas,
            )

            // When & Then
            mvc.perform(
                get("/api/v1/formations?ids=fl1&ids=fl2"),
            ).andDo(print()).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(
                    content().json(
                        """
                        {
                          "formations": [
                            {
                              "formation": {
                                "id": "fl1",
                                "nom": "Cycle pluridisciplinaire d'Études Supérieures - Science",
                                "idsFormationsAssociees": [
                                  "fl0012"
                                ],
                                "descriptifFormation": "Les formations CPES recrutent des lycéen.nes de très bon niveau sur sélection et dispensent des enseignements pluri-disciplinaires (scientifiques, artistiques, de sciences sociales, de littérature) permettant une poursuite d'études en master ou en grande école. Il s’agit de formations ouvertes socialement recrutant 40% de boursiers sur critères sociaux. Elles sont organisées conjointement par un établissement d’enseignement secondaire lycée et un établissement de l’enseignement supérieur, une université.",
                                "descriptifDiplome": "Les formations CPES sont des diplômes d’établissement diplômants en trois ans qui conférent le grade de licence.",
                                "descriptifConseils": "Nous vous conseillons de développer une sensibilité artistique et de rester informé des tendances actuelles en matière de design floral pour exceller dans ce domaine.",
                                "descriptifAttendus": "Il est attendu des candidats de démontrer une solide compréhension des techniques de base de la floristerie, y compris la composition florale, la reconnaissance des plantes et des fleurs, ainsi que les soins et l'entretien des végétaux.",
                                "moyenneGeneraleDesAdmis": null,
                                "criteresAnalyseCandidature": [
                                  {
                                    "nom": "Compétences académiques",
                                    "pourcentage": 10
                                  },
                                  {
                                    "nom": "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                                    "pourcentage": 0
                                  },
                                  {
                                    "nom": "Résultats académiques",
                                    "pourcentage": 18
                                  },
                                  {
                                    "nom": "Savoir-être",
                                    "pourcentage": 42
                                  },
                                  {
                                    "nom": "Motivation, connaissance",
                                    "pourcentage": 30
                                  }
                                ],
                                "repartitionAdmisAnneePrecedente": {
                                  "total": 12,
                                  "parBaccalaureat": []
                                },
                                "liens": [
                                  {
                                    "nom": "Voir sur l'ONISEP",
                                    "url": "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/cycle-pluridisciplinaire-d-etudes-superieures"
                                  }
                                ],
                                "voeux": [
                                  {
                                    "id": "ta10",
                                    "nom": "Nom du ta10",
                                    "commune": {
                                      "nom": "Lyon",
                                      "codeInsee": "69123"
                                    }
                                  },
                                  {
                                    "id": "ta3",
                                    "nom": "Nom du ta3",
                                    "commune": {
                                      "nom": "Paris",
                                      "codeInsee": "75105"
                                    }
                                  },
                                  {
                                    "id": "ta11",
                                    "nom": "Nom du ta11",
                                    "commune": {
                                      "nom": "Lyon",
                                      "codeInsee": "69123"
                                    }
                                  },
                                  {
                                    "id": "ta32",
                                    "nom": "Nom du ta32",
                                    "commune": {
                                      "nom": "Paris",
                                      "codeInsee": "75115"
                                    }
                                  },
                                  {
                                    "id": "ta17",
                                    "nom": "Nom du ta17",
                                    "commune": {
                                      "nom": "Strasbourg",
                                      "codeInsee": "67482"
                                    }
                                  },
                                  {
                                    "id": "ta7",
                                    "nom": "Nom du ta7",
                                    "commune": {
                                      "nom": "Marseille",
                                      "codeInsee": "13055"
                                    }
                                  }
                                ],
                                "communesFavoritesAvecLeursVoeux": [],
                                "metiers": [
                                  {
                                    "id": "MET001",
                                    "nom": "géomaticien/ne",
                                    "descriptif": "À la croisée de la géographie et de l'informatique, le géomaticien ou la géomaticienne exploite les données pour modéliser le territoire",
                                    "liens": [
                                      {
                                        "nom": "Voir sur l'ONISEP",
                                        "url": "https://www.onisep.fr/ressources/univers-metier/metiers/geomaticien-geomaticienne"
                                      }
                                    ]
                                  },
                                  {
                                    "id": "MET002",
                                    "nom": "documentaliste",
                                    "descriptif": null,
                                    "liens": []
                                  }
                                ],
                                "tauxAffinite": null,
                                "apprentissage": false
                              },
                              "explications": null
                            },
                            {
                              "formation": {
                                "id": "fl2",
                                "nom": "2eme formation",
                                "idsFormationsAssociees": [
                                  "fl3"
                                ],
                                "descriptifFormation": null,
                                "descriptifDiplome": null,
                                "descriptifConseils": null,
                                "descriptifAttendus": null,
                                "moyenneGeneraleDesAdmis": null,
                                "criteresAnalyseCandidature": [],
                                "repartitionAdmisAnneePrecedente": null,
                                "liens": [],
                                "voeux": [],
                                "communesFavoritesAvecLeursVoeux": [],
                                "metiers": [],
                                "tauxAffinite": null,
                                "apprentissage": false
                              },
                              "explications": null
                            }
                          ],
                          "liens": [
                            {
                              "rel": "premier",
                              "href": "http://localhost/api/v1/formations?ids=fl1&ids=fl2&numeroDePage=1"
                            },
                            {
                              "rel": "dernier",
                              "href": "http://localhost/api/v1/formations?ids=fl1&ids=fl2&numeroDePage=1"
                            },
                            {
                              "rel": "actuel",
                              "href": "http://localhost/api/v1/formations?ids=fl1&ids=fl2&numeroDePage=1"
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
            mvc.perform(get("/api/v1/formations?ids=fl1&ids=fl2")).andExpect(status().isUnauthorized)
        }

        @ConnecteAvecUnEleve(idEleve = "adcf627c-36dd-4df5-897b-159443a6d49c")
        @Test
        fun `si le service échoue avec une erreur interne, alors doit retourner 500`() {
            // Given
            val uneException =
                MonProjetSupInternalErrorException(
                    "ERREUR_API_SUGGESTIONS_CONNEXION",
                    "Erreur lors de la connexion à l'API de suggestions à l'url /api/v1/formations?ids=fl1&ids=fl2",
                    null,
                )
            given(suggestionsFormationsService.recupererLesSuggestionsPourUnProfil(unProfilEleve)).willThrow(uneException)

            // When & Then
            mvc.perform(
                get("/api/v1/formations?ids=fl1&ids=fl2"),
            ).andDo(print()).andExpect(status().isInternalServerError)
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
        }
    }
}
