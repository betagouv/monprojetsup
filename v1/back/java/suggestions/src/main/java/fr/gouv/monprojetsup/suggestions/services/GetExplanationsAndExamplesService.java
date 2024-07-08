package fr.gouv.monprojetsup.suggestions.services;

import fr.gouv.monprojetsup.data.dto.GetExplanationsAndExamplesServiceDTO;
import fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions;
import fr.gouv.monprojetsup.suggestions.server.SuggestionServer;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetExplanationsAndExamplesService extends MySuggService<GetExplanationsAndExamplesServiceDTO.Request, GetExplanationsAndExamplesServiceDTO.Response> {

    public static final  String EXPLANATIONS_ENDPOINT = "explanations";

    public GetExplanationsAndExamplesService() {
        super(GetExplanationsAndExamplesServiceDTO.Request.class);
    }


    @Override
    protected @NotNull GetExplanationsAndExamplesServiceDTO.Response handleRequest(@NotNull GetExplanationsAndExamplesServiceDTO.Request req) throws Exception {
        List<GetExplanationsAndExamplesServiceDTO.ExplanationAndExamples> eae
                = AlgoSuggestions.getExplanationsAndExamples(
                        req.profile(),
                        req.keys(),
                        SuggestionServer.getConfig().getSuggFilConfig()
        );
        return new GetExplanationsAndExamplesServiceDTO.Response( eae);
    }


}
