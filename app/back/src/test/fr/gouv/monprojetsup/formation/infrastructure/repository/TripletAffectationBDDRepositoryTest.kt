package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import fr.gouv.monprojetsup.formation.domain.entity.TripletAffectation
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class TripletAffectationBDDRepositoryTest : BDDRepositoryTest() {
    @Autowired
    lateinit var tripletAffectationBDDRepository: TripletAffectationBDDRepository

    @Nested
    inner class RecupererLesTripletsAffectationDeFormations {
        @Test
        @Sql("classpath:formation_triplet.sql")
        fun `Doit retourner les triplets affectation de formations`() {
            // Given
            val idsFormations = listOf("fl0001", "fl0004", "fl0003")

            // When
            val result = tripletAffectationBDDRepository.recupererLesTripletsAffectationDeFormations(idsFormations)

            // Then
            assertThat(result).usingRecursiveAssertion().isEqualTo(
                mapOf(
                    "fl0001" to
                        listOf(
                            TripletAffectation(id = "ta0001", commune = "Montreuil"),
                        ),
                    "fl0004" to
                        listOf(
                            TripletAffectation(id = "ta0005", commune = "Paris"),
                        ),
                    "fl0003" to
                        listOf(
                            TripletAffectation(id = "ta0002", commune = "Paris"),
                            TripletAffectation(id = "ta0003", commune = "Nancy"),
                            TripletAffectation(id = "ta0004", commune = "Rennes"),
                        ),
                ),
            )
        }

        @Test
        @Sql("classpath:formation_triplet.sql")
        fun `Si la formation n'a pas de triplets d'affectation, alors sa liste doit être vide`() {
            // Given
            val idsFormations = listOf("fl0002")

            // When
            val result = tripletAffectationBDDRepository.recupererLesTripletsAffectationDeFormations(idsFormations)

            // Then
            assertThat(result).usingRecursiveAssertion().isEqualTo(mapOf("fl0002" to emptyList<TripletAffectation>()))
        }

        @Test
        @Sql("classpath:formation_triplet.sql")
        fun `Si la liste est vide, alors doit retourner une map vide`() {
            // Given
            val idsFormations = emptyList<String>()

            // When
            val result = tripletAffectationBDDRepository.recupererLesTripletsAffectationDeFormations(idsFormations)

            // Then
            assertThat(result).usingRecursiveAssertion().isEqualTo(emptyMap<String, List<TripletAffectation>>())
        }
    }

    @Nested
    inner class RecupererLesTripletsAffectationDUneFormation {
        @Test
        @Sql("classpath:formation_triplet.sql")
        fun `Doit retourner les triplets affectation d'une formation`() {
            // Given
            val idFormation = "fl0003"

            // When
            val result = tripletAffectationBDDRepository.recupererLesTripletsAffectationDUneFormation(idFormation)

            // Then
            assertThat(result).usingRecursiveAssertion().isEqualTo(
                listOf(
                    TripletAffectation(id = "ta0002", commune = "Paris"),
                    TripletAffectation(id = "ta0003", commune = "Nancy"),
                    TripletAffectation(id = "ta0004", commune = "Rennes"),
                ),
            )
        }

        @Test
        @Sql("classpath:formation_triplet.sql")
        fun `Si la formation n'a pas de triplets d'affectation, alors doit retourner vide`() {
            // Given
            val idFormation = "fl0002"

            // When
            val result = tripletAffectationBDDRepository.recupererLesTripletsAffectationDUneFormation(idFormation)

            // Then
            assertThat(result).usingRecursiveAssertion().isEqualTo(emptyList<TripletAffectation>())
        }
    }
}
