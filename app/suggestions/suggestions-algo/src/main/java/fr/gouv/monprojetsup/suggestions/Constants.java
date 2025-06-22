package fr.gouv.monprojetsup.suggestions;

public class Constants {
    public static final String BR = ".<br/>";

    public static boolean isMpsFormation(String id) {
        return id.startsWith(FILIERE_PREFIX) || id.startsWith(TYPE_FORMATION_PREFIX);
    }

    private static final String TYPE_FORMATION_PREFIX = "fr";//like g_fr_cod
    private static final String FILIERE_PREFIX = "fl";//like g_fl_cod


    /**
     * creates a clean string suitable for indexing in js
     *
     * @param dirty the dirty string
     * @return the clean string
     */
    public static String cleanup(String dirty) {
        return dirty;
        //return dirty.replaceAll("[^a-zA-Z0-9]", "_");
    }

    private Constants() {
    }
}
