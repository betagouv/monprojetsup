package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import fr.gouv.monprojetsup.formation.domain.entity.CriteresAnalyseCandidature
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class CriteresAnalyseCandidatureBDDRepositoryTest : BDDRepositoryTest() {
    @Autowired
    lateinit var criteresAnalyseCandidatureBDDRepository: CriteresAnalyseCandidatureBDDRepository

    @Test
    @Sql("classpath:critere_analyse_candidature.sql")
    fun `Doit retourner la liste des critères d'analyse de candidature`() {
        // Given
        val valeursCriteresAnalyseCandidature = listOf(0, 5, 29, 63, 3)

        // When
        val result = criteresAnalyseCandidatureBDDRepository.recupererLesCriteresDUneFormation(valeursCriteresAnalyseCandidature)

        // Then
        val attendu =
            listOf(
                CriteresAnalyseCandidature(
                    nom = "Compétences académiques",
                    pourcentage = 0,
                ),
                CriteresAnalyseCandidature(
                    nom = "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                    pourcentage = 5,
                ),
                CriteresAnalyseCandidature(
                    nom = "Résultats académiques",
                    pourcentage = 29,
                ),
                CriteresAnalyseCandidature(
                    nom = "Savoir-être",
                    pourcentage = 63,
                ),
                CriteresAnalyseCandidature(
                    nom = "Motivation, connaissance",
                    pourcentage = 3,
                ),
            )
        assertThat(result).isEqualTo(attendu)
    }

    @Test
    @Sql("classpath:critere_analyse_candidature.sql")
    fun `Si un critère manque, doit retourner la liste des critères d'analyse de candidature sans ce dernier`() {
        // Given
        val valeursCriteresAnalyseCandidature = listOf(0, 0, 29, 63, 0, 8)

        // When
        val result = criteresAnalyseCandidatureBDDRepository.recupererLesCriteresDUneFormation(valeursCriteresAnalyseCandidature)

        // Then
        val attendu =
            listOf(
                CriteresAnalyseCandidature(
                    nom = "Compétences académiques",
                    pourcentage = 0,
                ),
                CriteresAnalyseCandidature(
                    nom = "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                    pourcentage = 0,
                ),
                CriteresAnalyseCandidature(
                    nom = "Résultats académiques",
                    pourcentage = 29,
                ),
                CriteresAnalyseCandidature(
                    nom = "Savoir-être",
                    pourcentage = 63,
                ),
                CriteresAnalyseCandidature(
                    nom = "Motivation, connaissance",
                    pourcentage = 0,
                ),
            )
        assertThat(result).isEqualTo(attendu)
    }

    @Test
    @Sql("classpath:critere_analyse_candidature.sql")
    fun `Si une valeur de critère manque, doit retourner la liste des critères d'analyse de candidature sans cette dernière`() {
        // Given
        val valeursCriteresAnalyseCandidature = listOf(71, 0, 29)

        // When
        val result = criteresAnalyseCandidatureBDDRepository.recupererLesCriteresDUneFormation(valeursCriteresAnalyseCandidature)

        // Then
        val attendu =
            listOf(
                CriteresAnalyseCandidature(
                    nom = "Compétences académiques",
                    pourcentage = 71,
                ),
                CriteresAnalyseCandidature(
                    nom = "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                    pourcentage = 0,
                ),
                CriteresAnalyseCandidature(
                    nom = "Résultats académiques",
                    pourcentage = 29,
                ),
            )
        assertThat(result).isEqualTo(attendu)
    }
}
