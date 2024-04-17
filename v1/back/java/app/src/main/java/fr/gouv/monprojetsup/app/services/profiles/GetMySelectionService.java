package fr.gouv.monprojetsup.app.services.profiles;

import fr.gouv.monprojetsup.app.db.DB;
import fr.gouv.monprojetsup.app.dto.ProfileDTO;
import fr.gouv.monprojetsup.app.server.MyService;
import fr.gouv.monprojetsup.app.server.WebServer;
import fr.gouv.monprojetsup.app.services.info.RechercheService;
import fr.gouv.monprojetsup.common.server.Helpers;
import fr.gouv.monprojetsup.common.server.ResponseHeader;
import fr.gouv.monprojetsup.common.server.Server;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class GetMySelectionService extends MyService<Server.BasicRequest, RechercheService.Response> {

    public GetMySelectionService() {
        super(Server.BasicRequest.class);
    }


    @Override
    protected @NotNull RechercheService.Response handleRequest(@NotNull Server.BasicRequest req) throws Exception {
        DB.authenticator.tokenAuthenticate(req.login(), req.token());

        ProfileDTO profile = WebServer.db().getProfile(req.login());

        Helpers.LOGGER.info("Serving profile with login: '" + req.login() + "'");

        return new RechercheService.Response(profile.sanitize());
    }



}
