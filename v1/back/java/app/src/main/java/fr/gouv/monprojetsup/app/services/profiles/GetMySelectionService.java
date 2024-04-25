package fr.gouv.monprojetsup.app.services.profiles;

import fr.gouv.monprojetsup.app.db.DB;
import fr.gouv.monprojetsup.app.server.MyService;
import fr.gouv.monprojetsup.app.server.WebServer;
import fr.gouv.monprojetsup.app.services.info.SearchService;
import fr.gouv.monprojetsup.common.server.Helpers;
import fr.gouv.monprojetsup.common.server.Server;
import fr.gouv.monprojetsup.data.dto.ProfileDTO;
import fr.gouv.monprojetsup.data.dto.SuggestionDTO;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.app.services.info.SearchService.getDetails;

@Service
public class GetMySelectionService extends MyService<Server.BasicRequest, SearchService.Response> {

    public GetMySelectionService() {
        super(Server.BasicRequest.class);
    }


    @Override
    protected @NotNull SearchService.Response handleRequest(@NotNull Server.BasicRequest req) throws Exception {
        DB.authenticator.tokenAuthenticate(req.login(), req.token());

        ProfileDTO profile = WebServer.db().getProfile(req.login()).toDto();

        Helpers.LOGGER.info("Serving profile with login: '" + req.login() + "'");

        List<String> keys = profile.suggApproved().stream().map(SuggestionDTO::fl).toList();

        final @NotNull List<SearchService.ResultatRecherche> suggestions = getDetails(
                profile,
                keys,
                keys.stream().collect(Collectors.toMap(k -> k, k -> 1.0)),
                Map.of());

        return new SearchService.Response(suggestions);
    }



}
