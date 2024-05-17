package fr.gouv.monprojetsup.app.services.accounts;

import fr.gouv.monprojetsup.app.db.DB;
import fr.gouv.monprojetsup.app.db.DBExceptions;
import fr.gouv.monprojetsup.app.db.model.User;
import fr.gouv.monprojetsup.app.dto.ProfileDb;
import fr.gouv.monprojetsup.app.server.MyService;
import fr.gouv.monprojetsup.app.server.WebServer;
import fr.gouv.monprojetsup.common.server.ResponseHeader;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class JoinGroupService extends MyService<JoinGroupService.Request, JoinGroupService.Response> {

    public JoinGroupService() {
        super(Request.class);
    }

    public record Request(
            @NotNull String login,
            @NotNull String token,

            @NotNull String accessCode
    ) {
    }

    /*
    A response with a null or empty token means that the account is being moderated
     */
    public record Response(
            @NotNull ResponseHeader header,
            boolean ok
    ) {
        public Response(boolean ok) {
            this(new ResponseHeader(), ok);
        }
    }

    @Override
    protected @NotNull Response handleRequest(@NotNull Request req) throws Exception {

        DB.authenticator.tokenAuthenticate(req.login(), req.token());
        boolean ok = true;
        try {
            WebServer.db().joinGroup(req.login(), req.accessCode);
        } catch (DBExceptions.UserInputException.WrongAccessCodeException ignored) {
            ok = false;
        }
        return new Response(ok || WebServer.config().isNoAuthentificationMode());
    }


}
