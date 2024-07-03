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
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis.RepartitionAdmis
import fr.gouv.monprojetsup.formation.domain.port.BaccalaureatRepository
import fr.gouv.monprojetsup.formation.domain.port.DomaineRepository
import fr.gouv.monprojetsup.formation.domain.port.FormationRepository
import fr.gouv.monprojetsup.formation.domain.port.InteretRepository
import fr.gouv.monprojetsup.formation.domain.port.SuggestionHttpClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
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

    @Mock
    lateinit var moyenneGeneraleDesAdmisService: StatistiquesDesAdmisService

    @InjectMocks
    lateinit var recupererExplicationsFormationService: RecupererExplicationsFormationService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

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

    @Test
    fun `dans le cas par défaut, doit retourner par défaut`() {
        // Given
        val explications =
            ExplicationsSuggestion(
                dureeEtudesPrevue = ChoixDureeEtudesPrevue.LONGUE,
                alternance = ChoixAlternance.TRES_INTERESSE,
                specialitesChoisies =
                    listOf(
                        AffiniteSpecialite(nomSpecialite = "specialiteA", pourcentage = 12),
                        AffiniteSpecialite(nomSpecialite = "specialiteB", pourcentage = 1),
                        AffiniteSpecialite(nomSpecialite = "specialiteC", pourcentage = 89),
                    ),
            )
        given(
            suggestionHttpClient.recupererLesExplications(
                profilEleve = profil,
                idFormation = "fl0001",
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
    fun `doit retourner les explications dureeEtudesPrevue, alternance, specialitesChoisies et moyenneGeneraleDesAdmis`() {
        // Given
        given(
            moyenneGeneraleDesAdmisService.recupererStatistiquesAdmisDUneFormation(
                idBaccalaureat = "Générale",
                idFormation = "fl0001",
                classe = ChoixNiveau.TERMINALE,
            ),
        ).willReturn(
            StatistiquesDesAdmis(
                repartitionAdmis =
                    RepartitionAdmis(
                        total = 0,
                        parBaccalaureat =
                            listOf(),
                    ),
                moyenneGeneraleDesAdmis = null,
            ),
        )
        given(
            suggestionHttpClient.recupererLesExplications(
                profilEleve = profil,
                idFormation = "fl0001",
            ),
        ).willReturn(ExplicationsSuggestion())

        // When
        val resultat = recupererExplicationsFormationService.recupererExplications(profilEleve = profil, idFormation = "fl0001")

        // Then
        assertThat(resultat).usingRecursiveComparison().isEqualTo(ExplicationsSuggestionDetaillees())
    }

    @Test
    fun `doit trier et filtrer les explications géographiques`() {
        // Given
        val explications =
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
            )
        given(
            suggestionHttpClient.recupererLesExplications(
                profilEleve = profil,
                idFormation = "fl0001",
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
        given(baccalaureatRepository.recupererUnBaccalaureatParIdExterne("Général")).willReturn(
            Baccalaureat(id = "Générale", idExterne = "Général", nom = "Série Générale"),
        )
        val explications =
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
            )
        given(
            suggestionHttpClient.recupererLesExplications(
                profilEleve = profil,
                idFormation = "fl0001",
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
                baccalaureatUtilise = Baccalaureat(id = "Générale", idExterne = "Général", nom = "Série Générale"),
            ),
        )
        assertThat(resultat.explicationTypeBaccalaureat).usingRecursiveComparison().isEqualTo(
            ExplicationTypeBaccalaureat(
                baccalaureat = Baccalaureat(id = "Générale", idExterne = "Général", nom = "Série Générale"),
                pourcentage = 18,
            ),
        )
    }

    @Test
    fun `si le baccalaureatRepository échoue, doit retourner l'explication de l'auto évaluation avec le nom renvoyer`() {
        // Given
        given(baccalaureatRepository.recupererUnBaccalaureatParIdExterne("Général")).willReturn(null)
        val explications =
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
            )
        given(
            suggestionHttpClient.recupererLesExplications(
                profilEleve = profil,
                idFormation = "fl0001",
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
    fun `doit retourner les domaines et interets`() {
        // Given
        val interetsEtDomainesChoisis =
            listOf(
                "T_ROME_731379930",
                "T_ITM_1169",
                "T_ROME_1959553899",
            )
        val explications = ExplicationsSuggestion(interetsEtDomainesChoisis = interetsEtDomainesChoisis)
        given(
            suggestionHttpClient.recupererLesExplications(
                profilEleve = profil,
                idFormation = "fl0001",
            ),
        ).willReturn(explications)
        val domaines = listOf(Domaine(id = "T_ITM_1169", nom = "défense nationale"))
        val interets =
            listOf(
                InteretSousCategorie(id = "aider_autres", nom = "Aider les autres"),
                InteretSousCategorie(id = "creer", nom = "Créer quelque chose de mes mains"),
            )
        given(domaineRepository.recupererLesDomaines(interetsEtDomainesChoisis)).willReturn(domaines)
        given(interetRepository.recupererLesSousCategoriesDInterets(interetsEtDomainesChoisis)).willReturn(interets)

        // When
        val resultat = recupererExplicationsFormationService.recupererExplications(profilEleve = profil, idFormation = "fl0001")

        // Then
        assertThat(resultat.interets).usingRecursiveComparison().isEqualTo(interets)
        assertThat(resultat.domaines).usingRecursiveComparison().isEqualTo(domaines)
    }

    @Test
    fun `doit retourner les formations similaires`() {
        // Given
        val explications = ExplicationsSuggestion(formationsSimilaires = listOf("fl1", "fl7"))
        given(
            suggestionHttpClient.recupererLesExplications(
                profilEleve = profil,
                idFormation = "fl0001",
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
