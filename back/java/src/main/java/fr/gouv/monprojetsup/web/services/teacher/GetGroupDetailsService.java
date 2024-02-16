package fr.gouv.monprojetsup.web.services.teacher;

import fr.gouv.monprojetsup.tools.Sanitizer;
import fr.gouv.monprojetsup.web.server.WebServer;
import fr.gouv.monprojetsup.web.db.DB;
import fr.gouv.monprojetsup.tools.server.MyService;
import fr.gouv.monprojetsup.tools.server.ResponseHeader;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.tools.Sanitizer.sanitize;

@Service
public class GetGroupDetailsService extends MyService<GetGroupDetailsService.Request, GetGroupDetailsService.Response> {

    public GetGroupDetailsService() {
        super(Request.class);
    }

    public record Request(
            @NotNull String login,
            @NotNull String token,
            @NotNull String groupId
    ) {
    }

    public record StudentDetails(
            String login,
            String name,
            int profileComplete,
            int likes,
            int bins,
            String msg,
            int health /* 0 ok 1 medium 2 bad */
    ) {
        public StudentDetails sanitize() {
            return new StudentDetails(
                    Sanitizer.sanitize(login),
                    Sanitizer.sanitize(name),
                    profileComplete,
                    likes,
                    bins,
                    Sanitizer.sanitize(msg),
                    health
            );
        }
    }

    public record GroupDetails(
            String groupName,
            String groupId,
            boolean isOpened,
            String token,
            List<StudentDetails> students,
            Set<String> admins,
            List<String> moderationList
    ) {

        public GroupDetails sanitize() {
            return new GroupDetails(
                    Sanitizer.sanitize(groupName),
                    Sanitizer.sanitize(groupId),
                    isOpened,
                    token,
                    students.stream().map(s -> s.sanitize()).toList(),
                    admins.stream().map(s -> Sanitizer.sanitize(s)).collect(Collectors.toSet()),
                    moderationList.stream().map(s -> Sanitizer.sanitize(s)).toList()
                    );
        }
    }
    public record Response(
            ResponseHeader header,
            GroupDetails details
    ) {
    }

    @Override
    protected @NotNull Response handleRequest(@NotNull Request req) throws Exception {

        DB.authenticator.tokenAuthenticate(req.login, req.token);

        if(!WebServer.db().isAdminOfGroup(req.login, req.groupId)) {
            throw new RuntimeException("opération réservée aux admins de groupe");
        }

        if(req.groupId == null) throw new RuntimeException("groupId is null");

        GroupDetails details = WebServer.db().getGroupDetails(sanitize(req.groupId));
        return new Response(new ResponseHeader(), details.sanitize());
    }

}
