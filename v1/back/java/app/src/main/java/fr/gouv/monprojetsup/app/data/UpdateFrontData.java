package fr.gouv.monprojetsup.app.data;

import com.google.gson.GsonBuilder;
import fr.gouv.monprojetsup.data.config.DataServerConfig;
import fr.gouv.monprojetsup.data.model.attendus.Attendus;
import fr.gouv.monprojetsup.data.model.attendus.GrilleAnalyse;
import fr.gouv.monprojetsup.data.model.descriptifs.Descriptifs;
import fr.gouv.monprojetsup.data.model.specialites.SpecialitesLoader;
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.data.onisep.OnisepData;
import fr.gouv.monprojetsup.data.psup.PsupData;
import fr.gouv.monprojetsup.data.rome.RomeData;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static fr.gouv.monprojetsup.data.tools.Serialisation.fromZippedJson;


@Service
public class UpdateFrontData {

    static final Logger LOGGER = Logger.getLogger(UpdateFrontData.class.getSimpleName());
    private final DataSources sources;

    @Autowired
    public UpdateFrontData(DataSources sources) {
        this.sources = sources;
    }

    public static void main(String[] args) throws IOException {

        DataServerConfig.load();

        LOGGER.info("Chargement et minimization de " + DataSources.FRONT_MID_SRC_PATH);
        PsupStatistiques data = Serialisation.fromZippedJson(DataSources.getSourceDataFilePath(DataSources.FRONT_MID_SRC_PATH), PsupStatistiques.class);
        data.removeFormations();
        data.removeSmallPopulations();

        LOGGER.info("Chargement et nettoyage de " + DataSources.getSourceDataFilePath(DataSources.BACK_PSUP_DATA_FILENAME));
        PsupData psupData = Serialisation.fromZippedJson(DataSources.getSourceDataFilePath(DataSources.BACK_PSUP_DATA_FILENAME), PsupData.class);
        psupData.cleanup();


        LOGGER.info("Chargement des données Onisep");
        final OnisepData onisepData = OnisepData.fromFiles();

        LOGGER.info("Chargement des données ROME");
        RomeData romeData = RomeData.load();

        LOGGER.info("Insertion des données ROME dans les données Onisep");
        onisepData.insertRomeData(romeData.centresInterest()); //before updateLabels

        LOGGER.info("Maj des données Onisep (noms des filières et urls)");
        data.updateLabels(onisepData, psupData, data.getLASCorrespondance().lasToGeneric());

        LOGGER.info("Ajout des liens metiers");
        val urls = new HashMap<String, Descriptifs.Link>();
        data.liensOnisep.forEach((key, value) -> urls.put(key, Descriptifs.toAvenirs(value, data.labels.getOrDefault(key,""))));
        onisepData.metiers().metiers().forEach((s, metier) -> {
            urls.put(s, Descriptifs.toAvenirs(
                    "https://explorer-avenirs.onisep.fr/http/redirection/metier/slug/" + s.replace('_','.'),
                    metier.lib())
            );
        });

        Map<String, Attendus> eds = Attendus.getAttendus(
                psupData,
                data,
                SpecialitesLoader.load(ServerData.statistiques),
                false
        );
        Map<String, GrilleAnalyse> grilles = GrilleAnalyse.getGrilles(psupData);

        DataContainer data2 = DataContainer.load(psupData, onisepData, urls, data.getLASCorrespondance(), eds, grilles, GrilleAnalyse.getLabelsMap());

        LOGGER.info("Mise à jour de " + DataSources.getFrontSrcPath());
        try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(Path.of(DataSources.getFrontSrcPath())))) {
            out.setMethod(8);
            out.setLevel(7);

            try (OutputStreamWriter o = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {

                out.putNextEntry(new ZipEntry(DataSources.FRONT_DATA_JSON_FILENAME2));
                new GsonBuilder().setPrettyPrinting().create().toJson(data2, o);

                o.flush();
                out.putNextEntry(new ZipEntry(DataSources.FRONT_DATA_JSON_FILENAME));
                new GsonBuilder().setPrettyPrinting().create().toJson(data, o);

            }

        }

    }
}
