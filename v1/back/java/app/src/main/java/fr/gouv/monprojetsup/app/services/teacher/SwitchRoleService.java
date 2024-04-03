package fr.gouv.monprojetsup.app.services.teacher;

import fr.gouv.monprojetsup.app.server.MyService;
import fr.gouv.monprojetsup.common.server.ResponseHeader;
import fr.gouv.monprojetsup.app.db.DB;
import fr.gouv.monprojetsup.app.db.DBExceptions;
import fr.gouv.monprojetsup.app.server.WebServer;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class SwitchRoleService extends MyService<SwitchRoleService.Request, MyService.BasicResponse> {

    public SwitchRoleService() {
        super(Request.class);
    }

    public record Request(
            @NotNull String login,
            @NotNull String token,

            @NotNull String role
    ) {
    }

    @Override
    public @NotNull MyService.BasicResponse handleRequest(Request req) throws Exception {
        DB.authenticator.tokenAuthenticate(req.login(), req.token());

        String role = req.role;
        if(role == null || (!role.equals("pp") && !role.equals("lyceen"))) {
            throw new DBExceptions.UserInputException.ForbiddenException();
        }

        boolean pp = role.equals("pp");
        //pp means back to default
        WebServer.db().switchTemporarilyToLyceenUserType(req.login, !pp);

        String userMessage = pp ? "Vous êtes repassé en mode référent"
                : "Vous êtes temporairement passé en mode lycéen.";

        return new MyService.BasicResponse(
                new ResponseHeader(userMessage)
        );

    }

}
