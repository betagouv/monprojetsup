package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import fr.gouv.monprojetsup.referentiel.domain.entity.Domaine
import fr.gouv.monprojetsup.referentiel.infrastructure.repository.DomaineBDDRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class DomaineBDDRepositoryTest : BDDRepositoryTest() {
    @Autowired
    lateinit var domaineBDDRepository: DomaineBDDRepository

    @Test
    @Sql("classpath:domaine.sql")
    fun `Doit retourner les domaines reconnus et ignorer ceux inconnus`() {
        // Given
        val ids = listOf("agroequipement", "agronomie", "agriculture_alimentaire", "animaux")

        // When
        val result = domaineBDDRepository.recupererLesDomaines(ids)

        // Then
        val attendu =
            listOf(
                Domaine(id = "animaux", nom = "Soins aux animaux"),
                Domaine(id = "agroequipement", nom = "Agroéquipement"),
            )
        assertThat(result).isEqualTo(attendu)
    }

    @Test
    @Sql("classpath:domaine.sql")
    fun `Si la liste est vide, doit retourner une liste vide`() {
        // Given
        val ids = emptyList<String>()

        // When
        val result = domaineBDDRepository.recupererLesDomaines(ids)

        // Then
        val attendu = emptyList<Domaine>()
        assertThat(result).isEqualTo(attendu)
    }
}
