package fr.gouv.monprojetsup.data.domain.port;

import fr.gouv.monprojetsup.data.domain.entity.Formation;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public abstract class FormationsPort {

    abstract public Map<String, Formation> retrieveFormations();

    abstract public Optional<Formation> retrieveFormation(@NotNull String id);


}
