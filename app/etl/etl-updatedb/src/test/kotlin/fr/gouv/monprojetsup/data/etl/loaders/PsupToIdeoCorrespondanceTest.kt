package fr.gouv.monprojetsup.data.etl.loaders

import fr.gouv.monprojetsup.data.domain.model.formations.FilieresPsupVersFormationsEtMetiersIdeo
import fr.gouv.monprojetsup.data.domain.model.formations.FormationIdeoDuSup
import fr.gouv.monprojetsup.data.domain.model.metiers.MetierIdeoDuSup
import fr.gouv.monprojetsup.data.domain.model.onisep.SousDomaineWeb
import fr.gouv.monprojetsup.data.domain.onisep.OnisepDataTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [OnisepDataTest::class, DataSources::class] )
class PsupToIdeoCorrespondanceTest {

    private lateinit var formationsIdeoDuSup: Map<String, FormationIdeoDuSup>

    private lateinit var metiersIdeoDuSup: MutableList<MetierIdeoDuSup>

    private lateinit var filieresPsupToFormationsMetiersIdeo: List<FilieresPsupVersFormationsEtMetiersIdeo>

    @Autowired
    lateinit var sources : DataSources

    @BeforeEach
    fun setUp() {
        val formationsIdeoSansfiche = OnisepDataLoader.loadFormationsSimplesIdeo()
        val formationsIdeoAvecFiche = OnisepDataLoader.loadFichesFormationsIdeo()
        formationsIdeoDuSup = OnisepDataLoader.extractFormationsIdeoDuSup(
            formationsIdeoSansfiche,
            formationsIdeoAvecFiche
        )
        filieresPsupToFormationsMetiersIdeo =
            OnisepDataLoader.loadPsupToIdeoCorrespondance(
                sources,
                formationsIdeoDuSup.values.stream().toList()
            )
        val sousDomainesWeb: List<SousDomaineWeb> = ArrayList(OnisepDataLoader.loadDomainesSousDomaines())
        metiersIdeoDuSup = OnisepDataLoader.loadMetiers(formationsIdeoDuSup.values, sousDomainesWeb, sources);
    }

    @Test
    fun `Toutes les formations référencées dans la correspondance psup vers ideo sont connues`() {
        assertThat(filieresPsupToFormationsMetiersIdeo.flatMap { f -> f.ideoFormationsIds }).allSatisfy { ideo ->
            formationsIdeoDuSup.containsKey(ideo)
        }
    }

    @Test
    fun `Toutes les métiers référencées dans la correspondance psup vers ideo sont connues`() {
        val metiersIdeoDuSupKeys = metiersIdeoDuSup.map { m -> m.ideo }.toSet()
        assertThat(filieresPsupToFormationsMetiersIdeo.flatMap { f -> f.ideoMetiersIds }).allSatisfy { ideo ->
            metiersIdeoDuSupKeys.contains(ideo)
        }
    }

}