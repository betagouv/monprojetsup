package fr.gouv.monprojetsup.app.services.accounts;

import fr.gouv.monprojetsup.app.server.WebServer;
import fr.gouv.monprojetsup.app.db.model.User;
import fr.gouv.monprojetsup.app.server.MyService;
import fr.gouv.monprojetsup.app.tools.server.ResponseHeader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
public class CreateAccountService extends MyService<CreateAccountService.Request, CreateAccountService.Response> {

    public CreateAccountService() {
        super(Request.class);
    }

    public record CreateAccountRequest(
            @NotNull User.UserTypes type,
            @NotNull String login,
            @NotNull String password,
            @NotNull String cguVersion,
            @Nullable String accesGroupe
    ) {

    }

    public record Request(
            @NotNull CreateAccountRequest data
    ) {
    }

    /*
    A response with a null or empty token means that the account is being moderated
     */
    public record Response(
            @NotNull ResponseHeader header,
            @NotNull PasswordLoginService.LoginAnswer data
    ) {
        public Response(PasswordLoginService.LoginAnswer answer) {
            this(new ResponseHeader(), answer);
        }
    }

    @Override
    protected @NotNull Response handleRequest(@NotNull Request req) throws Exception {
        PasswordLoginService.LoginAnswer answer = WebServer.db().createAccount(req.data);
        return new Response(answer);
    }


}
