package fr.gouv.monprojetsup.data;

public class Constants {
    public static final String TYPE_FORMATION_PREFIX = "fr";//like frCodPsup
    public static final String FILIERE_PREFIX = "fl";//like g_fl_cod
    public static final String FORMATION_PREFIX = "ta";//like g_ta_cod
    public static final String MET_PREFIX = "MET_";
    public static final String THEME_PREFIX = "T_ITM";

    public static final String SEC_ACT_PREFIX_IN_FILES = "T_IDEO2_";
    public static final String SEC_ACT_PREFIX_IN_GRAPH = "SEC_";

    public static final String CENTRE_INTERETS_ROME = "T_ROME_";
    public static final String CENTRE_INTERETS_ONISEP = "T_IDEO2";
    public static final String BR = ".<br/>";

    /* TODO: move elsewhere */
    public static String gFlCodToFrontId(int cle) {
        return FILIERE_PREFIX + cle;
    }

    public static String gTaCodToFrontId(int cle) {
        return FORMATION_PREFIX + cle;
    }

    public static boolean DEBUG_MODE = false;


    /**
     * creates a clean string suitable for indexing in js
     *
     * @param dirty
     * @return
     */
    public static String cleanup(String dirty) {
        return dirty.replaceAll("[^a-zA-Z0-9]", "_");
    }
}
