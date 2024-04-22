package fr.gouv.monprojetsup.data.distances;

import fr.gouv.monprojetsup.data.dto.ExplanationGeo;
import fr.gouv.monprojetsup.common.tools.ConcurrentBoundedMapQueue;
import fr.gouv.monprojetsup.data.Constants;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.data.model.cities.CitiesBack;
import fr.gouv.monprojetsup.data.model.cities.Coords;
import fr.gouv.monprojetsup.data.model.cities.Distance;
import fr.gouv.monprojetsup.data.model.formations.Formation;
import fr.gouv.parcoursup.carte.modele.modele.Etablissement;
import fr.parcoursup.carte.algos.tools.Paire;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static fr.gouv.monprojetsup.data.Constants.FILIERE_PREFIX;
import static fr.gouv.monprojetsup.data.Constants.FORMATION_PREFIX;
import static fr.gouv.monprojetsup.data.ServerData.backPsupData;
import static fr.gouv.monprojetsup.data.ServerData.getFormationsFromFil;
import static fr.gouv.monprojetsup.data.tools.GeodeticDistance.distance;
import static java.lang.Math.min;

@Slf4j
public class Distances {
    public static final Map<String, List<Coords>> cityClientKeyToCities = new HashMap<>();
    private static final int DISTANCE_CACHE_SIZE = 10000;
    /**
     * caches return values of getDistanceKm
     */
    public static final ConcurrentBoundedMapQueue<Paire<String, String>, List<ExplanationGeo>>
            distanceCaches = new ConcurrentBoundedMapQueue<>(DISTANCE_CACHE_SIZE);

    /**
     * computes minimal distance between a city and a filiere
     *
     * @param flKey     the node, either "fl123" or "fr1223"
     * @param cityName the city name
     * @return the minimal distance of the city to a etablissement providing this filiere
     */
    public static @NotNull List<ExplanationGeo> getGeoExplanations(String flKey, String cityName) {
        Paire<String, String> p = new Paire<>(flKey, cityName);

        val l = distanceCaches.get(p);
        if( l != null) return l;

        List<Coords> cities = cityClientKeyToCities.get(cityName);
        if (cities == null || cities.isEmpty())
            return Collections.emptyList();

        List<Formation> fors = Collections.emptyList();
        ///attention aux groupes
        if (flKey.startsWith(FILIERE_PREFIX)) {
            fors = getFormationsFromFil(flKey);
        } else if (flKey.startsWith((Constants.FORMATION_PREFIX))) {
            int gTaCod = Integer.parseInt(flKey.substring(2));
            Formation f = backPsupData.formations().formations.get(gTaCod);
            if (f != null) {
                fors = List.of(f);
            }
        }

        List<Pair<String, Coords>> coords = fors.stream()
                .filter(f -> f.lng != null && f.lat != null)
                .map(f -> Pair.of(FORMATION_PREFIX + f.gTaCod, new Coords("", "", f.lat, f.lng))).toList();

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

    public static @NotNull List<ExplanationGeo> getGeoExplanations(Collection<String> flKeys, String cityName, int maxResultsPerNode) {
        return
                flKeys.stream().flatMap(n -> getGeoExplanations(n, cityName).stream().limit(maxResultsPerNode))
                        .filter(Objects::nonNull)
                        .sorted(Comparator.comparing(ExplanationGeo::distance))
                        .toList();
    }

    public static List<String> getCities(String flKey, Set<String> favorites) {

        List<Coords> cities = favorites.stream().flatMap(
                cityName -> cityClientKeyToCities.getOrDefault(cityName, List.of()).stream()).distinct().toList();
        if (cities.isEmpty())
            return Collections.emptyList();

        val formations = getFormationsFromFil(flKey);

        Map<String, Double> citiesDistances = new HashMap<>();

        formations
                .stream()
                .filter(f -> f.lat != null)
                .forEach(f -> {
                    double distance = cities.stream().mapToDouble(c -> distance(c.gps_lat(), c.gps_lng(), f.lat, f.lng))
                            .min().orElse(Double.MAX_VALUE);
                    if (f.commune != null && !f.commune.isEmpty()) {
                        citiesDistances.put(f.commune, min(citiesDistances.getOrDefault(f.commune, Double.MAX_VALUE), distance));
                    }
                });

        return citiesDistances.keySet().stream().sorted(Comparator.comparing(citiesDistances::get)).toList();
    }

    public static void init() {
        log.info("Double indexation des villes");
        CitiesBack cities = ServerData.cities;
        //double indexation par nom et par zip code
        cities.cities().forEach(c -> cityClientKeyToCities.put(c.name(), c.coords()));
        cities.cities().forEach(c -> {
                    if (c.coords() != null) {
                        c.coords().forEach(cc ->
                                cityClientKeyToCities.put(cc.zip_code(), c.coords())
                        );
                        c.coords().forEach(cc ->
                                cityClientKeyToCities.put("i" + cc.insee_code(), c.coords())
                        );
                    }
                }
        );

    }
}
