package fr.gouv.monprojetsup.data;

import org.jetbrains.annotations.NotNull;

public class Constants {

    public static final int PASS_FL_COD = 2047;
    public static final String URL_ARTICLE_PAS_LAS = "https://explorer-avenirs.onisep.fr/formation/les-principaux-domaines-de-formation/les-etudes-de-sante/les-voies-d-acces-aux-etudes-de-maieutique-medecine-odontologie-pharmacie";

    public static final String LABEL_ARTICLE_PAS_LAS = "Les études de santé";
    public static final String PAS_LAS_TEXT = "<p>Pour accéder aux études de santé (maïeutique, médecine, odontologie et pharmacie), les lycéens doivent suivre le PASS (parcours d'accès spécifique santé) ou une L.AS (licence avec option accès santé) à l'université et réussir aux sélections organisées à l'issue.</p>";
    public static final String NEW_ONISEP_METIERS_SLUG_PREFIX = "https://explorer-avenirs.onisep.fr/http/redirection/metier/slug/";
    public static final String EXPLORER_AVENIRS_URL = "https://explorer-avenirs.onisep.fr";
    public static final String ONISEP_URL1 = "http://www.onisep.fr";
    public static final String ONISEP_URL2 = "https://www.onisep.fr";
    public static final String CARTE_PARCOURSUP_PREFIX_URI = "https://dossier.parcoursup.fr/Candidat/carte?search=";
    public static final String PASS_MOT_CLE = "PASS";
    public static final int DUREE_LAS = 5;
    public static final int MIN_NB_ADMIS_FOR_BAC_ACTIF = 200;
    public static final int IEP_PSUP_FR_COD = 90;
    public static final int BPJEPS_PSUP_FR_COD = 640;
    public static final int ECOLES_ARTS_PSUP_FR_COD = 27;
    public static final int ECOLES_INGE_PSUP_FR_COD = 21;
    public static final int CMI_PSUP_FR_COD = 22;
    public static final int ECOLE_COMMERCE_PSUP_FR_COD = 24;
    public static final int ECOLE_ARCHI_PSUP_FL_COD = 250;
    public static final int ECOLE_ARCHI_INGE_PSUP_FL_COD = 251;
    public static final int ECOLE_ART_PSUP_FR_COD = 27;
    public static final int DIPLOME_ART_PSUP_FR_COD = 84700;
    public static final int CODE_NSF_CONSERVATION_RESTAURATION = 342;
    public static final int ECOLE_CONSERVATION_RESTAURATION_PSUP_FL_COD = 253;
    public static final int DMA_PSUP_FR_COD = 81;

    public static final String FRANCE_TRAVAIL_FICHE_METIER_PREFIX = "https://candidat.francetravail.fr/metierscope/fiche-metier/";
    public static String PSUP_FORMATION_FICHE = "https://dossier.parcoursup.fr/Candidats/public/fiches/afficherFicheFormation?g_ta_cod=";

    public static final String DIAGNOSTICS_OUTPUT_DIR = "diagnostics/";
    /* constant added to the las gFlCod indexes */
    public static final Integer LAS_CONSTANT = 1000000;
    public static final String CENTRE_INTERETS_ROME = "T-ROME.";

    private static final String TYPE_FORMATION_PREFIX = "fr";//like g_fr_cod
    private static final String FILIERE_PREFIX = "fl";//like g_fl_cod
    public static final String FORMATION_PREFIX = "ta";//like g_ta_cod

    public static final int MAX_DISTANCE_VILLE_VOEU_KM = 100;

    public static final String CODE_COMMUNE_INSEE_PARIS_VINGTIEME = "75120";

    public static String gFlCodToMpsId(int cle) {
        return FILIERE_PREFIX + cle;
    }
    public static String gFlCodToMpsLasId(int cle) {
        return   FILIERE_PREFIX + (cle < LAS_CONSTANT ? LAS_CONSTANT + cle : cle);
    }
    public static String gFrCodToMpsId(int cle) {
        return TYPE_FORMATION_PREFIX + cle;
    }

    public static String gTaCodToMpsId(int cle) {
        return FORMATION_PREFIX + cle;
    }


    /**
     * creates a clean string suitable for indexing in js
     *
     * @param dirty the string to clean
     * @return the cleaned string
     */
    public static String cleanup(String dirty) {
        return dirty;
        //return dirty.replaceAll("[^a-zA-Z0-9]", "_");
    }

    private Constants() {
    }

    public static String includeKey(String key, String libelle) {
        return   libelle + " (" + key + ")";
    }

    public static boolean isFiliere(@NotNull String key) {
        return key.startsWith(Constants.FILIERE_PREFIX)
                || key.startsWith(Constants.TYPE_FORMATION_PREFIX);
    }

    private static final String METIER_PREFIX = "MET";//like g_ta_cod

    private static final String DOMAINE_MPS_PREFIX = "dom";//like g_ta_cod
    public static boolean isMetier(@NotNull String key) {
        return key.startsWith(METIER_PREFIX);
    }

    public static boolean isDomaineMps(@NotNull String key) {
        return key.startsWith(DOMAINE_MPS_PREFIX);
    }

    public static int mpsIdToGFlCod(@NotNull String id) {
        return Integer.parseInt(id.substring(FILIERE_PREFIX.length()));
    }

    public static boolean isPsupFiliere(@NotNull String id) {
        return  id.startsWith(FILIERE_PREFIX);
    }
}
