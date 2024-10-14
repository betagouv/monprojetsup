package fr.gouv.monprojetsup.data

class TestData {
    companion object {


        const val MIN_NB_ARETES_FORMATIONS_METIERS: Int = 3000
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
        const val L1_HISTOIRE_ART_IDEO_CODE: String = "FOR.4741"
        const val CUPGE_ECO_GESTION_PSUP_FR_COD: Int = 85
        const val CUPGE_ECO_SCIENCES_TECHNO_SANTE_PSUP_FR_COD: Int = 83
        const val CUPGE_ECO_GESTION_PSUP_FL_COD1: Int = 984
        const val CUPGE_ECO_GESTION_PSUP_FL_COD2: Int = 985
        const val COMMERCE_INTERNATIONAL_DOMAINE_IDEO_CODE: Int = 4445

        const val BTS_AERONAUTIQUE_FL_COD_PSUP: Int = 393
        const val BTS_AERONAUTIQUE_IDEO_COD: String = "FOR.9627"

        const val CMI_SVT_FL_COD_PSUP: Int = 4039
        const val CMI_SVT_IDEO_COD: String = "FOR.4980"

        const val PASS_FL_COD_PSUP: Int = 2047
        const val PASS_IDEO_COD: String = "FOR.3884"

        const val MET_DIR_HOTEL_IDEO = "MET.2"
        const val MET_TECH_FORGE_IDEO = "MET.550"
        const val MET_COMMISSAIRE_PRISEUR_IDEO = "MET.673"
        const val MASTER_TOURISME = "FOR.10361"

        val psupToIdeoReference = mapOf(
            Constants.gFlCodToMpsId(BTS_AERONAUTIQUE_FL_COD_PSUP) to BTS_AERONAUTIQUE_IDEO_COD,
            Constants.gFlCodToMpsId(CMI_SVT_FL_COD_PSUP) to CMI_SVT_IDEO_COD,
            Constants.gFlCodToMpsId(PASS_FL_COD_PSUP) to PASS_IDEO_COD
        )

    }
}