package fr.gouv.monprojetsup.app.services.admin;

import fr.gouv.monprojetsup.app.tools.server.Helpers;
import fr.gouv.monprojetsup.app.tools.server.MyService;
import fr.gouv.monprojetsup.app.db.DB;
import fr.gouv.monprojetsup.app.db.DBExceptions;
import fr.gouv.monprojetsup.app.dto.AdminInfosDTO;
import fr.gouv.monprojetsup.app.server.WebServer;
import fr.gouv.monprojetsup.app.services.teacher.GetAdminInfosService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class DeleteUserHandler extends MyService<DeleteUserHandler.Request, GetAdminInfosService.Response> {

    public DeleteUserHandler() {
        super(Request.class);
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

    @Override
    public @NotNull GetAdminInfosService.Response handleRequest(@NotNull Request req) throws Exception {
        if (!DB.authenticator.checkIsSuperUser(req.login(), req.token())) {
            throw new DBExceptions.UserInputException.ForbiddenException();
        }
        Helpers.LOGGER.info("Deleting user'" + req.user() + "'");
        WebServer.db().deleteUser(req.user);
        final AdminInfosDTO updatedInfos = WebServer.db().getAdminInfos(req.login);
        return new GetAdminInfosService.Response(
                        updatedInfos,
                        "L'utilisateur '" + req.user + "' a été supprimé."
                );
    }


}
