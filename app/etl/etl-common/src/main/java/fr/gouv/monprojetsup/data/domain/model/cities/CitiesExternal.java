package fr.gouv.monprojetsup.data.domain.model.cities;

import java.util.ArrayList;
import java.util.List;

public record CitiesExternal(List<CityExternal> cities) {

    public CitiesExternal() {
        this(new ArrayList<>());
    }


}
