package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import fr.gouv.monprojetsup.formation.domain.entity.CritereAnalyseCandidature
import fr.gouv.monprojetsup.formation.domain.entity.Formation
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class CriteresAnalyseCandidatureBDDRepositoryTest : BDDRepositoryTest() {
    @Autowired
    lateinit var criteresAnalyseCandidatureBDDRepository: CriteresAnalyseCandidatureBDDRepository

    private val criteresAnalyseDeBonneTaille = listOf(0, 5, 29, 63, 3)
    private val criteresAnalyseTropLongs = listOf(0, 0, 29, 33, 0, 18, 20)
    private val criteresAnalyseTropCourts = listOf(71, 0, 29)

    private val attenduCriteresAnalyseDeBonneTaille =
        listOf(
            CritereAnalyseCandidature(
                nom = "Compétences académiques",
                pourcentage = 0,
            ),
            CritereAnalyseCandidature(
                nom = "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                pourcentage = 5,
            ),
            CritereAnalyseCandidature(
                nom = "Résultats académiques",
                pourcentage = 29,
            ),
            CritereAnalyseCandidature(
                nom = "Savoir-être",
                pourcentage = 63,
            ),
            CritereAnalyseCandidature(
                nom = "Motivation, connaissance",
                pourcentage = 3,
            ),
        )
    private val attenduCriteresAnalyseTropLongs =
        listOf(
            CritereAnalyseCandidature(
                nom = "Compétences académiques",
                pourcentage = 0,
            ),
            CritereAnalyseCandidature(
                nom = "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                pourcentage = 0,
            ),
            CritereAnalyseCandidature(
                nom = "Résultats académiques",
                pourcentage = 29,
            ),
            CritereAnalyseCandidature(
                nom = "Savoir-être",
                pourcentage = 33,
            ),
            CritereAnalyseCandidature(
                nom = "Motivation, connaissance",
                pourcentage = 0,
            ),
        )
    private val attenduCriteresAnalyseTropCourts =
        listOf(
            CritereAnalyseCandidature(
                nom = "Compétences académiques",
                pourcentage = 71,
            ),
            CritereAnalyseCandidature(
                nom = "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                pourcentage = 0,
            ),
            CritereAnalyseCandidature(
                nom = "Résultats académiques",
                pourcentage = 29,
            ),
        )

    @Nested
    inner class RecupererLesCriteresDUneFormation {
        @Test
        @Sql("classpath:critere_analyse_candidature.sql")
        fun `Doit retourner la liste des critères d'analyse de candidature`() {
            // When
            val result = criteresAnalyseCandidatureBDDRepository.recupererLesCriteresDUneFormation(criteresAnalyseDeBonneTaille)

            // Then
            assertThat(result).isEqualTo(attenduCriteresAnalyseDeBonneTaille)
        }

        @Test
        @Sql("classpath:critere_analyse_candidature.sql")
        fun `Si un critère manque, doit retourner la liste des critères d'analyse de candidature sans ce dernier`() {
            // When
            val result = criteresAnalyseCandidatureBDDRepository.recupererLesCriteresDUneFormation(criteresAnalyseTropLongs)

            // Then
            assertThat(result).isEqualTo(attenduCriteresAnalyseTropLongs)
        }

        @Test
        @Sql("classpath:critere_analyse_candidature.sql")
        fun `Si une valeur de critère manque, doit retourner la liste des critères d'analyse de candidature sans cette dernière`() {
            // When
            val result = criteresAnalyseCandidatureBDDRepository.recupererLesCriteresDUneFormation(criteresAnalyseTropCourts)

            // Then
            assertThat(result).isEqualTo(attenduCriteresAnalyseTropCourts)
        }
    }

    @Nested
    inner class RecupererLesCriteresDeFormations {
        private val formation1: Formation =
            mock(Formation::class.java).apply {
                given(id).willReturn("fl1")
                given(valeurCriteresAnalyseCandidature).willReturn(criteresAnalyseDeBonneTaille)
            }

        private val formation2: Formation =
            mock(Formation::class.java).apply {
                given(id).willReturn("fl2")
                given(valeurCriteresAnalyseCandidature).willReturn(criteresAnalyseTropLongs)
            }

        private val formation3: Formation =
            mock(Formation::class.java).apply {
                given(id).willReturn("fl3")
                given(valeurCriteresAnalyseCandidature).willReturn(criteresAnalyseTropCourts)
            }

        @BeforeEach
        fun setup() {
            MockitoAnnotations.openMocks(this)
        }

        @Test
        @Sql("classpath:critere_analyse_candidature.sql")
        fun `Doit retourner la liste des critères d'analyse de candidature pour chaque formation`() {
            // Given
            val formations = listOf(formation1, formation2, formation3)

            // When
            val result = criteresAnalyseCandidatureBDDRepository.recupererLesCriteresDeFormations(formations)

            // Then
            val attendu =
                mapOf(
                    "fl1" to attenduCriteresAnalyseDeBonneTaille,
                    "fl2" to attenduCriteresAnalyseTropLongs,
                    "fl3" to attenduCriteresAnalyseTropCourts,
                )

            assertThat(result).isEqualTo(attendu)
        }
    }
}
