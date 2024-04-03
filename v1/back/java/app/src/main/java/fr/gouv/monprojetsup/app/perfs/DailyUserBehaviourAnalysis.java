package fr.gouv.monprojetsup.app.perfs;

import fr.gouv.monprojetsup.data.tools.Serialisation;
import fr.gouv.monprojetsup.data.tools.csv.CsvTools;
import fr.gouv.monprojetsup.app.db.DBExceptions;
import fr.gouv.monprojetsup.app.db.admin.ExportDataToLocalFile;
import fr.gouv.monprojetsup.app.db.dbimpl.DBMongo;
import fr.gouv.monprojetsup.app.db.model.Group;
import fr.gouv.monprojetsup.app.db.model.User;
import fr.gouv.monprojetsup.app.log.ServerTrace;
import fr.gouv.monprojetsup.app.mail.MailSender;
import fr.gouv.monprojetsup.app.server.WebServerConfig;
import lombok.val;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.app.db.admin.ExportDataToLocalFile.copyFile;

@SpringBootApplication
@EnableMongoRepositories
@ComponentScan(basePackages = {"fr.gouv.monprojetsup"})
public class DailyUserBehaviourAnalysis {

    private static final Logger LOGGER = Logger.getLogger(DailyUserBehaviourAnalysis.class.getSimpleName());

    public static void main(String[] args) throws IOException, DBExceptions.ModelException, InterruptedException {

        WebServerConfig config = WebServerConfig.load();

        ConfigurableApplicationContext context =
                new SpringApplicationBuilder(ExportDataToLocalFile.class)
                        .web(WebApplicationType.NONE)
                        .run(args);

        DBMongo db = context.getBean(DBMongo.class);

        db.load(config);

        boolean anonymize = true;

        {
            boolean onlyToday = true;
            List<ServerTrace> traces = db.getTraces();

            List<Group> groups = new ArrayList<>(db.getAllGroups());

            List<User> users = new ArrayList<>(db.getUsers());

            boolean onlyENS = true;
            boolean onlyENSTest = true;

            sendReport(
                    "stats quotidiennes expe ENS groupe test",
                    config,
                    groups,
                    users,
                    traces,
                    onlyToday,
                    onlyENS,
                    onlyENSTest,
                    anonymize,
                    config.getDailyEmailENSRecipients()
            );

        }
        {
            boolean onlyToday = true;

            List<ServerTrace> traces = db.getTraces();

            List<Group> groups = new ArrayList<>(db.getAllGroups());

            List<User> users = new ArrayList<>(db.getUsers());

            sendReport(
                    "stats 24 heures",
                    config,
                    groups,
                    users,
                    traces,
                    onlyToday,
                    false,
                    false,
                    anonymize,
                    config.getDailyEmailRecipients()
            );
        }
        {
            boolean onlyToday = false;

            List<ServerTrace> traces = db.getTraces();

            LocalDateTime start = LocalDateTime.now()
                    .minusDays(30)
                    .withHour(0).withMinute(0).withSecond(0).withNano(0);
            traces.removeIf(e -> LocalDateTime.parse(e.timestamp()).isBefore(start));

            List<Group> groups = new ArrayList<>(db.getAllGroups());

            List<User> users = new ArrayList<>(db.getUsers());

            sendReport(
                    "stats 30 jours",
                    config,
                    groups,
                    users,
                    traces,
                    onlyToday,
                    false,
                    false,
                    anonymize,
                    config.getDailyEmailRecipients()
            );
        }
    }

    private static void sendReport(
            String subject,
            WebServerConfig config,
            List<Group> groups,
            List<User> users,
            List<ServerTrace> traces,
            boolean onlyToday,
            boolean onlyENS,
            boolean onlyENSTest,
            boolean anonymize,
            Collection<String> recipients
    ) throws IOException, InterruptedException {

        UsersBehaviourReport report = analyze(groups, users, traces, onlyToday, onlyENS, onlyENSTest, anonymize);


        String text = subject + "\n\n\n" + report.getMacrosStats();

        LOGGER.info(text);

        text = text + "Détails des traces de navigation en PJ\n\n\n";
        String filename = "rapport_detaille" +
                LocalDateTime.now().withSecond(0).withNano(0)
                        .toString().replace(":","") ;

        Serialisation.toJsonFile(filename+ ".json", report, true);
        Path sourceFile = Path.of(filename + ".json");
        copyFile(sourceFile, Path.of("data/" + filename + ".json"));

        try(CsvTools writer = new CsvTools(filename + ".csv", ',')) {
            writer.append(List.of("Groupe", "User", "Durée (min)", "Evènements"));
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
        Path attachmentFile = Path.of(filename + ".csv");


        Thread thread = MailSender.send(
                config.getEmailConfig(),
                recipients,
                subject,
                text.replace("\n", "<br/>"),
                List.of(attachmentFile),
                true,
                null
        );
        thread.join();

    }

    private static UsersBehaviourReport analyze(
            List<Group> groups,
            List<User> users,
            List<ServerTrace> traces,
            boolean onlyToday,
            boolean onlyENS,
            boolean onlyENSTest,
            boolean anonymize
    ) {

        if(onlyENS) {
            groups.removeIf(group -> !group.isExpeENSGroup());
        }
        if(onlyENSTest) {
            groups.removeIf(group -> !group.isExpeENSGroupeTest());
        } else {
            traces.removeIf(trace -> trace.toSummary().contains("empty"));
        }
        Map<String, Group> usertoGroup = new HashMap<>();
        groups.forEach(group -> group.getMembers()
                .forEach(login -> usertoGroup.put(login, group)));

        if(onlyToday) {
            LocalDateTime now = LocalDateTime.now();
            if(now.getHour() <= 6) {
                now = now.minusDays(1);
            }
            LocalDateTime today = now.withHour(0).withMinute(0).withSecond(0).withNano(0);
            traces.removeIf(e -> LocalDateTime.parse(e.timestamp()).isBefore(today));
        }

        Set<String> scope = groups.stream().flatMap(g -> g.getMembers().stream()).collect(Collectors.toSet());
        scope.retainAll(traces.stream().map(ServerTrace::origin).collect(Collectors.toSet()));
        scope.retainAll(users.stream().map(User::getId).collect(Collectors.toSet()));

        users.removeIf(u -> !scope.contains(u.getId()));
        groups.removeIf(g -> g.getMembers().stream().noneMatch(scope::contains));
        traces.removeIf(t -> !scope.contains(t.origin()));
        usertoGroup.keySet().retainAll(scope);

         return  UsersBehaviourReport.get(usertoGroup, traces, anonymize);

    }

}
