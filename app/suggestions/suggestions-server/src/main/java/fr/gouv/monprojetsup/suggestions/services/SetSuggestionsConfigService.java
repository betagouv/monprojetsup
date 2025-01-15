package fr.gouv.monprojetsup.suggestions.services;

import fr.gouv.monprojetsup.suggestions.algo.AlgoSuggestions;
import fr.gouv.monprojetsup.suggestions.dto.SetSuggestionsConfigServiceDTO;
import fr.gouv.monprojetsup.suggestions.services.generic.MySuggService;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
        name = "mps.suggestions.dynamic_parameter_service.enabled", // The property to check
        havingValue = "true"           // Enable the service when the property is "true"
)
public class SetSuggestionsConfigService extends MySuggService<SetSuggestionsConfigServiceDTO.Request, SetSuggestionsConfigServiceDTO.Response> {

    private final AlgoSuggestions algo;

    @Autowired
    public SetSuggestionsConfigService(
            AlgoSuggestions algo
    ) {
        super();
        this.algo = algo;
    }

    @Override
    protected @NotNull SetSuggestionsConfigServiceDTO.Response handleRequest(@NotNull SetSuggestionsConfigServiceDTO.Request req) {
        val newConfig = algo.setParameters(req.config());
        return new SetSuggestionsConfigServiceDTO.Response(newConfig);
    }

    @Override
    public String getServiceName() {
        return SetSuggestionsConfigService.class.getSimpleName();
    }

}
