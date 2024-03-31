package fr.gouv.monprojetsup.tools.expeEns;

import fr.gouv.monprojetsup.web.db.DBExceptions;
import fr.gouv.monprojetsup.web.db.dbimpl.DBMongo;
import fr.gouv.monprojetsup.web.server.WebServerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.io.IOException;
import java.util.logging.Logger;

@SpringBootApplication
@EnableMongoRepositories
@ComponentScan(basePackages = {"fr.gouv.monprojetsup"})
public class ReinitTreatmentGroupAccessKeys {

    public static final Logger LOGGER = Logger.getLogger(ReinitTreatmentGroupAccessKeys.class.getName());

    public static void main(String[] args) throws IOException, DBExceptions.ModelException {

        LOGGER.info("Loading config...");
        WebServerConfig config = WebServerConfig.load();

        ConfigurableApplicationContext context =
                new SpringApplicationBuilder(ReinitTreatmentGroupAccessKeys.class)
                        .web(WebApplicationType.NONE)
                        .run(args);
        DBMongo db = context.getBean(DBMongo.class);

        db.load(config);

        LOGGER.info("Getting groups...");
        db.reinitTreatmentGroupRegistrationCodes();

        SpringApplication.exit(context);
    }
}
