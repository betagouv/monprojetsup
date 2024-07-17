package fr.gouv.monprojetsup.suggestions.export.experts;

import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.suggestions.data.DataSources;
import fr.gouv.monprojetsup.suggestions.data.Helpers;
import fr.gouv.monprojetsup.suggestions.data.SuggestionsData;
import fr.gouv.monprojetsup.suggestions.data.tools.Serialisation;
import fr.gouv.monprojetsup.suggestions.dto.ProfileDTO;
import lombok.val;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Logger;

import static fr.gouv.monprojetsup.suggestions.export.experts.ReferenceCases.useRemoteUrl;

@Component
public class SuggestionsGenerator {

    private final SuggestionsData data;

    @Autowired
    public SuggestionsGenerator(SuggestionsData data) {
        this.data = data;
    }

    public static final Logger LOGGER = Logger.getLogger(SuggestionsGenerator.class.getName());
    public static final String REF_CASES_WITH_SUGGESTIONS = "refCasesWithSuggestions.json";

    private static final Integer RESTRICT_TO_INDEX = null;

    private static final boolean ONLY_FORMATIONS = true;

    public void generate() throws Exception {


        //we want the server in debug mode, with full explanations
        useRemoteUrl(false);

        LOGGER.info("Loading experts profiles...");
        List<Pair<String, ProfileDTO>> profiles = Serialisation.fromJsonFile(
                DataSources.getSourceDataFilePath(DataSources.PROFILS_EXPERTS_MPS_PATH),
                new TypeToken<List<ImmutablePair<String, ProfileDTO>>>(){}.getType()

        );
        ReferenceCases cases = from(profiles);


        LOGGER.info("Retrieving details and explanations...");
        ReferenceCases results = cases.getSuggestionsAndExplanations(RESTRICT_TO_INDEX, data.getLabels());

        if(ONLY_FORMATIONS) {
            results.cases().forEach(referenceCase -> referenceCase.suggestions().removeIf(
                    suggestion -> !Helpers.isFiliere(suggestion.fl())
            ));
        }

        LOGGER.info("Saving results...");
        results.toFile(REF_CASES_WITH_SUGGESTIONS);

    }

    public static ReferenceCases from(List<Pair<String, ProfileDTO>> profiles) {
        ReferenceCases result = new ReferenceCases();
        profiles.forEach(pair -> {
            val pf = pair.getRight();
            val refCase = new ReferenceCase(pair.getLeft(), pf);
            refCase.turnFavorisToExpectations();
            result.cases().add(refCase);
        });
        return result;
    }

}