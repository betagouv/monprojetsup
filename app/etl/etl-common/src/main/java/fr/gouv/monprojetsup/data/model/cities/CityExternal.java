package fr.gouv.monprojetsup.data.model.cities;

public record CityExternal(
        String zip_code,
        String insee_code,
        String name,
        Double gps_lat,
        Double gps_lng) {

}
