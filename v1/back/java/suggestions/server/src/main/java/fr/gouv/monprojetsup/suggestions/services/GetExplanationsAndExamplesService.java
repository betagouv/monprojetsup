package fr.gouv.monprojetsup.suggestions.services;

import fr.gouv.monprojetsup.suggestions.dto.GetExplanationsAndExamplesServiceDTO;
import fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions;
import fr.gouv.monprojetsup.suggestions.server.MySuggService;
import fr.gouv.monprojetsup.suggestions.server.SuggestionServer;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetExplanationsAndExamplesService extends MySuggService<GetExplanationsAndExamplesServiceDTO.Request, GetExplanationsAndExamplesServiceDTO.Response> {

    public static final  String EXPLANATIONS_ENDPOINT = "explanations";
    private final AlgoSuggestions algo;

    @Autowired
    public GetExplanationsAndExamplesService(
            AlgoSuggestions algo
    ) {
        super(GetExplanationsAndExamplesServiceDTO.Request.class);
        this.algo = algo;
    }


    @Override
    protected @NotNull GetExplanationsAndExamplesServiceDTO.Response handleRequest(@NotNull GetExplanationsAndExamplesServiceDTO.Request req) throws Exception {
        List<GetExplanationsAndExamplesServiceDTO.ExplanationAndExamples> eae
                = algo.getExplanationsAndExamples(
                        req.profile(),
                        req.keys(),
                        SuggestionServer.getConfig().getSuggFilConfig()
        );
        return new GetExplanationsAndExamplesServiceDTO.Response( eae);
    }


}
