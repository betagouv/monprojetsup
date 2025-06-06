package fr.gouv.monprojetsup.suggestions.port;

import fr.gouv.monprojetsup.data.model.LatLng;
import fr.gouv.monprojetsup.data.model.psup.DescriptifVoeu;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface VoeuxPort {

    //will be used to export data for ML
    @SuppressWarnings("unused")
    Map<String, DescriptifVoeu> retrieveDescriptifs();

    @NotNull List<@NotNull Pair<@NotNull String, @NotNull LatLng>> retrieveCoords(List<String> voeuxIds);
}
