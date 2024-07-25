package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.lien.domain.entity.Lien
import fr.gouv.monprojetsup.formation.domain.entity.CritereAnalyseCandidature
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionDetaillees
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation
import fr.gouv.monprojetsup.formation.domain.entity.Formation
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil.FormationAvecSonAffinite
import fr.gouv.monprojetsup.formation.domain.port.FormationRepository
import fr.gouv.monprojetsup.formation.domain.port.SuggestionHttpClient
import fr.gouv.monprojetsup.formation.entity.Communes
import fr.gouv.monprojetsup.metier.domain.entity.Metier
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.referentiel.domain.entity.SituationAvanceeProjetSup
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
    lateinit var recupererCommunesDUneFormationService: RecupererCommunesDUneFormationService

    @Mock
    lateinit var critereAnalyseCandidatureService: CritereAnalyseCandidatureService

    @Mock
    lateinit var recupererExplicationsEtExemplesMetiersPourFormationService: RecupererExplicationsEtExemplesMetiersPourFormationService

    @Mock
    lateinit var statistiquesDesAdmisPourFormationsService: StatistiquesDesAdmisPourFormationsService

    @Mock
    lateinit var metiersTriesParProfilBuilder: MetiersTriesParProfilBuilder

    @Mock
    lateinit var calculDuTauxDAffiniteBuilder: CalculDuTauxDAffiniteBuilder

    @InjectMocks
    lateinit var recupererFormationService: RecupererFormationService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    private val formation =
        Formation(
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
            valeurCriteresAnalyseCandidature = listOf(10, 0, 18, 42, 30),
        )

    private val criteresAnalyseCandidature =
        listOf(
            CritereAnalyseCandidature(nom = "Compétences académiques", pourcentage = 10),
            CritereAnalyseCandidature(nom = "Résultats académiques", pourcentage = 18),
            CritereAnalyseCandidature(nom = "Savoir-être", pourcentage = 42),
            CritereAnalyseCandidature(nom = "Motivation, connaissance", pourcentage = 30),
        )

    @Nested
    inner class ProfilNull {
        @Test
        fun `doit retourner une fiche formation sans le profil`() {
            // Given
            given(critereAnalyseCandidatureService.recupererCriteresAnalyseCandidature(formation)).willReturn(
                criteresAnalyseCandidature,
            )
            given(formationRepository.recupererUneFormationAvecSesMetiers("fl0001")).willReturn(formation)
            given(recupererCommunesDUneFormationService.recupererNomCommunes(idFormation = "fl0001")).willReturn(
                listOf("Paris", "Marseille", "Caen"),
            )
            val statistiquesDesAdmis = mock(StatistiquesDesAdmis::class.java)
            given(
                statistiquesDesAdmisPourFormationsService.recupererStatistiquesAdmisDUneFormation(
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
                    metiers = emptyList(),
                    communes = listOf("Paris", "Marseille", "Caen"),
                    criteresAnalyseCandidature =
                        listOf(
                            CritereAnalyseCandidature(nom = "Compétences académiques", pourcentage = 10),
                            CritereAnalyseCandidature(nom = "Résultats académiques", pourcentage = 18),
                            CritereAnalyseCandidature(nom = "Savoir-être", pourcentage = 42),
                            CritereAnalyseCandidature(nom = "Motivation, connaissance", pourcentage = 30),
                        ),
                    statistiquesDesAdmis = statistiquesDesAdmis,
                ),
            )
        }

        @Test
        fun `ne doit pas appeler le suggestionHttpClient`() {
            // Given
            given(critereAnalyseCandidatureService.recupererCriteresAnalyseCandidature(formation)).willReturn(
                criteresAnalyseCandidature,
            )
            given(formationRepository.recupererUneFormationAvecSesMetiers("fl0001")).willReturn(formation)
            given(recupererCommunesDUneFormationService.recupererNomCommunes("fl0001")).willReturn(
                listOf("Paris", "Marseille", "Caen"),
            )
            val statistiquesDesAdmis = mock(StatistiquesDesAdmis::class.java)
            given(
                statistiquesDesAdmisPourFormationsService.recupererStatistiquesAdmisDUneFormation(
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
                situation = SituationAvanceeProjetSup.PROJET_PRECIS,
                classe = ChoixNiveau.TERMINALE,
                baccalaureat = "Générale",
                dureeEtudesPrevue = ChoixDureeEtudesPrevue.INDIFFERENT,
                alternance = ChoixAlternance.PAS_INTERESSE,
                communesFavorites = listOf(Communes.CAEN),
                specialites = listOf("1001", "1049"),
                centresInterets = listOf("T_ROME_2092381917", "T_IDEO2_4812"),
                moyenneGenerale = 14f,
                metiersFavoris = listOf("MET_123", "MET_456"),
                formationsFavorites = listOf("fl1234", "fl5678"),
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
        fun `doit retourner une fiche formation avec les explications`() {
            // Given
            given(critereAnalyseCandidatureService.recupererCriteresAnalyseCandidature(formation)).willReturn(
                criteresAnalyseCandidature,
            )
            given(formationRepository.recupererUneFormationAvecSesMetiers("fl0001")).willReturn(formation)
            given(recupererCommunesDUneFormationService.recupererNomCommunesTriesParAffinites("fl0001", listOf(Communes.CAEN))).willReturn(
                listOf("Caen", "Paris", "Marseille"),
            )
            given(
                calculDuTauxDAffiniteBuilder.calculDuTauxDAffinite(
                    listOf(
                        FormationAvecSonAffinite(idFormation = "fl00012", tauxAffinite = 0.9f),
                        FormationAvecSonAffinite(idFormation = "fl0001", tauxAffinite = 0.7f),
                        FormationAvecSonAffinite(idFormation = "fl00014", tauxAffinite = 0.6648471f),
                        FormationAvecSonAffinite(idFormation = "fl0002", tauxAffinite = 0.1250544f),
                    ),
                    "fl0001",
                ),
            ).willReturn(70)
            given(suggestionHttpClient.recupererLesSuggestions(profilEleve = profil)).willReturn(suggestionsPourUnProfil)
            val metiersSuggeres =
                listOf(
                    Metier(
                        id = "MET_456",
                        nom = "Fleuriste bis",
                        descriptif = "Descriptif MET_456",
                        liens = emptyList(),
                    ),
                    Metier(
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
                )
            val explicationsEtExemplesMetiers = Pair(mock(ExplicationsSuggestionDetaillees::class.java), metiersSuggeres)
            given(
                recupererExplicationsEtExemplesMetiersPourFormationService.recupererExplicationsEtExemplesDeMetiers(
                    profilEleve = profil,
                    idFormation = "fl0001",
                ),
            ).willReturn(explicationsEtExemplesMetiers)
            val statistiquesDesAdmis = mock(StatistiquesDesAdmis::class.java)
            given(
                statistiquesDesAdmisPourFormationsService.recupererStatistiquesAdmisDUneFormation(
                    idBaccalaureat = "Générale",
                    classe = ChoixNiveau.TERMINALE,
                    idFormation = "fl0001",
                ),
            ).willReturn(statistiquesDesAdmis)
            given(
                metiersTriesParProfilBuilder.trierMetiersParAffinites(
                    metiersSuggeres,
                    listOf("MET_123", "MET_002", "MET_456", "MET_001"),
                ),
            ).willReturn(
                listOf(
                    Metier(
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
                    Metier(
                        id = "MET_456",
                        nom = "Fleuriste bis",
                        descriptif = "Descriptif MET_456",
                        liens = emptyList(),
                    ),
                ),
            )

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
                            Metier(
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
                            Metier(
                                id = "MET_456",
                                nom = "Fleuriste bis",
                                descriptif = "Descriptif MET_456",
                                liens = emptyList(),
                            ),
                        ),
                    communesTrieesParAffinites = listOf("Caen", "Paris", "Marseille"),
                    tauxAffinite = 70,
                    explications = explicationsEtExemplesMetiers.first,
                    criteresAnalyseCandidature =
                        listOf(
                            CritereAnalyseCandidature(nom = "Compétences académiques", pourcentage = 10),
                            CritereAnalyseCandidature(nom = "Résultats académiques", pourcentage = 18),
                            CritereAnalyseCandidature(nom = "Savoir-être", pourcentage = 42),
                            CritereAnalyseCandidature(nom = "Motivation, connaissance", pourcentage = 30),
                        ),
                    statistiquesDesAdmis = statistiquesDesAdmis,
                ),
            )
        }
    }
}
