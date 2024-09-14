package fr.gouv.monprojetsup.suggestions.port;

import fr.gouv.monprojetsup.data.formation.entity.FormationEntity;
import fr.gouv.monprojetsup.data.model.Formation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FormationsPort {

    @NotNull Map<@NotNull String, @NotNull Formation> retrieveFormations();

    @NotNull Optional<@NotNull Formation> retrieveFormation(@NotNull String id);

    @NotNull List<@NotNull FormationEntity> findAll();

}
