package fr.gouv.monprojetsup.suggestions.domain.port;

import fr.gouv.monprojetsup.suggestions.data.model.descriptifs.Descriptifs;

import java.util.Map;

public abstract class VoeuxPort {

    abstract public Map<String, Descriptifs.Descriptif> retrieveDescriptifs();

}
