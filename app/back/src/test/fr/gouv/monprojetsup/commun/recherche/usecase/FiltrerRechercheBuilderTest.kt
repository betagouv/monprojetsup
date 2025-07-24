package fr.gouv.monprojetsup.commun.recherche.usecase

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FiltrerRechercheBuilderTest {
    val builder = FiltrerRechercheBuilder()

    private val rechercheLongue = "[^12  ma*réchèrche 1%peu Toùt!peTit peu&lôngue a b c === ×÷\\ h^&e l l`.,|o w]{+o r l d'"

    @Test
    fun `doit garder seulement les mots alpha numérique avec les accents`() {
        // When
        val resultat = builder.filtrerMotsRecherches(recherche = rechercheLongue, tailleMinimumRecherche = 2)

        // Then
        assertThat(resultat).isEqualTo(listOf("12", "ma", "réchèrche", "peu", "Toùt", "peTit", "lôngue"))
    }

    @Test
    fun `doit filtrer les mots de moins de n caractères`() {
        // When
        val resultat = builder.filtrerMotsRecherches(recherche = rechercheLongue, tailleMinimumRecherche = 5)

        // Then
        assertThat(resultat).isEqualTo(listOf("réchèrche", "peTit", "lôngue"))
    }

    @Test
    fun `dois filtrer les mots doubles`() {
        // Given
        val rechercheMotsDoubles = "les chaussettes de l'archiduchesse sont-elles sèches archi-sèches"

        // When
        val resultat = builder.filtrerMotsRecherches(recherche = rechercheMotsDoubles, tailleMinimumRecherche = 2)

        // Then
        assertThat(resultat).isEqualTo(listOf("les", "chaussettes", "de", "archiduchesse", "sont", "elles", "sèches", "archi"))
    }
}
