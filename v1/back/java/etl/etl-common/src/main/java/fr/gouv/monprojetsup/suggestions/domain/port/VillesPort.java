package fr.gouv.monprojetsup.suggestions.domain.port;

import fr.gouv.monprojetsup.suggestions.domain.model.LatLng;
import fr.gouv.monprojetsup.suggestions.domain.model.Ville;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface VillesPort {

    @NotNull List<@NotNull LatLng> getCoords(@NotNull String cityName);

    void saveAll(@NotNull List<Ville> villes);

}
