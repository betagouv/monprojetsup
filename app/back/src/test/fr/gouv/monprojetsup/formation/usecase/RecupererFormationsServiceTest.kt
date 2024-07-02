package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.formation.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.formation.domain.entity.CritereAnalyseCandidature
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionDetaillees
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation
import fr.gouv.monprojetsup.formation.domain.entity.FormationDetaillee
import fr.gouv.monprojetsup.formation.domain.entity.Lien
import fr.gouv.monprojetsup.formation.domain.entity.MetierDetaille
import fr.gouv.monprojetsup.formation.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil.FormationAvecSonAffinite
import fr.gouv.monprojetsup.formation.domain.port.FormationRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

class RecupererFormationsServiceTest {
    @Mock
    lateinit var formationRepository: FormationRepository

    @Mock
    lateinit var recupererCommunesDUneFormationService: RecupererCommunesDUneFormationService

    @Mock
    lateinit var critereAnalyseCandidatureService: CritereAnalyseCandidatureService

    @Mock
    lateinit var recupererExplicationsFormationService: RecupererExplicationsFormationService

    @Mock
    lateinit var statistiquesDesAdmisPourFormationsService: StatistiquesDesAdmisPourFormationsService

    @Mock
    lateinit var metiersTriesParProfilBuilder: MetiersTriesParProfilBuilder

    @Mock
    lateinit var calculDuTauxDAffiniteBuilder: CalculDuTauxDAffiniteBuilder

    @InjectMocks
    lateinit var recupererFormationsService: RecupererFormationsService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `doit renvoyers les fiches formations pour un profil`() {
        // Given
        val profilEleve = mock(ProfilEleve::class.java)
        given(profilEleve.bac).willReturn("Général")
        given(profilEleve.classe).willReturn(ChoixNiveau.TERMINALE)
        given(profilEleve.communesPreferees).willReturn(listOf("Paris", "Lyon", "Strasbourg"))
        val formationAvecSonAffinite1 = mock(FormationAvecSonAffinite::class.java)
        val formationAvecSonAffinite2 = mock(FormationAvecSonAffinite::class.java)
        val formationAvecSonAffinite3 = mock(FormationAvecSonAffinite::class.java)
        val formationsAvecAffinites = listOf(formationAvecSonAffinite1, formationAvecSonAffinite2, formationAvecSonAffinite3)
        val affinitesFormationEtMetier = mock(SuggestionsPourUnProfil::class.java)
        val metier234 = mock(MetierDetaille::class.java)
        val metier123 = mock(MetierDetaille::class.java)
        val metier534 = mock(MetierDetaille::class.java)
        val metiersTriesParAffinites = listOf("MET_234", "MET_123", "MET_534")
        given(affinitesFormationEtMetier.formations).willReturn(formationsAvecAffinites)
        given(affinitesFormationEtMetier.metiersTriesParAffinites).willReturn(metiersTriesParAffinites)
        val idsFormations = listOf("fl0001", "fl0002", "fl0003")
        val lien1 = mock(Lien::class.java)
        val lien2 = mock(Lien::class.java)
        val formations =
            listOf(
                FormationDetaillee(
                    id = "fl0001",
                    nom = "L1 - Mathématique",
                    descriptifGeneral = "Descriptif general fl0001",
                    descriptifAttendus = "Descriptif attendus fl0001",
                    descriptifDiplome = "Descriptif diplome fl0001",
                    descriptifConseils = "Descriptif conseils fl0001",
                    formationsAssociees = listOf("fl0004", "fl0003"),
                    liens = listOf(lien1, lien2),
                    valeurCriteresAnalyseCandidature = listOf(0, 6, 14, 68, 12),
                    metiers = listOf(metier123, metier234),
                ),
                FormationDetaillee(
                    id = "fl0003",
                    nom = "CAP Patisserie",
                    descriptifGeneral = "Descriptif general fl0003",
                    descriptifAttendus = "Descriptif attendus fl0003",
                    descriptifDiplome = "Descriptif diplome fl0003",
                    descriptifConseils = "Descriptif conseils fl0003",
                    formationsAssociees = emptyList(),
                    liens = emptyList(),
                    valeurCriteresAnalyseCandidature = listOf(10, 0, 40, 20, 30),
                    metiers = listOf(metier534, metier123, metier234),
                ),
            )
        given(formationRepository.recupererLesFormationsAvecLeursMetiers(idsFormations)).willReturn(formations)
        val critereAnalyseCandidatureFL0001 = mock(CritereAnalyseCandidature::class.java)
        val critere2AnalyseCandidatureFL0001 = mock(CritereAnalyseCandidature::class.java)
        val critereAnalyseCandidatureFL0003 = mock(CritereAnalyseCandidature::class.java)
        val criteres =
            mapOf(
                "fl0001" to listOf(critereAnalyseCandidatureFL0001, critere2AnalyseCandidatureFL0001),
                "fl0003" to listOf(critereAnalyseCandidatureFL0003),
            )
        given(critereAnalyseCandidatureService.recupererCriteresAnalyseCandidature(formations)).willReturn(
            criteres,
        )
        val statistiqueDesAdmisFL0001 = mock(StatistiquesDesAdmis::class.java)
        val statistiqueDesAdmisFL0003 = mock(StatistiquesDesAdmis::class.java)
        val statistiquesDesAdmis =
            mapOf(
                "fl0001" to statistiqueDesAdmisFL0001,
                "fl0003" to statistiqueDesAdmisFL0003,
            )
        given(
            statistiquesDesAdmisPourFormationsService.recupererStatistiquesAdmisDeFormations(
                idBaccalaureat = "Général",
                idsFormations = listOf("fl0001", "fl0003"),
                classe = ChoixNiveau.TERMINALE,
            ),
        ).willReturn(statistiquesDesAdmis)
        val explicationsFL0001 = mock(ExplicationsSuggestionDetaillees::class.java)
        val explicationsFL0003 = mock(ExplicationsSuggestionDetaillees::class.java)
        val explications = mapOf("fl0001" to explicationsFL0001, "fl0003" to explicationsFL0003)
        given(recupererExplicationsFormationService.recupererExplications(profilEleve, listOf("fl0001", "fl0003"))).willReturn(explications)
        val communesParFormations =
            mapOf(
                "fl0001" to listOf("Paris", "Lyon", "Sartrouville"),
                "fl0003" to listOf("Strasbourg", "Houilles"),
            )
        given(
            recupererCommunesDUneFormationService.recupererNomCommunesTriesParAffinites(
                idsDesPremieresFormationsTriesParAffinites = listOf("fl0001", "fl0003"),
                communesFavorites = listOf("Paris", "Lyon", "Strasbourg"),
            ),
        ).willReturn(communesParFormations)
        given(
            calculDuTauxDAffiniteBuilder.calculDuTauxDAffinite(
                formationAvecLeurAffinite = formationsAvecAffinites,
                idFormation = "fl0001",
            ),
        ).willReturn(17)
        given(
            calculDuTauxDAffiniteBuilder.calculDuTauxDAffinite(
                formationAvecLeurAffinite = formationsAvecAffinites,
                idFormation = "fl0003",
            ),
        ).willReturn(87)
        given(
            metiersTriesParProfilBuilder.trierMetiersParAffinites(
                metiers = listOf(metier123, metier234),
                idsMetierTriesParAffinite = metiersTriesParAffinites,
            ),
        ).willReturn(listOf(metier234, metier123))
        given(
            metiersTriesParProfilBuilder.trierMetiersParAffinites(
                metiers = listOf(metier534, metier123, metier234),
                idsMetierTriesParAffinite = metiersTriesParAffinites,
            ),
        ).willReturn(listOf(metier234, metier123, metier534))

        // When
        val resultat = recupererFormationsService.recupererFichesFormationPourProfil(profilEleve, affinitesFormationEtMetier, idsFormations)

        // Then
        val ficheFormationFl0001 =
            FicheFormation.FicheFormationPourProfil(
                id = "fl0001",
                nom = "L1 - Mathématique",
                descriptifGeneral = "Descriptif general fl0001",
                descriptifAttendus = "Descriptif attendus fl0001",
                descriptifDiplome = "Descriptif diplome fl0001",
                descriptifConseils = "Descriptif conseils fl0001",
                formationsAssociees = listOf("fl0004", "fl0003"),
                liens = listOf(lien1, lien2),
                criteresAnalyseCandidature = listOf(critereAnalyseCandidatureFL0001, critere2AnalyseCandidatureFL0001),
                statistiquesDesAdmis = statistiqueDesAdmisFL0001,
                tauxAffinite = 17,
                metiersTriesParAffinites = listOf(metier234, metier123),
                communesTrieesParAffinites = listOf("Paris", "Lyon", "Sartrouville"),
                explications = explicationsFL0001,
            )
        val ficheFormationFl0003 =
            FicheFormation.FicheFormationPourProfil(
                id = "fl0003",
                nom = "CAP Patisserie",
                descriptifGeneral = "Descriptif general fl0003",
                descriptifAttendus = "Descriptif attendus fl0003",
                descriptifDiplome = "Descriptif diplome fl0003",
                descriptifConseils = "Descriptif conseils fl0003",
                formationsAssociees = emptyList(),
                liens = emptyList(),
                criteresAnalyseCandidature = listOf(critereAnalyseCandidatureFL0003),
                statistiquesDesAdmis = statistiqueDesAdmisFL0003,
                tauxAffinite = 87,
                metiersTriesParAffinites = listOf(metier234, metier123, metier534),
                communesTrieesParAffinites = listOf("Strasbourg", "Houilles"),
                explications = explicationsFL0003,
            )
        assertThat(resultat).usingRecursiveComparison().isEqualTo(listOf(ficheFormationFl0001, ficheFormationFl0003))
    }

    @Test
    fun `si la formation n'est pas renvoyé par les autres services, doit mettre des valeurs par défaut`() {
        // Given
        val profilEleve = mock(ProfilEleve::class.java)
        given(profilEleve.bac).willReturn("Général")
        given(profilEleve.classe).willReturn(ChoixNiveau.TERMINALE)
        given(profilEleve.communesPreferees).willReturn(listOf("Paris", "Lyon", "Strasbourg"))
        val formationAvecSonAffinite1 = mock(FormationAvecSonAffinite::class.java)
        val formationAvecSonAffinite2 = mock(FormationAvecSonAffinite::class.java)
        val formationAvecSonAffinite3 = mock(FormationAvecSonAffinite::class.java)
        val formationsAvecAffinites = listOf(formationAvecSonAffinite1, formationAvecSonAffinite2, formationAvecSonAffinite3)
        val affinitesFormationEtMetier = mock(SuggestionsPourUnProfil::class.java)
        val metier234 = mock(MetierDetaille::class.java)
        val metier123 = mock(MetierDetaille::class.java)
        val metier534 = mock(MetierDetaille::class.java)
        val metiersTriesParAffinites = listOf("MET_234", "MET_123", "MET_534")
        given(affinitesFormationEtMetier.formations).willReturn(formationsAvecAffinites)
        given(affinitesFormationEtMetier.metiersTriesParAffinites).willReturn(metiersTriesParAffinites)
        val idsFormations = listOf("fl0001", "fl0002", "fl0003")
        val lien1 = mock(Lien::class.java)
        val lien2 = mock(Lien::class.java)
        val formations =
            listOf(
                FormationDetaillee(
                    id = "fl0001",
                    nom = "L1 - Mathématique",
                    descriptifGeneral = "Descriptif general fl0001",
                    descriptifAttendus = "Descriptif attendus fl0001",
                    descriptifDiplome = "Descriptif diplome fl0001",
                    descriptifConseils = "Descriptif conseils fl0001",
                    formationsAssociees = listOf("fl0004", "fl0003"),
                    liens = listOf(lien1, lien2),
                    valeurCriteresAnalyseCandidature = listOf(0, 6, 14, 68, 12),
                    metiers = listOf(metier123, metier234),
                ),
                FormationDetaillee(
                    id = "fl0003",
                    nom = "CAP Patisserie",
                    descriptifGeneral = "Descriptif general fl0003",
                    descriptifAttendus = "Descriptif attendus fl0003",
                    descriptifDiplome = "Descriptif diplome fl0003",
                    descriptifConseils = "Descriptif conseils fl0003",
                    formationsAssociees = emptyList(),
                    liens = emptyList(),
                    valeurCriteresAnalyseCandidature = listOf(10, 0, 40, 20, 30),
                    metiers = listOf(metier534, metier123, metier234),
                ),
            )
        given(formationRepository.recupererLesFormationsAvecLeursMetiers(idsFormations)).willReturn(formations)
        val critereAnalyseCandidatureFL0001 = mock(CritereAnalyseCandidature::class.java)
        val critere2AnalyseCandidatureFL0001 = mock(CritereAnalyseCandidature::class.java)
        val criteres =
            mapOf(
                "fl0001" to listOf(critereAnalyseCandidatureFL0001, critere2AnalyseCandidatureFL0001),
            )
        given(critereAnalyseCandidatureService.recupererCriteresAnalyseCandidature(formations)).willReturn(
            criteres,
        )
        val statistiqueDesAdmisFL0001 = mock(StatistiquesDesAdmis::class.java)
        val statistiquesDesAdmis =
            mapOf(
                "fl0001" to statistiqueDesAdmisFL0001,
            )
        given(
            statistiquesDesAdmisPourFormationsService.recupererStatistiquesAdmisDeFormations(
                idBaccalaureat = "Général",
                idsFormations = listOf("fl0001", "fl0003"),
                classe = ChoixNiveau.TERMINALE,
            ),
        ).willReturn(statistiquesDesAdmis)
        val explicationsFL0001 = mock(ExplicationsSuggestionDetaillees::class.java)
        val explications = mapOf("fl0001" to explicationsFL0001)
        given(recupererExplicationsFormationService.recupererExplications(profilEleve, listOf("fl0001", "fl0003"))).willReturn(explications)
        val communesParFormations =
            mapOf(
                "fl0001" to listOf("Paris", "Lyon", "Sartrouville"),
            )
        given(
            recupererCommunesDUneFormationService.recupererNomCommunesTriesParAffinites(
                idsDesPremieresFormationsTriesParAffinites = listOf("fl0001", "fl0003"),
                communesFavorites = listOf("Paris", "Lyon", "Strasbourg"),
            ),
        ).willReturn(communesParFormations)
        given(
            calculDuTauxDAffiniteBuilder.calculDuTauxDAffinite(
                formationAvecLeurAffinite = formationsAvecAffinites,
                idFormation = "fl0001",
            ),
        ).willReturn(17)
        given(
            calculDuTauxDAffiniteBuilder.calculDuTauxDAffinite(
                formationAvecLeurAffinite = formationsAvecAffinites,
                idFormation = "fl0003",
            ),
        ).willReturn(0)
        given(
            metiersTriesParProfilBuilder.trierMetiersParAffinites(
                metiers = listOf(metier123, metier234),
                idsMetierTriesParAffinite = metiersTriesParAffinites,
            ),
        ).willReturn(listOf(metier234, metier123))
        given(
            metiersTriesParProfilBuilder.trierMetiersParAffinites(
                metiers = listOf(metier534, metier123, metier234),
                idsMetierTriesParAffinite = metiersTriesParAffinites,
            ),
        ).willReturn(listOf(metier234, metier123, metier534))

        // When
        val resultat = recupererFormationsService.recupererFichesFormationPourProfil(profilEleve, affinitesFormationEtMetier, idsFormations)

        // Then
        val ficheFormationFl0001 =
            FicheFormation.FicheFormationPourProfil(
                id = "fl0001",
                nom = "L1 - Mathématique",
                descriptifGeneral = "Descriptif general fl0001",
                descriptifAttendus = "Descriptif attendus fl0001",
                descriptifDiplome = "Descriptif diplome fl0001",
                descriptifConseils = "Descriptif conseils fl0001",
                formationsAssociees = listOf("fl0004", "fl0003"),
                liens = listOf(lien1, lien2),
                criteresAnalyseCandidature = listOf(critereAnalyseCandidatureFL0001, critere2AnalyseCandidatureFL0001),
                statistiquesDesAdmis = statistiqueDesAdmisFL0001,
                tauxAffinite = 17,
                metiersTriesParAffinites = listOf(metier234, metier123),
                communesTrieesParAffinites = listOf("Paris", "Lyon", "Sartrouville"),
                explications = explicationsFL0001,
            )
        val ficheFormationFl0003 =
            FicheFormation.FicheFormationPourProfil(
                id = "fl0003",
                nom = "CAP Patisserie",
                descriptifGeneral = "Descriptif general fl0003",
                descriptifAttendus = "Descriptif attendus fl0003",
                descriptifDiplome = "Descriptif diplome fl0003",
                descriptifConseils = "Descriptif conseils fl0003",
                formationsAssociees = emptyList(),
                liens = emptyList(),
                criteresAnalyseCandidature = emptyList(),
                statistiquesDesAdmis = null,
                tauxAffinite = 0,
                metiersTriesParAffinites = listOf(metier234, metier123, metier534),
                communesTrieesParAffinites = emptyList(),
                explications = null,
            )
        assertThat(resultat).usingRecursiveComparison().isEqualTo(listOf(ficheFormationFl0001, ficheFormationFl0003))
    }
}
