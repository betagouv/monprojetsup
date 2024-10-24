package fr.gouv.monprojetsup.suggestions.services;

import fr.gouv.monprojetsup.suggestions.algo.AlgoSuggestions;
import fr.gouv.monprojetsup.suggestions.dto.GetAffinitiesServiceDTO;
import fr.gouv.monprojetsup.suggestions.server.MySuggService;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class GetSuggestionsService extends MySuggService<GetAffinitiesServiceDTO.Request, GetAffinitiesServiceDTO.Response> {

    public static final  String SUGGESTIONS_ENDPOINT = "suggestions";
    private final AlgoSuggestions algo;

    @Autowired
    public GetSuggestionsService(
            AlgoSuggestions algo
    ) {
        super();
        this.algo = algo;
    }

    @Override
    protected @NotNull GetAffinitiesServiceDTO.Response handleRequest(@NotNull GetAffinitiesServiceDTO.Request req) {

        @NotNull List<Pair<String, @NotNull Map<String, @NotNull Double>>> suggestions = algo.getFormationsSuggestions(
                req.profile(),
                Objects.requireNonNullElse(req.inclureScores(), true)
        );
        List<String> metiers = algo.sortMetiersByAffinites(
                req.profile(),
                null
        );

        return new GetAffinitiesServiceDTO.Response(suggestions, metiers);
    }

    public String checkHealth() {
        return "OK" + "\n" + algo.getStats();
    }

    @Override
    public String getServiceName() {
        return GetSuggestionsService.class.getSimpleName();
    }

}
