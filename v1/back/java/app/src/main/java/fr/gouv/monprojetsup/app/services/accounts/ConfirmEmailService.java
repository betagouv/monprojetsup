package fr.gouv.monprojetsup.app.services.accounts;

import fr.gouv.monprojetsup.app.server.MyService;
import fr.gouv.monprojetsup.common.server.ResponseHeader;
import fr.gouv.monprojetsup.app.log.Log;
import fr.gouv.monprojetsup.app.mail.AccountManagementEmails;
import fr.gouv.monprojetsup.app.server.WebServer;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class ConfirmEmailService extends MyService<ConfirmEmailService.Request, ConfirmEmailService.Response> {

    public ConfirmEmailService() {
        super(Request.class);
    }

    public record Request(
            @NotNull String email,
            @NotNull String token
    ) {
    }

    /*
    A response with a null or empty token means that the account is being moderated
     */
    public record Response(
            @NotNull ResponseHeader header,
            @NotNull String message
    ) {
        public Response(String message) {
            this(new ResponseHeader(), message);
        }
    }

    @Override
    protected @NotNull Response handleRequest(@NotNull Request req) throws Exception {
        Log.logTrace(req.email, "confirm email");

        //failure leads to exception
        Pair<Boolean, String> msg = WebServer.db().validateAccount(req.email, req.token);

        /* send email to user */
        AccountManagementEmails.sendEmailAddressConfirmedEmail(req.email, msg.getRight());

        /* inform admin that the account is ready to be confirmed*/
        boolean isActivated = msg.getLeft();
        String activationMessage = msg.getRight();
        if(!isActivated) {
            AccountManagementEmails.informSupport(req.email, activationMessage);
        }

        return new Response(msg.getRight());
    }

}
