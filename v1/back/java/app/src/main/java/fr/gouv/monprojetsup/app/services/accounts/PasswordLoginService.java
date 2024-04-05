package fr.gouv.monprojetsup.app.services.accounts;

import fr.gouv.monprojetsup.common.server.ServerStartingException;
import fr.gouv.monprojetsup.app.server.MyService;
import fr.gouv.monprojetsup.common.server.ResponseHeader;
import fr.gouv.monprojetsup.app.db.DBExceptions;
import fr.gouv.monprojetsup.app.dto.AdminInfosDTO;
import fr.gouv.monprojetsup.app.log.Log;
import fr.gouv.monprojetsup.app.server.WebServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
public class PasswordLoginService extends MyService<PasswordLoginService.Request, PasswordLoginService.Response> {

    public PasswordLoginService() {
        super(Request.class);
    }

    public record Request(
            @NotNull String login,
            @NotNull String password

            ) {
    }


    public record LoginAnswer(
            @Nullable String login,
            @Nullable String token,
            @Nullable AdminInfosDTO infos,
            boolean validationRequired,
            @Nullable String validationMessage
    ) {

        public static LoginAnswer getAnswerWithValidationRequired(String s) {
            return new LoginAnswer(null, null,null,true,s);
        }

        public static LoginAnswer getAnswerWithValidToken(String login, String token, AdminInfosDTO adminInfos) {
            return new LoginAnswer(login, token,adminInfos,false,null);
        }
    }

    /*
    A response with a null or empty token means that the account is being moderated
     */
    public record Response(
            @NotNull ResponseHeader header,
            @NotNull PasswordLoginService.LoginAnswer data
            ) {
        public Response(LoginAnswer data) {
            this(new ResponseHeader(), data);
        }
    }

    @Override
    protected Response handleRequest(@NotNull Request req) throws Exception {
        if(WebServer.db() == null) {
            throw new ServerStartingException();
        }
        if(req.login == null || req.password == null) throw new DBExceptions.UserInputException.InvalidPasswordException();

        /*
        if(!req.login.equals("demo_anne_sophie") && !req.login.equals("hugo.gimbert@gmail.com")
                && !req.login.contains("louis")
                && !req.login.equals("hugooooo@gmail.com")) {
            throw new DBExceptions.UserInputException.MaintenanceException();
        }*/

        Log.logTrace(req.login,  " loggin in");
        LoginAnswer answer = WebServer.db().authenticate(req.login(), req.password());
        Log.logTrace(req.login,  " logged in");
        return new Response(answer);
    }

}
