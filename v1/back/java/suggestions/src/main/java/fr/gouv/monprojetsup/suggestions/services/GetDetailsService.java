package fr.gouv.monprojetsup.suggestions.services;

import fr.gouv.monprojetsup.common.dto.ProfileDTO;
import fr.gouv.monprojetsup.common.server.ResponseHeader;
import fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions;
import fr.gouv.monprojetsup.suggestions.server.SuggestionServer;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetDetailsService extends MyService<GetDetailsService.Request, GetDetailsService.Response> {


    public GetDetailsService() {
        super(Request.class);
    }

    public record Request(

            @Schema(name = "profile", description = "Profil utilisé pour évaluer l'affinité.", required = true)
            @NotNull ProfileDTO profile,
            @Schema(name = "batchSize", description = "Nombre de résultats dans un groupe.", required = true)
            List<String> keys

    ) {

    }

    public record Response(
            ResponseHeader header,
            @Schema(
                    name = "details",
                    description =
                            """
                               Renvoie la liste des details dans l'ordre d'affichage, ainsi que le score d'affinité, entre 0.0 et 1.0 à 6 décimales,
                               des explications et des exemples.
                               Précision 6 décimales. 
                               """,
                    required = true
            )
            List<AlgoSuggestions.DetailedSuggestion> details
    ) {

        public Response(@NotNull List<AlgoSuggestions.DetailedSuggestion>  suggestions) {
            this(
                    new ResponseHeader(),
                    suggestions
            );
        }

    }

    public record Affinity(
            String key,
            double affinite

    ) {
    }

    @Override
    protected @NotNull Response handleRequest(@NotNull GetDetailsService.Request req) throws Exception {

        //LOGGER.info("HAndling request " + req);
        final @NotNull List<AlgoSuggestions.DetailedSuggestion> suggestions = AlgoSuggestions.getDetails(
                req.profile,
                SuggestionServer.getConfig().getSuggFilConfig(),
                req.keys
        );

        return new GetDetailsService.Response(suggestions);
    }

}
