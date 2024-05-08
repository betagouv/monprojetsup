package fr.gouv.monprojetsup.app.services.teacher;

import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.model.stats.StatsContainers;
import fr.gouv.monprojetsup.app.server.MyService;
import fr.gouv.monprojetsup.common.server.ResponseHeader;
import fr.gouv.monprojetsup.app.db.DB;
import fr.gouv.monprojetsup.app.db.DBExceptions;
import fr.gouv.monprojetsup.app.dto.ProfileDb;
import fr.gouv.monprojetsup.app.server.WebServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GetStudentProfileService extends MyService<GetStudentProfileService.Request, GetStudentProfileService.Response> {

    public GetStudentProfileService() {
        super(Request.class);
    }

    public record Request(
            @NotNull String login,
            @NotNull String token,
            @NotNull String memberLogin
    ) {
    }

    public record Response(
            @NotNull ResponseHeader header,
            @Nullable ProfileDb profile,
            @Nullable Map<String, StatsContainers.DetailFiliere> stats
    ) {
    }

    @Override
    protected @NotNull Response handleRequest(Request req) throws Exception {

        DB.authenticator.tokenAuthenticate(req.login, req.token);

        if(!WebServer.db().isAdminOfUser(req.login, req.memberLogin)) {
            throw new DBExceptions.UserInputException.ForbiddenException();
        }

        @Nullable ProfileDb p = WebServer.db().getProfile(req.memberLogin).sanitize();
        if(!WebServer.db().isSuperAdmin(req.login)) {
            p.retours().removeIf(r -> !r.author().equals(req.login));
        }
        @Nullable Map<String, StatsContainers.DetailFiliere> stats = p != null ? ServerData.getGroupStats(p.bac(), p.favoris()) : null;

        return new Response(new ResponseHeader(), p, stats);
    }


}
