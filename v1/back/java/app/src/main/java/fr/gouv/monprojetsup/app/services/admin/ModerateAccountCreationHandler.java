package fr.gouv.monprojetsup.app.services.admin;

import fr.gouv.monprojetsup.app.server.MyAppService;
import fr.gouv.monprojetsup.app.db.DB;
import fr.gouv.monprojetsup.app.db.DBExceptions;
import fr.gouv.monprojetsup.app.dto.AdminInfosDTO;
import fr.gouv.monprojetsup.app.mail.AccountManagementEmails;
import fr.gouv.monprojetsup.app.server.WebServer;
import fr.gouv.monprojetsup.app.services.teacher.GetAdminInfosService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class ModerateAccountCreationHandler extends MyAppService<ModerateAccountCreationHandler.Request, GetAdminInfosService.Response> {

    public ModerateAccountCreationHandler() {
        super(Request.class);
    }

    public record Request(
            @NotNull String login,
            @NotNull String token,

            /**
             * name of the user whose account creation we shall accept
              */
            @NotNull String user,

            boolean accept
    ) {
    }


    @Override
    public @NotNull GetAdminInfosService.Response handleRequest(@NotNull Request req) throws Exception {
        if (!DB.authenticator.checkIsSuperUser(req.login(), req.token())) {
            throw new DBExceptions.UserInputException.ForbiddenException();
        }

        if(req.accept) {
            WebServer.db().acceptUserCreation(req.user);
            AdminInfosDTO infos = WebServer.db().getAdminInfos(req.login);
            AccountManagementEmails.sendEmailAccountCreationConfirmed(req.user);
            return new GetAdminInfosService.Response(infos,
                    "La création du compte '" + req.user + "' a été acceptée.");
        } else{
            WebServer.db().rejectUserCreation(req.user);
            AdminInfosDTO infos = WebServer.db().getAdminInfos(req.login);
            AccountManagementEmails.sendEmailAccountCreationRejected(req.user);
            return new GetAdminInfosService.Response(infos,
                    "La création du compte '" + req.user + "' a été refusée.");

        }
    }

}
