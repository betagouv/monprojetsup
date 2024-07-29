package fr.gouv.monprojetsup.referentiel.infrastructure.repository

import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import fr.gouv.monprojetsup.referentiel.domain.entity.Baccalaureat
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class BaccalaureatBDDRepositoryTest : BDDRepositoryTest() {
    @Autowired
    lateinit var baccalaureatJPARepository: BaccalaureatJPARepository

    lateinit var baccalaureatBDDRepository: BaccalaureatBDDRepository

    @BeforeEach
    fun setup() {
        baccalaureatBDDRepository = BaccalaureatBDDRepository(baccalaureatJPARepository)
    }

    private val bacPro =
        Baccalaureat(
            id = "Professionnel",
            nom = "Série Pro",
            idExterne = "P",
        )

    private val bacGeneral =
        Baccalaureat(
            id = "Général",
            nom = "Série Générale",
            idExterne = "Générale",
        )

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
            assertThat(result).isEqualTo(bacPro)
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
    inner class RecupererDesBaccalaureatsParIdsExternes {
        @Test
        @Sql("classpath:baccalaureat.sql")
        fun `Doit retourner les baccalaureat à partir de leurs ids en ignorant les inconnus`() {
            // Given
            val idsExternes = listOf("P", "inconnu", "Générale", "Professionnel")

            // When
            val result = baccalaureatBDDRepository.recupererDesBaccalaureatsParIdsExternes(idsExternes)

            // Then
            val attendu = listOf(bacGeneral, bacPro)
            assertThat(result).isEqualTo(attendu)
        }
    }
}
