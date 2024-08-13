package fr.gouv.monprojetsup.suggestions.domain.port;

import fr.gouv.monprojetsup.suggestions.domain.model.Formation;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface FormationsPort {

    @NotNull Map<@NotNull String, @NotNull Formation> retrieveFormations();

    @NotNull Optional<@NotNull Formation> retrieveFormation(@NotNull String id);

    void saveAll(@NotNull Collection<@NotNull Formation> formations);

}
