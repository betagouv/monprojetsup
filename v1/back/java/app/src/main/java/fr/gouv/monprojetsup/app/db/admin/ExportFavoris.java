package fr.gouv.monprojetsup.app.db.admin;


import fr.gouv.monprojetsup.app.db.DBExceptions;
import fr.gouv.monprojetsup.app.db.dbimpl.DBMongo;
import fr.gouv.monprojetsup.app.server.WebServerConfig;
import org.springframework.boot.SpringApplication;
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
public class ExportFavoris {
    public static void main(String[] args) throws IOException, DBExceptions.ModelException {
        // Replace the placeholder with your Atlas connection string

        WebServerConfig config = WebServerConfig.load();

        ConfigurableApplicationContext context =
                new SpringApplicationBuilder(ExportFavoris.class)
                        .web(WebApplicationType.NONE)
                        .run(args);

        DBMongo db = context.getBean(DBMongo.class);

        db.load(config);
        db.exportFavorisToFile("favoris.json");

        SpringApplication.exit(context);

    }

}
