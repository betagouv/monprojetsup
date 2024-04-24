package fr.gouv.monprojetsup.app.tools;

import fr.gouv.monprojetsup.app.db.DBExceptions;
import fr.gouv.monprojetsup.app.db.admin.ExportDataToLocalFile;
import fr.gouv.monprojetsup.app.db.dbimpl.DBMongo;
import fr.gouv.monprojetsup.app.server.WebServerConfig;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.io.IOException;


@SpringBootApplication
@EnableMongoRepositories
@ComponentScan(basePackages = {"fr.gouv.monprojetsup"})

public class LoadNewGroups {

    public static void main(String[] args) throws DBExceptions.ModelException, IOException {
        System.out.println("Loading new groups...");

        WebServerConfig config = WebServerConfig.load();

        ConfigurableApplicationContext context =
                new SpringApplicationBuilder(ExportDataToLocalFile.class)
                        .web(WebApplicationType.NONE)
                        .run(args);


        DBMongo db = context.getBean(DBMongo.class);

        db.load(config);

        db.injectFromCsvFile("newGroups.csv");

        db.close();
    }

}
