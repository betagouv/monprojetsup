package fr.gouv.monprojetsup.suggestions.infrastructure.model.cities;

public record CityExternal(String zip_code, String insee_code, String name, Double gps_lat, Double gps_lng) {
    public String toClientKey() {
        return name() + " (" + zip_code() + ")";
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
