package fr.gouv.monprojetsup.app.services.profiles;

import fr.gouv.monprojetsup.app.db.DB;
import fr.gouv.monprojetsup.app.server.Helpers;
import fr.gouv.monprojetsup.app.server.MyAppService;
import fr.gouv.monprojetsup.app.server.Server;
import fr.gouv.monprojetsup.app.server.WebServer;
import fr.gouv.monprojetsup.app.services.info.SearchService;
import fr.gouv.monprojetsup.suggestions.dto.ProfileDTO;
import fr.gouv.monprojetsup.suggestions.dto.SuggestionDTO;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GetMySelectionService extends MyAppService<Server.BasicRequest, SearchService.Response> {

    public GetMySelectionService() {
        super(Server.BasicRequest.class);
    }


    @Autowired
    SearchService searchService;

    @Override
    protected @NotNull SearchService.Response handleRequest(@NotNull Server.BasicRequest req) throws Exception {
        DB.authenticator.tokenAuthenticate(req.login(), req.token());

        val pf = WebServer.db().getProfile(req.login()).sanitize();
        ProfileDTO profile = pf.toDto();

        Helpers.LOGGER.info("Serving profile with login: '" + req.login() + "'");

        List<String> keys = profile.suggApproved().stream().map(SuggestionDTO::fl).toList();

        final @NotNull List<SearchService.ResultatRecherche> suggestions = searchService.getDetails(
                profile,
                keys,
                keys.stream().collect(Collectors.toMap(k -> k, k -> 1.0)),
                Map.of());

        SearchService.injectRetours(suggestions, pf.retours());

        return new SearchService.Response(suggestions);
    }


}
