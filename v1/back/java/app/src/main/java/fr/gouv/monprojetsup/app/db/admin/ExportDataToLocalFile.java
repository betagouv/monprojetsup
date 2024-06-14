package fr.gouv.monprojetsup.app.db.admin;


import fr.gouv.monprojetsup.app.server.WebServerConfig;
import fr.gouv.monprojetsup.app.db.DBExceptions;
import fr.gouv.monprojetsup.app.db.dbimpl.DBMongo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

import static fr.gouv.monprojetsup.app.db.DB.LYCEE_EXPERTS;

@SpringBootApplication
@EnableMongoRepositories
@ComponentScan(basePackages = {"fr.gouv.monprojetsup"})
public class ExportDataToLocalFile {
    public static void main(String[] args) throws IOException, DBExceptions.ModelException {
        // Replace the placeholder with your Atlas connection string

        WebServerConfig config = WebServerConfig.load();

        ConfigurableApplicationContext context =
                new SpringApplicationBuilder(ExportDataToLocalFile.class)
                .web(WebApplicationType.NONE)
                .run(args);

        DBMongo db = context.getBean(DBMongo.class);

        db.load(config);

        db.exportGroupsNonENSToFile("groupsNonENS.json");

        db.exportTracesToFile("traces.json");
        copyFile(Path.of("traces.json"), Path.of("traces_" + LocalDateTime.now() + ".json"));
        copyFile(Path.of("traces.json"), Path.of("data/traces_" + LocalDateTime.now() + ".json"));

        db.exportErrorsToFile("errors.json", false);
        copyFile(Path.of("errors.json"), Path.of("errors_" + LocalDateTime.now() + ".json"));

        db.setFlagEvalENS();

        db.exportUsersToFile("users.json", false, false);
        copyFile(Path.of("users.json"), Path.of("users_" + LocalDateTime.now() + ".json"));

        db.exportUsersToFile("usersExpeENS.json", true, false);
        db.exportUsersToFile("usersExpeENSAnonymized.json", true, true);
        db.exportExpertProfiles("profilsExperts.json", LYCEE_EXPERTS, false);

        copyFile(Path.of("usersExpeENS.json"), Path.of("data/usersExpeENS_" + LocalDateTime.now() + ".json"));

        db.exportGroupsToFile("groups.json");
        copyFile(Path.of("groups.json"), Path.of("groups_" + LocalDateTime.now() + ".json"));
        copyFile(Path.of("groups.json"), Path.of("data/groups_" + LocalDateTime.now() + ".json"));

        db.stop();

        SpringApplication.exit(context);
    }

    public static void copyFile(Path src, Path dest) throws IOException {
        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
    }
}
