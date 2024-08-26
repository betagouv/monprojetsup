package fr.gouv.monprojetsup.data.domain.port;

import fr.gouv.monprojetsup.data.app.formation.entity.FormationEntity;
import fr.gouv.monprojetsup.data.domain.model.Formation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FormationsPort {

    @NotNull Map<@NotNull String, @NotNull Formation> retrieveFormations();

    @NotNull Optional<@NotNull Formation> retrieveFormation(@NotNull String id);

    @NotNull List<@NotNull FormationEntity> findAll();

    void deleteAll();

    void saveAll(@NotNull List<FormationEntity> entities);
}
