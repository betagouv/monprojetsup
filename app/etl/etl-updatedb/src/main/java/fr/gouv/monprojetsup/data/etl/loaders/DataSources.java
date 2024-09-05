package fr.gouv.monprojetsup.data.etl.loaders;

import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DataSources {


    /* the directory containing the data */
    @Value("${dataRootDirectory}")
    private String dataRootDirectory = "./";

    /* Psup data */
    public static final String BACK_PSUP_DATA_FILENAME = "parcoursup/psupDataBack.zip";
    public static final String SPECIALITES_FILENAME = "parcoursup/specialites.json";

    /* Ideo data */
    public static final String IDEO_OD_FORMATIONS_FICHES_URI = "https://api.opendata.onisep.fr/downloads/5fe07a9ecc960/5fe07a9ecc960.zip";
    public static final String IDEO_OD_FORMATIONS_SIMPLE_URI = "https://api.opendata.onisep.fr/downloads/5fa591127f501/5fa591127f501.json";
    public static final String IDEO_OD_METIERS_SIMPLE_URI = "https://api.opendata.onisep.fr/downloads/5fa5949243f97/5fa5949243f97.json";
    public static final String IDEO_OD_METIERS_FICHES_URI = "https://api.opendata.onisep.fr/downloads/5fe0808a2da6f/5fe0808a2da6f.zip";
    public static final String IDEO_OD_DOMAINES_URI = "https://api.opendata.onisep.fr/downloads/5fa58d750a60c/5fa58d750a60c.json";

    /* Onisep scrapping */
    public static final String ONISEP_SCRAPPED_DESCRIPTIFS_FORMATIONS_PATH = "onisep_scrap/descriptifs.json";
    public static final String ONISEP_SCRAPPED_DESCRIPTIFS_FORMATIONS_RESUMES_PATH = "onisep_scrap/summaries.json";
    public static final String ONISEP_SCRAPPED_DESCRIPTIFS_METIERS_PATH = "onisep_scrap/metiers.json";

    /* MPS data */
    public static final String RESUMES_MPS_PATH = "mps/Tableau_resume_descriptifs_formations.csv";
    public static final String DOMAINES_MPS_PATH = "mps/domaines_pro_MPS_-_Thematiques.tsv";
    public static final String INTERETS_GROUPES_PATH = "mps/centres_d_interets_MPS_-_Feuille_1.tsv";
    public static final String PSUP_TO_IDEO_CORRESPONDANCE_PATH = "onisep_ideo_hotline/T_livre_a_Parcoursup_2024_01_26_HG.csv";

    /* ROME data */
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
