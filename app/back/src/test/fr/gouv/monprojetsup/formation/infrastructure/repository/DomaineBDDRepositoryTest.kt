package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import fr.gouv.monprojetsup.referentiel.domain.entity.CategorieDomaine
import fr.gouv.monprojetsup.referentiel.domain.entity.Domaine
import fr.gouv.monprojetsup.referentiel.infrastructure.repository.DomaineBDDRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class DomaineBDDRepositoryTest : BDDRepositoryTest() {
    @Autowired
    lateinit var domaineBDDRepository: DomaineBDDRepository

    @Nested
    inner class RecupererLesDomaines {
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
                    Domaine(id = "animaux", nom = "Soins aux animaux", emoji = "\uD83D\uDC2E"),
                    Domaine(id = "agroequipement", nom = "Agroéquipement", emoji = "\uD83D\uDE9C"),
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

    @Nested
    inner class RecupererTousLesDomainesEtLeursCategories {
        @Test
        @Sql("classpath:domaine.sql")
        fun `Doit retourner les domaines et leurs catégories`() {
            // When
            val result = domaineBDDRepository.recupererTousLesDomainesEtLeursCategories()

            // Then
            val attendu =
                mapOf(
                    CategorieDomaine(
                        id = "agriculture_alimentaire",
                        nom = "Agriculture et Alimentation",
                        emoji = "🥕",
                    ) to
                        listOf(
                            Domaine(id = "animaux", nom = "Soins aux animaux", emoji = "\uD83D\uDC2E"),
                            Domaine(id = "agroequipement", nom = "Agroéquipement", emoji = "\uD83D\uDE9C"),
                        ),
                    CategorieDomaine(
                        id = "commerce",
                        nom = "Commerce",
                        emoji = "\uD83C\uDFE2",
                    ) to emptyList(),
                )
            assertThat(result).isEqualTo(attendu)
        }
    }
}
