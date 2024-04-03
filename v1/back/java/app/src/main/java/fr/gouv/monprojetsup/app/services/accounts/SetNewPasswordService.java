package fr.gouv.monprojetsup.app.services.accounts;

import fr.gouv.monprojetsup.app.log.Log;
import fr.gouv.monprojetsup.app.server.WebServer;
import fr.gouv.monprojetsup.app.tools.server.MyService;
import fr.gouv.monprojetsup.app.tools.server.ResponseHeader;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class SetNewPasswordService extends MyService<SetNewPasswordService.Request, SetNewPasswordService.Response> {

    public SetNewPasswordService() {
        super(Request.class);
    }

    public record Request(
            @NotNull String email,
            @NotNull String token,
            @NotNull String newPassword
            ) {
    }

    /*
    A response with a null or empty token means that the account is being moderated
     */
    public record Response(@NotNull ResponseHeader header ) {
        public Response() {
            this(new ResponseHeader());
        }
    }


    @Override
    protected @NotNull Response handleRequest(@NotNull Request req) throws Exception {
        Log.logTrace(req.email,  SetNewPasswordService.class.getSimpleName());
        WebServer.db().setNewPasswordWithTokenFromEmail(req.email, req.token, req.newPassword);
        return new Response();
    }


}
