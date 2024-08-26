package fr.gouv.monprojetsup.data.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public final class DataServerConfig {

    @Value("${dataRootDirectory}")
    private String _dataRootDirectory;

    @Getter
    private static String dataRootDirectory;

    @PostConstruct
    private void init() {
        this.dataRootDirectory = _dataRootDirectory;
    }

}
