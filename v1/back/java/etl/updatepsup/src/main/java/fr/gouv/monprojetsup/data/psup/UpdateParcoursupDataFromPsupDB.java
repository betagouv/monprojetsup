package fr.gouv.monprojetsup.data.psup;

import fr.gouv.monprojetsup.suggestions.infrastructure.DataSources;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.tags.TagsSources;
import fr.gouv.monprojetsup.suggestions.infrastructure.psup.PsupData;
import fr.gouv.monprojetsup.suggestions.tools.Serialisation;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

import static fr.gouv.monprojetsup.data.psup.MergeDuplicateTags.stemMergeTags;

/**
 * Update all data used by MonProjetSup
 */
@Slf4j
@Component
@SpringBootApplication
public class UpdateParcoursupDataFromPsupDB implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateParcoursupDataFromPsupDB.class);

    private final DataSources sources;

    @Autowired
    public UpdateParcoursupDataFromPsupDB(DataSources sources) {
        this.sources = sources;
    }

    @Override
    public void run(String... args) throws Exception {

        getStatistiquesFromPsupDB();

        getBackDataFromPsupDB();

    }

    /**
     * generates BACK_PSUP_DATA_FILENAME
     * @throws Exception
     */
    private void getBackDataFromPsupDB() throws Exception {
        OrientationConfig config = OrientationConfig.fromFile();

        try (ConnecteurSQL co = ConnecteurSQLHelper.getConnecteur(config.statsDB)
        ) {
            ConnecteurBackendSQL conn = new ConnecteurBackendSQL(co);
            LOGGER.info("Récupération des données");
            PsupData data = conn.recupererData();
            LOGGER.info("Export du backend data au format json  ");
            Serialisation.toZippedJson(sources.getSourceDataFilePath(DataSources.BACK_PSUP_DATA_FILENAME), data, true);
        }

    }

    /**
     * generates STATS_BACK_SRC_FILENAME, FRONT_MID_SRC_PATH and TAGS_SOURCE_MERGED_FILENAME
     * @throws Exception
     */
    public void getStatistiquesFromPsupDB() throws Exception {

        OrientationConfig config = OrientationConfig.fromFile();
        //config.save();

        String path = sources.getSourceDataFilePath(DataSources.TAGS_SOURCE_RAW_FILENAME);
        if (!Files.exists(Path.of(path))) {
            throw new RuntimeException("No file " + path);
        }


        try (ConnecteurSQL co = ConnecteurSQLHelper.getConnecteur(config.statsDB)
        ) {

            ConnecteurBackendSQL conn = new ConnecteurBackendSQL(co);

            LOGGER.info("Récupération des données");
            Pair<PsupStatistiques, TagsSources> data = conn.recupererStatistiquesEtMotsCles();
            val stats = data.getLeft();
            val tagsSources = data.getRight();


            LOGGER.info("Export du full data set");
            Serialisation.toZippedJson(sources.getSourceDataFilePath(DataSources.STATS_BACK_SRC_FILENAME), stats, true);

            LOGGER.info("Export du middle data set");
            stats.minimize();
            Serialisation.toZippedJson(sources.getSourceDataFilePath(DataSources.FRONT_MID_SRC_PATH), data.getLeft(), true);

            LOGGER.info("Export des tags au format json  ");
            //for debug
            Serialisation.toJsonFile(sources.getSourceDataFilePath(DataSources.TAGS_SOURCE_RAW_FILENAME), tagsSources, true);
            // Stem and merge tags.
            TagsSources stemmedTagsSources = stemMergeTags(tagsSources);
            // Save stemmed tags in json file.
            Serialisation.toJsonFile(sources.getSourceDataFilePath(DataSources.TAGS_SOURCE_MERGED_FILENAME), stemmedTagsSources, true);

        }


    }


}

