package fr.gouv.monprojetsup.tools.perfs;

import fr.gouv.monprojetsup.tools.Serialisation;
import fr.gouv.monprojetsup.web.db.DBExceptions;
import fr.gouv.monprojetsup.web.db.admin.ExportDataToLocalFile;
import fr.gouv.monprojetsup.web.db.dbimpl.DBMongo;
import fr.gouv.monprojetsup.web.db.model.Group;
import fr.gouv.monprojetsup.web.db.model.User;
import fr.gouv.monprojetsup.web.log.ServerTrace;
import fr.gouv.monprojetsup.web.mail.MailSender;
import fr.gouv.monprojetsup.web.server.WebServerConfig;
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

import static fr.gouv.monprojetsup.web.db.admin.ExportDataToLocalFile.copyFile;

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

        boolean onlyToday = true;
        boolean anonymize = true;

        {
            List<ServerTrace> traces = db.getTraces();

            List<Group> groups = new ArrayList<>(db.getGroups().getGroups());

            List<User> users = new ArrayList<>(db.getUsers());

            boolean onlyENS = true;
            boolean onlyENSTest = true;

            sendReport("stats quotidienne MonProjetSup - expe ENS groupe test", config, groups, users, traces, onlyToday, onlyENS, onlyENSTest, anonymize);

        }
        {
            List<ServerTrace> traces = db.getTraces();

            List<Group> groups = new ArrayList<>(db.getGroups().getGroups());

            List<User> users = new ArrayList<>(db.getUsers());

            sendReport("stats quotidienne MonProjetSup - tous", config, groups, users, traces, onlyToday, false, false, anonymize);
        }
    }

    private static void sendReport(String subject, WebServerConfig config, List<Group> groups, List<User> users, List<ServerTrace> traces, boolean onlyToday, boolean onlyENS, boolean onlyENSTest, boolean anonymize) throws IOException, InterruptedException {

        UsersBehaviourReport report = analyze(groups, users, traces, onlyToday, onlyENS, onlyENSTest, anonymize);


        String text = subject + "\n\n\n" + report.getMacrosStats();

        LOGGER.info(text);

        String filename = "navigation_report_" +
                LocalDateTime.now().withSecond(0).withNano(0)
                        .toString().replace(":","") + ".json";

        Serialisation.toJsonFile(filename, report, true);
        Path sourceFile = Path.of(filename);
        copyFile(sourceFile, Path.of("data/" + filename + ".json"));


        Thread thread = MailSender.send(
                config.getEmailConfig(),
                config.getDailyEmailTo(),
                subject,
                text.replace("\n", "<br/>"),
                List.of(sourceFile),
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
