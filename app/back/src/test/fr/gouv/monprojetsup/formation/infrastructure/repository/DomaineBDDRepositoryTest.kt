package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import fr.gouv.monprojetsup.referentiel.domain.entity.CategorieDomaine
import fr.gouv.monprojetsup.referentiel.domain.entity.Domaine
import fr.gouv.monprojetsup.referentiel.infrastructure.repository.CategorieDomaineJPARepository
import fr.gouv.monprojetsup.referentiel.infrastructure.repository.DomaineBDDRepository
import fr.gouv.monprojetsup.referentiel.infrastructure.repository.DomaineJPARepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class DomaineBDDRepositoryTest : BDDRepositoryTest() {
    @Autowired
    lateinit var domaineJPARepository: DomaineJPARepository

    @Autowired
    lateinit var categorieDomaineJPARepository: CategorieDomaineJPARepository

    lateinit var domaineBDDRepository: DomaineBDDRepository

    @BeforeEach
    fun setup() {
        domaineBDDRepository = DomaineBDDRepository(domaineJPARepository, categorieDomaineJPARepository)
    }

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
                    Domaine(
                        id = "animaux",
                        nom = "Soins aux animaux",
                        emoji = "\uD83D\uDC2E",
                        description =
                            "Pour travailler dans les élevages ou la pêche, mais aussi apprendre à soigner les animaux, " +
                                "les nourrir et assurer leur bien-être.",
                    ),
                    Domaine(id = "agroequipement", nom = "Agroéquipement", emoji = "\uD83D\uDE9C", description = null),
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
                            Domaine(
                                id = "animaux",
                                nom = "Soins aux animaux",
                                emoji = "\uD83D\uDC2E",
                                description =
                                    "Pour travailler dans les élevages ou la pêche, mais aussi apprendre à soigner " +
                                        "les animaux, les nourrir et assurer leur bien-être.",
                            ),
                            Domaine(id = "agroequipement", nom = "Agroéquipement", emoji = "\uD83D\uDE9C", description = null),
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

    @Nested
    inner class RecupererIdsDomainesInexistants {
        @Test
        @Sql("classpath:domaine.sql")
        fun `si toutes les domaines existent, renvoyer liste vide`() {
            // Given
            val ids = listOf("agroequipement", "animaux")

            // When
            val result = domaineBDDRepository.recupererIdsDomainesInexistants(ids)

            // Then
            assertThat(result).isEqualTo(emptyList<String>())
        }

        @Test
        @Sql("classpath:domaine.sql")
        fun `si un des domaines n'existe pas, renvoyer la liste de ces domaines`() {
            // Given
            val ids = listOf("agroequipement", "agriculture_alimentaire", "animaux")

            // When
            val result = domaineBDDRepository.recupererIdsDomainesInexistants(ids)

            // Then
            assertThat(result).isEqualTo(listOf("agriculture_alimentaire"))
        }
    }
}
