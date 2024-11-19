package fr.gouv.monprojetsup.data.etl.loaders

import fr.gouv.monprojetsup.data.Constants.ECOLE_COMMERCE_PSUP_FR_COD
import fr.gouv.monprojetsup.data.Constants.gFlCodToMpsId
import fr.gouv.monprojetsup.data.TestData.Companion.COMMERCE_INTERNATIONAL_DOMAINE_IDEO_CODE
import fr.gouv.monprojetsup.data.TestData.Companion.CPGE_LETTRES_PSUP_FL_COD
import fr.gouv.monprojetsup.data.TestData.Companion.CUPGE_ECO_GESTION_PSUP_FR_COD
import fr.gouv.monprojetsup.data.TestData.Companion.CUPGE_ECO_SCIENCES_TECHNO_SANTE_PSUP_FR_COD
import fr.gouv.monprojetsup.data.model.formations.FilieresPsupVersIdeoData
import fr.gouv.monprojetsup.data.model.formations.FormationIdeoDuSup
import fr.gouv.monprojetsup.data.model.metiers.MetierIdeo
import fr.gouv.monprojetsup.data.model.onisep.SousDomaineWeb
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [OnisepDataLoaderTest::class, DataSources::class] )
class PsupToIdeoCorrespondanceTest {

    private lateinit var formationsIdeoDuSup: Map<String, FormationIdeoDuSup>

    private lateinit var metiersIdeoDuSup: Map<String, MetierIdeo>

    private lateinit var filieresPsupToFormationsMetiersIdeo: Map<String, FilieresPsupVersIdeoData>

    @Autowired
    lateinit var sources: DataSources

    @BeforeEach
    fun setUp() {
        formationsIdeoDuSup = OnisepDataLoader.loadFormationsIdeoDuSup(sources)
        filieresPsupToFormationsMetiersIdeo =
            OnisepDataLoader.loadPsupToIdeoCorrespondance(
                sources,
                formationsIdeoDuSup
            ).associateBy { gFlCodToMpsId(it.gFlCod) }

        val sousDomainesWeb: List<SousDomaineWeb> = ArrayList(OnisepDataLoader.loadDomainesSousDomaines(sources))
        val metiers = OnisepDataLoader.loadMetiers(formationsIdeoDuSup.values, sousDomainesWeb, sources)
        metiersIdeoDuSup = metiers.left.filter { e -> metiers.right.contains(e.key) }.values.filterNotNull().associateBy { it.ideo }
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
        val cpge = filieresPsupToFormationsMetiersIdeo[gFlCodToMpsId(CPGE_LETTRES_PSUP_FL_COD)]
        assertThat(cpge).isNotNull
        assertThat(cpge!!.ideoMetiersIds).isNotEmpty()
    }

    @Test
    fun `CUPGE - Droit-économie-gestion hérite de tous les métiers des écoles de commerce`() {
        //c'est le mapping CPGE - licence qui crée les assciations métiers
        val cupges = filieresPsupToFormationsMetiersIdeo.values.filter { l -> l.gFrCod == CUPGE_ECO_GESTION_PSUP_FR_COD }
        val ecolesCommerce = filieresPsupToFormationsMetiersIdeo.values.filter { l -> l.gFrCod == ECOLE_COMMERCE_PSUP_FR_COD }

        val metiersEcoleCommerce = ecolesCommerce.flatMap { l -> l.ideoMetiersIds }.toSet()
        assertThat(metiersEcoleCommerce).isNotEmpty

        assertThat(cupges).isNotEmpty
        assertThat(cupges).allMatch { l -> l.ideoMetiersIds.containsAll(metiersEcoleCommerce) }
    }

    @Test
    fun `CUPGE - Sciences, technologie, santé ne contient pas le mot-clé commerce international `() {
        val cupges = filieresPsupToFormationsMetiersIdeo.values.filter { l -> l.gFrCod == CUPGE_ECO_SCIENCES_TECHNO_SANTE_PSUP_FR_COD }
        assertThat(cupges).isNotEmpty
        assertThat(cupges).allMatch { l -> l.libellesOuClesSousdomainesWeb.none { it.contains("commerce international") } }
        assertThat(cupges).allMatch { l -> l.libellesOuClesSousdomainesWeb.none { it.contains(COMMERCE_INTERNATIONAL_DOMAINE_IDEO_CODE.toString()) } }
    }

    }