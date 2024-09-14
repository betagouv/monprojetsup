package fr.gouv.monprojetsup.suggestions.dto.explanations;

import fr.gouv.monprojetsup.data.model.LatLng;
import fr.gouv.monprojetsup.data.model.Ville;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static fr.gouv.monprojetsup.suggestions.data.distances.GeodeticDistance.geodeticDistance;

public record ExplanationGeo(
        @Schema(description = "distance en km entre la formation d'accueil et la ville d'intérêt", example = "1")
        int distance,
        @Schema(description = "nom de la ville d'intérêt", example = "Soulac-sur-Mer")
        String city,
        @Schema(description = "code insee de la ville d'intérêt", example = "33514")
        String codeInsee,
        @Schema(description = "code de la formation d'accueil", example = "ta1234")
        @Nullable String formationAccueil
) {

    /**
     * computes minimal distance between a city and a filiere
     *
     * @param ville the city
     * @return the minimal distance of the city to a etablissement providing this filiere
     */
    public static @NotNull List<ExplanationGeo> getGeoExplanations(
            @NotNull Ville ville,
            @NotNull List<Pair<String, LatLng>> coords
    ) {
        @NotNull List<LatLng> cityCoords = ville.coords();
        if (cityCoords.isEmpty())
            return Collections.emptyList();
        if (coords.isEmpty())
            return Collections.emptyList();
        List<Pair<Integer, String>> results = getDistanceKm(cityCoords, coords);
        if(results == null)
            return Collections.emptyList();
        return
                results.stream().map(result -> new ExplanationGeo(
                                        result.getLeft(),
                                        ville.nom(),
                                        ville.codeInsee(),
                                        result.getRight()
                                )
                        )
                        .sorted(Comparator.comparing(ExplanationGeo::distance))
                        .toList();
    }

    public static @Nullable List<Pair<Integer,String>> getDistanceKm(List<LatLng> cities, @NotNull List<Pair<String, LatLng>> fors) {
        return fors.stream()
                .filter(f -> f != null && f.getRight() != null)
                .map(f -> cities.stream()
                        .filter(Objects::nonNull)
                        .map(c -> Pair.of(
                                        (int) (geodeticDistance(f.getRight().lat(), f.getRight().lng(), c.lat(), c.lng()) / 1000.0),
                                        f.getKey()
                                )
                        ).min(Comparator.comparingDouble(Pair::getLeft))
                        .orElse(null)
                )
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingDouble(Pair::getLeft))
                .limit(2)
                .toList();
    }

    public static @NotNull List<ExplanationGeo> getGeoExplanations(
            @NotNull Collection<@NotNull String> flKeys,
            Ville ville,
            int maxResultsPerNode,
            @NotNull List<@NotNull Pair<@NotNull String,@NotNull LatLng>> coords
    ) {
        return
                flKeys.stream().flatMap(n -> getGeoExplanations(ville, coords)
                                .stream().limit(maxResultsPerNode))
                        .filter(Objects::nonNull)
                        .sorted(Comparator.comparing(ExplanationGeo::distance))
                        .toList();
    }

}
