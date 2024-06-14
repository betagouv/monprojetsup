package fr.gouv.monprojetsup.suggestions.analysis;

import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.dto.ProfileDTO;
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.suggestions.server.SuggestionServer;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import org.apache.commons.lang3.tuple.Pair;

import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static fr.gouv.monprojetsup.suggestions.analysis.Simulate.LOGGER;
import static fr.gouv.monprojetsup.suggestions.analysis.Simulate.REF_CASES_WITH_SUGGESTIONS;

public class GenerateExpertsDocs {


    public static void main(String[] args) throws Exception {


        try {
            ServerData.statistiques = new PsupStatistiques();
            ServerData.statistiques.labels = Serialisation.fromJsonFile(
                    "labelsDebug.json",
                    Map.class
            );
        } catch (Exception e) {
            SuggestionServer server = new SuggestionServer();
            server.init();
        }

        //load Map<String, ProfileDTO> profiles = new HashMap<>(); from profilsExperts.json
        ReferenceCases cases;
        try {
            cases = ReferenceCases.loadFromFile(REF_CASES_WITH_SUGGESTIONS);
        } catch (Exception e) {
            LOGGER.info("Generating ref profiles");
            Simulate.main(args);
            cases = ReferenceCases.loadFromFile(REF_CASES_WITH_SUGGESTIONS);
        }

        cases.toDetails("détails", true);

        try (
                OutputStreamWriter fos = new OutputStreamWriter(
                        Files.newOutputStream(Path.of("profiles_expectations_suggestions.txt")),
                        StandardCharsets.UTF_8
                )
        ) {
            fos.write(cases.resume());
        }

        cases.resumeCsv("profiles_expectations_suggestions.csv");

    }

}
