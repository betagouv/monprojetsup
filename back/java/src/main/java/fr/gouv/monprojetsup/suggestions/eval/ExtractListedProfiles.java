package fr.gouv.monprojetsup.suggestions.eval;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.suggestions.dto.ProfileDTO;
import fr.gouv.monprojetsup.suggestions.server.SuggestionServer;
import fr.gouv.monprojetsup.tools.Serialisation;
import fr.gouv.monprojetsup.web.db.model.User;
import fr.gouv.monprojetsup.web.server.WebServerConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

@SpringBootApplication
@EnableMongoRepositories
@ComponentScan(basePackages = {"fr.gouv.monprojetsup"})
public class ExtractListedProfiles {

    public static final Logger LOGGER = Logger.getLogger(ExtractListedProfiles.class.getName());

    public static void main(String[] args) throws Exception {


        WebServerConfig config = WebServerConfig.load();

        try {
            ServerData.statistiques = new PsupStatistiques();
            ServerData.statistiques.labels = Serialisation.fromJsonFile(
                    "labelsDebug.json",
                    Map.class
            );
        } catch (Exception e) {
            SuggestionServer server = new SuggestionServer();
            server.init();
        }

        List<User> users = Serialisation.fromJsonFile("usersExpeENS.json",
                new TypeToken<List<User>>(){}.getType()
        );

        Set listedUsers = Set.of("linsttr@gmail.com",
                "ambrine.selmane",
                "sechaud.l74@gmail.com",
                "elisa.mercier.2004@icloud.com",
                "stellaromagny@gmail.com",
                "enzo.debieuvre@gmail.com",
                "schneider.elliott@gmail.com",
                "louisa@deveaux.fr",
                "cumbooceane17@gmail.com",
                "elmaataoui.youssra9@gmail.com",
                "mathildeferriol11@gmail.com");
        users.removeIf(u -> !listedUsers.contains(u.getId()));
        users.forEach(User::anonymize);
        users.forEach(u -> u.getProfile().anonymize());

        ReferenceCases cases = new ReferenceCases(new ArrayList<>());
        cases.cases().addAll(
                users.stream().map(
                        u -> new ReferenceCases.ReferenceCase(
                                u.login(),
                                new Gson().fromJson( new Gson().toJson(u.getProfile().toDTO()), ProfileDTO.class)
                        )
                ).toList()
        );
        Serialisation.toJsonFile("listedReferenceCases.json", cases, true);

        int i = 1;
        for (ReferenceCases.ReferenceCase refCase : cases.cases()) {

            try (
                    OutputStreamWriter fos = new OutputStreamWriter(
                            Files.newOutputStream(Path.of(
                                    " profil random #" + i + ".txt")
                            ),
                            StandardCharsets.UTF_8
                    )
            ) {

                i++;
                fos.write("************* Profil human readable *****************\n");
                fos.write(refCase.pf().toExplanationString());
                fos.write("\n");
                fos.write("\n");
                fos.write("\n");
                fos.write("************* Profil format db (à inclure dans le tableur stp) *******************\n");
                fos.write(new GsonBuilder().setPrettyPrinting().create().toJson(refCase.pf()));

            }
        }


        System.out.println("Hello world!");
    }

}
