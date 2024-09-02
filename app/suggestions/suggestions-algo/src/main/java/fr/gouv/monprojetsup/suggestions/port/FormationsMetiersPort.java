package fr.gouv.monprojetsup.suggestions.port;

import fr.gouv.monprojetsup.data.domain.model.Formation;
import fr.gouv.monprojetsup.data.formation.entity.FormationEntity;
import fr.gouv.monprojetsup.data.formationmetier.entity.FormationMetierEntity;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public interface FormationsMetiersPort {

    @NotNull List<@NotNull FormationMetierEntity> findAll();

    Collection<String> getMetiersOfFormation(@NotNull String formationID);
}
