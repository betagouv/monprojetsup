package fr.gouv.monprojetsup.data.update.psup;

import fr.gouv.monprojetsup.data.DataSources;
import fr.gouv.monprojetsup.tools.Serialisation;
import fr.gouv.monprojetsup.data.update.UpdateFrontData;

import java.util.logging.Logger;

import static fr.gouv.monprojetsup.data.DataSources.BACK_PSUP_DATA_FILENAME;

public class GetBackDataFomPsupDB {

    private static final Logger LOGGER = Logger.getLogger(UpdateFrontData.class.getSimpleName());

    public static void main(String[] args)
            throws Exception {

    /*
        Map<String,String> o = new HashMap<>();
        o.put("field1", "val1");
        o.put("field2", "val2");
        List l = new ArrayList<>();
        l.add(o);
        l.add(o);
        Serialisation.toJsonFile("toto", l, true);
*/

        OrientationConfig config = OrientationConfig.fromFile();

        try (ConnecteurSQL co = ConnecteurSQLHelper.getConnecteur(config.statsDB)
        ) {


            ConnecteurBackendSQL conn = new ConnecteurBackendSQL(co);


            LOGGER.info("Récupération des données");
            PsupData data = conn.recupererData();


            LOGGER.info("Export du backend data au format json  ");
            Serialisation.toZippedJson(DataSources.getSourceDataFilePath(BACK_PSUP_DATA_FILENAME), data, true);

        }


    }

}
