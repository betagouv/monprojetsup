package fr.gouv.monprojetsup.data.domain.port;

import fr.gouv.monprojetsup.data.domain.model.LatLng;
import fr.gouv.monprojetsup.data.domain.model.Ville;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface VillesPort {

    @NotNull List<@NotNull LatLng> getCoords(@NotNull String cityName);

    void saveAll(@NotNull List<Ville> villes);

}
