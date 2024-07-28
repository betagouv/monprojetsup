package fr.gouv.monprojetsup.suggestions.server;

import fr.gouv.monprojetsup.suggestions.algo.Config;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class SuggestionsServerConfig {

    private static final String CONFIG_FILENAME = "suggestionsConfig.json";

    @Getter
    private final Config suggFilConfig = new Config();

    @Getter
    private final String referenceCasesFilename = "referenceCasesFilename.json";

    @Getter
    private final String dataRootDirectory = "../../../";


    public static SuggestionsServerConfig load() throws IOException {
        SuggestionsServerConfig result;
        boolean fileExists = Files.exists(Path.of(CONFIG_FILENAME));
        try {
            result = Serialisation.fromJsonFile(CONFIG_FILENAME, SuggestionsServerConfig.class);
        } catch (IOException e) {
            if (fileExists) {
                throw e;
            }
            result = new SuggestionsServerConfig();
            Serialisation.toJsonFile(CONFIG_FILENAME, result, true);
        }

        //only default weight are used
        result.getSuggFilConfig().weights().putAll(new Config().weights());
        return result;
    }

}
