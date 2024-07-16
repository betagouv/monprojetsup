package fr.gouv.monprojetsup.suggestions.services;

import fr.gouv.monprojetsup.suggestions.dto.GetAffinitiesServiceDTO;
import fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions;
import fr.gouv.monprojetsup.suggestions.server.MySuggService;
import fr.gouv.monprojetsup.suggestions.server.SuggestionServer;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetSuggestionsService extends MySuggService<GetAffinitiesServiceDTO.Request, GetAffinitiesServiceDTO.Response> {

    public final static  String SUGGESTIONS_ENDPOINT = "suggestions";
    private final AlgoSuggestions algo;

    @Autowired
    public GetSuggestionsService(
            AlgoSuggestions algo
    ) {
        super(GetAffinitiesServiceDTO.class);
        this.algo = algo;
    }

    @Override
    protected @NotNull GetAffinitiesServiceDTO.Response handleRequest(@NotNull GetAffinitiesServiceDTO.Request req) {

        final @NotNull List<Pair<String,Double>> suggestions = algo.getFormationsSuggestions(
                req.profile(),
                SuggestionServer.getConfig().getSuggFilConfig()
        );
        List<String> metiers = algo.sortMetiersByAffinites(
                req.profile(),
                null,
                SuggestionServer.getConfig().getSuggFilConfig()
        );

        return new GetAffinitiesServiceDTO.Response(suggestions, metiers);
    }

    public String checkHealth() {
        return "OK" + "\n" + AlgoSuggestions.getStats();
    }
}
