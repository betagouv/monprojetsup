package fr.gouv.monprojetsup.suggestions.services;

import fr.gouv.monprojetsup.suggestions.algo.AlgoSuggestions;
import fr.gouv.monprojetsup.suggestions.dto.GetSuggestionsConfigServiceDTO;
import fr.gouv.monprojetsup.suggestions.services.generic.MySuggService;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetSuggestionsConfigService extends MySuggService<GetSuggestionsConfigServiceDTO.Request, GetSuggestionsConfigServiceDTO.Response> {

    private final AlgoSuggestions algo;

    @Autowired
    public GetSuggestionsConfigService(
            AlgoSuggestions algo
    ) {
        super();
        this.algo = algo;
    }

    @Override
    protected @NotNull GetSuggestionsConfigServiceDTO.Response handleRequest(@NotNull GetSuggestionsConfigServiceDTO.Request req) {
        val config = algo.getConfig();
        return new GetSuggestionsConfigServiceDTO.Response(config);
    }

    @Override
    public String getServiceName() {
        return GetSuggestionsConfigService.class.getSimpleName();
    }

}
