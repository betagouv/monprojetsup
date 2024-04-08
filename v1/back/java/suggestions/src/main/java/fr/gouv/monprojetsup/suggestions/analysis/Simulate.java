package fr.gouv.monprojetsup.suggestions.analysis;

import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import fr.gouv.monprojetsup.suggestions.server.SuggestionServer;

import java.util.Map;
import java.util.logging.Logger;

import static fr.gouv.monprojetsup.data.ServerData.isFiliere;
import static fr.gouv.monprojetsup.suggestions.analysis.ReferenceCases.ReferenceCase.useRemoteUrl;

public class Simulate {

    public static final Logger LOGGER = Logger.getLogger(Simulate.class.getName());
    public static final String REF_CASES_WITH_SUGGESTIONS = "refCasesWithSuggestions.json";

    private static final Integer RESTRICT_TO_INDEX = null;

    private static final boolean ONLY_FORMATIONS = true;

    public static void main(String[] args) throws Exception {


        //we want the server in debug mode, with full explanations
        useRemoteUrl(false);

        LOGGER.info("Loading config...");
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

        LOGGER.info("Loading reference cases...");
        ReferenceCases cases = ReferenceCases.loadFromFile("referenceCases.json");


        LOGGER.info("Retrieving details and explanations...");
        ReferenceCases results = cases.getSuggestionsAndExplanations(RESTRICT_TO_INDEX);

        if(ONLY_FORMATIONS) {
            results.cases().forEach(referenceCase -> referenceCase.suggestions().removeIf(
                    suggestion -> !isFiliere(suggestion.fl())
            ));
        }

        LOGGER.info("Saving results...");
        results.toFile(REF_CASES_WITH_SUGGESTIONS);
        results.toDetails("details", true);

    }
}
