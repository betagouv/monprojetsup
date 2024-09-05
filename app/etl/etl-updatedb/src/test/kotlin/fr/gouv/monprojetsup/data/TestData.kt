package fr.gouv.monprojetsup.data

import fr.gouv.monprojetsup.data.domain.Constants

class TestData {
    companion object {
        val psupToIdeoReference = mapOf(
            Constants.BTS_AERONAUTIQUE_FL_COD_PSUP to Constants.BTS_AERONAUTIQUE_IDEO_COD,
            Constants.CMI_MECA_FL_COD_PSUP to Constants.CMI_MECA_IDEO_COD
        )
    }
}