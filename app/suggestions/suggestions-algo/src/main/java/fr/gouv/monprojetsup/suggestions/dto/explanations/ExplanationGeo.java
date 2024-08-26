package fr.gouv.monprojetsup.suggestions.dto.explanations;

import fr.gouv.monprojetsup.data.domain.model.LatLng;
import fr.gouv.monprojetsup.suggestions.tools.ConcurrentBoundedMapQueue;
import fr.parcoursup.carte.algos.tools.Paire;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static fr.gouv.monprojetsup.suggestions.data.distances.GeodeticDistance.geodeticDistance;

public record ExplanationGeo(int distance, String city, @Nullable String form) {

    /**
     * computes minimal distance between a city and a filiere
     *
     * @param flKey             the node, either "fl123" or "fr1223"
     * @param cityName          the city name
     * @return the minimal distance of the city to a etablissement providing this filiere
     */
    public static @NotNull List<ExplanationGeo> getGeoExplanations(
            String flKey,
            String cityName,
            List<LatLng> cityCoords,
            List<Pair<String, LatLng>> coords,
            ConcurrentBoundedMapQueue<Paire<String, String>,
                    List<ExplanationGeo>> distanceCaches) {
        Paire<String, String> p = new Paire<>(flKey, cityName);

        val l = distanceCaches.get(p);
        if( l != null) return l;

        if (cityCoords == null || cityCoords.isEmpty())
            return Collections.emptyList();

        if (coords.isEmpty())
            return Collections.emptyList();
        List<Pair<Integer, String>> results = getDistanceKm(cityCoords, coords);
        if(results == null)
            return Collections.emptyList();
        List<ExplanationGeo> e =
                results.stream().map(result -> new ExplanationGeo(
                                        result.getLeft(),
                                        cityName,
                                        result.getRight()
                                )
                        )
                        .sorted(Comparator.comparing(ExplanationGeo::distance))
                        .toList();
        distanceCaches.put(p, e);
        return e;
    }

    public static @Nullable List<Pair<Integer,String>> getDistanceKm(List<@Nullable LatLng> cities, @NotNull List<Pair<String, @Nullable LatLng>> fors) {
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
            List<Pair<String,LatLng>> coords,
            ConcurrentBoundedMapQueue<Paire<String, String>, List<ExplanationGeo>> distanceCaches
    ) {
        return
                flKeys.stream().flatMap(n -> getGeoExplanations(n, cityName, cityCoords, coords, distanceCaches)
                                .stream().limit(maxResultsPerNode))
                        .filter(Objects::nonNull)
                        .sorted(Comparator.comparing(ExplanationGeo::distance))
                        .toList();
    }

}
