package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import fr.gouv.monprojetsup.referentiel.domain.entity.InteretSousCategorie
import fr.gouv.monprojetsup.referentiel.infrastructure.repository.InteretBDDRepository
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
            mapOf(
                "T_ROME_326548351" to InteretSousCategorie(id = "voyage", nom = "Voyager"),
                "T_ROME_934089965" to InteretSousCategorie(id = "voyage", nom = "Voyager"),
                "T_ROME_1825212206" to InteretSousCategorie(id = "linguistique", nom = "Apprendre de nouvelles langues"),
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
        val attendu = emptyMap<String, InteretSousCategorie>()
        assertThat(result).isEqualTo(attendu)
    }
}
