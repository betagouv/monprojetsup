package fr.gouv.monprojetsup.suggestions.services;

import fr.gouv.monprojetsup.common.dto.ProfileDTO;
import fr.gouv.monprojetsup.common.server.ResponseHeader;
import fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions;
import fr.gouv.monprojetsup.suggestions.server.SuggestionServer;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GetFormationsAffinitiesService extends MyService<GetFormationsAffinitiesService.Request, GetFormationsAffinitiesService.Response> {


    public GetFormationsAffinitiesService() {
        super(Request.class);
    }

    public record Request(

            @Schema(name = "profile", description = "Profil utilisé pour évaluer l'affinité.", required = true)
            @NotNull ProfileDTO profile

    ) {

    }

    public record Response(
            ResponseHeader header,
            @Schema(
                    name = "affinites",
                    description =
                            """
                               Associe à chaque formation représentée par sa clé un score entre 0.0 et 1.0.
                               Précision 6 décimales. 
                               Les clés apparaissant dans la corbeille du profil sont exclues du résultat.
                               Les scores nuls sont exclus du résultat.
                               """,
                    required = true
            )
            Map<String,Double> affinites
    ) {

        public Response(@NotNull Map<String,Double> affinites) {
            this(
                    new ResponseHeader(),
                    affinites
            );
        }

    }


    @Override
    protected @NotNull Response handleRequest(@NotNull GetFormationsAffinitiesService.Request req) throws Exception {

        //LOGGER.info("HAndling request " + req);
        final @NotNull Map<String,Double> affinites = AlgoSuggestions.getFormationsAffinities(
                req.profile,
                SuggestionServer.getConfig().getSuggFilConfig()
        );

        return new GetFormationsAffinitiesService.Response(affinites);
    }

}
