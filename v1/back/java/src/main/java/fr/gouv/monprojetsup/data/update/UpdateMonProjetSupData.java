package fr.gouv.monprojetsup.data.update;

import java.util.logging.Logger;

/**
 * Update all data used by MonProjetSup
 */
public class    UpdateMonProjetSupData {

    private static final Logger LOGGER = Logger.getLogger(UpdateMonProjetSupData.class.getSimpleName());

    public static void main(String[] args) throws Exception {

        UpdateFrontData.main(args);

        UpdateBackData.main(args);

    }

}
