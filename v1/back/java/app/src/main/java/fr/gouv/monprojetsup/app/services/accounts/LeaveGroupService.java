package fr.gouv.monprojetsup.app.services.accounts;

import fr.gouv.monprojetsup.app.db.DB;
import fr.gouv.monprojetsup.app.server.MyAppService;
import fr.gouv.monprojetsup.app.server.Server;
import fr.gouv.monprojetsup.app.server.WebServer;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class LeaveGroupService extends MyAppService<LeaveGroupService.Request, Server.BasicResponse> {

    public LeaveGroupService() {
        super(Request.class);
    }

    public record Request(
            @NotNull String login,
            @NotNull String token,
            @NotNull String key
    ) {
    }

    @Override
    protected @NotNull Server.BasicResponse handleRequest(@NotNull Request req) throws Exception {

        DB.authenticator.tokenAuthenticate(req.login(), req.token());
            WebServer.db().leaveGroup(req.login(), req.key());
        return new Server.BasicResponse();
    }


}
