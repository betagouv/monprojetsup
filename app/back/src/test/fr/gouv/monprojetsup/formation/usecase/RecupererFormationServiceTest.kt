package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.lien.domain.entity.Lien
import fr.gouv.monprojetsup.eleve.domain.entity.VoeuFormation
import fr.gouv.monprojetsup.eleve.entity.CommunesFavorites
import fr.gouv.monprojetsup.formation.domain.entity.CommuneAvecVoeuxAuxAlentours
import fr.gouv.monprojetsup.formation.domain.entity.CommuneAvecVoeuxAuxAlentours.VoeuAvecDistance
import fr.gouv.monprojetsup.formation.domain.entity.CritereAnalyseCandidature
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionDetaillees
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation
import fr.gouv.monprojetsup.formation.domain.entity.Formation
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil.FormationAvecSonAffinite
import fr.gouv.monprojetsup.formation.domain.entity.Voeu
import fr.gouv.monprojetsup.formation.domain.port.FormationRepository
import fr.gouv.monprojetsup.formation.domain.port.SuggestionHttpClient
import fr.gouv.monprojetsup.formation.entity.CommunesCourtes.CAEN
import fr.gouv.monprojetsup.formation.entity.CommunesCourtes.LYON
import fr.gouv.monprojetsup.formation.entity.CommunesCourtes.MARSEILLE
import fr.gouv.monprojetsup.formation.entity.CommunesCourtes.PARIS15EME
import fr.gouv.monprojetsup.formation.entity.CommunesCourtes.PARIS5EME
import fr.gouv.monprojetsup.formation.entity.CommunesCourtes.SAINT_MALO
import fr.gouv.monprojetsup.formation.entity.CommunesCourtes.STRASBOURG
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
    lateinit var recupererInformationsSurLesVoeuxEtLeursCommunesService:
        RecupererInformationsSurLesVoeuxEtLeursCommunesService

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
            apprentissage = true,
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
            given(formationRepository.recupererUneFormation("fl0001")).willReturn(formation)
            val voeuxPossiblesPourLaFormationFL0001 =
                listOf(
                    Voeu(
                        id = "ta10",
                        nom = "Nom du ta10",
                        commune = LYON,
                        latitude = 45.75,
                        longitude = 4.85,
                    ),
                    Voeu(
                        id = "ta3",
                        nom = "Nom du ta3",
                        commune = PARIS5EME,
                        longitude = 2.344,
                        latitude = 48.846,
                    ),
                    Voeu(
                        id = "ta11",
                        nom = "Nom du ta11",
                        commune = LYON,
                        latitude = 45.75,
                        longitude = 4.85,
                    ),
                    Voeu(
                        id = "ta32",
                        nom = "Nom du ta32",
                        commune = PARIS15EME,
                        longitude = 2.2885659,
                        latitude = 48.851227,
                    ),
                    Voeu(
                        id = "ta17",
                        nom = "Nom du ta17",
                        commune = STRASBOURG,
                        longitude = 1.666667,
                        latitude = 50.266666,
                    ),
                    Voeu(
                        id = "ta7",
                        nom = "Nom du ta7",
                        commune = MARSEILLE,
                        latitude = 43.300000,
                        longitude = 5.400000,
                    ),
                )
            given(
                recupererInformationsSurLesVoeuxEtLeursCommunesService.recupererVoeux(idFormation = "fl0001", true),
            ).willReturn(
                voeuxPossiblesPourLaFormationFL0001,
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
                    voeux = voeuxPossiblesPourLaFormationFL0001,
                    criteresAnalyseCandidature =
                        listOf(
                            CritereAnalyseCandidature(nom = "Compétences académiques", pourcentage = 10),
                            CritereAnalyseCandidature(nom = "Résultats académiques", pourcentage = 18),
                            CritereAnalyseCandidature(nom = "Savoir-être", pourcentage = 42),
                            CritereAnalyseCandidature(nom = "Motivation, connaissance", pourcentage = 30),
                        ),
                    statistiquesDesAdmis = statistiquesDesAdmis,
                    apprentissage = true,
                ),
            )
        }

        @Test
        fun `ne doit pas appeler le suggestionHttpClient`() {
            // Given
            given(critereAnalyseCandidatureService.recupererCriteresAnalyseCandidature(formation)).willReturn(
                criteresAnalyseCandidature,
            )
            given(formationRepository.recupererUneFormation("fl0001")).willReturn(formation)
            val voeuxFormationFL0001 =
                listOf(
                    Voeu(
                        id = "ta10",
                        nom = "Nom du ta10",
                        commune = LYON,
                        latitude = 45.75,
                        longitude = 4.85,
                    ),
                    Voeu(
                        id = "ta3",
                        nom = "Nom du ta3",
                        commune = PARIS5EME,
                        longitude = 2.344,
                        latitude = 48.846,
                    ),
                    Voeu(
                        id = "ta11",
                        nom = "Nom du ta11",
                        commune = LYON,
                        latitude = 45.75,
                        longitude = 4.85,
                    ),
                    Voeu(
                        id = "ta32",
                        nom = "Nom du ta32",
                        commune = PARIS15EME,
                        longitude = 2.2885659,
                        latitude = 48.851227,
                    ),
                    Voeu(
                        id = "ta17",
                        nom = "Nom du ta17",
                        commune = STRASBOURG,
                        longitude = 1.666667,
                        latitude = 50.266666,
                    ),
                    Voeu(
                        id = "ta7",
                        nom = "Nom du ta7",
                        commune = MARSEILLE,
                        latitude = 43.300000,
                        longitude = 5.400000,
                    ),
                )
            given(
                recupererInformationsSurLesVoeuxEtLeursCommunesService.recupererVoeux(
                    idFormation = "fl0001",
                    obsoletesInclus = true,
                ),
            ).willReturn(
                voeuxFormationFL0001,
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
            ProfilEleve.AvecProfilExistant(
                id = "adcf627c-36dd-4df5-897b-159443a6d49c",
                situation = SituationAvanceeProjetSup.PROJET_PRECIS,
                classe = ChoixNiveau.TERMINALE,
                baccalaureat = "Générale",
                dureeEtudesPrevue = ChoixDureeEtudesPrevue.INDIFFERENT,
                alternance = ChoixAlternance.PAS_INTERESSE,
                communesFavorites = listOf(CommunesFavorites.CAEN),
                specialites = listOf("1001", "1049"),
                centresInterets = listOf("T_ROME_2092381917", "T_IDEO2_4812"),
                moyenneGenerale = 14f,
                metiersFavoris = listOf("MET_123", "MET_456"),
                formationsFavorites =
                    listOf(
                        VoeuFormation(
                            idFormation = "fl1234",
                            niveauAmbition = 1,
                            voeuxChoisis = emptyList(),
                            priseDeNote = null,
                        ),
                        VoeuFormation(
                            idFormation = "fl5678",
                            niveauAmbition = 3,
                            voeuxChoisis = listOf("ta1", "ta2"),
                            priseDeNote = "Mon voeu préféré",
                        ),
                    ),
                domainesInterets = listOf("T_ITM_1054", "T_ITM_1534", "T_ITM_1248", "T_ITM_1351"),
                corbeilleFormations = listOf("fl0001"),
                compteParcoursupLie = true,
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
            given(formationRepository.recupererUneFormation("fl0001")).willReturn(formation)
            val voeuxTries =
                listOf(
                    Voeu(
                        id = "ta17",
                        nom = "Nom du ta17",
                        commune = CAEN,
                        latitude = 49.183334,
                        longitude = -0.350000,
                    ),
                    Voeu(
                        id = "ta10",
                        nom = "Nom du ta10",
                        commune = LYON,
                        latitude = 45.75,
                        longitude = 4.85,
                    ),
                    Voeu(
                        id = "ta3",
                        nom = "Nom du ta3",
                        commune = PARIS5EME,
                        longitude = 2.344,
                        latitude = 48.846,
                    ),
                    Voeu(
                        id = "ta11",
                        nom = "Nom du ta11",
                        commune = LYON,
                        latitude = 45.75,
                        longitude = 4.85,
                    ),
                    Voeu(
                        id = "ta32",
                        nom = "Nom du ta32",
                        commune = PARIS15EME,
                        longitude = 2.2885659,
                        latitude = 48.851227,
                    ),
                    Voeu(
                        id = "ta7",
                        nom = "Nom du ta7",
                        commune = SAINT_MALO,
                        latitude = 48.6571,
                        longitude = -1.96914,
                    ),
                )
            val communesTriees = listOf(CAEN, LYON, PARIS5EME, PARIS15EME, SAINT_MALO)
            val voeuxParCommunesFavorites =
                listOf(
                    CommuneAvecVoeuxAuxAlentours(
                        communeFavorite = CommunesFavorites.CAEN,
                        distances =
                            listOf(
                                VoeuAvecDistance(
                                    voeu =
                                        Voeu(
                                            id = "ta17",
                                            nom = "Nom du ta17",
                                            commune = CAEN,
                                            latitude = 49.183334,
                                            longitude = -0.350000,
                                        ),
                                    km = 0,
                                ),
                                VoeuAvecDistance(
                                    voeu =
                                        Voeu(
                                            id = "ta7",
                                            nom = "Nom du ta7",
                                            commune = SAINT_MALO,
                                            latitude = 48.6571,
                                            longitude = -1.96914,
                                        ),
                                    km = 120,
                                ),
                            ),
                    ),
                )
            val informationsSurLesVoeuxEtLeursCommunes =
                FicheFormation.FicheFormationPourProfil.InformationsSurLesVoeuxEtLeursCommunes(
                    voeux = voeuxTries,
                    communesTriees = communesTriees,
                    voeuxParCommunesFavorites = voeuxParCommunesFavorites,
                )
            given(
                recupererInformationsSurLesVoeuxEtLeursCommunesService.recupererInformationsSurLesVoeuxEtLeursCommunes(
                    idFormation = "fl0001",
                    profilEleve = profil,
                    obsoletesInclus = true,
                ),
            ).willReturn(informationsSurLesVoeuxEtLeursCommunes)
            given(
                calculDuTauxDAffiniteBuilder.calculDuTauxDAffinite(
                    formationAvecLeurAffinite =
                        listOf(
                            FormationAvecSonAffinite(idFormation = "fl00012", tauxAffinite = 0.9f),
                            FormationAvecSonAffinite(idFormation = "fl0001", tauxAffinite = 0.7f),
                            FormationAvecSonAffinite(idFormation = "fl00014", tauxAffinite = 0.6648471f),
                            FormationAvecSonAffinite(idFormation = "fl0002", tauxAffinite = 0.1250544f),
                        ),
                    idFormation = "fl0001",
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
                    informationsSurLesVoeuxEtLeursCommunes = informationsSurLesVoeuxEtLeursCommunes,
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
                    apprentissage = true,
                ),
            )
        }
    }
}
