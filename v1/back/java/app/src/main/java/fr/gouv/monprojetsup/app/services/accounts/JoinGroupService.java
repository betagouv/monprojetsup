package fr.gouv.monprojetsup.app.services.accounts;

import fr.gouv.monprojetsup.app.db.DB;
import fr.gouv.monprojetsup.app.db.DBExceptions;
import fr.gouv.monprojetsup.app.server.MyAppService;
import fr.gouv.monprojetsup.app.server.WebServer;
import fr.gouv.monprojetsup.app.server.ResponseHeader;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class JoinGroupService extends MyAppService<JoinGroupService.Request, JoinGroupService.Response> {

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
            boolean ok,
            boolean wasAlreadyInGroup,
            String groupName
    ) {
        public Response(boolean ok, boolean wasAlreadyInGroup, String groupName) {
            this(new ResponseHeader(), ok, wasAlreadyInGroup, groupName);
        }
    }

    @Override
    protected @NotNull Response handleRequest(@NotNull Request req) throws Exception {

        DB.authenticator.tokenAuthenticate(req.login(), req.token());
        boolean ok = true;
        try {
            val result = WebServer.db().joinGroup(req.login(), req.accessCode);
            return new Response(true, result.getLeft(), result.getRight());
        } catch (DBExceptions.UserInputException.WrongAccessCodeException ignored) {
            return new Response(false, false, null);
        }
    }


}
