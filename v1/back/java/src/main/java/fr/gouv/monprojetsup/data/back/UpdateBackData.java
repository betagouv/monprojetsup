package fr.gouv.monprojetsup.data.back;

import fr.gouv.monprojetsup.data.model.cities.CitiesBack;
import fr.gouv.monprojetsup.data.model.cities.CitiesLoader;
import fr.gouv.monprojetsup.data.update.psup.MergeDuplicateTags;
import fr.gouv.monprojetsup.tools.Serialisation;

import java.io.IOException;
import java.util.logging.Logger;

import static fr.gouv.monprojetsup.data.DataSources.CITIES_BACK_FILE_PATH;
import static fr.gouv.monprojetsup.data.DataSources.getSourceDataFilePath;

public class UpdateBackData {

    private static final Logger LOGGER = Logger.getLogger(UpdateBackData.class.getSimpleName());

    public static void main(String[] args) throws IOException {

        LOGGER.info("Mise à jour de " + CITIES_BACK_FILE_PATH);
        CitiesBack cities = CitiesLoader.loadCitiesBack();
        Serialisation.toJsonFile(getSourceDataFilePath(CITIES_BACK_FILE_PATH), cities, true);
        // Clean keywords (tagsSources.json).
        MergeDuplicateTags.main(args);
    }

}
