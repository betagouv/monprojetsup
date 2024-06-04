package fr.gouv.monprojetsup.recherche.usecase

import fr.gouv.monprojetsup.recherche.domain.entity.AffinitesPourProfil
import fr.gouv.monprojetsup.recherche.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.recherche.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.recherche.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.recherche.domain.entity.CriteresAdmission
import fr.gouv.monprojetsup.recherche.domain.entity.ExplicationsSuggestion
import fr.gouv.monprojetsup.recherche.domain.entity.FicheFormation
import fr.gouv.monprojetsup.recherche.domain.entity.FormationAvecSonAffinite
import fr.gouv.monprojetsup.recherche.domain.entity.FormationDetaillee
import fr.gouv.monprojetsup.recherche.domain.entity.MetierDetaille
import fr.gouv.monprojetsup.recherche.domain.entity.MoyenneGenerale
import fr.gouv.monprojetsup.recherche.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.recherche.domain.entity.TripletAffectation
import fr.gouv.monprojetsup.recherche.domain.port.FormationRepository
import fr.gouv.monprojetsup.recherche.domain.port.SuggestionHttpClient
import fr.gouv.monprojetsup.recherche.domain.port.TripletAffectationRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

class RecupererFormationServiceTest {
    @Mock
    lateinit var suggestionHttpClient: SuggestionHttpClient

    @Mock
    lateinit var formationRepository: FormationRepository

    @Mock
    lateinit var tripletAffectationRepository: TripletAffectationRepository

    @InjectMocks
    lateinit var recupererFormationService: RecupererFormationService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    private val formationDetaillee =
        FormationDetaillee(
            id = "fl0001",
            nom = "CAP Fleuriste",
            descriptifGeneral =
                "Le CAP Fleuriste est un diplôme de niveau 3 qui permet d acquérir les " +
                    "compétences nécessaires pour exercer le métier de fleuriste. La formation dure 2 ans et " +
                    "est accessible après la classe de 3ème. Elle comprend des enseignements généraux " +
                    "(français, mathématiques, histoire-géographie, etc.) et des enseignements professionnels " +
                    "(botanique, art floral, techniques de vente, etc.). Le CAP Fleuriste permet d exercer le " +
                    "métier de fleuriste en boutique, en grande surface, en jardinerie ou en atelier de composition florale.",
            descriptifAttendus =
                "Il est attendu des candidats de démontrer une solide compréhension des techniques " +
                    "de base de la floristerie, y compris la composition florale, la reconnaissance des plantes et des " +
                    "fleurs, ainsi que les soins et l'entretien des végétaux.",
            descriptifDiplome =
                "Le Certificat d'Aptitude Professionnelle (CAP) est un diplôme national de niveau 3 du " +
                    "système éducatif français, qui atteste l'acquisition d'une qualification professionnelle dans un " +
                    "métier précis. Il est généralement obtenu après une formation de deux ans suivant la fin du collège " +
                    "et s'adresse principalement aux élèves souhaitant entrer rapidement dans la vie active.",
            descriptifConseils =
                "Nous vous conseillons de développer une sensibilité artistique et de rester informé des " +
                    "tendances actuelles en matière de design floral pour exceller dans ce domaine.",
            pointsAttendus =
                listOf(
                    "Les compétences, méthodes de travail et savoir-faire",
                    "Motivation et cohérence de ton projet",
                ),
            formationsAssociees = listOf("fl0010", "fl0012"),
            liens = listOf("https://www.onisep.fr/ressources/univers-formation/formations/cap-fleuriste"),
            metiers =
                listOf(
                    MetierDetaille(
                        id = "MET_001",
                        nom = "Fleuriste",
                        descriptif =
                            "Le fleuriste est un artisan qui confectionne et vend des bouquets, des compositions " +
                                "florales, des plantes et des accessoires de décoration. Il peut également être amené à " +
                                "conseiller ses clients sur le choix des fleurs et des plantes en fonction de l occasion " +
                                "et de leur budget. Le fleuriste peut travailler en boutique, en grande surface, en " +
                                "jardinerie ou en atelier de composition florale.",
                        liens = listOf("https://www.onisep.fr/ressources/univers-metier/metiers/fleuriste"),
                    ),
                    MetierDetaille(
                        id = "MET_002",
                        nom = "Fleuriste événementiel",
                        descriptif =
                            "Le fleuriste événementiel est un artisan qui confectionne et vend des bouquets, " +
                                "des compositions florales, des plantes et des accessoires de décoration pour des " +
                                "événements particuliers (mariages, baptêmes, anniversaires, réceptions, etc.). " +
                                "Il peut également être amené à conseiller ses clients sur le choix des fleurs et des plantes " +
                                "en fonction de l occasion et de leur budget. Le fleuriste événementiel peut travailler " +
                                "en boutique, en grande surface, en jardinerie ou en atelier de composition florale.",
                        liens = listOf("https://www.onisep.fr/ressources/univers-metier/metiers/fleuriste"),
                    ),
                ),
        )

    private val tripletsAffectations =
        listOf(
            TripletAffectation(
                id = "ta0001",
                commune = "Paris",
            ),
            TripletAffectation(
                id = "ta0002",
                commune = "Marseille",
            ),
            TripletAffectation(
                id = "ta0003",
                commune = "Caen",
            ),
        )

    @Test
    fun `Quand le profil est null, doit retourner une fiche formation sans le profil avec le taux d'affinité et les explications nulles`() {
        // Given
        given(formationRepository.recupererUneFormationAvecSesMetiers("fl0001")).willReturn(formationDetaillee)
        given(tripletAffectationRepository.recupererLesTripletsAffectationDUneFormation(idFormation = "fl0001")).willReturn(
            tripletsAffectations,
        )

        // When
        val resultat = recupererFormationService.recupererFormation(profilEleve = null, idFormation = "fl0001")

        // Then
        assertThat(resultat).usingRecursiveComparison().isEqualTo(
            FicheFormation.FicheFormationSansProfil(
                id = "fl0001",
                nom = "CAP Fleuriste",
                formationsAssociees = listOf("fl0010", "fl0012"),
                descriptifFormation =
                    "Le CAP Fleuriste est un diplôme de niveau 3 qui permet d acquérir les compétences nécessaires" +
                        " pour exercer le métier de fleuriste. La formation dure 2 ans et est accessible après la classe de 3ème. " +
                        "Elle comprend des enseignements généraux (français, mathématiques, histoire-géographie, etc.) et des " +
                        "enseignements professionnels (botanique, art floral, techniques de vente, etc.). Le CAP Fleuriste " +
                        "permet d exercer le métier de fleuriste en boutique, en grande surface, en jardinerie ou en " +
                        "atelier de composition florale.",
                descriptifDiplome =
                    "Le Certificat d'Aptitude Professionnelle (CAP) est un diplôme national de niveau 3 du " +
                        "système éducatif français, qui atteste l'acquisition d'une qualification professionnelle dans un " +
                        "métier précis. Il est généralement obtenu après une formation de deux ans suivant la fin du collège " +
                        "et s'adresse principalement aux élèves souhaitant entrer rapidement dans la vie active.",
                descriptifAttendus =
                    "Il est attendu des candidats de démontrer une solide compréhension des techniques " +
                        "de base de la floristerie, y compris la composition florale, la reconnaissance des plantes " +
                        "et des fleurs, ainsi que les soins et l'entretien des végétaux.",
                descriptifConseils =
                    "Nous vous conseillons de développer une sensibilité artistique et de rester informé des tendances " +
                        "actuelles en matière de design floral pour exceller dans ce domaine.",
                criteresAdmission =
                    CriteresAdmission(
                        principauxPoints =
                            listOf(
                                "Les compétences, méthodes de travail et savoir-faire",
                                "Motivation et cohérence de ton projet",
                            ),
                        moyenneGenerale =
                            MoyenneGenerale(
                                centille5eme = 0.0f,
                                centille25eme = 12.0f,
                                centille75eme = 15.0f,
                                centille95eme = 19.0f,
                            ),
                    ),
                liens = listOf("https://www.onisep.fr/ressources/univers-formation/formations/cap-fleuriste"),
                metiers =
                    listOf(
                        MetierDetaille(
                            id = "MET_001",
                            nom = "Fleuriste",
                            descriptif =
                                "Le fleuriste est un artisan qui confectionne et vend des bouquets, des compositions " +
                                    "florales, des plantes et des accessoires de décoration. Il peut également être amené à " +
                                    "conseiller ses clients sur le choix des fleurs et des plantes en fonction de l occasion " +
                                    "et de leur budget. Le fleuriste peut travailler en boutique, en grande surface, en jardinerie " +
                                    "ou en atelier de composition florale.",
                            liens = listOf("https://www.onisep.fr/ressources/univers-metier/metiers/fleuriste"),
                        ),
                        MetierDetaille(
                            id = "MET_002",
                            nom = "Fleuriste événementiel",
                            descriptif =
                                "Le fleuriste événementiel est un artisan qui confectionne et vend des bouquets, des " +
                                    "compositions florales, des plantes et des accessoires de décoration pour des événements " +
                                    "particuliers (mariages, baptêmes, anniversaires, réceptions, etc.). Il peut également être " +
                                    "amené à conseiller ses clients sur le choix des fleurs et des plantes en fonction de " +
                                    "l occasion et de leur budget. Le fleuriste événementiel peut travailler en boutique, " +
                                    "en grande surface, en jardinerie ou en atelier de composition florale.",
                            liens = listOf("https://www.onisep.fr/ressources/univers-metier/metiers/fleuriste"),
                        ),
                    ),
                communes = listOf("Paris", "Marseille", "Caen"),
            ),
        )
        assertThat(resultat.tauxAffinite).isNull()
        assertThat(resultat.explications).isNull()
    }

    @Test
    fun `Quand le profil est null, ne doit pas appeler le suggestionHttpClient`() {
        // Given
        given(formationRepository.recupererUneFormationAvecSesMetiers("fl0001")).willReturn(formationDetaillee)
        given(tripletAffectationRepository.recupererLesTripletsAffectationDUneFormation("fl0001")).willReturn(
            tripletsAffectations,
        )

        // When
        recupererFormationService.recupererFormation(profilEleve = null, idFormation = "fl0001")

        // Then
        then(suggestionHttpClient).shouldHaveNoInteractions()
    }

    @Test
    fun `Quand le profil est non null, doit retourner une fiche formation avec un profil`() {
        // Given
        given(formationRepository.recupererUneFormationAvecSesMetiers("fl0001")).willReturn(formationDetaillee)
        given(tripletAffectationRepository.recupererLesTripletsAffectationDUneFormation("fl0001")).willReturn(
            tripletsAffectations,
        )
        val profil =
            ProfilEleve(
                id = "adcf627c-36dd-4df5-897b-159443a6d49c",
                classe = ChoixNiveau.TERMINALE,
                bac = "Générale",
                dureeEtudesPrevue = ChoixDureeEtudesPrevue.OPTIONS_OUVERTES,
                alternance = ChoixAlternance.PAS_INTERESSE,
                villesPreferees = listOf("Caen"),
                specialites = listOf("1001", "1049"),
                centresInterets = listOf("T_ROME_2092381917", "T_IDEO2_4812"),
                moyenneGenerale = 14f,
                metiersChoisis = listOf("MET_123", "MET_456"),
                formationsChoisies = listOf("fl1234", "fl5678"),
                domainesInterets = listOf("T_ITM_1054", "T_ITM_1534", "T_ITM_1248", "T_ITM_1351"),
            )
        given(suggestionHttpClient.recupererLesAffinitees(profilEleve = profil)).willReturn(
            AffinitesPourProfil(
                metiersTriesParAffinites = listOf("MET_123", "MET_002", "MET_456", "MET_001"),
                formations =
                    listOf(
                        FormationAvecSonAffinite(idFormation = "fl00012", tauxAffinite = 0.9f),
                        FormationAvecSonAffinite(idFormation = "fl0001", tauxAffinite = 0.7f),
                        FormationAvecSonAffinite(idFormation = "fl00014", tauxAffinite = 0.5f),
                        FormationAvecSonAffinite(idFormation = "fl0002", tauxAffinite = 0.6f),
                    ),
            ),
        )
        val explications = mock(ExplicationsSuggestion::class.java)
        given(suggestionHttpClient.recupererLesExplications(profilEleve = profil, "fl0001")).willReturn(explications)

        // When
        val resultat = recupererFormationService.recupererFormation(profilEleve = profil, idFormation = "fl0001")

        // Then
        assertThat(resultat).usingRecursiveComparison().isEqualTo(
            FicheFormation.FicheFormationPourProfil(
                id = "fl0001",
                nom = "CAP Fleuriste",
                formationsAssociees = listOf("fl0010", "fl0012"),
                descriptifFormation =
                    "Le CAP Fleuriste est un diplôme de niveau 3 qui permet d acquérir les compétences nécessaires" +
                        " pour exercer le métier de fleuriste. La formation dure 2 ans et est accessible après la classe de 3ème. " +
                        "Elle comprend des enseignements généraux (français, mathématiques, histoire-géographie, etc.) et des " +
                        "enseignements professionnels (botanique, art floral, techniques de vente, etc.). Le CAP Fleuriste " +
                        "permet d exercer le métier de fleuriste en boutique, en grande surface, en jardinerie ou en " +
                        "atelier de composition florale.",
                descriptifDiplome =
                    "Le Certificat d'Aptitude Professionnelle (CAP) est un diplôme national de niveau 3 du " +
                        "système éducatif français, qui atteste l'acquisition d'une qualification professionnelle dans un " +
                        "métier précis. Il est généralement obtenu après une formation de deux ans suivant la fin du collège " +
                        "et s'adresse principalement aux élèves souhaitant entrer rapidement dans la vie active.",
                descriptifAttendus =
                    "Il est attendu des candidats de démontrer une solide compréhension des techniques " +
                        "de base de la floristerie, y compris la composition florale, la reconnaissance des plantes " +
                        "et des fleurs, ainsi que les soins et l'entretien des végétaux.",
                descriptifConseils =
                    "Nous vous conseillons de développer une sensibilité artistique et de rester informé des tendances " +
                        "actuelles en matière de design floral pour exceller dans ce domaine.",
                criteresAdmission =
                    CriteresAdmission(
                        principauxPoints =
                            listOf(
                                "Les compétences, méthodes de travail et savoir-faire",
                                "Motivation et cohérence de ton projet",
                            ),
                        moyenneGenerale =
                            MoyenneGenerale(
                                centille5eme = 0.0f,
                                centille25eme = 12.0f,
                                centille75eme = 15.0f,
                                centille95eme = 19.0f,
                            ),
                    ),
                liens = listOf("https://www.onisep.fr/ressources/univers-formation/formations/cap-fleuriste"),
                metiersTriesParAffinites =
                    listOf(
                        MetierDetaille(
                            id = "MET_002",
                            nom = "Fleuriste événementiel",
                            descriptif =
                                "Le fleuriste événementiel est un artisan qui confectionne et vend des bouquets, des " +
                                    "compositions florales, des plantes et des accessoires de décoration pour des événements " +
                                    "particuliers (mariages, baptêmes, anniversaires, réceptions, etc.). Il peut également être " +
                                    "amené à conseiller ses clients sur le choix des fleurs et des plantes en fonction de " +
                                    "l occasion et de leur budget. Le fleuriste événementiel peut travailler en boutique, " +
                                    "en grande surface, en jardinerie ou en atelier de composition florale.",
                            liens = listOf("https://www.onisep.fr/ressources/univers-metier/metiers/fleuriste"),
                        ),
                        MetierDetaille(
                            id = "MET_001",
                            nom = "Fleuriste",
                            descriptif =
                                "Le fleuriste est un artisan qui confectionne et vend des bouquets, des compositions " +
                                    "florales, des plantes et des accessoires de décoration. Il peut également être amené à " +
                                    "conseiller ses clients sur le choix des fleurs et des plantes en fonction de l occasion " +
                                    "et de leur budget. Le fleuriste peut travailler en boutique, en grande surface, en jardinerie " +
                                    "ou en atelier de composition florale.",
                            liens = listOf("https://www.onisep.fr/ressources/univers-metier/metiers/fleuriste"),
                        ),
                    ),
                communesTrieesParAffinites = listOf("Caen", "Paris", "Marseille"),
                tauxAffinite = 0.7f,
                explications = explications,
            ),
        )
    }
}
