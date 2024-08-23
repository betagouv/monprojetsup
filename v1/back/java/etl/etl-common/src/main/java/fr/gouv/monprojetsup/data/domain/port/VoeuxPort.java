package fr.gouv.monprojetsup.data.domain.port;

import fr.gouv.monprojetsup.data.domain.model.Voeu;
import fr.gouv.monprojetsup.data.infrastructure.psup.DescriptifVoeu;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface VoeuxPort {

    Map<String, DescriptifVoeu> retrieveDescriptifs();

    void saveAll(@NotNull List<Voeu> voeux);

}
