package fr.gouv.monprojetsup.suggestions.infrastructure.model.cities;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static fr.gouv.monprojetsup.suggestions.tools.GeodeticDistance.distance;

public class Distance {

    /**
     * @param cities a list of cities
     * @param fors a list of pairs (city name, coords)
     * @return a list of pairs (distance, city name) sorted by distance
     */
    public static @Nullable List<Pair<Integer,String>> getDistanceKm(List<Coords> cities, List<Pair<String,Coords>> fors) {
        return fors.stream()
                .map(f -> cities.stream()
                        .filter(c -> c != null && c.gps_lat() != null && c.gps_lng() != null)
                        .map(c -> Pair.of(
                                (int) (distance(f.getRight().gps_lat(), f.getRight().gps_lng(), c.gps_lat(), c.gps_lng())/1000.0),
                                f.getKey()
                        )
                        )
                        .sorted(Comparator.comparingDouble(Pair::getLeft))
                        .findFirst()
                        .orElse(null)
                )
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingDouble(Pair::getLeft))
                .limit(2)
                .toList();
    }
}
