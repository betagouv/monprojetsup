package fr.gouv.monprojetsup.web.services.admin;

import fr.gouv.monprojetsup.web.db.DBExceptions;
import fr.gouv.monprojetsup.web.server.WebServer;
import fr.gouv.monprojetsup.web.db.DB;
import fr.gouv.monprojetsup.web.dto.AdminInfosDTO;
import fr.gouv.monprojetsup.web.services.teacher.GetAdminInfosService;
import fr.gouv.monprojetsup.tools.server.MyService;
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
