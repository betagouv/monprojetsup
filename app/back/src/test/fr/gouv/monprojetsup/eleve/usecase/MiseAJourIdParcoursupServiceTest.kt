package fr.gouv.monprojetsup.eleve.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.eleve.domain.port.CompteParcoursupRepository
import fr.gouv.monprojetsup.parcoursup.infrastructure.client.ParcoursupApiHttpClient
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.mock
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class MiseAJourIdParcoursupServiceTest {
    @Mock
    lateinit var compteParcoursupRepository: CompteParcoursupRepository

    @Mock
    lateinit var parcoursupApiHttpClient: ParcoursupApiHttpClient

    @InjectMocks
    lateinit var miseAJourIdParcoursupService: MiseAJourIdParcoursupService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `doit appeler l'api parcoursup pour récupérer l'id et l'enregistre en base`() {
        // Given
        val jwt = "eyliefjkjffekjefgvFEZelf"
        val idParcoursup = 123456
        given(parcoursupApiHttpClient.recupererIdParcoursupEleve(jwt)).willReturn(idParcoursup)
        val idProfil = "81d69058-4f08-4d53-a7ec-36175931ea84"
        val profil = mock(ProfilEleve.Identifie::class.java)
        given(profil.id).willReturn(idProfil)

        // When
        miseAJourIdParcoursupService.mettreAJourIdParcoursup(profil, jwt)

        // Then
        then(compteParcoursupRepository).should().enregistrerIdCompteParcoursup(idProfil, idParcoursup)
    }
}
