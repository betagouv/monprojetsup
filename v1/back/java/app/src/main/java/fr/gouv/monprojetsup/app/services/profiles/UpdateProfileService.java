package fr.gouv.monprojetsup.app.services.profiles;

import fr.gouv.monprojetsup.app.db.DB;
import fr.gouv.monprojetsup.app.dto.ProfileDb;
import fr.gouv.monprojetsup.app.dto.ProfileUpdateDTO;
import fr.gouv.monprojetsup.app.log.Log;
import fr.gouv.monprojetsup.app.server.MyAppService;
import fr.gouv.monprojetsup.app.server.WebServer;
import fr.gouv.monprojetsup.app.server.ResponseHeader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;


@Service
public class UpdateProfileService extends MyAppService<UpdateProfileService.Request, UpdateProfileService.Response> {

    public UpdateProfileService() {
        super(Request.class);
    }

    public record Request(
            String login,
            String token,
            @Nullable ProfileUpdateDTO update,

            @Nullable ProfileDb profile

    ) {
    }

    public record Response(
            ResponseHeader header
    ) {
        public Response() { this(new ResponseHeader()); }
    }



    @Override
    protected @NotNull Response handleRequest(@NotNull Request req) throws Exception {
        DB.authenticator.tokenAuthenticate(req.login(), req.token);
        //DB.addTrace(req.profile.login() + " updating profile");

        if(req.update != null) {
            Log.logTrace(req.login(), UpdateProfileService.class.getSimpleName(), req.update);
            WebServer.db().updateProfile(req.login(), req.update());
        }
        if(req.profile != null) {
            Log.logTrace(req.login(), UpdateProfileService.class.getSimpleName(), req.profile);
            WebServer.db().updateProfile(req.login(), req.profile());
        }
        return new Response();
    }



}
