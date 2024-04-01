package fr.gouv.monprojetsup.app.services.profiles;

import fr.gouv.monprojetsup.app.tools.server.Helpers;
import fr.gouv.monprojetsup.app.tools.server.MyService;
import fr.gouv.monprojetsup.app.tools.server.ResponseHeader;
import fr.gouv.monprojetsup.app.db.DB;
import fr.gouv.monprojetsup.app.dto.ProfileDTO;
import fr.gouv.monprojetsup.app.server.WebServer;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class GetMyProfileService extends MyService<MyService.BasicRequest, GetMyProfileService.Response> {

    public GetMyProfileService() {
        super(MyService.BasicRequest.class);
    }


    public record Response(
            ResponseHeader header,
            ProfileDTO profile
    ) {
        public Response(ProfileDTO profile) {
            this(new ResponseHeader(), profile);
        }
    }


    @Override
    protected @NotNull Response handleRequest(@NotNull MyService.BasicRequest req) throws Exception {
        DB.authenticator.tokenAuthenticate(req.login(), req.token());

        ProfileDTO profile = WebServer.db().getProfile(req.login());

        Helpers.LOGGER.info("Serving profile with login: '" + req.login() + "'");

        return new Response(profile.sanitize());
    }



}
