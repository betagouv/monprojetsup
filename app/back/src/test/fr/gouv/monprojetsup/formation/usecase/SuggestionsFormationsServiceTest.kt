package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation
import fr.gouv.monprojetsup.formation.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil.FormationAvecSonAffinite
import fr.gouv.monprojetsup.formation.domain.port.SuggestionHttpClient
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.never
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

class SuggestionsFormationsServiceTest {
    @Mock
    lateinit var suggestionHttpClient: SuggestionHttpClient

    @Mock
    lateinit var recupererFormationsService: RecupererFormationsService

    @InjectMocks
    lateinit var suggestionsFormationsService: SuggestionsFormationsService

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

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    private val affinitesFormationEtMetier =
        SuggestionsPourUnProfil(
            metiersTriesParAffinites = metiersTriesParAffinites,
            formations = formations,
        )

    @Test
    fun `quand les 5 premieres formations, alors doit appeler le service avec formations classées par ordre d'affinité du profil`() {
        // Given
        val profilEleve = mock(ProfilEleve::class.java)
        given(suggestionHttpClient.recupererLesSuggestions(profilEleve)).willReturn(affinitesFormationEtMetier)

        val idsFormationsTriesParAffinite =
            listOf(
                "fl52",
                "fl41",
                "fr83",
                "fl2073",
                "fl252",
            )

        // When
        suggestionsFormationsService.suggererFormations(profilEleve = profilEleve, deLIndex = 0, aLIndex = 5)

        // Then
        then(recupererFormationsService).should().recupererFichesFormationPourProfil(
            profilEleve = profilEleve,
            affinitesFormationEtMetier = affinitesFormationEtMetier,
            idsFormations = idsFormationsTriesParAffinite,
        )
    }

    @Test
    fun `quand 10 formations sont demandés, alors doit appeler le service avec formations classées par ordre d'affinité du profil`() {
        // Given
        val profilEleve = mock(ProfilEleve::class.java)
        given(suggestionHttpClient.recupererLesSuggestions(profilEleve)).willReturn(affinitesFormationEtMetier)

        val idsFormationsTriesParAffinite =
            listOf(
                "fl680003",
                "fl2022",
                "fl2010",
                "fl2018",
                "fl840010",
                "fl240",
                "fl2034",
                "fl2019",
                "fl2051",
                "fl2110",
            )

        // When
        suggestionsFormationsService.suggererFormations(profilEleve = profilEleve, deLIndex = 8, aLIndex = 18)

        // Then
        then(recupererFormationsService).should().recupererFichesFormationPourProfil(
            profilEleve = profilEleve,
            affinitesFormationEtMetier = affinitesFormationEtMetier,
            idsFormations = idsFormationsTriesParAffinite,
        )
    }

    @Test
    fun `quand plus de formations demandés qu'il y en a, doit appeler le service avec toutes les formations classées par affinité`() {
        // Given
        val profilEleve = mock(ProfilEleve::class.java)
        given(suggestionHttpClient.recupererLesSuggestions(profilEleve)).willReturn(affinitesFormationEtMetier)

        val idsFormationsTriesParAffinite =
            listOf(
                "fl52",
                "fl41",
                "fr83",
                "fl2073",
                "fl252",
                "fr22",
                "fl2016",
                "fl2118",
                "fl680003",
                "fl2022",
                "fl2010",
                "fl2018",
                "fl840010",
                "fl240",
                "fl2034",
                "fl2019",
                "fl2051",
                "fl2110",
                "fl2044",
                "fl2009",
                "fl2090",
                "fl2046",
            )

        // When
        suggestionsFormationsService.suggererFormations(profilEleve = profilEleve, deLIndex = 0, aLIndex = 30)

        // Then
        then(recupererFormationsService).should().recupererFichesFormationPourProfil(
            profilEleve = profilEleve,
            affinitesFormationEtMetier = affinitesFormationEtMetier,
            idsFormations = idsFormationsTriesParAffinite,
        )
    }

    @Test
    fun `quand les index des formations demandés sont en dehors de la liste, renvoyer liste vide`() {
        // Given
        val profilEleve = mock(ProfilEleve::class.java)
        given(suggestionHttpClient.recupererLesSuggestions(profilEleve)).willReturn(affinitesFormationEtMetier)

        // When
        val resultat = suggestionsFormationsService.suggererFormations(profilEleve = profilEleve, deLIndex = 50, aLIndex = 60)

        // Then
        assertThat(resultat).isEqualTo(emptyList<FicheFormation.FicheFormationPourProfil>())
        then(recupererFormationsService).should(never()).recupererFichesFormationPourProfil(
            profilEleve = profilEleve,
            affinitesFormationEtMetier = affinitesFormationEtMetier,
            idsFormations = emptyList(),
        )
    }

    @Test
    fun `quand l'API suggestion nous retourne des listes vides, alors on doit les retourner une liste vide`() {
        // Given
        val profilEleve = mock(ProfilEleve::class.java)
        val affinitesFormationEtMetierVides =
            SuggestionsPourUnProfil(
                metiersTriesParAffinites = emptyList(),
                formations = emptyList(),
            )
        given(suggestionHttpClient.recupererLesSuggestions(profilEleve)).willReturn(affinitesFormationEtMetierVides)

        // When
        val resultat = suggestionsFormationsService.suggererFormations(profilEleve = profilEleve, deLIndex = 0, aLIndex = 5)

        // Then
        assertThat(resultat).isEqualTo(emptyList<FicheFormation.FicheFormationPourProfil>())
        then(recupererFormationsService).should(never()).recupererFichesFormationPourProfil(
            profilEleve = profilEleve,
            affinitesFormationEtMetier = affinitesFormationEtMetierVides,
            idsFormations = emptyList(),
        )
    }

    @Test
    fun `quand l'index est négatif, alors doit throw une erreur`() {
        val profilEleve = mock(ProfilEleve::class.java)
        given(suggestionHttpClient.recupererLesSuggestions(profilEleve)).willReturn(affinitesFormationEtMetier)

        // When & Then
        assertThatThrownBy {
            suggestionsFormationsService.suggererFormations(profilEleve = profilEleve, deLIndex = -3, aLIndex = 5)
        }.isInstanceOf(IndexOutOfBoundsException::class.java)
    }

    @Test
    fun `quand les indexs sont inversés, alors on doit throw une exception`() {
        // Given
        val profilEleve = mock(ProfilEleve::class.java)
        given(suggestionHttpClient.recupererLesSuggestions(profilEleve)).willReturn(affinitesFormationEtMetier)

        // When & Then
        assertThatThrownBy {
            suggestionsFormationsService.suggererFormations(profilEleve = profilEleve, deLIndex = 5, aLIndex = 0)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }
}
