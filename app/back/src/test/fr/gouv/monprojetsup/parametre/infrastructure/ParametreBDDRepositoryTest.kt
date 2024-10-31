package fr.gouv.monprojetsup.parametre.infrastructure

import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import fr.gouv.monprojetsup.parametre.domain.entity.Parametre
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired

class ParametreBDDRepositoryTest : BDDRepositoryTest() {
    @Mock
    lateinit var logger: MonProjetSupLogger

    @Autowired
    lateinit var parametreJPARepository: ParametreJPARepository

    lateinit var parametreBDDRepository: ParametreBDDRepository

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        parametreBDDRepository = ParametreBDDRepository(parametreJPARepository, logger)
    }

    @Test
    fun `Doit retourner la valeur du status`() {
        // Given
        val parametre = Parametre.ETL_EN_COURS

        // When
        val resultat = parametreBDDRepository.estActif(parametre)

        // Then
        assertThat(resultat).isFalse()
    }

    @Test
    fun `Si le paramètre n'existe pas, doit logguer une erreur et retourner false`() {
        // Given
        val parametre = mock(Parametre::class.java)
        given(parametre.name).willReturn("PARAMETRE_INCONNU")

        // When
        val resultat = parametreBDDRepository.estActif(parametre)

        // Then
        assertThat(resultat).isFalse()
        then(logger).should().error("PARAMETRE_MANQUANT", "Paramètre PARAMETRE_INCONNU non trouvé en base")
    }
}
