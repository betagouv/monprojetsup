package fr.gouv.monprojetsup.eleve.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.clock.MonProjetSupClock
import fr.gouv.monprojetsup.eleve.domain.entity.ActionEleve
import fr.gouv.monprojetsup.eleve.domain.entity.Trace
import fr.gouv.monprojetsup.eleve.domain.port.TraceRepository
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.mock
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.time.LocalDate

class AjoutTraceServiceTest {
    @Mock
    lateinit var traceRepository: TraceRepository

    @Mock
    lateinit var clock: MonProjetSupClock

    @Mock
    lateinit var logger: MonProjetSupLogger

    @InjectMocks
    lateinit var ajoutTraceService: AjoutTraceService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `doit ajouter la trace en base`() {
        // Given
        val dateActuelle = LocalDate.of(2024, 11, 15)
        given(clock.dateActuelle()).willReturn(dateActuelle)

        val idProfil = "81d69058-4f08-4d53-a7ec-36175931ea84"
        val profil = mock(ProfilEleve.AvecProfilExistant::class.java)
        given(profil.id).willReturn(idProfil)

        // When
        ajoutTraceService.ajouterTrace(idProfil, Trace(ActionEleve.AJOUT_FAVORI_METIER, "param1", "param2"))

        // Then
        then(traceRepository).should().ajouterTrace(idProfil, ActionEleve.AJOUT_FAVORI_METIER, "param1", "param2")
    }
}
