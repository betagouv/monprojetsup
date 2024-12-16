package fr.gouv.monprojetsup.data.etl.loaders

import fr.gouv.monprojetsup.data.Constants
import fr.gouv.monprojetsup.data.TestData
import fr.gouv.monprojetsup.data.TestData.Companion.MASTER_TOURISME
import fr.gouv.monprojetsup.data.etl.loaders.OnisepDataLoader.loadIdeoHeritagesMastersLicences
import fr.gouv.monprojetsup.data.model.onisep.OnisepData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.Map


@SpringBootTest(classes = [DataSources::class] )
@ActiveProfiles("test")
class OnisepDataLoaderTest {

    private lateinit var onisepData: OnisepData

    @Autowired
    private lateinit var dataSources: DataSources

    @BeforeEach
    fun setUp() {
        onisepData = OnisepDataLoader.fromFiles(dataSources)
    }

    @Test
    fun `Doit réussir à charger les données Onisep`() {
        assertThat(onisepData.formationsIdeo).isNotEmpty()
        assertThat(onisepData.metiersIdeo).isNotEmpty()
    }

    @Test
    fun `Doit réussir à étendre les intérêts`() {
        val interets = onisepData.interets
        val expansion = interets.atomesversElements
        assertThat(expansion).isNotEmpty()
    }

    @Test
    fun `Il y a suffisamment de lignes dans la correspondance psup Onisep`() {
        assertThat(onisepData.filieresToFormationsOnisep.stream().map { l -> l.gFlCod }).hasSizeGreaterThanOrEqualTo(
            TestData.MIN_NB_CORR_PSUP_IDEO
        )
    }

    @Test
    fun `CUPGE Eco Gestion a au moins un metier`() {
        assertThat(onisepData.edgesMetiersFormations).anyMatch { it.right == Constants.gFlCodToMpsId(TestData.CUPGE_ECO_GESTION_PSUP_FL_COD1) }
        assertThat(onisepData.edgesMetiersFormations).anyMatch { it.right == Constants.gFlCodToMpsId(TestData.CUPGE_ECO_GESTION_PSUP_FL_COD2) }
    }

    @Test
    fun `Il y a suffisament arètes formations métiers`() {
        assertThat(onisepData.edgesMetiersFormations).hasSizeGreaterThanOrEqualTo(TestData.MIN_NB_ARETES_FORMATIONS_METIERS);
    }

    @Test
    fun `Licence d histoire ne mène pas à directeur de golf ni technicienne de forge`() {
        val formationsIdeoSansfiche = OnisepDataLoader.loadFormationsSimplesIdeo(
            dataSources,
            Map.of<Any, Any>()
        )
        val formationsIdeoAvecFiche = OnisepDataLoader.loadFichesFormationsIdeo(
            dataSources,
            Map.of<Any, Any>()
        )
        val formationsIdeoDuSup = OnisepDataLoader.extractFormationsIdeoDuSup(
            formationsIdeoSansfiche,
            formationsIdeoAvecFiche
        )
        val oldIdeoToNewIdeo = OnisepDataLoader.loadOldToNewIdeo(dataSources)
        val mastersIdeoToLicencesIdeo =
            loadIdeoHeritagesMastersLicences(dataSources, formationsIdeoDuSup.keys, oldIdeoToNewIdeo)

        val licencesHistoire =
            formationsIdeoDuSup.values.stream().filter { f -> f.label.contains("licence mention histoire") }.map { it.ideo }.toList().toSet()

        val mastersLicencesHistoire =
            mastersIdeoToLicencesIdeo.filter { it.value.any{ licencesHistoire.contains(it) }}
                .map { e -> formationsIdeoDuSup[e.key] }
                .filterNotNull()

        assertThat(mastersLicencesHistoire.map { it.ideo }).doesNotContain(MASTER_TOURISME)

        val mastersLicencesHistoireMenantADirHotel = mastersLicencesHistoire
            .filter { f -> f.metiers.contains(TestData.MET_DIR_HOTEL_IDEO) }

        assertThat(mastersLicencesHistoireMenantADirHotel).isEmpty()

        val mastersLicencesHistoireMenantATechnicienforge = mastersLicencesHistoire
            .filter { f -> f.metiers.contains(TestData.MET_TECH_FORGE_IDEO) }

        assertThat(mastersLicencesHistoireMenantATechnicienforge).isEmpty()

    }



}

