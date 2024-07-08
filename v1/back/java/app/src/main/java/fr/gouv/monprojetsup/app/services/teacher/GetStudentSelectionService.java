package fr.gouv.monprojetsup.app.services.teacher;

import fr.gouv.monprojetsup.app.db.DB;
import fr.gouv.monprojetsup.app.db.DBExceptions;
import fr.gouv.monprojetsup.app.dto.ProfileDb;
import fr.gouv.monprojetsup.app.server.Helpers;
import fr.gouv.monprojetsup.app.server.MyAppService;
import fr.gouv.monprojetsup.data.dto.ResponseHeader;
import fr.gouv.monprojetsup.app.server.WebServer;
import fr.gouv.monprojetsup.app.services.info.SearchService;
import fr.gouv.monprojetsup.data.dto.ProfileDTO;
import fr.gouv.monprojetsup.data.dto.SuggestionDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.app.services.info.SearchService.getDetails;

@Service
public class GetStudentSelectionService extends MyAppService<GetStudentProfileService.Request, GetStudentSelectionService.Response> {

    public GetStudentSelectionService() {
        super(GetStudentProfileService.Request.class);
    }

    public record Response(
        @NotNull ResponseHeader header,
        @Schema(
                name = "details",
                description =
                        """
                           Détails sur une liste de formations, en fonction d'un profil.
                           Chaque détail inclut:
                           * des explications sur les éléments de proximité entre le profil et la formation
                           * des exemples de métiers associées, triés par affinité décroissante
                           * des lieux de formations d'intérêts, proches des villes d'intérêts du profil
                           * des villes disponibles pour faire cette formation, triées par ordre décroissant de disctance avec les villes d'intérêts du profil
                           * des stats sur la formation, adaptées au type de bac du profil
                           """,
                required = true
        )
        @NotNull List<SearchService.ResultatRecherche> details,

        @NotNull List<ProfileDb.Retour> retours
    ){
        public Response(@NotNull List<SearchService.ResultatRecherche>  details, List<ProfileDb.Retour> retours) {
            this(
                    new ResponseHeader(),
                    details,
                    retours
            );
        }

    }

    @Override
    protected @NotNull GetStudentSelectionService.Response handleRequest(GetStudentProfileService.Request req) throws Exception {

        DB.authenticator.tokenAuthenticate(req.login(), req.token());

        if(!WebServer.db().isAdminOfUser(req.login(), req.memberLogin())) {
            throw new DBExceptions.UserInputException.ForbiddenException();
        }

        val pf = WebServer.db().getProfile(req.memberLogin()).sanitize();
        ProfileDTO profile = pf.toDto();

        Helpers.LOGGER.info("Serving profile with login: '" + req.login() + "'");

        List<String> keys = profile.suggApproved().stream().map(SuggestionDTO::fl).toList();

        final @NotNull List<SearchService.ResultatRecherche> suggestions = getDetails(
                profile,
                keys,
                keys.stream().collect(Collectors.toMap(k -> k, k -> 1.0)),
                Map.of()
        );

        return new GetStudentSelectionService.Response(
                suggestions,
                pf.retours()
        );

    }


}
