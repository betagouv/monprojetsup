package fr.gouv.monprojetsup.suggestions.services;

import fr.gouv.monprojetsup.data.dto.GetAffinitiesServiceDTO;
import fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions;
import fr.gouv.monprojetsup.suggestions.server.SuggestionServer;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetSuggestionsService extends MyService<GetAffinitiesServiceDTO.Request, GetAffinitiesServiceDTO.Response> {

    public final static  String SUGGESTIONS_ENDPOINT = "suggestions";


    public GetSuggestionsService() {
        super(GetAffinitiesServiceDTO.class);
    }


    @Override
    protected @NotNull GetAffinitiesServiceDTO.Response handleRequest(@NotNull GetAffinitiesServiceDTO.Request req) {

        final @NotNull List<Pair<String,Double>> suggestions = AlgoSuggestions.getFormationsSuggestions(
                req.profile(),
                SuggestionServer.getConfig().getSuggFilConfig()
        );
        List<String> metiers = AlgoSuggestions.sortMetiersByAffinites(
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
