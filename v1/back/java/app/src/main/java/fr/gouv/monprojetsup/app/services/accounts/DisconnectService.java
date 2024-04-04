package fr.gouv.monprojetsup.app.services.accounts;

import fr.gouv.monprojetsup.app.server.MyService;
import fr.gouv.monprojetsup.common.server.ResponseHeader;
import fr.gouv.monprojetsup.app.auth.Authenticator;
import fr.gouv.monprojetsup.app.log.Log;
import fr.gouv.monprojetsup.common.server.Server;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import static fr.gouv.monprojetsup.app.db.DB.authenticator;

@Service
public class DisconnectService extends MyService<Server.BasicRequest, DisconnectService.Response> {

    public DisconnectService() {
        super(Server.BasicRequest.class);
    }


    public record Response(
            ResponseHeader header
    ) {
        public Response() { this(new ResponseHeader()); }
    }

    @Override
    protected @NotNull Response handleRequest(@NotNull Server.BasicRequest req) throws Exception {
        authenticator.tokenAuthenticate(req.login(), req.token());
        Log.logTrace(req.login(), "logout");
        try {
            authenticator.logout(req.login(), req.token());
        } catch (Authenticator.TokenInvalidException | Authenticator.SessionExpiredException e) {
            // ignore
        }
        return new Response();
    }

}
