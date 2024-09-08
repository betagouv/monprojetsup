package fr.gouv.monprojetsup.data

import fr.gouv.monprojetsup.data.domain.Constants.*

class TestData {
    companion object {
        val psupToIdeoReference = mapOf(
            gFlCodToFrontId(BTS_AERONAUTIQUE_FL_COD_PSUP) to BTS_AERONAUTIQUE_IDEO_COD,
            gFlCodToFrontId(CMI_MECA_FL_COD_PSUP) to CMI_MECA_IDEO_COD
        )
    }
}