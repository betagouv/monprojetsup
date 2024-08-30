package fr.gouv.monprojetsup.app.services.teacher;

import fr.gouv.monprojetsup.app.db.DB;
import fr.gouv.monprojetsup.app.db.model.Classe;
import fr.gouv.monprojetsup.app.server.MyAppService;
import fr.gouv.monprojetsup.app.server.ResponseHeader;
import fr.gouv.monprojetsup.app.server.WebServer;
import fr.gouv.monprojetsup.app.tools.Sanitizer;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GetGroupDetailsService extends MyAppService<GetGroupDetailsService.Request, GetGroupDetailsService.Response> {

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
            int health,
            /* 0 ok 1 medium 2 bad */
            int profileCompletenessPercent,
            String reflexionStatus,
            int nbFormationsFavoris,
            int nbMetiersFavoris,
            String duration
    ) {
        public StudentDetails sanitize() {
            return new StudentDetails(
                    Sanitizer.sanitize(login),
                    Sanitizer.sanitize(name),
                    profileComplete,
                    likes,
                    bins,
                    health,
                    profileCompletenessPercent,
                    Sanitizer.sanitize(reflexionStatus),
                    nbFormationsFavoris,
                    nbMetiersFavoris,
                    Sanitizer.sanitize(duration)
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
            List<String> moderationList,

            String expeENSGroup,

            Classe.Niveau niveau
    ) {

        public GroupDetails sanitize() {
            return new GroupDetails(
                    Sanitizer.sanitize(groupName),
                    Sanitizer.sanitize(groupId),
                    isOpened,
                    token,
                    students.stream().map(StudentDetails::sanitize).toList(),
                    admins.stream().map(Sanitizer::sanitize).collect(Collectors.toSet()),
                    moderationList.stream().map(Sanitizer::sanitize).toList(),
                    Sanitizer.sanitize(expeENSGroup),
                    niveau
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

        GroupDetails details = WebServer.db().getGroupDetails(req.groupId);
        return new Response(new ResponseHeader(), details.sanitize());
    }

}
