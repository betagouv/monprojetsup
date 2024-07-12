package fr.gouv.monprojetsup.metier.infrastructure.repository

import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import fr.gouv.monprojetsup.commun.lien.domain.entity.Lien
import fr.gouv.monprojetsup.metier.domain.entity.Metier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.then
import org.mockito.Mock
import org.mockito.Mockito.only
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class MetierBDDRepositoryTest : BDDRepositoryTest() {
    @Mock
    lateinit var logger: Logger

    @Autowired
    lateinit var metierJPARepository: MetierJPARepository

    lateinit var metierBDDRepository: MetierBDDRepository

    @BeforeEach
    fun setup() {
        metierBDDRepository = MetierBDDRepository(metierJPARepository, logger)
    }

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
        val result = metierBDDRepository.recupererLesMetiersDetailles(ids)

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
                        "Le fleuriste est un artisan qui confectionne et vend des bouquets, des compositions florales, des plantes et " +
                            "des accessoires de décoration. Il peut également être amené à conseiller ses clients sur le choix des " +
                            "fleurs et des plantes en fonction de l occasion et de leur budget. Le fleuriste peut travailler en " +
                            "boutique, en grande surface, en jardinerie ou en atelier de composition florale.",
                    liens = emptyList(),
                ),
            )
        assertThat(result).usingRecursiveComparison().isEqualTo(attendu)
        then(logger).should(only()).error("Le métier MET004 n'est pas présent en base")
    }

    @Test
    @Sql("classpath:metier.sql")
    fun `Si la liste est vide, doit retourner une liste vide`() {
        // Given
        val ids = emptyList<String>()

        // When
        val result = metierBDDRepository.recupererLesMetiersDetailles(ids)

        // Then
        val attendu = emptyList<Metier>()
        assertThat(result).isEqualTo(attendu)
    }
}
