package fr.gouv.monprojetsup.eleve.infrastructure.repository

import fr.gouv.monprojetsup.commun.clock.MonProjetSupClock
import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import fr.gouv.monprojetsup.eleve.domain.entity.ActionEleve
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.then
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class) // JUnit 5 extension for Mockito
class TraceRepositoryTest : BDDRepositoryTest() {
    @Autowired
    lateinit var traceJPARepository: TraceJPARepository

    @Mock
    lateinit var clock: MonProjetSupClock

    @Mock
    lateinit var logger: MonProjetSupLogger

    lateinit var traceBDDRepository: TraceBDDRepository

    @BeforeEach
    fun setup() {
        traceBDDRepository = TraceBDDRepository(traceJPARepository, logger, clock)
    }

    @Nested
    inner class AjouterTrace {
        @Test
        @Sql("classpath:trace.sql")
        fun `doit ajouter la trace en base`() {
            // Given
            val dateTimeActuelle = LocalDateTime.of(2024, 11, 15, 1, 2, 3)
            Mockito.`when`(clock.dateTimeActuelle()).thenReturn(dateTimeActuelle)

            // When
            traceBDDRepository.ajouterTrace("idEleve", ActionEleve.ONGLET_FICHE_FORMATION, "param1", "param2")

            // Then
            then(clock).should().dateTimeActuelle()
            then(logger).should().info("TRACE_ENREGISTREE", "Un élève a effectué une action")

            assertThat(traceBDDRepository.getTraces("12345")).isNotEmpty

            assertThat(traceBDDRepository.getTraces("idEleve")).isNotEmpty

            assertThat(traceBDDRepository.getTraces("autreEleve")).isEmpty()
        }
    }
}
