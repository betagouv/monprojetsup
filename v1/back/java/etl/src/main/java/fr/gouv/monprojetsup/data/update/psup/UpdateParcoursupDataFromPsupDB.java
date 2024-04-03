package fr.gouv.monprojetsup.data.update.psup;

/**
 * Update all data used by MonProjetSup
 */
public class UpdateParcoursupDataFromPsupDB {

    public static void main(String[] args) throws Exception {

        GetStatistiquesFromPsupDB.main(args);

        GetBackDataFomPsupDB.main(args);

    }

}
