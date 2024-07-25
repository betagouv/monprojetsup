package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.eleve.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.formation.domain.entity.AffiniteSpecialite
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationGeographique
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionDetaillees
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionEtExemplesMetiers
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionEtExemplesMetiers.AutoEvaluationMoyenne
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionEtExemplesMetiers.TypeBaccalaureat
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation.FicheFormationPourProfil.ExplicationAutoEvaluationMoyenne
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation.FicheFormationPourProfil.ExplicationTypeBaccalaureat
import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import fr.gouv.monprojetsup.formation.domain.port.FormationRepository
import fr.gouv.monprojetsup.formation.domain.port.SuggestionHttpClient
import fr.gouv.monprojetsup.formation.entity.Communes
import fr.gouv.monprojetsup.metier.domain.entity.Metier
import fr.gouv.monprojetsup.metier.domain.port.MetierRepository
import fr.gouv.monprojetsup.referentiel.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.referentiel.domain.entity.Domaine
import fr.gouv.monprojetsup.referentiel.domain.entity.InteretSousCategorie
import fr.gouv.monprojetsup.referentiel.domain.entity.SituationAvanceeProjetSup
import fr.gouv.monprojetsup.referentiel.domain.port.BaccalaureatRepository
import fr.gouv.monprojetsup.referentiel.domain.port.DomaineRepository
import fr.gouv.monprojetsup.referentiel.domain.port.InteretRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.mock
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class RecupererExplicationsEtExemplesMetiersPourFormationServiceTest {
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

    @Mock
    lateinit var metierRepository: MetierRepository

    @InjectMocks
    lateinit var recupererExplicationsEtExemplesDeMetiersFormationService: RecupererExplicationsEtExemplesMetiersPourFormationService

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
            situation = SituationAvanceeProjetSup.PROJET_PRECIS,
            classe = ChoixNiveau.TERMINALE,
            baccalaureat = "Générale",
            dureeEtudesPrevue = ChoixDureeEtudesPrevue.INDIFFERENT,
            alternance = ChoixAlternance.PAS_INTERESSE,
            communesPreferees = listOf(Communes.CAEN),
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
                        ExplicationsSuggestionEtExemplesMetiers(
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
            val resultat =
                recupererExplicationsEtExemplesDeMetiersFormationService.recupererExplicationsEtExemplesDeMetiers(
                    profilEleve = profil,
                    idFormation = "fl0001",
                )

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
            assertThat(resultat.first).usingRecursiveComparison().isEqualTo(attendu)
        }

        @Test
        fun `dans le cas par défaut, doit retourner par défaut`() {
            // Given
            given(
                suggestionHttpClient.recupererLesExplications(
                    profilEleve = profil,
                    idsFormations = listOf("fl0001"),
                ),
            ).willReturn(mapOf("fl0001" to ExplicationsSuggestionEtExemplesMetiers()))

            // When
            val resultat =
                recupererExplicationsEtExemplesDeMetiersFormationService.recupererExplicationsEtExemplesDeMetiers(
                    profilEleve = profil,
                    idFormation = "fl0001",
                )

            // Then
            assertThat(resultat.first).usingRecursiveComparison().isEqualTo(ExplicationsSuggestionDetaillees())
        }

        @Test
        fun `doit trier et filtrer les explications géographiques`() {
            // Given
            val explications =
                mapOf(
                    "fl0001" to
                        ExplicationsSuggestionEtExemplesMetiers(
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
            val resultat =
                recupererExplicationsEtExemplesDeMetiersFormationService.recupererExplicationsEtExemplesDeMetiers(
                    profilEleve = profil,
                    idFormation = "fl0001",
                )

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
            assertThat(resultat.first.geographique).usingRecursiveComparison().isEqualTo(attendu)
        }

        @Test
        fun `si le baccalaureatRepository réussi, doit retourner l'explication de l'auto évaluation et du type de bac`() {
            // Given
            given(baccalaureatRepository.recupererUnBaccalaureatParIdExterne(idExterneBaccalaureat = "Général")).willReturn(bacGeneral)
            val explications =
                mapOf(
                    "fl0001" to
                        ExplicationsSuggestionEtExemplesMetiers(
                            autoEvaluationMoyenne =
                                AutoEvaluationMoyenne(
                                    moyenneAutoEvalue = 14.5f,
                                    rangs =
                                        ExplicationsSuggestionEtExemplesMetiers.RangsEchellons(
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
            val resultat =
                recupererExplicationsEtExemplesDeMetiersFormationService.recupererExplicationsEtExemplesDeMetiers(
                    profilEleve = profil,
                    idFormation = "fl0001",
                )

            // Then
            assertThat(resultat.first.explicationAutoEvaluationMoyenne).usingRecursiveComparison().isEqualTo(
                ExplicationAutoEvaluationMoyenne(
                    moyenneAutoEvalue = 14.5f,
                    hautIntervalleNotes = 8f,
                    basIntervalleNotes = 6f,
                    baccalaureatUtilise = bacGeneral,
                ),
            )
            assertThat(resultat.first.explicationTypeBaccalaureat).usingRecursiveComparison().isEqualTo(
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
                        ExplicationsSuggestionEtExemplesMetiers(
                            autoEvaluationMoyenne =
                                AutoEvaluationMoyenne(
                                    moyenneAutoEvalue = 14.5f,
                                    rangs =
                                        ExplicationsSuggestionEtExemplesMetiers.RangsEchellons(
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
            val resultat =
                recupererExplicationsEtExemplesDeMetiersFormationService.recupererExplicationsEtExemplesDeMetiers(
                    profilEleve = profil,
                    idFormation = "fl0001",
                )

            // Then
            assertThat(resultat.first.explicationAutoEvaluationMoyenne).usingRecursiveComparison().isEqualTo(
                ExplicationAutoEvaluationMoyenne(
                    moyenneAutoEvalue = 14.5f,
                    hautIntervalleNotes = 8f,
                    basIntervalleNotes = 6f,
                    baccalaureatUtilise = Baccalaureat(id = "Général", idExterne = "Général", nom = "Général"),
                ),
            )
            assertThat(resultat.first.explicationTypeBaccalaureat).usingRecursiveComparison().isEqualTo(
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
            val explications =
                mapOf("fl0001" to ExplicationsSuggestionEtExemplesMetiers(interetsEtDomainesChoisis = interetsEtDomainesChoisis))
            given(
                suggestionHttpClient.recupererLesExplications(
                    profilEleve = profil,
                    idsFormations = listOf("fl0001"),
                ),
            ).willReturn(explications)
            val domaines = listOf(Domaine(id = "T_ITM_1169", nom = "défense nationale", emoji = "\uD83D\uDEA8"))
            val interets =
                mapOf(
                    "T_ROME_1573349427" to
                        InteretSousCategorie(
                            id = "travail_manuel_creer",
                            nom = "Créer quelque chose de mes mains",
                            emoji = "\uD83E\uDE9B",
                        ),
                    "T_ROME_731379930" to InteretSousCategorie(id = "aider_autres", nom = "Aider les autres", emoji = "\uD83E\uDEC2"),
                    "T_ROME_1959553899" to
                        InteretSousCategorie(
                            id = "travail_manuel_creer",
                            nom = "Créer quelque chose de mes mains",
                            emoji = "\uD83E\uDE9B",
                        ),
                )
            given(domaineRepository.recupererLesDomaines(interetsEtDomainesChoisis)).willReturn(domaines)
            given(interetRepository.recupererLesSousCategoriesDInterets(interetsEtDomainesChoisis)).willReturn(interets)

            // When
            val resultat =
                recupererExplicationsEtExemplesDeMetiersFormationService.recupererExplicationsEtExemplesDeMetiers(
                    profilEleve = profil,
                    idFormation = "fl0001",
                )

            // Then
            assertThat(resultat.first.interets).usingRecursiveComparison().isEqualTo(
                listOf(
                    InteretSousCategorie(
                        id = "travail_manuel_creer",
                        nom = "Créer quelque chose de mes mains",
                        emoji = "\uD83E\uDE9B",
                    ),
                    InteretSousCategorie(id = "aider_autres", nom = "Aider les autres", emoji = "\uD83E\uDEC2"),
                ),
            )
            assertThat(resultat.first.domaines).usingRecursiveComparison().isEqualTo(domaines)
        }

        @Test
        fun `doit retourner les formations similaires`() {
            // Given
            val explications = mapOf("fl0001" to ExplicationsSuggestionEtExemplesMetiers(formationsSimilaires = listOf("fl1", "fl7")))
            given(
                suggestionHttpClient.recupererLesExplications(
                    profilEleve = profil,
                    idsFormations = listOf("fl0001"),
                ),
            ).willReturn(explications)
            val formationCourtes =
                listOf(
                    FormationCourte(id = "fl1", nom = "Classe préparatoire aux études supérieures - Cinéma audiovisuel"),
                    FormationCourte(id = "fl7", nom = "Classe préparatoire aux études supérieures - Littéraire"),
                )
            given(formationRepository.recupererLesNomsDesFormations(listOf("fl1", "fl7"))).willReturn(formationCourtes)
            // When
            val resultat =
                recupererExplicationsEtExemplesDeMetiersFormationService.recupererExplicationsEtExemplesDeMetiers(
                    profilEleve = profil,
                    idFormation = "fl0001",
                )

            // Then
            assertThat(resultat.first.formationsSimilaires).usingRecursiveComparison().isEqualTo(formationCourtes)
        }

        @Test
        fun `doit retourner les exemples de métiers`() {
            // Given
            val explications =
                mapOf(
                    "fl0001" to ExplicationsSuggestionEtExemplesMetiers(exemplesDeMetiers = listOf("MET_12", "MET_534", "MET_96")),
                )
            given(
                suggestionHttpClient.recupererLesExplications(
                    profilEleve = profil,
                    idsFormations = listOf("fl0001"),
                ),
            ).willReturn(explications)
            val metier12 = mock(Metier::class.java)
            val metier534 = mock(Metier::class.java)
            val metier96 = mock(Metier::class.java)
            val exemplesDeMetiers = listOf(metier12, metier534, metier96)
            given(metierRepository.recupererLesMetiersDetailles(listOf("MET_12", "MET_534", "MET_96"))).willReturn(exemplesDeMetiers)

            // When
            val resultat =
                recupererExplicationsEtExemplesDeMetiersFormationService.recupererExplicationsEtExemplesDeMetiers(
                    profilEleve = profil,
                    idFormation = "fl0001",
                )

            // Then
            assertThat(resultat.second).usingRecursiveComparison().isEqualTo(exemplesDeMetiers)
        }
    }

    @Nested
    inner class RecupererExplicationsPourDesFormations {
        @Test
        fun `dans le cas par défaut, doit retourner par défaut`() {
            // Given
            val explications =
                mapOf(
                    "fl0001" to ExplicationsSuggestionEtExemplesMetiers(),
                    "fl0002" to null,
                    "fl0003" to ExplicationsSuggestionEtExemplesMetiers(),
                )
            val idsFormations = listOf("fl0001", "fl0002", "fl0003")
            given(suggestionHttpClient.recupererLesExplications(profil, idsFormations)).willReturn(explications)

            // When
            val resultat =
                recupererExplicationsEtExemplesDeMetiersFormationService.recupererExplicationsEtExemplesDeMetiers(
                    profil,
                    idsFormations,
                )

            // Then
            val attendu =
                mapOf(
                    "fl0001" to Pair(ExplicationsSuggestionDetaillees(), emptyList<Metier>()),
                    "fl0002" to Pair(ExplicationsSuggestionDetaillees(), emptyList()),
                    "fl0003" to Pair(ExplicationsSuggestionDetaillees(), emptyList()),
                )
            assertThat(resultat).usingRecursiveComparison().isEqualTo(attendu)
        }

        @Test
        fun `dans le cas complexe, doit retourner toutes les données`() {
            // Given
            val explications =
                mapOf(
                    "fl0001" to
                        ExplicationsSuggestionEtExemplesMetiers(
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
                        ExplicationsSuggestionEtExemplesMetiers(
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
                                        ExplicationsSuggestionEtExemplesMetiers.RangsEchellons(
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
                        ExplicationsSuggestionEtExemplesMetiers(
                            autoEvaluationMoyenne =
                                AutoEvaluationMoyenne(
                                    moyenneAutoEvalue = 14.5f,
                                    rangs =
                                        ExplicationsSuggestionEtExemplesMetiers.RangsEchellons(
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
                        ExplicationsSuggestionEtExemplesMetiers(
                            interetsEtDomainesChoisis = listOf("T_ROME_731379930", "T_ITM_1169", "T_ROME_1959553899", "T_IDEO2_4812"),
                            formationsSimilaires = listOf("fl12", "fl79"),
                        ),
                    "fl0005" to
                        ExplicationsSuggestionEtExemplesMetiers(
                            interetsEtDomainesChoisis = listOf("T_ITM_723", "T_ROME_1959553899"),
                            formationsSimilaires = listOf("fl1", "fl7", "fl12"),
                        ),
                    "fl0006" to ExplicationsSuggestionEtExemplesMetiers(exemplesDeMetiers = listOf("MET_12", "MET_534", "MET_96")),
                )
            given(baccalaureatRepository.recupererDesBaccalaureatsParIdsExternes(listOf("Général", "STMG", "P"))).willReturn(
                listOf(bacGeneral, bacPro, bacSTMG),
            )
            val domainesEtInteretsDistincts = listOf("T_ROME_731379930", "T_ITM_1169", "T_ROME_1959553899", "T_IDEO2_4812", "T_ITM_723")
            given(domaineRepository.recupererLesDomaines(domainesEtInteretsDistincts)).willReturn(
                listOf(
                    Domaine(id = "T_ITM_1169", nom = "défense nationale", emoji = "\uD83D\uDEA8"),
                    Domaine(id = "T_ITM_723", nom = "arts du spectacle", emoji = "\uD83C\uDFAD"),
                ),
            )
            given(interetRepository.recupererLesSousCategoriesDInterets(domainesEtInteretsDistincts)).willReturn(
                mapOf(
                    "T_ROME_731379930" to InteretSousCategorie(id = "aider_autres", nom = "Aider les autres", emoji = "\uD83E\uDEC2"),
                    "T_ROME_1959553899" to InteretSousCategorie(id = "travail_manuel_bricoler", nom = "Bricoler", emoji = "\uD83D\uDE4C"),
                    "T_IDEO2_4812" to InteretSousCategorie(id = "aider_autres", nom = "Aider les autres", emoji = "\uD83E\uDEC2"),
                ),
            )
            val formationsDistinctes = listOf("fl12", "fl79", "fl1", "fl7")
            given(formationRepository.recupererLesNomsDesFormations(formationsDistinctes)).willReturn(
                listOf(
                    FormationCourte(id = "fl12", nom = "CS - Sommellerie - en apprentissage"),
                    FormationCourte(id = "fl79", nom = "L1 - Gestion - en apprentissage"),
                    FormationCourte(id = "fl1", nom = "L1 - Psychologie"),
                    FormationCourte(id = "fl7", nom = "L1 - Philosophie"),
                ),
            )
            val idsFormations = listOf("fl0001", "fl0002", "fl0003", "fl0004", "fl0005", "fl0006")
            val metier12 = mock(Metier::class.java)
            given(metier12.id).willReturn("MET_12")
            val metier534 = mock(Metier::class.java)
            given(metier534.id).willReturn("MET_534")
            val metier96 = mock(Metier::class.java)
            given(metier96.id).willReturn("MET_96")
            val exemplesDeMetiers = listOf(metier12, metier534, metier96)
            given(metierRepository.recupererLesMetiersDetailles(listOf("MET_12", "MET_534", "MET_96"))).willReturn(exemplesDeMetiers)
            given(
                suggestionHttpClient.recupererLesExplications(
                    profilEleve = profil,
                    idsFormations = idsFormations,
                ),
            ).willReturn(explications)

            // When
            val resultat =
                recupererExplicationsEtExemplesDeMetiersFormationService.recupererExplicationsEtExemplesDeMetiers(
                    profilEleve = profil,
                    idsFormations = idsFormations,
                )

            // Then
            val attendu =
                mapOf(
                    "fl0001" to
                        Pair(
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
                            emptyList(),
                        ),
                    "fl0002" to
                        Pair(
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
                            emptyList(),
                        ),
                    "fl0003" to
                        Pair(
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
                            emptyList(),
                        ),
                    "fl0004" to
                        Pair(
                            ExplicationsSuggestionDetaillees(
                                interets =
                                    listOf(
                                        InteretSousCategorie(id = "aider_autres", nom = "Aider les autres", emoji = "\uD83E\uDEC2"),
                                        InteretSousCategorie(id = "travail_manuel_bricoler", nom = "Bricoler", emoji = "\uD83D\uDE4C"),
                                    ),
                                domaines =
                                    listOf(
                                        Domaine(id = "T_ITM_1169", nom = "défense nationale", emoji = "\uD83D\uDEA8"),
                                    ),
                                formationsSimilaires =
                                    listOf(
                                        FormationCourte(id = "fl12", nom = "CS - Sommellerie - en apprentissage"),
                                        FormationCourte(id = "fl79", nom = "L1 - Gestion - en apprentissage"),
                                    ),
                            ),
                            emptyList(),
                        ),
                    "fl0005" to
                        Pair(
                            ExplicationsSuggestionDetaillees(
                                interets =
                                    listOf(
                                        InteretSousCategorie(id = "travail_manuel_bricoler", nom = "Bricoler", emoji = "\uD83D\uDE4C"),
                                    ),
                                domaines =
                                    listOf(
                                        Domaine(id = "T_ITM_723", nom = "arts du spectacle", emoji = "\uD83C\uDFAD"),
                                    ),
                                formationsSimilaires =
                                    listOf(
                                        FormationCourte(id = "fl1", nom = "L1 - Psychologie"),
                                        FormationCourte(id = "fl7", nom = "L1 - Philosophie"),
                                        FormationCourte(id = "fl12", nom = "CS - Sommellerie - en apprentissage"),
                                    ),
                            ),
                            emptyList(),
                        ),
                    "fl0006" to Pair(ExplicationsSuggestionDetaillees(), exemplesDeMetiers),
                )
            assertThat(resultat).usingRecursiveComparison().isEqualTo(attendu)
        }
    }
}
