package fr.gouv.monprojetsup.app.analysis;

import fr.gouv.monprojetsup.app.db.DBExceptions;
import fr.gouv.monprojetsup.app.db.admin.ExportDataToLocalFile;
import fr.gouv.monprojetsup.app.db.dbimpl.DBMongo;
import fr.gouv.monprojetsup.app.db.model.Group;
import fr.gouv.monprojetsup.app.db.model.User;
import fr.gouv.monprojetsup.app.log.ServerTrace;
import fr.gouv.monprojetsup.app.server.WebServerConfig;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import fr.gouv.monprojetsup.data.tools.csv.CsvTools;
import lombok.val;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.app.analysis.UsersBehaviourReport.analyze;
import static fr.gouv.monprojetsup.app.db.admin.ExportDataToLocalFile.copyFile;

@SpringBootApplication
@EnableMongoRepositories
@ComponentScan(basePackages = {"fr.gouv.monprojetsup"})
public class CreateAnalysisReports {

    public static void main(String[] args) throws IOException, DBExceptions.ModelException, InterruptedException {

        WebServerConfig config = WebServerConfig.load();

        ConfigurableApplicationContext context =
                new SpringApplicationBuilder(ExportDataToLocalFile.class)
                        .web(WebApplicationType.NONE)
                        .run(args);

        DBMongo db = context.getBean(DBMongo.class);

        db.load(config);

        boolean anonymize = true;

        List<ServerTrace> traces = db.getTraces();

        WordSearch.analyseWordSearch(traces);
        
        List<Group> groups = new ArrayList<>(db.getAllGroups());

        List<User> users = new ArrayList<>(db.getUsers());

        Set<String> testGroups = groups.stream().map(Group::getExpeENSGroupe).collect(Collectors.toSet());

        for(String testGroup : testGroups) {
            Set<String> members = groups.stream()
                    .filter(g -> g.getExpeENSGroupe().equals(testGroup))
                    .flatMap(g -> g.getMembers().stream())
                    .collect(Collectors.toSet());

            UsersBehaviourReport report = analyze(
                    groups.stream().filter(g -> g.getExpeENSGroupe().equals(testGroup)).toList(),
                    users.stream().filter(u -> members.contains(u.login())).toList(),
                    traces,
                    false,
                    anonymize
            );

            create(report, "report_" + testGroup);
        }
    }

    public static void create(UsersBehaviourReport report, String filename) throws IOException {


        Serialisation.toJsonFile(filename + ".json", report, true);
        Path sourceFile = Path.of(filename + ".json");
        copyFile(sourceFile, Path.of("data/" + filename + ".json"));

        try (CsvTools writer = new CsvTools(filename + ".csv", ',')) {
            writer.appendHeaders(List.of("Groupe", "User", "Durée (min)", "Evènements"));
            for (val entry : report.groupsBehaviours().entrySet()) {
                String group = entry.getKey();
                UsersBehaviourReport.GroupBehaviourReport stats = entry.getValue();
                for (val entryUser : stats.individualBehaviours().entrySet()) {
                    val userStats = entryUser.getValue();
                    writer.append(group);
                    writer.append(entryUser.getKey());
                    writer.append(userStats.navigationTimeInMinutes());
                    writer.append(userStats.behaviour().stream()
                            .filter(b -> !b.summary.contains("empty"))
                            .map(b -> b.summary)
                            .collect(Collectors.joining("\n"))
                    );
                    writer.newLine();
                }
            }
        }

        try (OutputStreamWriter writer = new OutputStreamWriter(
                Files.newOutputStream(
                        Path.of(filename + ".txt")),
                StandardCharsets.UTF_8
        )
        ) {
            writer.write(report.getMacrosStats() + "\n");
        }

    }
}
