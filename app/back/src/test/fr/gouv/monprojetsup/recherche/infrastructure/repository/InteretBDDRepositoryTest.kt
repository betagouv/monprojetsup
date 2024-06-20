package fr.gouv.monprojetsup.recherche.infrastructure.repository

import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import fr.gouv.monprojetsup.recherche.domain.entity.Interet
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class InteretBDDRepositoryTest : BDDRepositoryTest() {
    @Autowired
    lateinit var interetBDDRepository: InteretBDDRepository

    @Test
    @Sql("classpath:interet.sql")
    fun `Doit retourner les domaines reconnus et ignorer ceux inconnus`() {
        // Given
        val ids = listOf("decouvrir_monde", "linguistique", "voyage", "multiculturel")

        // When
        val result = interetBDDRepository.recupererLesInterets(ids)

        // Then
        val attendu =
            listOf(
                Interet(id = "voyage", nom = "Voyager"),
                Interet(id = "linguistique", nom = "Apprendre de nouvelles langues"),
            )
        assertThat(result).isEqualTo(attendu)
    }

    @Test
    @Sql("classpath:interet.sql")
    fun `Si la liste est vide, doit retourner une liste vide`() {
        // Given
        val ids = emptyList<String>()

        // When
        val result = interetBDDRepository.recupererLesInterets(ids)

        // Then
        val attendu = emptyList<Interet>()
        assertThat(result).isEqualTo(attendu)
    }
}
