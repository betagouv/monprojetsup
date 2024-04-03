package fr.gouv.monprojetsup.suggestions.eval;

import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.common.dto.ProfileDTO;
import fr.gouv.monprojetsup.suggestions.services.GetSuggestionsService.SuggestionsDTO.Suggestion;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import static fr.gouv.monprojetsup.suggestions.eval.ReferenceCases.ReferenceCase.useRemoteUrl;
import static fr.gouv.monprojetsup.suggestions.eval.ReferenceCases.callSuggestionsService;
import static fr.gouv.monprojetsup.suggestions.eval.ReferenceCases.getExplanationsAndExamples;

@Slf4j
public class StressTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Analyzing users");

        /* load all users as a list of users from the file "usersExpeENS.json" */

        //stree test of the remote server
        useRemoteUrl(true);

        List<ProfileDTO> profiles
                = Serialisation.fromJsonFile(
                "usersExpeENS.json",
                new TypeToken<List<ProfileDTO>>() {
                }.getType()
        );

        Random random = new Random();
        profiles.stream().parallel().forEach(pfo -> {
                    List<Suggestion> suggestions = null;
                    try {
                        suggestions = callSuggestionsService(pfo);

                        suggestions.stream().parallel().forEach(suggestion -> {
                                    if (random.nextDouble() > 0.9) {
                                        log.info("Getting explanation for " + suggestion.fl());
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
                                }
                        );
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }
}
