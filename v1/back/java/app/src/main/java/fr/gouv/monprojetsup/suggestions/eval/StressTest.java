package fr.gouv.monprojetsup.suggestions.eval;

import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.suggestions.dto.ProfileDTO;
import fr.gouv.monprojetsup.suggestions.services.GetSuggestionsService.SuggestionsDTO.Suggestion;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import fr.gouv.monprojetsup.app.db.model.User;

import java.io.IOException;
import java.util.List;

import static fr.gouv.monprojetsup.suggestions.eval.ReferenceCases.ReferenceCase.useRemoteUrl;
import static fr.gouv.monprojetsup.suggestions.eval.ReferenceCases.callSuggestionsService;
import static fr.gouv.monprojetsup.app.server.WebServer.LOGGER;

public class StressTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Analyzing users");

        /* load all users as a list of users from the file "usersExpeENS.json" */

        //stree test of the remote server
        useRemoteUrl(true);

        List<User> usersOfInterest
                = Serialisation.fromJsonFile(
                "usersExpeENS.json",
                new TypeToken<List<User>>() {
                }.getType()
        );

        usersOfInterest.stream().parallel().forEach(user -> {
                    LOGGER.info("Getting suggestions of User " + user.getId());
                    var pf = user.pf().toDTO();
                    var pfo = new ProfileDTO(pf);
                    List<Suggestion> suggestions = null;
                    try {
                        suggestions = callSuggestionsService(pfo);
                        LOGGER.info("Getting explanations of User " + user.getId());

                        /*
                        suggestions.stream().parallel().forEach(suggestion -> {
                                    LOGGER.info("Getting explanation of User " + user.getId() + " for " + suggestion.fl());
                                    try {
                                        getExplanationsAndExamples(
                                                pfo,
                                                suggestion.fl()
                                        );
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                        );*/
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }
}
