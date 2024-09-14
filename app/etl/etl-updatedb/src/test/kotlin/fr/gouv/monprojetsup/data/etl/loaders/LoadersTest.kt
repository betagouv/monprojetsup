package fr.gouv.monprojetsup.data.etl.loaders

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(classes = [DataSources::class] )
@ActiveProfiles("test")
class LoadersTest {

    @Autowired
    private lateinit var dataSources: DataSources

    @Test
    fun `Doit réussir à récupérer les formations ideo `() {
        // Given
        val formations = OnisepDataLoader.loadFormationsSimplesIdeo(dataSources)
        // When
        // Then
        Assertions.assertThat(formations).isNotEmpty()
    }

    @Test
    fun `Doit réussir à récupérer les fiches formations ideo `() {
        // Given
        val fiches = OnisepDataLoader.loadFichesFormationsIdeo(dataSources)
        // When
        // Then
        Assertions.assertThat(fiches).isNotEmpty()
    }

    @Test
    fun `Doit réussir à récupérer les métiers ideo`() {
        // Given
        val metiers = OnisepDataLoader.loadMetiersSimplesIdeo(dataSources)
        // When
        // Then
        Assertions.assertThat(metiers).isNotEmpty()
    }

    @Test
    fun `Doit réussir à récupérer les fiches métiers ideo  `() {
        // Given
        val metiers = OnisepDataLoader.loadFichesMetiersIdeo(dataSources)
        // When
        // Then
        Assertions.assertThat(metiers).isNotEmpty()
    }

    @Test
    fun `Toutes les fiches métiers ideo ont un descriptif`() {
        // Given
        val metiers = OnisepDataLoader.loadFichesMetiersIdeo(dataSources)
        // When
        // Then
        Assertions.assertThat(metiers.map { m -> m.descriptif }).allMatch { it.isNotBlank() }
    }


    @Test
    fun `Doit réussir à charger les intérêts, qui ne contiennent pas le terme je veux`() {
        val interets = OnisepDataLoader.loadInterets(dataSources)
        Assertions.assertThat(interets.categories).isNotEmpty
        Assertions.assertThat(interets.categories).allMatch { it.label.isNotBlank() }
        Assertions.assertThat(interets.categories).allMatch { it.elements.isNotEmpty() }
        Assertions.assertThat(interets.categories.flatMap { it.elements }).allMatch { it.label.isNotBlank() }
        Assertions.assertThat(interets.categories.flatMap { it.elements }).allMatch { it.atomes.isNotEmpty() }

        val labels = interets.categories.flatMap { it.elements }.map { it.label}
        Assertions.assertThat(labels).allMatch { !it.contains("je veux") }
    }

    @Test
    fun `Doit réussir à charger les domaines`() {
        val domaines = OnisepDataLoader.loadDomaines(dataSources)
        Assertions.assertThat(domaines.categories).isNotEmpty
        Assertions.assertThat(domaines.categories).allMatch { it.label.isNotBlank() }
        Assertions.assertThat(domaines.categories).allMatch { it.elements.isNotEmpty() }
        Assertions.assertThat(domaines.categories.flatMap { it.elements }).allMatch { it.label.isNotBlank() }
        Assertions.assertThat(domaines.categories.flatMap { it.elements }).allMatch { it.atomes.isNotEmpty() }
    }



}