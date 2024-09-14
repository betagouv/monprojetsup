package fr.gouv.monprojetsup.data.etl.port

import fr.gouv.monprojetsup.data.TestData
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test


class MetiersMpsTests : DataPortTest() {


    @Test
    fun `la plupart des métiers ont au moins une formation associée`() {
        val metiersIdsRef = mpsDataPort.getMetiersMpsIds()
        Assertions.assertThat(metiersIdsRef).isNotEmpty()
        val metiersAvecauMoinsuneformation  = mpsDataPort.getFormationsVersMetiersEtMetiersAssocies().flatMap { (_,b) -> b }.toSet()
        val metiersSansFormation = metiersIdsRef.filter { !metiersAvecauMoinsuneformation.contains(it) }.toSet()
        Assertions.assertThat(metiersSansFormation).hasSizeLessThanOrEqualTo(TestData.MAX_PCT_METIERS_SANS_FORMATION_ASSOCIEE * metiersIdsRef.size / 100)
    }

}