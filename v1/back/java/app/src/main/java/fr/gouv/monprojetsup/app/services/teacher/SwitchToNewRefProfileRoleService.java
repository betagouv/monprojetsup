package fr.gouv.monprojetsup.app.services.teacher;

import fr.gouv.monprojetsup.app.db.DB;
import fr.gouv.monprojetsup.app.db.DBExceptions;
import fr.gouv.monprojetsup.app.db.model.Group;
import fr.gouv.monprojetsup.app.db.model.User;
import fr.gouv.monprojetsup.app.mail.AccountManagementEmails;
import fr.gouv.monprojetsup.app.server.MyAppService;
import fr.gouv.monprojetsup.app.server.Server;
import fr.gouv.monprojetsup.app.server.WebServer;
import fr.gouv.monprojetsup.app.services.accounts.CreateAccountService;
import fr.gouv.monprojetsup.app.services.accounts.PasswordLoginService;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class SwitchToNewRefProfileRoleService extends MyAppService<Server.BasicRequest, CreateAccountService.Response> {

    public SwitchToNewRefProfileRoleService() {
        super(Server.BasicRequest.class);
    }

    @Override
    public @NotNull CreateAccountService.Response handleRequest(Server.BasicRequest req) throws Exception {
        DB.authenticator.tokenAuthenticate(req.login(), req.token());

        User user = WebServer.db().getUser(req.login());
        Group group = WebServer.db().findGroup(DB.getExpertGroupId());
        if(!WebServer.db().isExpert(user)) throw new DBExceptions.UserInputException.ForbiddenException();


        String password = RandomStringUtils.random(12, true, false).toLowerCase();
        long nbUsersAlreadyCreated = group.members().stream().filter(m -> m.startsWith(req.login())).count();
        String login = req.login() + "_" + (1 + nbUsersAlreadyCreated);

        PasswordLoginService.LoginAnswer answer = WebServer.db().createAccount(new CreateAccountService.CreateAccountRequest(
                User.UserTypes.lyceen,
                login,
                password,
                user.getCguVersion(),
                group.getRegistrationToken(),
                req.login(),
                Long.toString(1 + nbUsersAlreadyCreated)
        ));
        ///send email
        AccountManagementEmails.sendNewUserCreatedByExpertEmail(
                req.login(),
                login,
                password
        );

        return new CreateAccountService.Response(answer);

    }

}
