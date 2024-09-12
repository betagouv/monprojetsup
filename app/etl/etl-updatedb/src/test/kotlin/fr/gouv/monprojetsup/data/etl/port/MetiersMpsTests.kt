package fr.gouv.monprojetsup.data.etl.port

import fr.gouv.monprojetsup.data.TestData
import fr.gouv.monprojetsup.data.etl.MpsDataPort
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles


@SpringBootTest
@ActiveProfiles("test")
class MetiersMpsTests {

    @Autowired
    lateinit var mpsDataPort: MpsDataPort

    @Test
    fun `la plupart des métiers ont au moins une formation associée`() {
        val metiersIdsRef = mpsDataPort.getMetiersMpsIds()
        Assertions.assertThat(metiersIdsRef).isNotEmpty()
        val metiersAvecauMoinsuneformation  = mpsDataPort.getFormationsVersMetiersEtMetiersAssocies().flatMap { (_,b) -> b }.toSet()
        val metiersSansFormation = metiersIdsRef.filter { !metiersAvecauMoinsuneformation.contains(it) }.toSet()
        Assertions.assertThat(metiersSansFormation).hasSizeLessThanOrEqualTo(TestData.MAX_PCT_METIERS_SANS_FORMATION_ASSOCIEE * metiersIdsRef.size / 100)
    }

}