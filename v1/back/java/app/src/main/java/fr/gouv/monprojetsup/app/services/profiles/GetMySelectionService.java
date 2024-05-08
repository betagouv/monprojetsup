package fr.gouv.monprojetsup.app.services.profiles;

import fr.gouv.monprojetsup.app.db.DB;
import fr.gouv.monprojetsup.app.dto.ProfileDb;
import fr.gouv.monprojetsup.app.server.MyService;
import fr.gouv.monprojetsup.app.server.WebServer;
import fr.gouv.monprojetsup.app.services.info.SearchService;
import fr.gouv.monprojetsup.common.server.Helpers;
import fr.gouv.monprojetsup.common.server.Server;
import fr.gouv.monprojetsup.data.dto.ProfileDTO;
import fr.gouv.monprojetsup.data.dto.SuggestionDTO;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.app.services.info.SearchService.getDetails;

@Service
public class GetMySelectionService extends MyService<Server.BasicRequest, SearchService.Response> {

    public GetMySelectionService() {
        super(Server.BasicRequest.class);
    }


    @Override
    protected @NotNull SearchService.Response handleRequest(@NotNull Server.BasicRequest req) throws Exception {
        DB.authenticator.tokenAuthenticate(req.login(), req.token());

        val pf = WebServer.db().getProfile(req.login()).sanitize();
        ProfileDTO profile = pf.toDto();

        Helpers.LOGGER.info("Serving profile with login: '" + req.login() + "'");

        List<String> keys = profile.suggApproved().stream().map(SuggestionDTO::fl).toList();

        final @NotNull List<SearchService.ResultatRecherche> suggestions = getDetails(
                profile,
                keys,
                keys.stream().collect(Collectors.toMap(k -> k, k -> 1.0)),
                Map.of());

        injectRetours(suggestions, pf.retours());

        return new SearchService.Response(suggestions);
    }

    private void injectRetours(List<SearchService.ResultatRecherche> resultats, List<ProfileDb.Retour> retoursList) {
        Map<String, List<ProfileDb.Retour>> retoursByKeys = retoursList.stream().collect(Collectors.groupingBy(ProfileDb.Retour::key));
        for (SearchService.ResultatRecherche resultat : resultats) {
            val retours = retoursByKeys.get(resultat.key());
            if(retours != null) {
                retours.forEach(retour -> {
                    val author = retour.author();
                    String authorName;
                    try {
                        authorName = WebServer.db().getUserName(author);
                    } catch (Exception e) {
                        authorName = author;
                    }
                    resultat.retours().add(new ProfileDb.Retour(
                            authorName,
                            retour.type(),
                            retour.key(),
                            retour.content(),
                            retour.date()
                    ));
                });

            }
        }
    }


}
