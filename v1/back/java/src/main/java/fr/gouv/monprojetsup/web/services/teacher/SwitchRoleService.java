package fr.gouv.monprojetsup.web.services.teacher;

import fr.gouv.monprojetsup.tools.server.MyService;
import fr.gouv.monprojetsup.tools.server.ResponseHeader;
import fr.gouv.monprojetsup.web.db.DB;
import fr.gouv.monprojetsup.web.db.DBExceptions;
import fr.gouv.monprojetsup.web.server.WebServer;
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
