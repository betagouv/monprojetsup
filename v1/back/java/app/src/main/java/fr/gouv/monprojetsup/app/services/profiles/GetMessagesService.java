package fr.gouv.monprojetsup.app.services.profiles;

import fr.gouv.monprojetsup.app.db.DB;
import fr.gouv.monprojetsup.app.db.model.Message;
import fr.gouv.monprojetsup.app.server.Helpers;
import fr.gouv.monprojetsup.app.server.MyAppService;
import fr.gouv.monprojetsup.app.server.WebServer;
import fr.gouv.monprojetsup.app.server.ResponseHeader;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GetMessagesService extends MyAppService<GetMessagesService.Request, GetMessagesService.Response> {

    public GetMessagesService() {
        super(Request.class);
    }

    public record Request(
            @NotNull String login,
            @NotNull String token,

            @NotNull String user,
            String group,
            /*optional*/
            String topic
    ) {
    }

    public record Response(
            ResponseHeader header,
            Map<String, List<Message>> msgs
    ) {
        public Response(Map<String, List<Message>> chats) {
            this(new ResponseHeader(), chats);
        }
    }


    @Override
    protected @NotNull Response handleRequest(@NotNull Request req) throws Exception {
        DB.authenticator.tokenAuthenticate(req.login, req.token);

        if (!req.login.equals(req.user)) {
            boolean isGroupAdmin = WebServer.db().isAdminOfGroup(req.login(), req.group);
            if (!isGroupAdmin) {
                throw new RuntimeException("opération réservée aux admins de groupe");
            }
            boolean isMemberOfGroup = WebServer.db().isMemberOfGroup(req.user, req.group);
            if (!isMemberOfGroup) {
                throw new RuntimeException("le membre n'appartient pas au groupe");
            }
        }

        if (req.topic != null) {
            Helpers.LOGGER.info("Sending messages of user '" + req.user() + "' for topic '" + req.topic + "'");
        } else {
            Helpers.LOGGER.info("Sending messages of user '" + req.user() + "' for all topics");
        }

        return new Response(WebServer.db().getSanitizedMessages(req.user, req.topic));

    }

}