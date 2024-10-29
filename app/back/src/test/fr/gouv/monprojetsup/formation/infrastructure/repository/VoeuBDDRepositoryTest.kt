package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import fr.gouv.monprojetsup.formation.domain.entity.Voeu
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

class VoeuBDDRepositoryTest : BDDRepositoryTest() {
    @Autowired
    lateinit var voeuJPARepository: VoeuJPARepository

    lateinit var voeuBDDRepository: VoeuBDDRepository

    @BeforeEach
    fun setup() {
        voeuBDDRepository = VoeuBDDRepository(voeuJPARepository)
    }

    @Nested
    inner class RecupererVoeux {
        @Test
        @Sql("classpath:formation_voeu.sql")
        fun `Doit retourner les voeux grouper par formation en ignorant les inconnus`() {
            // Given
            val idsVoeux = listOf("ta0001", "ta0002", "tainconnu")

            // When
            val result = voeuBDDRepository.recupererVoeux(idsVoeux)

            // Then
            assertThat(result).usingRecursiveAssertion().isEqualTo(
                mapOf(
                    "fl0001" to
                        listOf(
                            Voeu(id = "ta0001", nom = "Lycée professionnel horticole de Montreuil", commune = MONTREUIL),
                        ),
                    "fl0003" to
                        listOf(
                            Voeu(id = "ta0002", nom = "ENSAPLV", commune = PARIS19EME),
                        ),
                ),
            )
        }
    }

    @Nested
    inner class RecupererLesVoeuxDeFormations {
        @Test
        @Sql("classpath:formation_voeu.sql")
        fun `Si les obsoletes sont inclus, doit retourner tous les voeux de formations`() {
            // Given
            val idsFormations = listOf("fl0001", "fl0004", "fl0003")

            // When
            val result = voeuBDDRepository.recupererLesVoeuxDeFormations(idsFormations, true)

            // Then
            assertThat(result).usingRecursiveAssertion().isEqualTo(
                mapOf(
                    "fl0001" to
                        listOf(
                            Voeu(id = "ta0001", nom = "Lycée professionnel horticole de Montreuil", commune = MONTREUIL),
                        ),
                    "fl0004" to
                        listOf(
                            Voeu(id = "ta0005", nom = "Université Paris 1 Panthéon-Sorbonne", commune = PARIS5EME),
                        ),
                    "fl0003" to
                        listOf(
                            Voeu(id = "ta0002", nom = "ENSAPLV", commune = PARIS19EME),
                            Voeu(id = "ta0003", nom = "ENSA Nancy", commune = NANCY),
                            Voeu(id = "ta0004", nom = "ENSAB", commune = RENNES),
                        ),
                ),
            )
        }

        @Test
        @Sql("classpath:formation_voeu.sql")
        fun `Si les obsoletes ne sont pas inclus, doit retourner les voeux non obsoletes des formations`() {
            // Given
            val idsFormations = listOf("fl0001", "fl0004", "fl0003")

            // When
            val result = voeuBDDRepository.recupererLesVoeuxDeFormations(idsFormations, false)

            // Then
            assertThat(result).usingRecursiveAssertion().isEqualTo(
                mapOf(
                    "fl0001" to
                        listOf(
                            Voeu(id = "ta0001", nom = "Lycée professionnel horticole de Montreuil", commune = MONTREUIL),
                        ),
                    "fl0004" to
                        listOf(
                            Voeu(id = "ta0005", nom = "Université Paris 1 Panthéon-Sorbonne", commune = PARIS5EME),
                        ),
                    "fl0003" to
                        listOf(
                            Voeu(id = "ta0003", nom = "ENSA Nancy", commune = NANCY),
                        ),
                ),
            )
        }

        @Test
        @Sql("classpath:formation_voeu.sql")
        fun `Si la formation n'a pas de voeux, alors sa liste doit être vide`() {
            // Given
            val idsFormations = listOf("fl0002")

            // When
            val result = voeuBDDRepository.recupererLesVoeuxDeFormations(idsFormations, true)

            // Then
            assertThat(result).usingRecursiveAssertion().isEqualTo(mapOf("fl0002" to emptyList<Voeu>()))
        }

        @Test
        @Sql("classpath:formation_voeu.sql")
        fun `Si la liste est vide, alors doit retourner une map vide`() {
            // Given
            val idsFormations = emptyList<String>()

            // When
            val result = voeuBDDRepository.recupererLesVoeuxDeFormations(idsFormations, true)

            // Then
            assertThat(result).usingRecursiveAssertion().isEqualTo(emptyMap<String, List<Voeu>>())
        }
    }

    @Nested
    inner class RecupererLesVoeuxDUneFormation {
        @Test
        @Sql("classpath:formation_voeu.sql")
        fun `Doit retourner les voeux d'une formation`() {
            // Given
            val idFormation = "fl0003"

            // When
            val result = voeuBDDRepository.recupererLesVoeuxDUneFormation(idFormation)

            // Then
            assertThat(result).usingRecursiveAssertion().isEqualTo(
                listOf(
                    Voeu(id = "ta0002", nom = "ENSAPLV", commune = PARIS19EME),
                    Voeu(id = "ta0003", nom = "ENSA Nancy", commune = NANCY),
                    Voeu(id = "ta0004", nom = "ENSAB", commune = RENNES),
                ),
            )
        }

        @Test
        @Sql("classpath:formation_voeu.sql")
        fun `Si la formation n'a pas de voeux, alors doit retourner vide`() {
            // Given
            val idFormation = "fl0002"

            // When
            val result = voeuBDDRepository.recupererLesVoeuxDUneFormation(idFormation)

            // Then
            assertThat(result).usingRecursiveAssertion().isEqualTo(emptyList<Voeu>())
        }
    }

    @Nested
    inner class RecupererIdsVoeuxInexistants {
        @Test
        @Sql("classpath:formation_voeu.sql")
        fun `si toutes les voeux existent, renvoyer la liste vide`() {
            // Given
            val idsVoeux = listOf("ta0001", "ta0002")

            // When
            val result = voeuBDDRepository.recupererIdsVoeuxInexistants(idsVoeux)

            // Then
            assertThat(result).isEqualTo(emptyList<String>())
        }

        @Test
        @Sql("classpath:formation_voeu.sql")
        fun `si un des voeux n'existe pas, renvoyer la liste des voeux inexistants`() {
            // Given
            val idsVoeux = listOf("ta0001", "ta0002", "tainconnu")

            // When
            val result = voeuBDDRepository.recupererIdsVoeuxInexistants(idsVoeux)

            // Then
            assertThat(result).isEqualTo(listOf("tainconnu"))
        }
    }
}
