package fr.gouv.monprojetsup.suggestions.domain.port;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public abstract class LabelsPort {

    abstract public @NotNull Map<String, @NotNull String> retrieveLabels();

    abstract public @NotNull Optional<String> retrieveLabel(@NotNull String key);

}
