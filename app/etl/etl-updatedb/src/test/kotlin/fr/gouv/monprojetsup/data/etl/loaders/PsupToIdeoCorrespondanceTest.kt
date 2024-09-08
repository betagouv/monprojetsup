package fr.gouv.monprojetsup.data.etl.loaders

import fr.gouv.monprojetsup.data.domain.Constants
import fr.gouv.monprojetsup.data.domain.Constants.*
import fr.gouv.monprojetsup.data.domain.model.formations.FilieresPsupVersIdeoData
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

    private lateinit var metiersIdeoDuSup: Map<String,MetierIdeoDuSup>

    private lateinit var filieresPsupToFormationsMetiersIdeo: Map<String, FilieresPsupVersIdeoData>

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
                formationsIdeoDuSup
            ).associateBy { it.gFlCod }

        val sousDomainesWeb: List<SousDomaineWeb> = ArrayList(OnisepDataLoader.loadDomainesSousDomaines())
        metiersIdeoDuSup = OnisepDataLoader.loadMetiers(formationsIdeoDuSup.values, sousDomainesWeb, sources)
    }

    @Test
    fun `Toutes les formations référencées dans la correspondance psup vers ideo sont connues`() {
        assertThat(filieresPsupToFormationsMetiersIdeo.values.flatMap { f -> f.ideoFormationsIds }).allSatisfy { ideo ->
            formationsIdeoDuSup.containsKey(ideo)
        }
    }

    @Test
    fun `Toutes les métiers référencées dans la correspondance psup vers ideo sont connues`() {
        assertThat(filieresPsupToFormationsMetiersIdeo.values.flatMap { f -> f.ideoMetiersIds }).allSatisfy { ideo ->
            metiersIdeoDuSup.containsKey(ideo)
        }
    }

    @Test
    fun `CPGE lettres est indexée et mène à des métiers`() {
        //c'est le mapping CPGE - licence qui crée les assciations métiers
        val cpge = filieresPsupToFormationsMetiersIdeo[gFlCodToFrontId(Constants.CPGE_LETTRES_PSUP_FL_COD)]
        assertThat(cpge).isNotNull
        assertThat(cpge!!.ideoMetiersIds).isNotEmpty()
    }

    @Test
    fun `CUPGE - Droit-économie-gestion hérite de tous les métiers des écoles de commerce`() {
        //c'est le mapping CPGE - licence qui crée les assciations métiers
        val cupge1 = filieresPsupToFormationsMetiersIdeo[gFlCodToFrontId(CUPGE_ECO_GESTION_PSUP_FL_COD1)]
        val cupge2 = filieresPsupToFormationsMetiersIdeo[gFlCodToFrontId(CUPGE_ECO_GESTION_PSUP_FL_COD2)]
        val ecoleCommerce3 = filieresPsupToFormationsMetiersIdeo[gFlCodToFrontId(ECOLE_COMMERCE_BAC_3_PSUP_FL_COD)]
        val ecoleCommerce4 = filieresPsupToFormationsMetiersIdeo[gFlCodToFrontId(ECOLE_COMMERCE_BAC_4_PSUP_FL_COD)]
        val ecoleCommerce5 = filieresPsupToFormationsMetiersIdeo[gFlCodToFrontId(ECOLE_COMMERCE_BAC_5_PSUP_FL_COD)]
        assertThat(listOf(cupge1, cupge2, ecoleCommerce3, ecoleCommerce4, ecoleCommerce5)).allSatisfy { assertThat(it).isNotNull }
        assertThat(ecoleCommerce3!!.ideoMetiersIds).isNotEmpty()
        assertThat(ecoleCommerce4!!.ideoMetiersIds).isNotEmpty()
        assertThat(ecoleCommerce5!!.ideoMetiersIds).isNotEmpty()
        assertThat(cupge1!!.ideoMetiersIds).containsAll(ecoleCommerce3.ideoMetiersIds)
        assertThat(cupge2!!.ideoMetiersIds).containsAll(ecoleCommerce3.ideoMetiersIds)
        assertThat(cupge1.ideoMetiersIds).containsAll(ecoleCommerce4.ideoMetiersIds)
        assertThat(cupge2.ideoMetiersIds).containsAll(ecoleCommerce4.ideoMetiersIds)
        assertThat(cupge1.ideoMetiersIds).containsAll(ecoleCommerce5.ideoMetiersIds)
        assertThat(cupge2.ideoMetiersIds).containsAll(ecoleCommerce5.ideoMetiersIds)
    }

}