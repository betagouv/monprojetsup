package fr.gouv.monprojetsup.data.distances;

import fr.gouv.monprojetsup.data.model.cities.CitiesBack;
import fr.gouv.monprojetsup.data.model.cities.Coords;
import fr.gouv.monprojetsup.data.tools.GeodeticDistance;
import fr.gouv.monprojetsup.data.ServerData;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.*;

import static fr.gouv.monprojetsup.data.ServerData.getFormationsFromFil;
import static java.lang.Math.min;

@Slf4j
public class Distances {
    public static final Map<String, List<Coords>> cityClientKeyToCities = new HashMap<>();

    public static List<String> getCities(String flKey, Set<String> favorites) {

        List<Coords> cities = favorites.stream().flatMap(
                cityName -> cityClientKeyToCities.getOrDefault(cityName, List.of()).stream()).distinct().toList();

        val formations = getFormationsFromFil(flKey);

        if (cities.isEmpty()) {
            val l = new ArrayList<>(formations);
            Collections.shuffle(l);
            return l
                    .stream()
                    .map(f -> f.commune)
                    .filter(Objects::nonNull)
                    .toList();

        }

        Map<String, Double> citiesDistances = new HashMap<>();

        formations
                .stream()
                .filter(f -> f.lat != null)
                .forEach(f -> {
                    double distance = cities.stream().mapToDouble(c -> GeodeticDistance.distance(c.gps_lat(), c.gps_lng(), f.lat, f.lng))
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

    public static List<Coords> getCity(String cityName) {
        return cityClientKeyToCities.get(cityName);
    }

}
