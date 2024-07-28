package fr.gouv.monprojetsup.data.config;

import fr.gouv.monprojetsup.data.tools.Serialisation;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public final class DataServerConfig {

    @Getter
    private String dataRootDirectory;

    public static final String DATA_SERVER_CONFIG_FILENAME = "dataConfig.json";

    public static DataServerConfig config;

    public static void setDataRootDirectory(String dataRootDirectory) {
        config = new DataServerConfig();
        config.dataRootDirectory = dataRootDirectory;
    }

    public static DataServerConfig load() throws IOException {

        DataServerConfig result;
        boolean fileExists = Files.exists(Path.of(DATA_SERVER_CONFIG_FILENAME));
        try {
            result = Serialisation.fromJsonFile(DATA_SERVER_CONFIG_FILENAME, DataServerConfig.class);
            log.info("Successfully loaded config from " + DATA_SERVER_CONFIG_FILENAME);
            log.info("data path is " + result.getDataRootDirectory());
        } catch (IOException e) {
            if (fileExists) {
                log.error("Failed to load config from " + DATA_SERVER_CONFIG_FILENAME);
                throw e;
            }
            log.info("No config file found, creating default config...");
            result = new DataServerConfig();
            Serialisation.toJsonFile(DATA_SERVER_CONFIG_FILENAME, result, true);
        }

        config = result;
        log.info("Using data directory '" + result.getDataRootDirectory() + "'");
        return result;
    }
}
