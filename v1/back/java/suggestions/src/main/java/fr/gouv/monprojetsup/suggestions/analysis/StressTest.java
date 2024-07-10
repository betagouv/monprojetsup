package fr.gouv.monprojetsup.suggestions.analysis;

import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.suggestions.dto.ProfileDTO;
import fr.gouv.monprojetsup.suggestions.data.tools.Serialisation;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import static fr.gouv.monprojetsup.suggestions.analysis.ReferenceCases.*;

@Slf4j
public class StressTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Analyzing users");

        /* load all users as a list of users from the file "usersExpeENS.json" */

        //stress test of the remote server
        useRemoteUrl(true);

        List<ProfileDTO> profiles
                = Serialisation.fromJsonFile(
                "usersExpeENS.json",
                new TypeToken<List<ProfileDTO>>() {
                }.getType()
        );

        Random random = new Random();
        profiles.stream().parallel().forEach(pfo -> {
                    try {
                        val suggestions = callSuggestionsService(pfo);

                        suggestions.stream().parallel().forEach(suggestion -> {
                                    if (random.nextDouble() > 0.9) {
                                        log.info("Getting explanation for " + suggestion.key());
                                        try {
                                            getExplanationsAndExamples(
                                                    pfo,
                                                    suggestion.key()
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
