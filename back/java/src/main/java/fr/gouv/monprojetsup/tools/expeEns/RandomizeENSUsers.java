package fr.gouv.monprojetsup.tools.expeEns;

import fr.gouv.monprojetsup.web.db.DBExceptions;
import fr.gouv.monprojetsup.web.db.dbimpl.DBMongo;
import fr.gouv.monprojetsup.web.db.model.User;
import fr.gouv.monprojetsup.web.server.WebServerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

@SpringBootApplication
@EnableMongoRepositories
@ComponentScan(basePackages = {"fr.gouv.monprojetsup"})
public class RandomizeENSUsers {

    public static final Logger LOGGER = Logger.getLogger(RandomizeENSUsers.class.getName());

    public static void main(String[] args) throws IOException, DBExceptions.ModelException {

        LOGGER.info("Loading config...");
        WebServerConfig config = WebServerConfig.load();

        ConfigurableApplicationContext context = SpringApplication.run(RandomizeENSUsers.class, args);
        DBMongo db = context.getBean(DBMongo.class);

        db.load(config);

        LOGGER.info("Getting ENS users...");
        List<User> users = db.getExpeENSUsers();

        LOGGER.info("Randomizing ENS users...");
        Random random = new Random();
        users.forEach(user -> user.getConfig().setStatsVisibilityRandomisationENS(random.nextBoolean()));

        LOGGER.info("Saving ENS users...");
        db.saveUsers(users);

        db.stop();
    }
}
