package fr.gouv.monprojetsup.suggestions.services;

import fr.gouv.monprojetsup.data.dto.SortMetiersByAffinityServiceDTO;
import fr.gouv.monprojetsup.suggestions.algos.AlgoSuggestions;
import fr.gouv.monprojetsup.suggestions.server.SuggestionServer;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SortMetiersByAffinityService extends MyService<SortMetiersByAffinityServiceDTO.Request, SortMetiersByAffinityServiceDTO.Response> {


    public SortMetiersByAffinityService() {
        super(SortMetiersByAffinityServiceDTO.Request.class);
    }


    @Override
    protected @NotNull SortMetiersByAffinityServiceDTO.Response handleRequest(@NotNull SortMetiersByAffinityServiceDTO.Request req) throws Exception {

        //LOGGER.info("HAndling request " + req);
        final @NotNull List<String> clesTriees = AlgoSuggestions.sortMetiersByAffinites(
                req.profile(),
                req.keys(),
                SuggestionServer.getConfig().getSuggFilConfig()
        );

        return new SortMetiersByAffinityServiceDTO.Response(clesTriees);
    }

}
