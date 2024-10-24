package fr.gouv.monprojetsup.suggestions.port;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public interface LabelsPort {

    @NotNull Map<String, @NotNull String> retrieveLabels();

    @NotNull Map<String, @NotNull String> retrieveDebugLabels();

    @NotNull Optional<String> retrieveLabel(@NotNull String key);

    @NotNull Optional<String> retrieveDebugLabel(@NotNull String key);

}
