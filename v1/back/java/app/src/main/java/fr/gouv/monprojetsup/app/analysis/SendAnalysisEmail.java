package fr.gouv.monprojetsup.app.analysis;

import fr.gouv.monprojetsup.app.db.DBExceptions;
import fr.gouv.monprojetsup.app.db.admin.ExportDataToLocalFile;
import fr.gouv.monprojetsup.app.db.dbimpl.DBMongo;
import fr.gouv.monprojetsup.app.db.model.Group;
import fr.gouv.monprojetsup.app.db.model.User;
import fr.gouv.monprojetsup.app.log.ServerTrace;
import fr.gouv.monprojetsup.app.mail.MailSender;
import fr.gouv.monprojetsup.app.server.WebServerConfig;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import static fr.gouv.monprojetsup.app.analysis.UsersBehaviourReport.analyze;

@SpringBootApplication
@EnableMongoRepositories
@ComponentScan(basePackages = {"fr.gouv.monprojetsup"})
public class SendAnalysisEmail {
    private static final Logger LOGGER = Logger.getLogger(SendAnalysisEmail.class.getSimpleName());

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
        List<Group> groups = db.getAllGroups();
        List<User> users = db.getUsers();


        sendReport(
                "stats quotidiennes expe ENS groupe test",
                config,
                groups.stream().filter(e -> e.getExpeENSGroupe().equals("T")).toList(),
                users,
                traces,
                true,
                anonymize,
                config.getDailyEmailENSRecipients()
        );


        sendReport(
                "stats 24 heures",
                config,
                groups,
                users,
                traces,
                true,
                anonymize,
                config.getDailyEmailRecipients()
        );

        LocalDateTime start = LocalDateTime.now()
                .minusDays(30)
                .withHour(0).withMinute(0).withSecond(0).withNano(0);

        sendReport(
                "stats 30 jours",
                config,
                groups,
                users,
                traces.stream().filter(e -> LocalDateTime.parse(e.timestamp()).isAfter(start)).toList(),
                false,
                anonymize,
                config.getDailyEmailRecipients()
        );
    }

    static void sendReport(
            String subject,
            WebServerConfig config,
            List<Group> groups,
            List<User> users,
            List<ServerTrace> traces,
            boolean onlyToday,
            boolean anonymize,
            Collection<String> recipients
    ) throws IOException, InterruptedException {

        UsersBehaviourReport report = analyze(groups, users, traces, onlyToday, anonymize);

        String text = subject + "\n\n\n" + report.getMacrosStats();

        LOGGER.info(text);

        text = text + "Détails des traces de navigation en PJ\n\n\n";


        String filename = "rapport_detaille" +
                LocalDateTime.now().withSecond(0).withNano(0)
                        .toString().replace(":","") ;

        CreateAnalysisReports.create(report, filename);
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
}
