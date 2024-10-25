package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import fr.gouv.monprojetsup.metier.domain.entity.Metier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

class MetiersTriesParProfilBuilderTest {
    @Mock
    lateinit var logger: MonProjetSupLogger

    @InjectMocks
    lateinit var builder: MetiersTriesParProfilBuilder

    private val metierDetaille1 = mock(Metier::class.java).apply { given(id).willReturn("MET_1") }
    private val metierDetaille2 = mock(Metier::class.java).apply { given(id).willReturn("MET_2") }
    private val metierDetaille3 = mock(Metier::class.java).apply { given(id).willReturn("MET_3") }
    private val metierDetaille4 = mock(Metier::class.java).apply { given(id).willReturn("MET_4") }
    private val metierDetaille5 = mock(Metier::class.java).apply { given(id).willReturn("MET_5") }
    private val metierDetaille6 = mock(Metier::class.java).apply { given(id).willReturn("MET_6") }
    private val metierDetaille7 = mock(Metier::class.java).apply { given(id).willReturn("MET_7") }

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `doit trier les métiers dans le meme ordre que la liste des métiers triés`() {
        // Given
        val metiers =
            listOf(
                metierDetaille1,
                metierDetaille2,
                metierDetaille3,
                metierDetaille4,
                metierDetaille5,
                metierDetaille6,
                metierDetaille7,
            )
        val idsMetierTriesParAffinite =
            listOf(
                "MET_5",
                "MET_2",
                "MET_4",
                "MET_3",
                "MET_9",
                "MET_1",
                "MET_7",
                "MET_6",
                "MET_8",
                "MET_10",
            )

        // When
        val resultat =
            builder.trierMetiersParAffinites(
                metiers,
                idsMetierTriesParAffinite,
            )

        // Then
        val attendu =
            listOf(
                metierDetaille5,
                metierDetaille2,
                metierDetaille4,
                metierDetaille3,
                metierDetaille1,
                metierDetaille7,
                metierDetaille6,
            )
        assertThat(resultat).isEqualTo(attendu)
    }

    @Test
    fun `si un métier n'est pas présent dans la liste des métier trié, doit le mettre à la fin de la liste`() {
        // Given
        val metiers =
            listOf(
                metierDetaille1,
                metierDetaille2,
                metierDetaille3,
                metierDetaille4,
                metierDetaille5,
                metierDetaille6,
                metierDetaille7,
            )
        val idsMetierTriesParAffinite =
            listOf(
                "MET_5",
                "MET_2",
                "MET_3",
                "MET_9",
                "MET_1",
                "MET_6",
                "MET_8",
                "MET_10",
            )

        // When
        val resultat =
            builder.trierMetiersParAffinites(
                metiers,
                idsMetierTriesParAffinite,
            )

        // Then
        val attendu =
            listOf(
                metierDetaille5,
                metierDetaille2,
                metierDetaille3,
                metierDetaille1,
                metierDetaille6,
                metierDetaille4,
                metierDetaille7,
            )
        assertThat(resultat).isEqualTo(attendu)
        then(logger).should().error(
            type = "METIER_NON_REMONTE_PAR_API_SUGGESTION",
            message = "Le metier MET_4 n'est pas retourné dans la liste des métiers triés par affinité par l'API",
            parametres = mapOf("metierAbsent" to "MET_4"),
        )
        then(logger).should().error(
            type = "METIER_NON_REMONTE_PAR_API_SUGGESTION",
            message = "Le metier MET_7 n'est pas retourné dans la liste des métiers triés par affinité par l'API",
            parametres = mapOf("metierAbsent" to "MET_7"),
        )
        then(logger).shouldHaveNoMoreInteractions()
    }
}
