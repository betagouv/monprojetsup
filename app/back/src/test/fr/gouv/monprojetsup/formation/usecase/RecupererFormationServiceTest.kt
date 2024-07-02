package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.formation.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.formation.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.formation.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.formation.domain.entity.CriteresAnalyseCandidature
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionDetaillees
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation
import fr.gouv.monprojetsup.formation.domain.entity.FormationDetaillee
import fr.gouv.monprojetsup.formation.domain.entity.Lien
import fr.gouv.monprojetsup.formation.domain.entity.MetierDetaille
import fr.gouv.monprojetsup.formation.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil.FormationAvecSonAffinite
import fr.gouv.monprojetsup.formation.domain.entity.TripletAffectation
import fr.gouv.monprojetsup.formation.domain.port.CriteresAnalyseCandidatureRepository
import fr.gouv.monprojetsup.formation.domain.port.FormationRepository
import fr.gouv.monprojetsup.formation.domain.port.SuggestionHttpClient
import fr.gouv.monprojetsup.formation.domain.port.TripletAffectationRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.mock
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class RecupererFormationServiceTest {
    @Mock
    lateinit var suggestionHttpClient: SuggestionHttpClient

    @Mock
    lateinit var formationRepository: FormationRepository

    @Mock
    lateinit var tripletAffectationRepository: TripletAffectationRepository

    @Mock
    lateinit var criteresAnalyseCandidatureRepository: CriteresAnalyseCandidatureRepository

    @Mock
    lateinit var recupererExplicationsFormationService: RecupererExplicationsFormationService

    @Mock
    lateinit var statistiquesDesAdmisService: StatistiquesDesAdmisService

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
            descriptifGeneral = "Le CAP Fleuriste est un diplôme de niveau 3 qui permet d acquérir les ...",
            descriptifAttendus = "Il est attendu des candidats de démontrer une solide compréhension des techniques ...",
            descriptifDiplome = "Le Certificat d'Aptitude Professionnelle (CAP) est un diplôme national de niveau 3 du ..",
            descriptifConseils = "Nous vous conseillons de développer une sensibilité artistique et de rester informé ...",
            formationsAssociees = listOf("fl0010", "fl0012"),
            liens =
                listOf(
                    Lien(nom = "Voir sur Onisep", url = "https://www.onisep.fr/ressources/univers-formation/formations/cap-fleuriste"),
                    Lien(nom = "Voir sur Parcoursup", url = "https://www.parcoursup.fr/cap-fleuriste"),
                ),
            metiers =
                listOf(
                    MetierDetaille(
                        id = "MET_001",
                        nom = "Fleuriste",
                        descriptif = "Le fleuriste est un artisan qui confectionne et vend des bouquets, des ...",
                        liens =
                            listOf(
                                Lien(nom = "Voir sur Onisep", url = "https://www.onisep.fr/ressources/univers-metier/metiers/fleuriste"),
                            ),
                    ),
                    MetierDetaille(
                        id = "MET_002",
                        nom = "Fleuriste événementiel",
                        descriptif = "Le fleuriste événementiel est un artisan qui confectionne et vend des bouquets ...",
                        liens =
                            listOf(
                                Lien(nom = "Voir sur Onisep", url = "https://www.onisep.fr/ressources/univers-metier/metiers/fleuriste"),
                            ),
                    ),
                ),
            valeurCriteresAnalyseCandidature = listOf(10, 0, 18, 42, 30),
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

    private val criteresAnalyseCandidature =
        listOf(
            CriteresAnalyseCandidature(nom = "Compétences académiques", pourcentage = 10),
            CriteresAnalyseCandidature(
                nom = "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                pourcentage = 0,
            ),
            CriteresAnalyseCandidature(nom = "Résultats académiques", pourcentage = 18),
            CriteresAnalyseCandidature(nom = "Savoir-être", pourcentage = 42),
            CriteresAnalyseCandidature(nom = "Motivation, connaissance", pourcentage = 30),
        )

    @Nested
    inner class ProfilNull {
        @Test
        fun `doit retourner une fiche formation sans le profil avec les criteres filtrant ceux à 0`() {
            // Given
            given(criteresAnalyseCandidatureRepository.recupererLesCriteresDUneFormation(listOf(10, 0, 18, 42, 30))).willReturn(
                criteresAnalyseCandidature,
            )
            given(formationRepository.recupererUneFormationAvecSesMetiers("fl0001")).willReturn(formationDetaillee)
            given(tripletAffectationRepository.recupererLesTripletsAffectationDUneFormation(idFormation = "fl0001")).willReturn(
                tripletsAffectations,
            )
            val statistiquesDesAdmis = mock(StatistiquesDesAdmis::class.java)
            given(
                statistiquesDesAdmisService.recupererStatistiquesAdmisDUneFormation(
                    idBaccalaureat = null,
                    classe = null,
                    idFormation = "fl0001",
                ),
            ).willReturn(statistiquesDesAdmis)

            // When
            val resultat = recupererFormationService.recupererFormation(profilEleve = null, idFormation = "fl0001")

            // Then
            assertThat(resultat).usingRecursiveComparison().isEqualTo(
                FicheFormation.FicheFormationSansProfil(
                    id = "fl0001",
                    nom = "CAP Fleuriste",
                    formationsAssociees = listOf("fl0010", "fl0012"),
                    descriptifGeneral = "Le CAP Fleuriste est un diplôme de niveau 3 qui permet d acquérir les ...",
                    descriptifAttendus = "Il est attendu des candidats de démontrer une solide compréhension des techniques ...",
                    descriptifDiplome = "Le Certificat d'Aptitude Professionnelle (CAP) est un diplôme national de niveau 3 du ..",
                    descriptifConseils = "Nous vous conseillons de développer une sensibilité artistique et de rester informé ...",
                    liens =
                        listOf(
                            Lien(
                                nom = "Voir sur Onisep",
                                url = "https://www.onisep.fr/ressources/univers-formation/formations/cap-fleuriste",
                            ),
                            Lien(nom = "Voir sur Parcoursup", url = "https://www.parcoursup.fr/cap-fleuriste"),
                        ),
                    metiers =
                        listOf(
                            MetierDetaille(
                                id = "MET_001",
                                nom = "Fleuriste",
                                descriptif = "Le fleuriste est un artisan qui confectionne et vend des bouquets, des ...",
                                liens =
                                    listOf(
                                        Lien(
                                            nom = "Voir sur Onisep",
                                            url = "https://www.onisep.fr/ressources/univers-metier/metiers/fleuriste",
                                        ),
                                    ),
                            ),
                            MetierDetaille(
                                id = "MET_002",
                                nom = "Fleuriste événementiel",
                                descriptif = "Le fleuriste événementiel est un artisan qui confectionne et vend des bouquets ...",
                                liens =
                                    listOf(
                                        Lien(
                                            nom = "Voir sur Onisep",
                                            url = "https://www.onisep.fr/ressources/univers-metier/metiers/fleuriste",
                                        ),
                                    ),
                            ),
                        ),
                    communes = listOf("Paris", "Marseille", "Caen"),
                    criteresAnalyseCandidature =
                        listOf(
                            CriteresAnalyseCandidature(nom = "Compétences académiques", pourcentage = 10),
                            CriteresAnalyseCandidature(nom = "Résultats académiques", pourcentage = 18),
                            CriteresAnalyseCandidature(nom = "Savoir-être", pourcentage = 42),
                            CriteresAnalyseCandidature(nom = "Motivation, connaissance", pourcentage = 30),
                        ),
                    statistiquesDesAdmis = statistiquesDesAdmis,
                ),
            )
        }

        @Test
        fun `ne doit pas appeler le suggestionHttpClient`() {
            // Given
            given(criteresAnalyseCandidatureRepository.recupererLesCriteresDUneFormation(listOf(10, 0, 18, 42, 30))).willReturn(
                criteresAnalyseCandidature,
            )
            given(formationRepository.recupererUneFormationAvecSesMetiers("fl0001")).willReturn(formationDetaillee)
            given(tripletAffectationRepository.recupererLesTripletsAffectationDUneFormation("fl0001")).willReturn(
                tripletsAffectations,
            )
            val statistiquesDesAdmis = mock(StatistiquesDesAdmis::class.java)
            given(
                statistiquesDesAdmisService.recupererStatistiquesAdmisDUneFormation(
                    idBaccalaureat = null,
                    classe = null,
                    idFormation = "fl0001",
                ),
            ).willReturn(statistiquesDesAdmis)

            // When
            recupererFormationService.recupererFormation(profilEleve = null, idFormation = "fl0001")

            // Then
            then(suggestionHttpClient).shouldHaveNoInteractions()
        }
    }

    @Nested
    inner class ProfilNonNull {
        private val profil =
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

        private val suggestionsPourUnProfil =
            SuggestionsPourUnProfil(
                metiersTriesParAffinites = listOf("MET_123", "MET_002", "MET_456", "MET_001"),
                formations =
                    listOf(
                        FormationAvecSonAffinite(idFormation = "fl00012", tauxAffinite = 0.9f),
                        FormationAvecSonAffinite(idFormation = "fl0001", tauxAffinite = 0.7f),
                        FormationAvecSonAffinite(idFormation = "fl00014", tauxAffinite = 0.6648471f),
                        FormationAvecSonAffinite(idFormation = "fl0002", tauxAffinite = 0.1250544f),
                    ),
            )

        @Test
        fun `doit retourner une fiche formation avec les metiers et villes triés, les explications et les criteres filtrant ceux à 0`() {
            // Given
            given(criteresAnalyseCandidatureRepository.recupererLesCriteresDUneFormation(listOf(10, 0, 18, 42, 30))).willReturn(
                criteresAnalyseCandidature,
            )
            given(formationRepository.recupererUneFormationAvecSesMetiers("fl0001")).willReturn(formationDetaillee)
            given(tripletAffectationRepository.recupererLesTripletsAffectationDUneFormation("fl0001")).willReturn(
                tripletsAffectations,
            )
            given(suggestionHttpClient.recupererLesSuggestions(profilEleve = profil)).willReturn(suggestionsPourUnProfil)
            val explications = mock(ExplicationsSuggestionDetaillees::class.java)
            given(
                recupererExplicationsFormationService.recupererExplications(
                    profilEleve = profil,
                    idFormation = "fl0001",
                ),
            ).willReturn(explications)
            val statistiquesDesAdmis = mock(StatistiquesDesAdmis::class.java)
            given(
                statistiquesDesAdmisService.recupererStatistiquesAdmisDUneFormation(
                    idBaccalaureat = "Générale",
                    classe = ChoixNiveau.TERMINALE,
                    idFormation = "fl0001",
                ),
            ).willReturn(statistiquesDesAdmis)

            // When
            val resultat = recupererFormationService.recupererFormation(profilEleve = profil, idFormation = "fl0001")

            // Then
            assertThat(resultat).usingRecursiveComparison().isEqualTo(
                FicheFormation.FicheFormationPourProfil(
                    id = "fl0001",
                    nom = "CAP Fleuriste",
                    formationsAssociees = listOf("fl0010", "fl0012"),
                    descriptifGeneral = "Le CAP Fleuriste est un diplôme de niveau 3 qui permet d acquérir les ...",
                    descriptifAttendus = "Il est attendu des candidats de démontrer une solide compréhension des techniques ...",
                    descriptifDiplome = "Le Certificat d'Aptitude Professionnelle (CAP) est un diplôme national de niveau 3 du ..",
                    descriptifConseils = "Nous vous conseillons de développer une sensibilité artistique et de rester informé ...",
                    liens =
                        listOf(
                            Lien(
                                nom = "Voir sur Onisep",
                                url = "https://www.onisep.fr/ressources/univers-formation/formations/cap-fleuriste",
                            ),
                            Lien(nom = "Voir sur Parcoursup", url = "https://www.parcoursup.fr/cap-fleuriste"),
                        ),
                    metiersTriesParAffinites =
                        listOf(
                            MetierDetaille(
                                id = "MET_002",
                                nom = "Fleuriste événementiel",
                                descriptif = "Le fleuriste événementiel est un artisan qui confectionne et vend des bouquets ...",
                                liens =
                                    listOf(
                                        Lien(
                                            nom = "Voir sur Onisep",
                                            url = "https://www.onisep.fr/ressources/univers-metier/metiers/fleuriste",
                                        ),
                                    ),
                            ),
                            MetierDetaille(
                                id = "MET_001",
                                nom = "Fleuriste",
                                descriptif = "Le fleuriste est un artisan qui confectionne et vend des bouquets, des ...",
                                liens =
                                    listOf(
                                        Lien(
                                            nom = "Voir sur Onisep",
                                            url = "https://www.onisep.fr/ressources/univers-metier/metiers/fleuriste",
                                        ),
                                    ),
                            ),
                        ),
                    communesTrieesParAffinites = listOf("Caen", "Paris", "Marseille"),
                    tauxAffinite = 70,
                    explications = explications,
                    criteresAnalyseCandidature =
                        listOf(
                            CriteresAnalyseCandidature(nom = "Compétences académiques", pourcentage = 10),
                            CriteresAnalyseCandidature(nom = "Résultats académiques", pourcentage = 18),
                            CriteresAnalyseCandidature(nom = "Savoir-être", pourcentage = 42),
                            CriteresAnalyseCandidature(nom = "Motivation, connaissance", pourcentage = 30),
                        ),
                    statistiquesDesAdmis = statistiquesDesAdmis,
                ),
            )
        }

        @Test
        fun `si la formation n'est pas présente dans la liste retournée par l'api suggestion, doit retourner 0 en taux d'affinité`() {
            // Given
            given(criteresAnalyseCandidatureRepository.recupererLesCriteresDUneFormation(listOf(10, 0, 18, 42, 30))).willReturn(
                criteresAnalyseCandidature,
            )
            given(formationRepository.recupererUneFormationAvecSesMetiers("fl00011")).willReturn(formationDetaillee.copy(id = "fl00011"))
            given(tripletAffectationRepository.recupererLesTripletsAffectationDUneFormation("fl00011")).willReturn(
                tripletsAffectations,
            )
            given(suggestionHttpClient.recupererLesSuggestions(profilEleve = profil)).willReturn(suggestionsPourUnProfil)
            val explications = mock(ExplicationsSuggestionDetaillees::class.java)
            given(
                recupererExplicationsFormationService.recupererExplications(
                    profilEleve = profil,
                    idFormation = "fl00011",
                ),
            ).willReturn(explications)
            val statistiquesDesAdmis = mock(StatistiquesDesAdmis::class.java)
            given(
                statistiquesDesAdmisService.recupererStatistiquesAdmisDUneFormation(
                    idBaccalaureat = "Générale",
                    classe = ChoixNiveau.TERMINALE,
                    idFormation = "fl00011",
                ),
            ).willReturn(statistiquesDesAdmis)

            // When
            val resultat = recupererFormationService.recupererFormation(profilEleve = profil, idFormation = "fl00011")

            // Then
            resultat as FicheFormation.FicheFormationPourProfil
            assertThat(resultat.tauxAffinite).isEqualTo(0)
        }

        @Test
        fun `si la valeur du taux d'affinité est strictement inférieur à x,xx5, doit l'arrondir à l'inférieur`() {
            // Given
            given(criteresAnalyseCandidatureRepository.recupererLesCriteresDUneFormation(listOf(10, 0, 18, 42, 30))).willReturn(
                criteresAnalyseCandidature,
            )
            given(formationRepository.recupererUneFormationAvecSesMetiers("fl0002")).willReturn(formationDetaillee.copy(id = "fl0002"))
            given(tripletAffectationRepository.recupererLesTripletsAffectationDUneFormation("fl0002")).willReturn(
                tripletsAffectations,
            )
            given(suggestionHttpClient.recupererLesSuggestions(profilEleve = profil)).willReturn(suggestionsPourUnProfil)
            val explications = mock(ExplicationsSuggestionDetaillees::class.java)
            given(
                recupererExplicationsFormationService.recupererExplications(
                    profilEleve = profil,
                    idFormation = "fl0002",
                ),
            ).willReturn(explications)
            val statistiquesDesAdmis = mock(StatistiquesDesAdmis::class.java)
            given(
                statistiquesDesAdmisService.recupererStatistiquesAdmisDUneFormation(
                    idBaccalaureat = "Générale",
                    classe = ChoixNiveau.TERMINALE,
                    idFormation = "fl0002",
                ),
            ).willReturn(statistiquesDesAdmis)

            // When
            val resultat = recupererFormationService.recupererFormation(profilEleve = profil, idFormation = "fl0002")

            // Then
            resultat as FicheFormation.FicheFormationPourProfil
            assertThat(resultat.tauxAffinite).isEqualTo(13)
        }

        @Test
        fun `si la valeur du taux d'affinité est supérieur ou égale à x,xx5, doit l'arrondir au supérieur`() {
            // Given
            given(criteresAnalyseCandidatureRepository.recupererLesCriteresDUneFormation(listOf(10, 0, 18, 42, 30))).willReturn(
                criteresAnalyseCandidature,
            )
            given(formationRepository.recupererUneFormationAvecSesMetiers("fl00014")).willReturn(formationDetaillee.copy(id = "fl00014"))
            given(tripletAffectationRepository.recupererLesTripletsAffectationDUneFormation("fl00014")).willReturn(
                tripletsAffectations,
            )
            given(suggestionHttpClient.recupererLesSuggestions(profilEleve = profil)).willReturn(suggestionsPourUnProfil)
            val explications = mock(ExplicationsSuggestionDetaillees::class.java)
            given(
                recupererExplicationsFormationService.recupererExplications(
                    profilEleve = profil,
                    idFormation = "fl00014",
                ),
            ).willReturn(explications)
            val statistiquesDesAdmis = mock(StatistiquesDesAdmis::class.java)
            given(
                statistiquesDesAdmisService.recupererStatistiquesAdmisDUneFormation(
                    idBaccalaureat = "Générale",
                    classe = ChoixNiveau.TERMINALE,
                    idFormation = "fl00014",
                ),
            ).willReturn(statistiquesDesAdmis)

            // When
            val resultat = recupererFormationService.recupererFormation(profilEleve = profil, idFormation = "fl00014")

            // Then
            resultat as FicheFormation.FicheFormationPourProfil
            assertThat(resultat.tauxAffinite).isEqualTo(66)
        }
    }
}
