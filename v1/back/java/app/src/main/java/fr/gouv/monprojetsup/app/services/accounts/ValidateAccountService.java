package fr.gouv.monprojetsup.app.services.accounts;

import fr.gouv.monprojetsup.app.db.DBExceptions;
import fr.gouv.monprojetsup.app.db.model.User;
import fr.gouv.monprojetsup.app.server.MyAppService;
import fr.gouv.monprojetsup.app.server.WebServer;
import fr.gouv.monprojetsup.data.dto.ResponseHeader;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class ValidateAccountService extends MyAppService<ValidateAccountService.Request, ValidateAccountService.Response> {

    public ValidateAccountService() {
        super(Request.class);
    }

    public record ValidateAccountRequest(
            @NotNull User.UserTypes type,
            @NotNull String accesGroupe
    ) {

    }

    public record Request(
            @NotNull ValidateAccountRequest data
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
        boolean ok = true;
        try {
            WebServer.db().validateCode(req.data.type, req.data.accesGroupe);
        } catch (DBExceptions.UserInputException.WrongAccessCodeException ignored) {
            ok = false;
        }
        return new Response(ok || WebServer.config().isNoAuthentificationMode());
    }


}
