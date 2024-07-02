package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.formation.domain.entity.AffiniteSpecialite
import fr.gouv.monprojetsup.formation.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.formation.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.formation.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.formation.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.formation.domain.entity.Domaine
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationGeographique
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestion
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestion.AutoEvaluationMoyenne
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestion.TypeBaccalaureat
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionDetaillees
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation.FicheFormationPourProfil.ExplicationAutoEvaluationMoyenne
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation.FicheFormationPourProfil.ExplicationTypeBaccalaureat
import fr.gouv.monprojetsup.formation.domain.entity.Formation
import fr.gouv.monprojetsup.formation.domain.entity.InteretSousCategorie
import fr.gouv.monprojetsup.formation.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.formation.domain.port.BaccalaureatRepository
import fr.gouv.monprojetsup.formation.domain.port.DomaineRepository
import fr.gouv.monprojetsup.formation.domain.port.FormationRepository
import fr.gouv.monprojetsup.formation.domain.port.InteretRepository
import fr.gouv.monprojetsup.formation.domain.port.SuggestionHttpClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class RecupererExplicationsFormationServiceTest {
    @Mock
    lateinit var suggestionHttpClient: SuggestionHttpClient

    @Mock
    lateinit var formationRepository: FormationRepository

    @Mock
    lateinit var baccalaureatRepository: BaccalaureatRepository

    @Mock
    lateinit var interetRepository: InteretRepository

    @Mock
    lateinit var domaineRepository: DomaineRepository

    @InjectMocks
    lateinit var recupererExplicationsFormationService: RecupererExplicationsFormationService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    private val bacGeneral = Baccalaureat(id = "Générale", idExterne = "Général", nom = "Série Générale")
    private val bacPro = Baccalaureat(id = "Professionel", idExterne = "P", nom = "Série Professionnelle")
    private val bacSTMG = Baccalaureat(id = "STMG", idExterne = "STMG", nom = "Série STMG")

    private val profil =
        ProfilEleve(
            id = "adcf627c-36dd-4df5-897b-159443a6d49c",
            classe = ChoixNiveau.TERMINALE,
            bac = "Générale",
            dureeEtudesPrevue = ChoixDureeEtudesPrevue.OPTIONS_OUVERTES,
            alternance = ChoixAlternance.PAS_INTERESSE,
            communesPreferees = listOf("Caen"),
            specialites = listOf("1001", "1049"),
            centresInterets = listOf("T_ROME_2092381917", "T_IDEO2_4812"),
            moyenneGenerale = 14f,
            metiersChoisis = listOf("MET_123", "MET_456"),
            formationsChoisies = listOf("fl1234", "fl5678"),
            domainesInterets = listOf("T_ITM_1054", "T_ITM_1534", "T_ITM_1248", "T_ITM_1351"),
        )

    @Nested
    inner class RecupererExplicationsPourUneFormation {
        @Test
        fun `doit retourner les explications duréeEtudesPrévue, alternance, spécialitésChoisies et moyenneGeneraleDesAdmis`() {
            // Given
            val explications =
                mapOf(
                    "fl0001" to
                        ExplicationsSuggestion(
                            dureeEtudesPrevue = ChoixDureeEtudesPrevue.LONGUE,
                            alternance = ChoixAlternance.TRES_INTERESSE,
                            specialitesChoisies =
                                listOf(
                                    AffiniteSpecialite(nomSpecialite = "specialiteA", pourcentage = 12),
                                    AffiniteSpecialite(nomSpecialite = "specialiteB", pourcentage = 1),
                                    AffiniteSpecialite(nomSpecialite = "specialiteC", pourcentage = 89),
                                ),
                        ),
                )
            given(
                suggestionHttpClient.recupererLesExplications(
                    profilEleve = profil,
                    idsFormations = listOf("fl0001"),
                ),
            ).willReturn(explications)

            // When
            val resultat = recupererExplicationsFormationService.recupererExplications(profilEleve = profil, idFormation = "fl0001")

            // Then
            val attendu =
                ExplicationsSuggestionDetaillees(
                    dureeEtudesPrevue = ChoixDureeEtudesPrevue.LONGUE,
                    alternance = ChoixAlternance.TRES_INTERESSE,
                    specialitesChoisies =
                        listOf(
                            AffiniteSpecialite(nomSpecialite = "specialiteA", pourcentage = 12),
                            AffiniteSpecialite(nomSpecialite = "specialiteB", pourcentage = 1),
                            AffiniteSpecialite(nomSpecialite = "specialiteC", pourcentage = 89),
                        ),
                )
            assertThat(resultat).usingRecursiveComparison().isEqualTo(attendu)
        }

        @Test
        fun `dans le cas par défaut, doit retourner par défaut`() {
            // Given
            given(
                suggestionHttpClient.recupererLesExplications(
                    profilEleve = profil,
                    idsFormations = listOf("fl0001"),
                ),
            ).willReturn(mapOf("fl0001" to ExplicationsSuggestion()))

            // When
            val resultat = recupererExplicationsFormationService.recupererExplications(profilEleve = profil, idFormation = "fl0001")

            // Then
            assertThat(resultat).usingRecursiveComparison().isEqualTo(ExplicationsSuggestionDetaillees())
        }

        @Test
        fun `doit trier et filtrer les explications géographiques`() {
            // Given
            val explications =
                mapOf(
                    "fl0001" to
                        ExplicationsSuggestion(
                            geographique =
                                listOf(
                                    ExplicationGeographique(
                                        ville = "Nantes",
                                        distanceKm = 10,
                                    ),
                                    ExplicationGeographique(
                                        ville = "Nantes",
                                        distanceKm = 85,
                                    ),
                                    ExplicationGeographique(
                                        ville = "Paris",
                                        distanceKm = 2,
                                    ),
                                    ExplicationGeographique(
                                        ville = "Paris",
                                        distanceKm = 1,
                                    ),
                                    ExplicationGeographique(
                                        ville = "Melun",
                                        distanceKm = 12,
                                    ),
                                ),
                        ),
                )
            given(
                suggestionHttpClient.recupererLesExplications(
                    profilEleve = profil,
                    idsFormations = listOf("fl0001"),
                ),
            ).willReturn(explications)

            // When
            val resultat = recupererExplicationsFormationService.recupererExplications(profilEleve = profil, idFormation = "fl0001")

            // Then
            val attendu =
                listOf(
                    ExplicationGeographique(
                        ville = "Paris",
                        distanceKm = 1,
                    ),
                    ExplicationGeographique(
                        ville = "Nantes",
                        distanceKm = 10,
                    ),
                    ExplicationGeographique(
                        ville = "Melun",
                        distanceKm = 12,
                    ),
                )
            assertThat(resultat.geographique).usingRecursiveComparison().isEqualTo(attendu)
        }

        @Test
        fun `si le baccalaureatRepository réussi, doit retourner l'explication de l'auto évaluation et du type de bac`() {
            // Given
            given(baccalaureatRepository.recupererUnBaccalaureatParIdExterne(idExterneBaccalaureat = "Général")).willReturn(bacGeneral)
            val explications =
                mapOf(
                    "fl0001" to
                        ExplicationsSuggestion(
                            autoEvaluationMoyenne =
                                AutoEvaluationMoyenne(
                                    moyenneAutoEvalue = 14.5f,
                                    rangs =
                                        ExplicationsSuggestion.RangsEchellons(
                                            rangEch25 = 12,
                                            rangEch50 = 14,
                                            rangEch75 = 16,
                                            rangEch10 = 10,
                                            rangEch90 = 17,
                                        ),
                                    baccalaureatUtilise = "Général",
                                ),
                            typeBaccalaureat =
                                TypeBaccalaureat(
                                    nomBaccalaureat = "Général",
                                    pourcentage = 18,
                                ),
                        ),
                )
            given(
                suggestionHttpClient.recupererLesExplications(
                    profilEleve = profil,
                    idsFormations = listOf("fl0001"),
                ),
            ).willReturn(explications)

            // When
            val resultat = recupererExplicationsFormationService.recupererExplications(profilEleve = profil, idFormation = "fl0001")

            // Then
            assertThat(resultat.explicationAutoEvaluationMoyenne).usingRecursiveComparison().isEqualTo(
                ExplicationAutoEvaluationMoyenne(
                    moyenneAutoEvalue = 14.5f,
                    hautIntervalleNotes = 8f,
                    basIntervalleNotes = 6f,
                    baccalaureatUtilise = bacGeneral,
                ),
            )
            assertThat(resultat.explicationTypeBaccalaureat).usingRecursiveComparison().isEqualTo(
                ExplicationTypeBaccalaureat(
                    baccalaureat = bacGeneral,
                    pourcentage = 18,
                ),
            )
        }

        @Test
        fun `si le baccalaureatRepository échoue, doit retourner l'explication de l'auto évaluation avec le nom renvoyer`() {
            // Given
            given(baccalaureatRepository.recupererUnBaccalaureatParIdExterne(idExterneBaccalaureat = "Général")).willReturn(null)
            val explications =
                mapOf(
                    "fl0001" to
                        ExplicationsSuggestion(
                            autoEvaluationMoyenne =
                                AutoEvaluationMoyenne(
                                    moyenneAutoEvalue = 14.5f,
                                    rangs =
                                        ExplicationsSuggestion.RangsEchellons(
                                            rangEch25 = 12,
                                            rangEch50 = 14,
                                            rangEch75 = 16,
                                            rangEch10 = 10,
                                            rangEch90 = 17,
                                        ),
                                    baccalaureatUtilise = "Général",
                                ),
                            typeBaccalaureat =
                                TypeBaccalaureat(
                                    nomBaccalaureat = "Général",
                                    pourcentage = 18,
                                ),
                        ),
                )
            given(
                suggestionHttpClient.recupererLesExplications(
                    profilEleve = profil,
                    idsFormations = listOf("fl0001"),
                ),
            ).willReturn(explications)

            // When
            val resultat = recupererExplicationsFormationService.recupererExplications(profilEleve = profil, idFormation = "fl0001")

            // Then
            assertThat(resultat.explicationAutoEvaluationMoyenne).usingRecursiveComparison().isEqualTo(
                ExplicationAutoEvaluationMoyenne(
                    moyenneAutoEvalue = 14.5f,
                    hautIntervalleNotes = 8f,
                    basIntervalleNotes = 6f,
                    baccalaureatUtilise = Baccalaureat(id = "Général", idExterne = "Général", nom = "Général"),
                ),
            )
            assertThat(resultat.explicationTypeBaccalaureat).usingRecursiveComparison().isEqualTo(
                ExplicationTypeBaccalaureat(
                    baccalaureat = Baccalaureat(id = "Général", idExterne = "Général", nom = "Général"),
                    pourcentage = 18,
                ),
            )
        }

        @Test
        fun `doit retourner les domaines et intérêts avec les interets filtrés`() {
            // Given
            val interetsEtDomainesChoisis =
                listOf(
                    "T_ROME_731379930",
                    "T_ROME_1573349427",
                    "T_ITM_1169",
                    "T_ROME_1959553899",
                )
            val explications = mapOf("fl0001" to ExplicationsSuggestion(interetsEtDomainesChoisis = interetsEtDomainesChoisis))
            given(
                suggestionHttpClient.recupererLesExplications(
                    profilEleve = profil,
                    idsFormations = listOf("fl0001"),
                ),
            ).willReturn(explications)
            val domaines = listOf(Domaine(id = "T_ITM_1169", nom = "défense nationale"))
            val interets =
                mapOf(
                    "T_ROME_1573349427" to InteretSousCategorie(id = "travail_manuel_creer", nom = "Créer quelque chose de mes mains"),
                    "T_ROME_731379930" to InteretSousCategorie(id = "aider_autres", nom = "Aider les autres"),
                    "T_ROME_1959553899" to InteretSousCategorie(id = "travail_manuel_creer", nom = "Créer quelque chose de mes mains"),
                )
            given(domaineRepository.recupererLesDomaines(interetsEtDomainesChoisis)).willReturn(domaines)
            given(interetRepository.recupererLesSousCategoriesDInterets(interetsEtDomainesChoisis)).willReturn(interets)

            // When
            val resultat = recupererExplicationsFormationService.recupererExplications(profilEleve = profil, idFormation = "fl0001")

            // Then
            assertThat(resultat.interets).usingRecursiveComparison().isEqualTo(
                listOf(
                    InteretSousCategorie(id = "travail_manuel_creer", nom = "Créer quelque chose de mes mains"),
                    InteretSousCategorie(id = "aider_autres", nom = "Aider les autres"),
                ),
            )
            assertThat(resultat.domaines).usingRecursiveComparison().isEqualTo(domaines)
        }

        @Test
        fun `doit retourner les formations similaires`() {
            // Given
            val explications = mapOf("fl0001" to ExplicationsSuggestion(formationsSimilaires = listOf("fl1", "fl7")))
            given(
                suggestionHttpClient.recupererLesExplications(
                    profilEleve = profil,
                    idsFormations = listOf("fl0001"),
                ),
            ).willReturn(explications)
            val formations =
                listOf(
                    Formation(id = "fl1", nom = "Classe préparatoire aux études supérieures - Cinéma audiovisuel"),
                    Formation(id = "fl7", nom = "Classe préparatoire aux études supérieures - Littéraire"),
                )
            given(formationRepository.recupererLesNomsDesFormations(listOf("fl1", "fl7"))).willReturn(formations)
            // When
            val resultat = recupererExplicationsFormationService.recupererExplications(profilEleve = profil, idFormation = "fl0001")

            // Then
            assertThat(resultat.formationsSimilaires).usingRecursiveComparison().isEqualTo(formations)
        }
    }

    @Nested
    inner class RecupererExplicationsPourDesFormations {
        @Test
        fun `dans le cas par défaut, doit retourner par défaut`() {
            // Given
            val explications =
                mapOf(
                    "fl0001" to ExplicationsSuggestion(),
                    "fl0002" to null,
                    "fl0003" to ExplicationsSuggestion(),
                )
            val idsFormations = listOf("fl0001", "fl0002", "fl0003")
            given(suggestionHttpClient.recupererLesExplications(profil, idsFormations)).willReturn(explications)

            // When
            val resultat = recupererExplicationsFormationService.recupererExplications(profil, idsFormations)

            // Then
            val attendu =
                mapOf(
                    "fl0001" to ExplicationsSuggestionDetaillees(),
                    "fl0002" to ExplicationsSuggestionDetaillees(),
                    "fl0003" to ExplicationsSuggestionDetaillees(),
                )
            assertThat(resultat).usingRecursiveComparison().isEqualTo(attendu)
        }

        @Test
        fun `dans le cas complexe, doit retourner toutes les données`() {
            // Given
            val explications =
                mapOf(
                    "fl0001" to
                        ExplicationsSuggestion(
                            dureeEtudesPrevue = ChoixDureeEtudesPrevue.LONGUE,
                            alternance = ChoixAlternance.TRES_INTERESSE,
                            specialitesChoisies =
                                listOf(
                                    AffiniteSpecialite(nomSpecialite = "specialiteA", pourcentage = 12),
                                    AffiniteSpecialite(nomSpecialite = "specialiteB", pourcentage = 1),
                                    AffiniteSpecialite(nomSpecialite = "specialiteC", pourcentage = 89),
                                ),
                        ),
                    "fl0002" to
                        ExplicationsSuggestion(
                            geographique =
                                listOf(
                                    ExplicationGeographique(
                                        ville = "Nantes",
                                        distanceKm = 10,
                                    ),
                                    ExplicationGeographique(
                                        ville = "Nantes",
                                        distanceKm = 85,
                                    ),
                                    ExplicationGeographique(
                                        ville = "Paris",
                                        distanceKm = 2,
                                    ),
                                    ExplicationGeographique(
                                        ville = "Paris",
                                        distanceKm = 1,
                                    ),
                                    ExplicationGeographique(
                                        ville = "Melun",
                                        distanceKm = 12,
                                    ),
                                ),
                            autoEvaluationMoyenne =
                                AutoEvaluationMoyenne(
                                    moyenneAutoEvalue = 14.5f,
                                    rangs =
                                        ExplicationsSuggestion.RangsEchellons(
                                            rangEch25 = 10,
                                            rangEch50 = 14,
                                            rangEch75 = 15,
                                            rangEch10 = 9,
                                            rangEch90 = 19,
                                        ),
                                    baccalaureatUtilise = "Général",
                                ),
                            typeBaccalaureat =
                                TypeBaccalaureat(
                                    nomBaccalaureat = "P",
                                    pourcentage = 30,
                                ),
                        ),
                    "fl0003" to
                        ExplicationsSuggestion(
                            autoEvaluationMoyenne =
                                AutoEvaluationMoyenne(
                                    moyenneAutoEvalue = 14.5f,
                                    rangs =
                                        ExplicationsSuggestion.RangsEchellons(
                                            rangEch25 = 12,
                                            rangEch50 = 14,
                                            rangEch75 = 16,
                                            rangEch10 = 10,
                                            rangEch90 = 17,
                                        ),
                                    baccalaureatUtilise = "STMG",
                                ),
                            typeBaccalaureat =
                                TypeBaccalaureat(
                                    nomBaccalaureat = "Général",
                                    pourcentage = 18,
                                ),
                        ),
                    "fl0004" to
                        ExplicationsSuggestion(
                            interetsEtDomainesChoisis = listOf("T_ROME_731379930", "T_ITM_1169", "T_ROME_1959553899", "T_IDEO2_4812"),
                            formationsSimilaires = listOf("fl12", "fl79"),
                        ),
                    "fl0005" to
                        ExplicationsSuggestion(
                            interetsEtDomainesChoisis = listOf("T_ITM_723", "T_ROME_1959553899"),
                            formationsSimilaires = listOf("fl1", "fl7", "fl12"),
                        ),
                )
            given(baccalaureatRepository.recupererDesBaccalaureatsParIdsExternes(listOf("Général", "STMG", "P"))).willReturn(
                listOf(bacGeneral, bacPro, bacSTMG),
            )
            val domainesEtInteretsDistincts = listOf("T_ROME_731379930", "T_ITM_1169", "T_ROME_1959553899", "T_IDEO2_4812", "T_ITM_723")
            given(domaineRepository.recupererLesDomaines(domainesEtInteretsDistincts)).willReturn(
                listOf(
                    Domaine(id = "T_ITM_1169", nom = "défense nationale"),
                    Domaine(id = "T_ITM_723", nom = "arts du spectacle"),
                ),
            )
            given(interetRepository.recupererLesSousCategoriesDInterets(domainesEtInteretsDistincts)).willReturn(
                mapOf(
                    "T_ROME_731379930" to InteretSousCategorie(id = "aider_autres", nom = "Aider les autres"),
                    "T_ROME_1959553899" to InteretSousCategorie(id = "travail_manuel_bricoler", nom = "Bricoler"),
                    "T_IDEO2_4812" to InteretSousCategorie(id = "aider_autres", nom = "Aider les autres"),
                ),
            )
            val formationsDistinctes = listOf("fl12", "fl79", "fl1", "fl7")
            given(formationRepository.recupererLesNomsDesFormations(formationsDistinctes)).willReturn(
                listOf(
                    Formation(id = "fl12", nom = "CS - Sommellerie - en apprentissage"),
                    Formation(id = "fl79", nom = "L1 - Gestion - en apprentissage"),
                    Formation(id = "fl1", nom = "L1 - Psychologie"),
                    Formation(id = "fl7", nom = "L1 - Philosophie"),
                ),
            )
            val idsFormations = listOf("fl0001", "fl0002", "fl0003", "fl0004", "fl0005")
            given(
                suggestionHttpClient.recupererLesExplications(
                    profilEleve = profil,
                    idsFormations = idsFormations,
                ),
            ).willReturn(explications)

            // When
            val resultat = recupererExplicationsFormationService.recupererExplications(profilEleve = profil, idsFormations = idsFormations)

            // Then
            val attendu =
                mapOf(
                    "fl0001" to
                        ExplicationsSuggestionDetaillees(
                            dureeEtudesPrevue = ChoixDureeEtudesPrevue.LONGUE,
                            alternance = ChoixAlternance.TRES_INTERESSE,
                            specialitesChoisies =
                                listOf(
                                    AffiniteSpecialite(nomSpecialite = "specialiteA", pourcentage = 12),
                                    AffiniteSpecialite(nomSpecialite = "specialiteB", pourcentage = 1),
                                    AffiniteSpecialite(nomSpecialite = "specialiteC", pourcentage = 89),
                                ),
                        ),
                    "fl0002" to
                        ExplicationsSuggestionDetaillees(
                            geographique =
                                listOf(
                                    ExplicationGeographique(
                                        ville = "Paris",
                                        distanceKm = 1,
                                    ),
                                    ExplicationGeographique(
                                        ville = "Nantes",
                                        distanceKm = 10,
                                    ),
                                    ExplicationGeographique(
                                        ville = "Melun",
                                        distanceKm = 12,
                                    ),
                                ),
                            explicationAutoEvaluationMoyenne =
                                ExplicationAutoEvaluationMoyenne(
                                    moyenneAutoEvalue = 14.5f,
                                    hautIntervalleNotes = 7.5f,
                                    basIntervalleNotes = 5f,
                                    baccalaureatUtilise = bacGeneral,
                                ),
                            explicationTypeBaccalaureat =
                                ExplicationTypeBaccalaureat(
                                    baccalaureat = bacPro,
                                    pourcentage = 30,
                                ),
                        ),
                    "fl0003" to
                        ExplicationsSuggestionDetaillees(
                            explicationAutoEvaluationMoyenne =
                                ExplicationAutoEvaluationMoyenne(
                                    moyenneAutoEvalue = 14.5f,
                                    hautIntervalleNotes = 8f,
                                    basIntervalleNotes = 6f,
                                    baccalaureatUtilise = Baccalaureat(id = "STMG", idExterne = "STMG", nom = "Série STMG"),
                                ),
                            explicationTypeBaccalaureat =
                                ExplicationTypeBaccalaureat(
                                    baccalaureat = bacGeneral,
                                    pourcentage = 18,
                                ),
                        ),
                    "fl0004" to
                        ExplicationsSuggestionDetaillees(
                            interets =
                                listOf(
                                    InteretSousCategorie(id = "aider_autres", nom = "Aider les autres"),
                                    InteretSousCategorie(id = "travail_manuel_bricoler", nom = "Bricoler"),
                                ),
                            domaines =
                                listOf(
                                    Domaine(id = "T_ITM_1169", nom = "défense nationale"),
                                ),
                            formationsSimilaires =
                                listOf(
                                    Formation(id = "fl12", nom = "CS - Sommellerie - en apprentissage"),
                                    Formation(id = "fl79", nom = "L1 - Gestion - en apprentissage"),
                                ),
                        ),
                    "fl0005" to
                        ExplicationsSuggestionDetaillees(
                            interets =
                                listOf(
                                    InteretSousCategorie(id = "travail_manuel_bricoler", nom = "Bricoler"),
                                ),
                            domaines =
                                listOf(
                                    Domaine(id = "T_ITM_723", nom = "arts du spectacle"),
                                ),
                            formationsSimilaires =
                                listOf(
                                    Formation(id = "fl1", nom = "L1 - Psychologie"),
                                    Formation(id = "fl7", nom = "L1 - Philosophie"),
                                    Formation(id = "fl12", nom = "CS - Sommellerie - en apprentissage"),
                                ),
                        ),
                )
            assertThat(resultat).usingRecursiveComparison().isEqualTo(attendu)
        }
    }
}
