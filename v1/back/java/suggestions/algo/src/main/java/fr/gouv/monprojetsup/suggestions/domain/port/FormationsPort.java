package fr.gouv.monprojetsup.suggestions.domain.port;

import fr.gouv.monprojetsup.suggestions.data.model.descriptifs.Descriptifs;
import fr.gouv.monprojetsup.suggestions.domain.entity.Formation;

import java.util.Map;
import java.util.Optional;

public abstract class FormationsPort {

    abstract public Map<String, Formation> retrieveFormations();

    abstract public Optional<Formation> retrieveFormation(String id);
}
