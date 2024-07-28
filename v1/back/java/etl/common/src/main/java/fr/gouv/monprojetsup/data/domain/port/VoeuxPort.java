package fr.gouv.monprojetsup.data.domain.port;

import fr.gouv.monprojetsup.data.model.descriptifs.Descriptifs;

import java.util.Map;

public abstract class VoeuxPort {

    abstract public Map<String, Descriptifs.Descriptif> retrieveDescriptifs();

}
