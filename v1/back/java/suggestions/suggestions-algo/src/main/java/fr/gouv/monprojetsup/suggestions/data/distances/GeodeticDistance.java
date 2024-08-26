package fr.gouv.monprojetsup.suggestions.data.distances;

public class GeodeticDistance
{
    //earth radius in meters
    private static final double R = 6371009;

    //https://en.wikipedia.org/wiki/Geographical_distance
    //Polar coordinate flat-Earth formula
    public static double geodeticDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta1 = Math.toRadians(90 - lat1);
        double theta2 = Math.toRadians(90 - lat2);
        double deltaLambda = Math.toRadians(lon1 - lon2);
        return R * Math.sqrt(
                Math.pow(theta1, 2.0)
                        + Math.pow(theta2, 2.0)
                        -2 * theta1 * theta2 * Math.cos(deltaLambda)
        );
    }


}
