package fr.gouv.monprojetsup.data.model.cities;


import fr.gouv.monprojetsup.data.DataSources;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.data.DataSources.CITIES_FILE_PATH;

public class CitiesLoader {


    public static CitiesFront loadCitiesFront(DataSources sources) throws IOException {

        CitiesBack cities = loadCitiesBack(sources);

        Map<String, List<CityBack>> mByName = cities.cities().stream().collect(Collectors.groupingBy(g -> g.name()));

        CitiesFront villes = new CitiesFront();
        mByName.values().forEach(l -> {
            boolean nameExplicitelyShowZipCodeToAvoidAmbiguityBetweenTwoCitiesWithSameName = l.size() >= 2;
            l.forEach(c ->
                    {
                        if (!c.coords().isEmpty()) {
                            //indexation par zip code
                            String label = nameExplicitelyShowZipCodeToAvoidAmbiguityBetweenTwoCitiesWithSameName ? c.nameWithZip() : c.name();
                            villes.cities().add(label);
                        }
                    }
            );
        });

        return villes;
    }


    public static CitiesBack loadCitiesBack(DataSources sources) throws IOException {
        CitiesExternal citiesOld = Serialisation.fromJsonFile(sources.getSourceDataFilePath(CITIES_FILE_PATH), CitiesExternal.class);

        //TODO: group coords by common name + 2 siginificant digits of zip code
        //also check which coords have twins and change their names
        Map<String, Pair<String, List<Coords>>> mByDpt = new HashMap<>();
        citiesOld.cities().forEach(c -> {
            try {
                String key = c.name();
                int zipcode = Integer.parseInt(c.zip_code());
                key += zipcode / 1000;
                mByDpt.computeIfAbsent(key, z -> Pair.of(c.name(), new ArrayList<>())).getRight().add(
                        new Coords(c.zip_code(), c.insee_code(), c.gps_lat(), c.gps_lng())
                );
            } catch (Exception ignored) {

            }
        });
        CitiesBack cities = new CitiesBack();
        mByDpt.entrySet().forEach(e -> {
            CityBack g = new CityBack(e.getValue().getLeft(), e.getValue().getRight());
            cities.cities().add(g);
        });
        return cities;
    }



}
