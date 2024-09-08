package fr.gouv.monprojetsup.data.domain;

import java.util.Set;

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

    public static final int PASS_FL_COD = 2047;
    public static final String URL_ARTICLE_PAS_LAS = "https://explorer-avenirs.onisep.fr/formation/les-principaux-domaines-de-formation/les-etudes-de-sante/les-voies-d-acces-aux-etudes-de-maieutique-medecine-odontologie-pharmacie";

    public static final String LABEL_ARTICLE_PAS_LAS = "Les études de santé";
    public static final String PAS_LAS_TEXT = "<p>Pour accéder aux études de santé (maïeutique, médecine, odontologie et pharmacie), les lycéens doivent suivre le PASS (parcours d'accès spécifique santé) ou une L.AS (licence avec option accès santé) à l'université et réussir aux sélections organisées à l'issue.</p>";
    public static final String NEW_ONISEP_METIERS_SLUG_PREFIX = "https://explorer-avenirs.onisep.fr/http/redirection/metiers/slug/";
    public static final String EXPLORER_AVENIRS_URL = "https://explorer-avenirs.onisep.fr";
    public static final String ONISEP_URL1 = "http://www.onisep.fr";
    public static final String ONISEP_URL2 = "https://www.onisep.fr";
    public static final String CARTE_PARCOURSUP_PREFIX_URI = "https://dossier.parcoursup.fr/Candidat/carte?search=";
    public static final String PASS_MOT_CLE = "PASS";
    public static final int DUREE_LAS = 5;
    public static final int MIN_NB_ADMIS_FOR_BAC_ACTIF = 200;
    public static final int IEP_PSUP_FR_COD = 90;
    public static final int CMI_PSUP_FR_COD = 90;
    public static final int ECOLE_INGE_PSUP_FR_COD = 21;
    public static final int ECOLE_COMMERCE_PSUP_FR_COD = 24;
    public static final int ECOLE_COMMERCE_BAC_3_PSUP_FL_COD = 241;
    public static final int ECOLE_COMMERCE_BAC_4_PSUP_FL_COD = 242;
    public static final int ECOLE_COMMERCE_BAC_5_PSUP_FL_COD = 240;
    public static final int ECOLE_ARCHI_PSUP_FL_COD = 250;
    public static final int ECOLE_ARCHI_INGE_PSUP_FL_COD = 251;
    public static final int ECOLE_ART_PSUP_FR_COD = 27;
    public static final int DIPLOME_ART_PSUP_FR_COD = 84700;
    public static final int CMI_MECA_FL_COD_PSUP = 4041;
    public static final int BTS_AERONAUTIQUE_FL_COD_PSUP = 393;
    public static final int CPGE_LETTRES_PSUP_FL_COD = 31;
    public static final int CUPGE_ECO_GESTION_PSUP_FR_COD = 85;

    public static final int CUPGE_ECO_GESTION_PSUP_FL_COD1 = 984;
    public static final int CUPGE_ECO_GESTION_PSUP_FL_COD2 = 985;

    public static final int CODE_NSF_CONSERVATION_RESTAURATION = 342;
    public static final int ECOLE_CONSERVATION_RESTAURATION_PSUP_FL_COD = 253;
    public static final int CUPGE_ECO_GESTION_FR_COD = 85;
    public static final Set<String> FORMATION_PSUP_EXCLUES = Set.of(
            "fr63",//année préparatoire
            "fl85001",//Formation valant grade de licence
            "fr75000",//Diplôme d'Etablissement
            "fl231",//Formation Bac + 4
            "fr70",//Diplôme d'établissement,
            "fr95000",//Sous-officier,
            "fr75"//Diplôme d'Université
    );
    public static final int DMA_PSUP_FR_COD = 81;
    public static final String DATA_IDEO_DIRNAME = "data/ideo";
    public static final String BTS_AERONAUTIQUE_IDEO_COD = "FOR.9627";
    public static final String CMI_MECA_IDEO_COD = "FOR.5013";

    public static final String PREPA_LETTRE_IDEO_CODE = "FOR.1471";

    public static final String FRANCE_TRAVAIL_FICHE_METIER_PREFIX = "https://candidat.francetravail.fr/metierscope/fiche-metier/";
    public static String gFlCodToFrontId(int cle) {
        return FILIERE_PREFIX + cle;
    }
    public static String gFlCodToFrontId(String cle) {
        return FILIERE_PREFIX + cle;
    }
    public static String gFrCodToFrontId(int cle) {
        return TYPE_FORMATION_PREFIX + cle;
    }

    public static String gTaCodToFrontId(int cle) {
        return FORMATION_PREFIX + cle;
    }


    /**
     * creates a clean string suitable for indexing in js
     *
     * @param dirty the string to clean
     * @return the cleaned string
     */
    public static String cleanup(String dirty) {
        return dirty.replaceAll("[^a-zA-Z0-9]", "_");
    }

    private Constants() {
    }

    public static String includeKey(String key, String libelle) {
        return   libelle + " (" + key + ")";
    }
}
