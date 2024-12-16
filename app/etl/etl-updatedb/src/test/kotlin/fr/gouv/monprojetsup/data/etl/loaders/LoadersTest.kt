package fr.gouv.monprojetsup.data.etl.loaders

import fr.gouv.monprojetsup.data.Constants.isDomaineMps
import fr.gouv.monprojetsup.data.Constants.isFiliere
import fr.gouv.monprojetsup.data.Constants.isMetier
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.Map

@SpringBootTest(classes = [DataSources::class] )
@ActiveProfiles("test")
class LoadersTest {

    @Autowired
    private lateinit var dataSources: DataSources

    @Test
    fun `Doit réussir à récupérer les formations ideo `() {
        // Given
        val formations = OnisepDataLoader.loadFormationsSimplesIdeo(dataSources, Map.of<Any, Any>())
        // When
        // Then
        Assertions.assertThat(formations).isNotEmpty()
    }

    @Test
    fun `Doit réussir à récupérer les fiches formations ideo `() {
        // Given
        val fiches = OnisepDataLoader.loadFichesFormationsIdeo(dataSources, Map.of<Any, Any>())
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
    fun `Doit réussir à charger la correspondance old to new`() {
        // Given
        val oldToNew = OnisepDataLoader.loadOldToNewIdeo(dataSources)
        // When
        // Then
        Assertions.assertThat(oldToNew).isNotEmpty()
    }

    //
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
        Assertions.assertThat(interets.categories).allMatch { it.label.isNotBlank() }
        Assertions.assertThat(interets.categories).allMatch { it.elements.isNotEmpty() }
        Assertions.assertThat(interets.categories.flatMap { it.elements }).allMatch { it.id.isNotBlank() }
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
        Assertions.assertThat(domaines.categories.flatMap { it.elements }).allMatch { it.id.isNotBlank() }
        Assertions.assertThat(domaines.categories.flatMap { it.elements }).allMatch { it.label.isNotBlank() }
        Assertions.assertThat(domaines.categories.flatMap { it.elements }).allMatch { it.atomes.isNotEmpty() }
    }

    @Test
    fun `Doit réussir à charger les liens formations métiers`() {
        val liens = OnisepDataLoader.loadLiensFormationsPsupMetiers(dataSources)
        Assertions.assertThat(liens).isNotEmpty
        Assertions.assertThat(liens.keys).allMatch { id -> isFiliere(id) }
        Assertions.assertThat(liens.values.stream().flatMap { it.stream() }.toList().toSet()).allMatch { id -> isMetier(id) }
    }

    @Test
    fun `Doit réussir à charger les liens formations domaines`() {
        val liens = OnisepDataLoader.loadLiensFormationsMpsDomainesMps(dataSources)
        Assertions.assertThat(liens).isNotEmpty
        Assertions.assertThat(liens.keys).allMatch { id -> isFiliere(id) }
        Assertions.assertThat(liens.values.stream().flatMap { it.stream() }.toList().toSet()).allMatch { id -> isDomaineMps(id) }
    }


}