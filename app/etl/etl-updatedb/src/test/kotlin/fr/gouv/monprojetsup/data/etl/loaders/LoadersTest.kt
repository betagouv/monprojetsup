package fr.gouv.monprojetsup.data.etl.loaders

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest(classes = [LoadersTest::class, DataSources::class] )
class LoadersTest {

    @Autowired
    lateinit var sources : DataSources

    @Test
    fun `Doit réussir à récupérer les formaitons ideo depuis le site onisep`() {
        // Given
        val formations = OnisepDataLoader.loadFormationsSimplesIdeo(sources)
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
    fun `Toutes les fiches métiers ideo ont un descriptif`() {
        // Given
        val metiers = OnisepDataLoader.loadFichesMetiersIDeo()
        // When
        // Then
        assertThat(metiers.map{ m -> m.descriptif}).allMatch { it.isNotBlank() }
    }


    @Test fun `Doit réussir à charger les intérêts, qui ne contiennent pas le terme je veux`() {
        val interets = OnisepDataLoader.loadInterets(sources)
        val labels = interets.groupeInterets.flatMap { it.items }.map { it.frontLabel}
        assertThat(labels).isNotEmpty
        assertThat(labels).allMatch { !it.contains("je veux") }
    }




}


