package fr.gouv.monprojetsup.data

import fr.gouv.monprojetsup.data.Constants

class TestData {
    companion object {


        const val MIN_NB_CORR_PSUP_IDEO: Int = 600
        const val MIN_NB_FORMATIONS_MPS: Int = 200
        const val MAX_NB_FORMATIONS_SANS_DUREE: Int = 1
        const val MAX_PCT_FORMATIONS_ECHOUANT_AU_TEST_INTEGRITE: Int = 20
        const val MAX_PCT_METIERS_SANS_FORMATION_ASSOCIEE: Int = 35
        const val MAX_NB_VOEUX_SANS_COORDONNEES_GPS: Int = 200
        const val MAX_PCT_LAS_AVEC_STATS_VIDES: Int = 35
        const val MAX_PCT_FORMATIONS_SANS_STATS_COMPLETES: Int = 40
        const val MAX_PCT_FORMATIONS_SANS_STATS_HORS_APPRENTISSAGE: Int = 5
        const val MIN_NB_ARETES_SUGGESTIONS_GRAPH: Long = 1000

        const val ECOLE_COMMERCE_PSUP_FR_COD: Int = 24
        const val CPGE_LETTRES_PSUP_FL_COD: Int = 31
        const val CPGE_LETTRE_IDEO_CODE: String = "FOR.1471"
        const val CUPGE_ECO_GESTION_PSUP_FR_COD: Int = 85
        const val CUPGE_ECO_SCIENCES_TECHNO_SANTE_PSUP_FR_COD: Int = 83
        const val CUPGE_ECO_GESTION_PSUP_FL_COD1: Int = 984
        const val CUPGE_ECO_GESTION_PSUP_FL_COD2: Int = 985
        const val COMMERCE_INTERNATIONAL_DOMAINE_IDEO_CODE: Int = 4445

        private const val CMI_MECA_FL_COD_PSUP: Int = 4041
        private const val CMI_SVT_FL_COD_PSUP: Int = 4039
        private const val BTS_AERONAUTIQUE_FL_COD_PSUP: Int = 393
        private const val BTS_AERONAUTIQUE_IDEO_COD: String = "FOR.9627"
        private const val CMI_MECA_IDEO_COD: String = "FOR.10294"
        private const val CMI_SVT_IDEO_COD: String = "FOR.4980"
        val psupToIdeoReference = mapOf(
            Constants.gFlCodToMpsId(BTS_AERONAUTIQUE_FL_COD_PSUP) to BTS_AERONAUTIQUE_IDEO_COD,
            Constants.gFlCodToMpsId(CMI_SVT_FL_COD_PSUP) to CMI_SVT_IDEO_COD
            //Constants.gFlCodToMpsId(CMI_MECA_FL_COD_PSUP) to CMI_MECA_IDEO_COD
        )

    }
}