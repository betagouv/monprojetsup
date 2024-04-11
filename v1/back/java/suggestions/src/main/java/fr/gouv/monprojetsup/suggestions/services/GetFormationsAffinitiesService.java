package fr.gouv.monprojetsup.suggestions.services;

import fr.gouv.monprojetsup.data.dto.GetFormationsAffinitiesServiceDTO;
import fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions;
import fr.gouv.monprojetsup.suggestions.server.SuggestionServer;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetFormationsAffinitiesService extends MyService<GetFormationsAffinitiesServiceDTO.Request, GetFormationsAffinitiesServiceDTO.Response> {


    public GetFormationsAffinitiesService() {
        super(GetFormationsAffinitiesServiceDTO.Request.class);
    }

    @Override
    protected @NotNull GetFormationsAffinitiesServiceDTO.Response handleRequest(@NotNull GetFormationsAffinitiesServiceDTO.Request req) throws Exception {

        //LOGGER.info("HAndling request " + req);
        final @NotNull List<Pair<String,Double>> affinites = AlgoSuggestions.getFormationsAffinities(
                req.profile(),
                SuggestionServer.getConfig().getSuggFilConfig()
        );

        return new GetFormationsAffinitiesServiceDTO.Response(affinites);
    }

}
