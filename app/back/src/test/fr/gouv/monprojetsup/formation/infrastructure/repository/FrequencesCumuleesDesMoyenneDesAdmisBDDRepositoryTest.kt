package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import fr.gouv.monprojetsup.referentiel.domain.entity.Baccalaureat
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class FrequencesCumuleesDesMoyenneDesAdmisBDDRepositoryTest : BDDRepositoryTest() {
    @Autowired
    lateinit var moyenneGeneraleAdmisBDDRepository: FrequencesCumuleesDesMoyenneDesAdmisBDDRepository

    @Nested
    inner class RecupererFrequencesCumuleesDeTousLesBacsParIdFormation {
        @Test
        @Sql("classpath:moyenne_generale_admis.sql")
        fun `Doit retourner les fréquences cumulées pour tous les baccalaureats`() {
            // Given
            val idFormation = "fl0001"

            // When
            val result = moyenneGeneraleAdmisBDDRepository.recupererFrequencesCumuleesDeTousLesBacs(idFormation)

            // Then
            val attendu =
                mapOf(
                    Baccalaureat(
                        id = "Général",
                        nom = "Série Générale",
                        idExterne = "Générale",
                    ) to
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
                            6,
                            24,
                            49,
                            77,
                            174,
                            292,
                            500,
                            685,
                            1206,
                            1700,
                            2375,
                            2845,
                            3924,
                            4755,
                            5479,
                            5893,
                            6401,
                            6612,
                            6670,
                            6677,
                        ),
                    Baccalaureat(
                        id = "Professionnel",
                        nom = "Série Pro",
                        idExterne = "P",
                    ) to
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
                            1,
                            1,
                            1,
                            1,
                            1,
                            1,
                            2,
                            2,
                            4,
                            4,
                            8,
                            8,
                            11,
                            12,
                            13,
                            14,
                            15,
                            15,
                            15,
                            15,
                            15,
                            15,
                            15,
                            15,
                            15,
                        ),
                )
            assertThat(result).isEqualTo(attendu)
        }

        @Test
        @Sql("classpath:moyenne_generale_admis.sql")
        fun `si la formation n'est pas trouvée, doit retourner map vide`() {
            // Given
            val idFormation = "fl0003"

            // When
            val result = moyenneGeneraleAdmisBDDRepository.recupererFrequencesCumuleesDeTousLesBacs(idFormation)

            // Then
            assertThat(result).isEqualTo(emptyMap<Baccalaureat, List<Int>>())
        }

        @Test
        @Sql("classpath:moyenne_generale_admis.sql")
        fun `si aucune donnée n'est trouvée pour l'année donnée, doit retourner map vide`() {
            // Given
            val idFormation = "fl0002"

            // When
            val result = moyenneGeneraleAdmisBDDRepository.recupererFrequencesCumuleesDeTousLesBacs(idFormation)

            // Then
            assertThat(result).isEqualTo(emptyMap<Baccalaureat, List<Int>>())
        }
    }

    @Nested
    inner class RecupererFrequencesCumuleesDeToutLesBacsPourPlusieursFormations {
        @Test
        @Sql("classpath:moyenne_generale_admis.sql")
        fun `Doit retourner les fréquences cumulées de tous les baccalaureats pour toutes les formations reconnues`() {
            // Given
            val idFormation = listOf("fl0001", "fl0003", "fl0002")

            // When
            val result = moyenneGeneraleAdmisBDDRepository.recupererFrequencesCumuleesDeTousLesBacs(idFormation)

            // Then
            val attendu =
                mapOf(
                    "fl0001" to
                        mapOf(
                            Baccalaureat(
                                id = "Général",
                                nom = "Série Générale",
                                idExterne = "Générale",
                            ) to
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
                                    6,
                                    24,
                                    49,
                                    77,
                                    174,
                                    292,
                                    500,
                                    685,
                                    1206,
                                    1700,
                                    2375,
                                    2845,
                                    3924,
                                    4755,
                                    5479,
                                    5893,
                                    6401,
                                    6612,
                                    6670,
                                    6677,
                                ),
                            Baccalaureat(
                                id = "Professionnel",
                                nom = "Série Pro",
                                idExterne = "P",
                            ) to
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
                                    1,
                                    1,
                                    1,
                                    1,
                                    1,
                                    1,
                                    2,
                                    2,
                                    4,
                                    4,
                                    8,
                                    8,
                                    11,
                                    12,
                                    13,
                                    14,
                                    15,
                                    15,
                                    15,
                                    15,
                                    15,
                                    15,
                                    15,
                                    15,
                                    15,
                                ),
                        ),
                    "fl0003" to emptyMap(),
                    "fl0002" to emptyMap(),
                )
            assertThat(result).isEqualTo(attendu)
        }
    }
}
