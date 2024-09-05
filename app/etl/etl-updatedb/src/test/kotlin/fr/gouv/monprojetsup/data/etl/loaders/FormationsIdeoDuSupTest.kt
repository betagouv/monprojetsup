package fr.gouv.monprojetsup.data.etl.loaders

import fr.gouv.monprojetsup.data.TestData
import fr.gouv.monprojetsup.data.domain.model.formations.FormationIdeoDuSup
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FormationsIdeoDuSupTest {


    private lateinit var formationsIdeoDuSup: MutableMap<String, FormationIdeoDuSup>

    @BeforeEach
    fun setUp() {
        formationsIdeoDuSup = OnisepDataLoader.loadFormationsIdeoDuSup()
    }

    @Test
    fun `Chaque formation ideo du sup a au moins un domaine associé`() {
        // Given
        val sousDomainesWeb = ArrayList(OnisepDataLoader.loadDomainesSousDomaines())
        val sousdomainesWebByIdeoKey = sousDomainesWeb.associateBy { it.ideo }
        // Then
        Assertions.assertThat(formationsIdeoDuSup).isNotEmpty()
        Assertions.assertThat(formationsIdeoDuSup.values).allSatisfy { f ->
            f.getSousdomainesWebMpsIds(sousdomainesWebByIdeoKey).isNotEmpty()
        }
    }

    @Test
    fun `Les formations ideo du sup incluent les formations de reference`() {
        Assertions.assertThat(TestData.psupToIdeoReference.values)
            .allSatisfy {
                    ideo -> formationsIdeoDuSup.containsKey(ideo)
            }
    }

}