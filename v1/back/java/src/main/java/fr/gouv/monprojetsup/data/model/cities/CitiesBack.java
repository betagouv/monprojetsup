package fr.gouv.monprojetsup.data.model.cities;

import java.util.ArrayList;
import java.util.List;

public record CitiesBack(List<CityBack> cities) {

    public CitiesBack() {
        this(new ArrayList<>());
    }
}
