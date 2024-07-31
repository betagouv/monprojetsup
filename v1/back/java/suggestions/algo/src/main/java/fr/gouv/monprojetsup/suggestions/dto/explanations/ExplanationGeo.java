package fr.gouv.monprojetsup.suggestions.dto.explanations;

import fr.gouv.monprojetsup.suggestions.data.distances.Distances;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.cities.Coords;
import fr.gouv.monprojetsup.suggestions.infrastructure.model.cities.Distance;
import fr.gouv.monprojetsup.suggestions.tools.ConcurrentBoundedMapQueue;
import fr.parcoursup.carte.algos.tools.Paire;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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
            List<Pair<String, Coords>> coords,
            ConcurrentBoundedMapQueue<Paire<String, String>,
                    List<ExplanationGeo>> distanceCaches) {
        Paire<String, String> p = new Paire<>(flKey, cityName);

        val l = distanceCaches.get(p);
        if( l != null) return l;

        List<Coords> cities = Distances.getCity(cityName);
        if (cities == null || cities.isEmpty())
            return Collections.emptyList();

        if (coords.isEmpty())
            return Collections.emptyList();
        List<Pair<Integer, String>> results = Distance.getDistanceKm(cities, coords);
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

    public static @NotNull List<ExplanationGeo> getGeoExplanations(
            Collection<String> flKeys,
            String cityName,
            int maxResultsPerNode,
            List<Pair<String,Coords>> coords,
            ConcurrentBoundedMapQueue<Paire<String, String>, List<ExplanationGeo>> distanceCaches
    ) {
        return
                flKeys.stream().flatMap(n -> getGeoExplanations(n, cityName, coords, distanceCaches)
                                .stream().limit(maxResultsPerNode))
                        .filter(Objects::nonNull)
                        .sorted(Comparator.comparing(ExplanationGeo::distance))
                        .toList();
    }

}
