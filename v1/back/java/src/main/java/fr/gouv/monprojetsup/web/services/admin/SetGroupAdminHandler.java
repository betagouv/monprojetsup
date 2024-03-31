package fr.gouv.monprojetsup.web.services.admin;

import fr.gouv.monprojetsup.tools.server.MyService;
import fr.gouv.monprojetsup.web.db.DB;
import fr.gouv.monprojetsup.web.db.DBExceptions;
import fr.gouv.monprojetsup.web.dto.AdminInfosDTO;
import fr.gouv.monprojetsup.web.server.WebServer;
import fr.gouv.monprojetsup.web.services.teacher.GetAdminInfosService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class SetGroupAdminHandler extends MyService<SetGroupAdminHandler.Request, GetAdminInfosService.Response> {

    public SetGroupAdminHandler() {
        super(Request.class);
    }

    /*    {
      groupId: group,
      groupAdminLogin: name,
      addAdmin: add,
    },
*/
    public record Request(
            @NotNull String login,
            @NotNull String token,

            /**
             * lycee and classe of the new group to create
             */
            @NotNull String groupId,
            @NotNull String groupAdminLogin,
            boolean addAdmin

    ) {
    }


    @Override
    public @NotNull GetAdminInfosService.Response handleRequest(@NotNull Request req) throws Exception {
        DB.authenticator.tokenAuthenticate(req.login, req.token);

        boolean hasRight = WebServer.db().hasRightToAddAdmin(req.login(), req.groupId, req.groupAdminLogin, req.addAdmin);
        if (!hasRight) {
            throw new DBExceptions.UserInputException.ForbiddenException();
        }
        WebServer.db().addGroupAdmin(req.groupId, req.groupAdminLogin, req.addAdmin);
        AdminInfosDTO infos = WebServer.db().getAdminInfos(req.login);
        return new GetAdminInfosService.Response(infos,
                "L'utilisateur " + req.groupAdminLogin + " a été ajouté comme admin du groupe " + req.groupId
        );
    }

}
