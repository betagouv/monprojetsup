package fr.gouv.monprojetsup.app.db.admin;


import fr.gouv.monprojetsup.app.server.WebServerConfig;
import fr.gouv.monprojetsup.app.db.DBExceptions;
import fr.gouv.monprojetsup.app.db.dbimpl.DBMongo;
import fr.gouv.monprojetsup.data.DataSources;
import fr.gouv.monprojetsup.data.config.DataServerConfig;
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
import static fr.gouv.monprojetsup.data.DataSources.PROFILS_EXPERTS_MPS_PATH;

@SpringBootApplication
@EnableMongoRepositories
@ComponentScan(basePackages = {"fr.gouv.monprojetsup"})
public class ExportDataToLocalFile {
    public static void main(String[] args) throws IOException, DBExceptions.ModelException {
        // Replace the placeholder with your Atlas connection string

        WebServerConfig config = WebServerConfig.load();
        DataServerConfig.load();

        ConfigurableApplicationContext context =
                new SpringApplicationBuilder(ExportDataToLocalFile.class)
                .web(WebApplicationType.NONE)
                .run(args);

        DBMongo db = context.getBean(DBMongo.class);

        db.load(config);


        db.exportTracesToFile("traces.json");
        String suffix = LocalDateTime.now() + ".json";
        copyFile(Path.of("traces.json"), Path.of("traces_" + suffix ));
        copyFile(Path.of("traces.json"), Path.of("data/traces_" + suffix));

        db.exportErrorsToFile("errors.json", false);
        copyFile(Path.of("errors.json"), Path.of("errors_" + suffix ));

        db.exportUsersToFile("users.json", false);
        copyFile(Path.of("users.json"), Path.of("users_" + suffix));

        db.exportExpertProfiles("profilsExperts.json", LYCEE_EXPERTS, false);
        db.exportExpertProfiles(DataSources.getSourceDataFilePath(PROFILS_EXPERTS_MPS_PATH), LYCEE_EXPERTS, false);

        db.exportGroupsToFile();
        db.exportGroupsOfTestsGroup("quanti3");
        copyFile(Path.of("groups.json"), Path.of("groups_" + suffix ));
        copyFile(Path.of("groups.json"), Path.of("data/groups_" + suffix ));

        db.stop();

        SpringApplication.exit(context);
    }

    public static void copyFile(Path src, Path dest) throws IOException {
        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
    }
}
