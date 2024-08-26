package fr.gouv.monprojetsup.data.domain;

public class Constants {
    public static final String TYPE_FORMATION_PREFIX = "fr";//like g_fr_cod
    public static final String FILIERE_PREFIX = "fl";//like g_fl_cod
    public static final String FORMATION_PREFIX = "ta";//like g_ta_cod
    public static final String MET_PREFIX = "MET_";
    public static final String THEME_PREFIX = "T_ITM";

    public static final String SEC_ACT_PREFIX_IN_FILES = "T_IDEO2_";
    public static final String SEC_ACT_PREFIX_IN_GRAPH = "SEC_";

    public static final String CENTRE_INTERETS_ROME = "T_ROME_";
    public static final String CENTRE_INTERETS_ONISEP = "T_IDEO2";
    public static final String BR = ".<br/>";

    public static final int PASS_FL_COD = 2047;
    public static final String URL_ARTICLE_PAS_LAS = "https://www.onisep.fr/formation/les-principaux-domaines-de-formation/les-etudes-de-sante/les-voies-d-acces-aux-etudes-de-maieutique-medecine-odontologie-pharmacie";

    public static final String LABEL_ARTICLE_PAS_LAS = "Les études de santé";
    public static final String PAS_LAS_TEXT = "<p>Pour accéder aux études de santé (maïeutique, médecine, odontologie et pharmacie), les lycéens doivent suivre le PASS (parcours d'accès spécifique santé) ou une L.AS (licence avec option accès santé) à l'université et réussir aux sélections organisées à l'issue.</p>";
    public static final String GROUPE_INFIX = " groupe ";
    public static final String OLD_ONISEP_FORMATION_SLUG_PREFIX = "http://www.terminales2022-2023.fr/http/redirection/formation/slug/";
    public static final String NEW_ONISEP_FORMATION_SLUG_PREFIX = "https://explorer-avenirs.onisep.fr/http/redirection/formation/slug/";
    public static final String NEW_ONISEP_METIERS_SLUG_PREFIX = "https://explorer-avenirs.onisep.fr/http/redirection/metiers/slug/";
    public static final String EXPLORER_AVENIRS_URL = "https://explorer-avenirs.onisep.fr";
    public static final String ONISEP_URL1 = "http://www.onisep.fr";
    public static final String ONISEP_URL2 = "https://www.onisep.fr";
    public static final String RESUME_FORMATION_MPS_HEADER = "résumé formation VLauriane";
    public static final String RESUME_FORMATION_V1 = "résumé formation V1";
    public static final String CARTE_PARCOURSUP_PREFIX_URI = "https://dossier.parcoursup.fr/Candidat/carte?search=";
    public static final String PASS_MOT_CLE = "PASS";
    public static final int DUREE_LAS = 5;

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
