package fr.gouv.monprojetsup.data.update.psup;

import fr.gouv.monprojetsup.data.DataSources;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.config.DataServerConfig;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Update all data used by MonProjetSup
 */
public class UpdateParcoursupDataFromPsupDB {

    public static void main(String[] args) throws Exception {

        DataServerConfig.load();

        String path = DataSources.getSourceDataFilePath(DataSources.TAGS_SOURCE_RAW_FILENAME);
        //path = URLEncoder.encode(path, "UTF-8");
        boolean ok = Files.exists(Path.of(path ));
        if(!ok) {
            throw new RuntimeException("No file " + path);
        }

        GetStatistiquesFromPsupDB.main(args);

        GetBackDataFomPsupDB.main(args);

    }

}
