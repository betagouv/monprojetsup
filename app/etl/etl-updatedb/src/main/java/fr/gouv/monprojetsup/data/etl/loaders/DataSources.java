package fr.gouv.monprojetsup.data.etl.loaders;

import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DataSources {


    /* the directory containing the data */
    @Value("${dataRootDirectory}")
    private String dataRootDirectory = "./";

    public static final String BACK_PSUP_DATA_FILENAME = "parcoursup/psupDataBack.zip";
    public static final String SPECIALITES_FILENAME = "parcoursup/specialites.json";
    public static final String BACS_FILENAME = "parcoursup/bacs.json";
    public static final String ONISEP_FICHES_METIERS = "onisep_fiches_metiers/Onisep_Ideo_Fiches_metiers_12092023.json";
    public static final String ONISEP_PSUP_TO_IDEO_PATH = "onisep_ideo_hotline/T_livre_a_Parcoursup_2024_01_26.json";
    public static final String ONISEP_IDEO_FICHES_FORMATIONS_WITH_METIER_PATH = "ideo_formations/Onisep_Ideo_Fiches_Formations_12092023.json";
    public static final String ONISEP_FORMATIONS_PATH = "ideo_formations/ideo-formations_initiales_en_france.json";
    public static final String METIERS_PATH = "ideo_metiers/ideo-metiers_onisep2.json";
    public static final String THEMATIQUES_PATH = "items_thematiques/ideo-thematiques.json";
    public static final String THEMATIQUES_REGROUPEMENTS_PATH = "items_thematiques/domaines_pro_MPS_-_Thematiques.tsv";

    public static final String THEMATIQUES_NOUVELLES_PATH = "items_thematiques/thematiques_nouvelles.json";
    public static final String THEMATIQUES_REDIRECTIONS_PATH = "items_thematiques/thematiques_redirections.json";
    public static final String INTERETS_PATH = "items_centres_interets/centres_interets2.json";
    public static final String INTERETS_GROUPES_PATH = "items_centres_interets/centres_d_interets_MPS_-_Feuille_1.tsv";

    public static final String DOMAINES_PRO_PATH = "domaines_pro/domaines2.json";
    public static final String METIERS_INTERETS_PAIRES_PATH = "indexation_metiers_centres_interets/indexation_centre_interets2.json";
    public static final String  METIERS_THEMATIQUES_PAIRES_PATH = "indexation_metiers_thematiques/FM_Disciplines_02112023.xml.json";
    /* Onisep scrapping */
    public static final String ONISEP_DESCRIPTIFS_FORMATIONS_PATH = "onisep_scrap/descriptifs.json";
    public static final String ONISEP_DESCRIPTIFS_FORMATIONS_RESUMES_PATH = "onisep_scrap/summaries.json";
    public static final String ONISEP_DESCRIPTIFS_METIERS_PATH = "onisep_scrap/metiers.json";

    public static final String RESUMES_MPS_PATH = "mps/Tableau_resume_descriptifs_formations.csv";
    /* Onisep LG */
    public static final String THEMATIQUES_FORMATIONS_PAIRES_PATH = "indexation_psup_thematiques/ideo_thematiques_cod.json";

    public static final String THEMATIQUES_FORMATIONS_PAIRES_v2_PATH ="indexation_psup_thematiques/ajout_indexation_thematiques_utilisables.json";
    public static final String METIERS_FORMATIONS_PAIRES_PATH_MANUEL = "indexation_psup_metiers/indexation_metiers_maj.json";
    public static final String METIERS_FORMATIONS_ONISEP_PAIRES_PATH = "indexation_psup_metiers/Indexation_FormationMPS_MetierI-Tableau 1.tsv";

    public static final String METIERS_FORMATIONS_AJOUT = "indexation_psup_metiers/ajouts_metiers.json";
    public static final String METIERS_FORMATIONS_HERITAGE = "indexation_psup_metiers/ajouts_heritage.json";

    public static final String LABELS_DICO = "frontAllLabels.json";


    /* Pole emploi data */
    public static final String ROME_CENTRES_INTERETS_PATH = "ROME/unix_arborescence_centre_interet_v451.json";
    public static final String ROME_THEMES_PATH = "ROME/unix_arborescence_thematique_v451.json";//unused
    /* External data  */
    public static final String CITIES_FILE_PATH = "villes/villes_france.json";

    /***************************************************************************
     ******************* PATHES TO DATA FILES ***********************************
     ****************************************************************************/


    public String getSourceDataFilePath(String filename) {
        String pathWithSpace = dataRootDirectory + "data/" + filename;
        val path = java.nio.file.Path.of(pathWithSpace);
        return path.toString();
    }
}
