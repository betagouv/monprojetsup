package fr.gouv.monprojetsup.suggestions.server;

import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions;
import fr.gouv.monprojetsup.tools.server.Server;
import lombok.Getter;

import java.nio.file.Path;
import java.util.logging.Logger;

public class SuggestionServer extends Server {

    public static final Logger LOGGER = Logger.getLogger(SuggestionServer.class.getName());

    @Getter
    private static SuggestionsServerConfig config;

    @Override
    public void init() throws Exception {

        LOGGER.info("Loading config...");
        config = SuggestionsServerConfig.load();

        LOGGER.info("Loading data for suggestion Server...");
        ServerData.load();

        LOGGER.info("Initializing suggestions ");
        AlgoSuggestions.initialize();

        LOGGER.info("Suggestion Server Initialized ");

    }

}
