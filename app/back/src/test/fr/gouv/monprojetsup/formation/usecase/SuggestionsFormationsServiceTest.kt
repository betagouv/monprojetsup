package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil
import fr.gouv.monprojetsup.formation.domain.port.SuggestionHttpClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

class SuggestionsFormationsServiceTest {
    @Mock
    lateinit var suggestionHttpClient: SuggestionHttpClient

    @InjectMocks
    lateinit var suggestionsFormationsService: SuggestionsFormationsService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Nested
    inner class RecupererToutesLesSuggestionsPourUnProfil {
        @Test
        fun `doit appeler le client et renvoyer sa réponse`() {
            // Given
            val profilEleve = mock(ProfilEleve.Identifie::class.java)
            val suggestionsPourUnProfil = mock(SuggestionsPourUnProfil::class.java)
            given(suggestionHttpClient.recupererLesSuggestions(profilEleve)).willReturn(suggestionsPourUnProfil)

            // When
            val resultat = suggestionsFormationsService.recupererToutesLesSuggestionsPourUnProfil(profilEleve = profilEleve)

            // Then
            assertThat(resultat).isEqualTo(suggestionsPourUnProfil)
        }
    }
}
