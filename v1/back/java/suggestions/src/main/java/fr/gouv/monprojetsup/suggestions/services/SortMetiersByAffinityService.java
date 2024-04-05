package fr.gouv.monprojetsup.suggestions.services;

import fr.gouv.monprojetsup.common.dto.ProfileDTO;
import fr.gouv.monprojetsup.common.server.ResponseHeader;
import fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions;
import fr.gouv.monprojetsup.suggestions.server.SuggestionServer;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SortMetiersByAffinityService extends MyService<SortMetiersByAffinityService.Request, SortMetiersByAffinityService.Response> {


    public SortMetiersByAffinityService() {
        super(Request.class);
    }

    public record Request(

            @Schema(name = "profile", description = "Profil utilisé pour trier les métiers.", required = true)
            @NotNull ProfileDTO profile,

            @ArraySchema(arraySchema = @Schema(name = "keys", example = "[\"MET_450\",\"MET_883\",\"MET_77\"]",description = "Liste des clés métiers dont le tri est attendu.", required = true))
            @NotNull List<String> keys

    ) {

    }

    public record Response(
            ResponseHeader header,
            @Schema(
                    name = "clesTriees",
                    description = "Clés triées par ordre décroissant d'affinité. Le meilleur métier en premier. Les métiers apparaissant dans les favoris ou la corbeille du profil sont exclus du résultat",
                    required = true
            )
            @NotNull List<String> clesTriees
    ) {

        public Response(@NotNull List<String> clesTriees) {
            this(
                    new ResponseHeader(),
                    clesTriees
            );
        }
    }


    @Override
    protected @NotNull Response handleRequest(@NotNull SortMetiersByAffinityService.Request req) throws Exception {

        //LOGGER.info("HAndling request " + req);
        final @NotNull List<String> clesTriees = AlgoSuggestions.sortMetiersByAffinites(
                req.profile,
                req.keys,
                SuggestionServer.getConfig().getSuggFilConfig()
        );

        return new SortMetiersByAffinityService.Response(clesTriees);
    }

}
