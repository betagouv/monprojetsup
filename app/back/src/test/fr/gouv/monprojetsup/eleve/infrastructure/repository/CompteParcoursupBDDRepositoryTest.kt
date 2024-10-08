package fr.gouv.monprojetsup.eleve.infrastructure.repository

import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import java.util.UUID

class CompteParcoursupBDDRepositoryTest : BDDRepositoryTest() {
    @Autowired
    lateinit var compteParcoursupJPARepository: CompteParcoursupJPARepository

    lateinit var compteParcoursupBDDRepository: CompteParcoursupBDDRepository

    @BeforeEach
    fun setup() {
        compteParcoursupBDDRepository = CompteParcoursupBDDRepository(compteParcoursupJPARepository)
    }

    @Nested
    inner class RecupererIdCompteParcoursup {
        @Test
        @Sql("classpath:comptes_parcoursup.sql")
        fun `Doit retourner l'id parcoursup`() {
            // Given
            val idEleve = UUID.fromString("0f88ddd1-62ef-436e-ad3f-cf56d5d14c15")

            // When
            val result = compteParcoursupBDDRepository.recupererIdCompteParcoursup(idEleve)

            // Then
            assertThat(result).isEqualTo(12345)
        }

        @Test
        @Sql("classpath:comptes_parcoursup.sql")
        fun `Si le compte n'est pas relié, doit retourner null`() {
            // Given
            val idEleve = UUID.fromString("129f6d9c-0f6f-4fa4-8107-75b7cb129889")

            // When
            val result = compteParcoursupBDDRepository.recupererIdCompteParcoursup(idEleve)

            // Then
            assertThat(result).isNull()
        }

        @Test
        @Sql("classpath:comptes_parcoursup.sql")
        fun `Si le compte n'existe pas, doit retourner null`() {
            // Given
            val idEleve = UUID.fromString("de14aa82-13dc-4eed-82ae-77d038e271e2")

            // When
            val result = compteParcoursupBDDRepository.recupererIdCompteParcoursup(idEleve)

            // Then
            assertThat(result).isNull()
        }
    }
}
