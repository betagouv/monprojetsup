package fr.gouv.monprojetsup.app.services.teacher;

import fr.gouv.monprojetsup.app.tools.server.Helpers;
import fr.gouv.monprojetsup.app.tools.server.MyService;
import fr.gouv.monprojetsup.app.db.DB;
import fr.gouv.monprojetsup.app.db.DBExceptions;
import fr.gouv.monprojetsup.app.dto.AdminInfosDTO;
import fr.gouv.monprojetsup.app.mail.AccountManagementEmails;
import fr.gouv.monprojetsup.app.server.WebServer;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import static fr.gouv.monprojetsup.app.tools.Sanitizer.sanitize;

@Service
public class SetGroupMemberService extends MyService<SetGroupMemberService.Request, GetAdminInfosService.Response> {

    public SetGroupMemberService() {
        super(Request.class);
    }

    public record Request(
            @NotNull String login,
            @NotNull String token,

            /**
             * name of the new group
              */
            @NotNull String groupId,

            /**
             *  name of the groupAdmin we want to add
             *  */
            @NotNull String memberlogin,

            /* if true add, if false remove */
            boolean addMember
    ) {
    }

    @Override
    protected GetAdminInfosService.@NotNull Response handleRequest(@NotNull Request req) throws Exception {
        if(!DB.authenticator.checkIsSuperUser(req.login, req.token)) {
            throw new DBExceptions.UserInputException.ForbiddenException();
        }

        Helpers.LOGGER.info(((req.addMember) ? "Adding " : "Removing ") + req.memberlogin + " to group '" + req.groupId + "'");

        WebServer.db().addOrRemoveMember(req.groupId, req.memberlogin, req.addMember);
        AdminInfosDTO updateInfos = WebServer.db().getAdminInfos(req.login);

        if(req.memberlogin.contains("@") && req.addMember) {
            AccountManagementEmails.sendAcceptUserInGroupEmail(
                    sanitize(req.memberlogin),
                    sanitize(req.groupId)
            );
        }

       return new GetAdminInfosService.Response(
                        updateInfos,
                        "Le membre '" + req.memberlogin + "' a été " + (req.addMember ? "ajouté au " : "retiré du ") + " groupe '" + req.groupId + "'"
                );

    }

}
