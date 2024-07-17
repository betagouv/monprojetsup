package fr.gouv.monprojetsup.suggestions.server;

import fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class SuggestionServer extends Server {

    public static final Logger LOGGER = Logger.getLogger(SuggestionServer.class.getName());

    @Getter
    private static SuggestionsServerConfig config;

    private final AlgoSuggestions algo;

    public static boolean isReady() {
        return true;
    }

    public static void loadConfig() {
        throw new RuntimeException("Unimplemented");
    }

    @Autowired
    public SuggestionServer(AlgoSuggestions algo) {
        this.algo = algo;
    }
    @PostConstruct
    private void init() throws Exception {

        LOGGER.info("Loading config...");
        config = SuggestionsServerConfig.load();

        LOGGER.info("Suggestion Server Initialized ");

    }

}
