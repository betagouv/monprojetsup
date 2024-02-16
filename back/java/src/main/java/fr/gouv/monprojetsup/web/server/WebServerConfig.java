package fr.gouv.monprojetsup.web.server;

import fr.gouv.monprojetsup.tools.Serialisation;
import fr.gouv.monprojetsup.web.db.DB;
import fr.gouv.monprojetsup.web.mail.EmailConfig;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static fr.gouv.monprojetsup.web.server.WebServer.LOGGER;
import static fr.gouv.monprojetsup.web.server.WebServer.SERVER_CONFIG_FILENAME;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class WebServerConfig {

    public static final int DEFAULT_HTTP_PORT = 8000;
    private static final String DEFAULT_SUPPORT_ADDRESS = "support@monprojetsup.fr";
    private static final String DEFAULT_ERRORS_ADDRESS = "errors@monprojetsup.fr";
    public static final long INVALID_PASSWORD_DELAY_MS = 1000;
    public static final int FAILED_LOGIN_MAX_NB_LOCK = 5;
    public static final int PASS_FL_COD = 2047;
    public static final String URL_ARTICLE_PAS_LAS = "https://www.onisep.fr/formation/les-principaux-domaines-de-formation/les-etudes-de-sante/les-voies-d-acces-aux-etudes-de-maieutique-medecine-odontologie-pharmacie";
    public static final String PAS_LAS_TEXT = "<p>Pour accéder aux études de santé (maïeutique, médecine, odontologie et pharmacie), les lycéens doivent suivre le PASS (parcours d'accès spécifique santé) ou une L.AS (licence avec option accès santé) à l'université et réussir aux sélections organisées à l'issue.</p>";

    // Google's public keys endpoint
    public static final String GOOGLE_API_CERTS_URI = "https://www.googleapis.com/oauth2/v3/certs";
    public static final String GOOGLE_ISS = "https://accounts.google.com";

    @Getter
    private EmailConfig emailConfig = new EmailConfig();
    @Getter
    private int authenticationTokenLifeSpanSinceCreationMinutes = 180;
    @Getter
    private int authenticationTokenLifeSpanWithoutActionMinutes = 15;
    @Getter
    private int authenticationTokenByteLength = 64;
    @Getter
    private boolean requireAdminConfirmationOnAccountCreation = true;
    @Getter
    private boolean confirmEmailOnAccountCreation = true;
    @Getter
    private Set<String> admins = new HashSet<>(List.of(DB.DEFAULT_SUPERUSER));
    @Getter
    private boolean noAuthentificationMode = false;
    @Getter
    private int httpPort = DEFAULT_HTTP_PORT;
    @Getter
    private boolean verbose = false;
    @Getter
    public String listeClassesCsvPath = null;
    @Getter
    public String url = "https://monprojetsup.fr";
    @Getter
    private String mongodbPassword = null;
    @Getter
    private String mongodbUser = null;
    @Getter
    private String mongodbHost = null;
    @Getter
    private String mongodbDatabase = null;
    @Getter
    private String mongodbProtocol = null;
    @Getter
    private boolean useMongoDB = false;
    @Getter
    private String supportEmailAddress = DEFAULT_SUPPORT_ADDRESS;
    @Getter
    private String errorsEmailAddress = DEFAULT_ERRORS_ADDRESS;

    @Getter
    private boolean doNotProvideSuggestions = false;
    @Getter
    public String googleOauth2ClientId;
    @Getter
    public String googleOauth2ClientSecret;

    @Getter
    private String dataRootDirectory = "../../../";

    public static WebServerConfig load() throws IOException {
        WebServerConfig result;
        boolean fileExists = Files.exists(Path.of(SERVER_CONFIG_FILENAME));
        try {
            result = Serialisation.fromJsonFile(SERVER_CONFIG_FILENAME, WebServerConfig.class);
            LOGGER.info("Successfully loaded config from " + SERVER_CONFIG_FILENAME);
            LOGGER.info("data path is " + result.getDataRootDirectory());
        } catch (IOException e) {
            if (fileExists) {
                LOGGER.severe("Failed to load config from " + SERVER_CONFIG_FILENAME);
                throw e;
            }
            LOGGER.info("No config file found, creating default config...");
            result = new WebServerConfig();
            Serialisation.toJsonFile(SERVER_CONFIG_FILENAME, result, true);
        }


        return result;
    }

    public void save() throws IOException {
        Serialisation.toJsonFile(SERVER_CONFIG_FILENAME, this, true);
    }

    public void setModeration(boolean moderateAcccountCreation, boolean checkEmails) {
        this.requireAdminConfirmationOnAccountCreation = moderateAcccountCreation;
        this.confirmEmailOnAccountCreation = checkEmails;
    }

}
