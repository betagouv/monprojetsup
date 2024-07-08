package fr.gouv.monprojetsup.suggestions.server;

import fr.gouv.monprojetsup.data.ServerData;
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

    @Override
    public void init() throws Exception {

        LOGGER.info("Loading config...");
        config = SuggestionsServerConfig.load();

        LOGGER.info("Loading data for suggestion Server...");
        ServerData.load();

        LOGGER.info("Initializing details ");
        AlgoSuggestions.initialize();

        LOGGER.info("Suggestion Server Initialized ");

    }

}
