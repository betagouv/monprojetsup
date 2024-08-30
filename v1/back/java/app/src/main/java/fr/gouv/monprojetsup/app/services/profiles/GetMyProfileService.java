package fr.gouv.monprojetsup.app.services.profiles;

import fr.gouv.monprojetsup.app.db.DB;
import fr.gouv.monprojetsup.app.dto.ProfileDb;
import fr.gouv.monprojetsup.app.server.*;
import fr.gouv.monprojetsup.app.server.ResponseHeader;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class GetMyProfileService extends MyAppService<Server.BasicRequest, GetMyProfileService.Response> {

    public GetMyProfileService() {
        super(Server.BasicRequest.class);
    }


    public record Response(
            ResponseHeader header,
            ProfileDb profile
    ) {
        public Response(ProfileDb profile) {
            this(new ResponseHeader(), profile);
        }
    }


    @Override
    protected @NotNull Response handleRequest(@NotNull Server.BasicRequest req) throws Exception {
        DB.authenticator.tokenAuthenticate(req.login(), req.token());

        ProfileDb profile = WebServer.db().getProfile(req.login());

        Helpers.LOGGER.info("Serving profile with login: '" + req.login() + "'");

        return new Response(profile.sanitize());
    }



}
