package fr.gouv.monprojetsup.app.services.profiles;

import fr.gouv.monprojetsup.app.db.DB;
import fr.gouv.monprojetsup.app.dto.ProfileDb;
import fr.gouv.monprojetsup.app.server.MyService;
import fr.gouv.monprojetsup.app.server.WebServer;
import fr.gouv.monprojetsup.app.services.info.RechercheService;
import fr.gouv.monprojetsup.common.server.Helpers;
import fr.gouv.monprojetsup.common.server.Server;
import fr.gouv.monprojetsup.data.dto.ProfileDTO;
import fr.gouv.monprojetsup.data.dto.SuggestionDTO;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static fr.gouv.monprojetsup.app.services.info.RechercheService.getDetails;

@Service
public class GetMySelectionService extends MyService<Server.BasicRequest, RechercheService.Response> {

    public GetMySelectionService() {
        super(Server.BasicRequest.class);
    }


    @Override
    protected @NotNull RechercheService.Response handleRequest(@NotNull Server.BasicRequest req) throws Exception {
        DB.authenticator.tokenAuthenticate(req.login(), req.token());

        ProfileDTO profile = WebServer.db().getProfile(req.login()).toDto();

        Helpers.LOGGER.info("Serving profile with login: '" + req.login() + "'");

        final @NotNull List<RechercheService.ResultatRecherche> suggestions = getDetails(
                profile,
                profile.suggApproved().stream().map(SuggestionDTO::fl).toList(),
                Map.of()
        );

        return new RechercheService.Response(suggestions);
    }



}
