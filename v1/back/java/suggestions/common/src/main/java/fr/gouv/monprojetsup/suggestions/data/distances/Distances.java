package fr.gouv.monprojetsup.suggestions.data.distances;

import fr.gouv.monprojetsup.suggestions.data.SuggestionsData;
import fr.gouv.monprojetsup.suggestions.data.model.cities.CitiesBack;
import fr.gouv.monprojetsup.suggestions.data.model.cities.Coords;
import fr.gouv.monprojetsup.suggestions.data.model.formations.Formation;
import fr.gouv.monprojetsup.suggestions.data.tools.GeodeticDistance;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.*;

import static java.lang.Math.min;

@Slf4j
public class Distances {
    private static final Map<String, List<Coords>> cityClientKeyToCities = new HashMap<>();

    public static void init(CitiesBack cities) {
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
