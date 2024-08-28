package fr.gouv.monprojetsup.data.psup;

import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PsupDataSources {

    @Value("${dataRootDirectory}")
    private String dataRootDirectory = "./";

    public String getSourceDataFilePath(String filename) {
        String pathWithSpace = dataRootDirectory + "data/" + filename;
        val path = java.nio.file.Path.of(pathWithSpace);
        return path.toString();
    }

    /* Parcoursup Data */
    public static final String STATS_BACK_SRC_FILENAME = "parcoursup/statistiques.zip";
    public static final String FRONT_MID_SRC_PATH = "parcoursup/data_mid.zip";
    public static final String BACK_PSUP_DATA_FILENAME = "parcoursup/backPsupData.json.zip";


}
