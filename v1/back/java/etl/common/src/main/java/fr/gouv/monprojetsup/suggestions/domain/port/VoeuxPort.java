package fr.gouv.monprojetsup.suggestions.domain.port;

import fr.gouv.monprojetsup.suggestions.domain.model.Voeu;
import fr.gouv.monprojetsup.suggestions.infrastructure.psup.DescriptifVoeu;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface VoeuxPort {

    Map<String, DescriptifVoeu> retrieveDescriptifs();

    void saveAll(@NotNull List<Voeu> voeux);

}
