package fr.gouv.monprojetsup.app.services.admin;

import fr.gouv.monprojetsup.app.db.DBExceptions;
import fr.gouv.monprojetsup.app.server.WebServer;
import fr.gouv.monprojetsup.app.db.DB;
import fr.gouv.monprojetsup.app.dto.AdminInfosDTO;
import fr.gouv.monprojetsup.app.services.teacher.GetAdminInfosService;
import fr.gouv.monprojetsup.app.tools.server.Helpers;
import fr.gouv.monprojetsup.app.server.MyService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import static fr.gouv.monprojetsup.app.tools.server.Helpers.NULL_DATA;

@Service
public class CreateGroupHandler extends MyService<CreateGroupHandler.Request, GetAdminInfosService.Response> {

    public CreateGroupHandler() {
        super(Request.class);
    }

    public record Request(
            @NotNull String login,
            @NotNull String token,

            /**
             * lycee and classe of the new group to create
              */
            String lycee,
            String classe,

            /**
             * id of the group to delete
             */
            String group,

            /* can be create or delete */
            @NotNull String op
    ) {
    }



    @Override
    public @NotNull GetAdminInfosService.Response handleRequest(@NotNull Request req) throws Exception {
        if(!DB.authenticator.checkIsSuperUser(req.login(), req.token())) {
            throw new DBExceptions.UserInputException.ForbiddenException();
        }
        Helpers.LOGGER.info("Creating group'" + req.classe() + " in lycee " + req.lycee);
        final AdminInfosDTO infos;
        if(req.op().equals("create")) {
            if(req.lycee == null || req.classe == null) throw new RuntimeException(NULL_DATA);
            WebServer.db().createNewGroup(req.lycee, req.classe);
            infos = WebServer.db().getAdminInfos(req.login);
            return new GetAdminInfosService.Response(infos,
                    "Le groupe '" + req.classe() + " in lycee " + req.lycee +  "' a été créé."
            );
        } else if(req.op().equals("delete")) {
            if(req.group == null) throw new RuntimeException(NULL_DATA);
            WebServer.db().deleteGroup(req.group);
            infos = WebServer.db().getAdminInfos(req.login);
            return new GetAdminInfosService.Response(infos,
                    "Le groupe '" + req.group() +  "' a été supprimé."
            );
        } else {
            throw new RuntimeException("op non reconnue '" + req.op + "'");
        }
    }

}
