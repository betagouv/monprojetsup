package fr.gouv.monprojetsup.app.services.accounts;

import fr.gouv.monprojetsup.app.db.DBExceptions;
import fr.gouv.monprojetsup.app.log.Log;
import fr.gouv.monprojetsup.app.mail.AccountManagementEmails;
import fr.gouv.monprojetsup.app.server.MyAppService;
import fr.gouv.monprojetsup.app.server.WebServer;
import fr.gouv.monprojetsup.app.server.ResponseHeader;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import static fr.gouv.monprojetsup.app.db.model.User.normalizeUser;

@Service
@Slf4j
public class SendResetPasswordEmailService extends MyAppService<SendResetPasswordEmailService.Request, SendResetPasswordEmailService.Response> {

    public SendResetPasswordEmailService() {
        super(Request.class);
    }

    public record Request(
            @NotNull String email
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
        String login = normalizeUser(req.email);
        if (login != null) Log.logTrace(login, SendResetPasswordEmailService.class.getSimpleName());
        if(WebServer.db().existsUserWithLogin(login)) {
            try {
                @NotNull String confirmationToken = WebServer.db().generateUserEmailResetToken(login);
                AccountManagementEmails.sendResetPasswordEmail(login, confirmationToken);
                Log.logTrace("back", "sending reset password email to " + login +  " token " + confirmationToken);
            } catch (DBExceptions.UnknownUserException e) {
                Log.logTrace("back", "failed to send reset password email " + e.getMessage());
            }
        }
        return new Response();
    }


}
