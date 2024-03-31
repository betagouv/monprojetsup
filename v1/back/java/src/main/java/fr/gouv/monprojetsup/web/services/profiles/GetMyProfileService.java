package fr.gouv.monprojetsup.web.services.profiles;

import fr.gouv.monprojetsup.tools.server.Helpers;
import fr.gouv.monprojetsup.tools.server.MyService;
import fr.gouv.monprojetsup.tools.server.ResponseHeader;
import fr.gouv.monprojetsup.web.db.DB;
import fr.gouv.monprojetsup.web.dto.ProfileDTO;
import fr.gouv.monprojetsup.web.server.WebServer;
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
