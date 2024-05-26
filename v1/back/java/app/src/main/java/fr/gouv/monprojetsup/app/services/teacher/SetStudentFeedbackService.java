package fr.gouv.monprojetsup.app.services.teacher;

import fr.gouv.monprojetsup.app.db.DB;
import fr.gouv.monprojetsup.app.db.DBExceptions;
import fr.gouv.monprojetsup.app.dto.ProfileDb;
import fr.gouv.monprojetsup.app.server.MyService;
import fr.gouv.monprojetsup.app.server.WebServer;
import fr.gouv.monprojetsup.common.server.ResponseHeader;
import fr.gouv.monprojetsup.common.server.Server;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.model.stats.StatsContainers;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SetStudentFeedbackService extends MyService<SetStudentFeedbackService.Request, Server.BasicResponse> {

    public SetStudentFeedbackService() {
        super(Request.class);
    }


    /*"teacher/student/feedback",
      {
        type: type,
        studentLogin: studentLogin,
        content: content,
      },*/

    public record Request(
            @NotNull String login,
            @NotNull String token,
            @NotNull String studentLogin,
            @NotNull String key,
            @NotNull String type,
            @Schema(description = "if null then erase")
            @Nullable String content
    ) {
    }

    @Override
    protected @NotNull Server.BasicResponse handleRequest(Request req) throws Exception {
        DB.authenticator.tokenAuthenticate(req.login, req.token);
        if(!req.login.equals(req.studentLogin) && !WebServer.db().isAdminOfUser(req.login, req.studentLogin)) {
            throw new DBExceptions.UserInputException.ForbiddenException();
        }
        WebServer.db().setTeacherFeedback(req.login, req.studentLogin, req.key, req.type, req.content);
        return new Server.BasicResponse();
    }


}
