package fr.gouv.monprojetsup.suggestions.port;

import fr.gouv.monprojetsup.suggestions.algo.Config;
import org.jetbrains.annotations.Nullable;

public interface ConfigPort {

    @Nullable Config retrieveActiveConfig();

    void setActiveConfig(Config config);

}
