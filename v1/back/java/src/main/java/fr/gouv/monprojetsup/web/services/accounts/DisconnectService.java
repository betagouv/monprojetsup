package fr.gouv.monprojetsup.web.services.accounts;

import fr.gouv.monprojetsup.tools.server.MyService;
import fr.gouv.monprojetsup.tools.server.ResponseHeader;
import fr.gouv.monprojetsup.web.auth.Authenticator;
import fr.gouv.monprojetsup.web.log.Log;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import static fr.gouv.monprojetsup.web.db.DB.authenticator;

@Service
public class DisconnectService extends MyService<MyService.BasicRequest, DisconnectService.Response> {

    public DisconnectService() {
        super(MyService.BasicRequest.class);
    }


    public record Response(
            ResponseHeader header
    ) {
        public Response() { this(new ResponseHeader()); }
    }

    @Override
    protected @NotNull Response handleRequest(@NotNull MyService.BasicRequest req) throws Exception {
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
