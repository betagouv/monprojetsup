package fr.gouv.monprojetsup.data.domain.port;

import fr.gouv.monprojetsup.data.domain.model.psup.DescriptifVoeu;

import java.util.Map;

public interface VoeuxPort {

    Map<String, DescriptifVoeu> retrieveDescriptifs();

}
