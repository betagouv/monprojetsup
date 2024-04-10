package fr.gouv.monprojetsup.suggestions.services;

import fr.gouv.monprojetsup.common.dto.ProfileDTO;
import fr.gouv.monprojetsup.common.server.ResponseHeader;
import fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions;
import fr.gouv.monprojetsup.suggestions.algos.Explanation;
import fr.gouv.monprojetsup.suggestions.server.SuggestionServer;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetFormationsAffinitiesService extends MyService<GetFormationsAffinitiesService.Request, GetFormationsAffinitiesService.Response> {


    public GetFormationsAffinitiesService() {
        super(Request.class);
    }

    public record Request(

            @Schema(name = "profile", description = "Profil utilisé pour évaluer l'affinité.")
            @NotNull ProfileDTO profile

    ) {

    }

    public record Response(
            ResponseHeader header,
            @Schema(
                    name = "affinites",
                    description =
                            """
                               Renvoie la liste des formations dans l'ordre d'affichage, ainsi que le score d'affinité, entre 0.0 et 1.0.
                               Précision 6 décimales. 
                               """,
                    required = true
            )
            List<Affinity> affinites
    ) {

        public Response(@NotNull List<Pair<String, Double>> affinites) {
            this(
                    new ResponseHeader(),
                    affinites.stream()
                            .map(p -> new Affinity(p.getLeft(), p.getRight()))
                            .toList()
            );
        }

    }

    public record Affinity(
            String key,
            double affinite

    ) {
    }

    @Override
    protected @NotNull Response handleRequest(@NotNull GetFormationsAffinitiesService.Request req) throws Exception {

        //LOGGER.info("HAndling request " + req);
        final @NotNull List<Pair<String,Double>> affinites = AlgoSuggestions.getFormationsAffinities(
                req.profile,
                SuggestionServer.getConfig().getSuggFilConfig()
        );

        return new GetFormationsAffinitiesService.Response(affinites);
    }

}
