package fr.gouv.monprojetsup.data.update.psup;

import fr.gouv.monprojetsup.data.DataSources;
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.data.model.tags.TagsSources;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import org.apache.commons.lang3.tuple.Pair;

import java.util.logging.Logger;

import static fr.gouv.monprojetsup.data.tools.Serialisation.toZippedJson;

public class GetStatistiquesFromPsupDB {

    private static final Logger LOGGER = Logger.getLogger(GetStatistiquesFromPsupDB.class.getSimpleName());


    public static void main(String[] args)
            throws Exception {

        OrientationConfig config = OrientationConfig.fromFile();
        //config.save();

        try (ConnecteurSQL co = ConnecteurSQLHelper.getConnecteur(config.statsDB)
        ) {

            ConnecteurBackendSQL conn = new ConnecteurBackendSQL(co);

            LOGGER.info("Récupération des données");
            Pair<PsupStatistiques, TagsSources> data = conn.recupererStatistiquesEtMotsCles();

            LOGGER.info("Export des sources au format json  ");
            Serialisation.toJsonFile(DataSources.getSourceDataFilePath(DataSources.TAGS_SOURCE_RAW_FILENAME), data.getRight(), true);

            LOGGER.info("Export du full data set");
            toZippedJson(DataSources.getSourceDataFilePath(DataSources.STATS_BACK_SRC_FILENAME), data.getLeft(), true);

            LOGGER.info("Export du middle data set");
            data.getLeft().minimize();
            toZippedJson(DataSources.getSourceDataFilePath(DataSources.FRONT_MID_SRC_PATH), data.getLeft(), true);

        }

        MergeDuplicateTags.main(args);

    }
}
