package fr.gouv.monprojetsup.suggestions.eval;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.model.stats.PsupStatistiques;
import fr.gouv.monprojetsup.suggestions.dto.ProfileDTO;
import fr.gouv.monprojetsup.suggestions.server.SuggestionServer;
import fr.gouv.monprojetsup.tools.Serialisation;
import fr.gouv.monprojetsup.web.db.dbimpl.DBMongo;
import fr.gouv.monprojetsup.web.db.model.User;
import fr.gouv.monprojetsup.web.server.WebServerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ExtractRandomProfiles {

    public static final Logger LOGGER = Logger.getLogger(ExtractRandomProfiles.class.getName());

    public static void main(String[] args) throws Exception {


        LOGGER.info("Loading config...");
        WebServerConfig webServerConfig = WebServerConfig.load();

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

        ConfigurableApplicationContext context = SpringApplication.run(ExtractRandomProfiles.class, args);
        DBMongo db = context.getBean(DBMongo.class);

        db.load(webServerConfig);

        List<User> users = new ArrayList<>(
                db.getExpeENSUsers()
                        .stream()
                        .filter(
                                u -> !u.getProfile().suggApproved().isEmpty()
                        ).toList()
        );

        while(users.size() > 30) {
            users.remove((int) (Math.random() * users.size()));
        }
        users.forEach(User::anonymize);
        users.forEach(u -> u.getProfile().anonymize());

        ReferenceCases cases = new ReferenceCases(new ArrayList<>());
        cases.cases().addAll(
                users.stream().map(
                        u -> new ReferenceCases.ReferenceCase(
                                u.getProfile().toExplanationString(),
                                new Gson().fromJson( new Gson().toJson(u.getProfile().toDTO()), ProfileDTO.class)
                        )
                ).toList()
        );
        Serialisation.toJsonFile("randomReferenceCases.json", cases, true);

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

        db.stop();

        System.out.println("Hello world!");
    }

}
