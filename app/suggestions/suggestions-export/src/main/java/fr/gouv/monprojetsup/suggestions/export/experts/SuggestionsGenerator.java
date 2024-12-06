package fr.gouv.monprojetsup.suggestions.export.experts;

import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import fr.gouv.monprojetsup.suggestions.data.SuggestionsData;
import fr.gouv.monprojetsup.suggestions.dto.ProfileDTO;
import lombok.val;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
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

    @Value("${profils.experts.mps.path}")
    private String profilsExpertPath = "profilsExperts.json";



    public void generate() throws IOException {

        //we want the server in debug mode, with full explanations
        useRemoteUrl(true);

        LOGGER.info("Loading experts profiles...");
        List<Pair<String, ProfileDTO>> profiles;
        try {
            profiles = Serialisation.fromJsonFile(
                    profilsExpertPath,
                    new TypeToken<List<ImmutablePair<String, ProfileDTO>>>(){}.getType()
            );
        } catch (IOException e) {
            throw new RuntimeException("Impossible d'accéder au fichier '" + profilsExpertPath + "'.");
        }
        ReferenceCases cases = from(profiles);

        LOGGER.info("Retrieving details and explanations...");
        ReferenceCases results = cases.getSuggestionsAndExplanations(RESTRICT_TO_INDEX, data.getDebugLabels());

        if(ONLY_FORMATIONS) {
            results.cases().forEach(referenceCase -> referenceCase.suggestions().removeIf(
                    suggestion -> !Constants.isFiliere(suggestion.id())
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
