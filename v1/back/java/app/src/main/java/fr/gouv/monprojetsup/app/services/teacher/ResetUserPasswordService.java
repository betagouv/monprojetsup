package fr.gouv.monprojetsup.app.services.teacher;

import fr.gouv.monprojetsup.app.server.WebServer;
import fr.gouv.monprojetsup.app.db.DB;
import fr.gouv.monprojetsup.app.tools.server.Helpers;
import fr.gouv.monprojetsup.app.tools.server.MyService;
import fr.gouv.monprojetsup.app.tools.server.ResponseHeader;
import fr.gouv.monprojetsup.app.db.DBExceptions;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import static fr.gouv.monprojetsup.app.db.model.User.normalizeUser;

@Service
public class ResetUserPasswordService extends MyService<ResetUserPasswordService.Request, ResetUserPasswordService.Response> {

    public ResetUserPasswordService() {
        super(Request.class);
    }

    public static String resetUserPassword(String user) throws DBExceptions.UnknownUserException, NoSuchAlgorithmException, InvalidKeySpecException {
        user = normalizeUser(user);
        StringBuilder sb = new StringBuilder();
        for (int i = 33; i <= 126; i++) {
            char c = (char) i;
            if (c != '0' && c != 'O' && c != 'o' && c != '`' && c != '\'' && c != 'l' && c != '^') {
                sb.append(c);
            }
        }
        char[] tchars = sb.toString().toCharArray();

        String password = RandomStringUtils.random(12, 0, tchars.length - 1, false, false, tchars, new SecureRandom());
        WebServer.db().resetUserPassword(user, password);
        return password;
    }

    public record Request(
            @NotNull String login,
            @NotNull String token,

            /**
             * name of the new group
              */
            @NotNull String user
    ) {
    }

    public record Response(
            ResponseHeader header,
            String password
    ) {

        public Response(String userMsg, String password) { this(new ResponseHeader(userMsg), password); }
    }

    @Override
    protected @NotNull Response handleRequest(Request req) throws Exception {
        DB.authenticator.tokenAuthenticate(req.login, req.token);

        if(!req.login.equals(req.user)) {
            boolean isGroupAdmin = WebServer.db().isAdminOfUser(req.login(), req.user);
            if (!isGroupAdmin) {
                throw new DBExceptions.UserInputException.ForbiddenException();
            }
        }

        Helpers.LOGGER.info("Resetting  user password'" + req.user() + "'");
        String password = resetUserPassword(req.user);
        return new Response("Le mot de passe de l'utilisateur '" + req.user + "' a été réinitialisé.", password);

    }


}
