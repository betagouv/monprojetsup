package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil.FormationAvecSonAffinite
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.MockitoAnnotations

class CalculDuTauxDAffiniteBuilderTest {
    @InjectMocks
    lateinit var builder: CalculDuTauxDAffiniteBuilder

    private val formationAvecLeurAffinite =
        listOf(
            FormationAvecSonAffinite(idFormation = "fl240", tauxAffinite = 0.5449393f),
            FormationAvecSonAffinite(idFormation = "fr22", tauxAffinite = 0.7782054f),
            FormationAvecSonAffinite(idFormation = "fl2110", tauxAffinite = 0.3333385f),
            FormationAvecSonAffinite(idFormation = "fl252", tauxAffinite = 0.7150008f),
            FormationAvecSonAffinite(idFormation = "fl872", tauxAffinite = 0.4708512f),
            FormationAvecSonAffinite(idFormation = "fl2042", tauxAffinite = 0.4338807f),
            FormationAvecSonAffinite(idFormation = "fl2012", tauxAffinite = 0.4016404f),
            FormationAvecSonAffinite(idFormation = "fl680002", tauxAffinite = 0.9f),
            FormationAvecSonAffinite(idFormation = "fl659", tauxAffinite = 0.3776005f),
            FormationAvecSonAffinite(idFormation = "fl2013", tauxAffinite = 0.4189669f),
            FormationAvecSonAffinite(idFormation = "fl2060", tauxAffinite = 0.3869467f),
            FormationAvecSonAffinite(idFormation = "fl2091", tauxAffinite = 0.3541359f),
            FormationAvecSonAffinite(idFormation = "fl840012", tauxAffinite = 0.3296169f),
            FormationAvecSonAffinite(idFormation = "fl840011", tauxAffinite = 0.4197874f),
        )

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `si la valeur du taux d'affinité est strictement inférieur à x,xx5, doit l'arrondir à l'inférieur`() {
        // Given
        val idFormation = "fl240"

        // When
        val resultat = builder.calculDuTauxDAffinite(formationAvecLeurAffinite, idFormation)

        // Then
        assertThat(resultat).isEqualTo(54)
    }

    @Test
    fun `si la valeur du taux d'affinité est supérieur ou égale à x,xx5, doit l'arrondir au supérieur`() {
        // Given
        val idFormation = "fl252"

        // When
        val resultat = builder.calculDuTauxDAffinite(formationAvecLeurAffinite, idFormation)

        // Then
        assertThat(resultat).isEqualTo(72)
    }

    @Test
    fun `si la formation n'existe pas, doit retourner le taux d'affinite à 0`() {
        // Given
        val idFormation = "formationInconnue"

        // When
        val resultat = builder.calculDuTauxDAffinite(formationAvecLeurAffinite, idFormation)

        // Then
        assertThat(resultat).isEqualTo(0)
    }
}
