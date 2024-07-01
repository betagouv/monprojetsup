package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import fr.gouv.monprojetsup.formation.domain.entity.InteretSousCategorie
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class InteretBDDRepositoryTest : BDDRepositoryTest() {
    @Autowired
    lateinit var interetBDDRepository: InteretBDDRepository

    @Test
    @Sql("classpath:interet.sql")
    fun `Doit retourner les interêts reconnus et ignorer ceux inconnus`() {
        // Given
        val ids =
            listOf(
                "decouvrir_monde",
                "linguistique",
                "voyage",
                "multiculturel",
                "T_ROME_1825212206",
                "T_ROME_934089965",
                "T_ROME_326548351",
            )

        // When
        val result = interetBDDRepository.recupererLesSousCategoriesDInterets(ids)

        // Then
        val attendu =
            listOf(
                InteretSousCategorie(id = "voyage", nom = "Voyager"),
                InteretSousCategorie(id = "linguistique", nom = "Apprendre de nouvelles langues"),
            )
        assertThat(result).isEqualTo(attendu)
    }

    @Test
    @Sql("classpath:interet.sql")
    fun `Si la liste est vide, doit retourner une liste vide`() {
        // Given
        val ids = emptyList<String>()

        // When
        val result = interetBDDRepository.recupererLesSousCategoriesDInterets(ids)

        // Then
        val attendu = emptyList<InteretSousCategorie>()
        assertThat(result).isEqualTo(attendu)
    }
}
