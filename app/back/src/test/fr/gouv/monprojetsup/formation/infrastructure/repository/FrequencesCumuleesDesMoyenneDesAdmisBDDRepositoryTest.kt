package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import fr.gouv.monprojetsup.referentiel.domain.entity.Baccalaureat
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class FrequencesCumuleesDesMoyenneDesAdmisBDDRepositoryTest : BDDRepositoryTest() {
    @Autowired
    lateinit var entityManager: EntityManager

    lateinit var moyenneGeneraleAdmisBDDRepository: FrequencesCumuleesDesMoyenneDesAdmisBDDRepository

    @BeforeEach
    fun setup() {
        moyenneGeneraleAdmisBDDRepository =
            FrequencesCumuleesDesMoyenneDesAdmisBDDRepository(entityManager)
    }

    @Nested
    inner class RecupererFrequencesCumuleesParBacs {
        @Test
        @Sql("classpath:moyenne_generale_admis.sql")
        fun `Doit retourner les fréquences cumulées pour tous les baccalaureats sauf NC`() {
            // When
            val result = moyenneGeneraleAdmisBDDRepository.recupererFrequencesCumuleesParBacs("2024")

            // Then
            val attendu =
                mapOf(
                    baccalaureatGeneral to
                        listOf(
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            1,
                            1,
                            1,
                            1,
                            1,
                            1,
                            1,
                            1,
                            1,
                            1,
                            1,
                            4,
                            8,
                            11,
                            26,
                            45,
                            75,
                            135,
                            219,
                            328,
                            436,
                            539,
                            620,
                            668,
                            707,
                            733,
                            744,
                            752,
                            754,
                            754,
                            754,
                            754,
                        ),
                    baccalaureatPro to
                        listOf(
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            1,
                            2,
                            3,
                            5,
                            5,
                            6,
                            6,
                            9,
                            11,
                            11,
                            11,
                            12,
                            12,
                            12,
                            12,
                            12,
                            12,
                            12,
                            12,
                            12,
                            12,
                            12,
                        ),
                    baccalaureatST2S to
                        listOf(
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            1,
                            1,
                            1,
                            3,
                            3,
                            6,
                            7,
                            9,
                            10,
                            10,
                            11,
                            15,
                            18,
                            21,
                            23,
                            23,
                            23,
                            23,
                            23,
                            24,
                            24,
                            24,
                            24,
                            24,
                        ),
                    baccalaureatSTAV to fl0001STAV2024,
                    baccalaureatSTI2D to fl0003STI2D2024,
                    baccalaureatSTL to
                        listOf(
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            1,
                            2,
                            2,
                            2,
                            2,
                            2,
                            3,
                            4,
                            4,
                            4,
                            4,
                            5,
                            5,
                            5,
                            5,
                            5,
                            5,
                            5,
                            5,
                        ),
                    baccalaureatSTMG to
                        listOf(
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            1,
                            3,
                            5,
                            6,
                            7,
                            7,
                            7,
                            9,
                            9,
                            9,
                            9,
                            9,
                            9,
                            9,
                            9,
                            9,
                            9,
                            9,
                        ),
                )
            assertThat(result).isEqualTo(attendu)
        }

        @Test
        @Sql("classpath:moyenne_generale_admis.sql")
        fun `si aucune donnée n'est trouvée pour l'année donnée, doit retourner map vide`() {
            // When
            val result = moyenneGeneraleAdmisBDDRepository.recupererFrequencesCumuleesParBacs("2022")

            // Then
            assertThat(result).isEqualTo(emptyMap<Baccalaureat, List<Int>>())
        }
    }

    @Nested
    inner class RecupererFrequencesCumuleesDeTousLesBacsParIdFormation {
        @Test
        @Sql("classpath:moyenne_generale_admis.sql")
        fun `Doit retourner les fréquences cumulées pour tous les baccalaureats sauf NC`() {
            // Given
            val idFormation = "fl0001"

            // When
            val result = moyenneGeneraleAdmisBDDRepository.recupererFrequencesCumuleesDeTousLesBacs(idFormation, "2024")

            // Then
            val attendu =
                mapOf(
                    baccalaureatGeneral to fl0001Generale2024,
                    baccalaureatPro to fl0001P2024,
                    baccalaureatST2S to fl0001ST2S2024,
                    baccalaureatSTAV to fl0001STAV2024,
                    baccalaureatSTL to fl0001STL2024,
                    baccalaureatSTMG to fl0001STMG2024,
                )
            assertThat(result).isEqualTo(attendu)
        }

        @Test
        @Sql("classpath:moyenne_generale_admis.sql")
        fun `si la formation n'est pas trouvée, doit retourner map vide`() {
            // Given
            val idFormation = "fl0004"

            // When
            val result = moyenneGeneraleAdmisBDDRepository.recupererFrequencesCumuleesDeTousLesBacs(idFormation, "2023")

            // Then
            assertThat(result).isEqualTo(emptyMap<Baccalaureat, List<Int>>())
        }

        @Test
        @Sql("classpath:moyenne_generale_admis.sql")
        fun `si aucune donnée n'est trouvée pour l'année donnée, doit retourner map vide`() {
            // Given
            val idFormation = "fl0002"

            // When
            val result = moyenneGeneraleAdmisBDDRepository.recupererFrequencesCumuleesDeTousLesBacs(idFormation, "2024")

            // Then
            assertThat(result).isEqualTo(emptyMap<Baccalaureat, List<Int>>())
        }
    }

    @Nested
    inner class RecupererFrequencesCumuleesDeToutLesBacsPourPlusieursFormations {
        @Test
        @Sql("classpath:moyenne_generale_admis.sql")
        fun `Doit retourner les fréquences cumulées de tous les baccalaureats sauf NC pour toutes les formations reconnues`() {
            // Given
            val idFormation = listOf("fl0001", "fl0003", "fl0002")

            // When
            val result =
                moyenneGeneraleAdmisBDDRepository.recupererFrequencesCumuleesDeTousLesBacs(
                    idsFormations = idFormation,
                    annee = "2023",
                )

            // Then
            val attendu =
                mapOf(
                    "fl0001" to emptyMap(),
                    "fl0003" to emptyMap(),
                    "fl0002" to
                        mapOf(
                            baccalaureatGeneral to fl0002General2023,
                            baccalaureatSTMG to fl0002STMG2023,
                            baccalaureatPro to fl0002P2023,
                        ),
                )
            assertThat(result).isEqualTo(attendu)
        }
    }

    companion object {
        private val baccalaureatGeneral =
            Baccalaureat(id = "Générale", nom = "Bac Général", idExterne = "Générale", idCarteParcoursup = "1")
        private val baccalaureatNC = Baccalaureat(id = "NC", nom = "Non-communiqué", idExterne = "NC", idCarteParcoursup = "0")
        private val baccalaureatPro = Baccalaureat(id = "P", nom = "Bac Professionnel", idExterne = "P", idCarteParcoursup = "3")
        private val baccalaureatST2S = Baccalaureat(id = "ST2S", nom = "Bac ST2S", idExterne = "ST2S", idCarteParcoursup = "2")
        private val baccalaureatSTAV = Baccalaureat(id = "STAV", nom = "Bac STAV", idExterne = "STAV", idCarteParcoursup = "2")
        private val baccalaureatSTI2D = Baccalaureat(id = "STI2D", nom = "Bac STI2D", idExterne = "STI2D", idCarteParcoursup = "2")
        private val baccalaureatSTL = Baccalaureat(id = "STL", nom = "Bac STL", idExterne = "STL", idCarteParcoursup = "2")
        private val baccalaureatSTMG = Baccalaureat(id = "STMG", nom = "Bac STMG", idExterne = "STMG", idCarteParcoursup = "2")

        private val fl0002NC2023 =
            listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 4, 5, 6, 8, 8, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9)
        private val fl0002General2023 =
            listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 4, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6)
        private val fl0002STMG2023 =
            listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
        private val fl0002P2023 =
            listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2)

        private val fl0001ST2S2024 =
            listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 3, 3, 5, 6, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8)
        private val fl0001STAV2024 =
            listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
        private val fl0001NC2024 =
            listOf(
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                1,
                1,
                2,
                6,
                7,
                14,
                17,
                21,
                24,
                29,
                32,
                32,
                34,
                35,
                36,
                36,
                36,
                36,
                36,
                36,
                36,
                36,
                36,
                36,
            )
        private val fl0001STMG2024 =
            listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5)
        private val fl0001P2024 =
            listOf(
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                1,
                2,
                3,
                5,
                5,
                6,
                6,
                8,
                10,
                10,
                10,
                10,
                10,
                10,
                10,
                10,
                10,
                10,
                10,
                10,
                10,
                10,
            )
        private val fl0001Generale2024 =
            listOf(
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                1,
                1,
                3,
                3,
                3,
                5,
                6,
                6,
                6,
                8,
                9,
                10,
                10,
                10,
                10,
                10,
                10,
                10,
                10,
                10,
                10,
            )
        private val fl0001STL2024 =
            listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2)

        private val fl0003STI2D2024 =
            listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
    }
}
