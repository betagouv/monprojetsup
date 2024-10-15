package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil.FormationAvecSonAffinite
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class OrdonnerRechercheFormationsBuilderTest {
    val builder = OrdonnerRechercheFormationsBuilder()

    @Nested
    inner class TrierParScore {
        @Test
        fun `si vide, doit renvoyer vide`() {
            // Given
            val resultatsRecherche = emptyMap<FormationCourte, Int>()

            // When
            val resultat = builder.trierParScore(resultatsRecherche)

            // Then
            assertThat(resultat).isEqualTo(emptyList<FormationCourte>())
        }

        @Test
        fun `doit trier la map par les valeurs`() {
            // Given
            val formation1 = FormationCourte("1", "1")
            val formation2 = FormationCourte("2", "2")
            val formation3 = FormationCourte("3", "3")
            val formation4 = FormationCourte("4", "4")
            val formation5 = FormationCourte("5", "5")
            val formation6 = FormationCourte("6", "6")
            val formation7 = FormationCourte("7", "7")
            val resultatsRecherche =
                mapOf(
                    formation1 to 0,
                    formation2 to 10,
                    formation3 to 63,
                    formation4 to 10,
                    formation5 to 30,
                    formation6 to 58,
                    formation7 to 100,
                )

            // When
            val resultat = builder.trierParScore(resultatsRecherche)

            // Then
            val attendu =
                listOf(
                    formation7,
                    formation3,
                    formation6,
                    formation5,
                    formation2,
                    formation4,
                    formation1,
                )
            assertThat(resultat).isEqualTo(attendu)
        }
    }

    @Nested
    inner class TrierParScoreEtSelonSuggestionsProfil {
        @Test
        fun `si résultats vide, doit renvoyer vide`() {
            // Given
            val resultatsRecherche = emptyMap<FormationCourte, Int>()
            val formationsAvecLeurAffinite = emptyList<FormationAvecSonAffinite>()

            // When
            val resultat = builder.trierParScoreEtSelonSuggestionsProfil(resultatsRecherche, formationsAvecLeurAffinite)

            // Then
            assertThat(resultat).isEqualTo(emptyList<FormationCourte>())
        }

        @Test
        fun `si formations avec leur affinité vide, doit renvoyer la liste triée sur les socre seulement`() {
            // Given
            val formation1 = FormationCourte("1", "1")
            val formation2 = FormationCourte("2", "2")
            val formation3 = FormationCourte("3", "3")
            val formation4 = FormationCourte("4", "4")
            val formation5 = FormationCourte("5", "5")
            val formation6 = FormationCourte("6", "6")
            val formation7 = FormationCourte("7", "7")
            val resultatsRecherche =
                mapOf(
                    formation1 to 0,
                    formation2 to 10,
                    formation3 to 63,
                    formation4 to 10,
                    formation5 to 30,
                    formation6 to 58,
                    formation7 to 100,
                )
            val formationsAvecLeurAffinite = emptyList<FormationAvecSonAffinite>()

            // When
            val resultat = builder.trierParScoreEtSelonSuggestionsProfil(resultatsRecherche, formationsAvecLeurAffinite)

            // Then
            val attendu =
                listOf(
                    formation7,
                    formation3,
                    formation6,
                    formation5,
                    formation2,
                    formation4,
                    formation1,
                )
            assertThat(resultat).isEqualTo(attendu)
        }

        @Test
        fun `doit trier la map par les valeurs puis par suggestions`() {
            // Given
            val formation1 = FormationCourte("1", "1")
            val formation2 = FormationCourte("2", "2")
            val formation3 = FormationCourte("3", "3")
            val formation4 = FormationCourte("4", "4")
            val formation5 = FormationCourte("5", "5")
            val formation6 = FormationCourte("6", "6")
            val formation7 = FormationCourte("7", "7")
            val resultatsRecherche =
                mapOf(
                    formation1 to 100,
                    formation2 to 10,
                    formation3 to 63,
                    formation4 to 10,
                    formation5 to 10,
                    formation6 to 63,
                    formation7 to 100,
                )
            val formationsAvecLeurAffinite =
                listOf(
                    FormationAvecSonAffinite("1", 1.0f),
                    FormationAvecSonAffinite("2", 0.43f),
                    FormationAvecSonAffinite("3", 0.64f),
                    FormationAvecSonAffinite("4", 0.29f),
                    FormationAvecSonAffinite("5", 0.86f),
                    FormationAvecSonAffinite("6", 0.36f),
                    FormationAvecSonAffinite("7", 0.32f),
                )

            // When
            val resultat = builder.trierParScoreEtSelonSuggestionsProfil(resultatsRecherche, formationsAvecLeurAffinite)

            // Then
            val attendu =
                listOf(
                    formation1,
                    formation7,
                    formation3,
                    formation6,
                    formation5,
                    formation2,
                    formation4,
                )
            assertThat(resultat).isEqualTo(attendu)
        }
    }
}
