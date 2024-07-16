package fr.gouv.monprojetsup.suggestions.server;

import fr.gouv.monprojetsup.suggestions.data.SuggestionsData;
import fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class SuggestionServer extends Server {

    public static final Logger LOGGER = Logger.getLogger(SuggestionServer.class.getName());

    @Getter
    private static SuggestionsServerConfig config;

    public static boolean isReady() {
        return true;
    }

    public static void loadConfig() {
        throw new RuntimeException("Unimplemented");
    }

    public void init(SuggestionsData data) throws Exception {

        LOGGER.info("Loading config...");
        config = SuggestionsServerConfig.load();

        LOGGER.info("Initializing details ");
        AlgoSuggestions.initialize();

        LOGGER.info("Suggestion Server Initialized ");

    }

}
