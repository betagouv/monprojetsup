package fr.gouv.monprojetsup.suggestions.port;

import fr.gouv.monprojetsup.data.domain.model.LatLng;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface VillesPort {
    @NotNull List<@NotNull LatLng> getCoords(@NotNull String cityName);

}
