package fr.gouv.monprojetsup.app.data;

import fr.gouv.monprojetsup.data.DataSources;
import fr.gouv.monprojetsup.data.model.cities.CitiesBack;
import fr.gouv.monprojetsup.data.model.cities.CitiesLoader;
import fr.gouv.monprojetsup.data.onisep.OnisepData;
import fr.gouv.monprojetsup.data.psup.PsupData;
import fr.gouv.monprojetsup.data.rome.RomeData;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import fr.gouv.parcoursup.carte.modele.modele.JsonCarte;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Logger;

import static fr.gouv.monprojetsup.data.DataSources.CARTE_JSON_PATH;


@Component
public class UpdateBackData {

    private static final Logger LOGGER = Logger.getLogger(UpdateBackData.class.getSimpleName());
    private final DataSources sources;

    @Autowired
    public UpdateBackData(DataSources sources) {
        this.sources = sources;
    }

    public void updateBackData() throws IOException {

        LOGGER.info("Chargement de " + DataSources.CITIES_FILE_PATH);
        CitiesBack cities = CitiesLoader.loadCitiesBack();

        val bpsFilename = sources.getSourceDataFilePath(DataSources.BACK_PSUP_DATA_FILENAME);
        LOGGER.info("Chargement de " + bpsFilename);
        PsupData psupData = Serialisation.fromZippedJson(bpsFilename, PsupData.class);
        psupData.cleanup();

        LOGGER.info("Chargement des données Onisep");
        OnisepData onisepData = OnisepData.fromFiles(sources);

        LOGGER.info("Chargement des données ROME");
        RomeData romeData = RomeData.load();
        onisepData.insertRomeData(romeData.centresInterest());

        JsonCarte carte= Serialisation.fromJsonFile(
                sources.getSourceDataFilePath(CARTE_JSON_PATH),
            JsonCarte.class
        );

        LOGGER.info("Enregistrement des données dans " + sources.getBackDataFilePath());
        BackEndData data = new BackEndData(psupData, onisepData, cities, carte);

        Serialisation.toZippedJson(sources.getBackDataFilePath(), data, true);
    }

}
