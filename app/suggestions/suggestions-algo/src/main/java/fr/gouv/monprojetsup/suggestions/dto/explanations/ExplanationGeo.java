package fr.gouv.monprojetsup.suggestions.dto.explanations;

import fr.gouv.monprojetsup.data.domain.model.LatLng;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static fr.gouv.monprojetsup.suggestions.data.distances.GeodeticDistance.geodeticDistance;

public record ExplanationGeo(int distance, String city, @Nullable String form) {

    /**
     * computes minimal distance between a city and a filiere
     *
     * @param cityName          the city name
     * @return the minimal distance of the city to a etablissement providing this filiere
     */
    public static @NotNull List<ExplanationGeo> getGeoExplanations(
            String cityName,
            List<LatLng> cityCoords,
            List<Pair<String, LatLng>> coords) {
        if (cityCoords == null || cityCoords.isEmpty())
            return Collections.emptyList();
        if (coords.isEmpty())
            return Collections.emptyList();
        List<Pair<Integer, String>> results = getDistanceKm(cityCoords, coords);
        if(results == null)
            return Collections.emptyList();
        return
                results.stream().map(result -> new ExplanationGeo(
                                        result.getLeft(),
                                        cityName,
                                        result.getRight()
                                )
                        )
                        .sorted(Comparator.comparing(ExplanationGeo::distance))
                        .toList();
    }

    public static @Nullable List<Pair<Integer,String>> getDistanceKm(List<LatLng> cities, @NotNull List<Pair<String, LatLng>> fors) {
        //noinspection DataFlowIssue
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
            Collection<String> flKeys,
            String cityName,
            List<LatLng> cityCoords,
            int maxResultsPerNode,
            List<Pair<String,LatLng>> coords
    ) {
        return
                flKeys.stream().flatMap(n -> getGeoExplanations(cityName, cityCoords, coords)
                                .stream().limit(maxResultsPerNode))
                        .filter(Objects::nonNull)
                        .sorted(Comparator.comparing(ExplanationGeo::distance))
                        .toList();
    }

}
