package fr.gouv.monprojetsup.eleve.infrastructure.repository

import fr.gouv.monprojetsup.commun.clock.MonProjetSupClock
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mock
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import java.time.LocalDate
import kotlin.jvm.optionals.getOrNull

class CompteParcoursupBDDRepositoryTest : BDDRepositoryTest() {
    @Autowired
    lateinit var compteParcoursupJPARepository: CompteParcoursupJPARepository

    @Mock
    lateinit var clock: MonProjetSupClock

    @Mock
    lateinit var logger: Logger

    lateinit var compteParcoursupBDDRepository: CompteParcoursupBDDRepository

    @BeforeEach
    fun setup() {
        compteParcoursupBDDRepository = CompteParcoursupBDDRepository(compteParcoursupJPARepository, logger, clock)
    }

    @Nested
    inner class EnregistrerIdCompteParcoursup {
        @Test
        @Sql("classpath:comptes_parcoursup.sql")
        fun `si enregistre un compte PS pour un élève n'ayant pas déjà associé son compte, doit réussir`() {
            // Given
            val dateActuelle = LocalDate.of(2024, 11, 15)
            given(clock.dateActuelle()).willReturn(dateActuelle)
            val idEleve = "129f6d9c-0f6f-4fa4-8107-75b7cb129889"
            val idParcoursup = 1223456

            // When
            compteParcoursupBDDRepository.enregistrerIdCompteParcoursup(idEleve, idParcoursup)

            // Then
            val resultat = compteParcoursupJPARepository.findById(idEleve)
            assertThat(resultat.getOrNull()?.idParcoursup).isEqualTo(idParcoursup)
            assertThat(resultat.getOrNull()?.dateMiseAJour).isEqualTo(dateActuelle)
        }

        @Test
        @Sql("classpath:comptes_parcoursup.sql")
        fun `si enregistre un compte PS pour un élève ayant déjà associé son compte, doit réussir à mettre à jour`() {
            // Given
            val dateActuelle = LocalDate.of(2024, 11, 15)
            given(clock.dateActuelle()).willReturn(dateActuelle)
            val idEleve = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15"
            val idParcoursup = 1223456

            // When
            compteParcoursupBDDRepository.enregistrerIdCompteParcoursup(idEleve, idParcoursup)

            // Then
            val resultat = compteParcoursupJPARepository.findById(idEleve)
            assertThat(resultat.getOrNull()?.idParcoursup).isEqualTo(idParcoursup)
            assertThat(resultat.getOrNull()?.dateMiseAJour).isEqualTo(dateActuelle)
        }

        @Test
        @Sql("classpath:comptes_parcoursup.sql")
        fun `si enregistre un compte PS pour un élève sans profil, doit logguer une erreur et throw MonProjetSupNotFoundException`() {
            // Given
            val dateActuelle = LocalDate.of(2024, 11, 15)
            given(clock.dateActuelle()).willReturn(dateActuelle)
            val idEleve = "aefcaf9f-826a-4952-8731-02a624b0f6d0"
            val idParcoursup = 122345

            // When & Then
            val message =
                "L'élève avec l'id aefcaf9f-826a-4952-8731-02a624b0f6d0 tente de sauvegarder son id parcoursup " +
                    "mais il n'est pas encore sauvegardé en BDD"
            assertThatThrownBy {
                compteParcoursupBDDRepository.enregistrerIdCompteParcoursup(idEleve, idParcoursup)
            }.isInstanceOf(MonProjetSupNotFoundException::class.java).hasMessage(message)

            val exception =
                MonProjetSupNotFoundException(
                    code = "ELEVE_NOT_FOUND",
                    msg = message,
                )
            then(logger).should().error(message, exception)
        }
    }

    @Nested
    inner class RecupererIdCompteParcoursup {
        @Test
        @Sql("classpath:comptes_parcoursup.sql")
        fun `Doit retourner l'id parcoursup`() {
            // Given
            val idEleve = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15"

            // When
            val result = compteParcoursupBDDRepository.recupererIdCompteParcoursup(idEleve)

            // Then
            assertThat(result).isEqualTo(12345)
        }

        @Test
        @Sql("classpath:comptes_parcoursup.sql")
        fun `Si le compte n'est pas relié, doit retourner null`() {
            // Given
            val idEleve = "129f6d9c-0f6f-4fa4-8107-75b7cb129889"

            // When
            val result = compteParcoursupBDDRepository.recupererIdCompteParcoursup(idEleve)

            // Then
            assertThat(result).isNull()
        }

        @Test
        @Sql("classpath:comptes_parcoursup.sql")
        fun `Si le compte n'existe pas, doit retourner null`() {
            // Given
            val idEleve = "de14aa82-13dc-4eed-82ae-77d038e271e2"

            // When
            val result = compteParcoursupBDDRepository.recupererIdCompteParcoursup(idEleve)

            // Then
            assertThat(result).isNull()
        }
    }
}
