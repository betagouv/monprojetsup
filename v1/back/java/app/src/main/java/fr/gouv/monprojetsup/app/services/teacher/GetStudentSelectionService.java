package fr.gouv.monprojetsup.app.services.teacher;

import fr.gouv.monprojetsup.app.db.DB;
import fr.gouv.monprojetsup.app.db.DBExceptions;
import fr.gouv.monprojetsup.app.dto.ProfileDb;
import fr.gouv.monprojetsup.app.server.MyService;
import fr.gouv.monprojetsup.app.server.WebServer;
import fr.gouv.monprojetsup.app.services.info.SearchService;
import fr.gouv.monprojetsup.app.services.profiles.GetMySelectionService;
import fr.gouv.monprojetsup.common.server.Helpers;
import fr.gouv.monprojetsup.common.server.ResponseHeader;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.dto.ProfileDTO;
import fr.gouv.monprojetsup.data.dto.SuggestionDTO;
import fr.gouv.monprojetsup.data.model.stats.StatsContainers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.app.services.info.SearchService.getDetails;

@Service
public class GetStudentSelectionService extends MyService<GetStudentProfileService.Request, SearchService.Response> {

    public GetStudentSelectionService() {
        super(GetStudentProfileService.Request.class);
    }


    @Override
    protected @NotNull SearchService.Response handleRequest(GetStudentProfileService.Request req) throws Exception {

        DB.authenticator.tokenAuthenticate(req.login(), req.token());

        if(!WebServer.db().isAdminOfGroup(req.login(), req.groupId())) {
            throw new DBExceptions.UserInputException.ForbiddenException();
        }
        @Nullable ProfileDb p = WebServer.db().getGroupMemberProfile(req.groupId(), req.memberLogin()).sanitize();

        if(p == null) {
            throw new DBExceptions.UnknownUserException(req.memberLogin());
        }
        ProfileDTO profile = p.toDto();

        Helpers.LOGGER.info("Serving profile with login: '" + req.login() + "'");

        List<String> keys = profile.suggApproved().stream().map(SuggestionDTO::fl).toList();

        final @NotNull List<SearchService.ResultatRecherche> suggestions = getDetails(
                profile,
                keys,
                keys.stream().collect(Collectors.toMap(k -> k, k -> 1.0)),
                Map.of());

        return new SearchService.Response(suggestions);

    }


}
