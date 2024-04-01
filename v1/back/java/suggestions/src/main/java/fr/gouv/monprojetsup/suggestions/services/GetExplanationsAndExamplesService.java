package fr.gouv.monprojetsup.suggestions.services;

import fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions;
import fr.gouv.monprojetsup.suggestions.algos.Explanation;
import fr.gouv.monprojetsup.suggestions.server.MyService;
import fr.gouv.monprojetsup.suggestions.server.SuggestionServer;
import fr.gouv.monprojetsup.app.tools.server.ResponseHeader;
import fr.gouv.monprojetsup.suggestions.dto.ProfileDTO;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetExplanationsAndExamplesService extends MyService<GetExplanationsAndExamplesService.Request, GetExplanationsAndExamplesService.Response> {

    public GetExplanationsAndExamplesService() {
        super(Request.class);
    }

    public record Request(
        @NotNull ProfileDTO profile,
        @NotNull String key) {
    }

    public record Response(
            @NotNull ResponseHeader header,
            @NotNull List<Explanation> explanations,
            @Nullable List<String> exemples
    ) {
        public Response(
                List<Explanation> explanations,
                @Nullable List<String> exemples
                ) {
            this(new ResponseHeader(), explanations, exemples);
        }
    }

    @Override
    protected @NotNull Response handleRequest(@NotNull Request req) throws Exception {
        Pair<List<Explanation>, List<String>> explanations
                = AlgoSuggestions.getExplanationsAndExamples(
                        req.profile,
                        req.key,
                        SuggestionServer.getConfig().getSuggFilConfig()
        );
        return new Response( explanations.getLeft(), explanations.getRight());
    }


}
