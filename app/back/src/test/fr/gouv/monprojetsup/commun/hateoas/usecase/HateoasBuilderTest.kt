package fr.gouv.monprojetsup.commun.hateoas.usecase

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupBadRequestException
import fr.gouv.monprojetsup.commun.hateoas.domain.entity.Hateoas
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class HateoasBuilderTest {
    val hateoasBuilder = HateoasBuilder()

    private val listeDe50Elements =
        listOf(
            1,
            2,
            3,
            4,
            5,
            6,
            7,
            8,
            9,
            10,
            11,
            12,
            13,
            14,
            15,
            16,
            17,
            18,
            19,
            20,
            21,
            22,
            23,
            24,
            25,
            26,
            27,
            28,
            29,
            30,
            31,
            32,
            33,
            34,
            35,
            36,
            37,
            38,
            39,
            40,
            41,
            42,
            43,
            44,
            45,
            46,
            47,
            48,
            49,
            50,
        )

    @Test
    fun `si le numero de page actuelle est supérieure à la dernière page, doit renvoyer une erreur`() {
        // When & Then
        assertThatThrownBy {
            hateoasBuilder.creerHateoas(
                liste = listeDe50Elements,
                numeroDePageActuelle = 6,
                tailleLot = 10,
            )
        }.isInstanceOf(MonProjetSupBadRequestException::class.java)
            .hasMessage("La page 6 n'existe pas. Veuillez en donner une entre 1 et 5")
    }

    @Test
    fun `si le numero de page actuelle est celui de la dernière page, page suivante doit être nulle`() {
        // When
        val resultat =
            hateoasBuilder.creerHateoas(
                liste = listeDe50Elements,
                numeroDePageActuelle = 5,
                tailleLot = 10,
            )

        // Then
        val attendu =
            Hateoas(
                premierePage = 1,
                dernierePage = 5,
                pageActuelle = 5,
                pageSuivante = null,
                listeCoupee = listOf(41, 42, 43, 44, 45, 46, 47, 48, 49, 50),
            )
        assertThat(resultat).isEqualTo(attendu)
    }

    @Test
    fun `si la taille du lot est supérieur à la taille de la liste, doit renvoyer le Hateoas attendu`() {
        // When
        val resultat =
            hateoasBuilder.creerHateoas(
                liste = listeDe50Elements,
                numeroDePageActuelle = 1,
                tailleLot = 100,
            )

        // Then
        val attendu =
            Hateoas(
                premierePage = 1,
                dernierePage = 1,
                pageActuelle = 1,
                pageSuivante = null,
                listeCoupee = listeDe50Elements,
            )
        assertThat(resultat).isEqualTo(attendu)
    }

    @Test
    fun `si la taille de la liste est un multiple de la taile du lot, doit renvoyer sans page avec des élèments vides`() {
        // When
        val resultat =
            hateoasBuilder.creerHateoas(
                liste = listeDe50Elements,
                numeroDePageActuelle = 8,
                tailleLot = 5,
            )

        // Then
        val attendu =
            Hateoas(
                premierePage = 1,
                dernierePage = 10,
                pageActuelle = 8,
                pageSuivante = 9,
                listeCoupee = listOf(36, 37, 38, 39, 40),
            )
        assertThat(resultat).isEqualTo(attendu)
    }

    @Test
    fun `si la taille de la liste n'est pas un multiple de la taile du lot, doit renvoyer le Hateoas attendu`() {
        // When
        val resultat =
            hateoasBuilder.creerHateoas(
                liste = listeDe50Elements,
                numeroDePageActuelle = 7,
                tailleLot = 8,
            )

        // Then
        val attendu =
            Hateoas(
                premierePage = 1,
                dernierePage = 7,
                pageActuelle = 7,
                pageSuivante = null,
                listeCoupee = listOf(49, 50),
            )
        assertThat(resultat).isEqualTo(attendu)
    }

    @Test
    fun `si la liste est vide, doit renvoyer le Hateoas attendu`() {
        // When
        val resultat =
            hateoasBuilder.creerHateoas(
                liste = emptyList<Int>(),
                numeroDePageActuelle = 1,
                tailleLot = 10,
            )

        // Then
        val attendu =
            Hateoas(
                premierePage = 1,
                dernierePage = 1,
                pageActuelle = 1,
                pageSuivante = null,
                listeCoupee = emptyList<Int>(),
            )
        assertThat(resultat).isEqualTo(attendu)
    }
}
