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
        assertThat(resultat).isEqualTo(listOf("12", "réchèrche", "peu", "Toùt", "peTit", "lôngue"))
    }

    @Test
    fun `doit filtrer les mots de moins de n caractères`() {
        // When
        val resultat = builder.filtrerMotsRecherches(recherche = rechercheLongue, tailleMinimumRecherche = 5)

        // Then
        assertThat(resultat).isEqualTo(listOf("réchèrche", "peTit", "lôngue"))
    }

    @Test
    fun `doit filtrer les mots vides`() {
        // Given
        val rechercheAvecMotsVide =
            "ma recherche avec ma liste de mots vides le la les aux un une des du des en sur sous dans chez par pour sans contre entre parmi vers derrière devant après avant autour et ou mais donc ni car que quand comme puisque quoique mon mes ton ta tes son sa ses notre nos votre vos leur leurs ce cet cette ces qui que quoi dont lequel laquelle lesquels lesquelles"

        // When
        val resultat = builder.filtrerMotsRecherches(recherche = rechercheAvecMotsVide, tailleMinimumRecherche = 2)

        // Then
        assertThat(resultat).isEqualTo(listOf("recherche", "liste", "mots", "vides"))
    }

    @Test
    fun `dois filtrer les mots doubles`() {
        // Given
        val rechercheMotsDoubles = "les chaussettes de l'archiduchesse sont-elles sèches archi-sèches"

        // When
        val resultat = builder.filtrerMotsRecherches(recherche = rechercheMotsDoubles, tailleMinimumRecherche = 2)

        // Then
        assertThat(resultat).isEqualTo(listOf("chaussettes", "archiduchesse", "sont", "elles", "sèches", "archi"))
    }
}
