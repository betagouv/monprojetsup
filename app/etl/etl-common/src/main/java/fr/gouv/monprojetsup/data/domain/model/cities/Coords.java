package fr.gouv.monprojetsup.data.domain.model.cities;

public record Coords(String zip_code, String insee_code, Double gps_lat, Double gps_lng) {
    /*"login": 1,
    "department_code": "01",
    "insee_code": "01001",
    "zip_code": "01400",
    "name": "L'Abergement-Clémenciat",
    "slug": "l abergement clemenciat",
    "gps_lat": 46.15678199203189,
    "gps_lng": 4.92469920318725*/

    public static Coords of(Double lat, Double lng) {
        return new Coords(null, null, lat, lng);
    }
}
