package fr.gouv.monprojetsup.web.services.accounts;

import fr.gouv.monprojetsup.tools.server.MyService;
import fr.gouv.monprojetsup.tools.server.ResponseHeader;
import fr.gouv.monprojetsup.web.db.DBExceptions;
import fr.gouv.monprojetsup.web.log.Log;
import fr.gouv.monprojetsup.web.mail.AccountManagementEmails;
import fr.gouv.monprojetsup.web.server.WebServer;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import static fr.gouv.monprojetsup.tools.server.Helpers.LOGGER;
import static fr.gouv.monprojetsup.web.db.model.User.normalizeUser;

@Service
public class SendResetPasswordEmailService extends MyService<SendResetPasswordEmailService.Request, SendResetPasswordEmailService.Response> {

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
                LOGGER.info("sending reset password email to " + login +  " token " + confirmationToken);
            } catch (DBExceptions.UnknownUserException e) {
                LOGGER.info("failed to send reset password email " + e.getMessage());
            }
        }
        return new Response();
    }


}
