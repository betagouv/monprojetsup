package fr.gouv.monprojetsup.data.etl

import fr.gouv.monprojetsup.data.etl.loaders.OnisepDataLoader
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LoadersTest {

    @Test
    fun `Doit réussir à récupérer les formaitons ideo depuis le site onisep`() {
        // Given
        val formations = OnisepDataLoader.loadFormationsSimplesIdeo()
        // When
        // Then
        assertThat(formations).isNotEmpty()
    }

    @Test
    fun `Doit réussir à récupérer les fiches formations ideo depuis le site onisep`() {
        // Given
        val fiches = OnisepDataLoader.loadFichesFormationsIdeo()
        // When
        // Then
        assertThat(fiches).isNotEmpty()
    }

    @Test
    fun `Doit réussir à récupérer les métiers ideo depuis le site onisep`() {
        // Given
        val metiers = OnisepDataLoader.loadMetiersSimplesIdeo()
        // When
        // Then
        assertThat(metiers).isNotEmpty()
    }

    @Test
    fun `Doit réussir à récupérer les fiches métiers ideo depuis le site onisep `() {
        // Given
        val metiers = OnisepDataLoader.loadFichesMetiersIDeo()
        // When
        // Then
        assertThat(metiers).isNotEmpty()
    }

    @Test
    fun `Toutes les fiches métiers ideo ont un descriptif `() {
        // Given
        val metiers = OnisepDataLoader.loadFichesMetiersIDeo()
        // When
        // Then
        assertThat(metiers.map{ m -> m.descriptif}).allMatch { it.isNotBlank() }
    }

}