package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import fr.gouv.monprojetsup.referentiel.domain.entity.Specialite
import fr.gouv.monprojetsup.referentiel.infrastructure.repository.SpecialiteBDDRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class SpecialiteBDDRepositoryTest : BDDRepositoryTest() {
    @Autowired
    lateinit var specialiteBDDRepository: SpecialiteBDDRepository

    @Test
    @Sql("classpath:specialite.sql")
    fun `Doit retourner les spécialites reconnues et ignorer celles inconnues`() {
        // Given
        val ids = listOf("4", "18", "1040", "5")

        // When
        val result = specialiteBDDRepository.recupererLesSpecialites(ids)

        // Then
        val attendu =
            listOf(
                Specialite(id = "4", label = "Sciences de l'ingénieur"),
                Specialite(id = "5", label = "Biologie/Ecologie"),
                Specialite(id = "1040", label = "Physique-Chimie et Mathématiques"),
            )
        assertThat(result).isEqualTo(attendu)
    }

    @Test
    @Sql("classpath:specialite.sql")
    fun `Si la liste est vide, doit retourner une liste vide`() {
        // Given
        val ids = emptyList<String>()

        // When
        val result = specialiteBDDRepository.recupererLesSpecialites(ids)

        // Then
        val attendu = emptyList<Specialite>()
        assertThat(result).isEqualTo(attendu)
    }
}
