package fr.gouv.monprojetsup.data.etl.loaders

import fr.gouv.monprojetsup.data.TestData
import fr.gouv.monprojetsup.data.model.formations.FormationIdeoDuSup
import fr.gouv.monprojetsup.data.model.formations.FormationIdeoDuSup.getSousdomainesWebMpsIds
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [DataSources::class] )
class FormationsIdeoDuSupTest {

    @Autowired
    lateinit var sources : DataSources

    private lateinit var formationsIdeoDuSup: MutableMap<String, FormationIdeoDuSup>

    @BeforeEach
    fun setUp() {
        formationsIdeoDuSup = OnisepDataLoader.loadFormationsIdeoDuSup(sources)
    }

    @Test
    fun `Chaque formation ideo du sup a au moins un domaine associé`() {
        // Given
        val sousDomainesWeb = ArrayList(OnisepDataLoader.loadDomainesSousDomaines(sources))
        val sousdomainesWebByIdeoKey = sousDomainesWeb.associateBy { it.ideo }
        // Then
        assertThat(formationsIdeoDuSup).isNotEmpty()
        assertThat(formationsIdeoDuSup.values).allSatisfy { f ->
            getSousdomainesWebMpsIds(f.libellesOuClesSousdomainesWeb(), sousdomainesWebByIdeoKey).isNotEmpty()
        }
    }

    @Test
    fun `Les formations ideo du sup incluent les formations de reference`() {
        assertThat(TestData.psupToIdeoReference.values)
            .allSatisfy {
                    ideo -> formationsIdeoDuSup.containsKey(ideo)
            }
    }

    @Test
    fun `Les formations ideo du sup incluent prépa lettre et cette formation a des metiers`() {
        assertThat(formationsIdeoDuSup).containsKey(TestData.CPGE_LETTRE_IDEO_CODE)
        val prepaLettre = formationsIdeoDuSup[TestData.CPGE_LETTRE_IDEO_CODE]!!
        assertThat(prepaLettre.metiers).isNotEmpty
    }

    @Test
    fun `Les formations ideo du sup sont indexées en style ideo et leurs métiers aussi`() {
        assertThat(formationsIdeoDuSup.keys).allMatch { FormationIdeoDuSup.isIdeoFormationKey(it) }

        assertThat(formationsIdeoDuSup.values.flatMap { f -> f.metiers }).allMatch { FormationIdeoDuSup.isIdeoMetierKey(it) }
    }

    @Test
    fun `Les formations ideo du sup incluent L1 - histoire de l'art et archéologie et cette formation est connectée a commissaire priseur`() {
        assertThat(formationsIdeoDuSup).containsKey(TestData.L1_HISTOIRE_ART_IDEO_CODE)
        val formation = formationsIdeoDuSup[TestData.L1_HISTOIRE_ART_IDEO_CODE]!!
        assertThat(formation.metiers).contains(TestData.MET_COMMISSAIRE_PRISEUR_IDEO)
    }

}