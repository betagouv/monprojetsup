package fr.gouv.monprojetsup.data.domain.port;

import fr.gouv.monprojetsup.data.infrastructure.psup.DescriptifVoeu;

import java.util.Map;

public interface VoeuxPort {

    Map<String, DescriptifVoeu> retrieveDescriptifs();

}
