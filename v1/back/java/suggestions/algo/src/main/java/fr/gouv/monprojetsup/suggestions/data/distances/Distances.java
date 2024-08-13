package fr.gouv.monprojetsup.suggestions.data.distances;

import fr.gouv.monprojetsup.suggestions.infrastructure.model.cities.Coords;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class Distances {

    @Autowired
    public Distances(VillesPort villesport) {

    }

    public List<Coords> getCity(String cityName) {
        return villesport.get(cityName);
    }

}
