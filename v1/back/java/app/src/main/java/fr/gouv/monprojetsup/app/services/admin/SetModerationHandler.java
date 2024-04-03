package fr.gouv.monprojetsup.app.services.admin;

import fr.gouv.monprojetsup.app.db.DBExceptions;
import fr.gouv.monprojetsup.app.server.WebServer;
import fr.gouv.monprojetsup.app.db.DB;
import fr.gouv.monprojetsup.app.dto.AdminInfosDTO;
import fr.gouv.monprojetsup.app.services.teacher.GetAdminInfosService;
import fr.gouv.monprojetsup.app.tools.server.MyService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class SetModerationHandler extends MyService<SetModerationHandler.Request, GetAdminInfosService.Response> {

    public SetModerationHandler() {
        super(Request.class);
    }

    public record Request(
            @NotNull String login,
            @NotNull String token,

            @NotNull Boolean moderate,

            @NotNull String type

    ) {
    }

    @Override
    public @NotNull GetAdminInfosService.Response handleRequest(@NotNull Request req) throws Exception {
        if(!DB.authenticator.checkIsSuperUser(req.login(), req.token())) {
            throw new DBExceptions.UserInputException.ForbiddenException();
        }
        WebServer.db().setModeration(req.moderate, req.type);
        final AdminInfosDTO infos = WebServer.db().getAdminInfos(req.login);
        return new GetAdminInfosService.Response(
                        infos,
                        "Modération des " + req.type + " " + (req.moderate ? "activée" : "désactivée") + "."
                );
    }


}
