package fr.gouv.monprojetsup.suggestions.server;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class SuggestionServer extends Server {

    public static final Logger LOGGER = Logger.getLogger(SuggestionServer.class.getName());

    @Getter
    private static SuggestionsServerConfig config;


    //spring property mps.suggestions.use_auto_eval_moygen from application.properties
    @Value("${mps.suggestions.use_auto_eval_moygen}")
    private boolean useAutoEvalMoyGen = false;

    @PostConstruct
    private void init() throws Exception {

        LOGGER.info("Loading config...");
        config = SuggestionsServerConfig.load();
        config.getSuggFilConfig().setUseAutoEvalMoyGen(useAutoEvalMoyGen);

        LOGGER.info("Suggestion Server Initialized ");

    }

}
