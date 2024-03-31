package fr.gouv.monprojetsup.data.model.cities;

import java.util.List;
import java.util.stream.Collectors;

public record CityBack(String name, List<Coords> coords) {
    public String nameWithZip() {
        return name + " (" + coords.stream().map(c -> c.zip_code()).collect(Collectors.joining(",")) + ")";
    }

        /*"login": 1,
        "department_code": "01",
        "insee_code": "01001",
        "zip_code": "01400",
        "name": "L'Abergement-Clémenciat",
        "slug": "l abergement clemenciat",
        "gps_lat": 46.15678199203189,
        "gps_lng": 4.92469920318725*/
}
