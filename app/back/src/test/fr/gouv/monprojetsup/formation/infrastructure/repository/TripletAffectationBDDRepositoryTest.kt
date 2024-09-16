package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import fr.gouv.monprojetsup.formation.domain.entity.TripletAffectation
import fr.gouv.monprojetsup.formation.entity.Communes.MONTREUIL
import fr.gouv.monprojetsup.formation.entity.Communes.NANCY
import fr.gouv.monprojetsup.formation.entity.Communes.PARIS19EME
import fr.gouv.monprojetsup.formation.entity.Communes.PARIS5EME
import fr.gouv.monprojetsup.formation.entity.Communes.RENNES
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class TripletAffectationBDDRepositoryTest : BDDRepositoryTest() {
    @Autowired
    lateinit var tripletAffectationJPARepository: TripletAffectationJPARepository

    lateinit var tripletAffectationBDDRepository: TripletAffectationBDDRepository

    @BeforeEach
    fun setup() {
        tripletAffectationBDDRepository = TripletAffectationBDDRepository(tripletAffectationJPARepository)
    }

    @Nested
    inner class RecupererTripletsAffectation {
        @Test
        @Sql("classpath:formation_triplet.sql")
        fun `Doit retourner les triplets affectation grouper pâr formation en ignorant les inconnus`() {
            // Given
            val idsFormations = listOf("ta0001", "ta0002", "tainconnu")

            // When
            val result = tripletAffectationBDDRepository.recupererTripletsAffectation(idsFormations)

            // Then
            assertThat(result).usingRecursiveAssertion().isEqualTo(
                mapOf(
                    "fl0001" to
                        listOf(
                            TripletAffectation(id = "ta0001", nom = "Lycée professionnel horticole de Montreuil", commune = MONTREUIL),
                        ),
                    "fl0003" to
                        listOf(
                            TripletAffectation(id = "ta0002", nom = "ENSAPLV", commune = PARIS19EME),
                        ),
                ),
            )
        }
    }

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
                            TripletAffectation(id = "ta0001", nom = "Lycée professionnel horticole de Montreuil", commune = MONTREUIL),
                        ),
                    "fl0004" to
                        listOf(
                            TripletAffectation(id = "ta0005", nom = "Université Paris 1 Panthéon-Sorbonne", commune = PARIS5EME),
                        ),
                    "fl0003" to
                        listOf(
                            TripletAffectation(id = "ta0002", nom = "ENSAPLV", commune = PARIS19EME),
                            TripletAffectation(id = "ta0003", nom = "ENSA Nancy", commune = NANCY),
                            TripletAffectation(id = "ta0004", nom = "ENSAB", commune = RENNES),
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
                    TripletAffectation(id = "ta0002", nom = "ENSAPLV", commune = PARIS19EME),
                    TripletAffectation(id = "ta0003", nom = "ENSA Nancy", commune = NANCY),
                    TripletAffectation(id = "ta0004", nom = "ENSAB", commune = RENNES),
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
