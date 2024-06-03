package fr.gouv.monprojetsup.suggestions.services;

import fr.gouv.monprojetsup.data.dto.GetAffinitiesServiceDTO;
import fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions;
import fr.gouv.monprojetsup.suggestions.server.SuggestionServer;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAffinitiesService extends MyService<GetAffinitiesServiceDTO.Request, GetAffinitiesServiceDTO.Response> {


    public GetAffinitiesService() {
        super(GetAffinitiesServiceDTO.Request.class);
    }

    @Override
    protected @NotNull GetAffinitiesServiceDTO.Response handleRequest(@NotNull GetAffinitiesServiceDTO.Request req) {

        //LOGGER.info("HAndling request " + req);
        final @NotNull List<Pair<String,Double>> affinites = AlgoSuggestions.getFormationsAffinities(
                req.profile(),
                SuggestionServer.getConfig().getSuggFilConfig()
        ).stream().map(p -> Pair.of(p.getLeft(), p.getRight().affinite())).toList();

        List<String> metiers = AlgoSuggestions.sortMetiersByAffinites(
                req.profile(),
                null,
                SuggestionServer.getConfig().getSuggFilConfig()
        );

        return new GetAffinitiesServiceDTO.Response(affinites, metiers);
    }

}
