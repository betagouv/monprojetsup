package fr.gouv.monprojetsup.metier.infrastructure.repository

import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import fr.gouv.monprojetsup.commun.lien.domain.entity.Lien
import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import fr.gouv.monprojetsup.metier.domain.entity.Metier
import fr.gouv.monprojetsup.metier.domain.entity.MetierAvecSesFormations
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.then
import org.mockito.Mock
import org.mockito.Mockito.only
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class MetierBDDRepositoryTest : BDDRepositoryTest() {
    @Mock
    lateinit var logger: MonProjetSupLogger

    @Autowired
    lateinit var metierJPARepository: MetierJPARepository

    @Autowired
    lateinit var entityManager: EntityManager

    lateinit var metierBDDRepository: MetierBDDRepository

    @BeforeEach
    fun setup() {
        metierBDDRepository = MetierBDDRepository(metierJPARepository, entityManager, logger)
    }

    @Nested
    inner class RecupererMetiersDeFormations {
        @Test
        @Sql("classpath:metier.sql")
        fun `Doit retourner les métiers associés aux formations`() {
            // Given
            val idsFormations =
                listOf(
                    "fl1",
                    "fl10419",
                    "fl250",
                    "fl660008",
                )

            // When
            val result = metierBDDRepository.recupererMetiersDeFormations(idsFormations)

            // Then
            val attendu =
                mapOf(
                    "fl1" to listOf(),
                    "fl10419" to
                        listOf(
                            Metier(
                                id = "MET003",
                                nom = "Architecte",
                                descriptif =
                                    "L architecte est un professionnel du bâtiment qui conçoit des projets de construction ou de " +
                                        "rénovation de bâtiments. Il peut travailler sur des projets de construction de maisons " +
                                        "individuelles, d immeubles, de bureaux, d écoles, de musées, de centres commerciaux, de " +
                                        "stades, etc. L architecte peut travailler en agence d architecture, en bureau d études, " +
                                        "en entreprise de construction ou en collectivité territoriale.",
                                liens =
                                    listOf(
                                        Lien(
                                            nom = "Voir la fiche Onisep",
                                            url = "https://www.onisep.fr/ressources/univers-metier/metiers/architecte",
                                        ),
                                    ),
                            ),
                        ),
                    "fl250" to
                        listOf(
                            Metier(
                                id = "MET003",
                                nom = "Architecte",
                                descriptif =
                                    "L architecte est un professionnel du bâtiment qui conçoit des projets de construction ou de " +
                                        "rénovation de bâtiments. Il peut travailler sur des projets de construction de maisons " +
                                        "individuelles, d immeubles, de bureaux, d écoles, de musées, de centres commerciaux, de " +
                                        "stades, etc. L architecte peut travailler en agence d architecture, en bureau d études, " +
                                        "en entreprise de construction ou en collectivité territoriale.",
                                liens =
                                    listOf(
                                        Lien(
                                            nom = "Voir la fiche Onisep",
                                            url = "https://www.onisep.fr/ressources/univers-metier/metiers/architecte",
                                        ),
                                    ),
                            ),
                        ),
                    "fl660008" to
                        listOf(
                            Metier(
                                id = "MET002",
                                nom = "Fleuriste événementiel",
                                descriptif = null,
                                liens =
                                    listOf(
                                        Lien(
                                            nom = "Voir la fiche Onisep",
                                            url = "https://www.onisep.fr/ressources/univers-metier/metiers/fleuriste",
                                        ),
                                        Lien(
                                            nom = "Voir la fiche HelloWork",
                                            url = "https://www.hellowork.com/fr-fr/metiers/fleuriste.html",
                                        ),
                                    ),
                            ),
                        ),
                )
            assertThat(result).usingRecursiveComparison().isEqualTo(attendu)
        }

        @Test
        @Sql("classpath:metier.sql")
        fun `Si la liste est vide, doit retourner une liste vide`() {
            // Given
            val ids = emptyList<String>()

            // When
            val result = metierBDDRepository.recupererMetiersDeFormations(ids)

            // Then
            val attendu = emptyMap<String, Metier>()
            assertThat(result).isEqualTo(attendu)
        }
    }

    @Nested
    inner class RecupererLesMetiersAvecSesFormations {
        @Test
        @Sql("classpath:metier.sql")
        fun `Doit retourner les métiers reconnus dans l'ordre et ignorer ceux inconnus tout en les logguant`() {
            // Given
            val ids =
                listOf(
                    "MET004",
                    "MET003",
                    "MET002",
                    "MET001",
                )

            // When
            val result = metierBDDRepository.recupererLesMetiersAvecSesFormations(ids)

            // Then
            val attendu =
                listOf(
                    MetierAvecSesFormations(
                        id = "MET003",
                        nom = "Architecte",
                        descriptif =
                            "L architecte est un professionnel du bâtiment qui conçoit des projets de construction ou de rénovation de " +
                                "bâtiments. Il peut travailler sur des projets de construction de maisons individuelles, d immeubles, " +
                                "de bureaux, d écoles, de musées, de centres commerciaux, de stades, etc. L architecte peut travailler " +
                                "en agence d architecture, en bureau d études, en entreprise de construction ou en collectivité " +
                                "territoriale.",
                        liens =
                            listOf(
                                Lien(
                                    nom = "Voir la fiche Onisep",
                                    url = "https://www.onisep.fr/ressources/univers-metier/metiers/architecte",
                                ),
                            ),
                        formations =
                            listOf(
                                FormationCourte(id = "fl10419", nom = "BTS - Architectures en Métal : conception et Réalisation"),
                                FormationCourte(id = "fl250", nom = "EA-BAC5 - Architecture"),
                            ),
                    ),
                    MetierAvecSesFormations(
                        id = "MET002",
                        nom = "Fleuriste événementiel",
                        descriptif = null,
                        liens =
                            listOf(
                                Lien(
                                    nom = "Voir la fiche Onisep",
                                    url = "https://www.onisep.fr/ressources/univers-metier/metiers/fleuriste",
                                ),
                                Lien(
                                    nom = "Voir la fiche HelloWork",
                                    url = "https://www.hellowork.com/fr-fr/metiers/fleuriste.html",
                                ),
                            ),
                        formations =
                            listOf(
                                FormationCourte(id = "fl660008", nom = "BTSA - Métiers du Végétal : Alimentation, Ornement, Environnement"),
                            ),
                    ),
                    MetierAvecSesFormations(
                        id = "MET001",
                        nom = "Fleuriste",
                        descriptif =
                            "Le fleuriste est un artisan qui confectionne et vend des bouquets, des compositions florales, des plantes " +
                                "et des accessoires de décoration. Il peut également être amené à conseiller ses clients sur le " +
                                "choix des fleurs et des plantes en fonction de l occasion et de leur budget. Le fleuriste peut " +
                                "travailler en boutique, en grande surface, en jardinerie ou en atelier de composition florale.",
                        liens = emptyList(),
                        formations = emptyList(),
                    ),
                )
            assertThat(result).usingRecursiveComparison().isEqualTo(attendu)
            then(logger).should(only()).error(
                type = "METIER_ABSENT_BDD",
                message = "Le métier MET004 n'est pas présent en base",
                parametres = mapOf("metierAbsent" to "MET004"),
            )
        }

        @Test
        @Sql("classpath:metier.sql")
        fun `Si la liste est vide, doit retourner une liste vide`() {
            // Given
            val ids = emptyList<String>()

            // When
            val result = metierBDDRepository.recupererLesMetiersAvecSesFormations(ids)

            // Then
            val attendu = emptyList<MetierAvecSesFormations>()
            assertThat(result).isEqualTo(attendu)
        }
    }

    @Nested
    inner class RecupererLesMetiers {
        @Test
        @Sql("classpath:metier.sql")
        fun `Doit retourner les métiers reconnus dans l'ordre et ignorer ceux inconnus tout en les logguant`() {
            // Given
            val ids =
                listOf(
                    "MET004",
                    "MET003",
                    "MET002",
                    "MET001",
                )

            // When
            val result = metierBDDRepository.recupererLesMetiers(ids)

            // Then
            val attendu =
                listOf(
                    Metier(
                        id = "MET003",
                        nom = "Architecte",
                        descriptif =
                            "L architecte est un professionnel du bâtiment qui conçoit des projets de construction ou de rénovation de " +
                                "bâtiments. Il peut travailler sur des projets de construction de maisons individuelles, d immeubles, " +
                                "de bureaux, d écoles, de musées, de centres commerciaux, de stades, etc. L architecte peut travailler " +
                                "en agence d architecture, en bureau d études, en entreprise de construction ou en collectivité " +
                                "territoriale.",
                        liens =
                            listOf(
                                Lien(
                                    nom = "Voir la fiche Onisep",
                                    url = "https://www.onisep.fr/ressources/univers-metier/metiers/architecte",
                                ),
                            ),
                    ),
                    Metier(
                        id = "MET002",
                        nom = "Fleuriste événementiel",
                        descriptif = null,
                        liens =
                            listOf(
                                Lien(
                                    nom = "Voir la fiche Onisep",
                                    url = "https://www.onisep.fr/ressources/univers-metier/metiers/fleuriste",
                                ),
                                Lien(
                                    nom = "Voir la fiche HelloWork",
                                    url = "https://www.hellowork.com/fr-fr/metiers/fleuriste.html",
                                ),
                            ),
                    ),
                    Metier(
                        id = "MET001",
                        nom = "Fleuriste",
                        descriptif =
                            "Le fleuriste est un artisan qui confectionne et vend des bouquets, des compositions florales, des plantes " +
                                "et des accessoires de décoration. Il peut également être amené à conseiller ses clients sur le " +
                                "choix des fleurs et des plantes en fonction de l occasion et de leur budget. Le fleuriste peut " +
                                "travailler en boutique, en grande surface, en jardinerie ou en atelier de composition florale.",
                        liens = emptyList(),
                    ),
                )
            assertThat(result).usingRecursiveComparison().isEqualTo(attendu)
            then(logger).should(only()).error(
                type = "METIER_ABSENT_BDD",
                message = "Le métier MET004 n'est pas présent en base",
                parametres = mapOf("metierAbsent" to "MET004"),
            )
        }

        @Test
        @Sql("classpath:metier.sql")
        fun `Si la liste est vide, doit retourner une liste vide`() {
            // Given
            val ids = emptyList<String>()

            // When
            val result = metierBDDRepository.recupererLesMetiers(ids)

            // Then
            val attendu = emptyList<MetierAvecSesFormations>()
            assertThat(result).isEqualTo(attendu)
        }
    }

    @Nested
    inner class RecupererIdsMetiersInexistants {
        @Test
        @Sql("classpath:metier.sql")
        fun `si toutes les métiers existent, renvoyer une liste vide`() {
            // Given
            val ids = listOf("MET003", "MET002", "MET001")

            // When
            val result = metierBDDRepository.recupererIdsMetiersInexistants(ids)

            // Then
            assertThat(result).isEqualTo(emptyList<String>())
        }

        @Test
        @Sql("classpath:metier.sql")
        fun `si un des métiers n'existent pas, renvoyer la liste de ces métiers`() {
            // Given
            val ids = listOf("MET_INCONNU", "MET003", "MET002", "MET001")

            // When
            val result = metierBDDRepository.recupererIdsMetiersInexistants(ids)

            // Then
            assertThat(result).isEqualTo(listOf("MET_INCONNU"))
        }
    }
}
