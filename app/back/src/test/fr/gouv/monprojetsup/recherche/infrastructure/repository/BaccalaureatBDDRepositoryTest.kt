package fr.gouv.monprojetsup.recherche.infrastructure.repository

import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import fr.gouv.monprojetsup.recherche.domain.entity.Baccalaureat
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class BaccalaureatBDDRepositoryTest : BDDRepositoryTest() {
    @Autowired
    lateinit var baccalaureatBDDRepository: BaccalaureatBDDRepository

    @Nested
    inner class RecupererUnBaccalaureatParIdExterne {
        @Test
        @Sql("classpath:baccalaureat.sql")
        fun `Doit retourner le baccalaureat à partir de son id externe`() {
            // Given
            val idExterne = "P"

            // When
            val result = baccalaureatBDDRepository.recupererUnBaccalaureatParIdExterne(idExterne)

            // Then
            val attendu =
                Baccalaureat(
                    id = "Professionnelle",
                    nom = "Série Pro",
                    idExterne = "P",
                )
            assertThat(result).isEqualTo(attendu)
        }

        @Test
        @Sql("classpath:baccalaureat.sql")
        fun `Si l'id n'est pas présent, doit retourner null`() {
            // Given
            val idExterne = "Général"

            // When
            val result = baccalaureatBDDRepository.recupererUnBaccalaureatParIdExterne(idExterne)

            // Then
            assertThat(result).isNull()
        }
    }

    @Nested
    inner class RecupererUnBaccalaureat {
        @Test
        @Sql("classpath:baccalaureat.sql")
        fun `Doit retourner le baccalaureat à partir de son id`() {
            // Given
            val id = "Professionnelle"

            // When
            val result = baccalaureatBDDRepository.recupererUnBaccalaureat(id)

            // Then
            val attendu =
                Baccalaureat(
                    id = "Professionnelle",
                    nom = "Série Pro",
                    idExterne = "P",
                )
            assertThat(result).isEqualTo(attendu)
        }

        @Test
        @Sql("classpath:baccalaureat.sql")
        fun `Si l'id n'existe pas, doit retourner null`() {
            // Given
            val idExterne = "P"

            // When
            val result = baccalaureatBDDRepository.recupererUnBaccalaureat(idExterne)

            // Then
            assertThat(result).isNull()
        }
    }
}
