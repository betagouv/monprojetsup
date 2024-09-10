package fr.gouv.monprojetsup.data.etl.port

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
    fun `70 pourcent des métiers ont au moins une formation associée`() {
        val metiersIdsRef = mpsDataPort.getMetiersMpsIds()
        Assertions.assertThat(metiersIdsRef).isNotEmpty()
        val metiersAvecauMoinsuneformation  = mpsDataPort.getFormationsVersMetiersEtMetiersAssocies().flatMap { (_,b) -> b }.toSet()
        val pctOk = 100 * metiersAvecauMoinsuneformation.size / metiersIdsRef.size
        Assertions.assertThat(pctOk).isGreaterThan(70)
    }

}