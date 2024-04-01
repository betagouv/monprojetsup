package fr.gouv.monprojetsup.app.server;

import com.google.gson.Gson;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.suggestions.server.SuggestionServer;
import fr.gouv.monprojetsup.app.tools.server.Server;
import fr.gouv.monprojetsup.app.db.DB;
import fr.gouv.monprojetsup.app.db.dbimpl.DBMongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Logger;

@Component
public class WebServer extends Server {

    public static final Logger LOGGER = Logger.getLogger(WebServer.class.getName());

    public static final String SERVER_CONFIG_FILENAME = "serverConfig.json";

    private static WebServerConfig config;
    private SuggestionServer suggestionServer = null;

    public static WebServerConfig config() { return config; }

    @Autowired
    private DBMongo db;

    public static DB db() {
        return _db;
    }
    private static DBMongo _db;

    public static boolean isInitialized() {
        return _db != null;
    }

    public static void loadConfig() throws IOException {
        config = WebServerConfig.load();
    }

    @Override
    public void init() throws Exception {
        init(false);
    }

    public synchronized void init(boolean onlyDB) throws Exception {


        LOGGER.info("Loading config...");
        config = WebServerConfig.load();

        LOGGER.info("Loaded config..." + new Gson().toJson(config));

        if(!onlyDB) {
            ServerData.load();

            if (!config.isDoNotProvideSuggestions()) {
                LOGGER.info("Starting suggestions server...");
                suggestionServer = new SuggestionServer();
                suggestionServer.init();
            }
        }

        LOGGER.info("WebServer Initialized ");

        db.load(config);
        _db = db;

        LOGGER.info("DB Initialized ");

    }

}
