package fr.gouv.monprojetsup.data.etl.loaders

import fr.gouv.monprojetsup.data.etl.MpsDataFromFiles
import fr.gouv.monprojetsup.data.model.specialites.Specialites
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertNotNull

@SpringBootTest(classes = [DataSources::class, MpsDataFromFiles::class] )
@ActiveProfiles("test")
class SpecialitesLoaderTest {

    @Autowired
    private lateinit var data: MpsDataFromFiles

    private var specialites: Specialites? = null

    @BeforeEach
    fun init() {
        specialites = data.getSpecialites()
    }

    @Test
    fun `l'ensemble des specialites est non vide`() {
        assertNotNull(specialites)
        val s = specialites?.toSpecialites()
        Assertions.assertThat(s).isNotEmpty()

    }

}