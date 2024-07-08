package fr.gouv.monprojetsup.data.update;

/**
 * Update all data used by MonProjetSup
 */
public class UpdateMonProjetSupData {

    public static void main(String[] args) throws Exception {

        UpdateBackData.main(args);

        UpdateFrontData.main(args);

    }

}
