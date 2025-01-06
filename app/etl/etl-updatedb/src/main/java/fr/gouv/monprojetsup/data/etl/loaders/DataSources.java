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

    public static final String IDEO_OD_FORMATIONS_FICHES_PATH = "ideo/5fe07a9ecc960.zip";
    public static final String IDEO_OD_FORMATIONS_FICHES_URL = "https://api.opendata.onisep.fr/downloads/5fe07a9ecc960/5fe07a9ecc960.zip";
    public static final String IDEO_OD_FORMATIONS_SIMPLE_PATH = "ideo/5fa591127f501.json";
    public static final String IDEO_OD_FORMATIONS_SIMPLE_URL = "https://api.opendata.onisep.fr/downloads/5fa591127f501/5fa591127f501.json";

    public static final String IDEO_OD_METIERS_SIMPLE_PATH = "ideo/5fa5949243f97.json";
    public static final String IDEO_OD_METIERS_FICHES_PATH = "ideo/5fe0808a2da6f.zip";
    public static final String IDEO_OD_DOMAINES_PATH = "ideo/5fa58d750a60c.json";

    /* Onisep data */
    public static final String IDEO_HERITAGES_LICENCES_CPGE_PATH = "onisep_ideo_hotline/MPS_LiensLicencesCPGE_valideOnisep.csv";
    public static final String IDEO_HERITAGES_LICENCES_MASTERS_PATH = "onisep_ideo_hotline/MPS_LiensLicencesMasters_valideOnisep.csv";
    public static final String IDEO_OLD_TO_NEW_PATH = "onisep_ideo_hotline/formations_archivees.csv";

    public static final String OLD_TO_NEW_IDEO_OLD_IDEO_HEADER = "OLD_ID_IDEO";
    public static final String OLD_TO_NEW_IDEO_NEW_IDEO_HEADER = "NEW_ID_IDEO";


    public static final String IDEO_HERITAGES_LICENCES_CPGE_HERITIER_HEADER = "IDEO2_PREPA";
    public static final String IDEO_HERITAGES_LICENCES_CPGE_LEGATAIRES_HEADER = "IDEO2_LICENCE";

    public static final String IDEO_HERITAGES_MASTERS_LICENCES_HERITIER_HEADER = "ID_Licence_ONISEP";
    public static final String IDEO_HERITAGES_MASTERS_LICENCES_LEGATAIRES_HEADER = "ID_Master_ONISEP";

    /* Onisep scrapping */
    public static final String ONISEP_SCRAPPED_DESCRIPTIFS_METIERS_PATH = "onisep_scrap/metiers.json";

    /* MPS data */
    public static final String RESUMES_MPS_PATH = "mps/Tableau_resume_descriptifs_formations.csv";
    public static final String RESUMES_MPS_RESUME_TYPE_FORMATION = "resume type formation";
    public static final String RESUMES_MPS_RESUME_TYPE_FORMATION_MOS = "Retours MOSS type formation";
    public static final String RESUMES_MPS_RESUME_FORMATION = "resume filiere";
    public static final String RESUMES_MPS_RESUME_FORMATION_MOS = "Retours MOSS filiere";
    public static final String RESUMES_MPS_RESUME_KEY = "code filiere";
    public static final String LIENS_MPS_PATH = "mps/Tableau_resume_descriptifs_formations_liens.csv";
    public static final String LIENS_MPS_PATH_HEADER_ID = "id";
    public static final String LIENS_MPS_PATH_HEADER_IGNORER = "probleme onisep data";
    public static final String LIENS_MPS_PATH_HEADER_EXTRAS = "ajouts";
    public static final String DOMAINES_MPS_PATH = "mps/Domaines MPS Onisep.csv";
    public static final String INTERETS_GROUPES_PATH = "mps/centres_d_interets_MPS_-_Feuille_1.csv";
    public static final String PSUP_TO_IDEO_CORRESPONDANCE_PATH = "onisep_ideo_hotline/liensPSUP_ONISEP.csv";
    public static final String PSUP_TO_METIERS_CORRESPONDANCE_PATH_PSUP_HEADER = "MPS_ID";
    public static final String PSUP_TO_METIERS_CORRESPONDANCE_PATH_FORMATION_IDEO_HEADER = "FOR_ID";
    public static final String PSUP_TO_METIERS_CORRESPONDANCE_PATH_METIER_IDEO_HEADER = "MET_ID";
    public static final String PSUP_TO_METIERS_CORRESPONDANCE_PATH = "onisep_ideo_hotline/liens_formations_mps_metiers_ideo.csv";
    public static final String MPS_FORMATIONS_TO_MPS_DOMAINE = "onisep_ideo_hotline/formations_domaines_ajout.csv";
    public static final String MPS_FORMATIONS_TO_MPS_DOMAINE_FORMATION_HEADER = "cleMPS";
    public static final String MPS_FORMATIONS_TO_MPS_DOMAINE_DOMAINE_HEADER = "domMPS";

    public static final String PSUP_HERITAGES_PATH = "mps/MPS_heritages.csv";
    public static final String PSUP_HERITAGES_HERITIER_HEADER = "mps_heritier";
    public static final String PSUP_HERITAGES_LEGATAIRES_HEADER = "mps_legataire";

    public static final String MOTS_CLES_MPS_PATH = "mps/motsCles.json";
    public static final String MPS_FORMATIONS_EXCLUES_PATH = "mps/formationsExclues.csv";
    public static final String MPS_FORMATIONS_EXCLUES_HEADER = "cle";
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
