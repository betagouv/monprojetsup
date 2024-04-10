package fr.gouv.monprojetsup.suggestions.services;

import fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions;
import fr.gouv.monprojetsup.suggestions.algos.Explanation;
import fr.gouv.monprojetsup.suggestions.server.SuggestionServer;
import fr.gouv.monprojetsup.common.server.ResponseHeader;
import fr.gouv.monprojetsup.common.dto.ProfileDTO;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetExplanationsAndExamplesService extends MyService<GetExplanationsAndExamplesService.Request, GetExplanationsAndExamplesService.Response> {

    public GetExplanationsAndExamplesService() {
        super(Request.class);
    }

    public record Request(
        @NotNull ProfileDTO profile,
        @ArraySchema( arraySchema = @Schema(description = "clé de la formation", example = "[\"fl2014\",\"fl2015\"]"))
        @NotNull List<String> keys
    ) {
    }

    public record Response(
            @NotNull ResponseHeader header,

            @ArraySchema( arraySchema = @Schema(description = "liste des résultats", allOf = AlgoSuggestions.ExplanationAndExamples.class))
            @NotNull List<AlgoSuggestions.ExplanationAndExamples> liste
    ) {
        public Response(
                List<AlgoSuggestions.ExplanationAndExamples> liste
        ) {
            this(new ResponseHeader(), liste);
        }

    }


    @Override
    protected @NotNull Response handleRequest(@NotNull Request req) throws Exception {
        List<AlgoSuggestions.ExplanationAndExamples> eae
                = AlgoSuggestions.getExplanationsAndExamples(
                        req.profile,
                        req.keys,
                        SuggestionServer.getConfig().getSuggFilConfig()
        );
        return new Response( eae);
    }


}
