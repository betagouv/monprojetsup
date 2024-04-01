package fr.gouv.monprojetsup.suggestions.services;

import fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions;
import fr.gouv.monprojetsup.suggestions.algos.Suggestions;
import fr.gouv.monprojetsup.suggestions.dto.ProfileDTO;
import fr.gouv.monprojetsup.suggestions.server.MyService;
import fr.gouv.monprojetsup.suggestions.server.SuggestionServer;
import fr.gouv.monprojetsup.app.tools.server.ResponseHeader;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetSuggestionsService extends MyService<GetSuggestionsService.Request, GetSuggestionsService.Response> {


    public GetSuggestionsService() {
        super(Request.class);
    }

    public record Request(
            @NotNull ProfileDTO profile
    ) {

    }

    public record Response(
            ResponseHeader header,
            SuggestionsDTO suggestions
    ) {

        public Response(@NotNull Suggestions suggestions) {
            this(
                    new ResponseHeader(),
                    new SuggestionsDTO(suggestions)
            );
        }
    }


    @Override
    protected @NotNull Response handleRequest(@NotNull GetSuggestionsService.Request req) throws Exception {

        //LOGGER.info("HAndling request " + req);
        final @NotNull Suggestions suggestions = AlgoSuggestions.getSuggestions(
                req.profile,
                SuggestionServer.getConfig().getSuggFilConfig()
        );

        return new GetSuggestionsService.Response(suggestions);
    }


    public record SuggestionsDTO(List<Suggestion> suggestions) {

        public SuggestionsDTO(Suggestions suggestions) {
            this(suggestions.suggestions().stream()
                    .map(s -> new Suggestion(s))
                    .toList()
            );
        }

        public record Suggestion(
                String fl
        ) {
            public Suggestion(fr.gouv.monprojetsup.suggestions.algos.Suggestion s) {
                this(s.fl());
            }
        }
    }

    public String checkHealth() {
        return "OK" + "\n" + AlgoSuggestions.getStats();
    }
}
