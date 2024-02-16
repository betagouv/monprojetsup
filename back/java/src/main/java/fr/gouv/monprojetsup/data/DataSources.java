package fr.gouv.monprojetsup.data;

import lombok.Getter;
import lombok.Setter;

public class DataSources {
    public static final String FRONT_DATA_JSON_FILENAME = "frontendData.json";
    public static final String FRONT_DATA_JSON_FILENAME2 = "frontendData2.json";
    /* Parcoursup Data */
    public static final String TAGS_SOURCE_RAW_FILENAME = "parcoursup/tagsSources.json";
    public static final String TAGS_SOURCE_MERGED_FILENAME = "parcoursup/tagsSourcesMerged.json";
    public static final String STATS_BACK_SRC_FILENAME = "parcoursup/statistiques.zip";
    public static final String FRONT_MID_SRC_PATH = "parcoursup/data_mid.zip";
    public static final String BACK_PSUP_DATA_FILENAME = "parcoursup/backPsupData.json.zip";
    public static final String SPECIALITES_FILENAME = "parcoursup/specialites.json";
    /* Onisep official data */
    public static final String ONISEP_FICHES_METIERS = "onisep_fiches_metiers/Onisep_Ideo_Fiches_metiers_12092023.json";
    public static final String ONISEP_PSUP_TO_IDEO_PATH = "onisep_ideo_hotline/export_psup_onisep_ideo1_ideo2_06-12-2022_CompleteOnisep.json";
    //public static final String ONISEP_PSUP_TO_IDEO_PATH = "onisep_ideo_hotline/T_livre_a_Parcoursup_2024_01_26.json";
    public static final String ONISEP_FORMATIONS_WITH_METIER_PATH = "ideo_formations/Onisep_Ideo_Fiches_Formations_12092023.json";
    public static final String ONISEP_FORMATIONS_PATH = "ideo_formations/ideo-formations_initiales_en_france.json";
    public static final String METIERS_PATH = "ideo_metiers/ideo-metiers_onisep2.json";
    public static final String TYPES_FORMATIONS_ONISEP_PATH = "ideo_types_formations/types_formation1.json";
    public static final String THEMATIQUES_PATH = "items_thematiques/ideo-thematiques.json";
    public static final String THEMATIQUES_NOUVELLES_PATH = "items_thematiques/thematiques_nouvelles.json";
    public static final String THEMATIQUES_CATEGORIES_PATH = "items_thematiques/thematiques_categories.json";
    public static final String THEMATIQUES_REDIRECTIONS_PATH = "items_thematiques/thematiques_redirections.json";
    public static final String INTERETS_PATH = "items_centres_interets/centres_interets2.json";
    public static final String SECTEURS_PRO_PATH = "secteurs_pro/indexation_domaine.json";

    public static final String DOMAINES_PRO_PATH = "domaines_pro/domaines2.json";
    public static final String METIERS_INTERETS_PAIRES_PATH = "indexation_metiers_centres_interets/indexation_centre_interets2.json";
    public static final String  METIERS_THEMATIQUES_PAIRES_PATH = "indexation_metiers_thematiques/FM_Disciplines_02112023.xml.json";
    /* Onisep scrapping */
    public static final String ONISEP_DESCRIPTIFS_FORMATIONS_PATH = "onisep_scrap/descriptifs.json";
    public static final String ONISEP_DESCRIPTIFS_FORMATIONS_RESUMES_PATH = "onisep_scrap/summaries.json";
    public static final String ONISEP_DESCRIPTIFS_METIERS_PATH = "onisep_scrap/metiers.json";
    /* Onisep LG */
    public static final String THEMATIQUES_FORMATIONS_PAIRES_PATH = "indexation_psup_thematiques/ideo_thematiques_cod.json";
    public static final String THEMATIQUES_FORMATIONS_PAIRES_v2_PATH ="indexation_psup_thematiques/ajout_indexation_thematiques_utilisables.json";
    public static final String METIERS_FORMATIONS_PAIRES_PATH_MANUEL = "indexation_psup_metiers/indexation_metiers_maj.json";

    public static final String METIERS_FORMATIONS_AJOUT = "indexation_psup_metiers/ajouts_metiers.json";
    public static final String METIERS_FORMATIONS_HERITAGE = "indexation_psup_metiers/ajouts_heritage.json";

    public static final String LABELS_DICO = "frontAllLabels.json";


    /* Pole emploi data */
    public static final String ROME_CENTRES_INTERETS_PATH = "ROME/unix_arborescence_centre_interet_v451.json";
    public static final String ROME_THEMES_PATH = "ROME/unix_arborescence_thematique_v451.json";//unused
    /* External data  */
    public static final String CITIES_FILE_PATH = "villes/villes_france.json";
    public static final String CITIES_BACK_FILE_PATH = "villes_france.back.json";
    /***************************************************************************
     ******************* PATHES TO DATA FILES ***********************************
     ****************************************************************************/

    /* the directory containing the data */
    @Getter
    @Setter
    private static String rootDirectory = "../../../";

    /* the data file downloaded by the front clients */
    public static String getFrontSrcPath() {
        return getRootDirectory() + "/front/src/data/data.zip";
    }

    /* one of the data files used by the server */
    public static String getBackDataFilePath() {
        return getRootDirectory() + "/back/data/data/backendData.zip";
    }

    public static String getSourceDataFilePath(String filename) {
        return getRootDirectory() + "/data/" + filename;
    }
}
