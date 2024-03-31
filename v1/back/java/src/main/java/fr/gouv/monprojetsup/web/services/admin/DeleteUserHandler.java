package fr.gouv.monprojetsup.web.services.admin;

import fr.gouv.monprojetsup.tools.server.Helpers;
import fr.gouv.monprojetsup.tools.server.MyService;
import fr.gouv.monprojetsup.web.db.DB;
import fr.gouv.monprojetsup.web.db.DBExceptions;
import fr.gouv.monprojetsup.web.dto.AdminInfosDTO;
import fr.gouv.monprojetsup.web.server.WebServer;
import fr.gouv.monprojetsup.web.services.teacher.GetAdminInfosService;
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
