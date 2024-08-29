package fr.gouv.monprojetsup.suggestions.port;

import fr.gouv.monprojetsup.data.domain.model.psup.DescriptifVoeu;

import java.util.Map;

public interface VoeuxPort {


    //will be used to export data for ML
    @SuppressWarnings("unused")
    Map<String, DescriptifVoeu> retrieveDescriptifs();

}
